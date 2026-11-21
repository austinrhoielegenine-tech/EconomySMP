package org.apache.commons.collections4.bloomfilter;

import java.util.Objects;
import java.util.function.IntPredicate;

public class EnhancedDoubleHasher implements Hasher {
   private final long initial;
   private final long increment;

   private static long toLong(byte[] byteArray, int offset, int len) {
      long val = 0L;
      int shift = 64;
      int end = offset + Math.min(len, 8);

      for(int i = offset; i < end; ++i) {
         shift -= 8;
         val |= (long)(byteArray[i] & 255) << shift;
      }

      return val;
   }

   public EnhancedDoubleHasher(byte[] buffer) {
      if (buffer.length == 0) {
         throw new IllegalArgumentException("buffer length must be greater than 0");
      } else {
         int segment = buffer.length / 2;
         this.initial = toLong(buffer, 0, segment);
         this.increment = toLong(buffer, segment, buffer.length - segment);
      }
   }

   public EnhancedDoubleHasher(long initial, long increment) {
      this.initial = initial;
      this.increment = increment;
   }

   long getIncrement() {
      return this.increment;
   }

   long getInitial() {
      return this.initial;
   }

   public IndexExtractor indices(final Shape shape) {
      Objects.requireNonNull(shape, "shape");
      return new IndexExtractor() {
         public int[] asIndexArray() {
            int[] result = new int[shape.getNumberOfHashFunctions()];
            int[] idx = new int[1];
            this.processIndices((i) -> {
               int var10004 = idx[0];
               int var10001 = idx[0];
               idx[0] = var10004 + 1;
               result[var10001] = i;
               return true;
            });
            return result;
         }

         public boolean processIndices(IntPredicate consumer) {
            Objects.requireNonNull(consumer, "consumer");
            int bits = shape.getNumberOfBits();
            int index = BitMaps.mod(EnhancedDoubleHasher.this.initial, bits);
            if (!consumer.test(index)) {
               return false;
            } else {
               int inc = BitMaps.mod(EnhancedDoubleHasher.this.increment, bits);
               int k = shape.getNumberOfHashFunctions();
               if (k >= bits) {
                  int tet = 1;

                  for(int i = 1; i < k; ++i) {
                     index -= inc;
                     index = index < 0 ? index + bits : index;
                     if (!consumer.test(index)) {
                        return false;
                     }

                     inc -= tet;
                     inc = inc < 0 ? inc + bits : inc;
                     ++tet;
                     if (tet == bits) {
                        tet = 0;
                     }
                  }
               } else {
                  for(int i = 1; i < k; ++i) {
                     index -= inc;
                     index = index < 0 ? index + bits : index;
                     if (!consumer.test(index)) {
                        return false;
                     }

                     inc -= i;
                     inc = inc < 0 ? inc + bits : inc;
                  }
               }

               return true;
            }
         }
      };
   }
}
