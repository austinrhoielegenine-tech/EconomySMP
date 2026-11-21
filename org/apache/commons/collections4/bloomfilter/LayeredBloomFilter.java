package org.apache.commons.collections4.bloomfilter;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

public class LayeredBloomFilter<T extends BloomFilter<T>> implements BloomFilter<LayeredBloomFilter<T>>, BloomFilterExtractor {
   private final Shape shape;
   private final LayerManager<T> layerManager;

   public LayeredBloomFilter(Shape shape, LayerManager<T> layerManager) {
      this.shape = shape;
      this.layerManager = layerManager;
   }

   public int cardinality() {
      return SetOperations.cardinality(this);
   }

   public int characteristics() {
      return 0;
   }

   public void cleanup() {
      this.layerManager.cleanup();
   }

   public final void clear() {
      this.layerManager.clear();
   }

   public boolean contains(BitMapExtractor bitMapExtractor) {
      return this.contains((BloomFilter)this.createFilter(bitMapExtractor));
   }

   public boolean contains(BloomFilter other) {
      return other instanceof BloomFilterExtractor ? this.contains((BloomFilterExtractor)other) : !this.processBloomFilters((x) -> !x.contains(other));
   }

   public boolean contains(BloomFilterExtractor bloomFilterExtractor) {
      boolean[] result = new boolean[]{true};
      return bloomFilterExtractor.processBloomFilters((x) -> {
         result[0] &= this.contains(x);
         return result[0];
      });
   }

   public boolean contains(Hasher hasher) {
      return this.contains((BloomFilter)this.createFilter(hasher));
   }

   public boolean contains(IndexExtractor indexExtractor) {
      return this.contains((BloomFilter)this.createFilter(indexExtractor));
   }

   public LayeredBloomFilter<T> copy() {
      return new LayeredBloomFilter<T>(this.shape, this.layerManager.copy());
   }

   private SimpleBloomFilter createFilter(BitMapExtractor bitMapExtractor) {
      SimpleBloomFilter bf = new SimpleBloomFilter(this.shape);
      bf.merge(bitMapExtractor);
      return bf;
   }

   private SimpleBloomFilter createFilter(Hasher hasher) {
      SimpleBloomFilter bf = new SimpleBloomFilter(this.shape);
      bf.merge(hasher);
      return bf;
   }

   private SimpleBloomFilter createFilter(IndexExtractor indexExtractor) {
      SimpleBloomFilter bf = new SimpleBloomFilter(this.shape);
      bf.merge(indexExtractor);
      return bf;
   }

   public int estimateN() {
      return this.flatten().estimateN();
   }

   public int estimateUnion(BloomFilter other) {
      Objects.requireNonNull(other, "other");
      BloomFilter cpy = this.flatten();
      cpy.merge(other);
      return cpy.estimateN();
   }

   public int[] find(BitMapExtractor bitMapExtractor) {
      SimpleBloomFilter bf = new SimpleBloomFilter(this.shape);
      bf.merge(bitMapExtractor);
      return this.find((BloomFilter)bf);
   }

   public int[] find(BloomFilter bf) {
      LayeredBloomFilter<T>.Finder finder = new Finder(bf);
      this.processBloomFilters(finder);
      return finder.getResult();
   }

   public int[] find(Hasher hasher) {
      SimpleBloomFilter bf = new SimpleBloomFilter(this.shape);
      bf.merge(hasher);
      return this.find((BloomFilter)bf);
   }

   public int[] find(IndexExtractor indexExtractor) {
      SimpleBloomFilter bf = new SimpleBloomFilter(this.shape);
      bf.merge(indexExtractor);
      return this.find((BloomFilter)bf);
   }

   public SimpleBloomFilter flatten() {
      SimpleBloomFilter bf = new SimpleBloomFilter(this.shape);
      Objects.requireNonNull(bf);
      this.processBloomFilters(bf::merge);
      return bf;
   }

   public T get(int depth) {
      return this.layerManager.get(depth);
   }

   public final int getDepth() {
      return this.layerManager.getDepth();
   }

   public final Shape getShape() {
      return this.shape;
   }

   public boolean isEmpty() {
      return this.processBloomFilters(BloomFilter::isEmpty);
   }

   public boolean merge(BitMapExtractor bitMapExtractor) {
      return this.layerManager.getTarget().merge(bitMapExtractor);
   }

   public boolean merge(BloomFilter bf) {
      return this.layerManager.getTarget().merge(bf);
   }

   public boolean merge(IndexExtractor indexExtractor) {
      return this.layerManager.getTarget().merge(indexExtractor);
   }

   public void next() {
      this.layerManager.next();
   }

   public boolean processBitMaps(LongPredicate predicate) {
      return this.flatten().processBitMaps(predicate);
   }

   public final boolean processBloomFilters(Predicate<BloomFilter> bloomFilterPredicate) {
      return this.layerManager.processBloomFilters(bloomFilterPredicate);
   }

   public boolean processIndices(IntPredicate predicate) {
      return this.processBloomFilters((bf) -> bf.processIndices(predicate));
   }

   private class Finder implements Predicate<BloomFilter> {
      int[] result;
      int bfIdx;
      int resultIdx;
      BloomFilter<?> bf;

      Finder(final BloomFilter<?> bf) {
         this.result = new int[LayeredBloomFilter.this.layerManager.getDepth()];
         this.bf = bf;
      }

      int[] getResult() {
         return Arrays.copyOf(this.result, this.resultIdx);
      }

      public boolean test(BloomFilter x) {
         if (x.contains(this.bf)) {
            this.result[this.resultIdx++] = this.bfIdx;
         }

         ++this.bfIdx;
         return true;
      }
   }
}
