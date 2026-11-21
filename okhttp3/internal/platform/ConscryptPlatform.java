package okhttp3.internal.platform;

import java.security.KeyStore;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.Protocol;
import org.conscrypt.Conscrypt;
import org.conscrypt.ConscryptHostnameVerifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000 \u001a2\u00020\u0001:\u0002\u0019\u001aB\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0006\u001a\u00020\u0007H\u0016J\b\u0010\b\u001a\u00020\tH\u0016J\u0012\u0010\n\u001a\u0004\u0018\u00010\t2\u0006\u0010\u000b\u001a\u00020\fH\u0016J-\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u00122\u0011\u0010\u0013\u001a\r\u0012\t\u0012\u00070\u0015¢\u0006\u0002\b\u00160\u0014H\u0016J\u0012\u0010\u0017\u001a\u0004\u0018\u00010\u00122\u0006\u0010\u000f\u001a\u00020\u0010H\u0016J\u0010\u0010\u0018\u001a\u00020\f2\u0006\u0010\n\u001a\u00020\tH\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001b"},
   d2 = {"Lokhttp3/internal/platform/ConscryptPlatform;", "Lokhttp3/internal/platform/Platform;", "<init>", "()V", "provider", "Ljava/security/Provider;", "newSSLContext", "Ljavax/net/ssl/SSLContext;", "platformTrustManager", "Ljavax/net/ssl/X509TrustManager;", "trustManager", "sslSocketFactory", "Ljavax/net/ssl/SSLSocketFactory;", "configureTlsExtensions", "", "sslSocket", "Ljavax/net/ssl/SSLSocket;", "hostname", "", "protocols", "", "Lokhttp3/Protocol;", "Lkotlin/jvm/JvmSuppressWildcards;", "getSelectedProtocol", "newSslSocketFactory", "DisabledHostnameVerifier", "Companion", "okhttp"}
)
@SourceDebugExtension({"SMAP\nConscryptPlatform.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConscryptPlatform.kt\nokhttp3/internal/platform/ConscryptPlatform\n+ 2 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,145:1\n37#2,2:146\n*S KotlinDebug\n*F\n+ 1 ConscryptPlatform.kt\nokhttp3/internal/platform/ConscryptPlatform\n*L\n88#1:146,2\n*E\n"})
public final class ConscryptPlatform extends Platform {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final Provider provider;
   private static final boolean isSupported;

   private ConscryptPlatform() {
      Provider var10001 = Conscrypt.newProvider();
      Intrinsics.checkNotNullExpressionValue(var10001, "newProvider(...)");
      this.provider = var10001;
   }

   @NotNull
   public SSLContext newSSLContext() {
      SSLContext var10000 = SSLContext.getInstance("TLS", this.provider);
      Intrinsics.checkNotNullExpressionValue(var10000, "getInstance(...)");
      return var10000;
   }

   @NotNull
   public X509TrustManager platformTrustManager() {
      TrustManagerFactory $this$platformTrustManager_u24lambda_u240 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      int var4 = 0;
      $this$platformTrustManager_u24lambda_u240.init((KeyStore)null);
      TrustManager var10000 = $this$platformTrustManager_u24lambda_u240.getTrustManagers();
      Intrinsics.checkNotNull(var10000);
      TrustManager[] trustManagers = var10000;
      if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
         int $this$platformTrustManager_u24lambda_u240 = 0;
         StringBuilder var8 = (new StringBuilder()).append("Unexpected default trust managers: ");
         String var10001 = Arrays.toString(trustManagers);
         Intrinsics.checkNotNullExpressionValue(var10001, "toString(...)");
         String var6 = var8.append(var10001).toString();
         throw new IllegalStateException(var6.toString());
      } else {
         var10000 = trustManagers[0];
         Intrinsics.checkNotNull(trustManagers[0], "null cannot be cast to non-null type javax.net.ssl.X509TrustManager");
         X509TrustManager x509TrustManager = (X509TrustManager)var10000;
         Conscrypt.setHostnameVerifier((TrustManager)x509TrustManager, ConscryptPlatform.DisabledHostnameVerifier.INSTANCE);
         return x509TrustManager;
      }
   }

   @Nullable
   public X509TrustManager trustManager(@NotNull SSLSocketFactory sslSocketFactory) {
      Intrinsics.checkNotNullParameter(sslSocketFactory, "sslSocketFactory");
      return null;
   }

   public void configureTlsExtensions(@NotNull SSLSocket sslSocket, @Nullable String hostname, @NotNull List<Protocol> protocols) {
      Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");
      Intrinsics.checkNotNullParameter(protocols, "protocols");
      if (Conscrypt.isConscrypt(sslSocket)) {
         Conscrypt.setUseSessionTickets(sslSocket, true);
         List names = Platform.Companion.alpnProtocolNames(protocols);
         Collection $this$toTypedArray$iv = (Collection)names;
         int $i$f$toTypedArray = 0;
         Conscrypt.setApplicationProtocols(sslSocket, (String[])$this$toTypedArray$iv.toArray(new String[0]));
      } else {
         super.configureTlsExtensions(sslSocket, hostname, protocols);
      }

   }

   @Nullable
   public String getSelectedProtocol(@NotNull SSLSocket sslSocket) {
      Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");
      return Conscrypt.isConscrypt(sslSocket) ? Conscrypt.getApplicationProtocol(sslSocket) : super.getSelectedProtocol(sslSocket);
   }

   @NotNull
   public SSLSocketFactory newSslSocketFactory(@NotNull X509TrustManager trustManager) {
      Intrinsics.checkNotNullParameter(trustManager, "trustManager");
      SSLContext $this$newSslSocketFactory_u24lambda_u240 = this.newSSLContext();
      int var4 = 0;
      TrustManager[] var5 = new TrustManager[]{trustManager};
      $this$newSslSocketFactory_u24lambda_u240.init((KeyManager[])null, var5, (SecureRandom)null);
      SSLSocketFactory var10000 = $this$newSslSocketFactory_u24lambda_u240.getSocketFactory();
      Intrinsics.checkNotNullExpressionValue(var10000, "getSocketFactory(...)");
      return var10000;
   }

   // $FF: synthetic method
   public ConscryptPlatform(DefaultConstructorMarker $constructor_marker) {
      this();
   }

   static {
      boolean var0;
      try {
         Class.forName("org.conscrypt.Conscrypt$Version", false, Companion.getClass().getClassLoader());
         var0 = Conscrypt.isAvailable() && Companion.atLeastVersion(2, 1, 0);
      } catch (NoClassDefFoundError var2) {
         var0 = false;
      } catch (ClassNotFoundException var3) {
         var0 = false;
      }

      isSupported = var0;
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\bÀ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001a\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\u0010\b\u001a\u0004\u0018\u00010\tJ3\u0010\u0004\u001a\u00020\u00052\u0010\u0010\n\u001a\f\u0012\u0006\b\u0001\u0012\u00020\f\u0018\u00010\u000b2\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0016¢\u0006\u0002\u0010\r¨\u0006\u000e"},
      d2 = {"Lokhttp3/internal/platform/ConscryptPlatform$DisabledHostnameVerifier;", "Lorg/conscrypt/ConscryptHostnameVerifier;", "<init>", "()V", "verify", "", "hostname", "", "session", "Ljavax/net/ssl/SSLSession;", "certs", "", "Ljava/security/cert/X509Certificate;", "([Ljava/security/cert/X509Certificate;Ljava/lang/String;Ljavax/net/ssl/SSLSession;)Z", "okhttp"}
   )
   public static final class DisabledHostnameVerifier implements ConscryptHostnameVerifier {
      @NotNull
      public static final DisabledHostnameVerifier INSTANCE = new DisabledHostnameVerifier();

      private DisabledHostnameVerifier() {
      }

      public final boolean verify(@Nullable String hostname, @Nullable SSLSession session) {
         return true;
      }

      public boolean verify(@Nullable X509Certificate[] certs, @Nullable String hostname, @Nullable SSLSession session) {
         return true;
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0007\u001a\u0004\u0018\u00010\bJ\"\u0010\t\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u000b2\b\b\u0002\u0010\r\u001a\u00020\u000bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0006¨\u0006\u000e"},
      d2 = {"Lokhttp3/internal/platform/ConscryptPlatform$Companion;", "", "<init>", "()V", "isSupported", "", "()Z", "buildIfSupported", "Lokhttp3/internal/platform/ConscryptPlatform;", "atLeastVersion", "major", "", "minor", "patch", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      public final boolean isSupported() {
         return ConscryptPlatform.isSupported;
      }

      @Nullable
      public final ConscryptPlatform buildIfSupported() {
         return this.isSupported() ? new ConscryptPlatform((DefaultConstructorMarker)null) : null;
      }

      public final boolean atLeastVersion(int major, int minor, int patch) {
         Conscrypt.Version var10000 = Conscrypt.version();
         if (var10000 == null) {
            return false;
         } else {
            Conscrypt.Version conscryptVersion = var10000;
            if (conscryptVersion.major() != major) {
               return conscryptVersion.major() > major;
            } else if (conscryptVersion.minor() != minor) {
               return conscryptVersion.minor() > minor;
            } else {
               return conscryptVersion.patch() >= patch;
            }
         }
      }

      // $FF: synthetic method
      public static boolean atLeastVersion$default(Companion var0, int var1, int var2, int var3, int var4, Object var5) {
         if ((var4 & 2) != 0) {
            var2 = 0;
         }

         if ((var4 & 4) != 0) {
            var3 = 0;
         }

         return var0.atLeastVersion(var1, var2, var3);
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
