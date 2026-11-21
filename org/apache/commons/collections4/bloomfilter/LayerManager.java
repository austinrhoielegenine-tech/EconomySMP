package org.apache.commons.collections4.bloomfilter;

import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LayerManager<T extends BloomFilter<T>> implements BloomFilterExtractor {
   private final LinkedList<T> filters;
   private final Consumer<Deque<T>> filterCleanup;
   private final Predicate<LayerManager<T>> extendCheck;
   private final Supplier<T> filterSupplier;

   public static <T extends BloomFilter<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   private LayerManager(Supplier<T> filterSupplier, Predicate<LayerManager<T>> extendCheck, Consumer<Deque<T>> filterCleanup, boolean initialize) {
      this.filters = new LinkedList();
      this.filterSupplier = (Supplier)Objects.requireNonNull(filterSupplier, "filterSupplier");
      this.extendCheck = (Predicate)Objects.requireNonNull(extendCheck, "extendCheck");
      this.filterCleanup = (Consumer)Objects.requireNonNull(filterCleanup, "filterCleanup");
      if (initialize) {
         this.addFilter();
      }

   }

   private void addFilter() {
      this.filters.add((BloomFilter)Objects.requireNonNull((BloomFilter)this.filterSupplier.get(), "filterSupplier.get() returned null."));
   }

   void cleanup() {
      this.filterCleanup.accept(this.filters);
      if (this.filters.isEmpty()) {
         this.addFilter();
      }

   }

   public final void clear() {
      this.filters.clear();
      this.addFilter();
   }

   public LayerManager<T> copy() {
      LayerManager<T> newMgr = new LayerManager<T>(this.filterSupplier, this.extendCheck, this.filterCleanup, false);

      for(T bf : this.filters) {
         newMgr.filters.add(bf.copy());
      }

      return newMgr;
   }

   public final T first() {
      return (T)(this.filters.getFirst());
   }

   public final T get(int depth) {
      if (depth >= 0 && depth < this.filters.size()) {
         return (T)(this.filters.get(depth));
      } else {
         throw new NoSuchElementException(String.format("Depth must be in the range [0,%s)", this.filters.size()));
      }
   }

   public final int getDepth() {
      return this.filters.size();
   }

   public final T getTarget() {
      if (this.extendCheck.test(this)) {
         this.next();
      }

      return (T)this.last();
   }

   public final T last() {
      return (T)(this.filters.getLast());
   }

   void next() {
      this.filterCleanup.accept(this.filters);
      this.addFilter();
   }

   public boolean processBloomFilters(Predicate<BloomFilter> bloomFilterPredicate) {
      return this.filters.stream().allMatch(bloomFilterPredicate);
   }

   public static class Builder<T extends BloomFilter<T>> implements Supplier<LayerManager<T>> {
      private Predicate<LayerManager<T>> extendCheck;
      private Supplier<T> supplier;
      private Consumer<Deque<T>> cleanup;

      private Builder() {
         this.extendCheck = LayerManager.ExtendCheck.neverAdvance();
         this.cleanup = LayerManager.Cleanup.noCleanup();
      }

      public LayerManager<T> get() {
         return new LayerManager<T>(this.supplier, this.extendCheck, this.cleanup, true);
      }

      public Builder<T> setCleanup(Consumer<Deque<T>> cleanup) {
         this.cleanup = cleanup;
         return this;
      }

      public Builder<T> setExtendCheck(Predicate<LayerManager<T>> extendCheck) {
         this.extendCheck = extendCheck;
         return this;
      }

      public Builder<T> setSupplier(Supplier<T> supplier) {
         this.supplier = supplier;
         return this;
      }
   }

   public static final class Cleanup {
      public static <T extends BloomFilter<T>> Consumer<Deque<T>> noCleanup() {
         return (x) -> {
         };
      }

      public static <T extends BloomFilter<T>> Consumer<Deque<T>> onMaxSize(int maxSize) {
         if (maxSize <= 0) {
            throw new IllegalArgumentException("'maxSize' must be greater than 0");
         } else {
            return (ll) -> {
               while(ll.size() > maxSize) {
                  ll.removeFirst();
               }

            };
         }
      }

      public static <T extends BloomFilter<T>> Consumer<Deque<T>> removeEmptyTarget() {
         return (x) -> {
            if (!x.isEmpty() && ((BloomFilter)x.getLast()).isEmpty()) {
               x.removeLast();
            }

         };
      }

      public static <T extends BloomFilter<T>> Consumer<Deque<T>> removeIf(Predicate<? super T> test) {
         return (x) -> x.removeIf(test);
      }

      private Cleanup() {
      }
   }

   public static final class ExtendCheck {
      public static <T extends BloomFilter<T>> Predicate<LayerManager<T>> advanceOnCount(final int breakAt) {
         if (breakAt <= 0) {
            throw new IllegalArgumentException("'breakAt' must be greater than 0");
         } else {
            return new Predicate<LayerManager<T>>() {
               int count;

               public boolean test(LayerManager<T> filter) {
                  if (++this.count == breakAt) {
                     this.count = 0;
                     return true;
                  } else {
                     return false;
                  }
               }
            };
         }
      }

      public static <T extends BloomFilter<T>> Predicate<LayerManager<T>> advanceOnPopulated() {
         return (lm) -> !lm.last().isEmpty();
      }

      public static <T extends BloomFilter<T>> Predicate<LayerManager<T>> advanceOnSaturation(double maxN) {
         if (maxN <= (double)0.0F) {
            throw new IllegalArgumentException("'maxN' must be greater than 0");
         } else {
            return (manager) -> {
               T bf = (T)manager.last();
               return maxN <= bf.getShape().estimateN(bf.cardinality());
            };
         }
      }

      public static <T extends BloomFilter<T>> Predicate<LayerManager<T>> neverAdvance() {
         return (x) -> false;
      }

      private ExtendCheck() {
      }
   }
}
