package okhttp3.internal.tls;

import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.internal._HostnamesCommonKt;
import okio.Utf8;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010 \n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\u000eJ\u0018\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0010\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\u000eH\u0002J\u0018\u0010\u0011\u001a\u00020\b2\u0006\u0010\u0012\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\u000eH\u0002J\f\u0010\u0013\u001a\u00020\n*\u00020\nH\u0002J\f\u0010\u0014\u001a\u00020\b*\u00020\nH\u0002J\u001c\u0010\u0011\u001a\u00020\b2\b\u0010\u0012\u001a\u0004\u0018\u00010\n2\b\u0010\u0015\u001a\u0004\u0018\u00010\nH\u0002J\u0014\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\n0\u00172\u0006\u0010\r\u001a\u00020\u000eJ\u001e\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\n0\u00172\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0019\u001a\u00020\u0005H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u001a"},
   d2 = {"Lokhttp3/internal/tls/OkHostnameVerifier;", "Ljavax/net/ssl/HostnameVerifier;", "<init>", "()V", "ALT_DNS_NAME", "", "ALT_IPA_NAME", "verify", "", "host", "", "session", "Ljavax/net/ssl/SSLSession;", "certificate", "Ljava/security/cert/X509Certificate;", "verifyIpAddress", "ipAddress", "verifyHostname", "hostname", "asciiToLowercase", "isAscii", "pattern", "allSubjectAltNames", "", "getSubjectAltNames", "type", "okhttp"}
)
@SourceDebugExtension({"SMAP\nOkHostnameVerifier.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OkHostnameVerifier.kt\nokhttp3/internal/tls/OkHostnameVerifier\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,224:1\n1761#2,3:225\n1761#2,3:228\n*S KotlinDebug\n*F\n+ 1 OkHostnameVerifier.kt\nokhttp3/internal/tls/OkHostnameVerifier\n*L\n69#1:225,3\n80#1:228,3\n*E\n"})
public final class OkHostnameVerifier implements HostnameVerifier {
   @NotNull
   public static final OkHostnameVerifier INSTANCE = new OkHostnameVerifier();
   private static final int ALT_DNS_NAME = 2;
   private static final int ALT_IPA_NAME = 7;

   private OkHostnameVerifier() {
   }

   public boolean verify(@NotNull String host, @NotNull SSLSession session) {
      Intrinsics.checkNotNullParameter(host, "host");
      Intrinsics.checkNotNullParameter(session, "session");
      boolean var10000;
      if (!this.isAscii(host)) {
         var10000 = false;
      } else {
         boolean var3;
         try {
            Certificate var10002 = session.getPeerCertificates()[0];
            Intrinsics.checkNotNull(var10002, "null cannot be cast to non-null type java.security.cert.X509Certificate");
            var3 = this.verify(host, (X509Certificate)var10002);
         } catch (SSLException var5) {
            var3 = false;
         }

         var10000 = var3;
      }

      return var10000;
   }

   public final boolean verify(@NotNull String host, @NotNull X509Certificate certificate) {
      Intrinsics.checkNotNullParameter(host, "host");
      Intrinsics.checkNotNullParameter(certificate, "certificate");
      return _HostnamesCommonKt.canParseAsIpAddress(host) ? this.verifyIpAddress(host, certificate) : this.verifyHostname(host, certificate);
   }

   private final boolean verifyIpAddress(String ipAddress, X509Certificate certificate) {
      String canonicalIpAddress = _HostnamesCommonKt.toCanonicalHost(ipAddress);
      Iterable $this$any$iv = (Iterable)this.getSubjectAltNames(certificate, 7);
      int $i$f$any = 0;
      boolean var10000;
      if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
         var10000 = false;
      } else {
         Iterator var6 = $this$any$iv.iterator();

         while(true) {
            if (!var6.hasNext()) {
               var10000 = false;
               break;
            }

            Object element$iv = var6.next();
            String it = (String)element$iv;
            int var9 = 0;
            if (Intrinsics.areEqual((Object)canonicalIpAddress, (Object)_HostnamesCommonKt.toCanonicalHost(it))) {
               var10000 = true;
               break;
            }
         }
      }

      return var10000;
   }

   private final boolean verifyHostname(String hostname, X509Certificate certificate) {
      String hostname = this.asciiToLowercase(hostname);
      Iterable $this$any$iv = (Iterable)this.getSubjectAltNames(certificate, 2);
      int $i$f$any = 0;
      boolean var10000;
      if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
         var10000 = false;
      } else {
         Iterator var6 = $this$any$iv.iterator();

         while(true) {
            if (!var6.hasNext()) {
               var10000 = false;
               break;
            }

            Object element$iv = var6.next();
            String it = (String)element$iv;
            int var9 = 0;
            if (INSTANCE.verifyHostname(hostname, it)) {
               var10000 = true;
               break;
            }
         }
      }

      return var10000;
   }

   private final String asciiToLowercase(String $this$asciiToLowercase) {
      String var3;
      if (this.isAscii($this$asciiToLowercase)) {
         Locale var10000 = Locale.US;
         Intrinsics.checkNotNullExpressionValue(var10000, "US");
         var3 = $this$asciiToLowercase.toLowerCase(var10000);
         Intrinsics.checkNotNullExpressionValue(var3, "toLowerCase(...)");
      } else {
         var3 = $this$asciiToLowercase;
      }

      return var3;
   }

   private final boolean isAscii(String $this$isAscii) {
      return $this$isAscii.length() == (int)Utf8.size$default($this$isAscii, 0, 0, 3, (Object)null);
   }

   private final boolean verifyHostname(String hostname, String pattern) {
      String hostname = hostname;
      String pattern = pattern;
      CharSequence var5 = (CharSequence)hostname;
      if (var5 != null && var5.length() != 0 && !StringsKt.startsWith$default(hostname, ".", false, 2, (Object)null) && !StringsKt.endsWith$default(hostname, "..", false, 2, (Object)null)) {
         var5 = (CharSequence)pattern;
         if (var5 != null && var5.length() != 0 && !StringsKt.startsWith$default(pattern, ".", false, 2, (Object)null) && !StringsKt.endsWith$default(pattern, "..", false, 2, (Object)null)) {
            if (!StringsKt.endsWith$default(hostname, ".", false, 2, (Object)null)) {
               hostname = hostname + '.';
            }

            if (!StringsKt.endsWith$default(pattern, ".", false, 2, (Object)null)) {
               pattern = pattern + '.';
            }

            pattern = this.asciiToLowercase(pattern);
            if (!StringsKt.contains$default((CharSequence)pattern, (CharSequence)"*", false, 2, (Object)null)) {
               return Intrinsics.areEqual((Object)hostname, (Object)pattern);
            } else if (StringsKt.startsWith$default(pattern, "*.", false, 2, (Object)null) && StringsKt.indexOf$default((CharSequence)pattern, '*', 1, false, 4, (Object)null) == -1) {
               if (hostname.length() < pattern.length()) {
                  return false;
               } else if (Intrinsics.areEqual((Object)"*.", (Object)pattern)) {
                  return false;
               } else {
                  String var10000 = pattern.substring(1);
                  Intrinsics.checkNotNullExpressionValue(var10000, "substring(...)");
                  String suffix = var10000;
                  if (!StringsKt.endsWith$default(hostname, suffix, false, 2, (Object)null)) {
                     return false;
                  } else {
                     int suffixStartIndexInHostname = hostname.length() - suffix.length();
                     return suffixStartIndexInHostname <= 0 || StringsKt.lastIndexOf$default((CharSequence)hostname, '.', suffixStartIndexInHostname - 1, false, 4, (Object)null) == -1;
                  }
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @NotNull
   public final List<String> allSubjectAltNames(@NotNull X509Certificate certificate) {
      Intrinsics.checkNotNullParameter(certificate, "certificate");
      List altIpaNames = this.getSubjectAltNames(certificate, 7);
      List altDnsNames = this.getSubjectAltNames(certificate, 2);
      return CollectionsKt.plus((Collection)altIpaNames, (Iterable)altDnsNames);
   }

   private final List<String> getSubjectAltNames(X509Certificate certificate, int type) {
      try {
         Collection var10000 = certificate.getSubjectAlternativeNames();
         if (var10000 == null) {
            return CollectionsKt.emptyList();
         } else {
            Collection subjectAltNames = var10000;
            List result = (List)(new ArrayList());

            for(List subjectAltName : subjectAltNames) {
               if (subjectAltName != null && subjectAltName.size() >= 2 && Intrinsics.areEqual((Object)subjectAltName.get(0), (Object)type)) {
                  Object var9 = subjectAltName.get(1);
                  if (var9 != null) {
                     Object altName = var9;
                     result.add((String)altName);
                  }
               }
            }

            return result;
         }
      } catch (CertificateParsingException var8) {
         return CollectionsKt.emptyList();
      }
   }
}
