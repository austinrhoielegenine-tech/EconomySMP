package okhttp3.internal.platform;

import java.security.KeyStore;
import java.security.Provider;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.net.ssl.SSLContext;
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
import org.bouncycastle.jsse.BCSSLParameters;
import org.bouncycastle.jsse.BCSSLSocket;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \u00182\u00020\u0001:\u0001\u0018B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0006\u001a\u00020\u0007H\u0016J\b\u0010\b\u001a\u00020\tH\u0016J\u0010\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\fH\u0016J-\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u00122\u0011\u0010\u0013\u001a\r\u0012\t\u0012\u00070\u0015¢\u0006\u0002\b\u00160\u0014H\u0016J\u0012\u0010\u0017\u001a\u0004\u0018\u00010\u00122\u0006\u0010\u000f\u001a\u00020\u0010H\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0019"},
   d2 = {"Lokhttp3/internal/platform/BouncyCastlePlatform;", "Lokhttp3/internal/platform/Platform;", "<init>", "()V", "provider", "Ljava/security/Provider;", "newSSLContext", "Ljavax/net/ssl/SSLContext;", "platformTrustManager", "Ljavax/net/ssl/X509TrustManager;", "trustManager", "sslSocketFactory", "Ljavax/net/ssl/SSLSocketFactory;", "configureTlsExtensions", "", "sslSocket", "Ljavax/net/ssl/SSLSocket;", "hostname", "", "protocols", "", "Lokhttp3/Protocol;", "Lkotlin/jvm/JvmSuppressWildcards;", "getSelectedProtocol", "Companion", "okhttp"}
)
@SourceDebugExtension({"SMAP\nBouncyCastlePlatform.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BouncyCastlePlatform.kt\nokhttp3/internal/platform/BouncyCastlePlatform\n+ 2 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,101:1\n37#2,2:102\n*S KotlinDebug\n*F\n+ 1 BouncyCastlePlatform.kt\nokhttp3/internal/platform/BouncyCastlePlatform\n*L\n68#1:102,2\n*E\n"})
public final class BouncyCastlePlatform extends Platform {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final Provider provider;
   private static final boolean isSupported;

   private BouncyCastlePlatform() {
      this.provider = (Provider)(new BouncyCastleJsseProvider());
   }

   @NotNull
   public SSLContext newSSLContext() {
      SSLContext var10000 = SSLContext.getInstance("TLS", this.provider);
      Intrinsics.checkNotNullExpressionValue(var10000, "getInstance(...)");
      return var10000;
   }

   @NotNull
   public X509TrustManager platformTrustManager() {
      TrustManagerFactory factory = TrustManagerFactory.getInstance("PKIX", "BCJSSE");
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

   @NotNull
   public X509TrustManager trustManager(@NotNull SSLSocketFactory sslSocketFactory) {
      Intrinsics.checkNotNullParameter(sslSocketFactory, "sslSocketFactory");
      throw new UnsupportedOperationException("clientBuilder.sslSocketFactory(SSLSocketFactory) not supported with BouncyCastle");
   }

   public void configureTlsExtensions(@NotNull SSLSocket sslSocket, @Nullable String hostname, @NotNull List<Protocol> protocols) {
      Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");
      Intrinsics.checkNotNullParameter(protocols, "protocols");
      if (sslSocket instanceof BCSSLSocket) {
         BCSSLParameters sslParameters = ((BCSSLSocket)sslSocket).getParameters();
         List names = Platform.Companion.alpnProtocolNames(protocols);
         Collection $this$toTypedArray$iv = (Collection)names;
         int $i$f$toTypedArray = 0;
         sslParameters.setApplicationProtocols((String[])$this$toTypedArray$iv.toArray(new String[0]));
         ((BCSSLSocket)sslSocket).setParameters(sslParameters);
      } else {
         super.configureTlsExtensions(sslSocket, hostname, protocols);
      }

   }

   @Nullable
   public String getSelectedProtocol(@NotNull SSLSocket sslSocket) {
      Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");
      String var10000;
      if (sslSocket instanceof BCSSLSocket) {
         String protocol = ((BCSSLSocket)sslSocket).getApplicationProtocol();
         var10000 = protocol != null && !Intrinsics.areEqual((Object)protocol, (Object)"") ? protocol : null;
      } else {
         var10000 = super.getSelectedProtocol(sslSocket);
      }

      return var10000;
   }

   // $FF: synthetic method
   public BouncyCastlePlatform(DefaultConstructorMarker $constructor_marker) {
      this();
   }

   static {
      boolean var0;
      try {
         Class.forName("org.bouncycastle.jsse.provider.BouncyCastleJsseProvider", false, Companion.getClass().getClassLoader());
         var0 = true;
      } catch (ClassNotFoundException var2) {
         var0 = false;
      }

      isSupported = var0;
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0007\u001a\u0004\u0018\u00010\bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0006¨\u0006\t"},
      d2 = {"Lokhttp3/internal/platform/BouncyCastlePlatform$Companion;", "", "<init>", "()V", "isSupported", "", "()Z", "buildIfSupported", "Lokhttp3/internal/platform/BouncyCastlePlatform;", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      public final boolean isSupported() {
         return BouncyCastlePlatform.isSupported;
      }

      @Nullable
      public final BouncyCastlePlatform buildIfSupported() {
         return this.isSupported() ? new BouncyCastlePlatform((DefaultConstructorMarker)null) : null;
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
