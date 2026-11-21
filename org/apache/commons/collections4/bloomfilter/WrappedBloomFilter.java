package org.apache.commons.collections4.bloomfilter;

import java.util.function.IntPredicate;
import java.util.function.LongPredicate;

public abstract class WrappedBloomFilter<T extends WrappedBloomFilter<T, W>, W extends BloomFilter<W>> implements BloomFilter<T> {
   private final W wrapped;

   public WrappedBloomFilter(W wrapped) {
      this.wrapped = wrapped;
   }

   public long[] asBitMapArray() {
      return this.wrapped.asBitMapArray();
   }

   public int[] asIndexArray() {
      return this.wrapped.asIndexArray();
   }

   public int cardinality() {
      return this.wrapped.cardinality();
   }

   public int characteristics() {
      return this.wrapped.characteristics();
   }

   public void clear() {
      this.wrapped.clear();
   }

   public boolean contains(BitMapExtractor bitMapExtractor) {
      return this.wrapped.contains(bitMapExtractor);
   }

   public boolean contains(BloomFilter<?> other) {
      return this.wrapped.contains(other);
   }

   public boolean contains(Hasher hasher) {
      return this.wrapped.contains(hasher);
   }

   public boolean contains(IndexExtractor indexExtractor) {
      return this.wrapped.contains(indexExtractor);
   }

   public int estimateIntersection(BloomFilter<?> other) {
      return this.wrapped.estimateIntersection(other);
   }

   public int estimateN() {
      return this.wrapped.estimateN();
   }

   public int estimateUnion(BloomFilter<?> other) {
      return this.wrapped.estimateUnion(other);
   }

   public Shape getShape() {
      return this.wrapped.getShape();
   }

   protected W getWrapped() {
      return this.wrapped;
   }

   public boolean isFull() {
      return this.wrapped.isFull();
   }

   public boolean merge(BitMapExtractor bitMapExtractor) {
      return this.wrapped.merge(bitMapExtractor);
   }

   public boolean merge(BloomFilter<?> other) {
      return this.wrapped.merge(other);
   }

   public boolean merge(Hasher hasher) {
      return this.wrapped.merge(hasher);
   }

   public boolean merge(IndexExtractor indexExtractor) {
      return this.wrapped.merge(indexExtractor);
   }

   public boolean processBitMapPairs(BitMapExtractor other, LongBiPredicate func) {
      return this.wrapped.processBitMapPairs(other, func);
   }

   public boolean processBitMaps(LongPredicate predicate) {
      return this.wrapped.processBitMaps(predicate);
   }

   public boolean processIndices(IntPredicate predicate) {
      return this.wrapped.processIndices(predicate);
   }
}
