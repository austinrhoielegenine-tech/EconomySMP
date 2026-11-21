package okhttp3.internal.connection;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.Socket;
import java.net.UnknownServiceException;
import java.net.Proxy.Type;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.Address;
import okhttp3.CertificatePinner;
import okhttp3.ConnectionSpec;
import okhttp3.Handshake;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal._UtilJvmKt;
import okhttp3.internal.concurrent.Lockable;
import okhttp3.internal.concurrent.TaskRunner;
import okhttp3.internal.http.ExchangeCodec;
import okhttp3.internal.http1.Http1ExchangeCodec;
import okhttp3.internal.platform.Platform;
import okhttp3.internal.tls.CertificateChainCleaner;
import okhttp3.internal.tls.OkHostnameVerifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0092\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 R2\u00020\u00012\u00020\u0002:\u0001RB\u0093\u0001\b\u0000\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\b\u0012\u0006\u0010\n\u001a\u00020\b\u0012\u0006\u0010\u000b\u001a\u00020\b\u0012\u0006\u0010\f\u001a\u00020\b\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u000f\u001a\u00020\u0010\u0012\u0006\u0010\u0011\u001a\u00020\u0012\u0012\u0006\u0010\u0013\u001a\u00020\u0014\u0012\u000e\u0010\u0015\u001a\n\u0012\u0004\u0012\u00020\u0014\u0018\u00010\u0016\u0012\u0006\u0010\u0017\u001a\u00020\b\u0012\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019\u0012\u0006\u0010\u001a\u001a\u00020\b\u0012\u0006\u0010\u001b\u001a\u00020\u000e¢\u0006\u0004\b\u001c\u0010\u001dJ2\u00107\u001a\u00020\u00002\b\b\u0002\u0010\u0017\u001a\u00020\b2\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u00192\b\b\u0002\u0010\u001a\u001a\u00020\b2\b\b\u0002\u0010\u001b\u001a\u00020\u000eH\u0002J\b\u00108\u001a\u000209H\u0016J\b\u0010:\u001a\u000209H\u0016J\b\u0010;\u001a\u00020<H\u0002J\r\u0010=\u001a\u000209H\u0000¢\u0006\u0002\b>J\u0018\u0010?\u001a\u00020<2\u0006\u0010@\u001a\u00020A2\u0006\u0010B\u001a\u00020CH\u0002J\n\u0010D\u001a\u0004\u0018\u00010\u0019H\u0002J#\u0010E\u001a\u00020\u00002\f\u0010F\u001a\b\u0012\u0004\u0012\u00020C0\u00162\u0006\u0010@\u001a\u00020AH\u0000¢\u0006\u0002\bGJ%\u0010H\u001a\u0004\u0018\u00010\u00002\f\u0010F\u001a\b\u0012\u0004\u0012\u00020C0\u00162\u0006\u0010@\u001a\u00020AH\u0000¢\u0006\u0002\bIJ\b\u0010J\u001a\u000205H\u0016J\u001a\u0010K\u001a\u00020<2\u0006\u0010\u000f\u001a\u00020\u00102\b\u0010L\u001a\u0004\u0018\u00010MH\u0016J\b\u0010N\u001a\u00020<H\u0016J\b\u0010O\u001a\u00020<H\u0016J\b\u0010P\u001a\u00020\u0001H\u0016J\u0006\u0010Q\u001a\u00020<R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0013\u001a\u00020\u0014X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u001c\u0010\u0015\u001a\n\u0012\u0004\u0012\u00020\u0014\u0018\u00010\u0016X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u000e\u0010\u0017\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0018\u001a\u0004\u0018\u00010\u0019X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u001a\u001a\u00020\bX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u0014\u0010\u001b\u001a\u00020\u000eX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b$\u0010%R\u000e\u0010&\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010'\u001a\u0004\u0018\u00010(X\u0082\u000e¢\u0006\u0002\n\u0000R\u001c\u0010)\u001a\u0004\u0018\u00010(X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b*\u0010+\"\u0004\b,\u0010-R\u0010\u0010.\u001a\u0004\u0018\u00010/X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u00100\u001a\u0004\u0018\u000101X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u00102\u001a\u000203X\u0082.¢\u0006\u0002\n\u0000R\u0010\u00104\u001a\u0004\u0018\u000105X\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u00106\u001a\u00020\u000e8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b6\u0010%¨\u0006S"},
   d2 = {"Lokhttp3/internal/connection/ConnectPlan;", "Lokhttp3/internal/connection/RoutePlanner$Plan;", "Lokhttp3/internal/http/ExchangeCodec$Carrier;", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "connectionPool", "Lokhttp3/internal/connection/RealConnectionPool;", "readTimeoutMillis", "", "writeTimeoutMillis", "socketConnectTimeoutMillis", "socketReadTimeoutMillis", "pingIntervalMillis", "retryOnConnectionFailure", "", "call", "Lokhttp3/internal/connection/RealCall;", "routePlanner", "Lokhttp3/internal/connection/RealRoutePlanner;", "route", "Lokhttp3/Route;", "routes", "", "attempt", "tunnelRequest", "Lokhttp3/Request;", "connectionSpecIndex", "isTlsFallback", "<init>", "(Lokhttp3/internal/concurrent/TaskRunner;Lokhttp3/internal/connection/RealConnectionPool;IIIIIZLokhttp3/internal/connection/RealCall;Lokhttp3/internal/connection/RealRoutePlanner;Lokhttp3/Route;Ljava/util/List;ILokhttp3/Request;IZ)V", "getRoute", "()Lokhttp3/Route;", "getRoutes$okhttp", "()Ljava/util/List;", "getConnectionSpecIndex$okhttp", "()I", "isTlsFallback$okhttp", "()Z", "canceled", "rawSocket", "Ljava/net/Socket;", "javaNetSocket", "getJavaNetSocket$okhttp", "()Ljava/net/Socket;", "setJavaNetSocket$okhttp", "(Ljava/net/Socket;)V", "handshake", "Lokhttp3/Handshake;", "protocol", "Lokhttp3/Protocol;", "socket", "Lokhttp3/internal/connection/BufferedSocket;", "connection", "Lokhttp3/internal/connection/RealConnection;", "isReady", "copy", "connectTcp", "Lokhttp3/internal/connection/RoutePlanner$ConnectResult;", "connectTlsEtc", "connectSocket", "", "connectTunnel", "connectTunnel$okhttp", "connectTls", "sslSocket", "Ljavax/net/ssl/SSLSocket;", "connectionSpec", "Lokhttp3/ConnectionSpec;", "createTunnel", "planWithCurrentOrInitialConnectionSpec", "connectionSpecs", "planWithCurrentOrInitialConnectionSpec$okhttp", "nextConnectionSpec", "nextConnectionSpec$okhttp", "handleSuccess", "trackFailure", "e", "Ljava/io/IOException;", "noNewExchanges", "cancel", "retry", "closeQuietly", "Companion", "okhttp"}
)
@SourceDebugExtension({"SMAP\nConnectPlan.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConnectPlan.kt\nokhttp3/internal/connection/ConnectPlan\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Lockable.kt\nokhttp3/internal/concurrent/LockableKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,564:1\n1#2:565\n63#3:566\n1563#4:567\n1634#4,3:568\n*S KotlinDebug\n*F\n+ 1 ConnectPlan.kt\nokhttp3/internal/connection/ConnectPlan\n*L\n508#1:566\n393#1:567\n393#1:568,3\n*E\n"})
public final class ConnectPlan implements RoutePlanner.Plan, ExchangeCodec.Carrier {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final TaskRunner taskRunner;
   @NotNull
   private final RealConnectionPool connectionPool;
   private final int readTimeoutMillis;
   private final int writeTimeoutMillis;
   private final int socketConnectTimeoutMillis;
   private final int socketReadTimeoutMillis;
   private final int pingIntervalMillis;
   private final boolean retryOnConnectionFailure;
   @NotNull
   private final RealCall call;
   @NotNull
   private final RealRoutePlanner routePlanner;
   @NotNull
   private final Route route;
   @Nullable
   private final List<Route> routes;
   private final int attempt;
   @Nullable
   private final Request tunnelRequest;
   private final int connectionSpecIndex;
   private final boolean isTlsFallback;
   private volatile boolean canceled;
   @Nullable
   private Socket rawSocket;
   @Nullable
   private Socket javaNetSocket;
   @Nullable
   private Handshake handshake;
   @Nullable
   private Protocol protocol;
   private BufferedSocket socket;
   @Nullable
   private RealConnection connection;
   @NotNull
   private static final String NPE_THROW_WITH_NULL = "throw with null exception";
   private static final int MAX_TUNNEL_ATTEMPTS = 21;

   public ConnectPlan(@NotNull TaskRunner taskRunner, @NotNull RealConnectionPool connectionPool, int readTimeoutMillis, int writeTimeoutMillis, int socketConnectTimeoutMillis, int socketReadTimeoutMillis, int pingIntervalMillis, boolean retryOnConnectionFailure, @NotNull RealCall call, @NotNull RealRoutePlanner routePlanner, @NotNull Route route, @Nullable List<Route> routes, int attempt, @Nullable Request tunnelRequest, int connectionSpecIndex, boolean isTlsFallback) {
      Intrinsics.checkNotNullParameter(taskRunner, "taskRunner");
      Intrinsics.checkNotNullParameter(connectionPool, "connectionPool");
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(routePlanner, "routePlanner");
      Intrinsics.checkNotNullParameter(route, "route");
      super();
      this.taskRunner = taskRunner;
      this.connectionPool = connectionPool;
      this.readTimeoutMillis = readTimeoutMillis;
      this.writeTimeoutMillis = writeTimeoutMillis;
      this.socketConnectTimeoutMillis = socketConnectTimeoutMillis;
      this.socketReadTimeoutMillis = socketReadTimeoutMillis;
      this.pingIntervalMillis = pingIntervalMillis;
      this.retryOnConnectionFailure = retryOnConnectionFailure;
      this.call = call;
      this.routePlanner = routePlanner;
      this.route = route;
      this.routes = routes;
      this.attempt = attempt;
      this.tunnelRequest = tunnelRequest;
      this.connectionSpecIndex = connectionSpecIndex;
      this.isTlsFallback = isTlsFallback;
   }

   @NotNull
   public Route getRoute() {
      return this.route;
   }

   @Nullable
   public final List<Route> getRoutes$okhttp() {
      return this.routes;
   }

   public final int getConnectionSpecIndex$okhttp() {
      return this.connectionSpecIndex;
   }

   public final boolean isTlsFallback$okhttp() {
      return this.isTlsFallback;
   }

   @Nullable
   public final Socket getJavaNetSocket$okhttp() {
      return this.javaNetSocket;
   }

   public final void setJavaNetSocket$okhttp(@Nullable Socket var1) {
      this.javaNetSocket = var1;
   }

   public boolean isReady() {
      return this.protocol != null;
   }

   private final ConnectPlan copy(int attempt, Request tunnelRequest, int connectionSpecIndex, boolean isTlsFallback) {
      return new ConnectPlan(this.taskRunner, this.connectionPool, this.readTimeoutMillis, this.writeTimeoutMillis, this.socketConnectTimeoutMillis, this.socketReadTimeoutMillis, this.pingIntervalMillis, this.retryOnConnectionFailure, this.call, this.routePlanner, this.getRoute(), this.routes, attempt, tunnelRequest, connectionSpecIndex, isTlsFallback);
   }

   // $FF: synthetic method
   static ConnectPlan copy$default(ConnectPlan var0, int var1, Request var2, int var3, boolean var4, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = var0.attempt;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.tunnelRequest;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.connectionSpecIndex;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.isTlsFallback;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   public RoutePlanner.ConnectResult connectTcp() {
      if (this.rawSocket != null) {
         int var10 = 0;
         String var11 = "TCP already connected";
         throw new IllegalStateException(var11.toString());
      } else {
         boolean success = false;
         ((Collection)this.call.getPlansToCancel$okhttp()).add(this);
         boolean var7 = false;

         RoutePlanner.ConnectResult var2;
         label105: {
            RoutePlanner.ConnectResult var4;
            try {
               try {
                  var7 = true;
                  this.call.getEventListener$okhttp().connectStart(this.call, this.getRoute().socketAddress(), this.getRoute().proxy());
                  this.connectionPool.getConnectionListener$okhttp().connectStart(this.getRoute(), this.call);
                  this.connectSocket();
                  success = true;
                  var2 = new RoutePlanner.ConnectResult(this, (RoutePlanner.Plan)null, (Throwable)null, 6, (DefaultConstructorMarker)null);
                  var7 = false;
                  break label105;
               } catch (IOException var8) {
                  if (this.getRoute().address().proxy() == null && this.getRoute().proxy().type() != Type.DIRECT) {
                     this.getRoute().address().proxySelector().connectFailed(this.getRoute().address().url().uri(), this.getRoute().proxy().address(), var8);
                  }
               }

               this.call.getEventListener$okhttp().connectFailed(this.call, this.getRoute().socketAddress(), this.getRoute().proxy(), (Protocol)null, var8);
               this.connectionPool.getConnectionListener$okhttp().connectFailed(this.getRoute(), this.call, var8);
               var4 = new RoutePlanner.ConnectResult(this, (RoutePlanner.Plan)null, (Throwable)var8, 2, (DefaultConstructorMarker)null);
               var7 = false;
            } finally {
               if (var7) {
                  ((Collection)this.call.getPlansToCancel$okhttp()).remove(this);
                  if (!success) {
                     Socket var10000 = this.rawSocket;
                     if (var10000 != null) {
                        _UtilJvmKt.closeQuietly(var10000);
                     }
                  }

               }
            }

            ((Collection)this.call.getPlansToCancel$okhttp()).remove(this);
            if (!success) {
               Socket var15 = this.rawSocket;
               if (var15 != null) {
                  _UtilJvmKt.closeQuietly(var15);
               }
            }

            return var4;
         }

         ((Collection)this.call.getPlansToCancel$okhttp()).remove(this);
         return var2;
      }
   }

   @NotNull
   public RoutePlanner.ConnectResult connectTlsEtc() {
      Socket var10000 = this.rawSocket;
      if (var10000 == null) {
         int var15 = 0;
         String var16 = "TCP not connected";
         throw new IllegalArgumentException(var16.toString());
      } else {
         Socket rawSocket = var10000;
         if (this.isReady()) {
            int var13 = 0;
            String var14 = "already connected";
            throw new IllegalStateException(var14.toString());
         } else {
            List connectionSpecs = this.getRoute().address().connectionSpecs();
            ConnectPlan retryTlsConnection = null;
            boolean success = false;
            ((Collection)this.call.getPlansToCancel$okhttp()).add(this);
            boolean var10 = false;

            RoutePlanner.ConnectResult e;
            label238: {
               RoutePlanner.ConnectResult connectionSpec;
               label239: {
                  try {
                     label226: {
                        label225: {
                           try {
                              var10 = true;
                              if (this.tunnelRequest != null) {
                                 RoutePlanner.ConnectResult tunnelResult = this.connectTunnel$okhttp();
                                 if (tunnelResult.getNextPlan() != null || tunnelResult.getThrowable() != null) {
                                    e = tunnelResult;
                                    var10 = false;
                                    break label226;
                                 }
                              }

                              if (this.getRoute().address().sslSocketFactory() != null) {
                                 BufferedSocket var26 = this.socket;
                                 if (var26 == null) {
                                    Intrinsics.throwUninitializedPropertyAccessException("socket");
                                    var26 = null;
                                 }

                                 if (!var26.getSource().getBuffer().exhausted()) {
                                    throw new IOException("TLS tunnel buffered too many bytes!");
                                 }

                                 var26 = this.socket;
                                 if (var26 == null) {
                                    Intrinsics.throwUninitializedPropertyAccessException("socket");
                                    var26 = null;
                                 }

                                 if (!var26.getSink().getBuffer().exhausted()) {
                                    throw new IOException("TLS tunnel buffered too many bytes!");
                                 }

                                 this.call.getEventListener$okhttp().secureConnectStart(this.call);
                                 Socket var28 = this.getRoute().address().sslSocketFactory().createSocket(rawSocket, this.getRoute().address().url().host(), this.getRoute().address().url().port(), true);
                                 Intrinsics.checkNotNull(var28, "null cannot be cast to non-null type javax.net.ssl.SSLSocket");
                                 SSLSocket sslSocket = (SSLSocket)var28;
                                 ConnectPlan tlsEquipPlan = this.planWithCurrentOrInitialConnectionSpec$okhttp(connectionSpecs, sslSocket);
                                 ConnectionSpec connectionSpec = (ConnectionSpec)connectionSpecs.get(tlsEquipPlan.connectionSpecIndex);
                                 tlsEquipPlan.nextConnectionSpec$okhttp(connectionSpecs, sslSocket);
                                 connectionSpec.apply$okhttp(sslSocket, tlsEquipPlan.isTlsFallback);
                                 this.connectTls(sslSocket, connectionSpec);
                                 this.call.getEventListener$okhttp().secureConnectEnd(this.call, this.handshake);
                              } else {
                                 this.javaNetSocket = rawSocket;
                                 this.protocol = this.getRoute().address().protocols().contains(Protocol.H2_PRIOR_KNOWLEDGE) ? Protocol.H2_PRIOR_KNOWLEDGE : Protocol.HTTP_1_1;
                              }

                              RealConnection var29 = new RealConnection;
                              TaskRunner var10002 = this.taskRunner;
                              RealConnectionPool var10003 = this.connectionPool;
                              Route var10004 = this.getRoute();
                              Socket var10006 = this.javaNetSocket;
                              Intrinsics.checkNotNull(var10006);
                              Handshake var10007 = this.handshake;
                              Protocol var10008 = this.protocol;
                              Intrinsics.checkNotNull(var10008);
                              BufferedSocket var10009 = this.socket;
                              if (var10009 == null) {
                                 Intrinsics.throwUninitializedPropertyAccessException("socket");
                                 var10009 = null;
                              }

                              var29.<init>(var10002, var10003, var10004, rawSocket, var10006, var10007, var10008, var10009, this.pingIntervalMillis, this.connectionPool.getConnectionListener$okhttp());
                              RealConnection connection = var29;
                              this.connection = connection;
                              connection.start();
                              this.call.getEventListener$okhttp().connectEnd(this.call, this.getRoute().socketAddress(), this.getRoute().proxy(), this.protocol);
                              success = true;
                              e = new RoutePlanner.ConnectResult(this, (RoutePlanner.Plan)null, (Throwable)null, 6, (DefaultConstructorMarker)null);
                              var10 = false;
                              break label238;
                           } catch (IOException var11) {
                              this.call.getEventListener$okhttp().connectFailed(this.call, this.getRoute().socketAddress(), this.getRoute().proxy(), (Protocol)null, var11);
                              this.connectionPool.getConnectionListener$okhttp().connectFailed(this.getRoute(), this.call, var11);
                              if (this.retryOnConnectionFailure && RetryTlsHandshakeKt.retryTlsHandshake(var11)) {
                                 break label225;
                              }
                           }

                           retryTlsConnection = null;
                        }

                        connectionSpec = new RoutePlanner.ConnectResult(this, retryTlsConnection, (Throwable)var11);
                        var10 = false;
                        break label239;
                     }
                  } finally {
                     if (var10) {
                        ((Collection)this.call.getPlansToCancel$okhttp()).remove(this);
                        if (!success) {
                           var10000 = this.javaNetSocket;
                           if (var10000 != null) {
                              _UtilJvmKt.closeQuietly(var10000);
                           }

                           _UtilJvmKt.closeQuietly(rawSocket);
                        }

                     }
                  }

                  ((Collection)this.call.getPlansToCancel$okhttp()).remove(this);
                  var10000 = this.javaNetSocket;
                  if (var10000 != null) {
                     _UtilJvmKt.closeQuietly(var10000);
                  }

                  _UtilJvmKt.closeQuietly(rawSocket);
                  return e;
               }

               ((Collection)this.call.getPlansToCancel$okhttp()).remove(this);
               if (!success) {
                  var10000 = this.javaNetSocket;
                  if (var10000 != null) {
                     _UtilJvmKt.closeQuietly(var10000);
                  }

                  _UtilJvmKt.closeQuietly(rawSocket);
               }

               return connectionSpec;
            }

            ((Collection)this.call.getPlansToCancel$okhttp()).remove(this);
            return e;
         }
      }
   }

   private final void connectSocket() throws IOException {
      Proxy.Type var10000 = this.getRoute().proxy().type();
      Socket var8;
      switch (var10000 == null ? -1 : ConnectPlan.WhenMappings.$EnumSwitchMapping$0[var10000.ordinal()]) {
         case 1:
         case 2:
            var8 = this.getRoute().address().socketFactory().createSocket();
            Intrinsics.checkNotNull(var8);
            break;
         default:
            var8 = new Socket(this.getRoute().proxy());
      }

      Socket rawSocket = var8;
      this.rawSocket = rawSocket;
      if (this.canceled) {
         throw new IOException("canceled");
      } else {
         rawSocket.setSoTimeout(this.socketReadTimeoutMillis);

         try {
            Platform.Companion.get().connectSocket(rawSocket, this.getRoute().socketAddress(), this.socketConnectTimeoutMillis);
         } catch (ConnectException e) {
            ConnectException $this$connectSocket_u24lambda_u240 = new ConnectException("Failed to connect to " + this.getRoute().socketAddress());
            int var5 = 0;
            $this$connectSocket_u24lambda_u240.initCause((Throwable)e);
            throw (Throwable)$this$connectSocket_u24lambda_u240;
         }

         try {
            this.socket = BufferedSocketKt.asBufferedSocket(rawSocket);
         } catch (NullPointerException npe) {
            if (Intrinsics.areEqual((Object)npe.getMessage(), (Object)"throw with null exception")) {
               throw new IOException((Throwable)npe);
            }
         }

      }
   }

   @NotNull
   public final RoutePlanner.ConnectResult connectTunnel$okhttp() throws IOException {
      Request var10000 = this.createTunnel();
      if (var10000 == null) {
         return new RoutePlanner.ConnectResult(this, (RoutePlanner.Plan)null, (Throwable)null, 6, (DefaultConstructorMarker)null);
      } else {
         Request nextTunnelRequest = var10000;
         Socket var4 = this.rawSocket;
         if (var4 != null) {
            _UtilJvmKt.closeQuietly(var4);
         }

         int nextAttempt = this.attempt + 1;
         if (nextAttempt < 21) {
            this.call.getEventListener$okhttp().connectEnd(this.call, this.getRoute().socketAddress(), this.getRoute().proxy(), (Protocol)null);
            return new RoutePlanner.ConnectResult(this, copy$default(this, nextAttempt, nextTunnelRequest, 0, false, 12, (Object)null), (Throwable)null, 4, (DefaultConstructorMarker)null);
         } else {
            ProtocolException failure = new ProtocolException("Too many tunnel connections attempted: 21");
            this.call.getEventListener$okhttp().connectFailed(this.call, this.getRoute().socketAddress(), this.getRoute().proxy(), (Protocol)null, (IOException)failure);
            this.connectionPool.getConnectionListener$okhttp().connectFailed(this.getRoute(), this.call, (IOException)failure);
            return new RoutePlanner.ConnectResult(this, (RoutePlanner.Plan)null, (Throwable)failure, 2, (DefaultConstructorMarker)null);
         }
      }
   }

   private final void connectTls(SSLSocket sslSocket, ConnectionSpec connectionSpec) throws IOException {
      Address address = this.getRoute().address();
      boolean success = false;
      boolean var11 = false;

      try {
         var11 = true;
         if (connectionSpec.supportsTlsExtensions()) {
            Platform.Companion.get().configureTlsExtensions(sslSocket, address.url().host(), address.protocols());
         }

         sslSocket.startHandshake();
         SSLSession sslSocketSession = sslSocket.getSession();
         Handshake.Companion var10000 = Handshake.Companion;
         Intrinsics.checkNotNull(sslSocketSession);
         Handshake unverifiedHandshake = var10000.get(sslSocketSession);
         HostnameVerifier var18 = address.hostnameVerifier();
         Intrinsics.checkNotNull(var18);
         if (!var18.verify(address.url().host(), sslSocketSession)) {
            List peerCertificates = unverifiedHandshake.peerCertificates();
            if (!((Collection)peerCertificates).isEmpty()) {
               Object var20 = peerCertificates.get(0);
               Intrinsics.checkNotNull(var20, "null cannot be cast to non-null type java.security.cert.X509Certificate");
               X509Certificate cert = (X509Certificate)var20;
               throw new SSLPeerUnverifiedException(StringsKt.trimMargin$default("\n            |Hostname " + address.url().host() + " not verified:\n            |    certificate: " + CertificatePinner.Companion.pin((Certificate)cert) + "\n            |    DN: " + cert.getSubjectDN().getName() + "\n            |    subjectAltNames: " + OkHostnameVerifier.INSTANCE.allSubjectAltNames(cert) + "\n            ", (String)null, 1, (Object)null));
            }

            throw new SSLPeerUnverifiedException("Hostname " + address.url().host() + " not verified (no certificates)");
         }

         CertificatePinner var19 = address.certificatePinner();
         Intrinsics.checkNotNull(var19);
         CertificatePinner certificatePinner = var19;
         Handshake handshake = new Handshake(unverifiedHandshake.tlsVersion(), unverifiedHandshake.cipherSuite(), unverifiedHandshake.localCertificates(), ConnectPlan::connectTls$lambda$0);
         this.handshake = handshake;
         certificatePinner.check$okhttp(address.url().host(), ConnectPlan::connectTls$lambda$1);
         String maybeProtocol = connectionSpec.supportsTlsExtensions() ? Platform.Companion.get().getSelectedProtocol(sslSocket) : null;
         this.javaNetSocket = (Socket)sslSocket;
         this.socket = BufferedSocketKt.asBufferedSocket((Socket)sslSocket);
         this.protocol = maybeProtocol != null ? Protocol.Companion.get(maybeProtocol) : Protocol.HTTP_1_1;
         success = true;
         var11 = false;
      } finally {
         if (var11) {
            Platform.Companion.get().afterHandshake(sslSocket);
            _UtilJvmKt.closeQuietly((Socket)sslSocket);
         }
      }

      Platform.Companion.get().afterHandshake(sslSocket);
   }

   private final Request createTunnel() throws IOException {
      Request var10000 = this.tunnelRequest;
      Intrinsics.checkNotNull(var10000);
      Request nextRequest = var10000;
      HttpUrl url = this.getRoute().address().url();
      String requestLine = "CONNECT " + _UtilJvmKt.toHostHeader(url, true) + " HTTP/1.1";

      label31:
      while(true) {
         Http1ExchangeCodec var6 = new Http1ExchangeCodec;
         ExchangeCodec.Carrier var10003 = this;
         BufferedSocket var10004 = this.socket;
         if (var10004 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("socket");
            var10004 = null;
         }

         var6.<init>((OkHttpClient)null, var10003, var10004);
         Http1ExchangeCodec tunnelCodec = var6;
         BufferedSocket var7 = this.socket;
         if (var7 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("socket");
            var7 = null;
         }

         var7.getSource().timeout().timeout((long)this.readTimeoutMillis, TimeUnit.MILLISECONDS);
         var7 = this.socket;
         if (var7 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("socket");
            var7 = null;
         }

         var7.getSink().timeout().timeout((long)this.writeTimeoutMillis, TimeUnit.MILLISECONDS);
         tunnelCodec.writeRequest(nextRequest.headers(), requestLine);
         tunnelCodec.finishRequest();
         Response.Builder var9 = tunnelCodec.readResponseHeaders(false);
         Intrinsics.checkNotNull(var9);
         Response response = var9.request(nextRequest).build();
         tunnelCodec.skipConnectBody(response);
         switch (response.code()) {
            case 200:
               return null;
            case 407:
               Request var10 = this.getRoute().address().proxyAuthenticator().authenticate(this.getRoute(), response);
               if (var10 == null) {
                  throw new IOException("Failed to authenticate with proxy");
               }

               nextRequest = var10;
               if (StringsKt.equals("close", Response.header$default(response, "Connection", (String)null, 2, (Object)null), true)) {
                  break label31;
               }
               break;
            default:
               throw new IOException("Unexpected response code for CONNECT: " + response.code());
         }
      }

      return nextRequest;
   }

   @NotNull
   public final ConnectPlan planWithCurrentOrInitialConnectionSpec$okhttp(@NotNull List<ConnectionSpec> connectionSpecs, @NotNull SSLSocket sslSocket) throws IOException {
      Intrinsics.checkNotNullParameter(connectionSpecs, "connectionSpecs");
      Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");
      if (this.connectionSpecIndex != -1) {
         return this;
      } else {
         ConnectPlan var10000 = this.nextConnectionSpec$okhttp(connectionSpecs, sslSocket);
         if (var10000 == null) {
            StringBuilder var10002 = (new StringBuilder()).append("Unable to find acceptable protocols. isFallback=").append(this.isTlsFallback).append(", modes=").append(connectionSpecs).append(", supported protocols=");
            String var10003 = sslSocket.getEnabledProtocols();
            Intrinsics.checkNotNull(var10003);
            var10003 = Arrays.toString(var10003);
            Intrinsics.checkNotNullExpressionValue(var10003, "toString(...)");
            throw new UnknownServiceException(var10002.append(var10003).toString());
         } else {
            return var10000;
         }
      }
   }

   @Nullable
   public final ConnectPlan nextConnectionSpec$okhttp(@NotNull List<ConnectionSpec> connectionSpecs, @NotNull SSLSocket sslSocket) {
      Intrinsics.checkNotNullParameter(connectionSpecs, "connectionSpecs");
      Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");
      int i = this.connectionSpecIndex + 1;

      for(int var4 = connectionSpecs.size(); i < var4; ++i) {
         if (((ConnectionSpec)connectionSpecs.get(i)).isCompatible(sslSocket)) {
            return copy$default(this, 0, (Request)null, i, this.connectionSpecIndex != -1, 3, (Object)null);
         }
      }

      return null;
   }

   @NotNull
   public RealConnection handleSuccess() {
      this.call.getClient().getRouteDatabase$okhttp().connected(this.getRoute());
      RealConnection var10000 = this.connection;
      Intrinsics.checkNotNull(var10000);
      RealConnection connection = var10000;
      connection.getConnectionListener$okhttp().connectEnd(connection, this.getRoute(), this.call);
      ReusePlan pooled3 = this.routePlanner.planReusePooledConnection$okhttp(this, this.routes);
      if (pooled3 != null) {
         return pooled3.getConnection();
      } else {
         Lockable $this$withLock$iv = connection;
         int $i$f$withLock = 0;
         synchronized($this$withLock$iv) {
            int var6 = 0;
            this.connectionPool.put(connection);
            this.call.acquireConnectionNoEvents(connection);
            Unit var7 = Unit.INSTANCE;
         }

         this.call.getEventListener$okhttp().connectionAcquired(this.call, connection);
         connection.getConnectionListener$okhttp().connectionAcquired(connection, this.call);
         return connection;
      }
   }

   public void trackFailure(@NotNull RealCall call, @Nullable IOException e) {
      Intrinsics.checkNotNullParameter(call, "call");
   }

   public void noNewExchanges() {
   }

   public void cancel() {
      this.canceled = true;
      Socket var10000 = this.rawSocket;
      if (var10000 != null) {
         _UtilJvmKt.closeQuietly(var10000);
      }

   }

   @NotNull
   public RoutePlanner.Plan retry() {
      return new ConnectPlan(this.taskRunner, this.connectionPool, this.readTimeoutMillis, this.writeTimeoutMillis, this.socketConnectTimeoutMillis, this.socketReadTimeoutMillis, this.pingIntervalMillis, this.retryOnConnectionFailure, this.call, this.routePlanner, this.getRoute(), this.routes, this.attempt, this.tunnelRequest, this.connectionSpecIndex, this.isTlsFallback);
   }

   public final void closeQuietly() {
      Socket var10000 = this.javaNetSocket;
      if (var10000 != null) {
         _UtilJvmKt.closeQuietly(var10000);
      }

   }

   private static final List connectTls$lambda$0(CertificatePinner $certificatePinner, Handshake $unverifiedHandshake, Address $address) {
      CertificateChainCleaner var10000 = $certificatePinner.getCertificateChainCleaner$okhttp();
      Intrinsics.checkNotNull(var10000);
      return var10000.clean($unverifiedHandshake.peerCertificates(), $address.url().host());
   }

   private static final List connectTls$lambda$1(Handshake $handshake) {
      Iterable $this$map$iv = (Iterable)$handshake.peerCertificates();
      int $i$f$map = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
      int $i$f$mapTo = 0;

      for(Object item$iv$iv : $this$map$iv) {
         Certificate it = (Certificate)item$iv$iv;
         int var9 = 0;
         Intrinsics.checkNotNull(it, "null cannot be cast to non-null type java.security.cert.X509Certificate");
         destination$iv$iv.add((X509Certificate)it);
      }

      return (List)destination$iv$iv;
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000¨\u0006\b"},
      d2 = {"Lokhttp3/internal/connection/ConnectPlan$Companion;", "", "<init>", "()V", "NPE_THROW_WITH_NULL", "", "MAX_TUNNEL_ATTEMPTS", "", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }

   // $FF: synthetic class
   @Metadata(
      mv = {2, 2, 0},
      k = 3,
      xi = 48
   )
   public static final class WhenMappings {
      // $FF: synthetic field
      public static final int[] $EnumSwitchMapping$0;

      static {
         int[] var0 = new int[Type.values().length];

         try {
            var0[Type.DIRECT.ordinal()] = 1;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[Type.HTTP.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
         }

         $EnumSwitchMapping$0 = var0;
      }
   }
}
