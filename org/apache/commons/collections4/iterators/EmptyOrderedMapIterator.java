package org.apache.commons.collections4.iterators;

import org.apache.commons.collections4.OrderedMapIterator;

public class EmptyOrderedMapIterator<K, V> extends AbstractEmptyMapIterator<K, V> implements OrderedMapIterator<K, V> {
   public static final OrderedMapIterator INSTANCE = new EmptyOrderedMapIterator();

   public static <K, V> OrderedMapIterator<K, V> emptyOrderedMapIterator() {
      return INSTANCE;
   }

   protected EmptyOrderedMapIterator() {
   }
}
