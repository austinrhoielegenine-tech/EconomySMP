package okio.internal;

import kotlin.Metadata;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\f\n\u0000\u001a\r\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u0080\b\u001a\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0002¨\u0006\u0007"},
   d2 = {"commonDecodeHex", "Lokio/ByteString;", "", "decodeHexDigit", "", "c", "", "okio"}
)
@JvmName(
   name = "-ByteStringNonJs"
)
@SourceDebugExtension({"SMAP\nByteStringNonJs.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ByteStringNonJs.kt\nokio/internal/-ByteStringNonJs\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,44:1\n1#2:45\n*E\n"})
public final class -ByteStringNonJs {
   @NotNull
   public static final ByteString commonDecodeHex(@NotNull String $this$commonDecodeHex) {
      Intrinsics.checkNotNullParameter($this$commonDecodeHex, "<this>");
      int $i$f$commonDecodeHex = 0;
      if ($this$commonDecodeHex.length() % 2 != 0) {
         int var7 = 0;
         String var8 = "Unexpected hex string: " + $this$commonDecodeHex;
         throw new IllegalArgumentException(var8.toString());
      } else {
         byte[] result = new byte[$this$commonDecodeHex.length() / 2];
         int i = 0;

         for(int var4 = result.length; i < var4; ++i) {
            int d1 = access$decodeHexDigit($this$commonDecodeHex.charAt(i * 2)) << 4;
            int d2 = access$decodeHexDigit($this$commonDecodeHex.charAt(i * 2 + 1));
            result[i] = (byte)(d1 + d2);
         }

         return new ByteString(result);
      }
   }

   private static final int decodeHexDigit(char c) {
      int var10000;
      if ('0' <= c ? c < ':' : false) {
         var10000 = c - 48;
      } else if ('a' <= c ? c < 'g' : false) {
         var10000 = c - 97 + 10;
      } else {
         if (!('A' <= c ? c < 'G' : false)) {
            throw new IllegalArgumentException("Unexpected hex digit: " + c);
         }

         var10000 = c - 65 + 10;
      }

      return var10000;
   }

   // $FF: synthetic method
   public static final int access$decodeHexDigit(char c) {
      return decodeHexDigit(c);
   }
}
