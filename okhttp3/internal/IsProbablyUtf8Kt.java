package okhttp3.internal;

import java.io.EOFException;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okio.BufferedSource;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\u001a\u0016\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\b\b\u0002\u0010\u0003\u001a\u00020\u0004H\u0000¨\u0006\u0005"},
   d2 = {"isProbablyUtf8", "", "Lokio/BufferedSource;", "codePointLimit", "", "okhttp"}
)
public final class IsProbablyUtf8Kt {
   public static final boolean isProbablyUtf8(@NotNull BufferedSource $this$isProbablyUtf8, long codePointLimit) {
      Intrinsics.checkNotNullParameter($this$isProbablyUtf8, "<this>");

      try {
         BufferedSource peek = $this$isProbablyUtf8.peek();

         for(long i = 0L; i < codePointLimit && !peek.exhausted(); ++i) {
            int codePoint = peek.readUtf8CodePoint();
            if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
               return false;
            }
         }

         return true;
      } catch (EOFException var7) {
         return false;
      }
   }

   // $FF: synthetic method
   public static boolean isProbablyUtf8$default(BufferedSource var0, long var1, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = Long.MAX_VALUE;
      }

      return isProbablyUtf8(var0, var1);
   }
}
