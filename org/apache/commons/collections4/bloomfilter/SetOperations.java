package org.apache.commons.collections4.bloomfilter;

import java.util.function.LongBinaryOperator;

public final class SetOperations {
   public static int andCardinality(BitMapExtractor first, BitMapExtractor second) {
      return cardinality(first, second, (x, y) -> x & y);
   }

   public static int cardinality(BitMapExtractor bitMapExtractor) {
      int[] cardinality = new int[1];
      bitMapExtractor.processBitMaps((l) -> {
         cardinality[0] += Long.bitCount(l);
         return true;
      });
      return cardinality[0];
   }

   private static int cardinality(BitMapExtractor first, BitMapExtractor second, LongBinaryOperator op) {
      int[] cardinality = new int[1];
      first.processBitMapPairs(second, (x, y) -> {
         cardinality[0] += Long.bitCount(op.applyAsLong(x, y));
         return true;
      });
      return cardinality[0];
   }

   public static double cosineDistance(BitMapExtractor first, BitMapExtractor second) {
      return (double)1.0F - cosineSimilarity(first, second);
   }

   public static double cosineSimilarity(BitMapExtractor first, BitMapExtractor second) {
      int numerator = andCardinality(first, second);
      return numerator == 0 ? (double)0.0F : (double)numerator / Math.sqrt((double)(cardinality(first) * cardinality(second)));
   }

   public static double cosineSimilarity(BloomFilter<?> first, BloomFilter<?> second) {
      int numerator = andCardinality(first, second);
      return numerator == 0 ? (double)0.0F : (double)numerator / Math.sqrt((double)(first.cardinality() * second.cardinality()));
   }

   public static int hammingDistance(BitMapExtractor first, BitMapExtractor second) {
      return xorCardinality(first, second);
   }

   public static double jaccardDistance(BitMapExtractor first, BitMapExtractor second) {
      return (double)1.0F - jaccardSimilarity(first, second);
   }

   public static double jaccardSimilarity(BitMapExtractor first, BitMapExtractor second) {
      int[] cardinality = new int[2];
      first.processBitMapPairs(second, (x, y) -> {
         cardinality[0] += Long.bitCount(x & y);
         cardinality[1] += Long.bitCount(x | y);
         return true;
      });
      int intersection = cardinality[0];
      return intersection == 0 ? (double)0.0F : (double)intersection / (double)cardinality[1];
   }

   public static int orCardinality(BitMapExtractor first, BitMapExtractor second) {
      return cardinality(first, second, (x, y) -> x | y);
   }

   public static int xorCardinality(BitMapExtractor first, BitMapExtractor second) {
      return cardinality(first, second, (x, y) -> x ^ y);
   }

   private SetOperations() {
   }
}
