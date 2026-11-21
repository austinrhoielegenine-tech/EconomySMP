package org.apache.commons.collections4.iterators;

import java.util.Objects;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.Unmodifiable;

public final class UnmodifiableMapIterator<K, V> implements MapIterator<K, V>, Unmodifiable {
   private final MapIterator<? extends K, ? extends V> iterator;

   public static <K, V> MapIterator<K, V> unmodifiableMapIterator(MapIterator<? extends K, ? extends V> iterator) {
      Objects.requireNonNull(iterator, "iterator");
      return (MapIterator<K, V>)(iterator instanceof Unmodifiable ? iterator : new UnmodifiableMapIterator(iterator));
   }

   private UnmodifiableMapIterator(MapIterator<? extends K, ? extends V> iterator) {
      this.iterator = iterator;
   }

   public K getKey() {
      return this.iterator.getKey();
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
      throw new UnsupportedOperationException("remove() is not supported");
   }

   public V setValue(V value) {
      throw new UnsupportedOperationException("setValue() is not supported");
   }
}
