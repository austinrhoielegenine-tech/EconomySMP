package org.apache.commons.collections4.iterators;

import org.apache.commons.collections4.MapIterator;

public class EmptyMapIterator<K, V> extends AbstractEmptyMapIterator<K, V> implements MapIterator<K, V> {
   public static final MapIterator INSTANCE = new EmptyMapIterator();

   public static <K, V> MapIterator<K, V> emptyMapIterator() {
      return INSTANCE;
   }

   protected EmptyMapIterator() {
   }
}
