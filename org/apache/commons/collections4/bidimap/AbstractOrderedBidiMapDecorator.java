package org.apache.commons.collections4.bidimap;

import org.apache.commons.collections4.OrderedBidiMap;
import org.apache.commons.collections4.OrderedMapIterator;

public abstract class AbstractOrderedBidiMapDecorator<K, V> extends AbstractBidiMapDecorator<K, V> implements OrderedBidiMap<K, V> {
   protected AbstractOrderedBidiMapDecorator(OrderedBidiMap<K, V> map) {
      super(map);
   }

   protected OrderedBidiMap<K, V> decorated() {
      return (OrderedBidiMap)super.decorated();
   }

   public K firstKey() {
      return (K)this.decorated().firstKey();
   }

   public OrderedBidiMap<V, K> inverseBidiMap() {
      return this.decorated().inverseBidiMap();
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
