package org.apache.commons.collections4.bloomfilter;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.LongPredicate;

@FunctionalInterface
public interface BitMapExtractor {
   static BitMapExtractor fromBitMapArray(final long... bitMaps) {
      return new BitMapExtractor() {
         public long[] asBitMapArray() {
            return Arrays.copyOf(bitMaps, bitMaps.length);
         }

         public boolean processBitMapPairs(BitMapExtractor other, LongBiPredicate func) {
            CountingLongPredicate p = new CountingLongPredicate(bitMaps, func);
            return other.processBitMaps(p) && p.processRemaining();
         }

         public boolean processBitMaps(LongPredicate predicate) {
            for(long word : bitMaps) {
               if (!predicate.test(word)) {
                  return false;
               }
            }

            return true;
         }
      };
   }

   static BitMapExtractor fromIndexExtractor(IndexExtractor extractor, int numberOfBits) {
      Objects.requireNonNull(extractor, "extractor");
      long[] result = BitMaps.newBitMap(numberOfBits);
      extractor.processIndices((i) -> {
         BitMaps.set(result, i);
         return true;
      });
      return fromBitMapArray(result);
   }

   default long[] asBitMapArray() {
      final class Bits {
         private long[] data = new long[16];
         private int size;

         boolean add(long bits) {
            if (this.size == this.data.length) {
               this.data = Arrays.copyOf(this.data, this.size * 2);
            }

            this.data[this.size++] = bits;
            return true;
         }

         long[] toArray() {
            return this.size == this.data.length ? this.data : Arrays.copyOf(this.data, this.size);
         }
      }

      Bits bits = new Bits();
      Objects.requireNonNull(bits);
      this.processBitMaps(bits::add);
      return bits.toArray();
   }

   default boolean processBitMapPairs(BitMapExtractor other, LongBiPredicate func) {
      CountingLongPredicate p = new CountingLongPredicate(this.asBitMapArray(), func);
      return other.processBitMaps(p) && p.processRemaining();
   }

   boolean processBitMaps(LongPredicate var1);
}
