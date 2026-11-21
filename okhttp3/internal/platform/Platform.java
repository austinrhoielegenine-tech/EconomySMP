package okhttp3.internal.platform;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.ExtendedSSLSession;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.internal._UtilJvmKt;
import okhttp3.internal.publicsuffix.PublicSuffixDatabase;
import okhttp3.internal.tls.BasicCertificateChainCleaner;
import okhttp3.internal.tls.BasicTrustRootIndex;
import okhttp3.internal.tls.CertificateChainCleaner;
import okhttp3.internal.tls.TrustRootIndex;
import okio.Buffer;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000t\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0003\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0016\u0018\u0000 12\u00020\u0001:\u00011B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0004\u001a\u00020\u0005J\b\u0010\u0006\u001a\u00020\u0007H\u0016J\b\u0010\b\u001a\u00020\tH\u0016J\u0012\u0010\n\u001a\u0004\u0018\u00010\t2\u0006\u0010\u000b\u001a\u00020\fH\u0016J-\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u00052\u0011\u0010\u0012\u001a\r\u0012\t\u0012\u00070\u0014¢\u0006\u0002\b\u00150\u0013H\u0016J\u0010\u0010\u0016\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0016J\u0012\u0010\u0017\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u000f\u001a\u00020\u0010H\u0016J\u0016\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00050\u00132\u0006\u0010\u000f\u001a\u00020\u0010H\u0017J \u0010\u0019\u001a\u00020\u000e2\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fH\u0016J&\u0010 \u001a\u00020\u000e2\u0006\u0010!\u001a\u00020\u00052\b\b\u0002\u0010\"\u001a\u00020\u001f2\n\b\u0002\u0010#\u001a\u0004\u0018\u00010$H\u0016J\u0010\u0010%\u001a\u00020&2\u0006\u0010\u0011\u001a\u00020\u0005H\u0016J\u0012\u0010'\u001a\u0004\u0018\u00010\u00012\u0006\u0010(\u001a\u00020\u0005H\u0016J\u001a\u0010)\u001a\u00020\u000e2\u0006\u0010!\u001a\u00020\u00052\b\u0010*\u001a\u0004\u0018\u00010\u0001H\u0016J\u0010\u0010+\u001a\u00020,2\u0006\u0010\n\u001a\u00020\tH\u0016J\u0010\u0010-\u001a\u00020.2\u0006\u0010\n\u001a\u00020\tH\u0016J\u0010\u0010/\u001a\u00020\f2\u0006\u0010\n\u001a\u00020\tH\u0016J\b\u00100\u001a\u00020\u0005H\u0016¨\u00062"},
   d2 = {"Lokhttp3/internal/platform/Platform;", "", "<init>", "()V", "getPrefix", "", "newSSLContext", "Ljavax/net/ssl/SSLContext;", "platformTrustManager", "Ljavax/net/ssl/X509TrustManager;", "trustManager", "sslSocketFactory", "Ljavax/net/ssl/SSLSocketFactory;", "configureTlsExtensions", "", "sslSocket", "Ljavax/net/ssl/SSLSocket;", "hostname", "protocols", "", "Lokhttp3/Protocol;", "Lkotlin/jvm/JvmSuppressWildcards;", "afterHandshake", "getSelectedProtocol", "getHandshakeServerNames", "connectSocket", "socket", "Ljava/net/Socket;", "address", "Ljava/net/InetSocketAddress;", "connectTimeout", "", "log", "message", "level", "t", "", "isCleartextTrafficPermitted", "", "getStackTraceForCloseable", "closer", "logCloseableLeak", "stackTrace", "buildCertificateChainCleaner", "Lokhttp3/internal/tls/CertificateChainCleaner;", "buildTrustRootIndex", "Lokhttp3/internal/tls/TrustRootIndex;", "newSslSocketFactory", "toString", "Companion", "okhttp"}
)
@SourceDebugExtension({"SMAP\nPlatform.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Platform.kt\nokhttp3/internal/platform/Platform\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,241:1\n1617#2,9:242\n1869#2:251\n1870#2:253\n1626#2:254\n1#3:252\n*S KotlinDebug\n*F\n+ 1 Platform.kt\nokhttp3/internal/platform/Platform\n*L\n134#1:242,9\n134#1:251\n134#1:253\n134#1:254\n134#1:252\n*E\n"})
public class Platform {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private static volatile Platform platform;
   public static final int INFO = 4;
   public static final int WARN = 5;
   private static final Logger logger;

   @NotNull
   public final String getPrefix() {
      return "OkHttp";
   }

   @NotNull
   public SSLContext newSSLContext() {
      SSLContext var10000 = SSLContext.getInstance("TLS");
      Intrinsics.checkNotNullExpressionValue(var10000, "getInstance(...)");
      return var10000;
   }

   @NotNull
   public X509TrustManager platformTrustManager() {
      TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      factory.init((KeyStore)null);
      TrustManager var10000 = factory.getTrustManagers();
      Intrinsics.checkNotNull(var10000);
      TrustManager[] trustManagers = var10000;
      if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
         int var3 = 0;
         StringBuilder var6 = (new StringBuilder()).append("Unexpected default trust managers: ");
         String var10001 = Arrays.toString(trustManagers);
         Intrinsics.checkNotNullExpressionValue(var10001, "toString(...)");
         String var4 = var6.append(var10001).toString();
         throw new IllegalStateException(var4.toString());
      } else {
         var10000 = trustManagers[0];
         Intrinsics.checkNotNull(trustManagers[0], "null cannot be cast to non-null type javax.net.ssl.X509TrustManager");
         return (X509TrustManager)var10000;
      }
   }

   @Nullable
   public X509TrustManager trustManager(@NotNull SSLSocketFactory sslSocketFactory) {
      Intrinsics.checkNotNullParameter(sslSocketFactory, "sslSocketFactory");

      X509TrustManager sslContextClass;
      try {
         Class sslContextClass = Class.forName("sun.security.ssl.SSLContextImpl");
         Intrinsics.checkNotNull(sslContextClass);
         Object var10000 = _UtilJvmKt.readFieldOrNull(sslSocketFactory, sslContextClass, "context");
         if (var10000 == null) {
            return null;
         }

         Object context = var10000;
         sslContextClass = (X509TrustManager)_UtilJvmKt.readFieldOrNull(context, X509TrustManager.class, "trustManager");
      } catch (ClassNotFoundException var4) {
         sslContextClass = null;
      } catch (RuntimeException e) {
         if (!Intrinsics.areEqual((Object)e.getClass().getName(), (Object)"java.lang.reflect.InaccessibleObjectException")) {
            throw e;
         }

         sslContextClass = null;
      }

      return sslContextClass;
   }

   public void configureTlsExtensions(@NotNull SSLSocket sslSocket, @Nullable String hostname, @NotNull List<Protocol> protocols) {
      Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");
      Intrinsics.checkNotNullParameter(protocols, "protocols");
   }

   public void afterHandshake(@NotNull SSLSocket sslSocket) {
      Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");
   }

   @Nullable
   public String getSelectedProtocol(@NotNull SSLSocket sslSocket) {
      Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");
      return null;
   }

   @IgnoreJRERequirement
   @NotNull
   public List<String> getHandshakeServerNames(@NotNull SSLSocket sslSocket) {
      Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");
      SSLSession var4 = sslSocket.getSession();
      ExtendedSSLSession var10000 = var4 instanceof ExtendedSSLSession ? (ExtendedSSLSession)var4 : null;
      if ((var4 instanceof ExtendedSSLSession ? (ExtendedSSLSession)var4 : null) == null) {
         return CollectionsKt.emptyList();
      } else {
         ExtendedSSLSession session = var10000;

         List $this$mapNotNull$iv;
         try {
            List var21 = session.getRequestedServerNames();
            Intrinsics.checkNotNullExpressionValue(var21, "getRequestedServerNames(...)");
            Iterable $this$mapNotNull$iv = (Iterable)var21;
            int $i$f$mapNotNull = 0;
            Collection destination$iv$iv = (Collection)(new ArrayList());
            int $i$f$mapNotNullTo = 0;
            int $i$f$forEach = 0;

            for(Object element$iv$iv$iv : $this$mapNotNull$iv) {
               int var13 = 0;
               SNIServerName it = (SNIServerName)element$iv$iv$iv;
               int var15 = 0;
               String var22 = (it instanceof SNIHostName ? (SNIHostName)it : null) != null ? (it instanceof SNIHostName ? (SNIHostName)it : null).getAsciiName() : null;
               if (var22 != null) {
                  Object it$iv$iv = var22;
                  int var17 = 0;
                  destination$iv$iv.add(it$iv$iv);
               }
            }

            $this$mapNotNull$iv = (List)destination$iv$iv;
         } catch (UnsupportedOperationException var18) {
            $this$mapNotNull$iv = CollectionsKt.emptyList();
         }

         return $this$mapNotNull$iv;
      }
   }

   public void connectSocket(@NotNull Socket socket, @NotNull InetSocketAddress address, int connectTimeout) throws IOException {
      Intrinsics.checkNotNullParameter(socket, "socket");
      Intrinsics.checkNotNullParameter(address, "address");
      socket.connect((SocketAddress)address, connectTimeout);
   }

   public void log(@NotNull String message, int level, @Nullable Throwable t) {
      Intrinsics.checkNotNullParameter(message, "message");
      Level logLevel = level == 5 ? Level.WARNING : Level.INFO;
      logger.log(logLevel, message, t);
   }

   // $FF: synthetic method
   public static void log$default(Platform var0, String var1, int var2, Throwable var3, int var4, Object var5) {
      if (var5 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: log");
      } else {
         if ((var4 & 2) != 0) {
            var2 = 4;
         }

         if ((var4 & 4) != 0) {
            var3 = null;
         }

         var0.log(var1, var2, var3);
      }
   }

   public boolean isCleartextTrafficPermitted(@NotNull String hostname) {
      Intrinsics.checkNotNullParameter(hostname, "hostname");
      return true;
   }

   @Nullable
   public Object getStackTraceForCloseable(@NotNull String closer) {
      Intrinsics.checkNotNullParameter(closer, "closer");
      return logger.isLoggable(Level.FINE) ? new Throwable(closer) : null;
   }

   public void logCloseableLeak(@NotNull String message, @Nullable Object stackTrace) {
      Intrinsics.checkNotNullParameter(message, "message");
      String logMessage = message;
      if (stackTrace == null) {
         logMessage = message + " To see where this was allocated, set the OkHttpClient logger level to FINE: Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);";
      }

      this.log(logMessage, 5, (Throwable)stackTrace);
   }

   @NotNull
   public CertificateChainCleaner buildCertificateChainCleaner(@NotNull X509TrustManager trustManager) {
      Intrinsics.checkNotNullParameter(trustManager, "trustManager");
      return new BasicCertificateChainCleaner(this.buildTrustRootIndex(trustManager));
   }

   @NotNull
   public TrustRootIndex buildTrustRootIndex(@NotNull X509TrustManager trustManager) {
      Intrinsics.checkNotNullParameter(trustManager, "trustManager");
      X509Certificate[] var2 = trustManager.getAcceptedIssuers();
      return new BasicTrustRootIndex((X509Certificate[])Arrays.copyOf(var2, var2.length));
   }

   @NotNull
   public SSLSocketFactory newSslSocketFactory(@NotNull X509TrustManager trustManager) {
      Intrinsics.checkNotNullParameter(trustManager, "trustManager");

      try {
         SSLContext $this$newSslSocketFactory_u24lambda_u240 = this.newSSLContext();
         int var4 = 0;
         TrustManager[] var5 = new TrustManager[]{trustManager};
         $this$newSslSocketFactory_u24lambda_u240.init((KeyManager[])null, var5, (SecureRandom)null);
         SSLSocketFactory var10000 = $this$newSslSocketFactory_u24lambda_u240.getSocketFactory();
         Intrinsics.checkNotNullExpressionValue(var10000, "getSocketFactory(...)");
         return var10000;
      } catch (GeneralSecurityException e) {
         throw new AssertionError("No System TLS: " + e, (Throwable)e);
      }
   }

   @NotNull
   public String toString() {
      String var10000 = this.getClass().getSimpleName();
      Intrinsics.checkNotNullExpressionValue(var10000, "getSimpleName(...)");
      return var10000;
   }

   @JvmStatic
   @NotNull
   public static final Platform get() {
      return Companion.get();
   }

   static {
      platform = Companion.findPlatform();
      logger = Logger.getLogger(OkHttpClient.class.getName());
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0012\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\f\u001a\u00020\u0005H\u0007J\u0010\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u0004\u001a\u00020\u0005J\u001a\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u00102\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\u0010J\b\u0010\u0017\u001a\u00020\u0005H\u0002J\u0014\u0010\u0018\u001a\u00020\u00192\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\u0010R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u0016\u0010\t\u001a\n \u000b*\u0004\u0018\u00010\n0\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0014\u001a\u00020\u00158F¢\u0006\u0006\u001a\u0004\b\u0014\u0010\u0016¨\u0006\u001a"},
      d2 = {"Lokhttp3/internal/platform/Platform$Companion;", "", "<init>", "()V", "platform", "Lokhttp3/internal/platform/Platform;", "INFO", "", "WARN", "logger", "Ljava/util/logging/Logger;", "kotlin.jvm.PlatformType", "get", "resetForTests", "", "alpnProtocolNames", "", "", "protocols", "Lokhttp3/Protocol;", "isAndroid", "", "()Z", "findPlatform", "concatLengthPrefixed", "", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nPlatform.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Platform.kt\nokhttp3/internal/platform/Platform$Companion\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,241:1\n774#2:242\n865#2,2:243\n1563#2:245\n1634#2,3:246\n*S KotlinDebug\n*F\n+ 1 Platform.kt\nokhttp3/internal/platform/Platform$Companion\n*L\n219#1:242\n219#1:243,2\n219#1:245\n219#1:246,3\n*E\n"})
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @NotNull
      public final Platform get() {
         return Platform.platform;
      }

      public final void resetForTests(@NotNull Platform platform) {
         Intrinsics.checkNotNullParameter(platform, "platform");
         Platform.platform = platform;
         PublicSuffixDatabase.Companion.resetForTests$okhttp();
      }

      // $FF: synthetic method
      public static void resetForTests$default(Companion var0, Platform var1, int var2, Object var3) {
         if ((var2 & 1) != 0) {
            var1 = var0.findPlatform();
         }

         var0.resetForTests(var1);
      }

      @NotNull
      public final List<String> alpnProtocolNames(@NotNull List<? extends Protocol> protocols) {
         Intrinsics.checkNotNullParameter(protocols, "protocols");
         Iterable $this$filter$iv = (Iterable)protocols;
         int $i$f$filter = 0;
         Collection destination$iv$iv = (Collection)(new ArrayList());
         int $i$f$filterTo = 0;

         for(Object element$iv$iv : $this$filter$iv) {
            Protocol it = (Protocol)element$iv$iv;
            int var10 = 0;
            if (it != Protocol.HTTP_1_0) {
               destination$iv$iv.add(element$iv$iv);
            }
         }

         $this$filter$iv = (Iterable)((List)destination$iv$iv);
         $i$f$filter = 0;
         destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$filter$iv, 10)));
         $i$f$filterTo = 0;

         for(Object item$iv$iv : $this$filter$iv) {
            Protocol it = (Protocol)item$iv$iv;
            int var19 = 0;
            destination$iv$iv.add(it.toString());
         }

         return (List)destination$iv$iv;
      }

      public final boolean isAndroid() {
         return PlatformRegistry.INSTANCE.isAndroid();
      }

      private final Platform findPlatform() {
         return PlatformRegistry.INSTANCE.findPlatform();
      }

      @NotNull
      public final byte[] concatLengthPrefixed(@NotNull List<? extends Protocol> protocols) {
         Intrinsics.checkNotNullParameter(protocols, "protocols");
         Buffer result = new Buffer();

         for(String protocol : this.alpnProtocolNames(protocols)) {
            result.writeByte(protocol.length());
            result.writeUtf8(protocol);
         }

         return result.readByteArray();
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
