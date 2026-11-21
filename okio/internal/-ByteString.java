package okio.internal;

import java.util.Arrays;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okio.-Base64;
import okio.Buffer;
import okio.ByteString;
import okio._JvmPlatformKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000N\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0019\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0005\n\u0002\b\u0003\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\n\n\u0002\u0010\u0000\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a\r\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u0080\b\u001a\r\u0010\u0003\u001a\u00020\u0001*\u00020\u0002H\u0080\b\u001a\r\u0010\u0004\u001a\u00020\u0001*\u00020\u0002H\u0080\b\u001a\r\u0010\t\u001a\u00020\u0001*\u00020\u0002H\u0080\b\u001a\r\u0010\n\u001a\u00020\u0002*\u00020\u0002H\u0080\b\u001a\r\u0010\u000b\u001a\u00020\u0002*\u00020\u0002H\u0080\b\u001a\u001d\u0010\f\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000eH\u0080\b\u001a\u0015\u0010\u0010\u001a\u00020\u0011*\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u000eH\u0080\b\u001a\r\u0010\u0013\u001a\u00020\u000e*\u00020\u0002H\u0080\b\u001a\r\u0010\u0014\u001a\u00020\u0015*\u00020\u0002H\u0080\b\u001a\r\u0010\u0016\u001a\u00020\u0015*\u00020\u0002H\u0080\b\u001a-\u0010\u0017\u001a\u00020\u0018*\u00020\u00022\u0006\u0010\u0019\u001a\u00020\u000e2\u0006\u0010\u001a\u001a\u00020\u00022\u0006\u0010\u001b\u001a\u00020\u000e2\u0006\u0010\u001c\u001a\u00020\u000eH\u0080\b\u001a-\u0010\u0017\u001a\u00020\u0018*\u00020\u00022\u0006\u0010\u0019\u001a\u00020\u000e2\u0006\u0010\u001a\u001a\u00020\u00152\u0006\u0010\u001b\u001a\u00020\u000e2\u0006\u0010\u001c\u001a\u00020\u000eH\u0080\b\u001a-\u0010\u001d\u001a\u00020\u001e*\u00020\u00022\u0006\u0010\u0019\u001a\u00020\u000e2\u0006\u0010\u001f\u001a\u00020\u00152\u0006\u0010 \u001a\u00020\u000e2\u0006\u0010\u001c\u001a\u00020\u000eH\u0080\b\u001a\u0015\u0010!\u001a\u00020\u0018*\u00020\u00022\u0006\u0010\"\u001a\u00020\u0002H\u0080\b\u001a\u0015\u0010!\u001a\u00020\u0018*\u00020\u00022\u0006\u0010\"\u001a\u00020\u0015H\u0080\b\u001a\u0015\u0010#\u001a\u00020\u0018*\u00020\u00022\u0006\u0010$\u001a\u00020\u0002H\u0080\b\u001a\u0015\u0010#\u001a\u00020\u0018*\u00020\u00022\u0006\u0010$\u001a\u00020\u0015H\u0080\b\u001a\u001d\u0010%\u001a\u00020\u000e*\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u00152\u0006\u0010&\u001a\u00020\u000eH\u0080\b\u001a\u001d\u0010'\u001a\u00020\u000e*\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u00022\u0006\u0010&\u001a\u00020\u000eH\u0080\b\u001a\u001d\u0010'\u001a\u00020\u000e*\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u00152\u0006\u0010&\u001a\u00020\u000eH\u0080\b\u001a\u0017\u0010(\u001a\u00020\u0018*\u00020\u00022\b\u0010\u001a\u001a\u0004\u0018\u00010)H\u0080\b\u001a\r\u0010*\u001a\u00020\u000e*\u00020\u0002H\u0080\b\u001a\u0015\u0010+\u001a\u00020\u000e*\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u0002H\u0080\b\u001a\u0011\u0010,\u001a\u00020\u00022\u0006\u0010-\u001a\u00020\u0015H\u0080\b\u001a\u001d\u0010.\u001a\u00020\u0002*\u00020\u00152\u0006\u0010\u0019\u001a\u00020\u000e2\u0006\u0010\u001c\u001a\u00020\u000eH\u0080\b\u001a\r\u0010/\u001a\u00020\u0002*\u00020\u0001H\u0080\b\u001a\u000f\u00100\u001a\u0004\u0018\u00010\u0002*\u00020\u0001H\u0080\b\u001a$\u00101\u001a\u00020\u001e*\u00020\u00022\u0006\u00102\u001a\u0002032\u0006\u0010\u0019\u001a\u00020\u000e2\u0006\u0010\u001c\u001a\u00020\u000eH\u0000\u001a\r\u00104\u001a\u00020\u0001*\u00020\u0002H\u0080\b\u001a\u0018\u00105\u001a\u00020\u000e2\u0006\u00106\u001a\u00020\u00152\u0006\u00107\u001a\u00020\u000eH\u0002\"\u0014\u0010\u0005\u001a\u00020\u0006X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u00068"},
   d2 = {"commonUtf8", "", "Lokio/ByteString;", "commonBase64", "commonBase64Url", "HEX_DIGIT_CHARS", "", "getHEX_DIGIT_CHARS", "()[C", "commonHex", "commonToAsciiLowercase", "commonToAsciiUppercase", "commonSubstring", "beginIndex", "", "endIndex", "commonGetByte", "", "pos", "commonGetSize", "commonToByteArray", "", "commonInternalArray", "commonRangeEquals", "", "offset", "other", "otherOffset", "byteCount", "commonCopyInto", "", "target", "targetOffset", "commonStartsWith", "prefix", "commonEndsWith", "suffix", "commonIndexOf", "fromIndex", "commonLastIndexOf", "commonEquals", "", "commonHashCode", "commonCompareTo", "commonOf", "data", "commonToByteString", "commonEncodeUtf8", "commonDecodeBase64", "commonWrite", "buffer", "Lokio/Buffer;", "commonToString", "codePointIndexToCharIndex", "s", "codePointCount", "okio"}
)
@JvmName(
   name = "-ByteString"
)
@SourceDebugExtension({"SMAP\nByteString.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ByteString.kt\nokio/internal/-ByteString\n+ 2 Util.kt\nokio/-SegmentedByteString\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Utf8.kt\nokio/Utf8\n*L\n1#1,342:1\n129#1,2:348\n131#1,9:351\n67#2:343\n73#2:344\n73#2:346\n73#2:347\n67#2:375\n73#2:387\n1#3:345\n1#3:350\n212#4,7:360\n122#4:367\n219#4,5:368\n122#4:373\n226#4:374\n228#4:376\n397#4,2:377\n122#4:379\n400#4,6:380\n127#4:386\n406#4:388\n122#4:389\n407#4,13:390\n122#4:403\n422#4:404\n122#4:405\n425#4:406\n230#4,3:407\n440#4,3:410\n122#4:413\n443#4:414\n127#4:415\n446#4,10:416\n127#4:426\n456#4:427\n122#4:428\n457#4,4:429\n127#4:433\n461#4:434\n122#4:435\n462#4,14:436\n122#4:450\n477#4,2:451\n122#4:453\n481#4:454\n122#4:455\n484#4:456\n234#4,3:457\n500#4,3:460\n122#4:463\n503#4:464\n127#4:465\n506#4,2:466\n127#4:468\n510#4,10:469\n127#4:479\n520#4:480\n122#4:481\n521#4,4:482\n127#4:486\n525#4:487\n122#4:488\n526#4,4:489\n127#4:493\n530#4:494\n122#4:495\n531#4,15:496\n122#4:511\n547#4,2:512\n122#4:514\n550#4,2:515\n122#4:517\n554#4:518\n122#4:519\n557#4:520\n241#4:521\n122#4:522\n242#4,5:523\n*S KotlinDebug\n*F\n+ 1 ByteString.kt\nokio/internal/-ByteString\n*L\n308#1:348,2\n308#1:351,9\n65#1:343\n66#1:344\n256#1:346\n257#1:347\n327#1:375\n327#1:387\n308#1:350\n327#1:360,7\n332#1:367\n327#1:368,5\n332#1:373\n327#1:374\n327#1:376\n327#1:377,2\n332#1:379\n327#1:380,6\n327#1:386\n327#1:388\n332#1:389\n327#1:390,13\n332#1:403\n327#1:404\n332#1:405\n327#1:406\n327#1:407,3\n327#1:410,3\n332#1:413\n327#1:414\n327#1:415\n327#1:416,10\n327#1:426\n327#1:427\n332#1:428\n327#1:429,4\n327#1:433\n327#1:434\n332#1:435\n327#1:436,14\n332#1:450\n327#1:451,2\n332#1:453\n327#1:454\n332#1:455\n327#1:456\n327#1:457,3\n327#1:460,3\n332#1:463\n327#1:464\n327#1:465\n327#1:466,2\n327#1:468\n327#1:469,10\n327#1:479\n327#1:480\n332#1:481\n327#1:482,4\n327#1:486\n327#1:487\n332#1:488\n327#1:489,4\n327#1:493\n327#1:494\n332#1:495\n327#1:496,15\n332#1:511\n327#1:512,2\n332#1:514\n327#1:515,2\n332#1:517\n327#1:518\n332#1:519\n327#1:520\n327#1:521\n332#1:522\n327#1:523,5\n*E\n"})
public final class -ByteString {
   @NotNull
   private static final char[] HEX_DIGIT_CHARS;

   @NotNull
   public static final String commonUtf8(@NotNull ByteString $this$commonUtf8) {
      Intrinsics.checkNotNullParameter($this$commonUtf8, "<this>");
      int $i$f$commonUtf8 = 0;
      String result = $this$commonUtf8.getUtf8$okio();
      if (result == null) {
         result = _JvmPlatformKt.toUtf8String($this$commonUtf8.internalArray$okio());
         $this$commonUtf8.setUtf8$okio(result);
      }

      return result;
   }

   @NotNull
   public static final String commonBase64(@NotNull ByteString $this$commonBase64) {
      Intrinsics.checkNotNullParameter($this$commonBase64, "<this>");
      int $i$f$commonBase64 = 0;
      return -Base64.encodeBase64$default($this$commonBase64.getData$okio(), (byte[])null, 1, (Object)null);
   }

   @NotNull
   public static final String commonBase64Url(@NotNull ByteString $this$commonBase64Url) {
      Intrinsics.checkNotNullParameter($this$commonBase64Url, "<this>");
      int $i$f$commonBase64Url = 0;
      return -Base64.encodeBase64($this$commonBase64Url.getData$okio(), -Base64.getBASE64_URL_SAFE());
   }

   @NotNull
   public static final char[] getHEX_DIGIT_CHARS() {
      return HEX_DIGIT_CHARS;
   }

   @NotNull
   public static final String commonHex(@NotNull ByteString $this$commonHex) {
      Intrinsics.checkNotNullParameter($this$commonHex, "<this>");
      int $i$f$commonHex = 0;
      char[] result = new char[$this$commonHex.getData$okio().length * 2];
      int c = 0;
      byte[] var4 = $this$commonHex.getData$okio();
      int var5 = 0;

      for(int var6 = var4.length; var5 < var6; ++var5) {
         byte b = var4[var5];
         int var10001 = c++;
         char[] var10002 = getHEX_DIGIT_CHARS();
         int other$iv = 4;
         int $i$f$shr = 0;
         result[var10001] = var10002[b >> other$iv & 15];
         var10001 = c++;
         var10002 = getHEX_DIGIT_CHARS();
         other$iv = 15;
         $i$f$shr = 0;
         result[var10001] = var10002[b & other$iv];
      }

      return StringsKt.concatToString(result);
   }

   @NotNull
   public static final ByteString commonToAsciiLowercase(@NotNull ByteString $this$commonToAsciiLowercase) {
      Intrinsics.checkNotNullParameter($this$commonToAsciiLowercase, "<this>");
      int $i$f$commonToAsciiLowercase = 0;

      for(int i = 0; i < $this$commonToAsciiLowercase.getData$okio().length; ++i) {
         byte c = $this$commonToAsciiLowercase.getData$okio()[i];
         if (c >= 65 && c <= 90) {
            byte[] var10000 = $this$commonToAsciiLowercase.getData$okio();
            var10000 = Arrays.copyOf(var10000, var10000.length);
            Intrinsics.checkNotNullExpressionValue(var10000, "copyOf(...)");
            byte[] lowercase = var10000;
            lowercase[i++] = (byte)(c - -32);

            while(i < lowercase.length) {
               c = lowercase[i];
               if (c >= 65 && c <= 90) {
                  lowercase[i] = (byte)(c - -32);
                  ++i;
               } else {
                  ++i;
               }
            }

            return new ByteString(lowercase);
         }
      }

      return $this$commonToAsciiLowercase;
   }

   @NotNull
   public static final ByteString commonToAsciiUppercase(@NotNull ByteString $this$commonToAsciiUppercase) {
      Intrinsics.checkNotNullParameter($this$commonToAsciiUppercase, "<this>");
      int $i$f$commonToAsciiUppercase = 0;

      for(int i = 0; i < $this$commonToAsciiUppercase.getData$okio().length; ++i) {
         byte c = $this$commonToAsciiUppercase.getData$okio()[i];
         if (c >= 97 && c <= 122) {
            byte[] var10000 = $this$commonToAsciiUppercase.getData$okio();
            var10000 = Arrays.copyOf(var10000, var10000.length);
            Intrinsics.checkNotNullExpressionValue(var10000, "copyOf(...)");
            byte[] lowercase = var10000;
            lowercase[i++] = (byte)(c - 32);

            while(i < lowercase.length) {
               c = lowercase[i];
               if (c >= 97 && c <= 122) {
                  lowercase[i] = (byte)(c - 32);
                  ++i;
               } else {
                  ++i;
               }
            }

            return new ByteString(lowercase);
         }
      }

      return $this$commonToAsciiUppercase;
   }

   @NotNull
   public static final ByteString commonSubstring(@NotNull ByteString $this$commonSubstring, int beginIndex, int endIndex) {
      Intrinsics.checkNotNullParameter($this$commonSubstring, "<this>");
      int $i$f$commonSubstring = 0;
      int endIndex = okio.-SegmentedByteString.resolveDefaultParameter($this$commonSubstring, endIndex);
      if (beginIndex < 0) {
         int var9 = 0;
         String var10 = "beginIndex < 0";
         throw new IllegalArgumentException(var10.toString());
      } else if (endIndex > $this$commonSubstring.getData$okio().length) {
         int var6 = 0;
         String var8 = "endIndex > length(" + $this$commonSubstring.getData$okio().length + ')';
         throw new IllegalArgumentException(var8.toString());
      } else {
         int subLen = endIndex - beginIndex;
         if (subLen < 0) {
            int var7 = 0;
            String var11 = "endIndex < beginIndex";
            throw new IllegalArgumentException(var11.toString());
         } else {
            return beginIndex == 0 && endIndex == $this$commonSubstring.getData$okio().length ? $this$commonSubstring : new ByteString(ArraysKt.copyOfRange($this$commonSubstring.getData$okio(), beginIndex, endIndex));
         }
      }
   }

   public static final byte commonGetByte(@NotNull ByteString $this$commonGetByte, int pos) {
      Intrinsics.checkNotNullParameter($this$commonGetByte, "<this>");
      int $i$f$commonGetByte = 0;
      return $this$commonGetByte.getData$okio()[pos];
   }

   public static final int commonGetSize(@NotNull ByteString $this$commonGetSize) {
      Intrinsics.checkNotNullParameter($this$commonGetSize, "<this>");
      int $i$f$commonGetSize = 0;
      return $this$commonGetSize.getData$okio().length;
   }

   @NotNull
   public static final byte[] commonToByteArray(@NotNull ByteString $this$commonToByteArray) {
      Intrinsics.checkNotNullParameter($this$commonToByteArray, "<this>");
      int $i$f$commonToByteArray = 0;
      byte[] var10000 = $this$commonToByteArray.getData$okio();
      var10000 = Arrays.copyOf(var10000, var10000.length);
      Intrinsics.checkNotNullExpressionValue(var10000, "copyOf(...)");
      return var10000;
   }

   @NotNull
   public static final byte[] commonInternalArray(@NotNull ByteString $this$commonInternalArray) {
      Intrinsics.checkNotNullParameter($this$commonInternalArray, "<this>");
      int $i$f$commonInternalArray = 0;
      return $this$commonInternalArray.getData$okio();
   }

   public static final boolean commonRangeEquals(@NotNull ByteString $this$commonRangeEquals, int offset, @NotNull ByteString other, int otherOffset, int byteCount) {
      Intrinsics.checkNotNullParameter($this$commonRangeEquals, "<this>");
      Intrinsics.checkNotNullParameter(other, "other");
      int $i$f$commonRangeEquals = 0;
      return other.rangeEquals(otherOffset, $this$commonRangeEquals.getData$okio(), offset, byteCount);
   }

   public static final boolean commonRangeEquals(@NotNull ByteString $this$commonRangeEquals, int offset, @NotNull byte[] other, int otherOffset, int byteCount) {
      Intrinsics.checkNotNullParameter($this$commonRangeEquals, "<this>");
      Intrinsics.checkNotNullParameter(other, "other");
      int $i$f$commonRangeEquals = 0;
      return offset >= 0 && offset <= $this$commonRangeEquals.getData$okio().length - byteCount && otherOffset >= 0 && otherOffset <= other.length - byteCount && okio.-SegmentedByteString.arrayRangeEquals($this$commonRangeEquals.getData$okio(), offset, other, otherOffset, byteCount);
   }

   public static final void commonCopyInto(@NotNull ByteString $this$commonCopyInto, int offset, @NotNull byte[] target, int targetOffset, int byteCount) {
      Intrinsics.checkNotNullParameter($this$commonCopyInto, "<this>");
      Intrinsics.checkNotNullParameter(target, "target");
      int $i$f$commonCopyInto = 0;
      ArraysKt.copyInto($this$commonCopyInto.getData$okio(), target, targetOffset, offset, offset + byteCount);
   }

   public static final boolean commonStartsWith(@NotNull ByteString $this$commonStartsWith, @NotNull ByteString prefix) {
      Intrinsics.checkNotNullParameter($this$commonStartsWith, "<this>");
      Intrinsics.checkNotNullParameter(prefix, "prefix");
      int $i$f$commonStartsWith = 0;
      return $this$commonStartsWith.rangeEquals(0, (ByteString)prefix, 0, prefix.size());
   }

   public static final boolean commonStartsWith(@NotNull ByteString $this$commonStartsWith, @NotNull byte[] prefix) {
      Intrinsics.checkNotNullParameter($this$commonStartsWith, "<this>");
      Intrinsics.checkNotNullParameter(prefix, "prefix");
      int $i$f$commonStartsWith = 0;
      return $this$commonStartsWith.rangeEquals(0, (byte[])prefix, 0, prefix.length);
   }

   public static final boolean commonEndsWith(@NotNull ByteString $this$commonEndsWith, @NotNull ByteString suffix) {
      Intrinsics.checkNotNullParameter($this$commonEndsWith, "<this>");
      Intrinsics.checkNotNullParameter(suffix, "suffix");
      int $i$f$commonEndsWith = 0;
      return $this$commonEndsWith.rangeEquals($this$commonEndsWith.size() - suffix.size(), (ByteString)suffix, 0, suffix.size());
   }

   public static final boolean commonEndsWith(@NotNull ByteString $this$commonEndsWith, @NotNull byte[] suffix) {
      Intrinsics.checkNotNullParameter($this$commonEndsWith, "<this>");
      Intrinsics.checkNotNullParameter(suffix, "suffix");
      int $i$f$commonEndsWith = 0;
      return $this$commonEndsWith.rangeEquals($this$commonEndsWith.size() - suffix.length, (byte[])suffix, 0, suffix.length);
   }

   public static final int commonIndexOf(@NotNull ByteString $this$commonIndexOf, @NotNull byte[] other, int fromIndex) {
      Intrinsics.checkNotNullParameter($this$commonIndexOf, "<this>");
      Intrinsics.checkNotNullParameter(other, "other");
      int $i$f$commonIndexOf = 0;
      int limit = $this$commonIndexOf.getData$okio().length - other.length;
      int i = Math.max(fromIndex, 0);
      if (i <= limit) {
         while(true) {
            if (okio.-SegmentedByteString.arrayRangeEquals($this$commonIndexOf.getData$okio(), i, other, 0, other.length)) {
               return i;
            }

            if (i == limit) {
               break;
            }

            ++i;
         }
      }

      return -1;
   }

   public static final int commonLastIndexOf(@NotNull ByteString $this$commonLastIndexOf, @NotNull ByteString other, int fromIndex) {
      Intrinsics.checkNotNullParameter($this$commonLastIndexOf, "<this>");
      Intrinsics.checkNotNullParameter(other, "other");
      int $i$f$commonLastIndexOf = 0;
      return $this$commonLastIndexOf.lastIndexOf(other.internalArray$okio(), fromIndex);
   }

   public static final int commonLastIndexOf(@NotNull ByteString $this$commonLastIndexOf, @NotNull byte[] other, int fromIndex) {
      Intrinsics.checkNotNullParameter($this$commonLastIndexOf, "<this>");
      Intrinsics.checkNotNullParameter(other, "other");
      int $i$f$commonLastIndexOf = 0;
      int fromIndex = okio.-SegmentedByteString.resolveDefaultParameter($this$commonLastIndexOf, fromIndex);
      int limit = $this$commonLastIndexOf.getData$okio().length - other.length;

      for(int i = Math.min(fromIndex, limit); -1 < i; --i) {
         if (okio.-SegmentedByteString.arrayRangeEquals($this$commonLastIndexOf.getData$okio(), i, other, 0, other.length)) {
            return i;
         }
      }

      return -1;
   }

   public static final boolean commonEquals(@NotNull ByteString $this$commonEquals, @Nullable Object other) {
      Intrinsics.checkNotNullParameter($this$commonEquals, "<this>");
      int $i$f$commonEquals = 0;
      return other == $this$commonEquals ? true : (other instanceof ByteString ? ((ByteString)other).size() == $this$commonEquals.getData$okio().length && ((ByteString)other).rangeEquals(0, (byte[])$this$commonEquals.getData$okio(), 0, $this$commonEquals.getData$okio().length) : false);
   }

   public static final int commonHashCode(@NotNull ByteString $this$commonHashCode) {
      Intrinsics.checkNotNullParameter($this$commonHashCode, "<this>");
      int $i$f$commonHashCode = 0;
      int result = $this$commonHashCode.getHashCode$okio();
      if (result != 0) {
         return result;
      } else {
         int it = Arrays.hashCode($this$commonHashCode.getData$okio());
         int var5 = 0;
         $this$commonHashCode.setHashCode$okio(it);
         return it;
      }
   }

   public static final int commonCompareTo(@NotNull ByteString $this$commonCompareTo, @NotNull ByteString other) {
      Intrinsics.checkNotNullParameter($this$commonCompareTo, "<this>");
      Intrinsics.checkNotNullParameter(other, "other");
      int $i$f$commonCompareTo = 0;
      int sizeA = $this$commonCompareTo.size();
      int sizeB = other.size();
      int i = 0;

      for(int size = Math.min(sizeA, sizeB); i < size; ++i) {
         int byteB = $this$commonCompareTo.getByte(i);
         int other$iv = 255;
         int $i$f$and = 0;
         int byteA = byteB & other$iv;
         other$iv = other.getByte(i);
         $i$f$and = 255;
         int $i$f$and = 0;
         byteB = other$iv & $i$f$and;
         if (byteA != byteB) {
            return byteA < byteB ? -1 : 1;
         }
      }

      if (sizeA == sizeB) {
         return 0;
      } else {
         return sizeA < sizeB ? -1 : 1;
      }
   }

   @NotNull
   public static final ByteString commonOf(@NotNull byte[] data) {
      Intrinsics.checkNotNullParameter(data, "data");
      int $i$f$commonOf = 0;
      byte[] var10002 = Arrays.copyOf(data, data.length);
      Intrinsics.checkNotNullExpressionValue(var10002, "copyOf(...)");
      return new ByteString(var10002);
   }

   @NotNull
   public static final ByteString commonToByteString(@NotNull byte[] $this$commonToByteString, int offset, int byteCount) {
      Intrinsics.checkNotNullParameter($this$commonToByteString, "<this>");
      int $i$f$commonToByteString = 0;
      int byteCount = okio.-SegmentedByteString.resolveDefaultParameter($this$commonToByteString, byteCount);
      okio.-SegmentedByteString.checkOffsetAndCount((long)$this$commonToByteString.length, (long)offset, (long)byteCount);
      return new ByteString(ArraysKt.copyOfRange($this$commonToByteString, offset, offset + byteCount));
   }

   @NotNull
   public static final ByteString commonEncodeUtf8(@NotNull String $this$commonEncodeUtf8) {
      Intrinsics.checkNotNullParameter($this$commonEncodeUtf8, "<this>");
      int $i$f$commonEncodeUtf8 = 0;
      ByteString byteString = new ByteString(_JvmPlatformKt.asUtf8ToByteArray($this$commonEncodeUtf8));
      byteString.setUtf8$okio($this$commonEncodeUtf8);
      return byteString;
   }

   @Nullable
   public static final ByteString commonDecodeBase64(@NotNull String $this$commonDecodeBase64) {
      Intrinsics.checkNotNullParameter($this$commonDecodeBase64, "<this>");
      int $i$f$commonDecodeBase64 = 0;
      byte[] decoded = -Base64.decodeBase64ToArray($this$commonDecodeBase64);
      return decoded != null ? new ByteString(decoded) : null;
   }

   public static final void commonWrite(@NotNull ByteString $this$commonWrite, @NotNull Buffer buffer, int offset, int byteCount) {
      Intrinsics.checkNotNullParameter($this$commonWrite, "<this>");
      Intrinsics.checkNotNullParameter(buffer, "buffer");
      buffer.write($this$commonWrite.getData$okio(), offset, byteCount);
   }

   @NotNull
   public static final String commonToString(@NotNull ByteString $this$commonToString) {
      Intrinsics.checkNotNullParameter($this$commonToString, "<this>");
      int $i$f$commonToString = 0;
      if ($this$commonToString.getData$okio().length == 0) {
         return "[size=0]";
      } else {
         int i = access$codePointIndexToCharIndex($this$commonToString.getData$okio(), 64);
         if (i == -1) {
            String var14;
            if ($this$commonToString.getData$okio().length <= 64) {
               var14 = "[hex=" + $this$commonToString.hex() + ']';
            } else {
               StringBuilder var15 = (new StringBuilder()).append("[size=").append($this$commonToString.getData$okio().length).append(" hex=");
               byte safeText = 0;
               int endIndex$iv = 64;
               int $i$f$commonSubstring = 0;
               int endIndex$iv = okio.-SegmentedByteString.resolveDefaultParameter($this$commonToString, endIndex$iv);
               if (endIndex$iv > $this$commonToString.getData$okio().length) {
                  int var8 = 0;
                  String var12 = "endIndex > length(" + $this$commonToString.getData$okio().length + ')';
                  throw new IllegalArgumentException(var12.toString());
               }

               int subLen$iv = endIndex$iv - safeText;
               if (subLen$iv < 0) {
                  int var10 = 0;
                  String var13 = "endIndex < beginIndex";
                  throw new IllegalArgumentException(var13.toString());
               }

               var14 = var15.append((endIndex$iv == $this$commonToString.getData$okio().length ? $this$commonToString : new ByteString(ArraysKt.copyOfRange($this$commonToString.getData$okio(), safeText, endIndex$iv))).hex()).append("…]").toString();
            }

            return var14;
         } else {
            String text = $this$commonToString.utf8();
            String var10000 = text.substring(0, i);
            Intrinsics.checkNotNullExpressionValue(var10000, "substring(...)");
            String safeText = StringsKt.replace$default(StringsKt.replace$default(StringsKt.replace$default(var10000, "\\", "\\\\", false, 4, (Object)null), "\n", "\\n", false, 4, (Object)null), "\r", "\\r", false, 4, (Object)null);
            return i < text.length() ? "[size=" + $this$commonToString.getData$okio().length + " text=" + safeText + "…]" : "[text=" + safeText + ']';
         }
      }
   }

   private static final int codePointIndexToCharIndex(byte[] s, int codePointCount) {
      int charCount = 0;
      int j = 0;
      byte[] $this$processUtf8CodePoints$iv = s;
      byte beginIndex$iv = 0;
      int endIndex$iv = s.length;
      int $i$f$processUtf8CodePoints = 0;
      int index$iv = beginIndex$iv;

      while(index$iv < endIndex$iv) {
         byte b0$iv = $this$processUtf8CodePoints$iv[index$iv];
         if (b0$iv >= 0) {
            int var11 = 0;
            int codePoint$iv = j++;
            if (codePoint$iv == codePointCount) {
               return charCount;
            }

            if (b0$iv != 10 && b0$iv != 13) {
               int $i$f$isIsoControl = 0;
               if ((0 <= b0$iv ? b0$iv < 32 : false) || (127 <= b0$iv ? b0$iv < 160 : false)) {
                  return -1;
               }
            }

            if (b0$iv == '�') {
               return -1;
            }

            charCount += b0$iv < 65536 ? 1 : 2;
            ++index$iv;

            while(index$iv < endIndex$iv && $this$processUtf8CodePoints$iv[index$iv] >= 0) {
               int c = $this$processUtf8CodePoints$iv[index$iv++];
               var11 = 0;
               codePoint$iv = j++;
               if (codePoint$iv == codePointCount) {
                  return charCount;
               }

               if (c != 10 && c != 13) {
                  int $i$f$isIsoControl = 0;
                  if ((0 <= c ? c < 32 : false) || (127 <= c ? c < 160 : false)) {
                     return -1;
                  }
               }

               if (c == 65533) {
                  return -1;
               }

               charCount += c < 65536 ? 1 : 2;
            }

            Unit var10000 = Unit.INSTANCE;
         } else {
            int other$iv$iv = 5;
            int $i$f$shr = 0;
            if (b0$iv >> other$iv$iv == -2) {
               int $i$f$process2Utf8Bytes = 0;
               int var179;
               byte var187;
               if (endIndex$iv <= index$iv + 1) {
                  char it$iv = '�';
                  int var20 = 0;
                  int var33 = 0;
                  int codePoint$iv = j++;
                  if (codePoint$iv == codePointCount) {
                     return charCount;
                  }

                  if (it$iv != '\n' && it$iv != '\r') {
                     int $i$f$isIsoControl = 0;
                     if ((0 <= it$iv ? it$iv < ' ' : false) || (127 <= it$iv ? it$iv < 160 : false)) {
                        return -1;
                     }
                  }

                  if (it$iv == '�') {
                     return -1;
                  }

                  charCount += it$iv < 65536 ? 1 : 2;
                  Unit var21 = Unit.INSTANCE;
                  var179 = index$iv;
                  var187 = 1;
               } else {
                  byte b0$iv$iv = $this$processUtf8CodePoints$iv[index$iv];
                  byte b1$iv$iv = $this$processUtf8CodePoints$iv[index$iv + 1];
                  int $i$f$isUtf8Continuation = 0;
                  int other$iv$iv$iv$iv = 192;
                  int $i$f$and = 0;
                  if ((b1$iv$iv & other$iv$iv$iv$iv) != 128) {
                     char it$iv = '�';
                     int var112 = 0;
                     int var34 = 0;
                     int codePoint$iv = j++;
                     if (codePoint$iv == codePointCount) {
                        return charCount;
                     }

                     if (it$iv != '\n' && it$iv != '\r') {
                        int $i$f$isIsoControl = 0;
                        if ((0 <= it$iv ? it$iv < ' ' : false) || (127 <= it$iv ? it$iv < 160 : false)) {
                           return -1;
                        }
                     }

                     if (it$iv == '�') {
                        return -1;
                     }

                     charCount += it$iv < 65536 ? 1 : 2;
                     Unit var129 = Unit.INSTANCE;
                     var179 = index$iv;
                     var187 = 1;
                  } else {
                     int codePoint$iv$iv = 3968 ^ b1$iv$iv ^ b0$iv$iv << 6;
                     if (codePoint$iv$iv < 128) {
                        char it$iv = '�';
                        int var113 = 0;
                        int var35 = 0;
                        int codePoint$iv = j++;
                        if (codePoint$iv == codePointCount) {
                           return charCount;
                        }

                        if (it$iv != '\n' && it$iv != '\r') {
                           int $i$f$isIsoControl = 0;
                           if ((0 <= it$iv ? it$iv < ' ' : false) || (127 <= it$iv ? it$iv < 160 : false)) {
                              return -1;
                           }
                        }

                        if (it$iv == '�') {
                           return -1;
                        }

                        charCount += it$iv < 65536 ? 1 : 2;
                        Unit var130 = Unit.INSTANCE;
                        var179 = index$iv;
                     } else {
                        int var114 = 0;
                        int var36 = 0;
                        int codePoint$iv = j++;
                        if (codePoint$iv == codePointCount) {
                           return charCount;
                        }

                        if (codePoint$iv$iv != 10 && codePoint$iv$iv != 13) {
                           int $i$f$isIsoControl = 0;
                           if ((0 <= codePoint$iv$iv ? codePoint$iv$iv < 32 : false) || (127 <= codePoint$iv$iv ? codePoint$iv$iv < 160 : false)) {
                              return -1;
                           }
                        }

                        if (codePoint$iv$iv == 65533) {
                           return -1;
                        }

                        charCount += codePoint$iv$iv < 65536 ? 1 : 2;
                        Unit var131 = Unit.INSTANCE;
                        var179 = index$iv;
                     }

                     var187 = 2;
                  }
               }

               index$iv = var179 + var187;
               Unit var180 = Unit.INSTANCE;
            } else {
               other$iv$iv = 4;
               $i$f$shr = 0;
               if (b0$iv >> other$iv$iv == -2) {
                  int $i$f$process3Utf8Bytes = 0;
                  int var181;
                  byte var188;
                  if (endIndex$iv <= index$iv + 2) {
                     label1202: {
                        char it$iv = '�';
                        int var120 = 0;
                        int var42 = 0;
                        int codePoint$iv = j++;
                        if (codePoint$iv == codePointCount) {
                           return charCount;
                        }

                        if (it$iv != '\n' && it$iv != '\r') {
                           int $i$f$isIsoControl = 0;
                           if ((0 <= it$iv ? it$iv < ' ' : false) || (127 <= it$iv ? it$iv < 160 : false)) {
                              return -1;
                           }
                        }

                        if (it$iv == '�') {
                           return -1;
                        }

                        charCount += it$iv < 65536 ? 1 : 2;
                        Unit var137 = Unit.INSTANCE;
                        var181 = index$iv;
                        if (endIndex$iv > index$iv + 1) {
                           byte byte$iv$iv$iv = $this$processUtf8CodePoints$iv[index$iv + 1];
                           int $i$f$isUtf8Continuation = 0;
                           int other$iv$iv$iv$iv = 192;
                           int $i$f$and = 0;
                           if ((byte$iv$iv$iv & other$iv$iv$iv$iv) == 128) {
                              var188 = 2;
                              break label1202;
                           }
                        }

                        var188 = 1;
                     }
                  } else {
                     byte b0$iv$iv = $this$processUtf8CodePoints$iv[index$iv];
                     byte b1$iv$iv = $this$processUtf8CodePoints$iv[index$iv + 1];
                     int $i$f$isUtf8Continuation = 0;
                     int other$iv$iv$iv$iv = 192;
                     int $i$f$and = 0;
                     if ((b1$iv$iv & other$iv$iv$iv$iv) != 128) {
                        char it$iv = '�';
                        int var115 = 0;
                        int var37 = 0;
                        int codePoint$iv = j++;
                        if (codePoint$iv == codePointCount) {
                           return charCount;
                        }

                        if (it$iv != '\n' && it$iv != '\r') {
                           int $i$f$isIsoControl = 0;
                           if ((0 <= it$iv ? it$iv < ' ' : false) || (127 <= it$iv ? it$iv < 160 : false)) {
                              return -1;
                           }
                        }

                        if (it$iv == '�') {
                           return -1;
                        }

                        charCount += it$iv < 65536 ? 1 : 2;
                        Unit var132 = Unit.INSTANCE;
                        var181 = index$iv;
                        var188 = 1;
                     } else {
                        byte b2$iv$iv = $this$processUtf8CodePoints$iv[index$iv + 2];
                        int $i$f$isUtf8Continuation = 0;
                        $i$f$and = 192;
                        int $i$f$and = 0;
                        if ((b2$iv$iv & $i$f$and) != 128) {
                           char it$iv = '�';
                           int var116 = 0;
                           int var38 = 0;
                           int codePoint$iv = j++;
                           if (codePoint$iv == codePointCount) {
                              return charCount;
                           }

                           if (it$iv != '\n' && it$iv != '\r') {
                              int $i$f$isIsoControl = 0;
                              if ((0 <= it$iv ? it$iv < ' ' : false) || (127 <= it$iv ? it$iv < 160 : false)) {
                                 return -1;
                              }
                           }

                           if (it$iv == '�') {
                              return -1;
                           }

                           charCount += it$iv < 65536 ? 1 : 2;
                           Unit var133 = Unit.INSTANCE;
                           var181 = index$iv;
                           var188 = 2;
                        } else {
                           $i$f$isUtf8Continuation = -123008 ^ b2$iv$iv ^ b1$iv$iv << 6 ^ b0$iv$iv << 12;
                           if ($i$f$isUtf8Continuation < 2048) {
                              char it$iv = '�';
                              int var117 = 0;
                              int var39 = 0;
                              int codePoint$iv = j++;
                              if (codePoint$iv == codePointCount) {
                                 return charCount;
                              }

                              if (it$iv != '\n' && it$iv != '\r') {
                                 int $i$f$isIsoControl = 0;
                                 if ((0 <= it$iv ? it$iv < ' ' : false) || (127 <= it$iv ? it$iv < 160 : false)) {
                                    return -1;
                                 }
                              }

                              if (it$iv == '�') {
                                 return -1;
                              }

                              charCount += it$iv < 65536 ? 1 : 2;
                              Unit var134 = Unit.INSTANCE;
                              var181 = index$iv;
                           } else if (55296 <= $i$f$isUtf8Continuation ? $i$f$isUtf8Continuation < 57344 : false) {
                              char it$iv = '�';
                              int var118 = 0;
                              int var40 = 0;
                              int codePoint$iv = j++;
                              if (codePoint$iv == codePointCount) {
                                 return charCount;
                              }

                              if (it$iv != '\n' && it$iv != '\r') {
                                 int $i$f$isIsoControl = 0;
                                 if ((0 <= it$iv ? it$iv < ' ' : false) || (127 <= it$iv ? it$iv < 160 : false)) {
                                    return -1;
                                 }
                              }

                              if (it$iv == '�') {
                                 return -1;
                              }

                              charCount += it$iv < 65536 ? 1 : 2;
                              Unit var135 = Unit.INSTANCE;
                              var181 = index$iv;
                           } else {
                              int var119 = 0;
                              int var41 = 0;
                              int codePoint$iv = j++;
                              if (codePoint$iv == codePointCount) {
                                 return charCount;
                              }

                              if ($i$f$isUtf8Continuation != 10 && $i$f$isUtf8Continuation != 13) {
                                 int $i$f$isIsoControl = 0;
                                 if ((0 <= $i$f$isUtf8Continuation ? $i$f$isUtf8Continuation < 32 : false) || (127 <= $i$f$isUtf8Continuation ? $i$f$isUtf8Continuation < 160 : false)) {
                                    return -1;
                                 }
                              }

                              if ($i$f$isUtf8Continuation == 65533) {
                                 return -1;
                              }

                              charCount += $i$f$isUtf8Continuation < 65536 ? 1 : 2;
                              Unit var136 = Unit.INSTANCE;
                              var181 = index$iv;
                           }

                           var188 = 3;
                        }
                     }
                  }

                  index$iv = var181 + var188;
                  Unit var182 = Unit.INSTANCE;
               } else {
                  other$iv$iv = 3;
                  $i$f$shr = 0;
                  if (b0$iv >> other$iv$iv == -2) {
                     int $i$f$process4Utf8Bytes = 0;
                     int var183;
                     byte var189;
                     if (endIndex$iv <= index$iv + 3) {
                        label1215: {
                           char it$iv = '�';
                           int var128 = 0;
                           int var50 = 0;
                           int codePoint$iv = j++;
                           if (codePoint$iv == codePointCount) {
                              return charCount;
                           }

                           if (it$iv != '\n' && it$iv != '\r') {
                              int $i$f$isIsoControl = 0;
                              if ((0 <= it$iv ? it$iv < ' ' : false) || (127 <= it$iv ? it$iv < 160 : false)) {
                                 return -1;
                              }
                           }

                           if (it$iv == '�') {
                              return -1;
                           }

                           charCount += it$iv < 65536 ? 1 : 2;
                           Unit var145 = Unit.INSTANCE;
                           var183 = index$iv;
                           if (endIndex$iv > index$iv + 1) {
                              byte byte$iv$iv$iv = $this$processUtf8CodePoints$iv[index$iv + 1];
                              int $i$f$isUtf8Continuation = 0;
                              int other$iv$iv$iv$iv = 192;
                              int $i$f$and = 0;
                              if ((byte$iv$iv$iv & other$iv$iv$iv$iv) == 128) {
                                 if (endIndex$iv > index$iv + 2) {
                                    byte$iv$iv$iv = $this$processUtf8CodePoints$iv[index$iv + 2];
                                    $i$f$isUtf8Continuation = 0;
                                    other$iv$iv$iv$iv = 192;
                                    $i$f$and = 0;
                                    if ((byte$iv$iv$iv & other$iv$iv$iv$iv) == 128) {
                                       var189 = 3;
                                       break label1215;
                                    }
                                 }

                                 var189 = 2;
                                 break label1215;
                              }
                           }

                           var189 = 1;
                        }
                     } else {
                        byte b0$iv$iv = $this$processUtf8CodePoints$iv[index$iv];
                        byte b1$iv$iv = $this$processUtf8CodePoints$iv[index$iv + 1];
                        int $i$f$isUtf8Continuation = 0;
                        int other$iv$iv$iv$iv = 192;
                        int $i$f$and = 0;
                        if ((b1$iv$iv & other$iv$iv$iv$iv) != 128) {
                           char it$iv = '�';
                           int var121 = 0;
                           int var43 = 0;
                           int codePoint$iv = j++;
                           if (codePoint$iv == codePointCount) {
                              return charCount;
                           }

                           if (it$iv != '\n' && it$iv != '\r') {
                              int $i$f$isIsoControl = 0;
                              if ((0 <= it$iv ? it$iv < ' ' : false) || (127 <= it$iv ? it$iv < 160 : false)) {
                                 return -1;
                              }
                           }

                           if (it$iv == '�') {
                              return -1;
                           }

                           charCount += it$iv < 65536 ? 1 : 2;
                           Unit var138 = Unit.INSTANCE;
                           var183 = index$iv;
                           var189 = 1;
                        } else {
                           byte b2$iv$iv = $this$processUtf8CodePoints$iv[index$iv + 2];
                           int $i$f$isUtf8Continuation = 0;
                           $i$f$and = 192;
                           int $i$f$and = 0;
                           if ((b2$iv$iv & $i$f$and) != 128) {
                              char it$iv = '�';
                              int var122 = 0;
                              int var44 = 0;
                              int codePoint$iv = j++;
                              if (codePoint$iv == codePointCount) {
                                 return charCount;
                              }

                              if (it$iv != '\n' && it$iv != '\r') {
                                 int $i$f$isIsoControl = 0;
                                 if ((0 <= it$iv ? it$iv < ' ' : false) || (127 <= it$iv ? it$iv < 160 : false)) {
                                    return -1;
                                 }
                              }

                              if (it$iv == '�') {
                                 return -1;
                              }

                              charCount += it$iv < 65536 ? 1 : 2;
                              Unit var139 = Unit.INSTANCE;
                              var183 = index$iv;
                              var189 = 2;
                           } else {
                              $i$f$isUtf8Continuation = $this$processUtf8CodePoints$iv[index$iv + 3];
                              other$iv$iv$iv$iv = 0;
                              $i$f$and = 192;
                              int $i$f$and = 0;
                              if (($i$f$isUtf8Continuation & $i$f$and) != 128) {
                                 char it$iv = '�';
                                 int var123 = 0;
                                 int var45 = 0;
                                 int codePoint$iv = j++;
                                 if (codePoint$iv == codePointCount) {
                                    return charCount;
                                 }

                                 if (it$iv != '\n' && it$iv != '\r') {
                                    int $i$f$isIsoControl = 0;
                                    if ((0 <= it$iv ? it$iv < ' ' : false) || (127 <= it$iv ? it$iv < 160 : false)) {
                                       return -1;
                                    }
                                 }

                                 if (it$iv == '�') {
                                    return -1;
                                 }

                                 charCount += it$iv < 65536 ? 1 : 2;
                                 Unit var140 = Unit.INSTANCE;
                                 var183 = index$iv;
                                 var189 = 3;
                              } else {
                                 $i$f$isUtf8Continuation = 3678080 ^ $i$f$isUtf8Continuation ^ b2$iv$iv << 6 ^ b1$iv$iv << 12 ^ b0$iv$iv << 18;
                                 if ($i$f$isUtf8Continuation > 1114111) {
                                    char it$iv = '�';
                                    int var124 = 0;
                                    int var46 = 0;
                                    int codePoint$iv = j++;
                                    if (codePoint$iv == codePointCount) {
                                       return charCount;
                                    }

                                    if (it$iv != '\n' && it$iv != '\r') {
                                       int $i$f$isIsoControl = 0;
                                       if ((0 <= it$iv ? it$iv < ' ' : false) || (127 <= it$iv ? it$iv < 160 : false)) {
                                          return -1;
                                       }
                                    }

                                    if (it$iv == '�') {
                                       return -1;
                                    }

                                    charCount += it$iv < 65536 ? 1 : 2;
                                    Unit var141 = Unit.INSTANCE;
                                    var183 = index$iv;
                                 } else if (55296 <= $i$f$isUtf8Continuation ? $i$f$isUtf8Continuation < 57344 : false) {
                                    char it$iv = '�';
                                    int var125 = 0;
                                    int var47 = 0;
                                    int codePoint$iv = j++;
                                    if (codePoint$iv == codePointCount) {
                                       return charCount;
                                    }

                                    if (it$iv != '\n' && it$iv != '\r') {
                                       int $i$f$isIsoControl = 0;
                                       if ((0 <= it$iv ? it$iv < ' ' : false) || (127 <= it$iv ? it$iv < 160 : false)) {
                                          return -1;
                                       }
                                    }

                                    if (it$iv == '�') {
                                       return -1;
                                    }

                                    charCount += it$iv < 65536 ? 1 : 2;
                                    Unit var142 = Unit.INSTANCE;
                                    var183 = index$iv;
                                 } else if ($i$f$isUtf8Continuation < 65536) {
                                    char it$iv = '�';
                                    int var126 = 0;
                                    int var48 = 0;
                                    int codePoint$iv = j++;
                                    if (codePoint$iv == codePointCount) {
                                       return charCount;
                                    }

                                    if (it$iv != '\n' && it$iv != '\r') {
                                       int $i$f$isIsoControl = 0;
                                       if ((0 <= it$iv ? it$iv < ' ' : false) || (127 <= it$iv ? it$iv < 160 : false)) {
                                          return -1;
                                       }
                                    }

                                    if (it$iv == '�') {
                                       return -1;
                                    }

                                    charCount += it$iv < 65536 ? 1 : 2;
                                    Unit var143 = Unit.INSTANCE;
                                    var183 = index$iv;
                                 } else {
                                    int var127 = 0;
                                    int var49 = 0;
                                    int codePoint$iv = j++;
                                    if (codePoint$iv == codePointCount) {
                                       return charCount;
                                    }

                                    if ($i$f$isUtf8Continuation != 10 && $i$f$isUtf8Continuation != 13) {
                                       int $i$f$isIsoControl = 0;
                                       if ((0 <= $i$f$isUtf8Continuation ? $i$f$isUtf8Continuation < 32 : false) || (127 <= $i$f$isUtf8Continuation ? $i$f$isUtf8Continuation < 160 : false)) {
                                          return -1;
                                       }
                                    }

                                    if ($i$f$isUtf8Continuation == 65533) {
                                       return -1;
                                    }

                                    charCount += $i$f$isUtf8Continuation < 65536 ? 1 : 2;
                                    Unit var144 = Unit.INSTANCE;
                                    var183 = index$iv;
                                 }

                                 var189 = 4;
                              }
                           }
                        }
                     }

                     index$iv = var183 + var189;
                     Unit var184 = Unit.INSTANCE;
                  } else {
                     int c = 65533;
                     int var51 = 0;
                     int codePoint$iv = j++;
                     if (codePoint$iv == codePointCount) {
                        return charCount;
                     }

                     if (c != 10 && c != 13) {
                        int $i$f$isIsoControl = 0;
                        if ((0 <= c ? c < 32 : false) || (127 <= c ? c < 160 : false)) {
                           return -1;
                        }
                     }

                     if (c == 65533) {
                        return -1;
                     }

                     charCount += c < 65536 ? 1 : 2;
                     Integer var186 = index$iv++;
                  }
               }
            }
         }
      }

      return charCount;
   }

   // $FF: synthetic method
   public static final int access$codePointIndexToCharIndex(byte[] s, int codePointCount) {
      return codePointIndexToCharIndex(s, codePointCount);
   }

   static {
      char[] var0 = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
      HEX_DIGIT_CHARS = var0;
   }
}
