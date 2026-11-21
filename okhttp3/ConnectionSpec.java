package okhttp3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.net.ssl.SSLSocket;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.Internal;
import okhttp3.internal._UtilCommonKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\b\n\u0002\b\u0004\u0018\u0000 '2\u00020\u0001:\u0002&'B9\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u000e\u0010\u0005\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u0006\u0012\u000e\u0010\b\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u0006¢\u0006\u0004\b\t\u0010\nJ\u0015\u0010\u000f\u001a\n\u0012\u0004\u0012\u00020\u0011\u0018\u00010\u0010H\u0007¢\u0006\u0002\b\u0013J\u0015\u0010\u0014\u001a\n\u0012\u0004\u0012\u00020\u0015\u0018\u00010\u0010H\u0007¢\u0006\u0002\b\u0016J\r\u0010\u0004\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u0017J\u001d\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u0003H\u0000¢\u0006\u0002\b\u001dJ\u0018\u0010\u001e\u001a\u00020\u00002\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u0003H\u0002J\u000e\u0010\u001f\u001a\u00020\u00032\u0006\u0010 \u001a\u00020\u001bJ\u0013\u0010!\u001a\u00020\u00032\b\u0010\"\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010#\u001a\u00020$H\u0016J\b\u0010%\u001a\u00020\u0007H\u0016R\u0013\u0010\u0002\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u000bR\u0013\u0010\u0004\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u000bR\u001e\u0010\u0005\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u0006X\u0080\u0004¢\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\f\u0010\rR\u0018\u0010\b\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u0006X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u000eR\u0019\u0010\u000f\u001a\n\u0012\u0004\u0012\u00020\u0011\u0018\u00010\u00108G¢\u0006\u0006\u001a\u0004\b\u000f\u0010\u0012R\u0019\u0010\u0014\u001a\n\u0012\u0004\u0012\u00020\u0015\u0018\u00010\u00108G¢\u0006\u0006\u001a\u0004\b\u0014\u0010\u0012¨\u0006("},
   d2 = {"Lokhttp3/ConnectionSpec;", "", "isTls", "", "supportsTlsExtensions", "cipherSuitesAsString", "", "", "tlsVersionsAsString", "<init>", "(ZZ[Ljava/lang/String;[Ljava/lang/String;)V", "()Z", "getCipherSuitesAsString$okhttp", "()[Ljava/lang/String;", "[Ljava/lang/String;", "cipherSuites", "", "Lokhttp3/CipherSuite;", "()Ljava/util/List;", "-deprecated_cipherSuites", "tlsVersions", "Lokhttp3/TlsVersion;", "-deprecated_tlsVersions", "-deprecated_supportsTlsExtensions", "apply", "", "sslSocket", "Ljavax/net/ssl/SSLSocket;", "isFallback", "apply$okhttp", "supportedSpec", "isCompatible", "socket", "equals", "other", "hashCode", "", "toString", "Builder", "Companion", "okhttp"}
)
@SourceDebugExtension({"SMAP\nConnectionSpec.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConnectionSpec.kt\nokhttp3/ConnectionSpec\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,384:1\n11561#2:385\n11896#2,3:386\n11561#2:389\n11896#2,3:390\n37#3,2:393\n37#3,2:395\n37#3,2:397\n*S KotlinDebug\n*F\n+ 1 ConnectionSpec.kt\nokhttp3/ConnectionSpec\n*L\n60#1:385\n60#1:386,3\n78#1:389\n78#1:390,3\n349#1:393,2\n361#1:395,2\n374#1:397,2\n*E\n"})
public final class ConnectionSpec {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   private final boolean isTls;
   private final boolean supportsTlsExtensions;
   @Nullable
   private final String[] cipherSuitesAsString;
   @Nullable
   private final String[] tlsVersionsAsString;
   @NotNull
   private static final List<CipherSuite> RESTRICTED_CIPHER_SUITES;
   @NotNull
   private static final List<CipherSuite> APPROVED_CIPHER_SUITES;
   @JvmField
   @NotNull
   public static final ConnectionSpec RESTRICTED_TLS;
   @JvmField
   @NotNull
   public static final ConnectionSpec MODERN_TLS;
   @JvmField
   @NotNull
   public static final ConnectionSpec COMPATIBLE_TLS;
   @JvmField
   @NotNull
   public static final ConnectionSpec CLEARTEXT;

   public ConnectionSpec(boolean isTls, boolean supportsTlsExtensions, @Nullable String[] cipherSuitesAsString, @Nullable String[] tlsVersionsAsString) {
      this.isTls = isTls;
      this.supportsTlsExtensions = supportsTlsExtensions;
      this.cipherSuitesAsString = cipherSuitesAsString;
      this.tlsVersionsAsString = tlsVersionsAsString;
   }

   @JvmName(
      name = "isTls"
   )
   public final boolean isTls() {
      return this.isTls;
   }

   @JvmName(
      name = "supportsTlsExtensions"
   )
   public final boolean supportsTlsExtensions() {
      return this.supportsTlsExtensions;
   }

   @Nullable
   public final String[] getCipherSuitesAsString$okhttp() {
      return this.cipherSuitesAsString;
   }

   @JvmName(
      name = "cipherSuites"
   )
   @Nullable
   public final List<CipherSuite> cipherSuites() {
      String[] var10000 = this.cipherSuitesAsString;
      List var12;
      if (var10000 != null) {
         Object[] $this$map$iv = var10000;
         int $i$f$map = 0;
         Object $this$mapTo$iv$iv = $this$map$iv;
         Collection destination$iv$iv = (Collection)(new ArrayList($this$map$iv.length));
         int $i$f$mapTo = 0;
         int var6 = 0;

         for(int var7 = $this$map$iv.length; var6 < var7; ++var6) {
            Object item$iv$iv = ((Object[])$this$mapTo$iv$iv)[var6];
            int var10 = 0;
            destination$iv$iv.add(CipherSuite.Companion.forJavaName((String)item$iv$iv));
         }

         var12 = (List)destination$iv$iv;
      } else {
         var12 = null;
      }

      return var12;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "cipherSuites",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_cipherSuites"
   )
   @Nullable
   public final List<CipherSuite> _deprecated_cipherSuites/* $FF was: -deprecated_cipherSuites*/() {
      return this.cipherSuites();
   }

   @JvmName(
      name = "tlsVersions"
   )
   @Nullable
   public final List<TlsVersion> tlsVersions() {
      String[] var10000 = this.tlsVersionsAsString;
      List var12;
      if (var10000 != null) {
         Object[] $this$map$iv = var10000;
         int $i$f$map = 0;
         Object $this$mapTo$iv$iv = $this$map$iv;
         Collection destination$iv$iv = (Collection)(new ArrayList($this$map$iv.length));
         int $i$f$mapTo = 0;
         int var6 = 0;

         for(int var7 = $this$map$iv.length; var6 < var7; ++var6) {
            Object item$iv$iv = ((Object[])$this$mapTo$iv$iv)[var6];
            int var10 = 0;
            destination$iv$iv.add(TlsVersion.Companion.forJavaName((String)item$iv$iv));
         }

         var12 = (List)destination$iv$iv;
      } else {
         var12 = null;
      }

      return var12;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "tlsVersions",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_tlsVersions"
   )
   @Nullable
   public final List<TlsVersion> _deprecated_tlsVersions/* $FF was: -deprecated_tlsVersions*/() {
      return this.tlsVersions();
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "supportsTlsExtensions",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_supportsTlsExtensions"
   )
   public final boolean _deprecated_supportsTlsExtensions/* $FF was: -deprecated_supportsTlsExtensions*/() {
      return this.supportsTlsExtensions;
   }

   public final void apply$okhttp(@NotNull SSLSocket sslSocket, boolean isFallback) {
      Intrinsics.checkNotNullParameter(sslSocket, "sslSocket");
      ConnectionSpec specToApply = this.supportedSpec(sslSocket, isFallback);
      if (specToApply.tlsVersions() != null) {
         sslSocket.setEnabledProtocols(specToApply.tlsVersionsAsString);
      }

      if (specToApply.cipherSuites() != null) {
         sslSocket.setEnabledCipherSuites(specToApply.cipherSuitesAsString);
      }

   }

   private final ConnectionSpec supportedSpec(SSLSocket sslSocket, boolean isFallback) {
      String[] socketEnabledCipherSuites = sslSocket.getEnabledCipherSuites();
      Intrinsics.checkNotNull(socketEnabledCipherSuites);
      String[] cipherSuitesIntersection = Internal.effectiveCipherSuites(this, socketEnabledCipherSuites);
      String[] var8;
      if (this.tlsVersionsAsString != null) {
         var8 = sslSocket.getEnabledProtocols();
         Intrinsics.checkNotNullExpressionValue(var8, "getEnabledProtocols(...)");
         var8 = _UtilCommonKt.intersect(var8, this.tlsVersionsAsString, ComparisonsKt.naturalOrder());
      } else {
         var8 = sslSocket.getEnabledProtocols();
      }

      String[] tlsVersionsIntersection = var8;
      String[] supportedCipherSuites = sslSocket.getSupportedCipherSuites();
      Intrinsics.checkNotNull(supportedCipherSuites);
      int indexOfFallbackScsv = _UtilCommonKt.indexOf(supportedCipherSuites, "TLS_FALLBACK_SCSV", CipherSuite.Companion.getORDER_BY_NAME$okhttp());
      if (isFallback && indexOfFallbackScsv != -1) {
         String var10001 = supportedCipherSuites[indexOfFallbackScsv];
         Intrinsics.checkNotNullExpressionValue(supportedCipherSuites[indexOfFallbackScsv], "get(...)");
         cipherSuitesIntersection = _UtilCommonKt.concat(cipherSuitesIntersection, var10001);
      }

      return (new Builder(this)).cipherSuites((String[])Arrays.copyOf(cipherSuitesIntersection, cipherSuitesIntersection.length)).tlsVersions((String[])Arrays.copyOf(tlsVersionsIntersection, tlsVersionsIntersection.length)).build();
   }

   public final boolean isCompatible(@NotNull SSLSocket socket) {
      Intrinsics.checkNotNullParameter(socket, "socket");
      if (!this.isTls) {
         return false;
      } else if (this.tlsVersionsAsString != null && !_UtilCommonKt.hasIntersection(this.tlsVersionsAsString, socket.getEnabledProtocols(), ComparisonsKt.naturalOrder())) {
         return false;
      } else {
         return this.cipherSuitesAsString == null || _UtilCommonKt.hasIntersection(this.cipherSuitesAsString, socket.getEnabledCipherSuites(), CipherSuite.Companion.getORDER_BY_NAME$okhttp());
      }
   }

   public boolean equals(@Nullable Object other) {
      if (!(other instanceof ConnectionSpec)) {
         return false;
      } else if (other == this) {
         return true;
      } else if (this.isTls != ((ConnectionSpec)other).isTls) {
         return false;
      } else {
         if (this.isTls) {
            if (!Arrays.equals(this.cipherSuitesAsString, ((ConnectionSpec)other).cipherSuitesAsString)) {
               return false;
            }

            if (!Arrays.equals(this.tlsVersionsAsString, ((ConnectionSpec)other).tlsVersionsAsString)) {
               return false;
            }

            if (this.supportsTlsExtensions != ((ConnectionSpec)other).supportsTlsExtensions) {
               return false;
            }
         }

         return true;
      }
   }

   public int hashCode() {
      int result = 17;
      if (this.isTls) {
         int var10000 = 31 * result;
         String[] var10001 = this.cipherSuitesAsString;
         result = var10000 + (var10001 != null ? Arrays.hashCode(var10001) : 0);
         var10000 = 31 * result;
         var10001 = this.tlsVersionsAsString;
         result = var10000 + (var10001 != null ? Arrays.hashCode(var10001) : 0);
         result = 31 * result + (this.supportsTlsExtensions ? 0 : 1);
      }

      return result;
   }

   @NotNull
   public String toString() {
      return !this.isTls ? "ConnectionSpec()" : "ConnectionSpec(cipherSuites=" + Objects.toString(this.cipherSuites(), "[all enabled]") + ", tlsVersions=" + Objects.toString(this.tlsVersions(), "[all enabled]") + ", supportsTlsExtensions=" + this.supportsTlsExtensions + ')';
   }

   static {
      CipherSuite[] var0 = new CipherSuite[]{CipherSuite.TLS_AES_128_GCM_SHA256, CipherSuite.TLS_AES_256_GCM_SHA384, CipherSuite.TLS_CHACHA20_POLY1305_SHA256, CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256};
      RESTRICTED_CIPHER_SUITES = CollectionsKt.listOf(var0);
      var0 = new CipherSuite[]{CipherSuite.TLS_AES_128_GCM_SHA256, CipherSuite.TLS_AES_256_GCM_SHA384, CipherSuite.TLS_CHACHA20_POLY1305_SHA256, CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA, CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA, CipherSuite.TLS_RSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_RSA_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA, CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA, CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA};
      APPROVED_CIPHER_SUITES = CollectionsKt.listOf(var0);
      Builder var10000 = new Builder(true);
      Collection $this$toTypedArray$iv = (Collection)RESTRICTED_CIPHER_SUITES;
      int $i$f$toTypedArray = 0;
      var0 = (CipherSuite[])$this$toTypedArray$iv.toArray(new CipherSuite[0]);
      var10000 = var10000.cipherSuites((CipherSuite[])Arrays.copyOf(var0, var0.length));
      TlsVersion[] var6 = new TlsVersion[]{TlsVersion.TLS_1_3, TlsVersion.TLS_1_2};
      RESTRICTED_TLS = var10000.tlsVersions(var6).supportsTlsExtensions(true).build();
      var10000 = new Builder(true);
      $this$toTypedArray$iv = (Collection)APPROVED_CIPHER_SUITES;
      $i$f$toTypedArray = 0;
      CipherSuite[] var7 = (CipherSuite[])$this$toTypedArray$iv.toArray(new CipherSuite[0]);
      var10000 = var10000.cipherSuites((CipherSuite[])Arrays.copyOf(var7, var7.length));
      TlsVersion[] var8 = new TlsVersion[]{TlsVersion.TLS_1_3, TlsVersion.TLS_1_2};
      MODERN_TLS = var10000.tlsVersions(var8).supportsTlsExtensions(true).build();
      var10000 = new Builder(true);
      $this$toTypedArray$iv = (Collection)APPROVED_CIPHER_SUITES;
      $i$f$toTypedArray = 0;
      CipherSuite[] var9 = (CipherSuite[])$this$toTypedArray$iv.toArray(new CipherSuite[0]);
      var10000 = var10000.cipherSuites((CipherSuite[])Arrays.copyOf(var9, var9.length));
      TlsVersion[] var10 = new TlsVersion[]{TlsVersion.TLS_1_3, TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0};
      COMPATIBLE_TLS = var10000.tlsVersions(var10).supportsTlsExtensions(true).build();
      CLEARTEXT = (new Builder(false)).build();
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0011\b\u0010\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005B\u0011\b\u0016\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\u0004\u0010\bJ\u0006\u0010\u001a\u001a\u00020\u0000J\u001f\u0010\f\u001a\u00020\u00002\u0012\u0010\f\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u001b0\r\"\u00020\u001b¢\u0006\u0002\u0010\u001cJ\u001f\u0010\f\u001a\u00020\u00002\u0012\u0010\f\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u000e0\r\"\u00020\u000e¢\u0006\u0002\u0010\u001dJ\u0006\u0010\u001e\u001a\u00020\u0000J\u001f\u0010\u0014\u001a\u00020\u00002\u0012\u0010\u0014\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u001f0\r\"\u00020\u001f¢\u0006\u0002\u0010 J\u001f\u0010\u0014\u001a\u00020\u00002\u0012\u0010\u0014\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u000e0\r\"\u00020\u000e¢\u0006\u0002\u0010\u001dJ\u0010\u0010\u0017\u001a\u00020\u00002\u0006\u0010\u0017\u001a\u00020\u0003H\u0007J\u0006\u0010!\u001a\u00020\u0007R\u001a\u0010\u0002\u001a\u00020\u0003X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\u0005R$\u0010\f\u001a\n\u0012\u0004\u0012\u00020\u000e\u0018\u00010\rX\u0080\u000e¢\u0006\u0010\n\u0002\u0010\u0013\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R$\u0010\u0014\u001a\n\u0012\u0004\u0012\u00020\u000e\u0018\u00010\rX\u0080\u000e¢\u0006\u0010\n\u0002\u0010\u0013\u001a\u0004\b\u0015\u0010\u0010\"\u0004\b\u0016\u0010\u0012R\u001a\u0010\u0017\u001a\u00020\u0003X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\n\"\u0004\b\u0019\u0010\u0005¨\u0006\""},
      d2 = {"Lokhttp3/ConnectionSpec$Builder;", "", "tls", "", "<init>", "(Z)V", "connectionSpec", "Lokhttp3/ConnectionSpec;", "(Lokhttp3/ConnectionSpec;)V", "getTls$okhttp", "()Z", "setTls$okhttp", "cipherSuites", "", "", "getCipherSuites$okhttp", "()[Ljava/lang/String;", "setCipherSuites$okhttp", "([Ljava/lang/String;)V", "[Ljava/lang/String;", "tlsVersions", "getTlsVersions$okhttp", "setTlsVersions$okhttp", "supportsTlsExtensions", "getSupportsTlsExtensions$okhttp", "setSupportsTlsExtensions$okhttp", "allEnabledCipherSuites", "Lokhttp3/CipherSuite;", "([Lokhttp3/CipherSuite;)Lokhttp3/ConnectionSpec$Builder;", "([Ljava/lang/String;)Lokhttp3/ConnectionSpec$Builder;", "allEnabledTlsVersions", "Lokhttp3/TlsVersion;", "([Lokhttp3/TlsVersion;)Lokhttp3/ConnectionSpec$Builder;", "build", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nConnectionSpec.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConnectionSpec.kt\nokhttp3/ConnectionSpec$Builder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 4 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,384:1\n1#2:385\n11561#3:386\n11896#3,3:387\n11561#3:392\n11896#3,3:393\n37#4,2:390\n37#4,2:396\n*S KotlinDebug\n*F\n+ 1 ConnectionSpec.kt\nokhttp3/ConnectionSpec$Builder\n*L\n247#1:386\n247#1:387,3\n269#1:392\n269#1:393,3\n247#1:390,2\n269#1:396,2\n*E\n"})
   public static final class Builder {
      private boolean tls;
      @Nullable
      private String[] cipherSuites;
      @Nullable
      private String[] tlsVersions;
      private boolean supportsTlsExtensions;

      public final boolean getTls$okhttp() {
         return this.tls;
      }

      public final void setTls$okhttp(boolean var1) {
         this.tls = var1;
      }

      @Nullable
      public final String[] getCipherSuites$okhttp() {
         return this.cipherSuites;
      }

      public final void setCipherSuites$okhttp(@Nullable String[] var1) {
         this.cipherSuites = var1;
      }

      @Nullable
      public final String[] getTlsVersions$okhttp() {
         return this.tlsVersions;
      }

      public final void setTlsVersions$okhttp(@Nullable String[] var1) {
         this.tlsVersions = var1;
      }

      public final boolean getSupportsTlsExtensions$okhttp() {
         return this.supportsTlsExtensions;
      }

      public final void setSupportsTlsExtensions$okhttp(boolean var1) {
         this.supportsTlsExtensions = var1;
      }

      public Builder(boolean tls) {
         this.tls = tls;
      }

      public Builder(@NotNull ConnectionSpec connectionSpec) {
         Intrinsics.checkNotNullParameter(connectionSpec, "connectionSpec");
         super();
         this.tls = connectionSpec.isTls();
         this.cipherSuites = connectionSpec.getCipherSuitesAsString$okhttp();
         this.tlsVersions = connectionSpec.tlsVersionsAsString;
         this.supportsTlsExtensions = connectionSpec.supportsTlsExtensions();
      }

      @NotNull
      public final Builder allEnabledCipherSuites() {
         Builder $this$allEnabledCipherSuites_u24lambda_u240 = this;
         int var3 = 0;
         if (!$this$allEnabledCipherSuites_u24lambda_u240.tls) {
            int var4 = 0;
            String var5 = "no cipher suites for cleartext connections";
            throw new IllegalArgumentException(var5.toString());
         } else {
            $this$allEnabledCipherSuites_u24lambda_u240.cipherSuites = null;
            return this;
         }
      }

      @NotNull
      public final Builder cipherSuites(@NotNull CipherSuite... cipherSuites) {
         Intrinsics.checkNotNullParameter(cipherSuites, "cipherSuites");
         Builder $this$cipherSuites_u24lambda_u240 = this;
         int var3 = 0;
         if (!$this$cipherSuites_u24lambda_u240.tls) {
            int $this$map$iv = 0;
            String $this$toTypedArray$iv = "no cipher suites for cleartext connections";
            throw new IllegalArgumentException($this$toTypedArray$iv.toString());
         } else {
            int $i$f$map = 0;
            CipherSuite[] $this$mapTo$iv$iv = cipherSuites;
            Collection destination$iv$iv = (Collection)(new ArrayList(cipherSuites.length));
            int $i$f$mapTo = 0;
            int var9 = 0;

            for(int var10 = cipherSuites.length; var9 < var10; ++var9) {
               Object item$iv$iv = $this$mapTo$iv$iv[var9];
               int var14 = 0;
               destination$iv$iv.add(((CipherSuite)item$iv$iv).javaName());
            }

            Collection thisCollection$iv = (Collection)((List)destination$iv$iv);
            $i$f$map = 0;
            String[] strings = (String[])thisCollection$iv.toArray(new String[0]);
            return $this$cipherSuites_u24lambda_u240.cipherSuites((String[])Arrays.copyOf(strings, strings.length));
         }
      }

      @NotNull
      public final Builder cipherSuites(@NotNull String... cipherSuites) {
         Intrinsics.checkNotNullParameter(cipherSuites, "cipherSuites");
         Builder $this$cipherSuites_u24lambda_u241 = this;
         int var4 = 0;
         if (!$this$cipherSuites_u24lambda_u241.tls) {
            int var7 = 0;
            String var8 = "no cipher suites for cleartext connections";
            throw new IllegalArgumentException(var8.toString());
         } else if (cipherSuites.length == 0) {
            int var5 = 0;
            String var6 = "At least one cipher suite is required";
            throw new IllegalArgumentException(var6.toString());
         } else {
            Object[] var10001 = Arrays.copyOf(cipherSuites, cipherSuites.length);
            Intrinsics.checkNotNullExpressionValue(var10001, "copyOf(...)");
            $this$cipherSuites_u24lambda_u241.cipherSuites = (String[])var10001;
            return this;
         }
      }

      @NotNull
      public final Builder allEnabledTlsVersions() {
         Builder $this$allEnabledTlsVersions_u24lambda_u240 = this;
         int var3 = 0;
         if (!$this$allEnabledTlsVersions_u24lambda_u240.tls) {
            int var4 = 0;
            String var5 = "no TLS versions for cleartext connections";
            throw new IllegalArgumentException(var5.toString());
         } else {
            $this$allEnabledTlsVersions_u24lambda_u240.tlsVersions = null;
            return this;
         }
      }

      @NotNull
      public final Builder tlsVersions(@NotNull TlsVersion... tlsVersions) {
         Intrinsics.checkNotNullParameter(tlsVersions, "tlsVersions");
         Builder $this$tlsVersions_u24lambda_u240 = this;
         int var3 = 0;
         if (!$this$tlsVersions_u24lambda_u240.tls) {
            int $this$map$iv = 0;
            String $this$toTypedArray$iv = "no TLS versions for cleartext connections";
            throw new IllegalArgumentException($this$toTypedArray$iv.toString());
         } else {
            int $i$f$map = 0;
            TlsVersion[] $this$mapTo$iv$iv = tlsVersions;
            Collection destination$iv$iv = (Collection)(new ArrayList(tlsVersions.length));
            int $i$f$mapTo = 0;
            int var9 = 0;

            for(int var10 = tlsVersions.length; var9 < var10; ++var9) {
               Object item$iv$iv = $this$mapTo$iv$iv[var9];
               int var14 = 0;
               destination$iv$iv.add(((TlsVersion)item$iv$iv).javaName());
            }

            Collection thisCollection$iv = (Collection)((List)destination$iv$iv);
            $i$f$map = 0;
            String[] strings = (String[])thisCollection$iv.toArray(new String[0]);
            return $this$tlsVersions_u24lambda_u240.tlsVersions((String[])Arrays.copyOf(strings, strings.length));
         }
      }

      @NotNull
      public final Builder tlsVersions(@NotNull String... tlsVersions) {
         Intrinsics.checkNotNullParameter(tlsVersions, "tlsVersions");
         Builder $this$tlsVersions_u24lambda_u241 = this;
         int var4 = 0;
         if (!$this$tlsVersions_u24lambda_u241.tls) {
            int var7 = 0;
            String var8 = "no TLS versions for cleartext connections";
            throw new IllegalArgumentException(var8.toString());
         } else if (tlsVersions.length == 0) {
            int var5 = 0;
            String var6 = "At least one TLS version is required";
            throw new IllegalArgumentException(var6.toString());
         } else {
            Object[] var10001 = Arrays.copyOf(tlsVersions, tlsVersions.length);
            Intrinsics.checkNotNullExpressionValue(var10001, "copyOf(...)");
            $this$tlsVersions_u24lambda_u241.tlsVersions = (String[])var10001;
            return this;
         }
      }

      /** @deprecated */
      @Deprecated(
         message = "since OkHttp 3.13 all TLS-connections are expected to support TLS extensions.\nIn a future release setting this to true will be unnecessary and setting it to false\nwill have no effect."
      )
      @NotNull
      public final Builder supportsTlsExtensions(boolean supportsTlsExtensions) {
         Builder $this$supportsTlsExtensions_u24lambda_u240 = this;
         int var4 = 0;
         if (!$this$supportsTlsExtensions_u24lambda_u240.tls) {
            int var5 = 0;
            String var6 = "no TLS extensions for cleartext connections";
            throw new IllegalArgumentException(var6.toString());
         } else {
            $this$supportsTlsExtensions_u24lambda_u240.supportsTlsExtensions = supportsTlsExtensions;
            return this;
         }
      }

      @NotNull
      public final ConnectionSpec build() {
         return new ConnectionSpec(this.tls, this.supportsTlsExtensions, this.cipherSuites, this.tlsVersions);
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u00020\t8\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u00020\t8\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u00020\t8\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u00020\t8\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\r"},
      d2 = {"Lokhttp3/ConnectionSpec$Companion;", "", "<init>", "()V", "RESTRICTED_CIPHER_SUITES", "", "Lokhttp3/CipherSuite;", "APPROVED_CIPHER_SUITES", "RESTRICTED_TLS", "Lokhttp3/ConnectionSpec;", "MODERN_TLS", "COMPATIBLE_TLS", "CLEARTEXT", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
