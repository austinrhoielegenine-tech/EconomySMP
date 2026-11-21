package com.fasterxml.jackson.core.io.schubfach;

public final class FloatToDecimal {
   static final int P = 24;
   private static final int W = 8;
   static final int Q_MIN = -149;
   static final int Q_MAX = 104;
   static final int E_MIN = -44;
   static final int E_MAX = 39;
   static final int C_TINY = 8;
   static final int K_MIN = -45;
   static final int K_MAX = 31;
   static final int H = 9;
   private static final int C_MIN = 8388608;
   private static final int BQ_MASK = 255;
   private static final int T_MASK = 8388607;
   private static final long MASK_32 = 4294967295L;
   private static final int MASK_28 = 268435455;
   private static final int NON_SPECIAL = 0;
   private static final int PLUS_ZERO = 1;
   private static final int MINUS_ZERO = 2;
   private static final int PLUS_INF = 3;
   private static final int MINUS_INF = 4;
   private static final int NAN = 5;
   public final int MAX_CHARS = 15;
   private final byte[] bytes = new byte[15];
   private int index;

   private FloatToDecimal() {
   }

   public static String toString(float v) {
      return (new FloatToDecimal()).toDecimalString(v);
   }

   private String toDecimalString(float v) {
      switch (this.toDecimal(v)) {
         case 0:
            return this.charsToString();
         case 1:
            return "0.0";
         case 2:
            return "-0.0";
         case 3:
            return "Infinity";
         case 4:
            return "-Infinity";
         default:
            return "NaN";
      }
   }

   private int toDecimal(float v) {
      int bits = Float.floatToRawIntBits(v);
      int t = bits & 8388607;
      int bq = bits >>> 23 & 255;
      if (bq < 255) {
         this.index = -1;
         if (bits < 0) {
            this.append(45);
         }

         if (bq != 0) {
            int mq = 150 - bq;
            int c = 8388608 | t;
            if (0 < mq & mq < 24) {
               int f = c >> mq;
               if (f << mq == c) {
                  return this.toChars(f, 0);
               }
            }

            return this.toDecimal(-mq, c, 0);
         } else if (t != 0) {
            return t < 8 ? this.toDecimal(-149, 10 * t, -1) : this.toDecimal(-149, t, 0);
         } else {
            return bits == 0 ? 1 : 2;
         }
      } else if (t != 0) {
         return 5;
      } else {
         return bits > 0 ? 3 : 4;
      }
   }

   private int toDecimal(int q, int c, int dk) {
      int out = c & 1;
      long cb = (long)(c << 2);
      long cbr = cb + 2L;
      long cbl;
      int k;
      if (c != 8388608 | q == -149) {
         cbl = cb - 2L;
         k = MathUtils.flog10pow2(q);
      } else {
         cbl = cb - 1L;
         k = MathUtils.flog10threeQuartersPow2(q);
      }

      int h = q + MathUtils.flog2pow10(-k) + 33;
      long g = MathUtils.g1(k) + 1L;
      int vb = rop(g, cb << h);
      int vbl = rop(g, cbl << h);
      int vbr = rop(g, cbr << h);
      int s = vb >> 2;
      if (s >= 100) {
         int sp10 = 10 * (int)((long)s * 1717986919L >>> 34);
         int tp10 = sp10 + 10;
         boolean upin = vbl + out <= sp10 << 2;
         boolean wpin = (tp10 << 2) + out <= vbr;
         if (upin != wpin) {
            return this.toChars(upin ? sp10 : tp10, k);
         }
      }

      int t = s + 1;
      boolean uin = vbl + out <= s << 2;
      boolean win = (t << 2) + out <= vbr;
      if (uin != win) {
         return this.toChars(uin ? s : t, k + dk);
      } else {
         int cmp = vb - (s + t << 1);
         return this.toChars(cmp >= 0 && (cmp != 0 || (s & 1) != 0) ? t : s, k + dk);
      }
   }

   private static int rop(long g, long cp) {
      long x1 = MathUtils.multiplyHigh(g, cp);
      long vbp = x1 >>> 31;
      return (int)(vbp | (x1 & 4294967295L) + 4294967295L >>> 32);
   }

   private int toChars(int f, int e) {
      int len = MathUtils.flog10pow2(32 - Integer.numberOfLeadingZeros(f));
      if ((long)f >= MathUtils.pow10(len)) {
         ++len;
      }

      f = (int)((long)f * MathUtils.pow10(9 - len));
      e += len;
      int h = (int)((long)f * 1441151881L >>> 57);
      int l = f - 100000000 * h;
      if (0 < e && e <= 7) {
         return this.toChars1(h, l, e);
      } else {
         return -3 < e && e <= 0 ? this.toChars2(h, l, e) : this.toChars3(h, l, e);
      }
   }

   private int toChars1(int h, int l, int e) {
      this.appendDigit(h);
      int y = this.y(l);

      int i;
      for(i = 1; i < e; ++i) {
         int t = 10 * y;
         this.appendDigit(t >>> 28);
         y = t & 268435455;
      }

      this.append(46);

      while(i <= 8) {
         int t = 10 * y;
         this.appendDigit(t >>> 28);
         y = t & 268435455;
         ++i;
      }

      this.removeTrailingZeroes();
      return 0;
   }

   private int toChars2(int h, int l, int e) {
      this.appendDigit(0);
      this.append(46);

      while(e < 0) {
         this.appendDigit(0);
         ++e;
      }

      this.appendDigit(h);
      this.append8Digits(l);
      this.removeTrailingZeroes();
      return 0;
   }

   private int toChars3(int h, int l, int e) {
      this.appendDigit(h);
      this.append(46);
      this.append8Digits(l);
      this.removeTrailingZeroes();
      this.exponent(e - 1);
      return 0;
   }

   private void append8Digits(int m) {
      int y = this.y(m);

      for(int i = 0; i < 8; ++i) {
         int t = 10 * y;
         this.appendDigit(t >>> 28);
         y = t & 268435455;
      }

   }

   private void removeTrailingZeroes() {
      while(this.bytes[this.index] == 48) {
         --this.index;
      }

      if (this.bytes[this.index] == 46) {
         ++this.index;
      }

   }

   private int y(int a) {
      return (int)(MathUtils.multiplyHigh((long)(a + 1) << 28, 193428131138340668L) >>> 20) - 1;
   }

   private void exponent(int e) {
      this.append(69);
      if (e < 0) {
         this.append(45);
         e = -e;
      }

      if (e < 10) {
         this.appendDigit(e);
      } else {
         int d = e * 103 >>> 10;
         this.appendDigit(d);
         this.appendDigit(e - 10 * d);
      }
   }

   private void append(int c) {
      this.bytes[++this.index] = (byte)c;
   }

   private void appendDigit(int d) {
      this.bytes[++this.index] = (byte)(48 + d);
   }

   private String charsToString() {
      return new String(this.bytes, 0, 0, this.index + 1);
   }
}
