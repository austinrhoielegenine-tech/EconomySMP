package okhttp3;

import java.net.Proxy;
import java.net.ProxySelector;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal._UtilJvmKt;
import okhttp3.internal.concurrent.TaskRunner;
import okhttp3.internal.connection.RealCall;
import okhttp3.internal.connection.RouteDatabase;
import okhttp3.internal.platform.Platform;
import okhttp3.internal.proxy.NullProxySelector;
import okhttp3.internal.tls.CertificateChainCleaner;
import okhttp3.internal.tls.OkHostnameVerifier;
import okhttp3.internal.ws.RealWebSocket;
import okhttp3.internal.ws.WebSocketExtensions;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000ú\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u001e\b\u0016\u0018\u0000 \u0083\u00012\u00020\u00012\u00020\u0002:\u0004\u0082\u0001\u0083\u0001B\u0011\b\u0000\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006B\t\b\u0016¢\u0006\u0004\b\u0005\u0010\u0007J\u000e\u0010Y\u001a\u00020Z2\u0006\u0010[\u001a\u00020\\J\b\u0010]\u001a\u00020^H\u0002J\u0010\u0010_\u001a\u00020`2\u0006\u0010a\u001a\u00020bH\u0016J\u0018\u0010c\u001a\u00020d2\u0006\u0010a\u001a\u00020b2\u0006\u0010e\u001a\u00020fH\u0016J\b\u0010g\u001a\u00020\u0004H\u0016J\r\u0010\b\u001a\u00020\tH\u0007¢\u0006\u0002\bhJ\r\u0010V\u001a\u00020WH\u0007¢\u0006\u0002\biJ\u0013\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u0007¢\u0006\u0002\bjJ\u0013\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u0007¢\u0006\u0002\bkJ\r\u0010\u0010\u001a\u00020\u0011H\u0007¢\u0006\u0002\blJ\r\u0010\u0013\u001a\u00020\u0014H\u0007¢\u0006\u0002\bmJ\r\u0010\u0017\u001a\u00020\u0018H\u0007¢\u0006\u0002\bnJ\r\u0010\u001a\u001a\u00020\u0014H\u0007¢\u0006\u0002\boJ\r\u0010\u001b\u001a\u00020\u0014H\u0007¢\u0006\u0002\bpJ\r\u0010\u001c\u001a\u00020\u001dH\u0007¢\u0006\u0002\bqJ\u000f\u0010\u001f\u001a\u0004\u0018\u00010 H\u0007¢\u0006\u0002\brJ\r\u0010\"\u001a\u00020#H\u0007¢\u0006\u0002\bsJ\u000f\u0010%\u001a\u0004\u0018\u00010&H\u0007¢\u0006\u0002\btJ\r\u0010(\u001a\u00020)H\u0007¢\u0006\u0002\buJ\r\u0010+\u001a\u00020\u0018H\u0007¢\u0006\u0002\bvJ\r\u0010,\u001a\u00020-H\u0007¢\u0006\u0002\bwJ\r\u00101\u001a\u000200H\u0007¢\u0006\u0002\bxJ\u0013\u00106\u001a\b\u0012\u0004\u0012\u0002070\fH\u0007¢\u0006\u0002\byJ\u0013\u00108\u001a\b\u0012\u0004\u0012\u0002090\fH\u0007¢\u0006\u0002\bzJ\r\u0010:\u001a\u00020;H\u0007¢\u0006\u0002\b{J\r\u0010=\u001a\u00020>H\u0007¢\u0006\u0002\b|J\r\u0010C\u001a\u00020DH\u0007¢\u0006\u0002\b}J\r\u0010F\u001a\u00020DH\u0007¢\u0006\u0002\b~J\r\u0010G\u001a\u00020DH\u0007¢\u0006\u0002\b\u007fJ\u000e\u0010H\u001a\u00020DH\u0007¢\u0006\u0003\b\u0080\u0001J\u000e\u0010I\u001a\u00020DH\u0007¢\u0006\u0003\b\u0081\u0001R\u0013\u0010\b\u001a\u00020\t8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\nR\u0019\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\u000eR\u0019\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\r0\f8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000eR\u0013\u0010\u0010\u001a\u00020\u00118\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0012R\u0013\u0010\u0013\u001a\u00020\u00148\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0015R\u0013\u0010\u0016\u001a\u00020\u00148\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0015R\u0013\u0010\u0017\u001a\u00020\u00188\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0019R\u0013\u0010\u001a\u001a\u00020\u00148\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0015R\u0013\u0010\u001b\u001a\u00020\u00148\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0015R\u0013\u0010\u001c\u001a\u00020\u001d8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001eR\u0015\u0010\u001f\u001a\u0004\u0018\u00010 8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010!R\u0013\u0010\"\u001a\u00020#8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\"\u0010$R\u0015\u0010%\u001a\u0004\u0018\u00010&8\u0007¢\u0006\b\n\u0000\u001a\u0004\b%\u0010'R\u0013\u0010(\u001a\u00020)8\u0007¢\u0006\b\n\u0000\u001a\u0004\b(\u0010*R\u0013\u0010+\u001a\u00020\u00188\u0007¢\u0006\b\n\u0000\u001a\u0004\b+\u0010\u0019R\u0013\u0010,\u001a\u00020-8\u0007¢\u0006\b\n\u0000\u001a\u0004\b,\u0010.R\u0010\u0010/\u001a\u0004\u0018\u000100X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u00101\u001a\u0002008G¢\u0006\u0006\u001a\u0004\b1\u00102R\u0015\u00103\u001a\u0004\u0018\u0001048\u0007¢\u0006\b\n\u0000\u001a\u0004\b3\u00105R\u0019\u00106\u001a\b\u0012\u0004\u0012\u0002070\f8\u0007¢\u0006\b\n\u0000\u001a\u0004\b6\u0010\u000eR\u0019\u00108\u001a\b\u0012\u0004\u0012\u0002090\f8\u0007¢\u0006\b\n\u0000\u001a\u0004\b8\u0010\u000eR\u0013\u0010:\u001a\u00020;8\u0007¢\u0006\b\n\u0000\u001a\u0004\b:\u0010<R\u0013\u0010=\u001a\u00020>8\u0007¢\u0006\b\n\u0000\u001a\u0004\b=\u0010?R\u0015\u0010@\u001a\u0004\u0018\u00010A8\u0007¢\u0006\b\n\u0000\u001a\u0004\b@\u0010BR\u0013\u0010C\u001a\u00020D8\u0007¢\u0006\b\n\u0000\u001a\u0004\bC\u0010ER\u0013\u0010F\u001a\u00020D8\u0007¢\u0006\b\n\u0000\u001a\u0004\bF\u0010ER\u0013\u0010G\u001a\u00020D8\u0007¢\u0006\b\n\u0000\u001a\u0004\bG\u0010ER\u0013\u0010H\u001a\u00020D8\u0007¢\u0006\b\n\u0000\u001a\u0004\bH\u0010ER\u0013\u0010I\u001a\u00020D8\u0007¢\u0006\b\n\u0000\u001a\u0004\bI\u0010ER\u0013\u0010J\u001a\u00020D8\u0007¢\u0006\b\n\u0000\u001a\u0004\bJ\u0010ER\u0013\u0010K\u001a\u00020L8\u0007¢\u0006\b\n\u0000\u001a\u0004\bK\u0010MR\u0014\u0010N\u001a\u00020OX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\bP\u0010QR\u0014\u0010R\u001a\u00020SX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\bT\u0010UR\u0013\u0010V\u001a\u00020W8\u0007¢\u0006\b\n\u0000\u001a\u0004\bV\u0010X¨\u0006\u0084\u0001"},
   d2 = {"Lokhttp3/OkHttpClient;", "Lokhttp3/Call$Factory;", "Lokhttp3/WebSocket$Factory;", "builder", "Lokhttp3/OkHttpClient$Builder;", "<init>", "(Lokhttp3/OkHttpClient$Builder;)V", "()V", "dispatcher", "Lokhttp3/Dispatcher;", "()Lokhttp3/Dispatcher;", "interceptors", "", "Lokhttp3/Interceptor;", "()Ljava/util/List;", "networkInterceptors", "eventListenerFactory", "Lokhttp3/EventListener$Factory;", "()Lokhttp3/EventListener$Factory;", "retryOnConnectionFailure", "", "()Z", "fastFallback", "authenticator", "Lokhttp3/Authenticator;", "()Lokhttp3/Authenticator;", "followRedirects", "followSslRedirects", "cookieJar", "Lokhttp3/CookieJar;", "()Lokhttp3/CookieJar;", "cache", "Lokhttp3/Cache;", "()Lokhttp3/Cache;", "dns", "Lokhttp3/Dns;", "()Lokhttp3/Dns;", "proxy", "Ljava/net/Proxy;", "()Ljava/net/Proxy;", "proxySelector", "Ljava/net/ProxySelector;", "()Ljava/net/ProxySelector;", "proxyAuthenticator", "socketFactory", "Ljavax/net/SocketFactory;", "()Ljavax/net/SocketFactory;", "sslSocketFactoryOrNull", "Ljavax/net/ssl/SSLSocketFactory;", "sslSocketFactory", "()Ljavax/net/ssl/SSLSocketFactory;", "x509TrustManager", "Ljavax/net/ssl/X509TrustManager;", "()Ljavax/net/ssl/X509TrustManager;", "connectionSpecs", "Lokhttp3/ConnectionSpec;", "protocols", "Lokhttp3/Protocol;", "hostnameVerifier", "Ljavax/net/ssl/HostnameVerifier;", "()Ljavax/net/ssl/HostnameVerifier;", "certificatePinner", "Lokhttp3/CertificatePinner;", "()Lokhttp3/CertificatePinner;", "certificateChainCleaner", "Lokhttp3/internal/tls/CertificateChainCleaner;", "()Lokhttp3/internal/tls/CertificateChainCleaner;", "callTimeoutMillis", "", "()I", "connectTimeoutMillis", "readTimeoutMillis", "writeTimeoutMillis", "pingIntervalMillis", "webSocketCloseTimeout", "minWebSocketMessageToCompress", "", "()J", "routeDatabase", "Lokhttp3/internal/connection/RouteDatabase;", "getRouteDatabase$okhttp", "()Lokhttp3/internal/connection/RouteDatabase;", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "getTaskRunner$okhttp", "()Lokhttp3/internal/concurrent/TaskRunner;", "connectionPool", "Lokhttp3/ConnectionPool;", "()Lokhttp3/ConnectionPool;", "address", "Lokhttp3/Address;", "url", "Lokhttp3/HttpUrl;", "verifyClientState", "", "newCall", "Lokhttp3/Call;", "request", "Lokhttp3/Request;", "newWebSocket", "Lokhttp3/WebSocket;", "listener", "Lokhttp3/WebSocketListener;", "newBuilder", "-deprecated_dispatcher", "-deprecated_connectionPool", "-deprecated_interceptors", "-deprecated_networkInterceptors", "-deprecated_eventListenerFactory", "-deprecated_retryOnConnectionFailure", "-deprecated_authenticator", "-deprecated_followRedirects", "-deprecated_followSslRedirects", "-deprecated_cookieJar", "-deprecated_cache", "-deprecated_dns", "-deprecated_proxy", "-deprecated_proxySelector", "-deprecated_proxyAuthenticator", "-deprecated_socketFactory", "-deprecated_sslSocketFactory", "-deprecated_connectionSpecs", "-deprecated_protocols", "-deprecated_hostnameVerifier", "-deprecated_certificatePinner", "-deprecated_callTimeoutMillis", "-deprecated_connectTimeoutMillis", "-deprecated_readTimeoutMillis", "-deprecated_writeTimeoutMillis", "-deprecated_pingIntervalMillis", "Builder", "Companion", "okhttp"}
)
@SourceDebugExtension({"SMAP\nOkHttpClient.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OkHttpClient.kt\nokhttp3/OkHttpClient\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1388:1\n2746#2,3:1389\n2746#2,3:1392\n1#3:1395\n*S KotlinDebug\n*F\n+ 1 OkHttpClient.kt\nokhttp3/OkHttpClient\n*L\n278#1:1389,3\n340#1:1392,3\n*E\n"})
public class OkHttpClient implements Call.Factory, WebSocket.Factory {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final Dispatcher dispatcher;
   @NotNull
   private final List<Interceptor> interceptors;
   @NotNull
   private final List<Interceptor> networkInterceptors;
   @NotNull
   private final EventListener.Factory eventListenerFactory;
   private final boolean retryOnConnectionFailure;
   private final boolean fastFallback;
   @NotNull
   private final Authenticator authenticator;
   private final boolean followRedirects;
   private final boolean followSslRedirects;
   @NotNull
   private final CookieJar cookieJar;
   @Nullable
   private final Cache cache;
   @NotNull
   private final Dns dns;
   @Nullable
   private final Proxy proxy;
   @NotNull
   private final ProxySelector proxySelector;
   @NotNull
   private final Authenticator proxyAuthenticator;
   @NotNull
   private final SocketFactory socketFactory;
   @Nullable
   private final SSLSocketFactory sslSocketFactoryOrNull;
   @Nullable
   private final X509TrustManager x509TrustManager;
   @NotNull
   private final List<ConnectionSpec> connectionSpecs;
   @NotNull
   private final List<Protocol> protocols;
   @NotNull
   private final HostnameVerifier hostnameVerifier;
   @NotNull
   private final CertificatePinner certificatePinner;
   @Nullable
   private final CertificateChainCleaner certificateChainCleaner;
   private final int callTimeoutMillis;
   private final int connectTimeoutMillis;
   private final int readTimeoutMillis;
   private final int writeTimeoutMillis;
   private final int pingIntervalMillis;
   private final int webSocketCloseTimeout;
   private final long minWebSocketMessageToCompress;
   @NotNull
   private final RouteDatabase routeDatabase;
   @NotNull
   private final TaskRunner taskRunner;
   @NotNull
   private final ConnectionPool connectionPool;
   @NotNull
   private static final List<Protocol> DEFAULT_PROTOCOLS;
   @NotNull
   private static final List<ConnectionSpec> DEFAULT_CONNECTION_SPECS;

   public OkHttpClient(@NotNull Builder builder) {
      Intrinsics.checkNotNullParameter(builder, "builder");
      super();
      this.dispatcher = builder.getDispatcher$okhttp();
      this.interceptors = _UtilJvmKt.toImmutableList(builder.getInterceptors$okhttp());
      this.networkInterceptors = _UtilJvmKt.toImmutableList(builder.getNetworkInterceptors$okhttp());
      this.eventListenerFactory = builder.getEventListenerFactory$okhttp();
      this.retryOnConnectionFailure = builder.getRetryOnConnectionFailure$okhttp();
      this.fastFallback = builder.getFastFallback$okhttp();
      this.authenticator = builder.getAuthenticator$okhttp();
      this.followRedirects = builder.getFollowRedirects$okhttp();
      this.followSslRedirects = builder.getFollowSslRedirects$okhttp();
      this.cookieJar = builder.getCookieJar$okhttp();
      this.cache = builder.getCache$okhttp();
      this.dns = builder.getDns$okhttp();
      this.proxy = builder.getProxy$okhttp();
      ProxySelector var10001;
      if (builder.getProxy$okhttp() != null) {
         var10001 = NullProxySelector.INSTANCE;
      } else {
         var10001 = builder.getProxySelector$okhttp();
         if (var10001 == null) {
            var10001 = ProxySelector.getDefault();
            if (var10001 == null) {
               var10001 = NullProxySelector.INSTANCE;
            }
         }
      }

      this.proxySelector = var10001;
      this.proxyAuthenticator = builder.getProxyAuthenticator$okhttp();
      this.socketFactory = builder.getSocketFactory$okhttp();
      this.connectionSpecs = builder.getConnectionSpecs$okhttp();
      this.protocols = builder.getProtocols$okhttp();
      this.hostnameVerifier = builder.getHostnameVerifier$okhttp();
      this.callTimeoutMillis = builder.getCallTimeout$okhttp();
      this.connectTimeoutMillis = builder.getConnectTimeout$okhttp();
      this.readTimeoutMillis = builder.getReadTimeout$okhttp();
      this.writeTimeoutMillis = builder.getWriteTimeout$okhttp();
      this.pingIntervalMillis = builder.getPingInterval$okhttp();
      this.webSocketCloseTimeout = builder.getWebSocketCloseTimeout$okhttp();
      this.minWebSocketMessageToCompress = builder.getMinWebSocketMessageToCompress$okhttp();
      RouteDatabase var12 = builder.getRouteDatabase$okhttp();
      if (var12 == null) {
         var12 = new RouteDatabase();
      }

      this.routeDatabase = var12;
      TaskRunner var13 = builder.getTaskRunner$okhttp();
      if (var13 == null) {
         var13 = TaskRunner.INSTANCE;
      }

      this.taskRunner = var13;
      OkHttpClient var10000 = this;
      ConnectionPool var14 = builder.getConnectionPool$okhttp();
      if (var14 == null) {
         ConnectionPool var3 = new ConnectionPool();
         int var5 = 0;
         builder.setConnectionPool$okhttp(var3);
         var10000 = this;
         var14 = var3;
      }

      var10000.connectionPool = var14;
      Iterable $this$none$iv = (Iterable)this.connectionSpecs;
      int $i$f$none = 0;
      boolean var11;
      if ($this$none$iv instanceof Collection && ((Collection)$this$none$iv).isEmpty()) {
         var11 = true;
      } else {
         Iterator it = $this$none$iv.iterator();

         while(true) {
            if (!it.hasNext()) {
               var11 = true;
               break;
            }

            Object element$iv = it.next();
            ConnectionSpec it = (ConnectionSpec)element$iv;
            int var7 = 0;
            if (it.isTls()) {
               var11 = false;
               break;
            }
         }
      }

      if (var11) {
         this.sslSocketFactoryOrNull = null;
         this.certificateChainCleaner = null;
         this.x509TrustManager = null;
         this.certificatePinner = CertificatePinner.DEFAULT;
      } else if (builder.getSslSocketFactoryOrNull$okhttp() != null) {
         this.sslSocketFactoryOrNull = builder.getSslSocketFactoryOrNull$okhttp();
         CertificateChainCleaner var15 = builder.getCertificateChainCleaner$okhttp();
         Intrinsics.checkNotNull(var15);
         this.certificateChainCleaner = var15;
         X509TrustManager var16 = builder.getX509TrustManagerOrNull$okhttp();
         Intrinsics.checkNotNull(var16);
         this.x509TrustManager = var16;
         this.certificatePinner = builder.getCertificatePinner$okhttp().withCertificateChainCleaner$okhttp(this.certificateChainCleaner);
      } else {
         this.x509TrustManager = Platform.Companion.get().platformTrustManager();
         this.sslSocketFactoryOrNull = Platform.Companion.get().newSslSocketFactory(this.x509TrustManager);
         this.certificateChainCleaner = CertificateChainCleaner.Companion.get(this.x509TrustManager);
         this.certificatePinner = builder.getCertificatePinner$okhttp().withCertificateChainCleaner$okhttp(this.certificateChainCleaner);
      }

      this.verifyClientState();
   }

   @JvmName(
      name = "dispatcher"
   )
   @NotNull
   public final Dispatcher dispatcher() {
      return this.dispatcher;
   }

   @JvmName(
      name = "interceptors"
   )
   @NotNull
   public final List<Interceptor> interceptors() {
      return this.interceptors;
   }

   @JvmName(
      name = "networkInterceptors"
   )
   @NotNull
   public final List<Interceptor> networkInterceptors() {
      return this.networkInterceptors;
   }

   @JvmName(
      name = "eventListenerFactory"
   )
   @NotNull
   public final EventListener.Factory eventListenerFactory() {
      return this.eventListenerFactory;
   }

   @JvmName(
      name = "retryOnConnectionFailure"
   )
   public final boolean retryOnConnectionFailure() {
      return this.retryOnConnectionFailure;
   }

   @JvmName(
      name = "fastFallback"
   )
   public final boolean fastFallback() {
      return this.fastFallback;
   }

   @JvmName(
      name = "authenticator"
   )
   @NotNull
   public final Authenticator authenticator() {
      return this.authenticator;
   }

   @JvmName(
      name = "followRedirects"
   )
   public final boolean followRedirects() {
      return this.followRedirects;
   }

   @JvmName(
      name = "followSslRedirects"
   )
   public final boolean followSslRedirects() {
      return this.followSslRedirects;
   }

   @JvmName(
      name = "cookieJar"
   )
   @NotNull
   public final CookieJar cookieJar() {
      return this.cookieJar;
   }

   @JvmName(
      name = "cache"
   )
   @Nullable
   public final Cache cache() {
      return this.cache;
   }

   @JvmName(
      name = "dns"
   )
   @NotNull
   public final Dns dns() {
      return this.dns;
   }

   @JvmName(
      name = "proxy"
   )
   @Nullable
   public final Proxy proxy() {
      return this.proxy;
   }

   @JvmName(
      name = "proxySelector"
   )
   @NotNull
   public final ProxySelector proxySelector() {
      return this.proxySelector;
   }

   @JvmName(
      name = "proxyAuthenticator"
   )
   @NotNull
   public final Authenticator proxyAuthenticator() {
      return this.proxyAuthenticator;
   }

   @JvmName(
      name = "socketFactory"
   )
   @NotNull
   public final SocketFactory socketFactory() {
      return this.socketFactory;
   }

   @JvmName(
      name = "sslSocketFactory"
   )
   @NotNull
   public final SSLSocketFactory sslSocketFactory() {
      return this.sslSocketFactoryOrNull;
   }

   @JvmName(
      name = "x509TrustManager"
   )
   @Nullable
   public final X509TrustManager x509TrustManager() {
      return this.x509TrustManager;
   }

   @JvmName(
      name = "connectionSpecs"
   )
   @NotNull
   public final List<ConnectionSpec> connectionSpecs() {
      return this.connectionSpecs;
   }

   @JvmName(
      name = "protocols"
   )
   @NotNull
   public final List<Protocol> protocols() {
      return this.protocols;
   }

   @JvmName(
      name = "hostnameVerifier"
   )
   @NotNull
   public final HostnameVerifier hostnameVerifier() {
      return this.hostnameVerifier;
   }

   @JvmName(
      name = "certificatePinner"
   )
   @NotNull
   public final CertificatePinner certificatePinner() {
      return this.certificatePinner;
   }

   @JvmName(
      name = "certificateChainCleaner"
   )
   @Nullable
   public final CertificateChainCleaner certificateChainCleaner() {
      return this.certificateChainCleaner;
   }

   @JvmName(
      name = "callTimeoutMillis"
   )
   public final int callTimeoutMillis() {
      return this.callTimeoutMillis;
   }

   @JvmName(
      name = "connectTimeoutMillis"
   )
   public final int connectTimeoutMillis() {
      return this.connectTimeoutMillis;
   }

   @JvmName(
      name = "readTimeoutMillis"
   )
   public final int readTimeoutMillis() {
      return this.readTimeoutMillis;
   }

   @JvmName(
      name = "writeTimeoutMillis"
   )
   public final int writeTimeoutMillis() {
      return this.writeTimeoutMillis;
   }

   @JvmName(
      name = "pingIntervalMillis"
   )
   public final int pingIntervalMillis() {
      return this.pingIntervalMillis;
   }

   @JvmName(
      name = "webSocketCloseTimeout"
   )
   public final int webSocketCloseTimeout() {
      return this.webSocketCloseTimeout;
   }

   @JvmName(
      name = "minWebSocketMessageToCompress"
   )
   public final long minWebSocketMessageToCompress() {
      return this.minWebSocketMessageToCompress;
   }

   @NotNull
   public final RouteDatabase getRouteDatabase$okhttp() {
      return this.routeDatabase;
   }

   @NotNull
   public final TaskRunner getTaskRunner$okhttp() {
      return this.taskRunner;
   }

   @JvmName(
      name = "connectionPool"
   )
   @NotNull
   public final ConnectionPool connectionPool() {
      return this.connectionPool;
   }

   public OkHttpClient() {
      this(new Builder());
   }

   @NotNull
   public final Address address(@NotNull HttpUrl url) {
      Intrinsics.checkNotNullParameter(url, "url");
      SSLSocketFactory useSslSocketFactory = null;
      HostnameVerifier useHostnameVerifier = null;
      CertificatePinner useCertificatePinner = null;
      if (url.isHttps()) {
         useSslSocketFactory = this.sslSocketFactory();
         useHostnameVerifier = this.hostnameVerifier;
         useCertificatePinner = this.certificatePinner;
      }

      return new Address(url.host(), url.port(), this.dns, this.socketFactory, useSslSocketFactory, useHostnameVerifier, useCertificatePinner, this.proxyAuthenticator, this.proxy, this.protocols, this.connectionSpecs, this.proxySelector);
   }

   private final void verifyClientState() {
      List var10000 = this.interceptors;
      Intrinsics.checkNotNull(var10000, "null cannot be cast to non-null type kotlin.collections.List<okhttp3.Interceptor?>");
      if (var10000.contains((Object)null)) {
         int var15 = 0;
         String var16 = "Null interceptor: " + this.interceptors;
         throw new IllegalStateException(var16.toString());
      } else {
         var10000 = this.networkInterceptors;
         Intrinsics.checkNotNull(var10000, "null cannot be cast to non-null type kotlin.collections.List<okhttp3.Interceptor?>");
         if (var10000.contains((Object)null)) {
            int var13 = 0;
            String var14 = "Null network interceptor: " + this.networkInterceptors;
            throw new IllegalStateException(var14.toString());
         } else {
            Iterable $this$none$iv = (Iterable)this.connectionSpecs;
            int $i$f$none = 0;
            boolean var18;
            if ($this$none$iv instanceof Collection && ((Collection)$this$none$iv).isEmpty()) {
               var18 = true;
            } else {
               Iterator var3 = $this$none$iv.iterator();

               while(true) {
                  if (!var3.hasNext()) {
                     var18 = true;
                     break;
                  }

                  Object element$iv = var3.next();
                  ConnectionSpec it = (ConnectionSpec)element$iv;
                  int var6 = 0;
                  if (it.isTls()) {
                     var18 = false;
                     break;
                  }
               }
            }

            if (var18) {
               if (this.sslSocketFactoryOrNull != null) {
                  throw new IllegalStateException("Check failed.");
               }

               if (this.certificateChainCleaner != null) {
                  throw new IllegalStateException("Check failed.");
               }

               if (this.x509TrustManager != null) {
                  throw new IllegalStateException("Check failed.");
               }

               if (!Intrinsics.areEqual((Object)this.certificatePinner, (Object)CertificatePinner.DEFAULT)) {
                  throw new IllegalStateException("Check failed.");
               }

               Unit var19 = Unit.INSTANCE;
            } else {
               if (this.sslSocketFactoryOrNull == null) {
                  $i$f$none = 0;
                  String var12 = "sslSocketFactory == null";
                  throw new IllegalStateException(var12.toString());
               }

               if (this.certificateChainCleaner == null) {
                  $i$f$none = 0;
                  String var10 = "certificateChainCleaner == null";
                  throw new IllegalStateException(var10.toString());
               }

               if (this.x509TrustManager == null) {
                  $i$f$none = 0;
                  String $i$f$none = "x509TrustManager == null";
                  throw new IllegalStateException($i$f$none.toString());
               }
            }

         }
      }
   }

   @NotNull
   public Call newCall(@NotNull Request request) {
      Intrinsics.checkNotNullParameter(request, "request");
      return new RealCall(this, request, false);
   }

   @NotNull
   public WebSocket newWebSocket(@NotNull Request request, @NotNull WebSocketListener listener) {
      Intrinsics.checkNotNullParameter(request, "request");
      Intrinsics.checkNotNullParameter(listener, "listener");
      RealWebSocket webSocket = new RealWebSocket(this.taskRunner, request, listener, new Random(), (long)this.pingIntervalMillis, (WebSocketExtensions)null, this.minWebSocketMessageToCompress, (long)this.webSocketCloseTimeout);
      webSocket.connect(this);
      return webSocket;
   }

   @NotNull
   public Builder newBuilder() {
      return new Builder(this);
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "dispatcher",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_dispatcher"
   )
   @NotNull
   public final Dispatcher _deprecated_dispatcher/* $FF was: -deprecated_dispatcher*/() {
      return this.dispatcher;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "connectionPool",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_connectionPool"
   )
   @NotNull
   public final ConnectionPool _deprecated_connectionPool/* $FF was: -deprecated_connectionPool*/() {
      return this.connectionPool;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "interceptors",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_interceptors"
   )
   @NotNull
   public final List<Interceptor> _deprecated_interceptors/* $FF was: -deprecated_interceptors*/() {
      return this.interceptors;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "networkInterceptors",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_networkInterceptors"
   )
   @NotNull
   public final List<Interceptor> _deprecated_networkInterceptors/* $FF was: -deprecated_networkInterceptors*/() {
      return this.networkInterceptors;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "eventListenerFactory",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_eventListenerFactory"
   )
   @NotNull
   public final EventListener.Factory _deprecated_eventListenerFactory/* $FF was: -deprecated_eventListenerFactory*/() {
      return this.eventListenerFactory;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "retryOnConnectionFailure",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_retryOnConnectionFailure"
   )
   public final boolean _deprecated_retryOnConnectionFailure/* $FF was: -deprecated_retryOnConnectionFailure*/() {
      return this.retryOnConnectionFailure;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "authenticator",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_authenticator"
   )
   @NotNull
   public final Authenticator _deprecated_authenticator/* $FF was: -deprecated_authenticator*/() {
      return this.authenticator;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "followRedirects",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_followRedirects"
   )
   public final boolean _deprecated_followRedirects/* $FF was: -deprecated_followRedirects*/() {
      return this.followRedirects;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "followSslRedirects",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_followSslRedirects"
   )
   public final boolean _deprecated_followSslRedirects/* $FF was: -deprecated_followSslRedirects*/() {
      return this.followSslRedirects;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "cookieJar",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_cookieJar"
   )
   @NotNull
   public final CookieJar _deprecated_cookieJar/* $FF was: -deprecated_cookieJar*/() {
      return this.cookieJar;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "cache",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_cache"
   )
   @Nullable
   public final Cache _deprecated_cache/* $FF was: -deprecated_cache*/() {
      return this.cache;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "dns",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_dns"
   )
   @NotNull
   public final Dns _deprecated_dns/* $FF was: -deprecated_dns*/() {
      return this.dns;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "proxy",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_proxy"
   )
   @Nullable
   public final Proxy _deprecated_proxy/* $FF was: -deprecated_proxy*/() {
      return this.proxy;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "proxySelector",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_proxySelector"
   )
   @NotNull
   public final ProxySelector _deprecated_proxySelector/* $FF was: -deprecated_proxySelector*/() {
      return this.proxySelector;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "proxyAuthenticator",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_proxyAuthenticator"
   )
   @NotNull
   public final Authenticator _deprecated_proxyAuthenticator/* $FF was: -deprecated_proxyAuthenticator*/() {
      return this.proxyAuthenticator;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "socketFactory",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_socketFactory"
   )
   @NotNull
   public final SocketFactory _deprecated_socketFactory/* $FF was: -deprecated_socketFactory*/() {
      return this.socketFactory;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "sslSocketFactory",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_sslSocketFactory"
   )
   @NotNull
   public final SSLSocketFactory _deprecated_sslSocketFactory/* $FF was: -deprecated_sslSocketFactory*/() {
      return this.sslSocketFactory();
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "connectionSpecs",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_connectionSpecs"
   )
   @NotNull
   public final List<ConnectionSpec> _deprecated_connectionSpecs/* $FF was: -deprecated_connectionSpecs*/() {
      return this.connectionSpecs;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "protocols",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_protocols"
   )
   @NotNull
   public final List<Protocol> _deprecated_protocols/* $FF was: -deprecated_protocols*/() {
      return this.protocols;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "hostnameVerifier",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_hostnameVerifier"
   )
   @NotNull
   public final HostnameVerifier _deprecated_hostnameVerifier/* $FF was: -deprecated_hostnameVerifier*/() {
      return this.hostnameVerifier;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "certificatePinner",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_certificatePinner"
   )
   @NotNull
   public final CertificatePinner _deprecated_certificatePinner/* $FF was: -deprecated_certificatePinner*/() {
      return this.certificatePinner;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "callTimeoutMillis",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_callTimeoutMillis"
   )
   public final int _deprecated_callTimeoutMillis/* $FF was: -deprecated_callTimeoutMillis*/() {
      return this.callTimeoutMillis;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "connectTimeoutMillis",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_connectTimeoutMillis"
   )
   public final int _deprecated_connectTimeoutMillis/* $FF was: -deprecated_connectTimeoutMillis*/() {
      return this.connectTimeoutMillis;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "readTimeoutMillis",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_readTimeoutMillis"
   )
   public final int _deprecated_readTimeoutMillis/* $FF was: -deprecated_readTimeoutMillis*/() {
      return this.readTimeoutMillis;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "writeTimeoutMillis",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_writeTimeoutMillis"
   )
   public final int _deprecated_writeTimeoutMillis/* $FF was: -deprecated_writeTimeoutMillis*/() {
      return this.writeTimeoutMillis;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "pingIntervalMillis",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_pingIntervalMillis"
   )
   public final int _deprecated_pingIntervalMillis/* $FF was: -deprecated_pingIntervalMillis*/() {
      return this.pingIntervalMillis;
   }

   static {
      Protocol[] var0 = new Protocol[]{Protocol.HTTP_2, Protocol.HTTP_1_1};
      DEFAULT_PROTOCOLS = _UtilJvmKt.<Protocol>immutableListOf(var0);
      ConnectionSpec[] var1 = new ConnectionSpec[]{ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT};
      DEFAULT_CONNECTION_SPECS = _UtilJvmKt.<ConnectionSpec>immutableListOf(var1);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0086\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0014\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003B\u0011\b\u0010\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0002\u0010\u0006J\u000e\u0010\u0007\u001a\u00020\u00002\u0006\u0010\u0007\u001a\u00020\bJ\u000e\u0010\r\u001a\u00020\u00002\u0006\u0010\r\u001a\u00020\u000eJ\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014J\u0010\u0010«\u0001\u001a\u00020\u00002\u0007\u0010¬\u0001\u001a\u00020\u0015J?\u0010«\u0001\u001a\u00020\u00002*\b\u0004\u0010\u00ad\u0001\u001a#\u0012\u0017\u0012\u00150¯\u0001¢\u0006\u000f\b°\u0001\u0012\n\b±\u0001\u0012\u0005\b\b(²\u0001\u0012\u0005\u0012\u00030³\u00010®\u0001H\u0087\bø\u0001\u0000¢\u0006\u0003\b´\u0001J\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014J\u0010\u0010µ\u0001\u001a\u00020\u00002\u0007\u0010¬\u0001\u001a\u00020\u0015J?\u0010µ\u0001\u001a\u00020\u00002*\b\u0004\u0010\u00ad\u0001\u001a#\u0012\u0017\u0012\u00150¯\u0001¢\u0006\u000f\b°\u0001\u0012\n\b±\u0001\u0012\u0005\b\b(²\u0001\u0012\u0005\u0012\u00030³\u00010®\u0001H\u0087\bø\u0001\u0000¢\u0006\u0003\b¶\u0001J\u0011\u0010·\u0001\u001a\u00020\u00002\b\u0010·\u0001\u001a\u00030¸\u0001J\u000e\u0010\u001a\u001a\u00020\u00002\u0006\u0010\u001a\u001a\u00020\u001bJ\u000e\u0010 \u001a\u00020\u00002\u0006\u0010 \u001a\u00020!J\u000e\u0010&\u001a\u00020\u00002\u0006\u0010&\u001a\u00020!J\u000e\u0010)\u001a\u00020\u00002\u0006\u0010)\u001a\u00020*J\u000e\u0010/\u001a\u00020\u00002\u0006\u0010/\u001a\u00020!J\u000f\u00102\u001a\u00020\u00002\u0007\u0010¹\u0001\u001a\u00020!J\u000e\u00105\u001a\u00020\u00002\u0006\u00105\u001a\u000206J\u0010\u0010;\u001a\u00020\u00002\b\u0010;\u001a\u0004\u0018\u00010<J\u0019\u0010¥\u0001\u001a\u00020\u00002\b\u0010¥\u0001\u001a\u00030¦\u0001H\u0000¢\u0006\u0003\bº\u0001J\u000e\u0010A\u001a\u00020\u00002\u0006\u0010A\u001a\u00020BJ\u0010\u0010G\u001a\u00020\u00002\b\u0010G\u001a\u0004\u0018\u00010HJ\u000e\u0010M\u001a\u00020\u00002\u0006\u0010M\u001a\u00020NJ\u000e\u0010S\u001a\u00020\u00002\u0006\u0010S\u001a\u00020*J\u000e\u0010V\u001a\u00020\u00002\u0006\u0010V\u001a\u00020WJ\u0012\u0010»\u0001\u001a\u00020\u00002\u0007\u0010»\u0001\u001a\u00020]H\u0007J\u0019\u0010»\u0001\u001a\u00020\u00002\u0007\u0010»\u0001\u001a\u00020]2\u0007\u0010¼\u0001\u001a\u00020cJ\u0014\u0010h\u001a\u00020\u00002\f\u0010h\u001a\b\u0012\u0004\u0012\u00020j0iJ\u0014\u0010n\u001a\u00020\u00002\f\u0010n\u001a\b\u0012\u0004\u0012\u00020o0iJ\u000e\u0010r\u001a\u00020\u00002\u0006\u0010r\u001a\u00020sJ\u000e\u0010x\u001a\u00020\u00002\u0006\u0010x\u001a\u00020yJ\u001b\u0010\u0084\u0001\u001a\u00020\u00002\b\u0010½\u0001\u001a\u00030\u009a\u00012\b\u0010¾\u0001\u001a\u00030¿\u0001J\u0013\u0010\u0084\u0001\u001a\u00020\u00002\b\u0010À\u0001\u001a\u00030Á\u0001H\u0007J\u001a\u0010\u0084\u0001\u001a\u00020\u00002\b\u0010À\u0001\u001a\u00030Â\u0001¢\u0006\u0006\bÃ\u0001\u0010Ä\u0001J\u001b\u0010\u008a\u0001\u001a\u00020\u00002\b\u0010½\u0001\u001a\u00030\u009a\u00012\b\u0010¾\u0001\u001a\u00030¿\u0001J\u0013\u0010\u008a\u0001\u001a\u00020\u00002\b\u0010À\u0001\u001a\u00030Á\u0001H\u0007J\u001a\u0010\u008a\u0001\u001a\u00020\u00002\b\u0010À\u0001\u001a\u00030Â\u0001¢\u0006\u0006\bÅ\u0001\u0010Ä\u0001J\u001b\u0010\u008d\u0001\u001a\u00020\u00002\b\u0010½\u0001\u001a\u00030\u009a\u00012\b\u0010¾\u0001\u001a\u00030¿\u0001J\u0013\u0010\u008d\u0001\u001a\u00020\u00002\b\u0010À\u0001\u001a\u00030Á\u0001H\u0007J\u001a\u0010\u008d\u0001\u001a\u00020\u00002\b\u0010À\u0001\u001a\u00030Â\u0001¢\u0006\u0006\bÆ\u0001\u0010Ä\u0001J\u001b\u0010\u0090\u0001\u001a\u00020\u00002\b\u0010½\u0001\u001a\u00030\u009a\u00012\b\u0010¾\u0001\u001a\u00030¿\u0001J\u0013\u0010\u0090\u0001\u001a\u00020\u00002\b\u0010À\u0001\u001a\u00030Á\u0001H\u0007J\u001a\u0010\u0090\u0001\u001a\u00020\u00002\b\u0010À\u0001\u001a\u00030Â\u0001¢\u0006\u0006\bÇ\u0001\u0010Ä\u0001J\u001b\u0010\u0093\u0001\u001a\u00020\u00002\b\u0010È\u0001\u001a\u00030\u009a\u00012\b\u0010¾\u0001\u001a\u00030¿\u0001J\u0013\u0010\u0093\u0001\u001a\u00020\u00002\b\u0010À\u0001\u001a\u00030Á\u0001H\u0007J\u001a\u0010\u0093\u0001\u001a\u00020\u00002\b\u0010À\u0001\u001a\u00030Â\u0001¢\u0006\u0006\bÉ\u0001\u0010Ä\u0001J\u001b\u0010\u0096\u0001\u001a\u00020\u00002\b\u0010½\u0001\u001a\u00030\u009a\u00012\b\u0010¾\u0001\u001a\u00030¿\u0001J\u0013\u0010\u0096\u0001\u001a\u00020\u00002\b\u0010À\u0001\u001a\u00030Á\u0001H\u0007J\u001a\u0010\u0096\u0001\u001a\u00020\u00002\b\u0010À\u0001\u001a\u00030Â\u0001¢\u0006\u0006\bÊ\u0001\u0010Ä\u0001J\u0011\u0010\u0099\u0001\u001a\u00020\u00002\b\u0010Ë\u0001\u001a\u00030\u009a\u0001J\u0007\u0010Ì\u0001\u001a\u00020\u0005R\u001a\u0010\u0007\u001a\u00020\bX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u001c\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u001a\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u001a\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0017R\u001a\u0010\u001a\u001a\u00020\u001bX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001fR\u001a\u0010 \u001a\u00020!X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010#\"\u0004\b$\u0010%R\u001a\u0010&\u001a\u00020!X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b'\u0010#\"\u0004\b(\u0010%R\u001a\u0010)\u001a\u00020*X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b+\u0010,\"\u0004\b-\u0010.R\u001a\u0010/\u001a\u00020!X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b0\u0010#\"\u0004\b1\u0010%R\u001a\u00102\u001a\u00020!X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b3\u0010#\"\u0004\b4\u0010%R\u001a\u00105\u001a\u000206X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b7\u00108\"\u0004\b9\u0010:R\u001c\u0010;\u001a\u0004\u0018\u00010<X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b=\u0010>\"\u0004\b?\u0010@R\u001a\u0010A\u001a\u00020BX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bC\u0010D\"\u0004\bE\u0010FR\u001c\u0010G\u001a\u0004\u0018\u00010HX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bI\u0010J\"\u0004\bK\u0010LR\u001c\u0010M\u001a\u0004\u0018\u00010NX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bO\u0010P\"\u0004\bQ\u0010RR\u001a\u0010S\u001a\u00020*X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bT\u0010,\"\u0004\bU\u0010.R\u001a\u0010V\u001a\u00020WX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bX\u0010Y\"\u0004\bZ\u0010[R\u001c\u0010\\\u001a\u0004\u0018\u00010]X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b^\u0010_\"\u0004\b`\u0010aR\u001c\u0010b\u001a\u0004\u0018\u00010cX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bd\u0010e\"\u0004\bf\u0010gR \u0010h\u001a\b\u0012\u0004\u0012\u00020j0iX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bk\u0010\u0017\"\u0004\bl\u0010mR \u0010n\u001a\b\u0012\u0004\u0012\u00020o0iX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bp\u0010\u0017\"\u0004\bq\u0010mR\u001a\u0010r\u001a\u00020sX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bt\u0010u\"\u0004\bv\u0010wR\u001a\u0010x\u001a\u00020yX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bz\u0010{\"\u0004\b|\u0010}R \u0010~\u001a\u0004\u0018\u00010\u007fX\u0080\u000e¢\u0006\u0012\n\u0000\u001a\u0006\b\u0080\u0001\u0010\u0081\u0001\"\u0006\b\u0082\u0001\u0010\u0083\u0001R \u0010\u0084\u0001\u001a\u00030\u0085\u0001X\u0080\u000e¢\u0006\u0012\n\u0000\u001a\u0006\b\u0086\u0001\u0010\u0087\u0001\"\u0006\b\u0088\u0001\u0010\u0089\u0001R \u0010\u008a\u0001\u001a\u00030\u0085\u0001X\u0080\u000e¢\u0006\u0012\n\u0000\u001a\u0006\b\u008b\u0001\u0010\u0087\u0001\"\u0006\b\u008c\u0001\u0010\u0089\u0001R \u0010\u008d\u0001\u001a\u00030\u0085\u0001X\u0080\u000e¢\u0006\u0012\n\u0000\u001a\u0006\b\u008e\u0001\u0010\u0087\u0001\"\u0006\b\u008f\u0001\u0010\u0089\u0001R \u0010\u0090\u0001\u001a\u00030\u0085\u0001X\u0080\u000e¢\u0006\u0012\n\u0000\u001a\u0006\b\u0091\u0001\u0010\u0087\u0001\"\u0006\b\u0092\u0001\u0010\u0089\u0001R \u0010\u0093\u0001\u001a\u00030\u0085\u0001X\u0080\u000e¢\u0006\u0012\n\u0000\u001a\u0006\b\u0094\u0001\u0010\u0087\u0001\"\u0006\b\u0095\u0001\u0010\u0089\u0001R \u0010\u0096\u0001\u001a\u00030\u0085\u0001X\u0080\u000e¢\u0006\u0012\n\u0000\u001a\u0006\b\u0097\u0001\u0010\u0087\u0001\"\u0006\b\u0098\u0001\u0010\u0089\u0001R \u0010\u0099\u0001\u001a\u00030\u009a\u0001X\u0080\u000e¢\u0006\u0012\n\u0000\u001a\u0006\b\u009b\u0001\u0010\u009c\u0001\"\u0006\b\u009d\u0001\u0010\u009e\u0001R\"\u0010\u009f\u0001\u001a\u0005\u0018\u00010 \u0001X\u0080\u000e¢\u0006\u0012\n\u0000\u001a\u0006\b¡\u0001\u0010¢\u0001\"\u0006\b£\u0001\u0010¤\u0001R\"\u0010¥\u0001\u001a\u0005\u0018\u00010¦\u0001X\u0080\u000e¢\u0006\u0012\n\u0000\u001a\u0006\b§\u0001\u0010¨\u0001\"\u0006\b©\u0001\u0010ª\u0001\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006Í\u0001"},
      d2 = {"Lokhttp3/OkHttpClient$Builder;", "", "<init>", "()V", "okHttpClient", "Lokhttp3/OkHttpClient;", "(Lokhttp3/OkHttpClient;)V", "dispatcher", "Lokhttp3/Dispatcher;", "getDispatcher$okhttp", "()Lokhttp3/Dispatcher;", "setDispatcher$okhttp", "(Lokhttp3/Dispatcher;)V", "connectionPool", "Lokhttp3/ConnectionPool;", "getConnectionPool$okhttp", "()Lokhttp3/ConnectionPool;", "setConnectionPool$okhttp", "(Lokhttp3/ConnectionPool;)V", "interceptors", "", "Lokhttp3/Interceptor;", "getInterceptors$okhttp", "()Ljava/util/List;", "networkInterceptors", "getNetworkInterceptors$okhttp", "eventListenerFactory", "Lokhttp3/EventListener$Factory;", "getEventListenerFactory$okhttp", "()Lokhttp3/EventListener$Factory;", "setEventListenerFactory$okhttp", "(Lokhttp3/EventListener$Factory;)V", "retryOnConnectionFailure", "", "getRetryOnConnectionFailure$okhttp", "()Z", "setRetryOnConnectionFailure$okhttp", "(Z)V", "fastFallback", "getFastFallback$okhttp", "setFastFallback$okhttp", "authenticator", "Lokhttp3/Authenticator;", "getAuthenticator$okhttp", "()Lokhttp3/Authenticator;", "setAuthenticator$okhttp", "(Lokhttp3/Authenticator;)V", "followRedirects", "getFollowRedirects$okhttp", "setFollowRedirects$okhttp", "followSslRedirects", "getFollowSslRedirects$okhttp", "setFollowSslRedirects$okhttp", "cookieJar", "Lokhttp3/CookieJar;", "getCookieJar$okhttp", "()Lokhttp3/CookieJar;", "setCookieJar$okhttp", "(Lokhttp3/CookieJar;)V", "cache", "Lokhttp3/Cache;", "getCache$okhttp", "()Lokhttp3/Cache;", "setCache$okhttp", "(Lokhttp3/Cache;)V", "dns", "Lokhttp3/Dns;", "getDns$okhttp", "()Lokhttp3/Dns;", "setDns$okhttp", "(Lokhttp3/Dns;)V", "proxy", "Ljava/net/Proxy;", "getProxy$okhttp", "()Ljava/net/Proxy;", "setProxy$okhttp", "(Ljava/net/Proxy;)V", "proxySelector", "Ljava/net/ProxySelector;", "getProxySelector$okhttp", "()Ljava/net/ProxySelector;", "setProxySelector$okhttp", "(Ljava/net/ProxySelector;)V", "proxyAuthenticator", "getProxyAuthenticator$okhttp", "setProxyAuthenticator$okhttp", "socketFactory", "Ljavax/net/SocketFactory;", "getSocketFactory$okhttp", "()Ljavax/net/SocketFactory;", "setSocketFactory$okhttp", "(Ljavax/net/SocketFactory;)V", "sslSocketFactoryOrNull", "Ljavax/net/ssl/SSLSocketFactory;", "getSslSocketFactoryOrNull$okhttp", "()Ljavax/net/ssl/SSLSocketFactory;", "setSslSocketFactoryOrNull$okhttp", "(Ljavax/net/ssl/SSLSocketFactory;)V", "x509TrustManagerOrNull", "Ljavax/net/ssl/X509TrustManager;", "getX509TrustManagerOrNull$okhttp", "()Ljavax/net/ssl/X509TrustManager;", "setX509TrustManagerOrNull$okhttp", "(Ljavax/net/ssl/X509TrustManager;)V", "connectionSpecs", "", "Lokhttp3/ConnectionSpec;", "getConnectionSpecs$okhttp", "setConnectionSpecs$okhttp", "(Ljava/util/List;)V", "protocols", "Lokhttp3/Protocol;", "getProtocols$okhttp", "setProtocols$okhttp", "hostnameVerifier", "Ljavax/net/ssl/HostnameVerifier;", "getHostnameVerifier$okhttp", "()Ljavax/net/ssl/HostnameVerifier;", "setHostnameVerifier$okhttp", "(Ljavax/net/ssl/HostnameVerifier;)V", "certificatePinner", "Lokhttp3/CertificatePinner;", "getCertificatePinner$okhttp", "()Lokhttp3/CertificatePinner;", "setCertificatePinner$okhttp", "(Lokhttp3/CertificatePinner;)V", "certificateChainCleaner", "Lokhttp3/internal/tls/CertificateChainCleaner;", "getCertificateChainCleaner$okhttp", "()Lokhttp3/internal/tls/CertificateChainCleaner;", "setCertificateChainCleaner$okhttp", "(Lokhttp3/internal/tls/CertificateChainCleaner;)V", "callTimeout", "", "getCallTimeout$okhttp", "()I", "setCallTimeout$okhttp", "(I)V", "connectTimeout", "getConnectTimeout$okhttp", "setConnectTimeout$okhttp", "readTimeout", "getReadTimeout$okhttp", "setReadTimeout$okhttp", "writeTimeout", "getWriteTimeout$okhttp", "setWriteTimeout$okhttp", "pingInterval", "getPingInterval$okhttp", "setPingInterval$okhttp", "webSocketCloseTimeout", "getWebSocketCloseTimeout$okhttp", "setWebSocketCloseTimeout$okhttp", "minWebSocketMessageToCompress", "", "getMinWebSocketMessageToCompress$okhttp", "()J", "setMinWebSocketMessageToCompress$okhttp", "(J)V", "routeDatabase", "Lokhttp3/internal/connection/RouteDatabase;", "getRouteDatabase$okhttp", "()Lokhttp3/internal/connection/RouteDatabase;", "setRouteDatabase$okhttp", "(Lokhttp3/internal/connection/RouteDatabase;)V", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "getTaskRunner$okhttp", "()Lokhttp3/internal/concurrent/TaskRunner;", "setTaskRunner$okhttp", "(Lokhttp3/internal/concurrent/TaskRunner;)V", "addInterceptor", "interceptor", "block", "Lkotlin/Function1;", "Lokhttp3/Interceptor$Chain;", "Lkotlin/ParameterName;", "name", "chain", "Lokhttp3/Response;", "-addInterceptor", "addNetworkInterceptor", "-addNetworkInterceptor", "eventListener", "Lokhttp3/EventListener;", "followProtocolRedirects", "taskRunner$okhttp", "sslSocketFactory", "trustManager", "timeout", "unit", "Ljava/util/concurrent/TimeUnit;", "duration", "Ljava/time/Duration;", "Lkotlin/time/Duration;", "callTimeout-LRDsOJo", "(J)Lokhttp3/OkHttpClient$Builder;", "connectTimeout-LRDsOJo", "readTimeout-LRDsOJo", "writeTimeout-LRDsOJo", "interval", "pingInterval-LRDsOJo", "webSocketCloseTimeout-LRDsOJo", "bytes", "build", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nOkHttpClient.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OkHttpClient.kt\nokhttp3/OkHttpClient$Builder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 -UtilJvm.kt\nokhttp3/internal/_UtilJvmKt\n*L\n1#1,1388:1\n1#2:1389\n242#3:1390\n*S KotlinDebug\n*F\n+ 1 OkHttpClient.kt\nokhttp3/OkHttpClient$Builder\n*L\n1048#1:1390\n*E\n"})
   public static final class Builder {
      @NotNull
      private Dispatcher dispatcher;
      @Nullable
      private ConnectionPool connectionPool;
      @NotNull
      private final List<Interceptor> interceptors;
      @NotNull
      private final List<Interceptor> networkInterceptors;
      @NotNull
      private EventListener.Factory eventListenerFactory;
      private boolean retryOnConnectionFailure;
      private boolean fastFallback;
      @NotNull
      private Authenticator authenticator;
      private boolean followRedirects;
      private boolean followSslRedirects;
      @NotNull
      private CookieJar cookieJar;
      @Nullable
      private Cache cache;
      @NotNull
      private Dns dns;
      @Nullable
      private Proxy proxy;
      @Nullable
      private ProxySelector proxySelector;
      @NotNull
      private Authenticator proxyAuthenticator;
      @NotNull
      private SocketFactory socketFactory;
      @Nullable
      private SSLSocketFactory sslSocketFactoryOrNull;
      @Nullable
      private X509TrustManager x509TrustManagerOrNull;
      @NotNull
      private List<ConnectionSpec> connectionSpecs;
      @NotNull
      private List<? extends Protocol> protocols;
      @NotNull
      private HostnameVerifier hostnameVerifier;
      @NotNull
      private CertificatePinner certificatePinner;
      @Nullable
      private CertificateChainCleaner certificateChainCleaner;
      private int callTimeout;
      private int connectTimeout;
      private int readTimeout;
      private int writeTimeout;
      private int pingInterval;
      private int webSocketCloseTimeout;
      private long minWebSocketMessageToCompress;
      @Nullable
      private RouteDatabase routeDatabase;
      @Nullable
      private TaskRunner taskRunner;

      public Builder() {
         this.dispatcher = new Dispatcher();
         this.interceptors = (List)(new ArrayList());
         this.networkInterceptors = (List)(new ArrayList());
         this.eventListenerFactory = _UtilJvmKt.asFactory(EventListener.NONE);
         this.retryOnConnectionFailure = true;
         this.fastFallback = true;
         this.authenticator = Authenticator.NONE;
         this.followRedirects = true;
         this.followSslRedirects = true;
         this.cookieJar = CookieJar.NO_COOKIES;
         this.dns = Dns.SYSTEM;
         this.proxyAuthenticator = Authenticator.NONE;
         SocketFactory var10001 = SocketFactory.getDefault();
         Intrinsics.checkNotNullExpressionValue(var10001, "getDefault(...)");
         this.socketFactory = var10001;
         this.connectionSpecs = OkHttpClient.Companion.getDEFAULT_CONNECTION_SPECS$okhttp();
         this.protocols = OkHttpClient.Companion.getDEFAULT_PROTOCOLS$okhttp();
         this.hostnameVerifier = OkHostnameVerifier.INSTANCE;
         this.certificatePinner = CertificatePinner.DEFAULT;
         this.connectTimeout = 10000;
         this.readTimeout = 10000;
         this.writeTimeout = 10000;
         this.webSocketCloseTimeout = 60000;
         this.minWebSocketMessageToCompress = 1024L;
      }

      @NotNull
      public final Dispatcher getDispatcher$okhttp() {
         return this.dispatcher;
      }

      public final void setDispatcher$okhttp(@NotNull Dispatcher var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.dispatcher = var1;
      }

      @Nullable
      public final ConnectionPool getConnectionPool$okhttp() {
         return this.connectionPool;
      }

      public final void setConnectionPool$okhttp(@Nullable ConnectionPool var1) {
         this.connectionPool = var1;
      }

      @NotNull
      public final List<Interceptor> getInterceptors$okhttp() {
         return this.interceptors;
      }

      @NotNull
      public final List<Interceptor> getNetworkInterceptors$okhttp() {
         return this.networkInterceptors;
      }

      @NotNull
      public final EventListener.Factory getEventListenerFactory$okhttp() {
         return this.eventListenerFactory;
      }

      public final void setEventListenerFactory$okhttp(@NotNull EventListener.Factory var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.eventListenerFactory = var1;
      }

      public final boolean getRetryOnConnectionFailure$okhttp() {
         return this.retryOnConnectionFailure;
      }

      public final void setRetryOnConnectionFailure$okhttp(boolean var1) {
         this.retryOnConnectionFailure = var1;
      }

      public final boolean getFastFallback$okhttp() {
         return this.fastFallback;
      }

      public final void setFastFallback$okhttp(boolean var1) {
         this.fastFallback = var1;
      }

      @NotNull
      public final Authenticator getAuthenticator$okhttp() {
         return this.authenticator;
      }

      public final void setAuthenticator$okhttp(@NotNull Authenticator var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.authenticator = var1;
      }

      public final boolean getFollowRedirects$okhttp() {
         return this.followRedirects;
      }

      public final void setFollowRedirects$okhttp(boolean var1) {
         this.followRedirects = var1;
      }

      public final boolean getFollowSslRedirects$okhttp() {
         return this.followSslRedirects;
      }

      public final void setFollowSslRedirects$okhttp(boolean var1) {
         this.followSslRedirects = var1;
      }

      @NotNull
      public final CookieJar getCookieJar$okhttp() {
         return this.cookieJar;
      }

      public final void setCookieJar$okhttp(@NotNull CookieJar var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.cookieJar = var1;
      }

      @Nullable
      public final Cache getCache$okhttp() {
         return this.cache;
      }

      public final void setCache$okhttp(@Nullable Cache var1) {
         this.cache = var1;
      }

      @NotNull
      public final Dns getDns$okhttp() {
         return this.dns;
      }

      public final void setDns$okhttp(@NotNull Dns var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.dns = var1;
      }

      @Nullable
      public final Proxy getProxy$okhttp() {
         return this.proxy;
      }

      public final void setProxy$okhttp(@Nullable Proxy var1) {
         this.proxy = var1;
      }

      @Nullable
      public final ProxySelector getProxySelector$okhttp() {
         return this.proxySelector;
      }

      public final void setProxySelector$okhttp(@Nullable ProxySelector var1) {
         this.proxySelector = var1;
      }

      @NotNull
      public final Authenticator getProxyAuthenticator$okhttp() {
         return this.proxyAuthenticator;
      }

      public final void setProxyAuthenticator$okhttp(@NotNull Authenticator var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.proxyAuthenticator = var1;
      }

      @NotNull
      public final SocketFactory getSocketFactory$okhttp() {
         return this.socketFactory;
      }

      public final void setSocketFactory$okhttp(@NotNull SocketFactory var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.socketFactory = var1;
      }

      @Nullable
      public final SSLSocketFactory getSslSocketFactoryOrNull$okhttp() {
         return this.sslSocketFactoryOrNull;
      }

      public final void setSslSocketFactoryOrNull$okhttp(@Nullable SSLSocketFactory var1) {
         this.sslSocketFactoryOrNull = var1;
      }

      @Nullable
      public final X509TrustManager getX509TrustManagerOrNull$okhttp() {
         return this.x509TrustManagerOrNull;
      }

      public final void setX509TrustManagerOrNull$okhttp(@Nullable X509TrustManager var1) {
         this.x509TrustManagerOrNull = var1;
      }

      @NotNull
      public final List<ConnectionSpec> getConnectionSpecs$okhttp() {
         return this.connectionSpecs;
      }

      public final void setConnectionSpecs$okhttp(@NotNull List<ConnectionSpec> var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.connectionSpecs = var1;
      }

      @NotNull
      public final List<Protocol> getProtocols$okhttp() {
         return this.protocols;
      }

      public final void setProtocols$okhttp(@NotNull List<? extends Protocol> var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.protocols = var1;
      }

      @NotNull
      public final HostnameVerifier getHostnameVerifier$okhttp() {
         return this.hostnameVerifier;
      }

      public final void setHostnameVerifier$okhttp(@NotNull HostnameVerifier var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.hostnameVerifier = var1;
      }

      @NotNull
      public final CertificatePinner getCertificatePinner$okhttp() {
         return this.certificatePinner;
      }

      public final void setCertificatePinner$okhttp(@NotNull CertificatePinner var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.certificatePinner = var1;
      }

      @Nullable
      public final CertificateChainCleaner getCertificateChainCleaner$okhttp() {
         return this.certificateChainCleaner;
      }

      public final void setCertificateChainCleaner$okhttp(@Nullable CertificateChainCleaner var1) {
         this.certificateChainCleaner = var1;
      }

      public final int getCallTimeout$okhttp() {
         return this.callTimeout;
      }

      public final void setCallTimeout$okhttp(int var1) {
         this.callTimeout = var1;
      }

      public final int getConnectTimeout$okhttp() {
         return this.connectTimeout;
      }

      public final void setConnectTimeout$okhttp(int var1) {
         this.connectTimeout = var1;
      }

      public final int getReadTimeout$okhttp() {
         return this.readTimeout;
      }

      public final void setReadTimeout$okhttp(int var1) {
         this.readTimeout = var1;
      }

      public final int getWriteTimeout$okhttp() {
         return this.writeTimeout;
      }

      public final void setWriteTimeout$okhttp(int var1) {
         this.writeTimeout = var1;
      }

      public final int getPingInterval$okhttp() {
         return this.pingInterval;
      }

      public final void setPingInterval$okhttp(int var1) {
         this.pingInterval = var1;
      }

      public final int getWebSocketCloseTimeout$okhttp() {
         return this.webSocketCloseTimeout;
      }

      public final void setWebSocketCloseTimeout$okhttp(int var1) {
         this.webSocketCloseTimeout = var1;
      }

      public final long getMinWebSocketMessageToCompress$okhttp() {
         return this.minWebSocketMessageToCompress;
      }

      public final void setMinWebSocketMessageToCompress$okhttp(long var1) {
         this.minWebSocketMessageToCompress = var1;
      }

      @Nullable
      public final RouteDatabase getRouteDatabase$okhttp() {
         return this.routeDatabase;
      }

      public final void setRouteDatabase$okhttp(@Nullable RouteDatabase var1) {
         this.routeDatabase = var1;
      }

      @Nullable
      public final TaskRunner getTaskRunner$okhttp() {
         return this.taskRunner;
      }

      public final void setTaskRunner$okhttp(@Nullable TaskRunner var1) {
         this.taskRunner = var1;
      }

      public Builder(@NotNull OkHttpClient okHttpClient) {
         Intrinsics.checkNotNullParameter(okHttpClient, "okHttpClient");
         this();
         this.dispatcher = okHttpClient.dispatcher();
         this.connectionPool = okHttpClient.connectionPool();
         CollectionsKt.addAll((Collection)this.interceptors, (Iterable)okHttpClient.interceptors());
         CollectionsKt.addAll((Collection)this.networkInterceptors, (Iterable)okHttpClient.networkInterceptors());
         this.eventListenerFactory = okHttpClient.eventListenerFactory();
         this.retryOnConnectionFailure = okHttpClient.retryOnConnectionFailure();
         this.fastFallback = okHttpClient.fastFallback();
         this.authenticator = okHttpClient.authenticator();
         this.followRedirects = okHttpClient.followRedirects();
         this.followSslRedirects = okHttpClient.followSslRedirects();
         this.cookieJar = okHttpClient.cookieJar();
         this.cache = okHttpClient.cache();
         this.dns = okHttpClient.dns();
         this.proxy = okHttpClient.proxy();
         this.proxySelector = okHttpClient.proxySelector();
         this.proxyAuthenticator = okHttpClient.proxyAuthenticator();
         this.socketFactory = okHttpClient.socketFactory();
         this.sslSocketFactoryOrNull = okHttpClient.sslSocketFactoryOrNull;
         this.x509TrustManagerOrNull = okHttpClient.x509TrustManager();
         this.connectionSpecs = okHttpClient.connectionSpecs();
         this.protocols = okHttpClient.protocols();
         this.hostnameVerifier = okHttpClient.hostnameVerifier();
         this.certificatePinner = okHttpClient.certificatePinner();
         this.certificateChainCleaner = okHttpClient.certificateChainCleaner();
         this.callTimeout = okHttpClient.callTimeoutMillis();
         this.connectTimeout = okHttpClient.connectTimeoutMillis();
         this.readTimeout = okHttpClient.readTimeoutMillis();
         this.writeTimeout = okHttpClient.writeTimeoutMillis();
         this.pingInterval = okHttpClient.pingIntervalMillis();
         this.webSocketCloseTimeout = okHttpClient.webSocketCloseTimeout();
         this.minWebSocketMessageToCompress = okHttpClient.minWebSocketMessageToCompress();
         this.routeDatabase = okHttpClient.getRouteDatabase$okhttp();
         this.taskRunner = okHttpClient.getTaskRunner$okhttp();
      }

      @NotNull
      public final Builder dispatcher(@NotNull Dispatcher dispatcher) {
         Intrinsics.checkNotNullParameter(dispatcher, "dispatcher");
         Builder $this$dispatcher_u24lambda_u240 = this;
         int var4 = 0;
         $this$dispatcher_u24lambda_u240.dispatcher = dispatcher;
         return this;
      }

      @NotNull
      public final Builder connectionPool(@NotNull ConnectionPool connectionPool) {
         Intrinsics.checkNotNullParameter(connectionPool, "connectionPool");
         Builder $this$connectionPool_u24lambda_u240 = this;
         int var4 = 0;
         $this$connectionPool_u24lambda_u240.connectionPool = connectionPool;
         return this;
      }

      @NotNull
      public final List<Interceptor> interceptors() {
         return this.interceptors;
      }

      @NotNull
      public final Builder addInterceptor(@NotNull Interceptor interceptor) {
         Intrinsics.checkNotNullParameter(interceptor, "interceptor");
         Builder $this$addInterceptor_u24lambda_u240 = this;
         int var4 = 0;
         ((Collection)$this$addInterceptor_u24lambda_u240.interceptors).add(interceptor);
         return this;
      }

      @JvmName(
         name = "-addInterceptor"
      )
      @NotNull
      public final Builder _addInterceptor/* $FF was: -addInterceptor*/(@NotNull final Function1<? super Interceptor.Chain, Response> block) {
         Intrinsics.checkNotNullParameter(block, "block");
         int var2 = 0;
         return this.addInterceptor(new Interceptor() {
            public final Response intercept(Interceptor.Chain chain) {
               Intrinsics.checkNotNullParameter(chain, "chain");
               return (Response)block.invoke(chain);
            }
         });
      }

      @NotNull
      public final List<Interceptor> networkInterceptors() {
         return this.networkInterceptors;
      }

      @NotNull
      public final Builder addNetworkInterceptor(@NotNull Interceptor interceptor) {
         Intrinsics.checkNotNullParameter(interceptor, "interceptor");
         Builder $this$addNetworkInterceptor_u24lambda_u240 = this;
         int var4 = 0;
         ((Collection)$this$addNetworkInterceptor_u24lambda_u240.networkInterceptors).add(interceptor);
         return this;
      }

      @JvmName(
         name = "-addNetworkInterceptor"
      )
      @NotNull
      public final Builder _addNetworkInterceptor/* $FF was: -addNetworkInterceptor*/(@NotNull final Function1<? super Interceptor.Chain, Response> block) {
         Intrinsics.checkNotNullParameter(block, "block");
         int var2 = 0;
         return this.addNetworkInterceptor(new Interceptor() {
            public final Response intercept(Interceptor.Chain chain) {
               Intrinsics.checkNotNullParameter(chain, "chain");
               return (Response)block.invoke(chain);
            }
         });
      }

      @NotNull
      public final Builder eventListener(@NotNull EventListener eventListener) {
         Intrinsics.checkNotNullParameter(eventListener, "eventListener");
         Builder $this$eventListener_u24lambda_u240 = this;
         int var4 = 0;
         $this$eventListener_u24lambda_u240.eventListenerFactory = _UtilJvmKt.asFactory(eventListener);
         return this;
      }

      @NotNull
      public final Builder eventListenerFactory(@NotNull EventListener.Factory eventListenerFactory) {
         Intrinsics.checkNotNullParameter(eventListenerFactory, "eventListenerFactory");
         Builder $this$eventListenerFactory_u24lambda_u240 = this;
         int var4 = 0;
         $this$eventListenerFactory_u24lambda_u240.eventListenerFactory = eventListenerFactory;
         return this;
      }

      @NotNull
      public final Builder retryOnConnectionFailure(boolean retryOnConnectionFailure) {
         Builder $this$retryOnConnectionFailure_u24lambda_u240 = this;
         int var4 = 0;
         $this$retryOnConnectionFailure_u24lambda_u240.retryOnConnectionFailure = retryOnConnectionFailure;
         return this;
      }

      @NotNull
      public final Builder fastFallback(boolean fastFallback) {
         Builder $this$fastFallback_u24lambda_u240 = this;
         int var4 = 0;
         $this$fastFallback_u24lambda_u240.fastFallback = fastFallback;
         return this;
      }

      @NotNull
      public final Builder authenticator(@NotNull Authenticator authenticator) {
         Intrinsics.checkNotNullParameter(authenticator, "authenticator");
         Builder $this$authenticator_u24lambda_u240 = this;
         int var4 = 0;
         $this$authenticator_u24lambda_u240.authenticator = authenticator;
         return this;
      }

      @NotNull
      public final Builder followRedirects(boolean followRedirects) {
         Builder $this$followRedirects_u24lambda_u240 = this;
         int var4 = 0;
         $this$followRedirects_u24lambda_u240.followRedirects = followRedirects;
         return this;
      }

      @NotNull
      public final Builder followSslRedirects(boolean followProtocolRedirects) {
         Builder $this$followSslRedirects_u24lambda_u240 = this;
         int var4 = 0;
         $this$followSslRedirects_u24lambda_u240.followSslRedirects = followProtocolRedirects;
         return this;
      }

      @NotNull
      public final Builder cookieJar(@NotNull CookieJar cookieJar) {
         Intrinsics.checkNotNullParameter(cookieJar, "cookieJar");
         Builder $this$cookieJar_u24lambda_u240 = this;
         int var4 = 0;
         $this$cookieJar_u24lambda_u240.cookieJar = cookieJar;
         return this;
      }

      @NotNull
      public final Builder cache(@Nullable Cache cache) {
         Builder $this$cache_u24lambda_u240 = this;
         int var4 = 0;
         $this$cache_u24lambda_u240.cache = cache;
         return this;
      }

      @NotNull
      public final Builder taskRunner$okhttp(@NotNull TaskRunner taskRunner) {
         Intrinsics.checkNotNullParameter(taskRunner, "taskRunner");
         Builder $this$taskRunner_u24lambda_u240 = this;
         int var4 = 0;
         $this$taskRunner_u24lambda_u240.taskRunner = taskRunner;
         return this;
      }

      @NotNull
      public final Builder dns(@NotNull Dns dns) {
         Intrinsics.checkNotNullParameter(dns, "dns");
         Builder $this$dns_u24lambda_u240 = this;
         int var4 = 0;
         if (!Intrinsics.areEqual((Object)dns, (Object)$this$dns_u24lambda_u240.dns)) {
            $this$dns_u24lambda_u240.routeDatabase = null;
         }

         $this$dns_u24lambda_u240.dns = dns;
         return this;
      }

      @NotNull
      public final Builder proxy(@Nullable Proxy proxy) {
         Builder $this$proxy_u24lambda_u240 = this;
         int var4 = 0;
         if (!Intrinsics.areEqual((Object)proxy, (Object)$this$proxy_u24lambda_u240.proxy)) {
            $this$proxy_u24lambda_u240.routeDatabase = null;
         }

         $this$proxy_u24lambda_u240.proxy = proxy;
         return this;
      }

      @NotNull
      public final Builder proxySelector(@NotNull ProxySelector proxySelector) {
         Intrinsics.checkNotNullParameter(proxySelector, "proxySelector");
         Builder $this$proxySelector_u24lambda_u240 = this;
         int var4 = 0;
         if (!Intrinsics.areEqual((Object)proxySelector, (Object)$this$proxySelector_u24lambda_u240.proxySelector)) {
            $this$proxySelector_u24lambda_u240.routeDatabase = null;
         }

         $this$proxySelector_u24lambda_u240.proxySelector = proxySelector;
         return this;
      }

      @NotNull
      public final Builder proxyAuthenticator(@NotNull Authenticator proxyAuthenticator) {
         Intrinsics.checkNotNullParameter(proxyAuthenticator, "proxyAuthenticator");
         Builder $this$proxyAuthenticator_u24lambda_u240 = this;
         int var4 = 0;
         if (!Intrinsics.areEqual((Object)proxyAuthenticator, (Object)$this$proxyAuthenticator_u24lambda_u240.proxyAuthenticator)) {
            $this$proxyAuthenticator_u24lambda_u240.routeDatabase = null;
         }

         $this$proxyAuthenticator_u24lambda_u240.proxyAuthenticator = proxyAuthenticator;
         return this;
      }

      @NotNull
      public final Builder socketFactory(@NotNull SocketFactory socketFactory) {
         Intrinsics.checkNotNullParameter(socketFactory, "socketFactory");
         Builder $this$socketFactory_u24lambda_u240 = this;
         int var4 = 0;
         if (socketFactory instanceof SSLSocketFactory) {
            int var5 = 0;
            String var6 = "socketFactory instanceof SSLSocketFactory";
            throw new IllegalArgumentException(var6.toString());
         } else {
            if (!Intrinsics.areEqual((Object)socketFactory, (Object)$this$socketFactory_u24lambda_u240.socketFactory)) {
               $this$socketFactory_u24lambda_u240.routeDatabase = null;
            }

            $this$socketFactory_u24lambda_u240.socketFactory = socketFactory;
            return this;
         }
      }

      /** @deprecated */
      @Deprecated(
         message = "Use the sslSocketFactory overload that accepts a X509TrustManager.",
         level = DeprecationLevel.ERROR
      )
      @NotNull
      public final Builder sslSocketFactory(@NotNull SSLSocketFactory sslSocketFactory) {
         Intrinsics.checkNotNullParameter(sslSocketFactory, "sslSocketFactory");
         Builder $this$sslSocketFactory_u24lambda_u240 = this;
         int var4 = 0;
         if (!Intrinsics.areEqual((Object)sslSocketFactory, (Object)$this$sslSocketFactory_u24lambda_u240.sslSocketFactoryOrNull)) {
            $this$sslSocketFactory_u24lambda_u240.routeDatabase = null;
         }

         $this$sslSocketFactory_u24lambda_u240.sslSocketFactoryOrNull = sslSocketFactory;
         X509TrustManager var10001 = Platform.Companion.get().trustManager(sslSocketFactory);
         if (var10001 == null) {
            throw new IllegalStateException("Unable to extract the trust manager on " + Platform.Companion.get() + ", sslSocketFactory is " + sslSocketFactory.getClass());
         } else {
            $this$sslSocketFactory_u24lambda_u240.x509TrustManagerOrNull = var10001;
            Platform var5 = Platform.Companion.get();
            X509TrustManager var10002 = $this$sslSocketFactory_u24lambda_u240.x509TrustManagerOrNull;
            Intrinsics.checkNotNull(var10002);
            $this$sslSocketFactory_u24lambda_u240.certificateChainCleaner = var5.buildCertificateChainCleaner(var10002);
            return this;
         }
      }

      @NotNull
      public final Builder sslSocketFactory(@NotNull SSLSocketFactory sslSocketFactory, @NotNull X509TrustManager trustManager) {
         Intrinsics.checkNotNullParameter(sslSocketFactory, "sslSocketFactory");
         Intrinsics.checkNotNullParameter(trustManager, "trustManager");
         Builder $this$sslSocketFactory_u24lambda_u241 = this;
         int var5 = 0;
         if (!Intrinsics.areEqual((Object)sslSocketFactory, (Object)$this$sslSocketFactory_u24lambda_u241.sslSocketFactoryOrNull) || !Intrinsics.areEqual((Object)trustManager, (Object)$this$sslSocketFactory_u24lambda_u241.x509TrustManagerOrNull)) {
            $this$sslSocketFactory_u24lambda_u241.routeDatabase = null;
         }

         $this$sslSocketFactory_u24lambda_u241.sslSocketFactoryOrNull = sslSocketFactory;
         $this$sslSocketFactory_u24lambda_u241.certificateChainCleaner = CertificateChainCleaner.Companion.get(trustManager);
         $this$sslSocketFactory_u24lambda_u241.x509TrustManagerOrNull = trustManager;
         return this;
      }

      @NotNull
      public final Builder connectionSpecs(@NotNull List<ConnectionSpec> connectionSpecs) {
         Intrinsics.checkNotNullParameter(connectionSpecs, "connectionSpecs");
         Builder $this$connectionSpecs_u24lambda_u240 = this;
         int var4 = 0;
         if (!Intrinsics.areEqual((Object)connectionSpecs, (Object)$this$connectionSpecs_u24lambda_u240.connectionSpecs)) {
            $this$connectionSpecs_u24lambda_u240.routeDatabase = null;
         }

         $this$connectionSpecs_u24lambda_u240.connectionSpecs = _UtilJvmKt.toImmutableList(connectionSpecs);
         return this;
      }

      @NotNull
      public final Builder protocols(@NotNull List<? extends Protocol> protocols) {
         Intrinsics.checkNotNullParameter(protocols, "protocols");
         Builder $this$protocols_u24lambda_u240 = this;
         int var4 = 0;
         List protocolsCopy = CollectionsKt.toMutableList((Collection)protocols);
         if (!protocolsCopy.contains(Protocol.H2_PRIOR_KNOWLEDGE) && !protocolsCopy.contains(Protocol.HTTP_1_1)) {
            int var14 = 0;
            String var15 = "protocols must contain h2_prior_knowledge or http/1.1: " + protocolsCopy;
            throw new IllegalArgumentException(var15.toString());
         } else if (protocolsCopy.contains(Protocol.H2_PRIOR_KNOWLEDGE) && protocolsCopy.size() > 1) {
            int var12 = 0;
            String var13 = "protocols containing h2_prior_knowledge cannot use other protocols: " + protocolsCopy;
            throw new IllegalArgumentException(var13.toString());
         } else if (protocolsCopy.contains(Protocol.HTTP_1_0)) {
            int var10 = 0;
            String $i$f$unmodifiable = "protocols must not contain http/1.0: " + protocolsCopy;
            throw new IllegalArgumentException($i$f$unmodifiable.toString());
         } else {
            Intrinsics.checkNotNull(protocolsCopy, "null cannot be cast to non-null type kotlin.collections.List<okhttp3.Protocol?>");
            if (protocolsCopy.contains((Object)null)) {
               int var8 = 0;
               String var9 = "protocols must not contain null";
               throw new IllegalArgumentException(var9.toString());
            } else {
               protocolsCopy.remove(Protocol.SPDY_3);
               if (!Intrinsics.areEqual((Object)protocolsCopy, (Object)$this$protocols_u24lambda_u240.protocols)) {
                  $this$protocols_u24lambda_u240.routeDatabase = null;
               }

               int $i$f$unmodifiable = 0;
               List var10001 = Collections.unmodifiableList(protocolsCopy);
               Intrinsics.checkNotNullExpressionValue(var10001, "unmodifiableList(...)");
               $this$protocols_u24lambda_u240.protocols = var10001;
               return this;
            }
         }
      }

      @NotNull
      public final Builder hostnameVerifier(@NotNull HostnameVerifier hostnameVerifier) {
         Intrinsics.checkNotNullParameter(hostnameVerifier, "hostnameVerifier");
         Builder $this$hostnameVerifier_u24lambda_u240 = this;
         int var4 = 0;
         if (!Intrinsics.areEqual((Object)hostnameVerifier, (Object)$this$hostnameVerifier_u24lambda_u240.hostnameVerifier)) {
            $this$hostnameVerifier_u24lambda_u240.routeDatabase = null;
         }

         $this$hostnameVerifier_u24lambda_u240.hostnameVerifier = hostnameVerifier;
         return this;
      }

      @NotNull
      public final Builder certificatePinner(@NotNull CertificatePinner certificatePinner) {
         Intrinsics.checkNotNullParameter(certificatePinner, "certificatePinner");
         Builder $this$certificatePinner_u24lambda_u240 = this;
         int var4 = 0;
         if (!Intrinsics.areEqual((Object)certificatePinner, (Object)$this$certificatePinner_u24lambda_u240.certificatePinner)) {
            $this$certificatePinner_u24lambda_u240.routeDatabase = null;
         }

         $this$certificatePinner_u24lambda_u240.certificatePinner = certificatePinner;
         return this;
      }

      @NotNull
      public final Builder callTimeout(long timeout, @NotNull TimeUnit unit) {
         Intrinsics.checkNotNullParameter(unit, "unit");
         Builder $this$callTimeout_u24lambda_u240 = this;
         int var6 = 0;
         $this$callTimeout_u24lambda_u240.callTimeout = _UtilJvmKt.checkDuration("timeout", timeout, unit);
         return this;
      }

      @IgnoreJRERequirement
      @NotNull
      public final Builder callTimeout(@NotNull Duration duration) {
         Intrinsics.checkNotNullParameter(duration, "duration");
         Builder $this$callTimeout_u24lambda_u241 = this;
         int var4 = 0;
         $this$callTimeout_u24lambda_u241.callTimeout(duration.toMillis(), TimeUnit.MILLISECONDS);
         return this;
      }

      @NotNull
      public final Builder callTimeout_LRDsOJo/* $FF was: callTimeout-LRDsOJo*/(long var1) {
         Builder $this$callTimeout_LRDsOJo_u24lambda_u240 = this;
         int var5 = 0;
         $this$callTimeout_LRDsOJo_u24lambda_u240.callTimeout = _UtilJvmKt.checkDuration-HG0u8IE("duration", var1);
         return this;
      }

      @NotNull
      public final Builder connectTimeout(long timeout, @NotNull TimeUnit unit) {
         Intrinsics.checkNotNullParameter(unit, "unit");
         Builder $this$connectTimeout_u24lambda_u240 = this;
         int var6 = 0;
         $this$connectTimeout_u24lambda_u240.connectTimeout = _UtilJvmKt.checkDuration("timeout", timeout, unit);
         return this;
      }

      @IgnoreJRERequirement
      @NotNull
      public final Builder connectTimeout(@NotNull Duration duration) {
         Intrinsics.checkNotNullParameter(duration, "duration");
         Builder $this$connectTimeout_u24lambda_u241 = this;
         int var4 = 0;
         $this$connectTimeout_u24lambda_u241.connectTimeout(duration.toMillis(), TimeUnit.MILLISECONDS);
         return this;
      }

      @NotNull
      public final Builder connectTimeout_LRDsOJo/* $FF was: connectTimeout-LRDsOJo*/(long var1) {
         Builder $this$connectTimeout_LRDsOJo_u24lambda_u240 = this;
         int var5 = 0;
         $this$connectTimeout_LRDsOJo_u24lambda_u240.connectTimeout = _UtilJvmKt.checkDuration-HG0u8IE("duration", var1);
         return this;
      }

      @NotNull
      public final Builder readTimeout(long timeout, @NotNull TimeUnit unit) {
         Intrinsics.checkNotNullParameter(unit, "unit");
         Builder $this$readTimeout_u24lambda_u240 = this;
         int var6 = 0;
         $this$readTimeout_u24lambda_u240.readTimeout = _UtilJvmKt.checkDuration("timeout", timeout, unit);
         return this;
      }

      @IgnoreJRERequirement
      @NotNull
      public final Builder readTimeout(@NotNull Duration duration) {
         Intrinsics.checkNotNullParameter(duration, "duration");
         Builder $this$readTimeout_u24lambda_u241 = this;
         int var4 = 0;
         $this$readTimeout_u24lambda_u241.readTimeout(duration.toMillis(), TimeUnit.MILLISECONDS);
         return this;
      }

      @NotNull
      public final Builder readTimeout_LRDsOJo/* $FF was: readTimeout-LRDsOJo*/(long var1) {
         Builder $this$readTimeout_LRDsOJo_u24lambda_u240 = this;
         int var5 = 0;
         $this$readTimeout_LRDsOJo_u24lambda_u240.readTimeout = _UtilJvmKt.checkDuration-HG0u8IE("duration", var1);
         return this;
      }

      @NotNull
      public final Builder writeTimeout(long timeout, @NotNull TimeUnit unit) {
         Intrinsics.checkNotNullParameter(unit, "unit");
         Builder $this$writeTimeout_u24lambda_u240 = this;
         int var6 = 0;
         $this$writeTimeout_u24lambda_u240.writeTimeout = _UtilJvmKt.checkDuration("timeout", timeout, unit);
         return this;
      }

      @IgnoreJRERequirement
      @NotNull
      public final Builder writeTimeout(@NotNull Duration duration) {
         Intrinsics.checkNotNullParameter(duration, "duration");
         Builder $this$writeTimeout_u24lambda_u241 = this;
         int var4 = 0;
         $this$writeTimeout_u24lambda_u241.writeTimeout(duration.toMillis(), TimeUnit.MILLISECONDS);
         return this;
      }

      @NotNull
      public final Builder writeTimeout_LRDsOJo/* $FF was: writeTimeout-LRDsOJo*/(long var1) {
         Builder $this$writeTimeout_LRDsOJo_u24lambda_u240 = this;
         int var5 = 0;
         $this$writeTimeout_LRDsOJo_u24lambda_u240.writeTimeout = _UtilJvmKt.checkDuration-HG0u8IE("duration", var1);
         return this;
      }

      @NotNull
      public final Builder pingInterval(long interval, @NotNull TimeUnit unit) {
         Intrinsics.checkNotNullParameter(unit, "unit");
         Builder $this$pingInterval_u24lambda_u240 = this;
         int var6 = 0;
         $this$pingInterval_u24lambda_u240.pingInterval = _UtilJvmKt.checkDuration("interval", interval, unit);
         return this;
      }

      @IgnoreJRERequirement
      @NotNull
      public final Builder pingInterval(@NotNull Duration duration) {
         Intrinsics.checkNotNullParameter(duration, "duration");
         Builder $this$pingInterval_u24lambda_u241 = this;
         int var4 = 0;
         $this$pingInterval_u24lambda_u241.pingInterval(duration.toMillis(), TimeUnit.MILLISECONDS);
         return this;
      }

      @NotNull
      public final Builder pingInterval_LRDsOJo/* $FF was: pingInterval-LRDsOJo*/(long var1) {
         Builder $this$pingInterval_LRDsOJo_u24lambda_u240 = this;
         int var5 = 0;
         $this$pingInterval_LRDsOJo_u24lambda_u240.pingInterval = _UtilJvmKt.checkDuration-HG0u8IE("duration", var1);
         return this;
      }

      @NotNull
      public final Builder webSocketCloseTimeout(long timeout, @NotNull TimeUnit unit) {
         Intrinsics.checkNotNullParameter(unit, "unit");
         Builder $this$webSocketCloseTimeout_u24lambda_u240 = this;
         int var6 = 0;
         $this$webSocketCloseTimeout_u24lambda_u240.webSocketCloseTimeout = _UtilJvmKt.checkDuration("webSocketCloseTimeout", timeout, unit);
         return this;
      }

      @IgnoreJRERequirement
      @NotNull
      public final Builder webSocketCloseTimeout(@NotNull Duration duration) {
         Intrinsics.checkNotNullParameter(duration, "duration");
         Builder $this$webSocketCloseTimeout_u24lambda_u241 = this;
         int var4 = 0;
         $this$webSocketCloseTimeout_u24lambda_u241.webSocketCloseTimeout(duration.toMillis(), TimeUnit.MILLISECONDS);
         return this;
      }

      @NotNull
      public final Builder webSocketCloseTimeout_LRDsOJo/* $FF was: webSocketCloseTimeout-LRDsOJo*/(long var1) {
         Builder $this$webSocketCloseTimeout_LRDsOJo_u24lambda_u240 = this;
         int var5 = 0;
         $this$webSocketCloseTimeout_LRDsOJo_u24lambda_u240.webSocketCloseTimeout = _UtilJvmKt.checkDuration-HG0u8IE("duration", var1);
         return this;
      }

      @NotNull
      public final Builder minWebSocketMessageToCompress(long bytes) {
         Builder $this$minWebSocketMessageToCompress_u24lambda_u240 = this;
         int var5 = 0;
         if (bytes < 0L) {
            int var6 = 0;
            String var7 = "minWebSocketMessageToCompress must be positive: " + bytes;
            throw new IllegalArgumentException(var7.toString());
         } else {
            $this$minWebSocketMessageToCompress_u24lambda_u240.minWebSocketMessageToCompress = bytes;
            return this;
         }
      }

      @NotNull
      public final OkHttpClient build() {
         return new OkHttpClient(this);
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u001a\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u001a\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0005X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\b¨\u0006\f"},
      d2 = {"Lokhttp3/OkHttpClient$Companion;", "", "<init>", "()V", "DEFAULT_PROTOCOLS", "", "Lokhttp3/Protocol;", "getDEFAULT_PROTOCOLS$okhttp", "()Ljava/util/List;", "DEFAULT_CONNECTION_SPECS", "Lokhttp3/ConnectionSpec;", "getDEFAULT_CONNECTION_SPECS$okhttp", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final List<Protocol> getDEFAULT_PROTOCOLS$okhttp() {
         return OkHttpClient.DEFAULT_PROTOCOLS;
      }

      @NotNull
      public final List<ConnectionSpec> getDEFAULT_CONNECTION_SPECS$okhttp() {
         return OkHttpClient.DEFAULT_CONNECTION_SPECS;
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
