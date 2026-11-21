package org.apache.commons.collections4.bloomfilter;

import java.util.Arrays;

final class IndexUtils {
   static final int MAX_ARRAY_SIZE = 2147483639;

   static int[] ensureCapacityForAdd(int[] array, int index) {
      return index >= array.length ? Arrays.copyOf(array, (int)Math.min(2147483639L, Math.max((long)array.length * 2L, (long)(index + 1)))) : array;
   }

   private IndexUtils() {
   }
}
