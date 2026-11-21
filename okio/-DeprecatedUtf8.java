package okio;

import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/** @deprecated */
@Deprecated(
   message = "changed in Okio 2.x"
)
@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\bÇ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007J \u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\tH\u0007¨\u0006\u000b"},
   d2 = {"Lokio/-DeprecatedUtf8;", "", "<init>", "()V", "size", "", "string", "", "beginIndex", "", "endIndex", "okio"}
)
public final class -DeprecatedUtf8 {
   @NotNull
   public static final -DeprecatedUtf8 INSTANCE = new -DeprecatedUtf8();

   private _DeprecatedUtf8/* $FF was: -DeprecatedUtf8*/() {
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to extension function",
      replaceWith = @ReplaceWith(
   expression = "string.utf8Size()",
   imports = {"okio.utf8Size"}
),
      level = DeprecationLevel.ERROR
   )
   public final long size(@NotNull String string) {
      Intrinsics.checkNotNullParameter(string, "string");
      return Utf8.size$default(string, 0, 0, 3, (Object)null);
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to extension function",
      replaceWith = @ReplaceWith(
   expression = "string.utf8Size(beginIndex, endIndex)",
   imports = {"okio.utf8Size"}
),
      level = DeprecationLevel.ERROR
   )
   public final long size(@NotNull String string, int beginIndex, int endIndex) {
      Intrinsics.checkNotNullParameter(string, "string");
      return Utf8.size(string, beginIndex, endIndex);
   }
}
