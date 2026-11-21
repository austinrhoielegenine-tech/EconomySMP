package org.apache.commons.collections4.bloomfilter;

import java.util.TreeMap;
import java.util.function.IntPredicate;

@FunctionalInterface
public interface CellExtractor extends IndexExtractor {
   static CellExtractor from(final IndexExtractor indexExtractor) {
      return new CellExtractor() {
         TreeMap<null.CounterCell, null.CounterCell> counterCells = new TreeMap();

         public int[] asIndexArray() {
            this.populate();
            return this.counterCells.keySet().stream().mapToInt((c) -> c.idx).toArray();
         }

         private void populate() {
            if (this.counterCells.isEmpty()) {
               indexExtractor.processIndices((idx) -> {
                  null.CounterCell cell = new null.CounterCell(idx, 1);
                  null.CounterCell counter = (null.CounterCell)this.counterCells.get(cell);
                  if (counter == null) {
                     this.counterCells.put(cell, cell);
                  } else {
                     ++counter.count;
                  }

                  return true;
               });
            }

         }

         public boolean processCells(CellPredicate consumer) {
            this.populate();

            for(null.CounterCell cell : this.counterCells.values()) {
               if (!consumer.test(cell.idx, cell.count)) {
                  return false;
               }
            }

            return true;
         }

         final class CounterCell implements Comparable<null.CounterCell> {
            final int idx;
            int count;

            CounterCell(final int idx, final int count) {
               this.idx = idx;
               this.count = count;
            }

            public int compareTo(null.CounterCell other) {
               return Integer.compare(this.idx, other.idx);
            }
         }
      };
   }

   boolean processCells(CellPredicate var1);

   default boolean processIndices(IntPredicate predicate) {
      return this.processCells((i, v) -> predicate.test(i));
   }

   default IndexExtractor uniqueIndices() {
      return this;
   }

   @FunctionalInterface
   public interface CellPredicate {
      boolean test(int var1, int var2);
   }
}
