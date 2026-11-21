package okhttp3.internal.connection;

import java.io.IOException;
import java.lang.ref.Reference;
import java.net.Socket;
import java.net.SocketException;
import java.net.Proxy.Type;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLPeerUnverifiedException;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.Address;
import okhttp3.CertificatePinner;
import okhttp3.Connection;
import okhttp3.Handshake;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Route;
import okhttp3.internal._UtilJvmKt;
import okhttp3.internal.concurrent.Lockable;
import okhttp3.internal.concurrent.TaskRunner;
import okhttp3.internal.http.ExchangeCodec;
import okhttp3.internal.http.RealInterceptorChain;
import okhttp3.internal.http1.Http1ExchangeCodec;
import okhttp3.internal.http2.ConnectionShutdownException;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.FlowControlListener;
import okhttp3.internal.http2.Http2Connection;
import okhttp3.internal.http2.Http2ExchangeCodec;
import okhttp3.internal.http2.Http2Stream;
import okhttp3.internal.http2.Settings;
import okhttp3.internal.http2.StreamResetException;
import okhttp3.internal.tls.OkHostnameVerifier;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000È\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0010\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018\u0000 r2\u00020\u00012\u00020\u00022\u00020\u00032\u00020\u0004:\u0001rB[\b\u0000\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\f\u0012\b\u0010\u000e\u001a\u0004\u0018\u00010\u000f\u0012\u0006\u0010\u0010\u001a\u00020\u0011\u0012\u0006\u0010\u0012\u001a\u00020\u0013\u0012\u0006\u0010\u0014\u001a\u00020\u0015\u0012\u0006\u0010\u0016\u001a\u00020\u0017¢\u0006\u0004\b\u0018\u0010\u0019J\b\u0010$\u001a\u00020CH\u0016J\r\u0010*\u001a\u00020CH\u0000¢\u0006\u0002\bDJ\r\u0010E\u001a\u00020CH\u0000¢\u0006\u0002\bFJ\u0006\u0010G\u001a\u00020CJ\b\u0010H\u001a\u00020CH\u0002J%\u0010I\u001a\u00020%2\u0006\u0010J\u001a\u00020K2\u000e\u0010L\u001a\n\u0012\u0004\u0012\u00020\n\u0018\u00010MH\u0000¢\u0006\u0002\bNJ\u0016\u0010O\u001a\u00020%2\f\u0010P\u001a\b\u0012\u0004\u0012\u00020\n0MH\u0002J\u0010\u0010Q\u001a\u00020%2\u0006\u0010R\u001a\u00020SH\u0002J\u0018\u0010T\u001a\u00020%2\u0006\u0010R\u001a\u00020S2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J\u001d\u0010U\u001a\u00020V2\u0006\u0010W\u001a\u00020X2\u0006\u0010Y\u001a\u00020ZH\u0000¢\u0006\u0002\b[J\r\u0010\\\u001a\u00020CH\u0000¢\u0006\u0002\b]J\b\u0010\t\u001a\u00020\nH\u0016J\b\u0010^\u001a\u00020CH\u0016J\b\u0010\u0012\u001a\u00020\fH\u0016J\u000e\u0010_\u001a\u00020%2\u0006\u0010`\u001a\u00020%J\u0010\u0010a\u001a\u00020C2\u0006\u0010b\u001a\u00020cH\u0016J\u0018\u0010d\u001a\u00020C2\u0006\u0010e\u001a\u00020#2\u0006\u0010f\u001a\u00020gH\u0016J\n\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0016J%\u0010h\u001a\u00020C2\u0006\u0010W\u001a\u00020X2\u0006\u0010i\u001a\u00020\n2\u0006\u0010j\u001a\u00020kH\u0000¢\u0006\u0002\blJ\u001a\u0010m\u001a\u00020C2\u0006\u0010n\u001a\u0002082\b\u0010o\u001a\u0004\u0018\u00010kH\u0016J\b\u0010\u0010\u001a\u00020\u0011H\u0016J\b\u0010p\u001a\u00020qH\u0016R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0014\u0010\t\u001a\u00020\nX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0016\u001a\u00020\u0017X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0010\u0010\"\u001a\u0004\u0018\u00010#X\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010$\u001a\u00020%X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b&\u0010'\"\u0004\b(\u0010)R\u000e\u0010*\u001a\u00020%X\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010+\u001a\u00020\u0015X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b,\u0010-\"\u0004\b.\u0010/R\u000e\u00100\u001a\u00020\u0015X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u00101\u001a\u00020\u0015X\u0082\u000e¢\u0006\u0002\n\u0000R\u001e\u00103\u001a\u00020\u00152\u0006\u00102\u001a\u00020\u0015@BX\u0080\u000e¢\u0006\b\n\u0000\u001a\u0004\b4\u0010-R\u001d\u00105\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002080706¢\u0006\b\n\u0000\u001a\u0004\b9\u0010:R\u001a\u0010;\u001a\u00020<X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b=\u0010>\"\u0004\b?\u0010@R\u0014\u0010A\u001a\u00020%8@X\u0080\u0004¢\u0006\u0006\u001a\u0004\bB\u0010'¨\u0006s"},
   d2 = {"Lokhttp3/internal/connection/RealConnection;", "Lokhttp3/internal/http2/Http2Connection$Listener;", "Lokhttp3/Connection;", "Lokhttp3/internal/http/ExchangeCodec$Carrier;", "Lokhttp3/internal/concurrent/Lockable;", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "connectionPool", "Lokhttp3/internal/connection/RealConnectionPool;", "route", "Lokhttp3/Route;", "rawSocket", "Ljava/net/Socket;", "javaNetSocket", "handshake", "Lokhttp3/Handshake;", "protocol", "Lokhttp3/Protocol;", "socket", "Lokhttp3/internal/connection/BufferedSocket;", "pingIntervalMillis", "", "connectionListener", "Lokhttp3/internal/connection/ConnectionListener;", "<init>", "(Lokhttp3/internal/concurrent/TaskRunner;Lokhttp3/internal/connection/RealConnectionPool;Lokhttp3/Route;Ljava/net/Socket;Ljava/net/Socket;Lokhttp3/Handshake;Lokhttp3/Protocol;Lokhttp3/internal/connection/BufferedSocket;ILokhttp3/internal/connection/ConnectionListener;)V", "getTaskRunner", "()Lokhttp3/internal/concurrent/TaskRunner;", "getConnectionPool", "()Lokhttp3/internal/connection/RealConnectionPool;", "getRoute", "()Lokhttp3/Route;", "getConnectionListener$okhttp", "()Lokhttp3/internal/connection/ConnectionListener;", "http2Connection", "Lokhttp3/internal/http2/Http2Connection;", "noNewExchanges", "", "getNoNewExchanges", "()Z", "setNoNewExchanges", "(Z)V", "noCoalescedConnections", "routeFailureCount", "getRouteFailureCount$okhttp", "()I", "setRouteFailureCount$okhttp", "(I)V", "successCount", "refusedStreamCount", "value", "allocationLimit", "getAllocationLimit$okhttp", "calls", "", "Ljava/lang/ref/Reference;", "Lokhttp3/internal/connection/RealCall;", "getCalls", "()Ljava/util/List;", "idleAtNs", "", "getIdleAtNs", "()J", "setIdleAtNs", "(J)V", "isMultiplexed", "isMultiplexed$okhttp", "", "noCoalescedConnections$okhttp", "incrementSuccessCount", "incrementSuccessCount$okhttp", "start", "startHttp2", "isEligible", "address", "Lokhttp3/Address;", "routes", "", "isEligible$okhttp", "routeMatchesAny", "candidates", "supportsUrl", "url", "Lokhttp3/HttpUrl;", "certificateSupportHost", "newCodec", "Lokhttp3/internal/http/ExchangeCodec;", "client", "Lokhttp3/OkHttpClient;", "chain", "Lokhttp3/internal/http/RealInterceptorChain;", "newCodec$okhttp", "useAsSocket", "useAsSocket$okhttp", "cancel", "isHealthy", "doExtensiveChecks", "onStream", "stream", "Lokhttp3/internal/http2/Http2Stream;", "onSettings", "connection", "settings", "Lokhttp3/internal/http2/Settings;", "connectFailed", "failedRoute", "failure", "Ljava/io/IOException;", "connectFailed$okhttp", "trackFailure", "call", "e", "toString", "", "Companion", "okhttp"}
)
@SourceDebugExtension({"SMAP\nRealConnection.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealConnection.kt\nokhttp3/internal/connection/RealConnection\n+ 2 Lockable.kt\nokhttp3/internal/concurrent/LockableKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,463:1\n63#2:464\n63#2:465\n63#2:466\n55#2,4:467\n55#2,4:474\n49#2,4:478\n63#2:482\n63#2:483\n63#2:484\n1761#3,3:471\n*S KotlinDebug\n*F\n+ 1 RealConnection.kt\nokhttp3/internal/connection/RealConnection\n*L\n133#1:464\n141#1:465\n147#1:466\n185#1:467,4\n241#1:474,4\n301#1:478,4\n318#1:482\n337#1:483\n372#1:484\n234#1:471,3\n*E\n"})
public final class RealConnection extends Http2Connection.Listener implements Connection, ExchangeCodec.Carrier, Lockable {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final TaskRunner taskRunner;
   @NotNull
   private final RealConnectionPool connectionPool;
   @NotNull
   private final Route route;
   @NotNull
   private final Socket rawSocket;
   @NotNull
   private final Socket javaNetSocket;
   @Nullable
   private final Handshake handshake;
   @NotNull
   private final Protocol protocol;
   @NotNull
   private final BufferedSocket socket;
   private final int pingIntervalMillis;
   @NotNull
   private final ConnectionListener connectionListener;
   @Nullable
   private Http2Connection http2Connection;
   private boolean noNewExchanges;
   private boolean noCoalescedConnections;
   private int routeFailureCount;
   private int successCount;
   private int refusedStreamCount;
   private int allocationLimit;
   @NotNull
   private final List<Reference<RealCall>> calls;
   private long idleAtNs;
   public static final long IDLE_CONNECTION_HEALTHY_NS = 10000000000L;

   public RealConnection(@NotNull TaskRunner taskRunner, @NotNull RealConnectionPool connectionPool, @NotNull Route route, @NotNull Socket rawSocket, @NotNull Socket javaNetSocket, @Nullable Handshake handshake, @NotNull Protocol protocol, @NotNull BufferedSocket socket, int pingIntervalMillis, @NotNull ConnectionListener connectionListener) {
      Intrinsics.checkNotNullParameter(taskRunner, "taskRunner");
      Intrinsics.checkNotNullParameter(connectionPool, "connectionPool");
      Intrinsics.checkNotNullParameter(route, "route");
      Intrinsics.checkNotNullParameter(rawSocket, "rawSocket");
      Intrinsics.checkNotNullParameter(javaNetSocket, "javaNetSocket");
      Intrinsics.checkNotNullParameter(protocol, "protocol");
      Intrinsics.checkNotNullParameter(socket, "socket");
      Intrinsics.checkNotNullParameter(connectionListener, "connectionListener");
      super();
      this.taskRunner = taskRunner;
      this.connectionPool = connectionPool;
      this.route = route;
      this.rawSocket = rawSocket;
      this.javaNetSocket = javaNetSocket;
      this.handshake = handshake;
      this.protocol = protocol;
      this.socket = socket;
      this.pingIntervalMillis = pingIntervalMillis;
      this.connectionListener = connectionListener;
      this.allocationLimit = 1;
      this.calls = (List)(new ArrayList());
      this.idleAtNs = Long.MAX_VALUE;
   }

   @NotNull
   public final TaskRunner getTaskRunner() {
      return this.taskRunner;
   }

   @NotNull
   public final RealConnectionPool getConnectionPool() {
      return this.connectionPool;
   }

   @NotNull
   public Route getRoute() {
      return this.route;
   }

   @NotNull
   public final ConnectionListener getConnectionListener$okhttp() {
      return this.connectionListener;
   }

   public final boolean getNoNewExchanges() {
      return this.noNewExchanges;
   }

   public final void setNoNewExchanges(boolean var1) {
      this.noNewExchanges = var1;
   }

   public final int getRouteFailureCount$okhttp() {
      return this.routeFailureCount;
   }

   public final void setRouteFailureCount$okhttp(int var1) {
      this.routeFailureCount = var1;
   }

   public final int getAllocationLimit$okhttp() {
      return this.allocationLimit;
   }

   @NotNull
   public final List<Reference<RealCall>> getCalls() {
      return this.calls;
   }

   public final long getIdleAtNs() {
      return this.idleAtNs;
   }

   public final void setIdleAtNs(long var1) {
      this.idleAtNs = var1;
   }

   public final boolean isMultiplexed$okhttp() {
      return this.http2Connection != null;
   }

   public void noNewExchanges() {
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv) {
         int var4 = 0;
         this.noNewExchanges = true;
         Unit var7 = Unit.INSTANCE;
      }

      this.connectionListener.noNewExchanges(this);
   }

   public final void noCoalescedConnections$okhttp() {
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv) {
         int var4 = 0;
         this.noCoalescedConnections = true;
         Unit var7 = Unit.INSTANCE;
      }

   }

   public final void incrementSuccessCount$okhttp() {
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv) {
         int var4 = 0;
         int var5 = this.successCount++;
      }

   }

   public final void start() throws IOException {
      this.idleAtNs = System.nanoTime();
      if (this.protocol == Protocol.HTTP_2 || this.protocol == Protocol.H2_PRIOR_KNOWLEDGE) {
         this.startHttp2();
      }

   }

   private final void startHttp2() throws IOException {
      this.javaNetSocket.setSoTimeout(0);
      ConnectionListener var3 = this.connectionListener;
      FlowControlListener var10000 = var3 instanceof FlowControlListener ? (FlowControlListener)var3 : null;
      if ((var3 instanceof FlowControlListener ? (FlowControlListener)var3 : null) == null) {
         var10000 = FlowControlListener.None.INSTANCE;
      }

      FlowControlListener flowControlListener = var10000;
      Http2Connection http2Connection = (new Http2Connection.Builder(true, this.taskRunner)).socket(this.socket, this.getRoute().address().url().host()).listener(this).pingIntervalMillis(this.pingIntervalMillis).flowControlListener(flowControlListener).build();
      this.http2Connection = http2Connection;
      this.allocationLimit = Http2Connection.Companion.getDEFAULT_SETTINGS().getMaxConcurrentStreams();
      Http2Connection.start$default(http2Connection, false, 1, (Object)null);
   }

   public final boolean isEligible$okhttp(@NotNull Address address, @Nullable List<Route> routes) {
      Intrinsics.checkNotNullParameter(address, "address");
      Lockable $this$assertLockHeld$iv = this;
      int $i$f$assertLockHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && !Thread.holdsLock($this$assertLockHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertLockHeld$iv);
      } else if (this.calls.size() < this.allocationLimit && !this.noNewExchanges) {
         if (!this.getRoute().address().equalsNonHost$okhttp(address)) {
            return false;
         } else if (Intrinsics.areEqual((Object)address.url().host(), (Object)this.route().address().url().host())) {
            return true;
         } else if (this.http2Connection == null) {
            return false;
         } else if (routes != null && this.routeMatchesAny(routes)) {
            if (address.hostnameVerifier() != OkHostnameVerifier.INSTANCE) {
               return false;
            } else if (!this.supportsUrl(address.url())) {
               return false;
            } else {
               try {
                  CertificatePinner var10000 = address.certificatePinner();
                  Intrinsics.checkNotNull(var10000);
                  String var10001 = address.url().host();
                  Handshake var10002 = this.handshake();
                  Intrinsics.checkNotNull(var10002);
                  var10000.check(var10001, var10002.peerCertificates());
                  return true;
               } catch (SSLPeerUnverifiedException var5) {
                  return false;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private final boolean routeMatchesAny(List<Route> candidates) {
      Iterable $this$any$iv = (Iterable)candidates;
      int $i$f$any = 0;
      boolean var10000;
      if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
         var10000 = false;
      } else {
         Iterator var4 = $this$any$iv.iterator();

         while(true) {
            if (!var4.hasNext()) {
               var10000 = false;
               break;
            }

            Object element$iv = var4.next();
            Route it = (Route)element$iv;
            int var7 = 0;
            if (it.proxy().type() == Type.DIRECT && this.getRoute().proxy().type() == Type.DIRECT && Intrinsics.areEqual((Object)this.getRoute().socketAddress(), (Object)it.socketAddress())) {
               var10000 = true;
               break;
            }
         }
      }

      return var10000;
   }

   private final boolean supportsUrl(HttpUrl url) {
      Lockable $this$assertLockHeld$iv = this;
      int $i$f$assertLockHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && !Thread.holdsLock($this$assertLockHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertLockHeld$iv);
      } else {
         HttpUrl routeUrl = this.getRoute().address().url();
         if (url.port() != routeUrl.port()) {
            return false;
         } else if (Intrinsics.areEqual((Object)url.host(), (Object)routeUrl.host())) {
            return true;
         } else {
            return !this.noCoalescedConnections && this.handshake != null && this.certificateSupportHost(url, this.handshake);
         }
      }
   }

   private final boolean certificateSupportHost(HttpUrl url, Handshake handshake) {
      List peerCertificates = handshake.peerCertificates();
      boolean var4;
      if (!((Collection)peerCertificates).isEmpty()) {
         OkHostnameVerifier var10000 = OkHostnameVerifier.INSTANCE;
         String var10001 = url.host();
         Object var10002 = peerCertificates.get(0);
         Intrinsics.checkNotNull(var10002, "null cannot be cast to non-null type java.security.cert.X509Certificate");
         if (var10000.verify(var10001, (X509Certificate)var10002)) {
            var4 = true;
            return var4;
         }
      }

      var4 = false;
      return var4;
   }

   @NotNull
   public final ExchangeCodec newCodec$okhttp(@NotNull OkHttpClient client, @NotNull RealInterceptorChain chain) throws SocketException {
      Intrinsics.checkNotNullParameter(client, "client");
      Intrinsics.checkNotNullParameter(chain, "chain");
      BufferedSocket okHttpSocket = this.socket;
      Http2Connection http2Connection = this.http2Connection;
      ExchangeCodec var10000;
      if (http2Connection != null) {
         var10000 = new Http2ExchangeCodec(client, this, chain, http2Connection);
      } else {
         this.javaNetSocket.setSoTimeout(chain.readTimeoutMillis());
         okHttpSocket.getSource().timeout().timeout((long)chain.getReadTimeoutMillis$okhttp(), TimeUnit.MILLISECONDS);
         okHttpSocket.getSink().timeout().timeout((long)chain.getWriteTimeoutMillis$okhttp(), TimeUnit.MILLISECONDS);
         var10000 = new Http1ExchangeCodec(client, this, okHttpSocket);
      }

      return var10000;
   }

   public final void useAsSocket$okhttp() {
      this.javaNetSocket.setSoTimeout(0);
      this.noNewExchanges();
   }

   @NotNull
   public Route route() {
      return this.getRoute();
   }

   public void cancel() {
      _UtilJvmKt.closeQuietly(this.rawSocket);
   }

   @NotNull
   public Socket socket() {
      return this.javaNetSocket;
   }

   public final boolean isHealthy(boolean param1) {
      // $FF: Couldn't be decompiled
   }

   public void onStream(@NotNull Http2Stream stream) throws IOException {
      Intrinsics.checkNotNullParameter(stream, "stream");
      stream.close(ErrorCode.REFUSED_STREAM, (IOException)null);
   }

   public void onSettings(@NotNull Http2Connection connection, @NotNull Settings settings) {
      Intrinsics.checkNotNullParameter(connection, "connection");
      Intrinsics.checkNotNullParameter(settings, "settings");
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv) {
         int var6 = 0;
         this.allocationLimit = settings.getMaxConcurrentStreams();
         Unit var7 = Unit.INSTANCE;
      }

   }

   @Nullable
   public Handshake handshake() {
      return this.handshake;
   }

   public final void connectFailed$okhttp(@NotNull OkHttpClient client, @NotNull Route failedRoute, @NotNull IOException failure) {
      Intrinsics.checkNotNullParameter(client, "client");
      Intrinsics.checkNotNullParameter(failedRoute, "failedRoute");
      Intrinsics.checkNotNullParameter(failure, "failure");
      if (failedRoute.proxy().type() != Type.DIRECT) {
         Address address = failedRoute.address();
         address.proxySelector().connectFailed(address.url().uri(), failedRoute.proxy().address(), failure);
      }

      client.getRouteDatabase$okhttp().failed(failedRoute);
   }

   public void trackFailure(@NotNull RealCall call, @Nullable IOException e) {
      Intrinsics.checkNotNullParameter(call, "call");
      boolean noNewExchangesEvent = false;
      Lockable $this$withLock$iv = this;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv) {
         int var7 = 0;
         if (e instanceof StreamResetException) {
            if (((StreamResetException)e).errorCode == ErrorCode.REFUSED_STREAM) {
               int var13 = this.refusedStreamCount++;
               if (this.refusedStreamCount > 1) {
                  noNewExchangesEvent = !this.noNewExchanges;
                  this.noNewExchanges = true;
                  var13 = this.routeFailureCount++;
               }
            } else if (((StreamResetException)e).errorCode != ErrorCode.CANCEL || !call.isCanceled()) {
               noNewExchangesEvent = !this.noNewExchanges;
               this.noNewExchanges = true;
               int var12 = this.routeFailureCount++;
            }
         } else if (!this.isMultiplexed$okhttp() || e instanceof ConnectionShutdownException) {
            noNewExchangesEvent = !this.noNewExchanges;
            this.noNewExchanges = true;
            if (this.successCount == 0) {
               if (e != null) {
                  this.connectFailed$okhttp(call.getClient(), this.getRoute(), e);
               }

               int var8 = this.routeFailureCount++;
            }
         }

         Unit var9 = Unit.INSTANCE;
      }

      if (noNewExchangesEvent) {
         this.connectionListener.noNewExchanges(this);
      }

   }

   @NotNull
   public Protocol protocol() {
      return this.protocol;
   }

   @NotNull
   public String toString() {
      StringBuilder var10000 = (new StringBuilder()).append("Connection{").append(this.getRoute().address().url().host()).append(':').append(this.getRoute().address().url().port()).append(", proxy=").append(this.getRoute().proxy()).append(" hostAddress=").append(this.getRoute().socketAddress()).append(" cipherSuite=");
      Handshake var10001 = this.handshake;
      Object var1;
      if (var10001 != null) {
         var1 = var10001.cipherSuite();
         if (var1 != null) {
            return var10000.append(var1).append(" protocol=").append(this.protocol).append('}').toString();
         }
      }

      var1 = "none";
      return var10000.append(var1).append(" protocol=").append(this.protocol).append('}').toString();
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J.\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0005R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0011"},
      d2 = {"Lokhttp3/internal/connection/RealConnection$Companion;", "", "<init>", "()V", "IDLE_CONNECTION_HEALTHY_NS", "", "newTestConnection", "Lokhttp3/internal/connection/RealConnection;", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "connectionPool", "Lokhttp3/internal/connection/RealConnectionPool;", "route", "Lokhttp3/Route;", "socket", "Ljava/net/Socket;", "idleAtNs", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final RealConnection newTestConnection(@NotNull TaskRunner taskRunner, @NotNull RealConnectionPool connectionPool, @NotNull Route route, @NotNull Socket socket, long idleAtNs) {
         Intrinsics.checkNotNullParameter(taskRunner, "taskRunner");
         Intrinsics.checkNotNullParameter(connectionPool, "connectionPool");
         Intrinsics.checkNotNullParameter(route, "route");
         Intrinsics.checkNotNullParameter(socket, "socket");
         <undefinedtype> bufferedSocket = new BufferedSocket() {
            private final Buffer sink = new Buffer();
            private final Buffer source = new Buffer();

            public Buffer getSink() {
               return this.sink;
            }

            public Buffer getSource() {
               return this.source;
            }

            public void cancel() {
            }
         };
         RealConnection result = new RealConnection(taskRunner, connectionPool, route, new Socket(), socket, (Handshake)null, Protocol.HTTP_2, bufferedSocket, 0, ConnectionListener.Companion.getNONE());
         result.setIdleAtNs(idleAtNs);
         return result;
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
