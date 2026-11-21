package okhttp3.internal.platform;

import java.security.Security;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\t\u001a\u00020\nR\u0014\u0010\u0004\u001a\u00020\u00058BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0004\u0010\u0006R\u0014\u0010\u0007\u001a\u00020\u00058BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0007\u0010\u0006R\u0014\u0010\b\u001a\u00020\u00058BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\b\u0010\u0006R\u0011\u0010\u000b\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u000b\u0010\u0006¨\u0006\f"},
   d2 = {"Lokhttp3/internal/platform/PlatformRegistry;", "", "<init>", "()V", "isConscryptPreferred", "", "()Z", "isOpenJSSEPreferred", "isBouncyCastlePreferred", "findPlatform", "Lokhttp3/internal/platform/Platform;", "isAndroid", "okhttp"}
)
public final class PlatformRegistry {
   @NotNull
   public static final PlatformRegistry INSTANCE = new PlatformRegistry();

   private PlatformRegistry() {
   }

   private final boolean isConscryptPreferred() {
      String preferredProvider = Security.getProviders()[0].getName();
      return Intrinsics.areEqual((Object)"Conscrypt", (Object)preferredProvider);
   }

   private final boolean isOpenJSSEPreferred() {
      String preferredProvider = Security.getProviders()[0].getName();
      return Intrinsics.areEqual((Object)"OpenJSSE", (Object)preferredProvider);
   }

   private final boolean isBouncyCastlePreferred() {
      String preferredProvider = Security.getProviders()[0].getName();
      return Intrinsics.areEqual((Object)"BC", (Object)preferredProvider);
   }

   @NotNull
   public final Platform findPlatform() {
      if (this.isConscryptPreferred()) {
         ConscryptPlatform conscrypt = ConscryptPlatform.Companion.buildIfSupported();
         if (conscrypt != null) {
            return conscrypt;
         }
      }

      if (this.isBouncyCastlePreferred()) {
         BouncyCastlePlatform bc = BouncyCastlePlatform.Companion.buildIfSupported();
         if (bc != null) {
            return bc;
         }
      }

      if (this.isOpenJSSEPreferred()) {
         OpenJSSEPlatform openJSSE = OpenJSSEPlatform.Companion.buildIfSupported();
         if (openJSSE != null) {
            return openJSSE;
         }
      }

      Jdk9Platform jdk9 = Jdk9Platform.Companion.buildIfSupported();
      if (jdk9 != null) {
         return jdk9;
      } else {
         Platform jdkWithJettyBoot = Jdk8WithJettyBootPlatform.Companion.buildIfSupported();
         return jdkWithJettyBoot != null ? jdkWithJettyBoot : new Platform();
      }
   }

   public final boolean isAndroid() {
      return false;
   }
}
