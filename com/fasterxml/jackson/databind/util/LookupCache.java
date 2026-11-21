package com.fasterxml.jackson.databind.util;

import java.util.function.BiConsumer;

public interface LookupCache<K, V> {
   default void contents(BiConsumer<K, V> consumer) {
      throw new UnsupportedOperationException();
   }

   default LookupCache<K, V> emptyCopy() {
      throw new UnsupportedOperationException("LookupCache implementation " + this.getClass().getName() + " does not implement `emptyCopy()`");
   }

   int size();

   V get(Object var1);

   V put(K var1, V var2);

   V putIfAbsent(K var1, V var2);

   void clear();
}
