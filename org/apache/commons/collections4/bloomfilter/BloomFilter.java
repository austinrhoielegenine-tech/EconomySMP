package org.apache.commons.collections4.bloomfilter;

import java.util.Objects;

public interface BloomFilter<T extends BloomFilter<T>> extends IndexExtractor, BitMapExtractor {
   int SPARSE = 1;

   int cardinality();

   int characteristics();

   void clear();

   default boolean contains(BitMapExtractor bitMapExtractor) {
      return this.processBitMapPairs(bitMapExtractor, (x, y) -> (x & y) == y);
   }

   default boolean contains(BloomFilter<?> other) {
      Objects.requireNonNull(other, "other");
      return (this.characteristics() & 1) != 0 ? this.contains((IndexExtractor)other) : this.contains(other);
   }

   default boolean contains(Hasher hasher) {
      Objects.requireNonNull(hasher, "Hasher");
      Shape shape = this.getShape();
      return this.contains(hasher.indices(shape));
   }

   boolean contains(IndexExtractor var1);

   T copy();

   default int estimateIntersection(BloomFilter<?> other) {
      Objects.requireNonNull(other, "other");
      double eThis = this.getShape().estimateN(this.cardinality());
      double eOther = this.getShape().estimateN(other.cardinality());
      if (Double.isInfinite(eThis) && Double.isInfinite(eOther)) {
         return Integer.MAX_VALUE;
      } else {
         long estimate;
         if (Double.isInfinite(eThis)) {
            estimate = Math.round(eOther);
         } else if (Double.isInfinite(eOther)) {
            estimate = Math.round(eThis);
         } else {
            T union = this.copy();
            union.merge(other);
            double eUnion = this.getShape().estimateN(union.cardinality());
            if (Double.isInfinite(eUnion)) {
               throw new IllegalArgumentException("The estimated N for the union of the filters is infinite");
            }

            estimate = Math.round(eThis + eOther - eUnion);
            estimate = estimate < 0L ? 0L : estimate;
         }

         return estimate > 2147483647L ? Integer.MAX_VALUE : (int)estimate;
      }
   }

   default int estimateN() {
      double d = this.getShape().estimateN(this.cardinality());
      if (Double.isInfinite(d)) {
         return Integer.MAX_VALUE;
      } else if (Double.isNaN(d)) {
         throw new IllegalArgumentException("Cardinality too large: " + this.cardinality());
      } else {
         long l = Math.round(d);
         return l > 2147483647L ? Integer.MAX_VALUE : (int)l;
      }
   }

   default int estimateUnion(BloomFilter<?> other) {
      Objects.requireNonNull(other, "other");
      T copy = this.copy();
      copy.merge(other);
      return copy.estimateN();
   }

   Shape getShape();

   default boolean isEmpty() {
      return this.processBitMaps((y) -> y == 0L);
   }

   default boolean isFull() {
      return this.cardinality() == this.getShape().getNumberOfBits();
   }

   boolean merge(BitMapExtractor var1);

   default boolean merge(BloomFilter<?> other) {
      return (this.characteristics() & 1) != 0 ? this.merge((IndexExtractor)other) : this.merge(other);
   }

   default boolean merge(Hasher hasher) {
      Objects.requireNonNull(hasher, "hasher");
      return this.merge(hasher.indices(this.getShape()));
   }

   boolean merge(IndexExtractor var1);

   default IndexExtractor uniqueIndices() {
      return this;
   }
}
