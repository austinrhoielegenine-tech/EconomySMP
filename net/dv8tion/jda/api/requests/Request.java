package net.dv8tion.jda.api.requests;

import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.audit.ThreadLocalReason;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.events.http.HttpRequestEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.requests.CallbackContext;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.IOUtil;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

public class Request<T> {
   private final JDAImpl api;
   private final RestActionImpl<T> restAction;
   private final Consumer<? super T> onSuccess;
   private final Consumer<? super Throwable> onFailure;
   private final BooleanSupplier checks;
   private final boolean shouldQueue;
   private final Route.CompiledRoute route;
   private final RequestBody body;
   private final Object rawBody;
   private final CaseInsensitiveMap<String, String> headers;
   private final long deadline;
   private final boolean priority;
   private final String localReason;
   private boolean done = false;
   private boolean isCancelled = false;

   public Request(RestActionImpl<T> restAction, Consumer<? super T> onSuccess, Consumer<? super Throwable> onFailure, BooleanSupplier checks, boolean shouldQueue, RequestBody body, Object rawBody, long deadline, boolean priority, Route.CompiledRoute route, CaseInsensitiveMap<String, String> headers) {
      this.deadline = deadline;
      this.priority = priority;
      this.restAction = restAction;
      this.onSuccess = onSuccess;
      if (onFailure instanceof ContextException.ContextConsumer) {
         this.onFailure = onFailure;
      } else if (RestActionImpl.isPassContext()) {
         this.onFailure = ContextException.here(onFailure);
      } else {
         this.onFailure = onFailure;
      }

      this.checks = checks;
      this.shouldQueue = shouldQueue;
      this.body = body;
      this.rawBody = rawBody;
      this.route = route;
      this.headers = headers;
      this.api = (JDAImpl)restAction.getJDA();
      this.localReason = ThreadLocalReason.getCurrent();
   }

   private void cleanup() {
      if (this.body instanceof MultipartBody) {
         MultipartBody multi = (MultipartBody)this.body;
         Stream var10000 = multi.parts().stream().map(MultipartBody.Part::body);
         Objects.requireNonNull(AutoCloseable.class);
         var10000 = var10000.filter(AutoCloseable.class::isInstance);
         Objects.requireNonNull(AutoCloseable.class);
         var10000.map(AutoCloseable.class::cast).forEach(IOUtil::silentClose);
      } else if (this.body instanceof AutoCloseable) {
         IOUtil.silentClose((AutoCloseable)this.body);
      }

   }

   public void onSuccess(@Nullable T successObj) {
      if (!this.done) {
         this.done = true;
         this.cleanup();
         RestActionImpl.LOG.trace((String)"Scheduling success callback for request with route {}/{}", (Object)this.route.getMethod(), (Object)this.route.getCompiledRoute());
         this.api.getCallbackPool().execute(() -> {
            try {
               ThreadLocalReason.Closable __ = ThreadLocalReason.closable(this.localReason);

               try {
                  CallbackContext ___ = CallbackContext.getInstance();

                  try {
                     RestActionImpl.LOG.trace((String)"Running success callback for request with route {}/{}", (Object)this.route.getMethod(), (Object)this.route.getCompiledRoute());
                     this.onSuccess.accept(successObj);
                  } catch (Throwable var8) {
                     if (___ != null) {
                        try {
                           ___.close();
                        } catch (Throwable x2) {
                           var8.addSuppressed(x2);
                        }
                     }

                     throw var8;
                  }

                  if (___ != null) {
                     ___.close();
                  }
               } catch (Throwable var9) {
                  if (__ != null) {
                     try {
                        __.close();
                     } catch (Throwable x2) {
                        var9.addSuppressed(x2);
                     }
                  }

                  throw var9;
               }

               if (__ != null) {
                  __.close();
               }
            } catch (Throwable t) {
               RestActionImpl.LOG.error("Encountered error while processing success consumer", t);
               if (t instanceof Error) {
                  this.api.handleEvent(new ExceptionEvent(this.api, t, true));
                  throw (Error)t;
               }
            }

         });
      }
   }

   public void onFailure(@Nonnull Response response) {
      if (response.code == 429) {
         this.onRateLimited(response);
      } else {
         this.onFailure((Throwable)this.createErrorResponseException(response));
      }

   }

   public void onRateLimited(@Nonnull Response response) {
      this.onFailure((Throwable)(new RateLimitedException(this.route, response.retryAfter)));
   }

   @Nonnull
   public ErrorResponseException createErrorResponseException(@Nonnull Response response) {
      return ErrorResponseException.create(ErrorResponse.fromJSON((DataObject)response.optObject().orElse((Object)null)), response);
   }

   public void onFailure(@Nonnull Throwable failException) {
      if (!this.done) {
         this.done = true;
         this.cleanup();
         RestActionImpl.LOG.trace((String)"Scheduling failure callback for request with route {}/{}", (Object)this.route.getMethod(), (Object)this.route.getCompiledRoute());
         this.api.getCallbackPool().execute(() -> {
            try {
               ThreadLocalReason.Closable __ = ThreadLocalReason.closable(this.localReason);

               try {
                  CallbackContext ___ = CallbackContext.getInstance();

                  try {
                     RestActionImpl.LOG.trace((String)"Running failure callback for request with route {}/{}", (Object)this.route.getMethod(), (Object)this.route.getCompiledRoute());
                     this.onFailure.accept(failException);
                     if (failException instanceof Error) {
                        this.api.handleEvent(new ExceptionEvent(this.api, failException, false));
                     }
                  } catch (Throwable var8) {
                     if (___ != null) {
                        try {
                           ___.close();
                        } catch (Throwable x2) {
                           var8.addSuppressed(x2);
                        }
                     }

                     throw var8;
                  }

                  if (___ != null) {
                     ___.close();
                  }
               } catch (Throwable var9) {
                  if (__ != null) {
                     try {
                        __.close();
                     } catch (Throwable x2) {
                        var9.addSuppressed(x2);
                     }
                  }

                  throw var9;
               }

               if (__ != null) {
                  __.close();
               }
            } catch (Throwable t) {
               RestActionImpl.LOG.error("Encountered error while processing failure consumer", t);
               if (t instanceof Error) {
                  this.api.handleEvent(new ExceptionEvent(this.api, t, true));
                  throw (Error)t;
               }
            }

         });
      }
   }

   public void onCancelled() {
      this.onFailure((Throwable)(new CancellationException("RestAction has been cancelled")));
   }

   public void onTimeout() {
      this.onFailure((Throwable)(new TimeoutException("RestAction has timed out")));
   }

   @Nonnull
   public JDAImpl getJDA() {
      return this.api;
   }

   @Nonnull
   @CheckReturnValue
   public RestAction<T> getRestAction() {
      return this.restAction;
   }

   @Nonnull
   public Consumer<? super T> getOnSuccess() {
      return this.onSuccess;
   }

   @Nonnull
   public Consumer<? super Throwable> getOnFailure() {
      return this.onFailure;
   }

   public boolean isPriority() {
      return this.priority;
   }

   public boolean isSkipped() {
      if (this.isTimeout()) {
         this.onTimeout();
         return true;
      } else {
         boolean skip = this.runChecks();
         if (skip) {
            this.onCancelled();
         }

         return skip;
      }
   }

   private boolean isTimeout() {
      return this.deadline > 0L && this.deadline < System.currentTimeMillis();
   }

   private boolean runChecks() {
      try {
         return this.isCancelled() || this.checks != null && !this.checks.getAsBoolean();
      } catch (Exception e) {
         this.onFailure((Throwable)e);
         return true;
      }
   }

   @Nullable
   public CaseInsensitiveMap<String, String> getHeaders() {
      return this.headers;
   }

   @Nonnull
   public Route.CompiledRoute getRoute() {
      return this.route;
   }

   @Nullable
   public RequestBody getBody() {
      return this.body;
   }

   @Nullable
   public Object getRawBody() {
      return this.rawBody;
   }

   public boolean shouldQueue() {
      return this.shouldQueue;
   }

   public void cancel() {
      if (!this.isCancelled) {
         this.onCancelled();
      }

      this.isCancelled = true;
   }

   public boolean isCancelled() {
      return this.isCancelled;
   }

   public void handleResponse(@Nonnull Response response) {
      RestActionImpl.LOG.trace("Handling response for request with route {}/{} and code {}", this.route.getMethod(), this.route.getCompiledRoute(), response.code);
      this.restAction.handleResponse(response, this);
      this.api.handleEvent(new HttpRequestEvent(this, response));
   }
}
