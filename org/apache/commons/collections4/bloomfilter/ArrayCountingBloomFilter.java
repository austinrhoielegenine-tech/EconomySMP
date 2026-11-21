package org.apache.commons.collections4.bloomfilter;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.stream.IntStream;

public final class ArrayCountingBloomFilter implements CountingBloomFilter {
   private final Shape shape;
   private final int[] cells;
   private int state;

   private ArrayCountingBloomFilter(ArrayCountingBloomFilter source) {
      this.shape = source.shape;
      this.state = source.state;
      this.cells = (int[])source.cells.clone();
   }

   public ArrayCountingBloomFilter(Shape shape) {
      Objects.requireNonNull(shape, "shape");
      this.shape = shape;
      this.cells = new int[shape.getNumberOfBits()];
   }

   public boolean add(CellExtractor other) {
      Objects.requireNonNull(other, "other");
      other.processCells(this::add);
      return this.isValid();
   }

   private boolean add(int idx, int addend) {
      try {
         int updated = this.cells[idx] + addend;
         this.state |= updated;
         this.cells[idx] = updated;
         return true;
      } catch (IndexOutOfBoundsException e) {
         throw new IllegalArgumentException(String.format("Filter only accepts values in the [0,%d) range", this.getShape().getNumberOfBits()), e);
      }
   }

   public int[] asIndexArray() {
      return IntStream.range(0, this.cells.length).filter((i) -> this.cells[i] > 0).toArray();
   }

   public int cardinality() {
      return (int)IntStream.range(0, this.cells.length).filter((i) -> this.cells[i] > 0).count();
   }

   public int characteristics() {
      return 1;
   }

   public void clear() {
      Arrays.fill(this.cells, 0);
   }

   public boolean contains(BitMapExtractor bitMapExtractor) {
      return this.contains(IndexExtractor.fromBitMapExtractor(bitMapExtractor));
   }

   public boolean contains(IndexExtractor indexExtractor) {
      return indexExtractor.processIndices((idx) -> this.cells[idx] != 0);
   }

   public ArrayCountingBloomFilter copy() {
      return new ArrayCountingBloomFilter(this);
   }

   public int getMaxCell() {
      return Integer.MAX_VALUE;
   }

   public int getMaxInsert(CellExtractor cellExtractor) {
      int[] max = new int[]{Integer.MAX_VALUE};
      cellExtractor.processCells((x, y) -> {
         int count = this.cells[x] / y;
         if (count < max[0]) {
            max[0] = count;
         }

         return max[0] > 0;
      });
      return max[0];
   }

   public Shape getShape() {
      return this.shape;
   }

   public boolean isValid() {
      return this.state >= 0;
   }

   public boolean processBitMaps(LongPredicate consumer) {
      Objects.requireNonNull(consumer, "consumer");
      int blocksm1 = BitMaps.numberOfBitMaps(this.cells.length) - 1;
      int i = 0;

      for(int j = 0; j < blocksm1; ++j) {
         long value = 0L;

         for(int k = 0; k < 64; ++k) {
            if (this.cells[i++] != 0) {
               value |= BitMaps.getLongBit(k);
            }
         }

         if (!consumer.test(value)) {
            return false;
         }
      }

      long value = 0L;

      for(int k = 0; i < this.cells.length; ++k) {
         if (this.cells[i++] != 0) {
            value |= BitMaps.getLongBit(k);
         }
      }

      return consumer.test(value);
   }

   public boolean processCells(CellExtractor.CellPredicate consumer) {
      Objects.requireNonNull(consumer, "consumer");

      for(int i = 0; i < this.cells.length; ++i) {
         if (this.cells[i] != 0 && !consumer.test(i, this.cells[i])) {
            return false;
         }
      }

      return true;
   }

   public boolean processIndices(IntPredicate consumer) {
      Objects.requireNonNull(consumer, "consumer");

      for(int i = 0; i < this.cells.length; ++i) {
         if (this.cells[i] != 0 && !consumer.test(i)) {
            return false;
         }
      }

      return true;
   }

   public boolean subtract(CellExtractor other) {
      Objects.requireNonNull(other, "other");
      other.processCells(this::subtract);
      return this.isValid();
   }

   private boolean subtract(int idx, int subtrahend) {
      try {
         int updated = this.cells[idx] - subtrahend;
         this.state |= updated;
         this.cells[idx] = updated;
         return true;
      } catch (IndexOutOfBoundsException e) {
         throw new IllegalArgumentException(String.format("Filter only accepts values in the [0,%d) range", this.getShape().getNumberOfBits()), e);
      }
   }
}
