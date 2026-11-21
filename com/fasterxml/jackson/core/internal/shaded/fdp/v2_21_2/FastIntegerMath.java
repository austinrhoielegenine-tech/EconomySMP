package com.fasterxml.jackson.core.internal.shaded.fdp.v2_21_2;

import java.math.BigInteger;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

final class FastIntegerMath {
   public static final BigInteger FIVE = BigInteger.valueOf(5L);
   static final BigInteger TEN_POW_16 = BigInteger.valueOf(10000000000000000L);
   static final BigInteger FIVE_POW_16 = BigInteger.valueOf(152587890625L);
   private static final BigInteger[] SMALL_POWERS_OF_TEN;

   private FastIntegerMath() {
   }

   static BigInteger computePowerOfTen(NavigableMap<Integer, BigInteger> powersOfTen, int n) {
      if (n < SMALL_POWERS_OF_TEN.length) {
         return SMALL_POWERS_OF_TEN[n];
      } else if (powersOfTen != null) {
         Map.Entry<Integer, BigInteger> floorEntry = powersOfTen.floorEntry(n);
         Integer floorN = (Integer)floorEntry.getKey();
         return floorN == n ? (BigInteger)floorEntry.getValue() : FftMultiplier.multiply((BigInteger)floorEntry.getValue(), computePowerOfTen(powersOfTen, n - floorN));
      } else {
         return FIVE.pow(n).shiftLeft(n);
      }
   }

   static BigInteger computeTenRaisedByNFloor16Recursive(NavigableMap<Integer, BigInteger> powersOfTen, int n) {
      n &= -16;
      Map.Entry<Integer, BigInteger> floorEntry = powersOfTen.floorEntry(n);
      int floorPower = (Integer)floorEntry.getKey();
      BigInteger floorValue = (BigInteger)floorEntry.getValue();
      if (floorPower == n) {
         return floorValue;
      } else {
         int diff = n - floorPower;
         BigInteger diffValue = (BigInteger)powersOfTen.get(diff);
         if (diffValue == null) {
            diffValue = computeTenRaisedByNFloor16Recursive(powersOfTen, diff);
            powersOfTen.put(diff, diffValue);
         }

         return FftMultiplier.multiply(floorValue, diffValue);
      }
   }

   static NavigableMap<Integer, BigInteger> createPowersOfTenFloor16Map() {
      NavigableMap<Integer, BigInteger> powersOfTen = new TreeMap();
      powersOfTen.put(0, BigInteger.ONE);
      powersOfTen.put(16, TEN_POW_16);
      return powersOfTen;
   }

   public static long estimateNumBits(long numDecimalDigits) {
      return (numDecimalDigits * 3402L >>> 10) + 1L;
   }

   static NavigableMap<Integer, BigInteger> fillPowersOf10Floor16(int from, int to) {
      NavigableMap<Integer, BigInteger> powers = new TreeMap();
      powers.put(0, BigInteger.valueOf(5L));
      powers.put(16, FIVE_POW_16);
      fillPowersOfNFloor16Recursive(powers, from, to);

      for(Map.Entry<Integer, BigInteger> e : powers.entrySet()) {
         e.setValue(((BigInteger)e.getValue()).shiftLeft((Integer)e.getKey()));
      }

      return powers;
   }

   static void fillPowersOfNFloor16Recursive(NavigableMap<Integer, BigInteger> powersOfTen, int from, int to) {
      int numDigits = to - from;
      if (numDigits > 18) {
         int mid = splitFloor16(from, to);
         int n = to - mid;
         if (!powersOfTen.containsKey(n)) {
            fillPowersOfNFloor16Recursive(powersOfTen, from, mid);
            fillPowersOfNFloor16Recursive(powersOfTen, mid, to);
            powersOfTen.put(n, computeTenRaisedByNFloor16Recursive(powersOfTen, n));
         }

      }
   }

   static long unsignedMultiplyHigh(long x, long y) {
      long x0 = x & 4294967295L;
      long x1 = x >>> 32;
      long y0 = y & 4294967295L;
      long y1 = y >>> 32;
      long p11 = x1 * y1;
      long p01 = x0 * y1;
      long p10 = x1 * y0;
      long p00 = x0 * y0;
      long middle = p10 + (p00 >>> 32) + (p01 & 4294967295L);
      return p11 + (middle >>> 32) + (p01 >>> 32);
   }

   static int splitFloor16(int from, int to) {
      int range = to - from + 31 >>> 5 << 4;
      return to - range;
   }

   static {
      SMALL_POWERS_OF_TEN = new BigInteger[]{BigInteger.ONE, BigInteger.TEN, BigInteger.valueOf(100L), BigInteger.valueOf(1000L), BigInteger.valueOf(10000L), BigInteger.valueOf(100000L), BigInteger.valueOf(1000000L), BigInteger.valueOf(10000000L), BigInteger.valueOf(100000000L), BigInteger.valueOf(1000000000L), BigInteger.valueOf(10000000000L), BigInteger.valueOf(100000000000L), BigInteger.valueOf(1000000000000L), BigInteger.valueOf(10000000000000L), BigInteger.valueOf(100000000000000L), BigInteger.valueOf(1000000000000000L)};
   }
}
