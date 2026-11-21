package okhttp3.internal.platform;

import java.security.KeyStore;
import java.security.Provider;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openjsse.net.ssl.OpenJSSE;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \u00182\u00020\u0001:\u0001\u0018B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0006\u001a\u00020\u0007H\u0016J\b\u0010\b\u001a\u00020\tH\u0016J\u0010\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\fH\u0016J-\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u00122\u0011\u0010\u0013\u001a\r\u0012\t\u0012\u00070\u0015¢\u0006\u0002\b\u00160\u0014H\u0016J\u0012\u0010\u0017\u001a\u0004\u0018\u00010\u00122\u0006\u0010\u000f\u001a\u00020\u0010H\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0019"},
   d2 = {"Lokhttp3/internal/platform/OpenJSSEPlatform;", "Lokhttp3/internal/platform/Platform;", "<init>", "()V", "provider", "Ljava/security/Provider;", "newSSLContext", "Ljavax/net/ssl/SSLContext;", "platformTrustManager", "Ljavax/net/ssl/X509TrustManager;", "trustManager", "sslSocketFactory", "Ljavax/net/ssl/SSLSocketFactory;", "configureTlsExtensions", "", "sslSocket", "Ljavax/net/ssl/SSLSocket;", "hostname", "", "protocols", "", "Lokhttp3/Protocol;", "Lkotlin/jvm/JvmSuppressWildcards;", "getSelectedProtocol", "Companion", "okhttp"}
)
@SourceDebugExtension({"SMAP\nOpenJSSEPlatform.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpenJSSEPlatform.kt\nokhttp3/internal/platform/OpenJSSEPlatform\n+ 2 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,107:1\n37#2,2:108\n*S KotlinDebug\n*F\n+ 1 OpenJSSEPlatform.kt\nokhttp3/internal/platform/OpenJSSEPlatform\n*L\n73#1:108,2\n*E\n"})
public final class OpenJSSEPlatform extends Platform {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final Provider provider;
   private static final boolean isSupported;

   private OpenJSSEPlatform() {
      this.provider = (Provider)(new OpenJSSE());
   }

   @NotNull
   public SSLContext newSSLContext() {
      SSLContext var10000 = SSLContext.getInstance("TLSv1.3", this.provider);
      Intrinsics.checkNotNullExpressionValue(var10000, "getInstance(...)");
      return var10000;
   }

   @NotNull
   public X509TrustManager platformTrustManager() {
      TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm(), this.provider);
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
      throw new UnsupportedOperationException("clientBuilder.sslSocketFactory(SSLSocketFactory) not supported with OpenJSSE");
   }

   public void configureTlsExtensions(@NotNull SSLSocket sslSocket, @Nullable String hostname, @NotNull List<Protocol> protocols) {
      Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");
      Intrinsics.checkNotNullParameter(protocols, "protocols");
      if (sslSocket instanceof org.openjsse.javax.net.ssl.SSLSocket) {
         SSLParameters sslParameters = ((org.openjsse.javax.net.ssl.SSLSocket)sslSocket).getSSLParameters();
         if (sslParameters instanceof org.openjsse.javax.net.ssl.SSLParameters) {
            List names = Platform.Companion.alpnProtocolNames(protocols);
            org.openjsse.javax.net.ssl.SSLParameters var10000 = (org.openjsse.javax.net.ssl.SSLParameters)sslParameters;
            Collection $this$toTypedArray$iv = (Collection)names;
            int $i$f$toTypedArray = 0;
            var10000.setApplicationProtocols((String[])$this$toTypedArray$iv.toArray(new String[0]));
            ((org.openjsse.javax.net.ssl.SSLSocket)sslSocket).setSSLParameters(sslParameters);
         }
      } else {
         super.configureTlsExtensions(sslSocket, hostname, protocols);
      }

   }

   @Nullable
   public String getSelectedProtocol(@NotNull SSLSocket sslSocket) {
      Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");
      String var10000;
      if (sslSocket instanceof org.openjsse.javax.net.ssl.SSLSocket) {
         String protocol = ((org.openjsse.javax.net.ssl.SSLSocket)sslSocket).getApplicationProtocol();
         var10000 = protocol != null && !Intrinsics.areEqual((Object)protocol, (Object)"") ? protocol : null;
      } else {
         var10000 = super.getSelectedProtocol(sslSocket);
      }

      return var10000;
   }

   // $FF: synthetic method
   public OpenJSSEPlatform(DefaultConstructorMarker $constructor_marker) {
      this();
   }

   static {
      boolean var0;
      try {
         Class.forName("org.openjsse.net.ssl.OpenJSSE", false, Companion.getClass().getClassLoader());
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
      d2 = {"Lokhttp3/internal/platform/OpenJSSEPlatform$Companion;", "", "<init>", "()V", "isSupported", "", "()Z", "buildIfSupported", "Lokhttp3/internal/platform/OpenJSSEPlatform;", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      public final boolean isSupported() {
         return OpenJSSEPlatform.isSupported;
      }

      @Nullable
      public final OpenJSSEPlatform buildIfSupported() {
         return this.isSupported() ? new OpenJSSEPlatform((DefaultConstructorMarker)null) : null;
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
