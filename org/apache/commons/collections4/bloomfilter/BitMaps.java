package org.apache.commons.collections4.bloomfilter;

public class BitMaps {
   private static final int DIVIDE_BY_64 = 6;

   public static boolean contains(long[] bitMaps, int bitIndex) {
      return (bitMaps[getLongIndex(bitIndex)] & getLongBit(bitIndex)) != 0L;
   }

   public static long getLongBit(int bitIndex) {
      return 1L << bitIndex;
   }

   public static int getLongIndex(int bitIndex) {
      return bitIndex >> 6;
   }

   public static int mod(long dividend, int divisor) {
      long quotient = (dividend >>> 1) / (long)divisor << 1;
      long remainder = dividend - quotient * (long)divisor;
      return (int)(remainder >= (long)divisor ? remainder - (long)divisor : remainder);
   }

   static long[] newBitMap(int numberOfBits) {
      return new long[numberOfBitMaps(numberOfBits)];
   }

   static long[] newBitMap(Shape shape) {
      return newBitMap(shape.getNumberOfBits());
   }

   public static int numberOfBitMaps(int numberOfBits) {
      return (numberOfBits - 1 >> 6) + 1;
   }

   static int numberOfBitMaps(Shape shape) {
      return numberOfBitMaps(shape.getNumberOfBits());
   }

   public static void set(long[] bitMaps, int bitIndex) {
      int var10001 = getLongIndex(bitIndex);
      bitMaps[var10001] |= getLongBit(bitIndex);
   }

   private BitMaps() {
   }
}
