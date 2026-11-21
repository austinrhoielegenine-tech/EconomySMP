package org.apache.commons.collections4.bloomfilter;

import java.util.Objects;

public interface CountingBloomFilter extends BloomFilter<CountingBloomFilter>, CellExtractor {
   boolean add(CellExtractor var1);

   int getMaxCell();

   default int getMaxInsert(BitMapExtractor bitMapExtractor) {
      if (!this.contains(bitMapExtractor)) {
         return 0;
      } else {
         long[] bitMaps = bitMapExtractor.asBitMapArray();
         int[] max = new int[]{Integer.MAX_VALUE};
         this.processCells((x, y) -> {
            if ((bitMaps[BitMaps.getLongIndex(x)] & BitMaps.getLongBit(x)) != 0L) {
               max[0] = max[0] <= y ? max[0] : y;
            }

            return true;
         });
         return max[0];
      }
   }

   default int getMaxInsert(BloomFilter<?> bloomFilter) {
      return this.getMaxInsert(bloomFilter);
   }

   int getMaxInsert(CellExtractor var1);

   default int getMaxInsert(Hasher hasher) {
      return this.getMaxInsert(hasher.indices(this.getShape()));
   }

   default int getMaxInsert(IndexExtractor indexExtractor) {
      return this.getMaxInsert(CellExtractor.from(indexExtractor.uniqueIndices()));
   }

   boolean isValid();

   default boolean merge(BitMapExtractor bitMapExtractor) {
      return this.merge(IndexExtractor.fromBitMapExtractor(bitMapExtractor));
   }

   default boolean merge(BloomFilter<?> other) {
      Objects.requireNonNull(other, "other");
      return this.merge((IndexExtractor)other);
   }

   default boolean merge(Hasher hasher) {
      Objects.requireNonNull(hasher, "hasher");
      return this.merge(hasher.indices(this.getShape()));
   }

   default boolean merge(IndexExtractor indexExtractor) {
      Objects.requireNonNull(indexExtractor, "indexExtractor");

      try {
         return this.add(CellExtractor.from(indexExtractor.uniqueIndices()));
      } catch (IndexOutOfBoundsException e) {
         throw new IllegalArgumentException(String.format("Filter only accepts values in the [0,%d) range", this.getShape().getNumberOfBits()), e);
      }
   }

   default boolean remove(BitMapExtractor bitMapExtractor) {
      return this.remove(IndexExtractor.fromBitMapExtractor(bitMapExtractor));
   }

   default boolean remove(BloomFilter<?> other) {
      return this.remove((IndexExtractor)other);
   }

   default boolean remove(Hasher hasher) {
      Objects.requireNonNull(hasher, "hasher");
      return this.remove(hasher.indices(this.getShape()));
   }

   default boolean remove(IndexExtractor indexExtractor) {
      Objects.requireNonNull(indexExtractor, "indexExtractor");

      try {
         return this.subtract(CellExtractor.from(indexExtractor.uniqueIndices()));
      } catch (IndexOutOfBoundsException var3) {
         throw new IllegalArgumentException(String.format("Filter only accepts values in the [0,%d) range", this.getShape().getNumberOfBits()));
      }
   }

   boolean subtract(CellExtractor var1);

   default IndexExtractor uniqueIndices() {
      return this;
   }
}
