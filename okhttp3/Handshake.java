package okhttp3;

import java.io.IOException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal._UtilJvmKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\u0018\u0000 '2\u00020\u0001:\u0001'B;\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\u0012\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\n¢\u0006\u0004\b\u000b\u0010\fJ\r\u0010\u0002\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\u0013J\r\u0010\u0004\u001a\u00020\u0005H\u0007¢\u0006\u0002\b\u0014J\u0013\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u0007¢\u0006\u0002\b\u0015J\u000f\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u0007¢\u0006\u0002\b\u0019J\u0013\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u0007¢\u0006\u0002\b\u001aJ\u000f\u0010\u001b\u001a\u0004\u0018\u00010\u0017H\u0007¢\u0006\u0002\b\u001cJ\u0013\u0010\u001d\u001a\u00020\u001e2\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010 \u001a\u00020!H\u0016J\b\u0010\"\u001a\u00020#H\u0016R\u0013\u0010\u0002\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\rR\u0013\u0010\u0004\u001a\u00020\u00058\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u000eR\u0019\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00078\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u000fR!\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\b0\u00078GX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0011\u0010\u0012\u001a\u0004\b\u0010\u0010\u000fR\u0013\u0010\u0016\u001a\u0004\u0018\u00010\u00178G¢\u0006\u0006\u001a\u0004\b\u0016\u0010\u0018R\u0013\u0010\u001b\u001a\u0004\u0018\u00010\u00178G¢\u0006\u0006\u001a\u0004\b\u001b\u0010\u0018R\u0018\u0010$\u001a\u00020#*\u00020\b8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b%\u0010&¨\u0006("},
   d2 = {"Lokhttp3/Handshake;", "", "tlsVersion", "Lokhttp3/TlsVersion;", "cipherSuite", "Lokhttp3/CipherSuite;", "localCertificates", "", "Ljava/security/cert/Certificate;", "peerCertificatesFn", "Lkotlin/Function0;", "<init>", "(Lokhttp3/TlsVersion;Lokhttp3/CipherSuite;Ljava/util/List;Lkotlin/jvm/functions/Function0;)V", "()Lokhttp3/TlsVersion;", "()Lokhttp3/CipherSuite;", "()Ljava/util/List;", "peerCertificates", "peerCertificates$delegate", "Lkotlin/Lazy;", "-deprecated_tlsVersion", "-deprecated_cipherSuite", "-deprecated_peerCertificates", "peerPrincipal", "Ljava/security/Principal;", "()Ljava/security/Principal;", "-deprecated_peerPrincipal", "-deprecated_localCertificates", "localPrincipal", "-deprecated_localPrincipal", "equals", "", "other", "hashCode", "", "toString", "", "name", "getName", "(Ljava/security/cert/Certificate;)Ljava/lang/String;", "Companion", "okhttp"}
)
@SourceDebugExtension({"SMAP\nHandshake.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Handshake.kt\nokhttp3/Handshake\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,201:1\n1563#2:202\n1634#2,3:203\n1563#2:206\n1634#2,3:207\n*S KotlinDebug\n*F\n+ 1 Handshake.kt\nokhttp3/Handshake\n*L\n131#1:202\n131#1:203,3\n136#1:206\n136#1:207,3\n*E\n"})
public final class Handshake {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final TlsVersion tlsVersion;
   @NotNull
   private final CipherSuite cipherSuite;
   @NotNull
   private final List<Certificate> localCertificates;
   @NotNull
   private final Lazy peerCertificates$delegate;

   public Handshake(@NotNull TlsVersion tlsVersion, @NotNull CipherSuite cipherSuite, @NotNull List<? extends Certificate> localCertificates, @NotNull Function0<? extends List<? extends Certificate>> peerCertificatesFn) {
      Intrinsics.checkNotNullParameter(tlsVersion, "tlsVersion");
      Intrinsics.checkNotNullParameter(cipherSuite, "cipherSuite");
      Intrinsics.checkNotNullParameter(localCertificates, "localCertificates");
      Intrinsics.checkNotNullParameter(peerCertificatesFn, "peerCertificatesFn");
      super();
      this.tlsVersion = tlsVersion;
      this.cipherSuite = cipherSuite;
      this.localCertificates = localCertificates;
      this.peerCertificates$delegate = LazyKt.lazy(Handshake::peerCertificates_delegate$lambda$0);
   }

   @JvmName(
      name = "tlsVersion"
   )
   @NotNull
   public final TlsVersion tlsVersion() {
      return this.tlsVersion;
   }

   @JvmName(
      name = "cipherSuite"
   )
   @NotNull
   public final CipherSuite cipherSuite() {
      return this.cipherSuite;
   }

   @JvmName(
      name = "localCertificates"
   )
   @NotNull
   public final List<Certificate> localCertificates() {
      return this.localCertificates;
   }

   @JvmName(
      name = "peerCertificates"
   )
   @NotNull
   public final List<Certificate> peerCertificates() {
      Lazy var1 = this.peerCertificates$delegate;
      return (List)var1.getValue();
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "tlsVersion",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_tlsVersion"
   )
   @NotNull
   public final TlsVersion _deprecated_tlsVersion/* $FF was: -deprecated_tlsVersion*/() {
      return this.tlsVersion;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "cipherSuite",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_cipherSuite"
   )
   @NotNull
   public final CipherSuite _deprecated_cipherSuite/* $FF was: -deprecated_cipherSuite*/() {
      return this.cipherSuite;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "peerCertificates",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_peerCertificates"
   )
   @NotNull
   public final List<Certificate> _deprecated_peerCertificates/* $FF was: -deprecated_peerCertificates*/() {
      return this.peerCertificates();
   }

   @JvmName(
      name = "peerPrincipal"
   )
   @Nullable
   public final Principal peerPrincipal() {
      Object var1 = CollectionsKt.firstOrNull(this.peerCertificates());
      return (Principal)((var1 instanceof X509Certificate ? (X509Certificate)var1 : null) != null ? (var1 instanceof X509Certificate ? (X509Certificate)var1 : null).getSubjectX500Principal() : null);
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "peerPrincipal",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_peerPrincipal"
   )
   @Nullable
   public final Principal _deprecated_peerPrincipal/* $FF was: -deprecated_peerPrincipal*/() {
      return this.peerPrincipal();
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "localCertificates",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_localCertificates"
   )
   @NotNull
   public final List<Certificate> _deprecated_localCertificates/* $FF was: -deprecated_localCertificates*/() {
      return this.localCertificates;
   }

   @JvmName(
      name = "localPrincipal"
   )
   @Nullable
   public final Principal localPrincipal() {
      Object var1 = CollectionsKt.firstOrNull(this.localCertificates);
      return (Principal)((var1 instanceof X509Certificate ? (X509Certificate)var1 : null) != null ? (var1 instanceof X509Certificate ? (X509Certificate)var1 : null).getSubjectX500Principal() : null);
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "localPrincipal",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_localPrincipal"
   )
   @Nullable
   public final Principal _deprecated_localPrincipal/* $FF was: -deprecated_localPrincipal*/() {
      return this.localPrincipal();
   }

   public boolean equals(@Nullable Object other) {
      return other instanceof Handshake && ((Handshake)other).tlsVersion == this.tlsVersion && Intrinsics.areEqual((Object)((Handshake)other).cipherSuite, (Object)this.cipherSuite) && Intrinsics.areEqual((Object)((Handshake)other).peerCertificates(), (Object)this.peerCertificates()) && Intrinsics.areEqual((Object)((Handshake)other).localCertificates, (Object)this.localCertificates);
   }

   public int hashCode() {
      int result = 17;
      result = 31 * result + this.tlsVersion.hashCode();
      result = 31 * result + this.cipherSuite.hashCode();
      result = 31 * result + this.peerCertificates().hashCode();
      result = 31 * result + this.localCertificates.hashCode();
      return result;
   }

   @NotNull
   public String toString() {
      Iterable $this$map$iv = (Iterable)this.peerCertificates();
      int $i$f$map = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
      int $i$f$mapTo = 0;

      for(Object item$iv$iv : $this$map$iv) {
         Certificate it = (Certificate)item$iv$iv;
         int var10 = 0;
         destination$iv$iv.add(this.getName(it));
      }

      String peerCertificatesString = ((List)destination$iv$iv).toString();
      StringBuilder var10000 = (new StringBuilder()).append("Handshake{tlsVersion=").append(this.tlsVersion).append(" cipherSuite=").append(this.cipherSuite).append(" peerCertificates=").append(peerCertificatesString).append(" localCertificates=");
      $this$map$iv = (Iterable)this.localCertificates;
      StringBuilder var11 = var10000;
      $i$f$map = 0;
      destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
      $i$f$mapTo = 0;

      for(Object item$iv$iv : $this$map$iv) {
         Certificate it = (Certificate)item$iv$iv;
         int var20 = 0;
         destination$iv$iv.add(this.getName(it));
      }

      return var11.append((List)destination$iv$iv).append('}').toString();
   }

   private final String getName(Certificate $this$name) {
      String var10000;
      if ($this$name instanceof X509Certificate) {
         var10000 = ((X509Certificate)$this$name).getSubjectDN().toString();
      } else {
         var10000 = $this$name.getType();
         Intrinsics.checkNotNullExpressionValue(var10000, "getType(...)");
      }

      return var10000;
   }

   private static final List peerCertificates_delegate$lambda$0(Function0 $peerCertificatesFn) {
      List var1;
      try {
         var1 = (List)$peerCertificatesFn.invoke();
      } catch (SSLPeerUnverifiedException var3) {
         var1 = CollectionsKt.emptyList();
      }

      return var1;
   }

   @JvmStatic
   @JvmName(
      name = "get"
   )
   @NotNull
   public static final Handshake get(@NotNull SSLSession $this$get) throws IOException {
      return Companion.get($this$get);
   }

   @JvmStatic
   @NotNull
   public static final Handshake get(@NotNull TlsVersion tlsVersion, @NotNull CipherSuite cipherSuite, @NotNull List<? extends Certificate> peerCertificates, @NotNull List<? extends Certificate> localCertificates) {
      return Companion.get(tlsVersion, cipherSuite, peerCertificates, localCertificates);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0011\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007¢\u0006\u0002\b\u0007J\u0015\u0010\u0007\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\u0006H\u0007¢\u0006\u0002\b\tJ4\u0010\u0007\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fH\u0007¨\u0006\u0012"},
      d2 = {"Lokhttp3/Handshake$Companion;", "", "<init>", "()V", "handshake", "Lokhttp3/Handshake;", "Ljavax/net/ssl/SSLSession;", "get", "sslSession", "-deprecated_get", "tlsVersion", "Lokhttp3/TlsVersion;", "cipherSuite", "Lokhttp3/CipherSuite;", "peerCertificates", "", "Ljava/security/cert/Certificate;", "localCertificates", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nHandshake.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Handshake.kt\nokhttp3/Handshake$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,201:1\n1#2:202\n*E\n"})
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @JvmName(
         name = "get"
      )
      @NotNull
      public final Handshake get(@NotNull SSLSession $this$handshake) throws IOException {
         Intrinsics.checkNotNullParameter($this$handshake, "<this>");
         String var10000 = $this$handshake.getCipherSuite();
         if (var10000 == null) {
            int peerCertificatesCopy = 0;
            String var12 = "cipherSuite == null";
            throw new IllegalStateException(var12.toString());
         } else {
            String cipherSuiteString = var10000;
            if (!Intrinsics.areEqual((Object)cipherSuiteString, (Object)"TLS_NULL_WITH_NULL_NULL") && !Intrinsics.areEqual((Object)cipherSuiteString, (Object)"SSL_NULL_WITH_NULL_NULL")) {
               CipherSuite cipherSuite = CipherSuite.Companion.forJavaName(cipherSuiteString);
               var10000 = $this$handshake.getProtocol();
               if (var10000 == null) {
                  int var5 = 0;
                  String var10 = "tlsVersion == null";
                  throw new IllegalStateException(var10.toString());
               } else {
                  cipherSuiteString = var10000;
                  if (Intrinsics.areEqual((Object)"NONE", (Object)cipherSuiteString)) {
                     throw new IOException("tlsVersion == NONE");
                  } else {
                     TlsVersion tlsVersion = TlsVersion.Companion.forJavaName(cipherSuiteString);

                     List peerCertificatesCopy;
                     try {
                        peerCertificatesCopy = _UtilJvmKt.toImmutableList($this$handshake.getPeerCertificates());
                     } catch (SSLPeerUnverifiedException var8) {
                        peerCertificatesCopy = CollectionsKt.emptyList();
                     }

                     return new Handshake(tlsVersion, cipherSuite, _UtilJvmKt.toImmutableList($this$handshake.getLocalCertificates()), Companion::handshake$lambda$2);
                  }
               }
            } else {
               throw new IOException("cipherSuite == " + cipherSuiteString);
            }
         }
      }

      /** @deprecated */
      @Deprecated(
         message = "moved to extension function",
         replaceWith = @ReplaceWith(
   expression = "sslSession.handshake()",
   imports = {}
),
         level = DeprecationLevel.ERROR
      )
      @JvmName(
         name = "-deprecated_get"
      )
      @NotNull
      public final Handshake _deprecated_get/* $FF was: -deprecated_get*/(@NotNull SSLSession sslSession) throws IOException {
         Intrinsics.checkNotNullParameter(sslSession, "sslSession");
         return this.get(sslSession);
      }

      @JvmStatic
      @NotNull
      public final Handshake get(@NotNull TlsVersion tlsVersion, @NotNull CipherSuite cipherSuite, @NotNull List<? extends Certificate> peerCertificates, @NotNull List<? extends Certificate> localCertificates) {
         Intrinsics.checkNotNullParameter(tlsVersion, "tlsVersion");
         Intrinsics.checkNotNullParameter(cipherSuite, "cipherSuite");
         Intrinsics.checkNotNullParameter(peerCertificates, "peerCertificates");
         Intrinsics.checkNotNullParameter(localCertificates, "localCertificates");
         List peerCertificatesCopy = _UtilJvmKt.toImmutableList(peerCertificates);
         return new Handshake(tlsVersion, cipherSuite, _UtilJvmKt.toImmutableList(localCertificates), Companion::get$lambda$0);
      }

      private static final List handshake$lambda$2(List $peerCertificatesCopy) {
         return $peerCertificatesCopy;
      }

      private static final List get$lambda$0(List $peerCertificatesCopy) {
         return $peerCertificatesCopy;
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
