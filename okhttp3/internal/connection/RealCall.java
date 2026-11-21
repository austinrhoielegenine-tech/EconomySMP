package okhttp3.internal.connection;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KClass;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Tags;
import okhttp3.internal.TagsKt;
import okhttp3.internal._UtilCommonKt;
import okhttp3.internal._UtilJvmKt;
import okhttp3.internal.cache.CacheInterceptor;
import okhttp3.internal.concurrent.Lockable;
import okhttp3.internal.http.BridgeInterceptor;
import okhttp3.internal.http.CallServerInterceptor;
import okhttp3.internal.http.ExchangeCodec;
import okhttp3.internal.http.RealInterceptorChain;
import okhttp3.internal.http.RetryAndFollowUpInterceptor;
import okhttp3.internal.platform.Platform;
import okio.AsyncTimeout;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000É\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u001a\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000e\n\u0002\b\u0005*\u0001\u0019\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u0003:\u0002uvB\u001f\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\b\u0010\u0018\u001a\u00020;H\u0016J'\u0010<\u001a\u0004\u0018\u0001H=\"\b\b\u0000\u0010=*\u00020\u001e2\f\u0010>\u001a\b\u0012\u0004\u0012\u0002H=0?H\u0016¢\u0006\u0002\u0010@J%\u0010<\u001a\u0004\u0018\u0001H=\"\u0004\b\u0000\u0010=2\u000e\u0010>\u001a\n\u0012\u0006\b\u0001\u0012\u0002H=0AH\u0016¢\u0006\u0002\u0010BJ3\u0010<\u001a\u0002H=\"\b\b\u0000\u0010=*\u00020\u001e2\f\u0010>\u001a\b\u0012\u0004\u0012\u0002H=0?2\f\u0010C\u001a\b\u0012\u0004\u0012\u0002H=0DH\u0016¢\u0006\u0002\u0010EJ3\u0010<\u001a\u0002H=\"\b\b\u0000\u0010=*\u00020\u001e2\f\u0010>\u001a\b\u0012\u0004\u0012\u0002H=0A2\f\u0010C\u001a\b\u0012\u0004\u0012\u0002H=0DH\u0016¢\u0006\u0002\u0010FJ\b\u0010G\u001a\u00020\u0001H\u0016J\b\u0010H\u001a\u00020\u0007H\u0016J\b\u0010I\u001a\u00020JH\u0016J\b\u0010K\u001a\u00020\tH\u0016J\b\u0010L\u001a\u00020MH\u0016J\u0010\u0010N\u001a\u00020J2\u0006\u0010O\u001a\u00020PH\u0016J\b\u0010Q\u001a\u00020\tH\u0016J\b\u0010R\u001a\u00020JH\u0002J\r\u0010S\u001a\u00020MH\u0000¢\u0006\u0002\bTJ\u001e\u0010U\u001a\u00020J2\u0006\u0010H\u001a\u00020\u00072\u0006\u0010V\u001a\u00020\t2\u0006\u0010W\u001a\u00020XJ\u0015\u0010Y\u001a\u00020'2\u0006\u0010W\u001a\u00020XH\u0000¢\u0006\u0002\bZJ\u000e\u0010[\u001a\u00020J2\u0006\u0010#\u001a\u00020\"JI\u0010\\\u001a\u0004\u0018\u00010]2\u0006\u00101\u001a\u00020'2\b\b\u0002\u0010^\u001a\u00020\t2\b\b\u0002\u0010_\u001a\u00020\t2\b\b\u0002\u0010`\u001a\u00020\t2\b\b\u0002\u0010a\u001a\u00020\t2\b\u0010b\u001a\u0004\u0018\u00010]H\u0000¢\u0006\u0002\bcJ\u0019\u0010d\u001a\u0004\u0018\u00010]2\b\u0010b\u001a\u0004\u0018\u00010]H\u0000¢\u0006\u0002\beJ\u0014\u0010f\u001a\u0004\u0018\u00010]2\b\u0010b\u001a\u0004\u0018\u00010]H\u0002J\u000f\u0010g\u001a\u0004\u0018\u00010hH\u0000¢\u0006\u0002\biJ\u0014\u0010j\u001a\u0004\u0018\u00010]2\b\u0010k\u001a\u0004\u0018\u00010]H\u0002J\u0006\u0010&\u001a\u00020JJ\u0006\u0010l\u001a\u00020JJ\u0015\u0010m\u001a\u00020J2\u0006\u0010n\u001a\u00020\tH\u0000¢\u0006\u0002\boJ\u0006\u0010p\u001a\u00020\tJ\b\u0010q\u001a\u00020rH\u0002J\r\u0010s\u001a\u00020rH\u0000¢\u0006\u0002\btR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0014\u001a\u00020\u0015X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0010\u0010\u0018\u001a\u00020\u0019X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u001aR\u000e\u0010\u001b\u001a\u00020\u001cX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u001eX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u001f\u001a\u0004\u0018\u00010 X\u0082\u000e¢\u0006\u0002\n\u0000R\"\u0010#\u001a\u0004\u0018\u00010\"2\b\u0010!\u001a\u0004\u0018\u00010\"@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b$\u0010%R\u000e\u0010&\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\"\u0010(\u001a\u0004\u0018\u00010'2\b\u0010!\u001a\u0004\u0018\u00010'@BX\u0080\u000e¢\u0006\b\n\u0000\u001a\u0004\b)\u0010*R\u000e\u0010+\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010,\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010-\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010.\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010/\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u00100\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u00101\u001a\u0004\u0018\u00010'X\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u00102\u001a\b\u0012\u0004\u0012\u00020403X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b5\u00106R\u001c\u00107\u001a\u0010\u0012\f\u0012\n :*\u0004\u0018\u0001090908X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006w"},
   d2 = {"Lokhttp3/internal/connection/RealCall;", "Lokhttp3/Call;", "", "Lokhttp3/internal/concurrent/Lockable;", "client", "Lokhttp3/OkHttpClient;", "originalRequest", "Lokhttp3/Request;", "forWebSocket", "", "<init>", "(Lokhttp3/OkHttpClient;Lokhttp3/Request;Z)V", "getClient", "()Lokhttp3/OkHttpClient;", "getOriginalRequest", "()Lokhttp3/Request;", "getForWebSocket", "()Z", "connectionPool", "Lokhttp3/internal/connection/RealConnectionPool;", "eventListener", "Lokhttp3/EventListener;", "getEventListener$okhttp", "()Lokhttp3/EventListener;", "timeout", "okhttp3/internal/connection/RealCall$timeout$1", "Lokhttp3/internal/connection/RealCall$timeout$1;", "executed", "Ljava/util/concurrent/atomic/AtomicBoolean;", "callStackTrace", "", "exchangeFinder", "Lokhttp3/internal/connection/ExchangeFinder;", "value", "Lokhttp3/internal/connection/RealConnection;", "connection", "getConnection", "()Lokhttp3/internal/connection/RealConnection;", "timeoutEarlyExit", "Lokhttp3/internal/connection/Exchange;", "interceptorScopedExchange", "getInterceptorScopedExchange$okhttp", "()Lokhttp3/internal/connection/Exchange;", "requestBodyOpen", "responseBodyOpen", "socketSinkOpen", "socketSourceOpen", "expectMoreExchanges", "canceled", "exchange", "plansToCancel", "Ljava/util/concurrent/CopyOnWriteArrayList;", "Lokhttp3/internal/connection/RoutePlanner$Plan;", "getPlansToCancel$okhttp", "()Ljava/util/concurrent/CopyOnWriteArrayList;", "tags", "Ljava/util/concurrent/atomic/AtomicReference;", "Lokhttp3/internal/Tags;", "kotlin.jvm.PlatformType", "Lokio/Timeout;", "tag", "T", "type", "Lkotlin/reflect/KClass;", "(Lkotlin/reflect/KClass;)Ljava/lang/Object;", "Ljava/lang/Class;", "(Ljava/lang/Class;)Ljava/lang/Object;", "computeIfAbsent", "Lkotlin/Function0;", "(Lkotlin/reflect/KClass;Lkotlin/jvm/functions/Function0;)Ljava/lang/Object;", "(Ljava/lang/Class;Lkotlin/jvm/functions/Function0;)Ljava/lang/Object;", "clone", "request", "cancel", "", "isCanceled", "execute", "Lokhttp3/Response;", "enqueue", "responseCallback", "Lokhttp3/Callback;", "isExecuted", "callStart", "getResponseWithInterceptorChain", "getResponseWithInterceptorChain$okhttp", "enterNetworkInterceptorExchange", "newRoutePlanner", "chain", "Lokhttp3/internal/http/RealInterceptorChain;", "initExchange", "initExchange$okhttp", "acquireConnectionNoEvents", "messageDone", "Ljava/io/IOException;", "requestDone", "responseDone", "socketSourceDone", "socketSinkDone", "e", "messageDone$okhttp", "noMoreExchanges", "noMoreExchanges$okhttp", "callDone", "releaseConnectionNoEvents", "Ljava/net/Socket;", "releaseConnectionNoEvents$okhttp", "timeoutExit", "cause", "upgradeToSocket", "exitNetworkInterceptorExchange", "closeExchange", "exitNetworkInterceptorExchange$okhttp", "retryAfterFailure", "toLoggableString", "", "redactedUrl", "redactedUrl$okhttp", "AsyncCall", "CallReference", "okhttp"}
)
@SourceDebugExtension({"SMAP\nRealCall.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealCall.kt\nokhttp3/internal/connection/RealCall\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Lockable.kt\nokhttp3/internal/concurrent/LockableKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,612:1\n1#2:613\n63#3:614\n63#3:615\n63#3:616\n55#3,4:617\n63#3:621\n63#3:622\n49#3,4:623\n49#3,4:627\n63#3:631\n55#3,4:632\n63#3:643\n63#3:644\n360#4,7:636\n*S KotlinDebug\n*F\n+ 1 RealCall.kt\nokhttp3/internal/connection/RealCall\n*L\n257#1:614\n292#1:615\n303#1:616\n313#1:617,4\n340#1:621\n373#1:622\n401#1:623,4\n405#1:627,4\n407#1:631\n438#1:632,4\n479#1:643\n495#1:644\n441#1:636,7\n*E\n"})
public final class RealCall implements Call, Cloneable, Lockable {
   @NotNull
   private final OkHttpClient client;
   @NotNull
   private final Request originalRequest;
   private final boolean forWebSocket;
   @NotNull
   private final RealConnectionPool connectionPool;
   @NotNull
   private final EventListener eventListener;
   @NotNull
   private final <undefinedtype> timeout;
   @NotNull
   private final AtomicBoolean executed;
   @Nullable
   private Object callStackTrace;
   @Nullable
   private ExchangeFinder exchangeFinder;
   @Nullable
   private RealConnection connection;
   private boolean timeoutEarlyExit;
   @Nullable
   private Exchange interceptorScopedExchange;
   private boolean requestBodyOpen;
   private boolean responseBodyOpen;
   private boolean socketSinkOpen;
   private boolean socketSourceOpen;
   private boolean expectMoreExchanges;
   private volatile boolean canceled;
   @Nullable
   private volatile Exchange exchange;
   @NotNull
   private final CopyOnWriteArrayList<RoutePlanner.Plan> plansToCancel;
   @NotNull
   private final AtomicReference<Tags> tags;

   public RealCall(@NotNull OkHttpClient client, @NotNull Request originalRequest, boolean forWebSocket) {
      Intrinsics.checkNotNullParameter(client, "client");
      Intrinsics.checkNotNullParameter(originalRequest, "originalRequest");
      super();
      this.client = client;
      this.originalRequest = originalRequest;
      this.forWebSocket = forWebSocket;
      this.connectionPool = this.client.connectionPool().getDelegate$okhttp();
      this.eventListener = this.client.eventListenerFactory().create(this);
      AsyncTimeout var4 = new AsyncTimeout() {
         protected void timedOut() {
            RealCall.this.cancel();
         }
      };
      int var6 = 0;
      ((<undefinedtype>)var4).timeout((long)this.client.callTimeoutMillis(), TimeUnit.MILLISECONDS);
      this.timeout = var4;
      this.executed = new AtomicBoolean();
      this.expectMoreExchanges = true;
      this.plansToCancel = new CopyOnWriteArrayList();
      this.tags = new AtomicReference(this.originalRequest.getTags$okhttp());
   }

   @NotNull
   public final OkHttpClient getClient() {
      return this.client;
   }

   @NotNull
   public final Request getOriginalRequest() {
      return this.originalRequest;
   }

   public final boolean getForWebSocket() {
      return this.forWebSocket;
   }

   @NotNull
   public final EventListener getEventListener$okhttp() {
      return this.eventListener;
   }

   @Nullable
   public final RealConnection getConnection() {
      return this.connection;
   }

   @Nullable
   public final Exchange getInterceptorScopedExchange$okhttp() {
      return this.interceptorScopedExchange;
   }

   @NotNull
   public final CopyOnWriteArrayList<RoutePlanner.Plan> getPlansToCancel$okhttp() {
      return this.plansToCancel;
   }

   @NotNull
   public Timeout timeout() {
      return this.timeout;
   }

   @Nullable
   public <T> T tag(@NotNull KClass<T> type) {
      Intrinsics.checkNotNullParameter(type, "type");
      return (T)JvmClassMappingKt.getJavaClass(type).cast(((Tags)this.tags.get()).get(type));
   }

   @Nullable
   public <T> T tag(@NotNull Class<? extends T> type) {
      Intrinsics.checkNotNullParameter(type, "type");
      return (T)this.tag(JvmClassMappingKt.getKotlinClass(type));
   }

   @NotNull
   public <T> T tag(@NotNull KClass<T> type, @NotNull Function0<? extends T> computeIfAbsent) {
      Intrinsics.checkNotNullParameter(type, "type");
      Intrinsics.checkNotNullParameter(computeIfAbsent, "computeIfAbsent");
      return (T)TagsKt.computeIfAbsent(this.tags, type, computeIfAbsent);
   }

   @NotNull
   public <T> T tag(@NotNull Class<T> type, @NotNull Function0<? extends T> computeIfAbsent) {
      Intrinsics.checkNotNullParameter(type, "type");
      Intrinsics.checkNotNullParameter(computeIfAbsent, "computeIfAbsent");
      return (T)TagsKt.computeIfAbsent(this.tags, JvmClassMappingKt.getKotlinClass(type), computeIfAbsent);
   }

   @NotNull
   public Call clone() {
      return new RealCall(this.client, this.originalRequest, this.forWebSocket);
   }

   @NotNull
   public Request request() {
      return this.originalRequest;
   }

   public void cancel() {
      if (!this.canceled) {
         this.canceled = true;
         Exchange var10000 = this.exchange;
         if (var10000 != null) {
            var10000.cancel();
         }

         Iterator var3 = this.plansToCancel.iterator();
         Intrinsics.checkNotNullExpressionValue(var3, "iterator(...)");
         Iterator var1 = var3;

         while(var1.hasNext()) {
            RoutePlanner.Plan plan = (RoutePlanner.Plan)var1.next();
            plan.cancel();
         }

         this.eventListener.canceled(this);
      }
   }

   public boolean isCanceled() {
      return this.canceled;
   }

   @NotNull
   public Response execute() {
      if (!this.executed.compareAndSet(false, true)) {
         int var2 = 0;
         String var5 = "Already Executed";
         throw new IllegalStateException(var5.toString());
      } else {
         this.timeout.enter();
         this.callStart();

         Response var1;
         try {
            this.client.dispatcher().executed$okhttp(this);
            var1 = this.getResponseWithInterceptorChain$okhttp();
         } finally {
            this.client.dispatcher().finished$okhttp(this);
         }

         return var1;
      }
   }

   public void enqueue(@NotNull Callback responseCallback) {
      Intrinsics.checkNotNullParameter(responseCallback, "responseCallback");
      if (!this.executed.compareAndSet(false, true)) {
         int var2 = 0;
         String var3 = "Already Executed";
         throw new IllegalStateException(var3.toString());
      } else {
         this.callStart();
         this.client.dispatcher().enqueue$okhttp(new AsyncCall(responseCallback));
      }
   }

   public boolean isExecuted() {
      return this.executed.get();
   }

   private final void callStart() {
      this.callStackTrace = Platform.Companion.get().getStackTraceForCloseable("response.body().close()");
      this.eventListener.callStart(this);
   }

   @NotNull
   public final Response getResponseWithInterceptorChain$okhttp() throws IOException {
      List interceptors = (List)(new ArrayList());
      CollectionsKt.addAll((Collection)interceptors, (Iterable)this.client.interceptors());
      ((Collection)interceptors).add(new RetryAndFollowUpInterceptor(this.client));
      ((Collection)interceptors).add(new BridgeInterceptor(this.client.cookieJar()));
      ((Collection)interceptors).add(new CacheInterceptor(this.client.cache()));
      ((Collection)interceptors).add(ConnectInterceptor.INSTANCE);
      if (!this.forWebSocket) {
         CollectionsKt.addAll((Collection)interceptors, (Iterable)this.client.networkInterceptors());
      }

      ((Collection)interceptors).add(CallServerInterceptor.INSTANCE);
      RealInterceptorChain chain = new RealInterceptorChain(this, interceptors, 0, (Exchange)null, this.originalRequest, this.client.connectTimeoutMillis(), this.client.readTimeoutMillis(), this.client.writeTimeoutMillis());
      boolean calledNoMoreExchanges = false;
      boolean var8 = false;

      Response var5;
      try {
         var8 = true;
         Response response = chain.proceed(this.originalRequest);
         if (this.isCanceled()) {
            _UtilCommonKt.closeQuietly(response);
            throw new IOException("Canceled");
         }

         var5 = response;
         var8 = false;
      } catch (IOException e) {
         calledNoMoreExchanges = true;
         IOException var10000 = this.noMoreExchanges$okhttp(e);
         Intrinsics.checkNotNull(var10000, "null cannot be cast to non-null type kotlin.Throwable");
         throw (Throwable)var10000;
      } finally {
         if (var8) {
            if (!calledNoMoreExchanges) {
               this.noMoreExchanges$okhttp((IOException)null);
            }

         }
      }

      this.noMoreExchanges$okhttp((IOException)null);
      return var5;
   }

   public final void enterNetworkInterceptorExchange(@NotNull Request request, boolean newRoutePlanner, @NotNull RealInterceptorChain chain) {
      Intrinsics.checkNotNullParameter(request, "request");
      Intrinsics.checkNotNullParameter(chain, "chain");
      if (this.interceptorScopedExchange != null) {
         throw new IllegalStateException("Check failed.");
      } else {
         Lockable $this$withLock$iv = this;
         int $i$f$withLock = 0;
         synchronized($this$withLock$iv){}

         try {
            int var7 = 0;
            if (this.responseBodyOpen) {
               int var8 = 0;
               String var13 = "cannot make a new request because the previous response is still open: please call response.close()";
               throw new IllegalStateException(var13.toString());
            }

            if (this.requestBodyOpen || this.socketSourceOpen || this.socketSinkOpen) {
               throw new IllegalStateException("Check failed.");
            }

            Unit var12 = Unit.INSTANCE;
         } finally {
            ;
         }

         if (newRoutePlanner) {
            RealRoutePlanner routePlanner = new RealRoutePlanner(this.client.getTaskRunner$okhttp(), this.connectionPool, this.client.readTimeoutMillis(), this.client.writeTimeoutMillis(), chain.getConnectTimeoutMillis$okhttp(), chain.getReadTimeoutMillis$okhttp(), this.client.pingIntervalMillis(), this.client.retryOnConnectionFailure(), this.client.fastFallback(), this.client.address(request.url()), this.client.getRouteDatabase$okhttp(), this, request);
            this.exchangeFinder = this.client.fastFallback() ? (ExchangeFinder)(new FastFallbackExchangeFinder(routePlanner, this.client.getTaskRunner$okhttp())) : (ExchangeFinder)(new SequentialExchangeFinder(routePlanner));
         }

      }
   }

   @NotNull
   public final Exchange initExchange$okhttp(@NotNull RealInterceptorChain chain) {
      Intrinsics.checkNotNullParameter(chain, "chain");
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv){}

      try {
         int var5 = 0;
         if (!this.expectMoreExchanges) {
            int var6 = 0;
            String $this$withLock$iv = "released";
            throw new IllegalStateException($this$withLock$iv.toString());
         }

         if (this.responseBodyOpen || this.requestBodyOpen || this.socketSourceOpen || this.socketSinkOpen) {
            throw new IllegalStateException("Check failed.");
         }

         Unit result = Unit.INSTANCE;
      } finally {
         ;
      }

      ExchangeFinder var10000 = this.exchangeFinder;
      Intrinsics.checkNotNull(var10000);
      ExchangeFinder exchangeFinder = var10000;
      RealConnection connection = exchangeFinder.find();
      ExchangeCodec codec = connection.newCodec$okhttp(this.client, chain);
      Exchange var18 = new Exchange(this, this.eventListener, exchangeFinder, codec);
      this.interceptorScopedExchange = var18;
      this.exchange = var18;
      Lockable var20 = this;
      boolean var7 = false;
      synchronized(var20) {
         int var9 = 0;
         this.requestBodyOpen = true;
         this.responseBodyOpen = true;
         Unit var21 = Unit.INSTANCE;
      }

      if (this.canceled) {
         throw new IOException("Canceled");
      } else {
         return var18;
      }
   }

   public final void acquireConnectionNoEvents(@NotNull RealConnection connection) {
      Intrinsics.checkNotNullParameter(connection, "connection");
      Lockable $this$assertLockHeld$iv = connection;
      int $i$f$assertLockHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && !Thread.holdsLock($this$assertLockHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertLockHeld$iv);
      } else if (this.connection != null) {
         throw new IllegalStateException("Check failed.");
      } else {
         this.connection = connection;
         connection.getCalls().add(new CallReference(this, this.callStackTrace));
      }
   }

   @Nullable
   public final IOException messageDone$okhttp(@NotNull Exchange exchange, boolean requestDone, boolean responseDone, boolean socketSourceDone, boolean socketSinkDone, @Nullable IOException e) {
      Intrinsics.checkNotNullParameter(exchange, "exchange");
      if (!Intrinsics.areEqual((Object)exchange, (Object)this.exchange)) {
         return e;
      } else {
         boolean allStreamsDone = false;
         boolean callDone = false;
         Lockable $this$withLock$iv = this;
         int $i$f$withLock = 0;
         synchronized($this$withLock$iv) {
            int var12 = 0;
            if (requestDone && this.requestBodyOpen || responseDone && this.responseBodyOpen || socketSinkDone && this.socketSinkOpen || socketSourceDone && this.socketSourceOpen) {
               if (requestDone) {
                  this.requestBodyOpen = false;
               }

               if (responseDone) {
                  this.responseBodyOpen = false;
               }

               if (socketSinkDone) {
                  this.socketSinkOpen = false;
               }

               if (socketSourceDone) {
                  this.socketSourceOpen = false;
               }

               allStreamsDone = !this.requestBodyOpen && !this.responseBodyOpen && !this.socketSinkOpen && !this.socketSourceOpen;
               callDone = allStreamsDone && !this.expectMoreExchanges;
            }

            Unit var13 = Unit.INSTANCE;
         }

         if (allStreamsDone) {
            this.exchange = null;
            RealConnection var10000 = this.connection;
            if (var10000 != null) {
               var10000.incrementSuccessCount$okhttp();
            }
         }

         return callDone ? this.callDone(e) : e;
      }
   }

   // $FF: synthetic method
   public static IOException messageDone$okhttp$default(RealCall var0, Exchange var1, boolean var2, boolean var3, boolean var4, boolean var5, IOException var6, int var7, Object var8) {
      if ((var7 & 2) != 0) {
         var2 = false;
      }

      if ((var7 & 4) != 0) {
         var3 = false;
      }

      if ((var7 & 8) != 0) {
         var4 = false;
      }

      if ((var7 & 16) != 0) {
         var5 = false;
      }

      return var0.messageDone$okhttp(var1, var2, var3, var4, var5, var6);
   }

   @Nullable
   public final IOException noMoreExchanges$okhttp(@Nullable IOException e) {
      boolean callDone = false;
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv) {
         int var6 = 0;
         if (this.expectMoreExchanges) {
            this.expectMoreExchanges = false;
            callDone = !this.requestBodyOpen && !this.responseBodyOpen && !this.socketSinkOpen && !this.socketSourceOpen;
         }

         Unit var7 = Unit.INSTANCE;
      }

      return callDone ? this.callDone(e) : e;
   }

   private final IOException callDone(IOException param1) {
      // $FF: Couldn't be decompiled
   }

   @Nullable
   public final Socket releaseConnectionNoEvents$okhttp() {
      RealConnection var10000 = this.connection;
      Intrinsics.checkNotNull(var10000);
      RealConnection connection = var10000;
      Lockable $this$assertLockHeld$iv = connection;
      int $i$f$assertLockHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && !Thread.holdsLock($this$assertLockHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertLockHeld$iv);
      } else {
         List calls = connection.getCalls();
         int $i$f$indexOfFirst = 0;
         int index$iv = 0;
         Iterator var7 = calls.iterator();

         while(true) {
            if (!var7.hasNext()) {
               var13 = -1;
               break;
            }

            Object item$iv = var7.next();
            Reference it = (Reference)item$iv;
            int var10 = 0;
            if (Intrinsics.areEqual((Object)it.get(), (Object)this)) {
               var13 = index$iv;
               break;
            }

            ++index$iv;
         }

         $i$f$assertLockHeld = var13;
         if ($i$f$assertLockHeld == -1) {
            throw new IllegalStateException("Check failed.");
         } else {
            calls.remove($i$f$assertLockHeld);
            this.connection = null;
            if (calls.isEmpty()) {
               connection.setIdleAtNs(System.nanoTime());
               if (this.connectionPool.connectionBecameIdle(connection)) {
                  return connection.socket();
               }
            }

            return null;
         }
      }
   }

   private final IOException timeoutExit(IOException cause) {
      if (this.timeoutEarlyExit) {
         return cause;
      } else if (!this.timeout.exit()) {
         return cause;
      } else {
         InterruptedIOException e = new InterruptedIOException("timeout");
         if (cause != null) {
            e.initCause((Throwable)cause);
         }

         return (IOException)e;
      }
   }

   public final void timeoutEarlyExit() {
      if (this.timeoutEarlyExit) {
         throw new IllegalStateException("Check failed.");
      } else {
         this.timeoutEarlyExit = true;
         this.timeout.exit();
      }
   }

   public final void upgradeToSocket() {
      this.timeoutEarlyExit();
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv){}

      try {
         int var4 = 0;
         if (this.exchange == null) {
            throw new IllegalStateException("Check failed.");
         }

         if (this.socketSinkOpen || this.socketSourceOpen) {
            throw new IllegalStateException("Check failed.");
         }

         if (this.requestBodyOpen) {
            throw new IllegalStateException("Check failed.");
         }

         if (!this.responseBodyOpen) {
            throw new IllegalStateException("Check failed.");
         }

         this.responseBodyOpen = false;
         this.socketSinkOpen = true;
         this.socketSourceOpen = true;
         Unit var8 = Unit.INSTANCE;
      } finally {
         ;
      }

   }

   public final void exitNetworkInterceptorExchange$okhttp(boolean closeExchange) {
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv){}

      try {
         int var5 = 0;
         if (!this.expectMoreExchanges) {
            int var6 = 0;
            String var10 = "released";
            throw new IllegalStateException(var10.toString());
         }

         Unit var9 = Unit.INSTANCE;
      } finally {
         ;
      }

      if (closeExchange) {
         Exchange var10000 = this.exchange;
         if (var10000 != null) {
            var10000.detachWithViolence();
         }
      }

      this.interceptorScopedExchange = null;
   }

   public final boolean retryAfterFailure() {
      Exchange var10000 = this.exchange;
      boolean var3;
      if (var10000 != null ? var10000.getHasFailure$okhttp() : false) {
         ExchangeFinder var1 = this.exchangeFinder;
         Intrinsics.checkNotNull(var1);
         RoutePlanner var2 = var1.getRoutePlanner();
         Exchange var10001 = this.exchange;
         if (var2.hasNext(var10001 != null ? var10001.getConnection$okhttp() : null)) {
            var3 = true;
            return var3;
         }
      }

      var3 = false;
      return var3;
   }

   private final String toLoggableString() {
      return (this.isCanceled() ? "canceled " : "") + (this.forWebSocket ? "web socket" : "call") + " to " + this.redactedUrl$okhttp();
   }

   @NotNull
   public final String redactedUrl$okhttp() {
      return this.originalRequest.url().redact();
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0012\u0010\u000b\u001a\u00020\f2\n\u0010\r\u001a\u00060\u0000R\u00020\u000eJ\u000e\u0010\u001a\u001a\u00020\f2\u0006\u0010\u001b\u001a\u00020\u001cJ\u0019\u0010\u001d\u001a\u00020\f2\n\b\u0002\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0000¢\u0006\u0002\b J\b\u0010!\u001a\u00020\fH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\b\u001a\u00020\u00072\u0006\u0010\u0006\u001a\u00020\u0007@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u000f\u001a\u00020\u00108F¢\u0006\u0006\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0013\u001a\u00020\u00148F¢\u0006\u0006\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0017\u001a\u00020\u000e8F¢\u0006\u0006\u001a\u0004\b\u0018\u0010\u0019¨\u0006\""},
      d2 = {"Lokhttp3/internal/connection/RealCall$AsyncCall;", "Ljava/lang/Runnable;", "responseCallback", "Lokhttp3/Callback;", "<init>", "(Lokhttp3/internal/connection/RealCall;Lokhttp3/Callback;)V", "value", "Ljava/util/concurrent/atomic/AtomicInteger;", "callsPerHost", "getCallsPerHost", "()Ljava/util/concurrent/atomic/AtomicInteger;", "reuseCallsPerHostFrom", "", "other", "Lokhttp3/internal/connection/RealCall;", "host", "", "getHost", "()Ljava/lang/String;", "request", "Lokhttp3/Request;", "getRequest", "()Lokhttp3/Request;", "call", "getCall", "()Lokhttp3/internal/connection/RealCall;", "executeOn", "executorService", "Ljava/util/concurrent/ExecutorService;", "failRejected", "e", "Ljava/util/concurrent/RejectedExecutionException;", "failRejected$okhttp", "run", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nRealCall.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealCall.kt\nokhttp3/internal/connection/RealCall$AsyncCall\n+ 2 -UtilJvm.kt\nokhttp3/internal/_UtilJvmKt\n*L\n1#1,612:1\n227#2,9:613\n*S KotlinDebug\n*F\n+ 1 RealCall.kt\nokhttp3/internal/connection/RealCall$AsyncCall\n*L\n570#1:613,9\n*E\n"})
   public final class AsyncCall implements Runnable {
      @NotNull
      private final Callback responseCallback;
      @NotNull
      private volatile AtomicInteger callsPerHost;

      public AsyncCall(@NotNull Callback responseCallback) {
         Intrinsics.checkNotNullParameter(responseCallback, "responseCallback");
         super();
         this.responseCallback = responseCallback;
         this.callsPerHost = new AtomicInteger(0);
      }

      @NotNull
      public final AtomicInteger getCallsPerHost() {
         return this.callsPerHost;
      }

      public final void reuseCallsPerHostFrom(@NotNull AsyncCall other) {
         Intrinsics.checkNotNullParameter(other, "other");
         this.callsPerHost = other.callsPerHost;
      }

      @NotNull
      public final String getHost() {
         return RealCall.this.getOriginalRequest().url().host();
      }

      @NotNull
      public final Request getRequest() {
         return RealCall.this.getOriginalRequest();
      }

      @NotNull
      public final RealCall getCall() {
         return RealCall.this;
      }

      public final void executeOn(@NotNull ExecutorService executorService) {
         Intrinsics.checkNotNullParameter(executorService, "executorService");
         _UtilJvmKt.assertLockNotHeld(RealCall.this.getClient().dispatcher());
         boolean success = false;
         boolean var6 = false;

         try {
            var6 = true;
            executorService.execute(this);
            success = true;
            var6 = false;
            return;
         } catch (RejectedExecutionException e) {
            this.failRejected$okhttp(e);
            var6 = false;
         } finally {
            if (var6) {
               RealCall.this.getClient().dispatcher().finished$okhttp(this);
            }
         }

         RealCall.this.getClient().dispatcher().finished$okhttp(this);
      }

      public final void failRejected$okhttp(@Nullable RejectedExecutionException e) {
         InterruptedIOException ioException = new InterruptedIOException("executor rejected");
         ioException.initCause((Throwable)e);
         RealCall.this.noMoreExchanges$okhttp((IOException)ioException);
         this.responseCallback.onFailure((Call)RealCall.this, (IOException)ioException);
      }

      // $FF: synthetic method
      public static void failRejected$okhttp$default(AsyncCall var0, RejectedExecutionException var1, int var2, Object var3) {
         if ((var2 & 1) != 0) {
            var1 = null;
         }

         var0.failRejected$okhttp(var1);
      }

      public void run() {
         String name$iv = "OkHttp " + RealCall.this.redactedUrl$okhttp();
         RealCall var2 = RealCall.this;
         int $i$f$threadName = 0;
         Thread currentThread$iv = Thread.currentThread();
         String oldName$iv = currentThread$iv.getName();
         currentThread$iv.setName(name$iv);

         try {
            int var6 = 0;
            boolean signalledCallback = false;
            var2.timeout.enter();

            try {
               Response response = var2.getResponseWithInterceptorChain$okhttp();
               signalledCallback = true;
               this.responseCallback.onResponse(var2, response);
            } catch (IOException e) {
               if (signalledCallback) {
                  Platform.Companion.get().log("Callback failure for " + var2.toLoggableString(), 4, (Throwable)e);
               } else {
                  this.responseCallback.onFailure(var2, e);
               }
            } catch (Throwable var19) {
               var2.cancel();
               if (!signalledCallback) {
                  IOException canceledException = new IOException("canceled due to " + var19);
                  canceledException.initCause(var19);
                  this.responseCallback.onFailure(var2, canceledException);
               }

               if (!(var19 instanceof InterruptedException)) {
                  throw var19;
               }

               Thread.currentThread().interrupt();
            } finally {
               var2.getClient().dispatcher().finished$okhttp(this);
            }
         } finally {
            currentThread$iv.setName(oldName$iv);
         }

      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0005\b\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0019\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t¨\u0006\n"},
      d2 = {"Lokhttp3/internal/connection/RealCall$CallReference;", "Ljava/lang/ref/WeakReference;", "Lokhttp3/internal/connection/RealCall;", "referent", "callStackTrace", "", "<init>", "(Lokhttp3/internal/connection/RealCall;Ljava/lang/Object;)V", "getCallStackTrace", "()Ljava/lang/Object;", "okhttp"}
   )
   public static final class CallReference extends WeakReference<RealCall> {
      @Nullable
      private final Object callStackTrace;

      public CallReference(@NotNull RealCall referent, @Nullable Object callStackTrace) {
         Intrinsics.checkNotNullParameter(referent, "referent");
         super(referent);
         this.callStackTrace = callStackTrace;
      }

      @Nullable
      public final Object getCallStackTrace() {
         return this.callStackTrace;
      }
   }
}
