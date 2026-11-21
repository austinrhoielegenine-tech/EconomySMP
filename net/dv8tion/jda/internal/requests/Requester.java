package net.dv8tion.jda.internal.requests;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.SSLPeerUnverifiedException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.Method;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.RestConfig;
import net.dv8tion.jda.api.requests.RestRateLimiter;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.utils.IOUtil;
import net.dv8tion.jda.internal.utils.JDALogger;
import net.dv8tion.jda.internal.utils.config.AuthorizationConfig;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.MDC;

public class Requester {
   private static final int[] RETRY_ERROR_CODES = new int[]{502, 503, 504, 520, 521, 522, 523, 524, 529};
   public static final Logger LOG = JDALogger.getLog(Requester.class);
   public static final RequestBody EMPTY_BODY = RequestBody.create((MediaType)null, (byte[])(new byte[0]));
   public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
   public static final MediaType MEDIA_TYPE_OCTET = MediaType.parse("application/octet-stream; charset=utf-8");
   public static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
   public static final MediaType MEDIA_TYPE_GIF = MediaType.parse("image/gif");
   protected final JDAImpl api;
   protected final AuthorizationConfig authConfig;
   private final RestRateLimiter rateLimiter;
   private final HttpUrl baseUrl;
   private final String userAgent;
   private final Consumer<? super okhttp3.Request.Builder> customBuilder;
   private final OkHttpClient httpClient;
   private boolean isContextReady = false;
   private ConcurrentMap<String, String> contextMap = null;
   private volatile boolean retryOnTimeout = false;

   public Requester(JDA api, AuthorizationConfig authConfig, RestConfig config, RestRateLimiter rateLimiter) {
      if (authConfig == null) {
         throw new NullPointerException("Provided config was null!");
      } else {
         this.authConfig = authConfig;
         this.api = (JDAImpl)api;
         this.rateLimiter = rateLimiter;
         this.baseUrl = HttpUrl.get(config.getBaseUrl());
         this.userAgent = config.getUserAgent();
         this.customBuilder = config.getCustomBuilder();
         this.httpClient = this.api.getHttpClient();
      }
   }

   public void setContextReady(boolean ready) {
      this.isContextReady = ready;
   }

   public void setContext() {
      if (this.isContextReady) {
         if (this.contextMap == null) {
            this.contextMap = this.api.getContextMap();
         }

         this.contextMap.forEach(MDC::put);
      }
   }

   public JDAImpl getJDA() {
      return this.api;
   }

   public <T> void request(Request<T> apiRequest) {
      if (this.rateLimiter.isStopped()) {
         throw new RejectedExecutionException("The Requester has been stopped! No new requests can be requested!");
      } else {
         if (apiRequest.shouldQueue()) {
            this.rateLimiter.enqueue(new WorkTask(apiRequest));
         } else {
            this.execute(new WorkTask(apiRequest), true);
         }

      }
   }

   private static boolean isRetry(Throwable e) {
      return e instanceof SocketException || e instanceof SocketTimeoutException || e instanceof SSLPeerUnverifiedException;
   }

   public Response execute(WorkTask task) {
      return this.execute(task, false);
   }

   public Response execute(WorkTask task, boolean handleOnRateLimit) {
      return this.execute(task, false, handleOnRateLimit);
   }

   public Response execute(WorkTask task, boolean retried, boolean handleOnRatelimit) {
      Route.CompiledRoute route = task.getRoute();
      okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
      HttpUrl url = route.toHttpUrl(this.baseUrl);
      builder.url(url);
      Request<?> apiRequest = task.request;
      this.applyBody(apiRequest, builder);
      this.applyHeaders(apiRequest, builder);
      if (this.customBuilder != null) {
         try {
            this.customBuilder.accept(builder);
         } catch (Exception e) {
            LOG.error((String)"Custom request builder caused exception", (Throwable)e);
         }
      }

      okhttp3.Request request = builder.build();
      Set<String> rays = new LinkedHashSet();
      Response[] responses = new Response[4];
      Response lastResponse = null;
      boolean var33 = false;

      Object attempt;
      label481: {
         label482: {
            label483: {
               label484: {
                  Object var51;
                  label485: {
                     label486: {
                        try {
                           var33 = true;
                           LOG.trace((String)"Executing request {} {}", (Object)task.getRoute().getMethod(), (Object)url);
                           int code = 0;

                           for(int var46 = 0; var46 < responses.length; ++var46) {
                              if (apiRequest.isSkipped()) {
                                 var51 = null;
                                 var33 = false;
                                 break label485;
                              }

                              Call call = this.httpClient.newCall(request);
                              lastResponse = call.execute();
                              code = lastResponse.code();
                              responses[var46] = lastResponse;
                              String cfRay = lastResponse.header("CF-RAY");
                              if (cfRay != null) {
                                 rays.add(cfRay);
                              }

                              if (!shouldRetry(code)) {
                                 break;
                              }

                              LOG.debug("Requesting {} -> {} returned status {}... retrying (attempt {})", apiRequest.getRoute().getMethod(), url, code, var46 + 1);

                              try {
                                 Thread.sleep((long)(500 << var46));
                              } catch (InterruptedException var38) {
                                 break;
                              }
                           }

                           LOG.trace("Finished Request {} {} with code {}", route.getMethod(), lastResponse.request().url(), code);
                           if (shouldRetry(code)) {
                              task.handleResponse(lastResponse, -1L, rays);
                              attempt = null;
                              var33 = false;
                              break label486;
                           }

                           if (!rays.isEmpty()) {
                              LOG.debug((String)"Received response with following cf-rays: {}", (Object)rays);
                           }

                           if (handleOnRatelimit && code == 429) {
                              long retryAfter = this.parseRetry(lastResponse);
                              task.handleResponse(lastResponse, retryAfter, rays);
                           } else if (code != 429) {
                              task.handleResponse(lastResponse, rays);
                           } else if (getContentType(lastResponse).startsWith("application/json")) {
                              try {
                                 InputStream body = IOUtil.getBody(lastResponse);

                                 try {
                                    long retryAfterBody = (long)Math.ceil(DataObject.fromJson(body).getDouble("retry_after", (double)0.0F));
                                    long retryAfterHeader = Long.parseLong(lastResponse.header("Retry-After"));
                                    lastResponse = lastResponse.newBuilder().header("Retry-After", Long.toString(Math.max(retryAfterHeader, retryAfterBody))).build();
                                 } catch (Throwable var36) {
                                    if (body != null) {
                                       try {
                                          body.close();
                                       } catch (Throwable var34) {
                                          var36.addSuppressed(var34);
                                       }
                                    }

                                    throw var36;
                                 }

                                 if (body != null) {
                                    body.close();
                                 }
                              } catch (Exception e) {
                                 LOG.warn((String)"Failed to parse retry-after response body", (Throwable)e);
                              }
                           }

                           var49 = lastResponse;
                           var33 = false;
                        } catch (UnknownHostException e) {
                           LOG.error((String)"DNS resolution failed: {}", (Object)e.getMessage());
                           task.handleResponse((Exception)e, rays);
                           attempt = null;
                           var33 = false;
                           break label484;
                        } catch (IOException var40) {
                           if (this.retryOnTimeout && !retried && isRetry(var40)) {
                              body = this.execute(task, true, handleOnRatelimit);
                              var33 = false;
                              break label482;
                           }

                           LOG.error((String)"There was an I/O error while executing a REST request: {}", (Object)var40.getMessage());
                           task.handleResponse((Exception)var40, rays);
                           attempt = null;
                           var33 = false;
                           break label483;
                        } catch (Exception e) {
                           LOG.error((String)"There was an unexpected error while executing a REST request", (Throwable)e);
                           task.handleResponse(e, rays);
                           attempt = null;
                           var33 = false;
                           break label481;
                        } finally {
                           if (var33) {
                              for(Response r : responses) {
                                 if (r == null) {
                                    break;
                                 }

                                 r.close();
                              }

                           }
                        }

                        for(Response r : responses) {
                           if (r == null) {
                              break;
                           }

                           r.close();
                        }

                        return var49;
                     }

                     for(Response r : responses) {
                        if (r == null) {
                           break;
                        }

                        r.close();
                     }

                     return (Response)attempt;
                  }

                  for(Response r : responses) {
                     if (r == null) {
                        break;
                     }

                     r.close();
                  }

                  return (Response)var51;
               }

               for(Response r : responses) {
                  if (r == null) {
                     break;
                  }

                  r.close();
               }

               return (Response)attempt;
            }

            for(Response r : responses) {
               if (r == null) {
                  break;
               }

               r.close();
            }

            return (Response)attempt;
         }

         for(Response r : responses) {
            if (r == null) {
               break;
            }

            r.close();
         }

         return body;
      }

      for(Response r : responses) {
         if (r == null) {
            break;
         }

         r.close();
      }

      return (Response)attempt;
   }

   private void applyBody(Request<?> apiRequest, okhttp3.Request.Builder builder) {
      Method method = apiRequest.getRoute().getMethod();
      RequestBody body = apiRequest.getBody();
      if (body == null && method.requiresRequestBody()) {
         body = EMPTY_BODY;
      }

      builder.method(method.toString(), body);
      if (apiRequest.getRawBody() != null) {
         LOG.trace("Sending request on route {}/{} with body\n{}", method, apiRequest.getRoute().getCompiledRoute(), apiRequest.getRawBody());
      }

   }

   private void applyHeaders(Request<?> apiRequest, okhttp3.Request.Builder builder) {
      builder.header("user-agent", this.userAgent).header("accept-encoding", "gzip").header("authorization", this.authConfig.getToken()).header("x-ratelimit-precision", "millisecond");
      if (apiRequest.getHeaders() != null) {
         for(Map.Entry<String, String> header : apiRequest.getHeaders().entrySet()) {
            builder.header((String)header.getKey(), (String)header.getValue());
         }
      }

   }

   public OkHttpClient getHttpClient() {
      return this.httpClient;
   }

   public RestRateLimiter getRateLimiter() {
      return this.rateLimiter;
   }

   public void setRetryOnTimeout(boolean retryOnTimeout) {
      this.retryOnTimeout = retryOnTimeout;
   }

   public void stop(boolean shutdown, Runnable callback) {
      this.rateLimiter.stop(shutdown, callback);
   }

   private static boolean shouldRetry(int code) {
      if (code >= RETRY_ERROR_CODES[0] && code <= RETRY_ERROR_CODES[RETRY_ERROR_CODES.length - 1]) {
         for(int retryCode : RETRY_ERROR_CODES) {
            if (retryCode == code) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private long parseRetry(Response response) {
      String retryAfter = response.header("Retry-After", "0");
      return (long)(Double.parseDouble(retryAfter) * (double)1000.0F);
   }

   private static String getContentType(Response response) {
      String type = response.header("content-type");
      return type == null ? "" : type.toLowerCase(Locale.ROOT);
   }

   private class WorkTask implements RestRateLimiter.Work {
      private final Request<?> request;
      private boolean done;

      private WorkTask(Request<?> request) {
         this.request = request;
      }

      @Nonnull
      public Route.CompiledRoute getRoute() {
         return this.request.getRoute();
      }

      @Nonnull
      public JDA getJDA() {
         return this.request.getJDA();
      }

      @Nullable
      public Response execute() {
         return Requester.this.execute(this);
      }

      public boolean isSkipped() {
         return this.request.isSkipped();
      }

      public boolean isDone() {
         return this.isSkipped() || this.done;
      }

      public boolean isPriority() {
         return this.request.isPriority();
      }

      public boolean isCancelled() {
         return this.request.isCancelled();
      }

      public void cancel() {
         this.request.cancel();
      }

      private void handleResponse(Response response, Set<String> rays) {
         this.done = true;
         this.request.handleResponse(new net.dv8tion.jda.api.requests.Response(response, -1L, rays));
      }

      private void handleResponse(Exception error, Set<String> rays) {
         this.done = true;
         this.request.handleResponse(new net.dv8tion.jda.api.requests.Response(error, rays));
      }

      private void handleResponse(Response response, long retryAfter, Set<String> cfRays) {
         this.done = true;
         this.request.handleResponse(new net.dv8tion.jda.api.requests.Response(response, retryAfter, cfRays));
      }
   }
}
