package org.apache.commons.collections4.bidimap;

import java.util.Set;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.AbstractMapDecorator;

public abstract class AbstractBidiMapDecorator<K, V> extends AbstractMapDecorator<K, V> implements BidiMap<K, V> {
   protected AbstractBidiMapDecorator(BidiMap<K, V> map) {
      super(map);
   }

   protected BidiMap<K, V> decorated() {
      return (BidiMap)super.decorated();
   }

   public K getKey(Object value) {
      return (K)this.decorated().getKey(value);
   }

   public BidiMap<V, K> inverseBidiMap() {
      return this.decorated().inverseBidiMap();
   }

   public MapIterator<K, V> mapIterator() {
      return this.decorated().mapIterator();
   }

   public K removeValue(Object value) {
      return (K)this.decorated().removeValue(value);
   }

   public Set<V> values() {
      return this.decorated().values();
   }
}
