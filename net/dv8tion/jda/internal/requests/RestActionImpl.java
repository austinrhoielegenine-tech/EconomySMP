package net.dv8tion.jda.internal.requests;

import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.RestFuture;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.AttachedFile;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.JDALogger;
import okhttp3.RequestBody;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.slf4j.Logger;

public class RestActionImpl<T> implements RestAction<T> {
   public static final Logger LOG = JDALogger.getLog(RestAction.class);
   private static Consumer<Object> DEFAULT_SUCCESS = (o) -> {
   };
   private static Consumer<? super Throwable> DEFAULT_FAILURE = (t) -> {
      if (!(t instanceof CancellationException) && !(t instanceof TimeoutException)) {
         if (!LOG.isDebugEnabled() && t instanceof ErrorResponseException) {
            if (t.getCause() != null) {
               LOG.error("RestAction queue returned failure: [{}] {}", t.getClass().getSimpleName(), t.getMessage(), t.getCause());
            } else {
               LOG.error((String)"RestAction queue returned failure: [{}] {}", (Object)t.getClass().getSimpleName(), (Object)t.getMessage());
            }
         } else {
            LOG.error("RestAction queue returned failure", t);
         }
      } else {
         LOG.debug(t.getMessage());
      }

   };
   protected static boolean passContext = true;
   protected static long defaultTimeout = 0L;
   protected final JDAImpl api;
   private final Route.CompiledRoute route;
   private final RequestBody data;
   private final BiFunction<Response, Request<T>, T> handler;
   private ErrorMapper errorMapper;
   private boolean priority;
   private long deadline;
   private Object rawData;
   private BooleanSupplier checks;

   public static void setPassContext(boolean enable) {
      passContext = enable;
   }

   public static boolean isPassContext() {
      return passContext;
   }

   public static void setDefaultFailure(Consumer<? super Throwable> callback) {
      DEFAULT_FAILURE = callback == null ? (t) -> {
      } : callback;
   }

   public static void setDefaultSuccess(Consumer<Object> callback) {
      DEFAULT_SUCCESS = callback == null ? (t) -> {
      } : callback;
   }

   public static void setDefaultTimeout(long timeout, @Nonnull TimeUnit unit) {
      Checks.notNull(unit, "TimeUnit");
      defaultTimeout = unit.toMillis(timeout);
   }

   public static long getDefaultTimeout() {
      return defaultTimeout;
   }

   public static Consumer<? super Throwable> getDefaultFailure() {
      return DEFAULT_FAILURE;
   }

   public static Consumer<Object> getDefaultSuccess() {
      return DEFAULT_SUCCESS;
   }

   public RestActionImpl(JDA api, Route.CompiledRoute route) {
      this(api, route, (RequestBody)((RequestBody)null), (BiFunction)null);
   }

   public RestActionImpl(JDA api, Route.CompiledRoute route, DataObject data) {
      this(api, route, (DataObject)data, (BiFunction)null);
   }

   public RestActionImpl(JDA api, Route.CompiledRoute route, RequestBody data) {
      this(api, route, (RequestBody)data, (BiFunction)null);
   }

   public RestActionImpl(JDA api, Route.CompiledRoute route, BiFunction<Response, Request<T>, T> handler) {
      this(api, route, (RequestBody)null, handler);
   }

   public RestActionImpl(JDA api, Route.CompiledRoute route, DataObject data, BiFunction<Response, Request<T>, T> handler) {
      this(api, route, data == null ? null : RequestBody.create(Requester.MEDIA_TYPE_JSON, data.toJson()), handler);
      this.rawData = data;
   }

   public RestActionImpl(JDA api, Route.CompiledRoute route, RequestBody data, BiFunction<Response, Request<T>, T> handler) {
      this.errorMapper = null;
      this.priority = false;
      this.deadline = 0L;
      Checks.notNull(api, "api");
      this.api = (JDAImpl)api;
      this.route = route;
      this.data = data;
      this.handler = handler;
   }

   public void setErrorMapper(ErrorMapper errorMapper) {
      this.errorMapper = errorMapper;
   }

   public RestActionImpl<T> priority() {
      this.priority = true;
      return this;
   }

   @Nonnull
   public JDA getJDA() {
      return this.api;
   }

   @Nonnull
   public RestAction<T> setCheck(BooleanSupplier checks) {
      this.checks = checks;
      return this;
   }

   @Nullable
   public BooleanSupplier getCheck() {
      return this.checks;
   }

   @Nonnull
   public RestAction<T> deadline(long timestamp) {
      this.deadline = timestamp;
      return this;
   }

   public void queue(Consumer<? super T> success, Consumer<? super Throwable> failure) {
      Route.CompiledRoute route = this.finalizeRoute();
      Checks.notNull(route, "Route");
      RequestBody data = this.finalizeData();
      CaseInsensitiveMap<String, String> headers = this.finalizeHeaders();
      BooleanSupplier finisher = this.getFinisher();
      if (success == null) {
         success = DEFAULT_SUCCESS;
      }

      if (failure == null) {
         failure = DEFAULT_FAILURE;
      }

      this.api.getRequester().request(new Request(this, success, failure, finisher, true, data, this.rawData, this.getDeadline(), this.priority, route, headers));
   }

   @Nonnull
   public CompletableFuture<T> submit(boolean shouldQueue) {
      Route.CompiledRoute route = this.finalizeRoute();
      Checks.notNull(route, "Route");
      RequestBody data = this.finalizeData();
      CaseInsensitiveMap<String, String> headers = this.finalizeHeaders();
      BooleanSupplier finisher = this.getFinisher();
      return new RestFuture<T>(this, shouldQueue, finisher, data, this.rawData, this.getDeadline(), this.priority, route, headers);
   }

   public T complete(boolean shouldQueue) throws RateLimitedException {
      if (CallbackContext.isCallbackContext()) {
         throw new IllegalStateException("Preventing use of complete() in callback threads! This operation can be a deadlock cause");
      } else {
         try {
            return (T)this.submit(shouldQueue).join();
         } catch (CompletionException var4) {
            if (var4.getCause() != null) {
               Throwable cause = var4.getCause();
               if (cause instanceof ErrorResponseException) {
                  throw (ErrorResponseException)cause.fillInStackTrace();
               }

               if (cause instanceof RateLimitedException) {
                  throw (RateLimitedException)cause.fillInStackTrace();
               }

               if (cause instanceof RuntimeException) {
                  throw (RuntimeException)cause;
               }

               if (cause instanceof Error) {
                  throw (Error)cause;
               }
            }

            throw var4;
         }
      }
   }

   protected RequestBody finalizeData() {
      return this.data;
   }

   protected Route.CompiledRoute finalizeRoute() {
      return this.route;
   }

   protected CaseInsensitiveMap<String, String> finalizeHeaders() {
      return null;
   }

   protected BooleanSupplier finalizeChecks() {
      return null;
   }

   protected RequestBody getRequestBody(DataObject object) {
      this.rawData = object;
      return object == null ? null : RequestBody.create(Requester.MEDIA_TYPE_JSON, object.toJson());
   }

   protected RequestBody getRequestBody(DataArray array) {
      this.rawData = array;
      return array == null ? null : RequestBody.create(Requester.MEDIA_TYPE_JSON, array.toJson());
   }

   @Nonnull
   protected RequestBody getMultipartBody(@Nonnull Set<? extends AttachedFile> files, @Nonnull DataObject json) {
      RequestBody payloadJson = this.getRequestBody(json);
      return (RequestBody)(files.isEmpty() ? payloadJson : AttachedFile.createMultipartBody(files, (RequestBody)payloadJson).build());
   }

   private CheckWrapper getFinisher() {
      BooleanSupplier pre = this.finalizeChecks();
      BooleanSupplier wrapped = this.checks;
      return pre == null && wrapped == null ? RestActionImpl.CheckWrapper.EMPTY : new CheckWrapper(wrapped, pre);
   }

   public void handleResponse(Response response, Request<T> request) {
      if (response.isOk()) {
         this.handleSuccess(response, request);
      } else if (response.isRateLimit()) {
         request.onRateLimited(response);
      } else {
         ErrorResponseException exception = request.createErrorResponseException(response);
         Throwable mappedThrowable = this.errorMapper != null ? this.errorMapper.apply(response, request, exception) : null;
         if (mappedThrowable != null) {
            request.onFailure(mappedThrowable);
         } else {
            request.onFailure((Throwable)exception);
         }
      }

   }

   protected void handleSuccess(Response response, Request<T> request) {
      if (this.handler == null) {
         request.onSuccess((Object)null);
      } else {
         request.onSuccess(this.handler.apply(response, request));
      }

   }

   private long getDeadline() {
      return this.deadline > 0L ? this.deadline : (defaultTimeout > 0L ? System.currentTimeMillis() + defaultTimeout : 0L);
   }

   protected static class CheckWrapper implements BooleanSupplier {
      public static final CheckWrapper EMPTY = new CheckWrapper((BooleanSupplier)null, (BooleanSupplier)null) {
         public boolean getAsBoolean() {
            return true;
         }
      };
      protected final BooleanSupplier pre;
      protected final BooleanSupplier wrapped;

      public CheckWrapper(BooleanSupplier wrapped, BooleanSupplier pre) {
         this.pre = pre;
         this.wrapped = wrapped;
      }

      public boolean pre() {
         return this.pre == null || this.pre.getAsBoolean();
      }

      public boolean test() {
         return this.wrapped == null || this.wrapped.getAsBoolean();
      }

      public boolean getAsBoolean() {
         return this.pre() && this.test();
      }
   }
}
