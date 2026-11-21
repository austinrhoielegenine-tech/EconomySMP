package okhttp3.internal.tls;

import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0010\"\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\u0018\u00002\u00020\u0001B\u001b\u0012\u0012\u0010\u0002\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00040\u0003\"\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006J\u0012\u0010\u000b\u001a\u0004\u0018\u00010\u00042\u0006\u0010\f\u001a\u00020\u0004H\u0016J\u0013\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0096\u0002J\b\u0010\u0011\u001a\u00020\u0012H\u0016R \u0010\u0007\u001a\u0014\u0012\u0004\u0012\u00020\t\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\n0\bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0013"},
   d2 = {"Lokhttp3/internal/tls/BasicTrustRootIndex;", "Lokhttp3/internal/tls/TrustRootIndex;", "caCerts", "", "Ljava/security/cert/X509Certificate;", "<init>", "([Ljava/security/cert/X509Certificate;)V", "subjectToCaCerts", "", "Ljavax/security/auth/x500/X500Principal;", "", "findByIssuerAndSignature", "cert", "equals", "", "other", "", "hashCode", "", "okhttp"}
)
@SourceDebugExtension({"SMAP\nBasicTrustRootIndex.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BasicTrustRootIndex.kt\nokhttp3/internal/tls/BasicTrustRootIndex\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,55:1\n382#2,7:56\n295#3,2:63\n*S KotlinDebug\n*F\n+ 1 BasicTrustRootIndex.kt\nokhttp3/internal/tls/BasicTrustRootIndex\n*L\n30#1:56,7\n39#1:63,2\n*E\n"})
public final class BasicTrustRootIndex implements TrustRootIndex {
   @NotNull
   private final Map<X500Principal, Set<X509Certificate>> subjectToCaCerts;

   public BasicTrustRootIndex(@NotNull X509Certificate... caCerts) {
      Intrinsics.checkNotNullParameter(caCerts, "caCerts");
      super();
      Map map = (Map)(new LinkedHashMap());
      int var3 = 0;

      for(int var4 = caCerts.length; var3 < var4; ++var3) {
         X509Certificate caCert = caCerts[var3];
         Object key$iv = caCert.getSubjectX500Principal();
         int $i$f$getOrPut = 0;
         Object value$iv = map.get(key$iv);
         Object var10000;
         if (value$iv == null) {
            int var10 = 0;
            Object answer$iv = (Set)(new LinkedHashSet());
            map.put(key$iv, answer$iv);
            var10000 = answer$iv;
         } else {
            var10000 = value$iv;
         }

         ((Set)var10000).add(caCert);
      }

      this.subjectToCaCerts = map;
   }

   @Nullable
   public X509Certificate findByIssuerAndSignature(@NotNull X509Certificate cert) {
      Intrinsics.checkNotNullParameter(cert, "cert");
      X500Principal issuer = cert.getIssuerX500Principal();
      Set var10000 = (Set)this.subjectToCaCerts.get(issuer);
      if (var10000 == null) {
         return null;
      } else {
         Set subjectCaCerts = var10000;
         Iterable $this$firstOrNull$iv = (Iterable)subjectCaCerts;
         int $i$f$firstOrNull = 0;
         Iterator var6 = $this$firstOrNull$iv.iterator();

         while(true) {
            if (var6.hasNext()) {
               Object element$iv = var6.next();
               X509Certificate it = (X509Certificate)element$iv;
               int var9 = 0;

               try {
                  cert.verify(it.getPublicKey());
                  var13 = true;
               } catch (Exception var11) {
                  var13 = false;
               }

               if (!var13) {
                  continue;
               }

               var12 = element$iv;
               break;
            }

            var12 = null;
            break;
         }

         return (X509Certificate)var12;
      }
   }

   public boolean equals(@Nullable Object other) {
      return other == this || other instanceof BasicTrustRootIndex && Intrinsics.areEqual((Object)((BasicTrustRootIndex)other).subjectToCaCerts, (Object)this.subjectToCaCerts);
   }

   public int hashCode() {
      return this.subjectToCaCerts.hashCode();
   }
}
