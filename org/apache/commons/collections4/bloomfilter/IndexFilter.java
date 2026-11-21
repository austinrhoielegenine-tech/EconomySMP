package org.apache.commons.collections4.bloomfilter;

import java.util.function.IntPredicate;

public final class IndexFilter {
   private final IntPredicate tracker;
   private final int size;
   private final IntPredicate consumer;

   public static IntPredicate create(Shape shape, IntPredicate consumer) {
      IndexFilter var10000 = new IndexFilter(shape, consumer);
      return var10000::test;
   }

   private IndexFilter(Shape shape, IntPredicate consumer) {
      this.size = shape.getNumberOfBits();
      this.consumer = consumer;
      if ((long)(BitMaps.numberOfBitMaps(shape) * 8) < (long)shape.getNumberOfHashFunctions() * 4L) {
         this.tracker = new BitMapTracker(shape);
      } else {
         this.tracker = new ArrayTracker(shape);
      }

   }

   public boolean test(int number) {
      if (number >= this.size) {
         throw new IndexOutOfBoundsException(String.format("number too large %d >= %d", number, this.size));
      } else {
         return this.tracker.test(number) ? this.consumer.test(number) : true;
      }
   }

   static class ArrayTracker implements IntPredicate {
      private final int[] seen;
      private int populated;

      ArrayTracker(Shape shape) {
         this.seen = new int[shape.getNumberOfHashFunctions()];
      }

      public boolean test(int number) {
         if (number < 0) {
            throw new IndexOutOfBoundsException("number may not be less than zero. " + number);
         } else {
            for(int i = 0; i < this.populated; ++i) {
               if (this.seen[i] == number) {
                  return false;
               }
            }

            this.seen[this.populated++] = number;
            return true;
         }
      }
   }

   static class BitMapTracker implements IntPredicate {
      private final long[] bits;

      BitMapTracker(Shape shape) {
         this.bits = BitMaps.newBitMap(shape);
      }

      public boolean test(int number) {
         boolean retval = !BitMaps.contains(this.bits, number);
         BitMaps.set(this.bits, number);
         return retval;
      }
   }
}
