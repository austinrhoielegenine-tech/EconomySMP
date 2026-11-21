package okio;

import kotlin.Metadata;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okio.internal.-ByteString;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000F\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\n\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0005\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a \u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0003H\u0000\u001a\f\u0010\u0006\u001a\u00020\u0007*\u00020\u0007H\u0000\u001a\f\u0010\u0006\u001a\u00020\b*\u00020\bH\u0000\u001a\f\u0010\u0006\u001a\u00020\u0003*\u00020\u0003H\u0000\u001a\u0015\u0010\t\u001a\u00020\b*\u00020\b2\u0006\u0010\n\u001a\u00020\bH\u0080\f\u001a\u0015\u0010\u000b\u001a\u00020\u0003*\u00020\u00032\u0006\u0010\n\u001a\u00020\bH\u0080\f\u001a\u0015\u0010\f\u001a\u00020\b*\u00020\r2\u0006\u0010\u000e\u001a\u00020\bH\u0080\f\u001a\u0015\u0010\u000f\u001a\u00020\b*\u00020\r2\u0006\u0010\u000e\u001a\u00020\bH\u0080\f\u001a\u0015\u0010\u0010\u001a\u00020\b*\u00020\r2\u0006\u0010\u000e\u001a\u00020\bH\u0080\f\u001a\u0015\u0010\u0010\u001a\u00020\u0003*\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0003H\u0080\f\u001a\u0015\u0010\u0011\u001a\u00020\r*\u00020\r2\u0006\u0010\u000e\u001a\u00020\rH\u0080\f\u001a\u0015\u0010\u0010\u001a\u00020\u0003*\u00020\b2\u0006\u0010\u000e\u001a\u00020\u0003H\u0080\f\u001a\u0019\u0010\u0012\u001a\u00020\u00032\u0006\u0010\u0013\u001a\u00020\u00032\u0006\u0010\u0014\u001a\u00020\bH\u0080\b\u001a\u0019\u0010\u0012\u001a\u00020\u00032\u0006\u0010\u0013\u001a\u00020\b2\u0006\u0010\u0014\u001a\u00020\u0003H\u0080\b\u001a0\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0013\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\b2\u0006\u0010\u0014\u001a\u00020\u00172\u0006\u0010\u0019\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\bH\u0000\u001a\f\u0010\u001a\u001a\u00020\u001b*\u00020\rH\u0000\u001a\f\u0010\u001a\u001a\u00020\u001b*\u00020\bH\u0000\u001a\f\u0010\u001a\u001a\u00020\u001b*\u00020\u0003H\u0000\u001a\u0010\u0010 \u001a\u00020\u001d2\u0006\u0010!\u001a\u00020\u001dH\u0000\u001a\u0014\u0010 \u001a\u00020\b*\u00020%2\u0006\u0010&\u001a\u00020\bH\u0000\u001a\u0014\u0010 \u001a\u00020\b*\u00020\u00172\u0006\u0010'\u001a\u00020\bH\u0000\"\u0014\u0010\u001c\u001a\u00020\u001dX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001f\"\u0014\u0010\"\u001a\u00020\bX\u0080D¢\u0006\b\n\u0000\u001a\u0004\b#\u0010$¨\u0006("},
   d2 = {"checkOffsetAndCount", "", "size", "", "offset", "byteCount", "reverseBytes", "", "", "leftRotate", "bitCount", "rightRotate", "shr", "", "other", "shl", "and", "xor", "minOf", "a", "b", "arrayRangeEquals", "", "", "aOffset", "bOffset", "toHexString", "", "DEFAULT__new_UnsafeCursor", "Lokio/Buffer$UnsafeCursor;", "getDEFAULT__new_UnsafeCursor", "()Lokio/Buffer$UnsafeCursor;", "resolveDefaultParameter", "unsafeCursor", "DEFAULT__ByteString_size", "getDEFAULT__ByteString_size", "()I", "Lokio/ByteString;", "position", "sizeParam", "okio"}
)
@JvmName(
   name = "-SegmentedByteString"
)
@SourceDebugExtension({"SMAP\nUtil.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,185:1\n67#1:186\n73#1:187\n*S KotlinDebug\n*F\n+ 1 Util.kt\nokio/-SegmentedByteString\n*L\n105#1:186\n106#1:187\n*E\n"})
public final class -SegmentedByteString {
   @NotNull
   private static final Buffer.UnsafeCursor DEFAULT__new_UnsafeCursor = new Buffer.UnsafeCursor();
   private static final int DEFAULT__ByteString_size = -1234567890;

   public static final void checkOffsetAndCount(long size, long offset, long byteCount) {
      if ((offset | byteCount) < 0L || offset > size || size - offset < byteCount) {
         throw new ArrayIndexOutOfBoundsException("size=" + size + " offset=" + offset + " byteCount=" + byteCount);
      }
   }

   public static final short reverseBytes(short $this$reverseBytes) {
      int i = $this$reverseBytes & '\uffff';
      int reversed = (i & '\uff00') >>> 8 | (i & 255) << 8;
      return (short)reversed;
   }

   public static final int reverseBytes(int $this$reverseBytes) {
      return ($this$reverseBytes & -16777216) >>> 24 | ($this$reverseBytes & 16711680) >>> 8 | ($this$reverseBytes & '\uff00') << 8 | ($this$reverseBytes & 255) << 24;
   }

   public static final long reverseBytes(long $this$reverseBytes) {
      return ($this$reverseBytes & -72057594037927936L) >>> 56 | ($this$reverseBytes & 71776119061217280L) >>> 40 | ($this$reverseBytes & 280375465082880L) >>> 24 | ($this$reverseBytes & 1095216660480L) >>> 8 | ($this$reverseBytes & 4278190080L) << 8 | ($this$reverseBytes & 16711680L) << 24 | ($this$reverseBytes & 65280L) << 40 | ($this$reverseBytes & 255L) << 56;
   }

   public static final int leftRotate(int $this$leftRotate, int bitCount) {
      int $i$f$leftRotate = 0;
      return $this$leftRotate << bitCount | $this$leftRotate >>> 32 - bitCount;
   }

   public static final long rightRotate(long $this$rightRotate, int bitCount) {
      int $i$f$rightRotate = 0;
      return $this$rightRotate >>> bitCount | $this$rightRotate << 64 - bitCount;
   }

   public static final int shr(byte $this$shr, int other) {
      int $i$f$shr = 0;
      return $this$shr >> other;
   }

   public static final int shl(byte $this$shl, int other) {
      int $i$f$shl = 0;
      return $this$shl << other;
   }

   public static final int and(byte $this$and, int other) {
      int $i$f$and = 0;
      return $this$and & other;
   }

   public static final long and(byte $this$and, long other) {
      int $i$f$and = 0;
      return (long)$this$and & other;
   }

   public static final byte xor(byte $this$xor, byte other) {
      int $i$f$xor = 0;
      return (byte)($this$xor ^ other);
   }

   public static final long and(int $this$and, long other) {
      int $i$f$and = 0;
      return (long)$this$and & other;
   }

   public static final long minOf(long a, int b) {
      int $i$f$minOf = 0;
      return Math.min(a, (long)b);
   }

   public static final long minOf(int a, long b) {
      int $i$f$minOf = 0;
      return Math.min((long)a, b);
   }

   public static final boolean arrayRangeEquals(@NotNull byte[] a, int aOffset, @NotNull byte[] b, int bOffset, int byteCount) {
      Intrinsics.checkNotNullParameter(a, "a");
      Intrinsics.checkNotNullParameter(b, "b");

      for(int i = 0; i < byteCount; ++i) {
         if (a[i + aOffset] != b[i + bOffset]) {
            return false;
         }
      }

      return true;
   }

   @NotNull
   public static final String toHexString(byte $this$toHexString) {
      char[] result = new char[2];
      char[] var10002 = -ByteString.getHEX_DIGIT_CHARS();
      int other$iv = 4;
      int $i$f$shr = 0;
      result[0] = var10002[$this$toHexString >> other$iv & 15];
      var10002 = -ByteString.getHEX_DIGIT_CHARS();
      other$iv = 15;
      $i$f$shr = 0;
      result[1] = var10002[$this$toHexString & other$iv];
      return StringsKt.concatToString(result);
   }

   @NotNull
   public static final String toHexString(int $this$toHexString) {
      if ($this$toHexString == 0) {
         return "0";
      } else {
         char[] result = new char[]{-ByteString.getHEX_DIGIT_CHARS()[$this$toHexString >> 28 & 15], -ByteString.getHEX_DIGIT_CHARS()[$this$toHexString >> 24 & 15], -ByteString.getHEX_DIGIT_CHARS()[$this$toHexString >> 20 & 15], -ByteString.getHEX_DIGIT_CHARS()[$this$toHexString >> 16 & 15], -ByteString.getHEX_DIGIT_CHARS()[$this$toHexString >> 12 & 15], -ByteString.getHEX_DIGIT_CHARS()[$this$toHexString >> 8 & 15], -ByteString.getHEX_DIGIT_CHARS()[$this$toHexString >> 4 & 15], -ByteString.getHEX_DIGIT_CHARS()[$this$toHexString & 15]};

         int i;
         for(i = 0; i < result.length && result[i] == '0'; ++i) {
         }

         return StringsKt.concatToString(result, i, result.length);
      }
   }

   @NotNull
   public static final String toHexString(long $this$toHexString) {
      if ($this$toHexString == 0L) {
         return "0";
      } else {
         char[] result = new char[]{-ByteString.getHEX_DIGIT_CHARS()[(int)($this$toHexString >> 60 & 15L)], -ByteString.getHEX_DIGIT_CHARS()[(int)($this$toHexString >> 56 & 15L)], -ByteString.getHEX_DIGIT_CHARS()[(int)($this$toHexString >> 52 & 15L)], -ByteString.getHEX_DIGIT_CHARS()[(int)($this$toHexString >> 48 & 15L)], -ByteString.getHEX_DIGIT_CHARS()[(int)($this$toHexString >> 44 & 15L)], -ByteString.getHEX_DIGIT_CHARS()[(int)($this$toHexString >> 40 & 15L)], -ByteString.getHEX_DIGIT_CHARS()[(int)($this$toHexString >> 36 & 15L)], -ByteString.getHEX_DIGIT_CHARS()[(int)($this$toHexString >> 32 & 15L)], -ByteString.getHEX_DIGIT_CHARS()[(int)($this$toHexString >> 28 & 15L)], -ByteString.getHEX_DIGIT_CHARS()[(int)($this$toHexString >> 24 & 15L)], -ByteString.getHEX_DIGIT_CHARS()[(int)($this$toHexString >> 20 & 15L)], -ByteString.getHEX_DIGIT_CHARS()[(int)($this$toHexString >> 16 & 15L)], -ByteString.getHEX_DIGIT_CHARS()[(int)($this$toHexString >> 12 & 15L)], -ByteString.getHEX_DIGIT_CHARS()[(int)($this$toHexString >> 8 & 15L)], -ByteString.getHEX_DIGIT_CHARS()[(int)($this$toHexString >> 4 & 15L)], -ByteString.getHEX_DIGIT_CHARS()[(int)($this$toHexString & 15L)]};

         int i;
         for(i = 0; i < result.length && result[i] == '0'; ++i) {
         }

         return StringsKt.concatToString(result, i, result.length);
      }
   }

   @NotNull
   public static final Buffer.UnsafeCursor getDEFAULT__new_UnsafeCursor() {
      return DEFAULT__new_UnsafeCursor;
   }

   @NotNull
   public static final Buffer.UnsafeCursor resolveDefaultParameter(@NotNull Buffer.UnsafeCursor unsafeCursor) {
      Intrinsics.checkNotNullParameter(unsafeCursor, "unsafeCursor");
      return unsafeCursor == DEFAULT__new_UnsafeCursor ? new Buffer.UnsafeCursor() : unsafeCursor;
   }

   public static final int getDEFAULT__ByteString_size() {
      return DEFAULT__ByteString_size;
   }

   public static final int resolveDefaultParameter(@NotNull ByteString $this$resolveDefaultParameter, int position) {
      Intrinsics.checkNotNullParameter($this$resolveDefaultParameter, "<this>");
      return position == DEFAULT__ByteString_size ? $this$resolveDefaultParameter.size() : position;
   }

   public static final int resolveDefaultParameter(@NotNull byte[] $this$resolveDefaultParameter, int sizeParam) {
      Intrinsics.checkNotNullParameter($this$resolveDefaultParameter, "<this>");
      return sizeParam == DEFAULT__ByteString_size ? $this$resolveDefaultParameter.length : sizeParam;
   }
}
