package org.apache.commons.collections4.bidimap;

import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections4.map.UnmodifiableEntrySet;
import org.apache.commons.collections4.set.UnmodifiableSet;

public final class UnmodifiableBidiMap<K, V> extends AbstractBidiMapDecorator<K, V> implements Unmodifiable {
   private UnmodifiableBidiMap<V, K> inverse;

   public static <K, V> BidiMap<K, V> unmodifiableBidiMap(BidiMap<? extends K, ? extends V> map) {
      return (BidiMap<K, V>)(map instanceof Unmodifiable ? map : new UnmodifiableBidiMap(map));
   }

   private UnmodifiableBidiMap(BidiMap<? extends K, ? extends V> map) {
      super(map);
   }

   public void clear() {
      throw new UnsupportedOperationException();
   }

   public Set<Map.Entry<K, V>> entrySet() {
      Set<Map.Entry<K, V>> set = super.entrySet();
      return UnmodifiableEntrySet.unmodifiableEntrySet(set);
   }

   public synchronized BidiMap<V, K> inverseBidiMap() {
      if (this.inverse == null) {
         this.inverse = new UnmodifiableBidiMap<V, K>(this.decorated().inverseBidiMap());
         this.inverse.inverse = this;
      }

      return this.inverse;
   }

   public Set<K> keySet() {
      Set<K> set = super.keySet();
      return UnmodifiableSet.<K>unmodifiableSet(set);
   }

   public MapIterator<K, V> mapIterator() {
      MapIterator<K, V> it = this.decorated().mapIterator();
      return UnmodifiableMapIterator.<K, V>unmodifiableMapIterator(it);
   }

   public V put(K key, V value) {
      throw new UnsupportedOperationException();
   }

   public void putAll(Map<? extends K, ? extends V> mapToCopy) {
      throw new UnsupportedOperationException();
   }

   public V remove(Object key) {
      throw new UnsupportedOperationException();
   }

   public K removeValue(Object value) {
      throw new UnsupportedOperationException();
   }

   public Set<V> values() {
      Set<V> set = super.values();
      return UnmodifiableSet.<V>unmodifiableSet(set);
   }
}
