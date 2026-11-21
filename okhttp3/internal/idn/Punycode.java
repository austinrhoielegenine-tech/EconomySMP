package okhttp3.internal.idn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.IntProgression;
import kotlin.ranges.RangesKt;
import kotlin.text.CharsKt;
import kotlin.text.StringsKt;
import okio.Buffer;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010 \n\u0002\b\u0004\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0015\u001a\u00020\u0005J(\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0015\u001a\u00020\u00052\u0006\u0010\u0018\u001a\u00020\r2\u0006\u0010\u0019\u001a\u00020\r2\u0006\u0010\u001a\u001a\u00020\u001bH\u0002J\u0010\u0010\u001c\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0015\u001a\u00020\u0005J(\u0010\u001d\u001a\u00020\u00172\u0006\u0010\u0015\u001a\u00020\u00052\u0006\u0010\u0018\u001a\u00020\r2\u0006\u0010\u0019\u001a\u00020\r2\u0006\u0010\u001a\u001a\u00020\u001bH\u0002J \u0010\u001e\u001a\u00020\r2\u0006\u0010\u001f\u001a\u00020\r2\u0006\u0010 \u001a\u00020\r2\u0006\u0010!\u001a\u00020\u0017H\u0002J\u001c\u0010\"\u001a\u00020\u0017*\u00020\u00052\u0006\u0010\u0018\u001a\u00020\r2\u0006\u0010\u0019\u001a\u00020\rH\u0002J\"\u0010#\u001a\b\u0012\u0004\u0012\u00020\r0$*\u00020\u00052\u0006\u0010\u0018\u001a\u00020\r2\u0006\u0010\u0019\u001a\u00020\rH\u0002R\u0014\u0010\u0004\u001a\u00020\u0005X\u0086D¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u000e\u0010\f\u001a\u00020\rX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\rX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\rX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\rX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\rX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\rX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\rX\u0082T¢\u0006\u0002\n\u0000R\u0018\u0010%\u001a\u00020\r*\u00020\r8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b&\u0010'¨\u0006("},
   d2 = {"Lokhttp3/internal/idn/Punycode;", "", "<init>", "()V", "PREFIX_STRING", "", "getPREFIX_STRING", "()Ljava/lang/String;", "PREFIX", "Lokio/ByteString;", "getPREFIX", "()Lokio/ByteString;", "BASE", "", "TMIN", "TMAX", "SKEW", "DAMP", "INITIAL_BIAS", "INITIAL_N", "encode", "string", "encodeLabel", "", "pos", "limit", "result", "Lokio/Buffer;", "decode", "decodeLabel", "adapt", "delta", "numpoints", "first", "requiresEncode", "codePoints", "", "punycodeDigit", "getPunycodeDigit", "(I)I", "okhttp"}
)
@SourceDebugExtension({"SMAP\nPunycode.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Punycode.kt\nokhttp3/internal/idn/Punycode\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,329:1\n2393#2,14:330\n*S KotlinDebug\n*F\n+ 1 Punycode.kt\nokhttp3/internal/idn/Punycode\n*L\n108#1:330,14\n*E\n"})
public final class Punycode {
   @NotNull
   public static final Punycode INSTANCE = new Punycode();
   @NotNull
   private static final String PREFIX_STRING = "xn--";
   @NotNull
   private static final ByteString PREFIX;
   private static final int BASE = 36;
   private static final int TMIN = 1;
   private static final int TMAX = 26;
   private static final int SKEW = 38;
   private static final int DAMP = 700;
   private static final int INITIAL_BIAS = 72;
   private static final int INITIAL_N = 128;

   private Punycode() {
   }

   @NotNull
   public final String getPREFIX_STRING() {
      return PREFIX_STRING;
   }

   @NotNull
   public final ByteString getPREFIX() {
      return PREFIX;
   }

   @Nullable
   public final String encode(@NotNull String string) {
      Intrinsics.checkNotNullParameter(string, "string");
      int pos = 0;
      int limit = string.length();

      Buffer result;
      int dot;
      for(result = new Buffer(); pos < limit; pos = dot + 1) {
         dot = StringsKt.indexOf$default((CharSequence)string, '.', pos, false, 4, (Object)null);
         if (dot == -1) {
            dot = limit;
         }

         if (!this.encodeLabel(string, pos, dot, result)) {
            return null;
         }

         if (dot >= limit) {
            break;
         }

         result.writeByte(46);
      }

      return result.readUtf8();
   }

   private final boolean encodeLabel(String string, int pos, int limit, Buffer result) {
      if (!this.requiresEncode(string, pos, limit)) {
         result.writeUtf8(string, pos, limit);
         return true;
      } else {
         result.write(PREFIX);
         List input = this.codePoints(string, pos, limit);
         int b = 0;
         Iterator var7 = input.iterator();

         while(var7.hasNext()) {
            int codePoint = ((Number)var7.next()).intValue();
            if (codePoint < 128) {
               result.writeByte(codePoint);
               ++b;
            }
         }

         if (b > 0) {
            result.writeByte(45);
         }

         int n = 0;
         n = 128;
         int delta = 0;
         int bias = 72;

         for(int h = b; h < input.size(); ++n) {
            Iterable $this$minBy$iv = (Iterable)input;
            int $i$f$minByOrThrow = 0;
            Iterator iterator$iv = $this$minBy$iv.iterator();
            if (!iterator$iv.hasNext()) {
               throw new NoSuchElementException();
            }

            Object minElem$iv = iterator$iv.next();
            Object var10000;
            if (!iterator$iv.hasNext()) {
               var10000 = minElem$iv;
            } else {
               int it = ((Number)minElem$iv).intValue();
               int var17 = 0;
               it = it >= n ? it : Integer.MAX_VALUE;

               do {
                  Object e$iv = iterator$iv.next();
                  int it = ((Number)e$iv).intValue();
                  int var19 = 0;
                  it = it >= n ? it : Integer.MAX_VALUE;
                  if (it > it) {
                     minElem$iv = e$iv;
                     it = it;
                  }
               } while(iterator$iv.hasNext());

               var10000 = minElem$iv;
            }

            int m = ((Number)var10000).intValue();
            int increment = (m - n) * (h + 1);
            if (delta > Integer.MAX_VALUE - increment) {
               return false;
            }

            delta += increment;
            n = m;
            Iterator var27 = input.iterator();

            while(var27.hasNext()) {
               int c = ((Number)var27.next()).intValue();
               if (c < n) {
                  if (delta == Integer.MAX_VALUE) {
                     return false;
                  }

                  ++delta;
               } else if (c == n) {
                  int q = delta;
                  IntProgression var31 = RangesKt.step(RangesKt.until(36, Integer.MAX_VALUE), 36);
                  int k = var31.getFirst();
                  int var35 = var31.getLast();
                  int var36 = var31.getStep();
                  if (var36 > 0 && k <= var35 || var36 < 0 && var35 <= k) {
                     while(true) {
                        int t = k <= bias ? 1 : (k >= bias + 26 ? 26 : k - bias);
                        if (q < t) {
                           break;
                        }

                        result.writeByte(this.getPunycodeDigit(t + (q - t) % (36 - t)));
                        q = (q - t) / (36 - t);
                        if (k == var35) {
                           break;
                        }

                        k += var36;
                     }
                  }

                  result.writeByte(this.getPunycodeDigit(q));
                  bias = this.adapt(delta, h + 1, h == b);
                  delta = 0;
                  ++h;
               }
            }

            ++delta;
         }

         return true;
      }
   }

   @Nullable
   public final String decode(@NotNull String string) {
      Intrinsics.checkNotNullParameter(string, "string");
      int pos = 0;
      int limit = string.length();

      Buffer result;
      int dot;
      for(result = new Buffer(); pos < limit; pos = dot + 1) {
         dot = StringsKt.indexOf$default((CharSequence)string, '.', pos, false, 4, (Object)null);
         if (dot == -1) {
            dot = limit;
         }

         if (!this.decodeLabel(string, pos, dot, result)) {
            return null;
         }

         if (dot >= limit) {
            break;
         }

         result.writeByte(46);
      }

      return result.readUtf8();
   }

   private final boolean decodeLabel(String string, int pos, int limit, Buffer result) {
      if (!StringsKt.regionMatches(string, pos, PREFIX_STRING, 0, 4, true)) {
         result.writeUtf8(string, pos, limit);
         return true;
      } else {
         int pos = pos + 4;
         List codePoints = (List)(new ArrayList());
         int lastDelimiter = StringsKt.lastIndexOf$default((CharSequence)string, '-', limit, false, 4, (Object)null);
         if (lastDelimiter >= pos) {
            while(pos < lastDelimiter) {
               char codePoint = string.charAt(pos++);
               if (!('a' <= codePoint ? codePoint < '{' : false) && !('A' <= codePoint ? codePoint < '[' : false) && !('0' <= codePoint ? codePoint < ':' : false) && codePoint != '-') {
                  return false;
               }

               ((Collection)codePoints).add(codePoint);
            }

            ++pos;
         }

         int n = 128;
         int i = 0;

         for(int bias = 72; pos < limit; ++i) {
            int oldi = i;
            int w = 1;
            IntProgression var13 = RangesKt.step(RangesKt.until(36, Integer.MAX_VALUE), 36);
            int k = var13.getFirst();
            int var15 = var13.getLast();
            int var16 = var13.getStep();
            if (var16 > 0 && k <= var15 || var16 < 0 && var15 <= k) {
               while(true) {
                  if (pos == limit) {
                     return false;
                  }

                  char c = string.charAt(pos++);
                  int var10000;
                  if ('a' <= c ? c < '{' : false) {
                     var10000 = c - 97;
                  } else if ('A' <= c ? c < '[' : false) {
                     var10000 = c - 65;
                  } else {
                     if (!('0' <= c ? c < ':' : false)) {
                        return false;
                     }

                     var10000 = c - 48 + 26;
                  }

                  int digit = var10000;
                  int deltaI = digit * w;
                  if (i > Integer.MAX_VALUE - deltaI) {
                     return false;
                  }

                  i += deltaI;
                  int t = k <= bias ? 1 : (k >= bias + 26 ? 26 : k - bias);
                  if (digit < t) {
                     break;
                  }

                  int scaleW = 36 - t;
                  if (w > Integer.MAX_VALUE / scaleW) {
                     return false;
                  }

                  w *= scaleW;
                  if (k == var15) {
                     break;
                  }

                  k += var16;
               }
            }

            bias = this.adapt(i - oldi, codePoints.size() + 1, oldi == 0);
            int deltaN = i / (codePoints.size() + 1);
            if (n > Integer.MAX_VALUE - deltaN) {
               return false;
            }

            n += deltaN;
            i %= codePoints.size() + 1;
            if (n > 1114111) {
               return false;
            }

            codePoints.add(i, n);
         }

         Iterator var24 = codePoints.iterator();

         while(var24.hasNext()) {
            int codePoint = ((Number)var24.next()).intValue();
            result.writeUtf8CodePoint(codePoint);
         }

         return true;
      }
   }

   private final int adapt(int delta, int numpoints, boolean first) {
      int delta = first ? delta / 700 : delta / 2;
      delta += delta / numpoints;

      int k;
      for(k = 0; delta > 455; k += 36) {
         delta /= 35;
      }

      return k + 36 * delta / (delta + 38);
   }

   private final boolean requiresEncode(String $this$requiresEncode, int pos, int limit) {
      for(int i = pos; i < limit; ++i) {
         if ($this$requiresEncode.charAt(i) >= 128) {
            return true;
         }
      }

      return false;
   }

   private final List<Integer> codePoints(String $this$codePoints, int pos, int limit) {
      List result = (List)(new ArrayList());

      for(int i = pos; i < limit; ++i) {
         char c = $this$codePoints.charAt(i);
         Collection var7 = (Collection)result;
         int var10000;
         if (CharsKt.isSurrogate(c)) {
            char low = i + 1 < limit ? $this$codePoints.charAt(i + 1) : 0;
            if (!Character.isLowSurrogate(c) && Character.isLowSurrogate(low)) {
               ++i;
               var10000 = 65536 + ((c & 1023) << 10 | low & 1023);
            } else {
               var10000 = 63;
            }
         } else {
            var10000 = c;
         }

         Integer var9 = var10000;
         var7.add(var9);
      }

      return result;
   }

   private final int getPunycodeDigit(int $this$punycodeDigit) {
      int var10000;
      if ($this$punycodeDigit < 26) {
         var10000 = $this$punycodeDigit + 97;
      } else {
         if ($this$punycodeDigit >= 36) {
            throw new IllegalStateException(("unexpected digit: " + $this$punycodeDigit).toString());
         }

         var10000 = $this$punycodeDigit - 26 + 48;
      }

      return var10000;
   }

   static {
      Punycode var10001 = INSTANCE;
      PREFIX = ByteString.Companion.encodeUtf8(PREFIX_STRING);
   }
}
