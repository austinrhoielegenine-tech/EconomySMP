package okhttp3;

import java.nio.charset.Charset;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\"\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\tH\u0007¨\u0006\n"},
   d2 = {"Lokhttp3/Credentials;", "", "<init>", "()V", "basic", "", "username", "password", "charset", "Ljava/nio/charset/Charset;", "okhttp"}
)
public final class Credentials {
   @NotNull
   public static final Credentials INSTANCE = new Credentials();

   private Credentials() {
   }

   @JvmStatic
   @JvmOverloads
   @NotNull
   public static final String basic(@NotNull String username, @NotNull String password, @NotNull Charset charset) {
      Intrinsics.checkNotNullParameter(username, "username");
      Intrinsics.checkNotNullParameter(password, "password");
      Intrinsics.checkNotNullParameter(charset, "charset");
      String usernameAndPassword = username + ':' + password;
      String encoded = ByteString.Companion.encodeString(usernameAndPassword, charset).base64();
      return "Basic " + encoded;
   }

   // $FF: synthetic method
   public static String basic$default(String var0, String var1, Charset var2, int var3, Object var4) {
      if ((var3 & 4) != 0) {
         var2 = Charsets.ISO_8859_1;
      }

      return basic(var0, var1, var2);
   }

   @JvmStatic
   @JvmOverloads
   @NotNull
   public static final String basic(@NotNull String username, @NotNull String password) {
      Intrinsics.checkNotNullParameter(username, "username");
      Intrinsics.checkNotNullParameter(password, "password");
      return basic$default(username, password, (Charset)null, 4, (Object)null);
   }
}
