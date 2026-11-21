package okhttp3;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLPeerUnverifiedException;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.jvm.internal.TypeIntrinsics;
import kotlin.text.StringsKt;
import okhttp3.internal._HostnamesCommonKt;
import okhttp3.internal.tls.CertificateChainCleaner;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\u0018\u0000 $2\u00020\u0001:\u0003\"#$B#\b\u0000\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u001c\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012J)\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0012\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00160\u00120\u0015H\u0000¢\u0006\u0002\b\u0017J)\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0012\u0010\u0011\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00130\u0018\"\u00020\u0013H\u0007¢\u0006\u0002\u0010\u0019J\u0014\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00040\u00122\u0006\u0010\u000f\u001a\u00020\u0010J\u0015\u0010\u001b\u001a\u00020\u00002\u0006\u0010\u0005\u001a\u00020\u0006H\u0000¢\u0006\u0002\b\u001cJ\u0013\u0010\u001d\u001a\u00020\u001e2\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010 \u001a\u00020!H\u0016R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0016\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006%"},
   d2 = {"Lokhttp3/CertificatePinner;", "", "pins", "", "Lokhttp3/CertificatePinner$Pin;", "certificateChainCleaner", "Lokhttp3/internal/tls/CertificateChainCleaner;", "<init>", "(Ljava/util/Set;Lokhttp3/internal/tls/CertificateChainCleaner;)V", "getPins", "()Ljava/util/Set;", "getCertificateChainCleaner$okhttp", "()Lokhttp3/internal/tls/CertificateChainCleaner;", "check", "", "hostname", "", "peerCertificates", "", "Ljava/security/cert/Certificate;", "cleanedPeerCertificatesFn", "Lkotlin/Function0;", "Ljava/security/cert/X509Certificate;", "check$okhttp", "", "(Ljava/lang/String;[Ljava/security/cert/Certificate;)V", "findMatchingPins", "withCertificateChainCleaner", "withCertificateChainCleaner$okhttp", "equals", "", "other", "hashCode", "", "Pin", "Builder", "Companion", "okhttp"}
)
@SourceDebugExtension({"SMAP\nCertificatePinner.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CertificatePinner.kt\nokhttp3/CertificatePinner\n+ 2 -UtilCommon.kt\nokhttp3/internal/_UtilCommonKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,378:1\n360#2,8:379\n1563#3:387\n1634#3,3:388\n*S KotlinDebug\n*F\n+ 1 CertificatePinner.kt\nokhttp3/CertificatePinner\n*L\n224#1:379,8\n154#1:387\n154#1:388,3\n*E\n"})
public final class CertificatePinner {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final Set<Pin> pins;
   @Nullable
   private final CertificateChainCleaner certificateChainCleaner;
   @JvmField
   @NotNull
   public static final CertificatePinner DEFAULT = (new Builder()).build();

   public CertificatePinner(@NotNull Set<Pin> pins, @Nullable CertificateChainCleaner certificateChainCleaner) {
      Intrinsics.checkNotNullParameter(pins, "pins");
      super();
      this.pins = pins;
      this.certificateChainCleaner = certificateChainCleaner;
   }

   // $FF: synthetic method
   public CertificatePinner(Set var1, CertificateChainCleaner var2, int var3, DefaultConstructorMarker var4) {
      if ((var3 & 2) != 0) {
         var2 = null;
      }

      this(var1, var2);
   }

   @NotNull
   public final Set<Pin> getPins() {
      return this.pins;
   }

   @Nullable
   public final CertificateChainCleaner getCertificateChainCleaner$okhttp() {
      return this.certificateChainCleaner;
   }

   public final void check(@NotNull String hostname, @NotNull List<? extends Certificate> peerCertificates) throws SSLPeerUnverifiedException {
      Intrinsics.checkNotNullParameter(hostname, "hostname");
      Intrinsics.checkNotNullParameter(peerCertificates, "peerCertificates");
      this.check$okhttp(hostname, CertificatePinner::check$lambda$0);
   }

   public final void check$okhttp(@NotNull String hostname, @NotNull Function0<? extends List<? extends X509Certificate>> cleanedPeerCertificatesFn) {
      Intrinsics.checkNotNullParameter(hostname, "hostname");
      Intrinsics.checkNotNullParameter(cleanedPeerCertificatesFn, "cleanedPeerCertificatesFn");
      List pins = this.findMatchingPins(hostname);
      if (!pins.isEmpty()) {
         List peerCertificates = (List)cleanedPeerCertificatesFn.invoke();

         for(X509Certificate peerCertificate : peerCertificates) {
            ByteString sha1 = null;
            ByteString sha256 = null;

            for(Pin pin : pins) {
               String var11 = pin.getHashAlgorithm();
               if (Intrinsics.areEqual((Object)var11, (Object)"sha256")) {
                  if (sha256 == null) {
                     sha256 = Companion.sha256Hash(peerCertificate);
                  }

                  if (Intrinsics.areEqual((Object)pin.getHash(), (Object)sha256)) {
                     return;
                  }
               } else {
                  if (!Intrinsics.areEqual((Object)var11, (Object)"sha1")) {
                     throw new AssertionError("unsupported hashAlgorithm: " + pin.getHashAlgorithm());
                  }

                  if (sha1 == null) {
                     sha1 = Companion.sha1Hash(peerCertificate);
                  }

                  if (Intrinsics.areEqual((Object)pin.getHash(), (Object)sha1)) {
                     return;
                  }
               }
            }
         }

         StringBuilder var13 = new StringBuilder();
         StringBuilder $this$check_u24lambda_u241 = var13;
         int var15 = 0;
         var13.append("Certificate pinning failure!");
         var13.append("\n  Peer certificate chain:");

         for(X509Certificate element : peerCertificates) {
            $this$check_u24lambda_u241.append("\n    ");
            $this$check_u24lambda_u241.append(Companion.pin((Certificate)element));
            $this$check_u24lambda_u241.append(": ");
            $this$check_u24lambda_u241.append(element.getSubjectDN().getName());
         }

         $this$check_u24lambda_u241.append("\n  Pinned certificates for ");
         $this$check_u24lambda_u241.append(hostname);
         $this$check_u24lambda_u241.append(":");

         for(Pin pin : pins) {
            $this$check_u24lambda_u241.append("\n    ");
            $this$check_u24lambda_u241.append(pin);
         }

         String message = var13.toString();
         throw new SSLPeerUnverifiedException(message);
      }
   }

   /** @deprecated */
   @Deprecated(
      message = "replaced with {@link #check(String, List)}.",
      replaceWith = @ReplaceWith(
   expression = "check(hostname, peerCertificates.toList())",
   imports = {}
)
   )
   public final void check(@NotNull String hostname, @NotNull Certificate... peerCertificates) throws SSLPeerUnverifiedException {
      Intrinsics.checkNotNullParameter(hostname, "hostname");
      Intrinsics.checkNotNullParameter(peerCertificates, "peerCertificates");
      this.check(hostname, ArraysKt.toList(peerCertificates));
   }

   @NotNull
   public final List<Pin> findMatchingPins(@NotNull String hostname) {
      Intrinsics.checkNotNullParameter(hostname, "hostname");
      Iterable $this$filterList$iv = (Iterable)this.pins;
      int $i$f$filterList = 0;
      List result$iv = CollectionsKt.emptyList();

      for(Object i$iv : $this$filterList$iv) {
         Pin $this$findMatchingPins_u24lambda_u240 = (Pin)i$iv;
         int var8 = 0;
         if ($this$findMatchingPins_u24lambda_u240.matchesHostname(hostname)) {
            if (result$iv.isEmpty()) {
               result$iv = (List)(new ArrayList());
            }

            Intrinsics.checkNotNull(result$iv, "null cannot be cast to non-null type kotlin.collections.MutableList<T of okhttp3.internal._UtilCommonKt.filterList>");
            TypeIntrinsics.asMutableList(result$iv).add(i$iv);
         }
      }

      return result$iv;
   }

   @NotNull
   public final CertificatePinner withCertificateChainCleaner$okhttp(@NotNull CertificateChainCleaner certificateChainCleaner) {
      Intrinsics.checkNotNullParameter(certificateChainCleaner, "certificateChainCleaner");
      return Intrinsics.areEqual((Object)this.certificateChainCleaner, (Object)certificateChainCleaner) ? this : new CertificatePinner(this.pins, certificateChainCleaner);
   }

   public boolean equals(@Nullable Object other) {
      return other instanceof CertificatePinner && Intrinsics.areEqual((Object)((CertificatePinner)other).pins, (Object)this.pins) && Intrinsics.areEqual((Object)((CertificatePinner)other).certificateChainCleaner, (Object)this.certificateChainCleaner);
   }

   public int hashCode() {
      int result = 37;
      result = 41 * result + this.pins.hashCode();
      int var10000 = 41 * result;
      CertificateChainCleaner var10001 = this.certificateChainCleaner;
      result = var10000 + (var10001 != null ? var10001.hashCode() : 0);
      return result;
   }

   private static final List check$lambda$0(CertificatePinner this$0, List $peerCertificates, String $hostname) {
      List var13;
      label20: {
         CertificateChainCleaner var10000 = this$0.certificateChainCleaner;
         if (var10000 != null) {
            var13 = var10000.clean($peerCertificates, $hostname);
            if (var13 != null) {
               break label20;
            }
         }

         var13 = $peerCertificates;
      }

      Iterable $this$map$iv = (Iterable)var13;
      int $i$f$map = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
      int $i$f$mapTo = 0;

      for(Object item$iv$iv : $this$map$iv) {
         Certificate it = (Certificate)item$iv$iv;
         int var11 = 0;
         Intrinsics.checkNotNull(it, "null cannot be cast to non-null type java.security.cert.X509Certificate");
         destination$iv$iv.add((X509Certificate)it);
      }

      return (List)destination$iv$iv;
   }

   @JvmStatic
   @NotNull
   public static final ByteString sha1Hash(@NotNull X509Certificate $this$sha1Hash) {
      return Companion.sha1Hash($this$sha1Hash);
   }

   @JvmStatic
   @NotNull
   public static final ByteString sha256Hash(@NotNull X509Certificate $this$sha256Hash) {
      return Companion.sha256Hash($this$sha256Hash);
   }

   @JvmStatic
   @NotNull
   public static final String pin(@NotNull Certificate certificate) {
      return Companion.pin(certificate);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\u000e\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0003J\u000e\u0010\u0012\u001a\u00020\u00102\u0006\u0010\u0013\u001a\u00020\u0014J\b\u0010\u0015\u001a\u00020\u0003H\u0016J\u0013\u0010\u0016\u001a\u00020\u00102\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010\u0018\u001a\u00020\u0019H\u0016R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\t\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\bR\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e¨\u0006\u001a"},
      d2 = {"Lokhttp3/CertificatePinner$Pin;", "", "pattern", "", "pin", "<init>", "(Ljava/lang/String;Ljava/lang/String;)V", "getPattern", "()Ljava/lang/String;", "hashAlgorithm", "getHashAlgorithm", "hash", "Lokio/ByteString;", "getHash", "()Lokio/ByteString;", "matchesHostname", "", "hostname", "matchesCertificate", "certificate", "Ljava/security/cert/X509Certificate;", "toString", "equals", "other", "hashCode", "", "okhttp"}
   )
   public static final class Pin {
      @NotNull
      private final String pattern;
      @NotNull
      private final String hashAlgorithm;
      @NotNull
      private final ByteString hash;

      public Pin(@NotNull String pattern, @NotNull String pin) {
         Intrinsics.checkNotNullParameter(pattern, "pattern");
         Intrinsics.checkNotNullParameter(pin, "pin");
         super();
         if ((!StringsKt.startsWith$default(pattern, "*.", false, 2, (Object)null) || StringsKt.indexOf$default((CharSequence)pattern, "*", 1, false, 4, (Object)null) != -1) && (!StringsKt.startsWith$default(pattern, "**.", false, 2, (Object)null) || StringsKt.indexOf$default((CharSequence)pattern, "*", 2, false, 4, (Object)null) != -1) && StringsKt.indexOf$default((CharSequence)pattern, "*", 0, false, 6, (Object)null) != -1) {
            int var3 = 0;
            String var4 = "Unexpected pattern: " + pattern;
            throw new IllegalArgumentException(var4.toString());
         } else {
            String var10001 = _HostnamesCommonKt.toCanonicalHost(pattern);
            if (var10001 == null) {
               throw new IllegalArgumentException("Invalid pattern: " + pattern);
            } else {
               this.pattern = var10001;
               if (StringsKt.startsWith$default(pin, "sha1/", false, 2, (Object)null)) {
                  this.hashAlgorithm = "sha1";
                  ByteString.Companion var5 = ByteString.Companion;
                  String var10002 = pin.substring(5);
                  Intrinsics.checkNotNullExpressionValue(var10002, "substring(...)");
                  ByteString var6 = var5.decodeBase64(var10002);
                  if (var6 == null) {
                     throw new IllegalArgumentException("Invalid pin hash: " + pin);
                  }

                  this.hash = var6;
               } else {
                  if (!StringsKt.startsWith$default(pin, "sha256/", false, 2, (Object)null)) {
                     throw new IllegalArgumentException("pins must start with 'sha256/' or 'sha1/': " + pin);
                  }

                  this.hashAlgorithm = "sha256";
                  ByteString.Companion var7 = ByteString.Companion;
                  String var9 = pin.substring(7);
                  Intrinsics.checkNotNullExpressionValue(var9, "substring(...)");
                  ByteString var8 = var7.decodeBase64(var9);
                  if (var8 == null) {
                     throw new IllegalArgumentException("Invalid pin hash: " + pin);
                  }

                  this.hash = var8;
               }

            }
         }
      }

      @NotNull
      public final String getPattern() {
         return this.pattern;
      }

      @NotNull
      public final String getHashAlgorithm() {
         return this.hashAlgorithm;
      }

      @NotNull
      public final ByteString getHash() {
         return this.hash;
      }

      public final boolean matchesHostname(@NotNull String hostname) {
         Intrinsics.checkNotNullParameter(hostname, "hostname");
         boolean var10000;
         if (StringsKt.startsWith$default(this.pattern, "**.", false, 2, (Object)null)) {
            int suffixLength = this.pattern.length() - 3;
            int prefixLength = hostname.length() - suffixLength;
            var10000 = StringsKt.regionMatches$default(hostname, hostname.length() - suffixLength, this.pattern, 3, suffixLength, false, 16, (Object)null) && (prefixLength == 0 || hostname.charAt(prefixLength - 1) == '.');
         } else if (StringsKt.startsWith$default(this.pattern, "*.", false, 2, (Object)null)) {
            int suffixLength = this.pattern.length() - 1;
            int prefixLength = hostname.length() - suffixLength;
            var10000 = StringsKt.regionMatches$default(hostname, hostname.length() - suffixLength, this.pattern, 1, suffixLength, false, 16, (Object)null) && StringsKt.lastIndexOf$default((CharSequence)hostname, '.', prefixLength - 1, false, 4, (Object)null) == -1;
         } else {
            var10000 = Intrinsics.areEqual((Object)hostname, (Object)this.pattern);
         }

         return var10000;
      }

      public final boolean matchesCertificate(@NotNull X509Certificate certificate) {
         Intrinsics.checkNotNullParameter(certificate, "certificate");
         String var2 = this.hashAlgorithm;
         return Intrinsics.areEqual((Object)var2, (Object)"sha256") ? Intrinsics.areEqual((Object)this.hash, (Object)CertificatePinner.Companion.sha256Hash(certificate)) : (Intrinsics.areEqual((Object)var2, (Object)"sha1") ? Intrinsics.areEqual((Object)this.hash, (Object)CertificatePinner.Companion.sha1Hash(certificate)) : false);
      }

      @NotNull
      public String toString() {
         return this.hashAlgorithm + '/' + this.hash.base64();
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof Pin)) {
            return false;
         } else if (!Intrinsics.areEqual((Object)this.pattern, (Object)((Pin)other).pattern)) {
            return false;
         } else if (!Intrinsics.areEqual((Object)this.hashAlgorithm, (Object)((Pin)other).hashAlgorithm)) {
            return false;
         } else {
            return Intrinsics.areEqual((Object)this.hash, (Object)((Pin)other).hash);
         }
      }

      public int hashCode() {
         int result = this.pattern.hashCode();
         result = 31 * result + this.hashAlgorithm.hashCode();
         result = 31 * result + this.hash.hashCode();
         return result;
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J'\u0010\t\u001a\u00020\u00002\u0006\u0010\n\u001a\u00020\u000b2\u0012\u0010\u0004\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u000b0\f\"\u00020\u000b¢\u0006\u0002\u0010\rJ\u0006\u0010\u000e\u001a\u00020\u000fR\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\u0010"},
      d2 = {"Lokhttp3/CertificatePinner$Builder;", "", "<init>", "()V", "pins", "", "Lokhttp3/CertificatePinner$Pin;", "getPins", "()Ljava/util/List;", "add", "pattern", "", "", "(Ljava/lang/String;[Ljava/lang/String;)Lokhttp3/CertificatePinner$Builder;", "build", "Lokhttp3/CertificatePinner;", "okhttp"}
   )
   public static final class Builder {
      @NotNull
      private final List<Pin> pins = (List)(new ArrayList());

      @NotNull
      public final List<Pin> getPins() {
         return this.pins;
      }

      @NotNull
      public final Builder add(@NotNull String pattern, @NotNull String... pins) {
         Intrinsics.checkNotNullParameter(pattern, "pattern");
         Intrinsics.checkNotNullParameter(pins, "pins");
         Builder $this$add_u24lambda_u240 = this;
         int var5 = 0;
         int var6 = 0;

         for(int var7 = pins.length; var6 < var7; ++var6) {
            String pin = pins[var6];
            $this$add_u24lambda_u240.pins.add(new Pin(pattern, pin));
         }

         return this;
      }

      @NotNull
      public final CertificatePinner build() {
         return new CertificatePinner(CollectionsKt.toSet((Iterable)this.pins), (CertificateChainCleaner)null, 2, (DefaultConstructorMarker)null);
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\f\u0010\u0006\u001a\u00020\u0007*\u00020\bH\u0007J\f\u0010\t\u001a\u00020\u0007*\u00020\bH\u0007J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0007R\u0010\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e"},
      d2 = {"Lokhttp3/CertificatePinner$Companion;", "", "<init>", "()V", "DEFAULT", "Lokhttp3/CertificatePinner;", "sha1Hash", "Lokio/ByteString;", "Ljava/security/cert/X509Certificate;", "sha256Hash", "pin", "", "certificate", "Ljava/security/cert/Certificate;", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nCertificatePinner.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CertificatePinner.kt\nokhttp3/CertificatePinner$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,378:1\n1#2:379\n*E\n"})
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @NotNull
      public final ByteString sha1Hash(@NotNull X509Certificate $this$sha1Hash) {
         Intrinsics.checkNotNullParameter($this$sha1Hash, "<this>");
         ByteString.Companion var10000 = ByteString.Companion;
         byte[] var10001 = $this$sha1Hash.getPublicKey().getEncoded();
         Intrinsics.checkNotNullExpressionValue(var10001, "getEncoded(...)");
         return ByteString.Companion.of$default(var10000, var10001, 0, 0, 3, (Object)null).sha1();
      }

      @JvmStatic
      @NotNull
      public final ByteString sha256Hash(@NotNull X509Certificate $this$sha256Hash) {
         Intrinsics.checkNotNullParameter($this$sha256Hash, "<this>");
         ByteString.Companion var10000 = ByteString.Companion;
         byte[] var10001 = $this$sha256Hash.getPublicKey().getEncoded();
         Intrinsics.checkNotNullExpressionValue(var10001, "getEncoded(...)");
         return ByteString.Companion.of$default(var10000, var10001, 0, 0, 3, (Object)null).sha256();
      }

      @JvmStatic
      @NotNull
      public final String pin(@NotNull Certificate certificate) {
         Intrinsics.checkNotNullParameter(certificate, "certificate");
         if (!(certificate instanceof X509Certificate)) {
            int var2 = 0;
            String var3 = "Certificate pinning requires X509 certificates";
            throw new IllegalArgumentException(var3.toString());
         } else {
            return "sha256/" + this.sha256Hash((X509Certificate)certificate).base64();
         }
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
