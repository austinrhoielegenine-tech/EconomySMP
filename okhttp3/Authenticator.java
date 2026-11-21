package okhttp3;

import java.io.IOException;
import kotlin.Metadata;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.internal.authenticator.JavaNetAuthenticator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bæ\u0080\u0001\u0018\u0000 \b2\u00020\u0001:\u0001\bJ\u001c\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u0007H&¨\u0006\tÀ\u0006\u0003"},
   d2 = {"Lokhttp3/Authenticator;", "", "authenticate", "Lokhttp3/Request;", "route", "Lokhttp3/Route;", "response", "Lokhttp3/Response;", "Companion", "okhttp"}
)
public interface Authenticator {
   @NotNull
   Companion Companion = Authenticator.Companion.$$INSTANCE;
   @JvmField
   @NotNull
   Authenticator NONE = new Companion.AuthenticatorNone();
   @JvmField
   @NotNull
   Authenticator JAVA_NET_AUTHENTICATOR = new JavaNetAuthenticator((Dns)null, 1, (DefaultConstructorMarker)null);

   @Nullable
   Request authenticate(@Nullable Route var1, @NotNull Response var2) throws IOException;

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001:\u0001\u0007B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0013\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\u0001R\u0013\u0010\u0006\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\u0001¨\u0006\b"},
      d2 = {"Lokhttp3/Authenticator$Companion;", "", "<init>", "()V", "NONE", "Lokhttp3/Authenticator;", "JAVA_NET_AUTHENTICATOR", "AuthenticatorNone", "okhttp"}
   )
   public static final class Companion {
      // $FF: synthetic field
      static final Companion $$INSTANCE = new Companion();

      private Companion() {
      }

      @Metadata(
         mv = {2, 2, 0},
         k = 1,
         xi = 48,
         d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\b\u001a\u00020\tH\u0016¨\u0006\n"},
         d2 = {"Lokhttp3/Authenticator$Companion$AuthenticatorNone;", "Lokhttp3/Authenticator;", "<init>", "()V", "authenticate", "Lokhttp3/Request;", "route", "Lokhttp3/Route;", "response", "Lokhttp3/Response;", "okhttp"}
      )
      private static final class AuthenticatorNone implements Authenticator {
         public AuthenticatorNone() {
         }

         @Nullable
         public Request authenticate(@Nullable Route route, @NotNull Response response) {
            Intrinsics.checkNotNullParameter(response, "response");
            return null;
         }
      }
   }
}
