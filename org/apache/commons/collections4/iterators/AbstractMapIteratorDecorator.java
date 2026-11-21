package org.apache.commons.collections4.iterators;

import java.util.Objects;
import org.apache.commons.collections4.MapIterator;

public class AbstractMapIteratorDecorator<K, V> implements MapIterator<K, V> {
   private final MapIterator<K, V> iterator;

   public AbstractMapIteratorDecorator(MapIterator<K, V> iterator) {
      this.iterator = (MapIterator)Objects.requireNonNull(iterator, "iterator");
   }

   public K getKey() {
      return this.iterator.getKey();
   }

   protected MapIterator<K, V> getMapIterator() {
      return this.iterator;
   }

   public V getValue() {
      return this.iterator.getValue();
   }

   public boolean hasNext() {
      return this.iterator.hasNext();
   }

   public K next() {
      return this.iterator.next();
   }

   public void remove() {
      this.iterator.remove();
   }

   public V setValue(V value) {
      return this.iterator.setValue(value);
   }
}
