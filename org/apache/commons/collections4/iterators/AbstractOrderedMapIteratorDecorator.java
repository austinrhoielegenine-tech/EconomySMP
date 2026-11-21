package org.apache.commons.collections4.iterators;

import java.util.Objects;
import org.apache.commons.collections4.OrderedMapIterator;

public class AbstractOrderedMapIteratorDecorator<K, V> implements OrderedMapIterator<K, V> {
   private final OrderedMapIterator<K, V> iterator;

   public AbstractOrderedMapIteratorDecorator(OrderedMapIterator<K, V> iterator) {
      this.iterator = (OrderedMapIterator)Objects.requireNonNull(iterator, "iterator");
   }

   public K getKey() {
      return (K)this.iterator.getKey();
   }

   protected OrderedMapIterator<K, V> getOrderedMapIterator() {
      return this.iterator;
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
      this.iterator.remove();
   }

   public V setValue(V value) {
      return (V)this.iterator.setValue(value);
   }
}
