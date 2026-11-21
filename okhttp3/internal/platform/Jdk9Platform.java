package okhttp3.internal.platform;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.Protocol;
import okhttp3.internal.SuppressSignatureCheck;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0016\u0018\u0000 \u00152\u00020\u0001:\u0001\u0015B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J-\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\t2\u0011\u0010\n\u001a\r\u0012\t\u0012\u00070\f¢\u0006\u0002\b\r0\u000bH\u0017J\u0012\u0010\u000e\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0006\u001a\u00020\u0007H\u0017J\u0012\u0010\u000f\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0016J\b\u0010\u0013\u001a\u00020\u0014H\u0016¨\u0006\u0016"},
   d2 = {"Lokhttp3/internal/platform/Jdk9Platform;", "Lokhttp3/internal/platform/Platform;", "<init>", "()V", "configureTlsExtensions", "", "sslSocket", "Ljavax/net/ssl/SSLSocket;", "hostname", "", "protocols", "", "Lokhttp3/Protocol;", "Lkotlin/jvm/JvmSuppressWildcards;", "getSelectedProtocol", "trustManager", "Ljavax/net/ssl/X509TrustManager;", "sslSocketFactory", "Ljavax/net/ssl/SSLSocketFactory;", "newSSLContext", "Ljavax/net/ssl/SSLContext;", "Companion", "okhttp"}
)
@SourceDebugExtension({"SMAP\nJdk9Platform.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Jdk9Platform.kt\nokhttp3/internal/platform/Jdk9Platform\n+ 2 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,109:1\n37#2,2:110\n*S KotlinDebug\n*F\n+ 1 Jdk9Platform.kt\nokhttp3/internal/platform/Jdk9Platform\n*L\n42#1:110,2\n*E\n"})
public class Jdk9Platform extends Platform {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   private static final boolean isAvailable;
   @Nullable
   private static final Integer majorVersion;

   @SuppressSignatureCheck
   public void configureTlsExtensions(@NotNull SSLSocket sslSocket, @Nullable String hostname, @NotNull List<Protocol> protocols) {
      Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");
      Intrinsics.checkNotNullParameter(protocols, "protocols");
      SSLParameters sslParameters = sslSocket.getSSLParameters();
      List names = Platform.Companion.alpnProtocolNames(protocols);
      Collection $this$toTypedArray$iv = (Collection)names;
      int $i$f$toTypedArray = 0;
      sslParameters.setApplicationProtocols((String[])$this$toTypedArray$iv.toArray(new String[0]));
      sslSocket.setSSLParameters(sslParameters);
   }

   @SuppressSignatureCheck
   @Nullable
   public String getSelectedProtocol(@NotNull SSLSocket sslSocket) {
      Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");

      String protocol;
      try {
         protocol = sslSocket.getApplicationProtocol();
         protocol = protocol != null && !Intrinsics.areEqual((Object)protocol, (Object)"") ? protocol : null;
      } catch (UnsupportedOperationException var4) {
         protocol = null;
      }

      return protocol;
   }

   @Nullable
   public X509TrustManager trustManager(@NotNull SSLSocketFactory sslSocketFactory) {
      Intrinsics.checkNotNullParameter(sslSocketFactory, "sslSocketFactory");
      throw new UnsupportedOperationException("clientBuilder.sslSocketFactory(SSLSocketFactory) not supported on JDK 8 (>= 252) or JDK 9+");
   }

   @NotNull
   public SSLContext newSSLContext() {
      SSLContext var10000;
      if (majorVersion != null && majorVersion >= 9) {
         var10000 = SSLContext.getInstance("TLS");
         Intrinsics.checkNotNullExpressionValue(var10000, "getInstance(...)");
      } else {
         SSLContext var2;
         try {
            var2 = SSLContext.getInstance("TLSv1.3");
         } catch (NoSuchAlgorithmException var4) {
            var2 = SSLContext.getInstance("TLS");
         }

         Intrinsics.checkNotNull(var2);
         var10000 = var2;
      }

      return var10000;
   }

   static {
      String var10000 = System.getProperty("java.specification.version");
      majorVersion = var10000 != null ? StringsKt.toIntOrNull(var10000) : null;
      boolean var3;
      if (majorVersion != null) {
         var3 = majorVersion >= 9;
      } else {
         boolean var0;
         try {
            SSLSocket.class.getMethod("getApplicationProtocol");
            var0 = true;
         } catch (NoSuchMethodException var2) {
            var0 = false;
         }

         var3 = var0;
      }

      isAvailable = var3;
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\f\u001a\u0004\u0018\u00010\rR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0006R\u0015\u0010\u0007\u001a\u0004\u0018\u00010\b¢\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b\t\u0010\n¨\u0006\u000e"},
      d2 = {"Lokhttp3/internal/platform/Jdk9Platform$Companion;", "", "<init>", "()V", "isAvailable", "", "()Z", "majorVersion", "", "getMajorVersion", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "buildIfSupported", "Lokhttp3/internal/platform/Jdk9Platform;", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      public final boolean isAvailable() {
         return Jdk9Platform.isAvailable;
      }

      @Nullable
      public final Integer getMajorVersion() {
         return Jdk9Platform.majorVersion;
      }

      @Nullable
      public final Jdk9Platform buildIfSupported() {
         return this.isAvailable() ? new Jdk9Platform() : null;
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
