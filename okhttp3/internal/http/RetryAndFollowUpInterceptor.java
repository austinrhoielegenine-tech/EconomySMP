package okhttp3.internal.http;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.Proxy.Type;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.List;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Regex;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.UnreadableResponseBodyKt;
import okhttp3.internal._UtilCommonKt;
import okhttp3.internal._UtilJvmKt;
import okhttp3.internal.connection.Exchange;
import okhttp3.internal.connection.RealCall;
import okhttp3.internal.connection.RealConnection;
import okhttp3.internal.http2.ConnectionShutdownException;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\u0018\u0000 \u001f2\u00020\u0001:\u0001\u001fB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0016J \u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002J\u0018\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002J\u0018\u0010\u0013\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u0014\u001a\u00020\u000bH\u0002J\u001c\u0010\u0015\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0016\u001a\u00020\u00072\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018H\u0002J\u001a\u0010\u0019\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0016\u001a\u00020\u00072\u0006\u0010\u001a\u001a\u00020\u001bH\u0002J\u0018\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u0016\u001a\u00020\u00072\u0006\u0010\u001e\u001a\u00020\u001dH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006 "},
   d2 = {"Lokhttp3/internal/http/RetryAndFollowUpInterceptor;", "Lokhttp3/Interceptor;", "client", "Lokhttp3/OkHttpClient;", "<init>", "(Lokhttp3/OkHttpClient;)V", "intercept", "Lokhttp3/Response;", "chain", "Lokhttp3/Interceptor$Chain;", "recover", "", "e", "Ljava/io/IOException;", "call", "Lokhttp3/internal/connection/RealCall;", "userRequest", "Lokhttp3/Request;", "requestIsOneShot", "isRecoverable", "requestSendStarted", "followUpRequest", "userResponse", "exchange", "Lokhttp3/internal/connection/Exchange;", "buildRedirectRequest", "method", "", "retryAfter", "", "defaultDelay", "Companion", "okhttp"}
)
public final class RetryAndFollowUpInterceptor implements Interceptor {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final OkHttpClient client;
   private static final int MAX_FOLLOW_UPS = 20;

   public RetryAndFollowUpInterceptor(@NotNull OkHttpClient client) {
      Intrinsics.checkNotNullParameter(client, "client");
      super();
      this.client = client;
   }

   @NotNull
   public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
      Intrinsics.checkNotNullParameter(chain, "chain");
      RealInterceptorChain realChain = (RealInterceptorChain)chain;
      Request request = ((RealInterceptorChain)chain).getRequest$okhttp();
      RealCall call = realChain.getCall$okhttp();
      int followUpCount = 0;
      Response priorResponse = null;
      boolean newRoutePlanner = true;
      List recoveredFailures = CollectionsKt.emptyList();

      while(true) {
         call.enterNetworkInterceptorExchange(request, newRoutePlanner, (RealInterceptorChain)chain);
         Response response = null;
         boolean closeActiveExchange = true;

         Response var13;
         try {
            if (call.isCanceled()) {
               throw new IOException("Canceled");
            }

            try {
               response = realChain.proceed(request);
               newRoutePlanner = true;
            } catch (IOException e) {
               boolean isRecoverable = this.recover(e, call, request);
               call.getEventListener$okhttp().retryDecision(call, e, isRecoverable);
               if (!isRecoverable) {
                  throw _UtilCommonKt.withSuppressed((Exception)e, recoveredFailures);
               }

               recoveredFailures = CollectionsKt.plus((Collection)recoveredFailures, e);
               newRoutePlanner = false;
               continue;
            }

            response = response.newBuilder().request(request).priorResponse(priorResponse != null ? UnreadableResponseBodyKt.stripBody(priorResponse) : null).build();
            Exchange exchange = call.getInterceptorScopedExchange$okhttp();
            Request followUp = this.followUpRequest(response, exchange);
            if (followUp != null) {
               RequestBody followUpBody = followUp.body();
               if (followUpBody == null || !followUpBody.isOneShot()) {
                  _UtilCommonKt.closeQuietly(response.body());
                  ++followUpCount;
                  if (followUpCount > 20) {
                     call.getEventListener$okhttp().followUpDecision(call, response, (Request)null);
                     throw new ProtocolException("Too many follow-up requests: " + followUpCount);
                  }

                  call.getEventListener$okhttp().followUpDecision(call, response, followUp);
                  request = followUp;
                  priorResponse = response;
                  continue;
               }

               closeActiveExchange = false;
               call.getEventListener$okhttp().followUpDecision(call, response, (Request)null);
               Response var14 = response;
               return var14;
            }

            if (exchange != null && exchange.isDuplex$okhttp()) {
               call.timeoutEarlyExit();
            }

            closeActiveExchange = false;
            call.getEventListener$okhttp().followUpDecision(call, response, (Request)null);
            var13 = response;
         } finally {
            call.exitNetworkInterceptorExchange$okhttp(closeActiveExchange);
         }

         return var13;
      }
   }

   private final boolean recover(IOException e, RealCall call, Request userRequest) {
      boolean requestSendStarted = !(e instanceof ConnectionShutdownException);
      if (!this.client.retryOnConnectionFailure()) {
         return false;
      } else if (requestSendStarted && this.requestIsOneShot(e, userRequest)) {
         return false;
      } else if (!this.isRecoverable(e, requestSendStarted)) {
         return false;
      } else {
         return call.retryAfterFailure();
      }
   }

   private final boolean requestIsOneShot(IOException e, Request userRequest) {
      RequestBody requestBody = userRequest.body();
      return requestBody != null && requestBody.isOneShot() || e instanceof FileNotFoundException;
   }

   private final boolean isRecoverable(IOException e, boolean requestSendStarted) {
      if (e instanceof ProtocolException) {
         return false;
      } else if (!(e instanceof InterruptedIOException)) {
         if (e instanceof SSLHandshakeException && e.getCause() instanceof CertificateException) {
            return false;
         } else {
            return !(e instanceof SSLPeerUnverifiedException);
         }
      } else {
         return e instanceof SocketTimeoutException && !requestSendStarted;
      }
   }

   private final Request followUpRequest(Response userResponse, Exchange exchange) throws IOException {
      Route var11;
      label66: {
         if (exchange != null) {
            RealConnection var10000 = exchange.getConnection$okhttp();
            if (var10000 != null) {
               var11 = var10000.route();
               break label66;
            }
         }

         var11 = null;
      }

      Route route = var11;
      int responseCode = userResponse.code();
      String method = userResponse.request().method();
      switch (responseCode) {
         case 300:
         case 301:
         case 302:
         case 303:
         case 307:
         case 308:
            return this.buildRedirectRequest(userResponse, method);
         case 401:
            return this.client.authenticator().authenticate(route, userResponse);
         case 407:
            Intrinsics.checkNotNull(route);
            Proxy selectedProxy = route.proxy();
            if (selectedProxy.type() != Type.HTTP) {
               throw new ProtocolException("Received HTTP_PROXY_AUTH (407) code while not using proxy");
            }

            return this.client.proxyAuthenticator().authenticate(route, userResponse);
         case 408:
            if (!this.client.retryOnConnectionFailure()) {
               return null;
            } else {
               RequestBody requestBody = userResponse.request().body();
               if (requestBody != null && requestBody.isOneShot()) {
                  return null;
               } else {
                  Response priorResponse = userResponse.priorResponse();
                  if (priorResponse != null && priorResponse.code() == 408) {
                     return null;
                  } else {
                     if (this.retryAfter(userResponse, 0) > 0) {
                        return null;
                     }

                     return userResponse.request();
                  }
               }
            }
         case 421:
            RequestBody requestBody = userResponse.request().body();
            if (requestBody != null && requestBody.isOneShot()) {
               return null;
            } else {
               if (exchange != null && exchange.isCoalescedConnection$okhttp()) {
                  exchange.getConnection$okhttp().noCoalescedConnections$okhttp();
                  return userResponse.request();
               }

               return null;
            }
         case 503:
            Response priorResponse = userResponse.priorResponse();
            if (priorResponse != null && priorResponse.code() == 503) {
               return null;
            } else {
               if (this.retryAfter(userResponse, Integer.MAX_VALUE) == 0) {
                  return userResponse.request();
               }

               return null;
            }
         default:
            return null;
      }
   }

   private final Request buildRedirectRequest(Response userResponse, String method) {
      if (!this.client.followRedirects()) {
         return null;
      } else {
         String var10000 = Response.header$default(userResponse, "Location", (String)null, 2, (Object)null);
         if (var10000 == null) {
            return null;
         } else {
            String location = var10000;
            HttpUrl var10 = userResponse.request().url().resolve(location);
            if (var10 == null) {
               return null;
            } else {
               HttpUrl url = var10;
               boolean sameScheme = Intrinsics.areEqual((Object)url.scheme(), (Object)userResponse.request().url().scheme());
               if (!sameScheme && !this.client.followSslRedirects()) {
                  return null;
               } else {
                  Request.Builder requestBuilder = userResponse.request().newBuilder();
                  if (HttpMethod.permitsRequestBody(method)) {
                     int responseCode = userResponse.code();
                     boolean maintainBody = HttpMethod.INSTANCE.redirectsWithBody(method) || responseCode == 308 || responseCode == 307;
                     if (HttpMethod.INSTANCE.redirectsToGet(method) && responseCode != 308 && responseCode != 307) {
                        requestBuilder.method("GET", (RequestBody)null);
                     } else {
                        RequestBody requestBody = maintainBody ? userResponse.request().body() : null;
                        requestBuilder.method(method, requestBody);
                     }

                     if (!maintainBody) {
                        requestBuilder.removeHeader("Transfer-Encoding");
                        requestBuilder.removeHeader("Content-Length");
                        requestBuilder.removeHeader("Content-Type");
                     }
                  }

                  if (!_UtilJvmKt.canReuseConnectionFor(userResponse.request().url(), url)) {
                     requestBuilder.removeHeader("Authorization");
                  }

                  return requestBuilder.url(url).build();
               }
            }
         }
      }
   }

   private final int retryAfter(Response userResponse, int defaultDelay) {
      String var10000 = Response.header$default(userResponse, "Retry-After", (String)null, 2, (Object)null);
      if (var10000 == null) {
         return defaultDelay;
      } else {
         String header = var10000;
         CharSequence var4 = (CharSequence)header;
         if ((new Regex("\\d+")).matches(var4)) {
            Integer var5 = Integer.valueOf(header);
            Intrinsics.checkNotNullExpressionValue(var5, "valueOf(...)");
            return ((Number)var5).intValue();
         } else {
            return Integer.MAX_VALUE;
         }
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0006"},
      d2 = {"Lokhttp3/internal/http/RetryAndFollowUpInterceptor$Companion;", "", "<init>", "()V", "MAX_FOLLOW_UPS", "", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
