package org.apache.commons.collections4.bloomfilter;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;

@FunctionalInterface
public interface IndexExtractor {
   static IndexExtractor fromBitMapExtractor(BitMapExtractor bitMapExtractor) {
      Objects.requireNonNull(bitMapExtractor, "bitMapExtractor");
      return (consumer) -> {
         LongPredicate longPredicate = new LongPredicate() {
            int wordIdx;

            public boolean test(long word) {
               for(int i = this.wordIdx; word != 0L; ++i) {
                  if ((word & 1L) == 1L && !consumer.test(i)) {
                     return false;
                  }

                  word >>>= 1;
               }

               this.wordIdx += 64;
               return true;
            }
         };
         Objects.requireNonNull(longPredicate);
         return bitMapExtractor.processBitMaps(longPredicate::test);
      };
   }

   static IndexExtractor fromIndexArray(final int... values) {
      return new IndexExtractor() {
         public int[] asIndexArray() {
            return (int[])(([I)values).clone();
         }

         public boolean processIndices(IntPredicate predicate) {
            for(int value : values) {
               if (!predicate.test(value)) {
                  return false;
               }
            }

            return true;
         }
      };
   }

   default int[] asIndexArray() {
      final class Indices {
         private int[] data = new int[32];
         private int size;

         boolean add(int index) {
            this.data = IndexUtils.ensureCapacityForAdd(this.data, this.size);
            this.data[this.size++] = index;
            return true;
         }

         int[] toArray() {
            return this.size == this.data.length ? this.data : Arrays.copyOf(this.data, this.size);
         }
      }

      Indices indices = new Indices();
      Objects.requireNonNull(indices);
      this.processIndices(indices::add);
      return indices.toArray();
   }

   boolean processIndices(IntPredicate var1);

   default IndexExtractor uniqueIndices() {
      final BitSet bitSet = new BitSet();
      this.processIndices((i) -> {
         bitSet.set(i);
         return true;
      });
      return new IndexExtractor() {
         public boolean processIndices(IntPredicate predicate) {
            for(int idx = bitSet.nextSetBit(0); idx >= 0; idx = bitSet.nextSetBit(idx + 1)) {
               if (!predicate.test(idx)) {
                  return false;
               }
            }

            return true;
         }

         public IndexExtractor uniqueIndices() {
            return this;
         }
      };
   }
}
