package org.apache.commons.collections4.map;

import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.OrderedMapIterator;

public abstract class AbstractOrderedMapDecorator<K, V> extends AbstractMapDecorator<K, V> implements OrderedMap<K, V> {
   protected AbstractOrderedMapDecorator() {
   }

   public AbstractOrderedMapDecorator(OrderedMap<K, V> map) {
      super(map);
   }

   protected OrderedMap<K, V> decorated() {
      return (OrderedMap)super.decorated();
   }

   public K firstKey() {
      return (K)this.decorated().firstKey();
   }

   public K lastKey() {
      return (K)this.decorated().lastKey();
   }

   public OrderedMapIterator<K, V> mapIterator() {
      return this.decorated().mapIterator();
   }

   public K nextKey(K key) {
      return (K)this.decorated().nextKey(key);
   }

   public K previousKey(K key) {
      return (K)this.decorated().previousKey(key);
   }
}
