package org.apache.commons.collections4.bloomfilter;

import java.util.Iterator;
import java.util.Objects;
import java.util.TreeSet;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.stream.Stream;

public final class SparseBloomFilter implements BloomFilter<SparseBloomFilter> {
   private final TreeSet<Integer> indices;
   private final Shape shape;

   public SparseBloomFilter(Shape shape) {
      Objects.requireNonNull(shape, "shape");
      this.shape = shape;
      this.indices = new TreeSet();
   }

   private SparseBloomFilter(SparseBloomFilter source) {
      this.shape = source.shape;
      this.indices = new TreeSet(source.indices);
   }

   private boolean add(int idx) {
      this.indices.add(idx);
      return true;
   }

   public long[] asBitMapArray() {
      long[] result = BitMaps.newBitMap(this.shape);

      for(int i : this.indices) {
         BitMaps.set(result, i);
      }

      return result;
   }

   public int cardinality() {
      return this.indices.size();
   }

   public int characteristics() {
      return 1;
   }

   public void clear() {
      this.indices.clear();
   }

   public boolean contains(BitMapExtractor bitMapExtractor) {
      return this.contains(IndexExtractor.fromBitMapExtractor(bitMapExtractor));
   }

   public boolean contains(IndexExtractor indexExtractor) {
      TreeSet var10001 = this.indices;
      Objects.requireNonNull(var10001);
      return indexExtractor.processIndices(var10001::contains);
   }

   public SparseBloomFilter copy() {
      return new SparseBloomFilter(this);
   }

   public Shape getShape() {
      return this.shape;
   }

   public boolean isEmpty() {
      return this.indices.isEmpty();
   }

   public boolean merge(BitMapExtractor bitMapExtractor) {
      Objects.requireNonNull(bitMapExtractor, "bitMapExtractor");
      return this.merge(IndexExtractor.fromBitMapExtractor(bitMapExtractor));
   }

   public boolean merge(BloomFilter<?> other) {
      Objects.requireNonNull(other, "other");
      IndexExtractor indexExtractor = (IndexExtractor)((other.characteristics() & 1) != 0 ? other : IndexExtractor.fromBitMapExtractor(other));
      this.merge(indexExtractor);
      return true;
   }

   public boolean merge(Hasher hasher) {
      Objects.requireNonNull(hasher, "hasher");
      this.merge(hasher.indices(this.shape));
      return true;
   }

   public boolean merge(IndexExtractor indexExtractor) {
      Objects.requireNonNull(indexExtractor, "indexExtractor");
      indexExtractor.processIndices(this::add);
      if (!this.indices.isEmpty()) {
         if ((Integer)this.indices.last() >= this.shape.getNumberOfBits()) {
            throw new IllegalArgumentException(String.format("Value in list %s is greater than maximum value (%s)", this.indices.last(), this.shape.getNumberOfBits() - 1));
         }

         if ((Integer)this.indices.first() < 0) {
            throw new IllegalArgumentException(String.format("Value in list %s is less than 0", this.indices.first()));
         }
      }

      return true;
   }

   public boolean processBitMaps(LongPredicate consumer) {
      Objects.requireNonNull(consumer, "consumer");
      int limit = BitMaps.numberOfBitMaps(this.shape);
      long bitMap = 0L;
      int idx = 0;

      int i;
      for(Iterator var6 = this.indices.iterator(); var6.hasNext(); bitMap |= BitMaps.getLongBit(i)) {
         for(i = (Integer)var6.next(); BitMaps.getLongIndex(i) != idx; ++idx) {
            if (!consumer.test(bitMap)) {
               return false;
            }

            bitMap = 0L;
         }
      }

      if (!consumer.test(bitMap)) {
         return false;
      } else {
         ++idx;

         while(idx < limit) {
            if (!consumer.test(0L)) {
               return false;
            }

            ++idx;
         }

         return true;
      }
   }

   public boolean processIndices(IntPredicate consumer) {
      Objects.requireNonNull(consumer, "consumer");
      Stream var10000 = this.indices.stream();
      Objects.requireNonNull(consumer);
      return var10000.allMatch(consumer::test);
   }
}
