package okhttp3.internal;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.jvm.internal.TypeIntrinsics;
import kotlin.text.StringsKt;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.FileSystem;
import okio.Options;
import okio.Path;
import okio.Sink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000º\u0001\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010\f\n\u0002\b\u0005\n\u0002\u0010\u0005\n\u0000\n\u0002\u0010\n\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010!\n\u0002\b\u0003\n\u0002\u0010\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u001c\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\u001aG\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007*\b\u0012\u0004\u0012\u00020\b0\u00072\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u001a\u0010\n\u001a\u0016\u0012\u0006\b\u0000\u0012\u00020\b0\u000bj\n\u0012\u0006\b\u0000\u0012\u00020\b`\fH\u0000¢\u0006\u0002\u0010\r\u001aC\u0010\u000e\u001a\u00020\u000f*\b\u0012\u0004\u0012\u00020\b0\u00072\u000e\u0010\t\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u00072\u001a\u0010\n\u001a\u0016\u0012\u0006\b\u0000\u0012\u00020\b0\u000bj\n\u0012\u0006\b\u0000\u0012\u00020\b`\fH\u0000¢\u0006\u0002\u0010\u0010\u001a7\u0010\u0011\u001a\u00020\u0012*\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\u0013\u001a\u00020\b2\u0016\u0010\n\u001a\u0012\u0012\u0004\u0012\u00020\b0\u000bj\b\u0012\u0004\u0012\u00020\b`\fH\u0000¢\u0006\u0002\u0010\u0014\u001a%\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\b0\u0007*\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\u0013\u001a\u00020\bH\u0000¢\u0006\u0002\u0010\u0016\u001a \u0010\u0017\u001a\u00020\u0012*\u00020\b2\b\b\u0002\u0010\u0018\u001a\u00020\u00122\b\b\u0002\u0010\u0019\u001a\u00020\u0012H\u0000\u001a \u0010\u001a\u001a\u00020\u0012*\u00020\b2\b\b\u0002\u0010\u0018\u001a\u00020\u00122\b\b\u0002\u0010\u0019\u001a\u00020\u0012H\u0000\u001a\u001e\u0010\u001b\u001a\u00020\b*\u00020\b2\b\b\u0002\u0010\u0018\u001a\u00020\u00122\b\b\u0002\u0010\u0019\u001a\u00020\u0012\u001a&\u0010\u001c\u001a\u00020\u0012*\u00020\b2\u0006\u0010\u001d\u001a\u00020\b2\b\b\u0002\u0010\u0018\u001a\u00020\u00122\b\b\u0002\u0010\u0019\u001a\u00020\u0012\u001a&\u0010\u001c\u001a\u00020\u0012*\u00020\b2\u0006\u0010\u001e\u001a\u00020\u001f2\b\b\u0002\u0010\u0018\u001a\u00020\u00122\b\b\u0002\u0010\u0019\u001a\u00020\u0012\u001a\f\u0010 \u001a\u00020\u0012*\u00020\bH\u0000\u001a\u0010\u0010!\u001a\u00020\u000f2\u0006\u0010\"\u001a\u00020\bH\u0000\u001a\f\u0010#\u001a\u00020\u0012*\u00020\u001fH\u0000\u001a\u0015\u0010$\u001a\u00020\u0012*\u00020%2\u0006\u0010&\u001a\u00020\u0012H\u0080\u0004\u001a\u0015\u0010$\u001a\u00020\u0012*\u00020'2\u0006\u0010&\u001a\u00020\u0012H\u0080\u0004\u001a\u0015\u0010$\u001a\u00020(*\u00020\u00122\u0006\u0010&\u001a\u00020(H\u0080\u0004\u001a\u0014\u0010)\u001a\u00020**\u00020+2\u0006\u0010,\u001a\u00020\u0012H\u0000\u001a\f\u0010-\u001a\u00020\u0012*\u00020.H\u0000\u001a\u001a\u0010/\u001a\u00020*2\f\u00100\u001a\b\u0012\u0004\u0012\u00020*01H\u0080\bø\u0001\u0000\u001a\u0014\u00102\u001a\u00020\u0012*\u0002032\u0006\u00104\u001a\u00020%H\u0000\u001a\u0016\u00105\u001a\u00020\u0012*\u00020\b2\b\b\u0002\u0010\u0018\u001a\u00020\u0012H\u0000\u001a\u0012\u00106\u001a\u00020(*\u00020\b2\u0006\u00107\u001a\u00020(\u001a\u0016\u00108\u001a\u00020\u0012*\u0004\u0018\u00010\b2\u0006\u00107\u001a\u00020\u0012H\u0000\u001a\u000e\u00109\u001a\u00020**\u00060:j\u0002`;\u001a\u0014\u0010<\u001a\u00020\u000f*\u00020=2\u0006\u0010>\u001a\u00020?H\u0000\u001a\u0014\u0010@\u001a\u00020**\u00020=2\u0006\u0010A\u001a\u00020?H\u0000\u001a\u0014\u0010B\u001a\u00020**\u00020=2\u0006\u0010C\u001a\u00020?H\u0000\u001a%\u0010D\u001a\u00020*\"\u0004\b\u0000\u0010E*\b\u0012\u0004\u0012\u0002HE0F2\u0006\u0010G\u001a\u0002HEH\u0000¢\u0006\u0002\u0010H\u001a\"\u0010I\u001a\u00020J*\u00060Kj\u0002`L2\u0010\u0010M\u001a\f\u0012\b\u0012\u00060Kj\u0002`L0NH\u0000\u001a;\u0010O\u001a\b\u0012\u0004\u0012\u0002HP0N\"\u0004\b\u0000\u0010P*\b\u0012\u0004\u0012\u0002HP0Q2\u0017\u0010R\u001a\u0013\u0012\u0004\u0012\u0002HP\u0012\u0004\u0012\u00020\u000f0S¢\u0006\u0002\bTH\u0080\bø\u0001\u0000\u001a \u0010V\u001a\u00020*2\u0006\u0010W\u001a\u00020(2\u0006\u0010X\u001a\u00020(2\u0006\u0010Y\u001a\u00020(H\u0000\u001a0\u0010Z\u001a\b\u0012\u0004\u0012\u0002HP0N\"\u0004\b\u0000\u0010P2\f\u0010[\u001a\b\u0012\u0004\u0012\u0002HP0Q2\f\u00104\u001a\b\u0012\u0004\u0012\u0002HP0QH\u0000\"\u0010\u0010\u0000\u001a\u00020\u00018\u0000X\u0081\u0004¢\u0006\u0002\n\u0000\"\u0014\u0010\u0002\u001a\u00020\u0003X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0005\"\u000e\u0010U\u001a\u00020\bX\u0080T¢\u0006\u0002\n\u0000\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006\\"},
   d2 = {"EMPTY_BYTE_ARRAY", "", "UNICODE_BOMS", "Lokio/Options;", "getUNICODE_BOMS", "()Lokio/Options;", "intersect", "", "", "other", "comparator", "Ljava/util/Comparator;", "Lkotlin/Comparator;", "([Ljava/lang/String;[Ljava/lang/String;Ljava/util/Comparator;)[Ljava/lang/String;", "hasIntersection", "", "([Ljava/lang/String;[Ljava/lang/String;Ljava/util/Comparator;)Z", "indexOf", "", "value", "([Ljava/lang/String;Ljava/lang/String;Ljava/util/Comparator;)I", "concat", "([Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;", "indexOfFirstNonAsciiWhitespace", "startIndex", "endIndex", "indexOfLastNonAsciiWhitespace", "trimSubstring", "delimiterOffset", "delimiters", "delimiter", "", "indexOfControlOrNonAscii", "isSensitiveHeader", "name", "parseHexDigit", "and", "", "mask", "", "", "writeMedium", "", "Lokio/BufferedSink;", "medium", "readMedium", "Lokio/BufferedSource;", "ignoreIoExceptions", "block", "Lkotlin/Function0;", "skipAll", "Lokio/Buffer;", "b", "indexOfNonWhitespace", "toLongOrDefault", "defaultValue", "toNonNegativeInt", "closeQuietly", "Ljava/io/Closeable;", "Lokio/Closeable;", "isCivilized", "Lokio/FileSystem;", "file", "Lokio/Path;", "deleteIfExists", "path", "deleteContents", "directory", "addIfAbsent", "E", "", "element", "(Ljava/util/List;Ljava/lang/Object;)V", "withSuppressed", "", "Ljava/lang/Exception;", "Lkotlin/Exception;", "suppressed", "", "filterList", "T", "", "predicate", "Lkotlin/Function1;", "Lkotlin/ExtensionFunctionType;", "USER_AGENT", "checkOffsetAndCount", "arrayLength", "offset", "count", "interleave", "a", "okhttp"}
)
@SourceDebugExtension({"SMAP\n-UtilCommon.kt\nKotlin\n*S Kotlin\n*F\n+ 1 -UtilCommon.kt\nokhttp3/internal/_UtilCommonKt\n+ 2 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 3 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 4 Okio.kt\nokio/Okio__OkioKt\n*L\n1#1,400:1\n37#2,2:401\n1772#3,6:403\n72#4:409\n58#4,22:410\n*S KotlinDebug\n*F\n+ 1 -UtilCommon.kt\nokhttp3/internal/_UtilCommonKt\n*L\n68#1:401,2\n97#1:403,6\n303#1:409\n303#1:410,22\n*E\n"})
public final class _UtilCommonKt {
   @JvmField
   @NotNull
   public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
   @NotNull
   private static final Options UNICODE_BOMS;
   @NotNull
   public static final String USER_AGENT = "okhttp/5.3.2";

   @NotNull
   public static final Options getUNICODE_BOMS() {
      return UNICODE_BOMS;
   }

   @NotNull
   public static final String[] intersect(@NotNull String[] $this$intersect, @NotNull String[] other, @NotNull Comparator<? super String> comparator) {
      Intrinsics.checkNotNullParameter($this$intersect, "<this>");
      Intrinsics.checkNotNullParameter(other, "other");
      Intrinsics.checkNotNullParameter(comparator, "comparator");
      List result = (List)(new ArrayList());
      int thisCollection$iv = 0;

      for(int var5 = $this$intersect.length; thisCollection$iv < var5; ++thisCollection$iv) {
         String a = $this$intersect[thisCollection$iv];
         int var7 = 0;

         for(int var8 = other.length; var7 < var8; ++var7) {
            String b = other[var7];
            if (comparator.compare(a, b) == 0) {
               result.add(a);
               break;
            }
         }
      }

      Collection $this$toTypedArray$iv = (Collection)result;
      int $i$f$toTypedArray = 0;
      return (String[])$this$toTypedArray$iv.toArray(new String[0]);
   }

   public static final boolean hasIntersection(@NotNull String[] $this$hasIntersection, @Nullable String[] other, @NotNull Comparator<? super String> comparator) {
      Intrinsics.checkNotNullParameter($this$hasIntersection, "<this>");
      Intrinsics.checkNotNullParameter(comparator, "comparator");
      if ($this$hasIntersection.length != 0 && other != null && other.length != 0) {
         int var3 = 0;

         for(int var4 = $this$hasIntersection.length; var3 < var4; ++var3) {
            String a = $this$hasIntersection[var3];
            String[] var6 = other;
            int var7 = 0;

            for(int var8 = other.length; var7 < var8; ++var7) {
               String b = var6[var7];
               if (comparator.compare(a, b) == 0) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static final int indexOf(@NotNull String[] $this$indexOf, @NotNull String value, @NotNull Comparator<String> comparator) {
      Intrinsics.checkNotNullParameter($this$indexOf, "<this>");
      Intrinsics.checkNotNullParameter(value, "value");
      Intrinsics.checkNotNullParameter(comparator, "comparator");
      Object[] $this$indexOfFirst$iv = $this$indexOf;
      int $i$f$indexOfFirst = 0;
      int index$iv = 0;
      int var6 = $this$indexOf.length;

      int var10000;
      while(true) {
         if (index$iv >= var6) {
            var10000 = -1;
            break;
         }

         String it = (String)$this$indexOfFirst$iv[index$iv];
         int var8 = 0;
         if (comparator.compare(it, value) == 0) {
            var10000 = index$iv;
            break;
         }

         ++index$iv;
      }

      return var10000;
   }

   @NotNull
   public static final String[] concat(@NotNull String[] $this$concat, @NotNull String value) {
      Intrinsics.checkNotNullParameter($this$concat, "<this>");
      Intrinsics.checkNotNullParameter(value, "value");
      Object[] var10000 = Arrays.copyOf($this$concat, $this$concat.length + 1);
      Intrinsics.checkNotNullExpressionValue(var10000, "copyOf(...)");
      String[] result = (String[])var10000;
      result[ArraysKt.getLastIndex(result)] = value;
      return result;
   }

   public static final int indexOfFirstNonAsciiWhitespace(@NotNull String $this$indexOfFirstNonAsciiWhitespace, int startIndex, int endIndex) {
      Intrinsics.checkNotNullParameter($this$indexOfFirstNonAsciiWhitespace, "<this>");

      for(int i = startIndex; i < endIndex; ++i) {
         switch ($this$indexOfFirstNonAsciiWhitespace.charAt(i)) {
            case '\t':
            case '\n':
            case '\f':
            case '\r':
            case ' ':
            default:
               return i;
         }
      }

      return endIndex;
   }

   // $FF: synthetic method
   public static int indexOfFirstNonAsciiWhitespace$default(String var0, int var1, int var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = 0;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.length();
      }

      return indexOfFirstNonAsciiWhitespace(var0, var1, var2);
   }

   public static final int indexOfLastNonAsciiWhitespace(@NotNull String $this$indexOfLastNonAsciiWhitespace, int startIndex, int endIndex) {
      Intrinsics.checkNotNullParameter($this$indexOfLastNonAsciiWhitespace, "<this>");
      int i = endIndex - 1;
      if (startIndex <= i) {
         while(true) {
            switch ($this$indexOfLastNonAsciiWhitespace.charAt(i)) {
               case '\t':
               case '\n':
               case '\f':
               case '\r':
               case ' ':
                  if (i == startIndex) {
                     return startIndex;
                  }

                  --i;
                  break;
               default:
                  return i + 1;
            }
         }
      } else {
         return startIndex;
      }
   }

   // $FF: synthetic method
   public static int indexOfLastNonAsciiWhitespace$default(String var0, int var1, int var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = 0;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.length();
      }

      return indexOfLastNonAsciiWhitespace(var0, var1, var2);
   }

   @NotNull
   public static final String trimSubstring(@NotNull String $this$trimSubstring, int startIndex, int endIndex) {
      Intrinsics.checkNotNullParameter($this$trimSubstring, "<this>");
      int start = indexOfFirstNonAsciiWhitespace($this$trimSubstring, startIndex, endIndex);
      int end = indexOfLastNonAsciiWhitespace($this$trimSubstring, start, endIndex);
      String var10000 = $this$trimSubstring.substring(start, end);
      Intrinsics.checkNotNullExpressionValue(var10000, "substring(...)");
      return var10000;
   }

   // $FF: synthetic method
   public static String trimSubstring$default(String var0, int var1, int var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = 0;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.length();
      }

      return trimSubstring(var0, var1, var2);
   }

   public static final int delimiterOffset(@NotNull String $this$delimiterOffset, @NotNull String delimiters, int startIndex, int endIndex) {
      Intrinsics.checkNotNullParameter($this$delimiterOffset, "<this>");
      Intrinsics.checkNotNullParameter(delimiters, "delimiters");

      for(int i = startIndex; i < endIndex; ++i) {
         if (StringsKt.contains$default((CharSequence)delimiters, $this$delimiterOffset.charAt(i), false, 2, (Object)null)) {
            return i;
         }
      }

      return endIndex;
   }

   // $FF: synthetic method
   public static int delimiterOffset$default(String var0, String var1, int var2, int var3, int var4, Object var5) {
      if ((var4 & 2) != 0) {
         var2 = 0;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.length();
      }

      return delimiterOffset(var0, var1, var2, var3);
   }

   public static final int delimiterOffset(@NotNull String $this$delimiterOffset, char delimiter, int startIndex, int endIndex) {
      Intrinsics.checkNotNullParameter($this$delimiterOffset, "<this>");

      for(int i = startIndex; i < endIndex; ++i) {
         if ($this$delimiterOffset.charAt(i) == delimiter) {
            return i;
         }
      }

      return endIndex;
   }

   // $FF: synthetic method
   public static int delimiterOffset$default(String var0, char var1, int var2, int var3, int var4, Object var5) {
      if ((var4 & 2) != 0) {
         var2 = 0;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.length();
      }

      return delimiterOffset(var0, var1, var2, var3);
   }

   public static final int indexOfControlOrNonAscii(@NotNull String $this$indexOfControlOrNonAscii) {
      Intrinsics.checkNotNullParameter($this$indexOfControlOrNonAscii, "<this>");
      int i = 0;

      for(int var2 = $this$indexOfControlOrNonAscii.length(); i < var2; ++i) {
         char c = $this$indexOfControlOrNonAscii.charAt(i);
         if (Intrinsics.compare(c, 31) <= 0 || Intrinsics.compare(c, 127) >= 0) {
            return i;
         }
      }

      return -1;
   }

   public static final boolean isSensitiveHeader(@NotNull String name) {
      Intrinsics.checkNotNullParameter(name, "name");
      return StringsKt.equals(name, "Authorization", true) || StringsKt.equals(name, "Cookie", true) || StringsKt.equals(name, "Proxy-Authorization", true) || StringsKt.equals(name, "Set-Cookie", true);
   }

   public static final int parseHexDigit(char $this$parseHexDigit) {
      return ('0' <= $this$parseHexDigit ? $this$parseHexDigit < ':' : false) ? $this$parseHexDigit - 48 : (('a' <= $this$parseHexDigit ? $this$parseHexDigit < 'g' : false) ? $this$parseHexDigit - 97 + 10 : (('A' <= $this$parseHexDigit ? $this$parseHexDigit < 'G' : false) ? $this$parseHexDigit - 65 + 10 : -1));
   }

   public static final int and(byte $this$and, int mask) {
      return $this$and & mask;
   }

   public static final int and(short $this$and, int mask) {
      return $this$and & mask;
   }

   public static final long and(int $this$and, long mask) {
      return (long)$this$and & mask;
   }

   public static final void writeMedium(@NotNull BufferedSink $this$writeMedium, int medium) throws IOException {
      Intrinsics.checkNotNullParameter($this$writeMedium, "<this>");
      $this$writeMedium.writeByte(medium >>> 16 & 255);
      $this$writeMedium.writeByte(medium >>> 8 & 255);
      $this$writeMedium.writeByte(medium & 255);
   }

   public static final int readMedium(@NotNull BufferedSource $this$readMedium) throws IOException {
      Intrinsics.checkNotNullParameter($this$readMedium, "<this>");
      return and((byte)$this$readMedium.readByte(), 255) << 16 | and((byte)$this$readMedium.readByte(), 255) << 8 | and((byte)$this$readMedium.readByte(), 255);
   }

   public static final void ignoreIoExceptions(@NotNull Function0<Unit> block) {
      Intrinsics.checkNotNullParameter(block, "block");
      int $i$f$ignoreIoExceptions = 0;

      try {
         block.invoke();
      } catch (IOException var3) {
      }

   }

   public static final int skipAll(@NotNull Buffer $this$skipAll, byte b) {
      Intrinsics.checkNotNullParameter($this$skipAll, "<this>");
      int count = 0;

      while(!$this$skipAll.exhausted() && $this$skipAll.getByte(0L) == b) {
         ++count;
         $this$skipAll.readByte();
      }

      return count;
   }

   public static final int indexOfNonWhitespace(@NotNull String $this$indexOfNonWhitespace, int startIndex) {
      Intrinsics.checkNotNullParameter($this$indexOfNonWhitespace, "<this>");
      int i = startIndex;

      for(int var3 = $this$indexOfNonWhitespace.length(); i < var3; ++i) {
         char c = $this$indexOfNonWhitespace.charAt(i);
         if (c != ' ' && c != '\t') {
            return i;
         }
      }

      return $this$indexOfNonWhitespace.length();
   }

   // $FF: synthetic method
   public static int indexOfNonWhitespace$default(String var0, int var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = 0;
      }

      return indexOfNonWhitespace(var0, var1);
   }

   public static final long toLongOrDefault(@NotNull String $this$toLongOrDefault, long defaultValue) {
      Intrinsics.checkNotNullParameter($this$toLongOrDefault, "<this>");

      long var3;
      try {
         var3 = Long.parseLong($this$toLongOrDefault);
      } catch (NumberFormatException var6) {
         var3 = defaultValue;
      }

      return var3;
   }

   public static final int toNonNegativeInt(@Nullable String $this$toNonNegativeInt, int defaultValue) {
      try {
         if ($this$toNonNegativeInt != null) {
            long value = Long.parseLong($this$toNonNegativeInt);
            return value > 2147483647L ? Integer.MAX_VALUE : (value < 0L ? 0 : (int)value);
         } else {
            return defaultValue;
         }
      } catch (NumberFormatException var4) {
         return defaultValue;
      }
   }

   public static final void closeQuietly(@NotNull Closeable $this$closeQuietly) {
      Intrinsics.checkNotNullParameter($this$closeQuietly, "<this>");

      try {
         $this$closeQuietly.close();
      } catch (RuntimeException rethrown) {
         throw rethrown;
      } catch (Exception var3) {
      }

   }

   public static final boolean isCivilized(@NotNull FileSystem $this$isCivilized, @NotNull Path file) {
      Intrinsics.checkNotNullParameter($this$isCivilized, "<this>");
      Intrinsics.checkNotNullParameter(file, "file");
      Closeable $this$use$iv = $this$isCivilized.sink(file);
      int $i$f$use = 0;
      Throwable thrown$iv = null;
      boolean var18 = false;

      boolean var10;
      label156: {
         label151: {
            label150: {
               try {
                  var18 = true;
                  Sink it = (Sink)$this$use$iv;
                  boolean var6 = false;

                  try {
                     $this$isCivilized.delete(file);
                     var10 = true;
                     var18 = false;
                     break label156;
                  } catch (IOException var23) {
                     Unit var27 = Unit.INSTANCE;
                     var18 = false;
                     break label150;
                  }
               } catch (Throwable t$iv) {
                  thrown$iv = t$iv;
                  Object it = null;
                  var18 = false;
               } finally {
                  if (var18) {
                     try {
                        if ($this$use$iv != null) {
                           $this$use$iv.close();
                        }
                     } catch (Throwable t$iv) {
                        if (thrown$iv != null) {
                           ExceptionsKt.addSuppressed(thrown$iv, t$iv);
                        }
                     }

                  }
               }

               try {
                  if ($this$use$iv != null) {
                     $this$use$iv.close();
                  }
               } catch (Throwable t$iv) {
                  if (thrown$iv == null) {
                     thrown$iv = t$iv;
                  } else {
                     ExceptionsKt.addSuppressed(thrown$iv, t$iv);
                  }
               }
               break label151;
            }

            try {
               if ($this$use$iv != null) {
                  $this$use$iv.close();
               }
            } catch (Throwable t$iv) {
               thrown$iv = t$iv;
            }
         }

         if (thrown$iv != null) {
            throw thrown$iv;
         }

         $this$isCivilized.delete(file);
         return false;
      }

      try {
         if ($this$use$iv != null) {
            $this$use$iv.close();
         }
      } catch (Throwable var20) {
      }

      return var10;
   }

   public static final void deleteIfExists(@NotNull FileSystem $this$deleteIfExists, @NotNull Path path) {
      Intrinsics.checkNotNullParameter($this$deleteIfExists, "<this>");
      Intrinsics.checkNotNullParameter(path, "path");

      try {
         $this$deleteIfExists.delete(path);
      } catch (FileNotFoundException var3) {
      }
   }

   public static final void deleteContents(@NotNull FileSystem $this$deleteContents, @NotNull Path directory) {
      Intrinsics.checkNotNullParameter($this$deleteContents, "<this>");
      Intrinsics.checkNotNullParameter(directory, "directory");
      IOException exception = null;

      List files;
      try {
         files = $this$deleteContents.list(directory);
      } catch (FileNotFoundException var7) {
         return;
      }

      for(Path file : files) {
         try {
            if ($this$deleteContents.metadata(file).isDirectory()) {
               deleteContents($this$deleteContents, file);
            }

            $this$deleteContents.delete(file);
         } catch (IOException ioe) {
            if (exception == null) {
               exception = ioe;
            }
         }
      }

      if (exception != null) {
         throw exception;
      }
   }

   public static final <E> void addIfAbsent(@NotNull List<E> $this$addIfAbsent, E element) {
      Intrinsics.checkNotNullParameter($this$addIfAbsent, "<this>");
      if (!$this$addIfAbsent.contains(element)) {
         $this$addIfAbsent.add(element);
      }

   }

   @NotNull
   public static final Throwable withSuppressed(@NotNull Exception $this$withSuppressed, @NotNull List<? extends Exception> suppressed) {
      Intrinsics.checkNotNullParameter($this$withSuppressed, "<this>");
      Intrinsics.checkNotNullParameter(suppressed, "suppressed");
      Exception $this$withSuppressed_u24lambda_u240 = $this$withSuppressed;
      int var4 = 0;

      for(Exception e : suppressed) {
         ExceptionsKt.addSuppressed((Throwable)$this$withSuppressed_u24lambda_u240, (Throwable)e);
      }

      return (Throwable)$this$withSuppressed;
   }

   @NotNull
   public static final <T> List<T> filterList(@NotNull Iterable<? extends T> $this$filterList, @NotNull Function1<? super T, Boolean> predicate) {
      Intrinsics.checkNotNullParameter($this$filterList, "<this>");
      Intrinsics.checkNotNullParameter(predicate, "predicate");
      int $i$f$filterList = 0;
      List result = CollectionsKt.emptyList();

      for(Object i : $this$filterList) {
         if ((Boolean)predicate.invoke(i)) {
            if (result.isEmpty()) {
               result = (List)(new ArrayList());
            }

            Intrinsics.checkNotNull(result, "null cannot be cast to non-null type kotlin.collections.MutableList<T of okhttp3.internal._UtilCommonKt.filterList>");
            TypeIntrinsics.asMutableList(result).add(i);
         }
      }

      return result;
   }

   public static final void checkOffsetAndCount(long arrayLength, long offset, long count) {
      if ((offset | count) < 0L || offset > arrayLength || arrayLength - offset < count) {
         throw new ArrayIndexOutOfBoundsException("length=" + arrayLength + ", offset=" + offset + ", count=" + offset);
      }
   }

   @NotNull
   public static final <T> List<T> interleave(@NotNull Iterable<? extends T> a, @NotNull Iterable<? extends T> b) {
      Intrinsics.checkNotNullParameter(a, "a");
      Intrinsics.checkNotNullParameter(b, "b");
      Iterator ia = a.iterator();
      Iterator ib = b.iterator();
      List var4 = CollectionsKt.createListBuilder();
      List $this$interleave_u24lambda_u240 = var4;
      int var6 = 0;

      while(ia.hasNext() || ib.hasNext()) {
         if (ia.hasNext()) {
            $this$interleave_u24lambda_u240.add(ia.next());
         }

         if (ib.hasNext()) {
            $this$interleave_u24lambda_u240.add(ib.next());
         }
      }

      return CollectionsKt.build(var4);
   }

   static {
      Options.Companion var10000 = Options.Companion;
      ByteString[] var0 = new ByteString[]{ByteString.Companion.decodeHex("efbbbf"), ByteString.Companion.decodeHex("feff"), ByteString.Companion.decodeHex("fffe0000"), ByteString.Companion.decodeHex("fffe"), ByteString.Companion.decodeHex("0000feff")};
      UNICODE_BOMS = var10000.of(var0);
   }
}
