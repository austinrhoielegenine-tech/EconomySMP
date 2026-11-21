package okhttp3.internal.tls;

import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.SSLPeerUnverifiedException;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0000\n\u0002\b\u0002\u0018\u0000 \u00172\u00020\u0001:\u0001\u0017B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J$\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\n\u001a\u00020\u000bH\u0016J \u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\b\u0010\u0013\u001a\u00020\u0012H\u0016J\u0013\u0010\u0014\u001a\u00020\r2\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016H\u0096\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0018"},
   d2 = {"Lokhttp3/internal/tls/BasicCertificateChainCleaner;", "Lokhttp3/internal/tls/CertificateChainCleaner;", "trustRootIndex", "Lokhttp3/internal/tls/TrustRootIndex;", "<init>", "(Lokhttp3/internal/tls/TrustRootIndex;)V", "clean", "", "Ljava/security/cert/Certificate;", "chain", "hostname", "", "verifySignature", "", "toVerify", "Ljava/security/cert/X509Certificate;", "signingCert", "minIntermediates", "", "hashCode", "equals", "other", "", "Companion", "okhttp"}
)
public final class BasicCertificateChainCleaner extends CertificateChainCleaner {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final TrustRootIndex trustRootIndex;
   private static final int MAX_SIGNERS = 9;

   public BasicCertificateChainCleaner(@NotNull TrustRootIndex trustRootIndex) {
      Intrinsics.checkNotNullParameter(trustRootIndex, "trustRootIndex");
      super();
      this.trustRootIndex = trustRootIndex;
   }

   @NotNull
   public List<Certificate> clean(@NotNull List<? extends Certificate> chain, @NotNull String hostname) throws SSLPeerUnverifiedException {
      Intrinsics.checkNotNullParameter(chain, "chain");
      Intrinsics.checkNotNullParameter(hostname, "hostname");
      Deque queue = (Deque)(new ArrayDeque((Collection)chain));
      List result = (List)(new ArrayList());
      Object var10001 = queue.removeFirst();
      Intrinsics.checkNotNullExpressionValue(var10001, "removeFirst(...)");
      result.add(var10001);
      boolean foundTrustedCertificate = false;

      for(int c = 0; c < 9; ++c) {
         Object var10000 = result.get(result.size() - 1);
         Intrinsics.checkNotNull(var10000, "null cannot be cast to non-null type java.security.cert.X509Certificate");
         X509Certificate toVerify = (X509Certificate)var10000;
         X509Certificate trustedCert = this.trustRootIndex.findByIssuerAndSignature(toVerify);
         if (trustedCert == null) {
            Iterator var11 = queue.iterator();
            Intrinsics.checkNotNullExpressionValue(var11, "iterator(...)");
            Iterator i = var11;

            X509Certificate signingCert;
            do {
               if (!i.hasNext()) {
                  if (foundTrustedCertificate) {
                     return result;
                  }

                  throw new SSLPeerUnverifiedException("Failed to find a trusted cert that signed " + toVerify);
               }

               Object var12 = i.next();
               Intrinsics.checkNotNull(var12, "null cannot be cast to non-null type java.security.cert.X509Certificate");
               signingCert = (X509Certificate)var12;
            } while(!this.verifySignature(toVerify, signingCert, result.size() - 1));

            i.remove();
            result.add(signingCert);
         } else {
            if (result.size() > 1 || !Intrinsics.areEqual((Object)toVerify, (Object)trustedCert)) {
               result.add(trustedCert);
            }

            if (this.verifySignature(trustedCert, trustedCert, result.size() - 2)) {
               return result;
            }

            foundTrustedCertificate = true;
         }
      }

      throw new SSLPeerUnverifiedException("Certificate chain too long: " + result);
   }

   private final boolean verifySignature(X509Certificate toVerify, X509Certificate signingCert, int minIntermediates) {
      if (!Intrinsics.areEqual((Object)toVerify.getIssuerDN(), (Object)signingCert.getSubjectDN())) {
         return false;
      } else if (signingCert.getBasicConstraints() < minIntermediates) {
         return false;
      } else {
         boolean var4;
         try {
            toVerify.verify(signingCert.getPublicKey());
            var4 = true;
         } catch (GeneralSecurityException var6) {
            var4 = false;
         }

         return var4;
      }
   }

   public int hashCode() {
      return this.trustRootIndex.hashCode();
   }

   public boolean equals(@Nullable Object other) {
      return other == this ? true : other instanceof BasicCertificateChainCleaner && Intrinsics.areEqual((Object)((BasicCertificateChainCleaner)other).trustRootIndex, (Object)this.trustRootIndex);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0006"},
      d2 = {"Lokhttp3/internal/tls/BasicCertificateChainCleaner$Companion;", "", "<init>", "()V", "MAX_SIGNERS", "", "okhttp"}
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
