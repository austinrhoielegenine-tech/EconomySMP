package org.apache.commons.collections4.iterators;

import java.util.Objects;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.Unmodifiable;

public final class UnmodifiableOrderedMapIterator<K, V> implements OrderedMapIterator<K, V>, Unmodifiable {
   private final OrderedMapIterator<? extends K, ? extends V> iterator;

   public static <K, V> OrderedMapIterator<K, V> unmodifiableOrderedMapIterator(OrderedMapIterator<K, ? extends V> iterator) {
      Objects.requireNonNull(iterator, "iterator");
      return (OrderedMapIterator<K, V>)(iterator instanceof Unmodifiable ? iterator : new UnmodifiableOrderedMapIterator(iterator));
   }

   private UnmodifiableOrderedMapIterator(OrderedMapIterator<K, ? extends V> iterator) {
      this.iterator = iterator;
   }

   public K getKey() {
      return (K)this.iterator.getKey();
   }

   public V getValue() {
      return (V)this.iterator.getValue();
   }

   public boolean hasNext() {
      return this.iterator.hasNext();
   }

   public boolean hasPrevious() {
      return this.iterator.hasPrevious();
   }

   public K next() {
      return (K)this.iterator.next();
   }

   public K previous() {
      return this.iterator.previous();
   }

   public void remove() {
      throw new UnsupportedOperationException("remove() is not supported");
   }

   public V setValue(V value) {
      throw new UnsupportedOperationException("setValue() is not supported");
   }
}
