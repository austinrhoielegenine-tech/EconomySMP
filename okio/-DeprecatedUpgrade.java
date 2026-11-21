package okio;

import kotlin.Metadata;
import kotlin.jvm.JvmName;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\"\u0011\u0010\u0000\u001a\u00020\u0001¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0003\"\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b"},
   d2 = {"Okio", "Lokio/-DeprecatedOkio;", "getOkio", "()Lokio/-DeprecatedOkio;", "Utf8", "Lokio/-DeprecatedUtf8;", "getUtf8", "()Lokio/-DeprecatedUtf8;", "okio"}
)
@JvmName(
   name = "-DeprecatedUpgrade"
)
public final class -DeprecatedUpgrade {
   @NotNull
   private static final -DeprecatedOkio Okio;
   @NotNull
   private static final -DeprecatedUtf8 Utf8;

   @NotNull
   public static final -DeprecatedOkio getOkio() {
      return Okio;
   }

   @NotNull
   public static final -DeprecatedUtf8 getUtf8() {
      return Utf8;
   }

   static {
      Okio = -DeprecatedOkio.INSTANCE;
      Utf8 = -DeprecatedUtf8.INSTANCE;
   }
}
