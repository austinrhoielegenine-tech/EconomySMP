package okhttp3.internal.url;

import java.nio.charset.Charset;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import okhttp3.internal._UtilCommonKt;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u00006\n\u0000\n\u0002\u0010\u0019\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\u001aV\u0010\u0010\u001a\u00020\u0011*\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00052\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u00052\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u00192\u0006\u0010\u001c\u001a\u00020\u00192\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eH\u0000\u001a\\\u0010\u001f\u001a\u00020\u0005*\u00020\u00052\b\b\u0002\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u00052\b\b\u0002\u0010\u0018\u001a\u00020\u00192\b\b\u0002\u0010\u001a\u001a\u00020\u00192\b\b\u0002\u0010\u001b\u001a\u00020\u00192\b\b\u0002\u0010\u001c\u001a\u00020\u00192\n\b\u0002\u0010\u001d\u001a\u0004\u0018\u00010\u001eH\u0000\u001a,\u0010 \u001a\u00020\u0011*\u00020\u00122\u0006\u0010!\u001a\u00020\u00052\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u001b\u001a\u00020\u0019H\u0000\u001aP\u0010\"\u001a\u00020\u0005*\u00020\u00052\b\b\u0002\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u00052\b\b\u0002\u0010\u0018\u001a\u00020\u00192\b\b\u0002\u0010\u001a\u001a\u00020\u00192\b\b\u0002\u0010\u001b\u001a\u00020\u00192\b\b\u0002\u0010\u001c\u001a\u00020\u0019H\u0000\u001a*\u0010#\u001a\u00020\u0005*\u00020\u00052\b\b\u0002\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u00152\b\b\u0002\u0010\u001b\u001a\u00020\u0019H\u0000\u001a\u001c\u0010$\u001a\u00020\u0019*\u00020\u00052\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0015H\u0000\"\u0014\u0010\u0000\u001a\u00020\u0001X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0003\"\u000e\u0010\u0004\u001a\u00020\u0005X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0006\u001a\u00020\u0005X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0007\u001a\u00020\u0005X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\b\u001a\u00020\u0005X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\t\u001a\u00020\u0005X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\n\u001a\u00020\u0005X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u000b\u001a\u00020\u0005X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\f\u001a\u00020\u0005X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\r\u001a\u00020\u0005X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u000e\u001a\u00020\u0005X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u000f\u001a\u00020\u0005X\u0080T¢\u0006\u0002\n\u0000¨\u0006%"},
   d2 = {"HEX_DIGITS", "", "getHEX_DIGITS", "()[C", "USERNAME_ENCODE_SET", "", "PASSWORD_ENCODE_SET", "PATH_SEGMENT_ENCODE_SET", "PATH_SEGMENT_ENCODE_SET_URI", "QUERY_ENCODE_SET", "QUERY_COMPONENT_REENCODE_SET", "QUERY_COMPONENT_ENCODE_SET", "QUERY_COMPONENT_ENCODE_SET_URI", "FORM_ENCODE_SET", "FRAGMENT_ENCODE_SET", "FRAGMENT_ENCODE_SET_URI", "writeCanonicalized", "", "Lokio/Buffer;", "input", "pos", "", "limit", "encodeSet", "alreadyEncoded", "", "strict", "plusIsSpace", "unicodeAllowed", "charset", "Ljava/nio/charset/Charset;", "canonicalizeWithCharset", "writePercentDecoded", "encoded", "canonicalize", "percentDecode", "isPercentEncoded", "okhttp"}
)
public final class _UrlKt {
   @NotNull
   private static final char[] HEX_DIGITS;
   @NotNull
   public static final String USERNAME_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
   @NotNull
   public static final String PASSWORD_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
   @NotNull
   public static final String PATH_SEGMENT_ENCODE_SET = " \"<>^`{}|/\\?#";
   @NotNull
   public static final String PATH_SEGMENT_ENCODE_SET_URI = "[]";
   @NotNull
   public static final String QUERY_ENCODE_SET = " \"'<>#";
   @NotNull
   public static final String QUERY_COMPONENT_REENCODE_SET = " \"'<>#&=";
   @NotNull
   public static final String QUERY_COMPONENT_ENCODE_SET = " !\"#$&'(),/:;<=>?@[]\\^`{|}~";
   @NotNull
   public static final String QUERY_COMPONENT_ENCODE_SET_URI = "\\^`{|}";
   @NotNull
   public static final String FORM_ENCODE_SET = " !\"#$&'()+,/:;<=>?@[\\]^`{|}~";
   @NotNull
   public static final String FRAGMENT_ENCODE_SET = "";
   @NotNull
   public static final String FRAGMENT_ENCODE_SET_URI = " \"#<>\\^`{|}";

   @NotNull
   public static final char[] getHEX_DIGITS() {
      return HEX_DIGITS;
   }

   public static final void writeCanonicalized(@NotNull Buffer $this$writeCanonicalized, @NotNull String input, int pos, int limit, @NotNull String encodeSet, boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean unicodeAllowed, @Nullable Charset charset) {
      Intrinsics.checkNotNullParameter($this$writeCanonicalized, "<this>");
      Intrinsics.checkNotNullParameter(input, "input");
      Intrinsics.checkNotNullParameter(encodeSet, "encodeSet");
      Buffer encodedCharBuffer = null;
      int codePoint = 0;

      for(int i = pos; i < limit; i += Character.charCount(codePoint)) {
         codePoint = input.codePointAt(i);
         if (!alreadyEncoded || codePoint != 9 && codePoint != 10 && codePoint != 12 && codePoint != 13) {
            if (codePoint == 32 && encodeSet == " !\"#$&'()+,/:;<=>?@[\\]^`{|}~") {
               $this$writeCanonicalized.writeUtf8("+");
            } else if (codePoint == 43 && plusIsSpace) {
               $this$writeCanonicalized.writeUtf8(alreadyEncoded ? "+" : "%2B");
            } else if (codePoint < 32 || codePoint == 127 || codePoint >= 128 && !unicodeAllowed || StringsKt.contains$default((CharSequence)encodeSet, (char)codePoint, false, 2, (Object)null) || codePoint == 37 && (!alreadyEncoded || strict && !isPercentEncoded(input, i, limit))) {
               if (encodedCharBuffer == null) {
                  encodedCharBuffer = new Buffer();
               }

               if (charset != null && !Intrinsics.areEqual((Object)charset, (Object)Charsets.UTF_8)) {
                  encodedCharBuffer.writeString(input, i, i + Character.charCount(codePoint), charset);
               } else {
                  encodedCharBuffer.writeUtf8CodePoint(codePoint);
               }

               while(!encodedCharBuffer.exhausted()) {
                  int b = encodedCharBuffer.readByte() & 255;
                  $this$writeCanonicalized.writeByte(37);
                  $this$writeCanonicalized.writeByte(HEX_DIGITS[b >> 4 & 15]);
                  $this$writeCanonicalized.writeByte(HEX_DIGITS[b & 15]);
               }

               Unit var15 = Unit.INSTANCE;
            } else {
               $this$writeCanonicalized.writeUtf8CodePoint(codePoint);
            }
         } else {
            Unit var10000 = Unit.INSTANCE;
         }
      }

   }

   @NotNull
   public static final String canonicalizeWithCharset(@NotNull String $this$canonicalizeWithCharset, int pos, int limit, @NotNull String encodeSet, boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean unicodeAllowed, @Nullable Charset charset) {
      Intrinsics.checkNotNullParameter($this$canonicalizeWithCharset, "<this>");
      Intrinsics.checkNotNullParameter(encodeSet, "encodeSet");
      int codePoint = 0;

      for(int i = pos; i < limit; i += Character.charCount(codePoint)) {
         codePoint = $this$canonicalizeWithCharset.codePointAt(i);
         if (codePoint < 32 || codePoint == 127 || codePoint >= 128 && !unicodeAllowed || StringsKt.contains$default((CharSequence)encodeSet, (char)codePoint, false, 2, (Object)null) || codePoint == 37 && (!alreadyEncoded || strict && !isPercentEncoded($this$canonicalizeWithCharset, i, limit)) || codePoint == 43 && plusIsSpace) {
            Buffer out = new Buffer();
            out.writeUtf8($this$canonicalizeWithCharset, pos, i);
            writeCanonicalized(out, $this$canonicalizeWithCharset, i, limit, encodeSet, alreadyEncoded, strict, plusIsSpace, unicodeAllowed, charset);
            return out.readUtf8();
         }
      }

      String var10000 = $this$canonicalizeWithCharset.substring(pos, limit);
      Intrinsics.checkNotNullExpressionValue(var10000, "substring(...)");
      return var10000;
   }

   // $FF: synthetic method
   public static String canonicalizeWithCharset$default(String var0, int var1, int var2, String var3, boolean var4, boolean var5, boolean var6, boolean var7, Charset var8, int var9, Object var10) {
      if ((var9 & 1) != 0) {
         var1 = 0;
      }

      if ((var9 & 2) != 0) {
         var2 = var0.length();
      }

      if ((var9 & 8) != 0) {
         var4 = false;
      }

      if ((var9 & 16) != 0) {
         var5 = false;
      }

      if ((var9 & 32) != 0) {
         var6 = false;
      }

      if ((var9 & 64) != 0) {
         var7 = false;
      }

      if ((var9 & 128) != 0) {
         var8 = null;
      }

      return canonicalizeWithCharset(var0, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public static final void writePercentDecoded(@NotNull Buffer $this$writePercentDecoded, @NotNull String encoded, int pos, int limit, boolean plusIsSpace) {
      Intrinsics.checkNotNullParameter($this$writePercentDecoded, "<this>");
      Intrinsics.checkNotNullParameter(encoded, "encoded");
      int codePoint = 0;
      int i = pos;

      while(i < limit) {
         codePoint = encoded.codePointAt(i);
         if (codePoint == 37 && i + 2 < limit) {
            int d1 = _UtilCommonKt.parseHexDigit(encoded.charAt(i + 1));
            int d2 = _UtilCommonKt.parseHexDigit(encoded.charAt(i + 2));
            if (d1 != -1 && d2 != -1) {
               $this$writePercentDecoded.writeByte((d1 << 4) + d2);
               i += 2;
               i += Character.charCount(codePoint);
               continue;
            }
         } else if (codePoint == 43 && plusIsSpace) {
            $this$writePercentDecoded.writeByte(32);
            ++i;
            continue;
         }

         $this$writePercentDecoded.writeUtf8CodePoint(codePoint);
         i += Character.charCount(codePoint);
      }

   }

   @NotNull
   public static final String canonicalize(@NotNull String $this$canonicalize, int pos, int limit, @NotNull String encodeSet, boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean unicodeAllowed) {
      Intrinsics.checkNotNullParameter($this$canonicalize, "<this>");
      Intrinsics.checkNotNullParameter(encodeSet, "encodeSet");
      return canonicalizeWithCharset$default($this$canonicalize, pos, limit, encodeSet, alreadyEncoded, strict, plusIsSpace, unicodeAllowed, (Charset)null, 128, (Object)null);
   }

   // $FF: synthetic method
   public static String canonicalize$default(String var0, int var1, int var2, String var3, boolean var4, boolean var5, boolean var6, boolean var7, int var8, Object var9) {
      if ((var8 & 1) != 0) {
         var1 = 0;
      }

      if ((var8 & 2) != 0) {
         var2 = var0.length();
      }

      if ((var8 & 8) != 0) {
         var4 = false;
      }

      if ((var8 & 16) != 0) {
         var5 = false;
      }

      if ((var8 & 32) != 0) {
         var6 = false;
      }

      if ((var8 & 64) != 0) {
         var7 = false;
      }

      return canonicalize(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   @NotNull
   public static final String percentDecode(@NotNull String $this$percentDecode, int pos, int limit, boolean plusIsSpace) {
      Intrinsics.checkNotNullParameter($this$percentDecode, "<this>");

      for(int i = pos; i < limit; ++i) {
         char c = $this$percentDecode.charAt(i);
         if (c == '%' || c == '+' && plusIsSpace) {
            Buffer out = new Buffer();
            out.writeUtf8($this$percentDecode, pos, i);
            writePercentDecoded(out, $this$percentDecode, i, limit, plusIsSpace);
            return out.readUtf8();
         }
      }

      String var10000 = $this$percentDecode.substring(pos, limit);
      Intrinsics.checkNotNullExpressionValue(var10000, "substring(...)");
      return var10000;
   }

   // $FF: synthetic method
   public static String percentDecode$default(String var0, int var1, int var2, boolean var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = 0;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.length();
      }

      if ((var4 & 4) != 0) {
         var3 = false;
      }

      return percentDecode(var0, var1, var2, var3);
   }

   public static final boolean isPercentEncoded(@NotNull String $this$isPercentEncoded, int pos, int limit) {
      Intrinsics.checkNotNullParameter($this$isPercentEncoded, "<this>");
      return pos + 2 < limit && $this$isPercentEncoded.charAt(pos) == '%' && _UtilCommonKt.parseHexDigit($this$isPercentEncoded.charAt(pos + 1)) != -1 && _UtilCommonKt.parseHexDigit($this$isPercentEncoded.charAt(pos + 2)) != -1;
   }

   static {
      char[] var0 = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
      HEX_DIGITS = var0;
   }
}
