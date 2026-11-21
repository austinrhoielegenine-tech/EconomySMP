package org.apache.commons.collections4.bloomfilter;

import [Lorg.apache.commons.collections4.bloomfilter.BloomFilter;;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@FunctionalInterface
public interface BloomFilterExtractor {
   static <T extends BloomFilter<T>> BloomFilterExtractor fromBloomFilterArray(final BloomFilter<?>... filters) {
      Objects.requireNonNull(filters, "filters");
      return new BloomFilterExtractor() {
         public BloomFilter[] asBloomFilterArray() {
            return (BloomFilter[])((BloomFilter;)filters).clone();
         }

         public boolean processBloomFilterPair(BloomFilterExtractor other, BiPredicate<BloomFilter, BloomFilter> func) {
            CountingPredicate<BloomFilter> p = new CountingPredicate<BloomFilter>(filters, func);
            return other.processBloomFilters(p) && p.processRemaining();
         }

         public boolean processBloomFilters(Predicate<BloomFilter> predicate) {
            return Arrays.stream(filters).allMatch(predicate);
         }
      };
   }

   default BloomFilter[] asBloomFilterArray() {
      List<BloomFilter> filters = new ArrayList();
      this.processBloomFilters((f) -> filters.add(f.copy()));
      return (BloomFilter[])filters.toArray(new BloomFilter[0]);
   }

   default BloomFilter flatten() {
      AtomicReference<BloomFilter> ref = new AtomicReference();
      this.processBloomFilters((x) -> {
         if (ref.get() == null) {
            ref.set(new SimpleBloomFilter(x.getShape()));
         }

         return ((BloomFilter)ref.get()).merge(x);
      });
      return (BloomFilter)Objects.requireNonNull((BloomFilter)ref.get(), "No filters.");
   }

   default boolean processBloomFilterPair(BloomFilterExtractor other, BiPredicate<BloomFilter, BloomFilter> func) {
      CountingPredicate<BloomFilter> p = new CountingPredicate<BloomFilter>(this.asBloomFilterArray(), func);
      return other.processBloomFilters(p) && p.processRemaining();
   }

   boolean processBloomFilters(Predicate<BloomFilter> var1);
}
