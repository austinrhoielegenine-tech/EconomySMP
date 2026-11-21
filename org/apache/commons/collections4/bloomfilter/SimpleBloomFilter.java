package org.apache.commons.collections4.bloomfilter;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;

public final class SimpleBloomFilter implements BloomFilter<SimpleBloomFilter> {
   private final long[] bitMap;
   private final Shape shape;
   private int cardinality;

   public SimpleBloomFilter(Shape shape) {
      Objects.requireNonNull(shape, "shape");
      this.shape = shape;
      this.bitMap = BitMaps.newBitMap(shape);
      this.cardinality = 0;
   }

   private SimpleBloomFilter(SimpleBloomFilter source) {
      this.shape = source.shape;
      this.bitMap = (long[])source.bitMap.clone();
      this.cardinality = source.cardinality;
   }

   public long[] asBitMapArray() {
      return Arrays.copyOf(this.bitMap, this.bitMap.length);
   }

   public int cardinality() {
      int c = this.cardinality;
      if (c < 0) {
         this.cardinality = c = SetOperations.cardinality(this);
      }

      return c;
   }

   public int characteristics() {
      return 0;
   }

   public void clear() {
      Arrays.fill(this.bitMap, 0L);
      this.cardinality = 0;
   }

   public boolean contains(IndexExtractor indexExtractor) {
      return indexExtractor.processIndices((idx) -> BitMaps.contains(this.bitMap, idx));
   }

   public SimpleBloomFilter copy() {
      return new SimpleBloomFilter(this);
   }

   public Shape getShape() {
      return this.shape;
   }

   public boolean isEmpty() {
      return this.cardinality == 0 || this.processBitMaps((y) -> y == 0L);
   }

   public boolean merge(BitMapExtractor bitMapExtractor) {
      Objects.requireNonNull(bitMapExtractor, "bitMapExtractor");

      try {
         int[] idx = new int[1];
         bitMapExtractor.processBitMaps((value) -> {
            long[] var10000 = this.bitMap;
            int var10004 = idx[0];
            int var10001 = idx[0];
            idx[0] = var10004 + 1;
            var10000[var10001] |= value;
            return true;
         });
         int var10002 = idx[0]--;
         int idxLimit = BitMaps.getLongIndex(this.shape.getNumberOfBits());
         if (idxLimit == idx[0]) {
            long excess = this.bitMap[idxLimit] >> this.shape.getNumberOfBits();
            if (excess != 0L) {
               throw new IllegalArgumentException(String.format("BitMapExtractor set a bit higher than the limit for the shape: %s", this.shape.getNumberOfBits()));
            }
         }

         this.cardinality = -1;
         return true;
      } catch (IndexOutOfBoundsException e) {
         throw new IllegalArgumentException(String.format("BitMapExtractor should send at most %s maps", this.bitMap.length), e);
      }
   }

   public boolean merge(BloomFilter<?> other) {
      Objects.requireNonNull(other, "other");
      if ((other.characteristics() & 1) != 0) {
         this.merge((IndexExtractor)other);
      } else {
         this.merge(other);
      }

      return true;
   }

   public boolean merge(Hasher hasher) {
      Objects.requireNonNull(hasher, "hasher");
      return this.merge(hasher.indices(this.shape));
   }

   public boolean merge(IndexExtractor indexExtractor) {
      Objects.requireNonNull(indexExtractor, "indexExtractor");
      indexExtractor.processIndices((idx) -> {
         if (idx >= 0 && idx < this.shape.getNumberOfBits()) {
            BitMaps.set(this.bitMap, idx);
            return true;
         } else {
            throw new IllegalArgumentException(String.format("IndexExtractor should only send values in the range[0,%s)", this.shape.getNumberOfBits()));
         }
      });
      this.cardinality = -1;
      return true;
   }

   public boolean processBitMapPairs(BitMapExtractor other, LongBiPredicate func) {
      CountingLongPredicate p = new CountingLongPredicate(this.bitMap, func);
      return other.processBitMaps(p) && p.processRemaining();
   }

   public boolean processBitMaps(LongPredicate consumer) {
      Objects.requireNonNull(consumer, "consumer");

      for(long l : this.bitMap) {
         if (!consumer.test(l)) {
            return false;
         }
      }

      return true;
   }

   public boolean processIndices(IntPredicate consumer) {
      Objects.requireNonNull(consumer, "consumer");
      return IndexExtractor.fromBitMapExtractor(this).processIndices(consumer);
   }
}
