package okio;

import java.util.Arrays;
import kotlin.Metadata;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0003\u001a\u000e\u0010\u0006\u001a\u0004\u0018\u00010\u0001*\u00020\u0007H\u0000\u001a\u0016\u0010\b\u001a\u00020\u0007*\u00020\u00012\b\b\u0002\u0010\t\u001a\u00020\u0001H\u0000\"\u0014\u0010\u0000\u001a\u00020\u0001X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0003\"\u0014\u0010\u0004\u001a\u00020\u0001X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0003¨\u0006\n"},
   d2 = {"BASE64", "", "getBASE64", "()[B", "BASE64_URL_SAFE", "getBASE64_URL_SAFE", "decodeBase64ToArray", "", "encodeBase64", "map", "okio"}
)
@JvmName(
   name = "-Base64"
)
public final class -Base64 {
   @NotNull
   private static final byte[] BASE64;
   @NotNull
   private static final byte[] BASE64_URL_SAFE;

   @NotNull
   public static final byte[] getBASE64() {
      return BASE64;
   }

   @NotNull
   public static final byte[] getBASE64_URL_SAFE() {
      return BASE64_URL_SAFE;
   }

   @Nullable
   public static final byte[] decodeBase64ToArray(@NotNull String $this$decodeBase64ToArray) {
      Intrinsics.checkNotNullParameter($this$decodeBase64ToArray, "<this>");

      int limit;
      for(limit = $this$decodeBase64ToArray.length(); limit > 0; --limit) {
         char c = $this$decodeBase64ToArray.charAt(limit - 1);
         if (c != '=' && c != '\n' && c != '\r' && c != ' ' && c != '\t') {
            break;
         }
      }

      byte[] out = new byte[(int)((long)limit * 6L / 8L)];
      int outCount = 0;
      int inCount = 0;
      int word = 0;
      int pos = 0;

      for(int var7 = limit; pos < var7; ++pos) {
         char c = $this$decodeBase64ToArray.charAt(pos);
         int bits = 0;
         if ('A' <= c ? c < '[' : false) {
            bits = c - 65;
         } else if ('a' <= c ? c < '{' : false) {
            bits = c - 71;
         } else if ('0' <= c ? c < ':' : false) {
            bits = c + 4;
         } else if (c != '+' && c != '-') {
            if (c != '/' && c != '_') {
               if (c != '\n' && c != '\r' && c != ' ' && c != '\t') {
                  return null;
               }
               continue;
            }

            bits = 63;
         } else {
            bits = 62;
         }

         word = word << 6 | bits;
         ++inCount;
         if (inCount % 4 == 0) {
            out[outCount++] = (byte)(word >> 16);
            out[outCount++] = (byte)(word >> 8);
            out[outCount++] = (byte)word;
         }
      }

      pos = inCount % 4;
      switch (pos) {
         case 1:
            return null;
         case 2:
            word <<= 12;
            out[outCount++] = (byte)(word >> 16);
            break;
         case 3:
            word <<= 6;
            out[outCount++] = (byte)(word >> 16);
            out[outCount++] = (byte)(word >> 8);
      }

      if (outCount == out.length) {
         return out;
      } else {
         byte[] var10000 = Arrays.copyOf(out, outCount);
         Intrinsics.checkNotNullExpressionValue(var10000, "copyOf(...)");
         return var10000;
      }
   }

   @NotNull
   public static final String encodeBase64(@NotNull byte[] $this$encodeBase64, @NotNull byte[] map) {
      Intrinsics.checkNotNullParameter($this$encodeBase64, "<this>");
      Intrinsics.checkNotNullParameter(map, "map");
      int length = ($this$encodeBase64.length + 2) / 3 * 4;
      byte[] out = new byte[length];
      int index = 0;
      int end = $this$encodeBase64.length - $this$encodeBase64.length % 3;

      int i;
      int b2;
      for(i = 0; i < end; out[index++] = map[b2 & 63]) {
         int b0 = $this$encodeBase64[i++];
         int b1 = $this$encodeBase64[i++];
         b2 = $this$encodeBase64[i++];
         out[index++] = map[(b0 & 255) >> 2];
         out[index++] = map[(b0 & 3) << 4 | (b1 & 255) >> 4];
         out[index++] = map[(b1 & 15) << 2 | (b2 & 255) >> 6];
      }

      switch ($this$encodeBase64.length - end) {
         case 1:
            int b0 = $this$encodeBase64[i];
            out[index++] = map[(b0 & 255) >> 2];
            out[index++] = map[(b0 & 3) << 4];
            out[index++] = 61;
            out[index] = 61;
            break;
         case 2:
            int b0 = $this$encodeBase64[i++];
            b2 = $this$encodeBase64[i];
            out[index++] = map[(b0 & 255) >> 2];
            out[index++] = map[(b0 & 3) << 4 | (b2 & 255) >> 4];
            out[index++] = map[(b2 & 15) << 2];
            out[index] = 61;
      }

      return _JvmPlatformKt.toUtf8String(out);
   }

   // $FF: synthetic method
   public static String encodeBase64$default(byte[] var0, byte[] var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = BASE64;
      }

      return encodeBase64(var0, var1);
   }

   static {
      BASE64 = ByteString.Companion.encodeUtf8("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/").getData$okio();
      BASE64_URL_SAFE = ByteString.Companion.encodeUtf8("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_").getData$okio();
   }
}
