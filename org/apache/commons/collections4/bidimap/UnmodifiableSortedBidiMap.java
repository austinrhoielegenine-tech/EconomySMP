package org.apache.commons.collections4.bidimap;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.SortedBidiMap;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.iterators.UnmodifiableOrderedMapIterator;
import org.apache.commons.collections4.map.UnmodifiableEntrySet;
import org.apache.commons.collections4.map.UnmodifiableSortedMap;
import org.apache.commons.collections4.set.UnmodifiableSet;

public final class UnmodifiableSortedBidiMap<K, V> extends AbstractSortedBidiMapDecorator<K, V> implements Unmodifiable {
   private UnmodifiableSortedBidiMap<V, K> inverse;

   public static <K, V> SortedBidiMap<K, V> unmodifiableSortedBidiMap(SortedBidiMap<K, ? extends V> map) {
      return (SortedBidiMap<K, V>)(map instanceof Unmodifiable ? map : new UnmodifiableSortedBidiMap(map));
   }

   private UnmodifiableSortedBidiMap(SortedBidiMap<K, ? extends V> map) {
      super(map);
   }

   public void clear() {
      throw new UnsupportedOperationException();
   }

   public Set<Map.Entry<K, V>> entrySet() {
      Set<Map.Entry<K, V>> set = super.entrySet();
      return UnmodifiableEntrySet.unmodifiableEntrySet(set);
   }

   public SortedMap<K, V> headMap(K toKey) {
      SortedMap<K, V> sm = this.decorated().headMap(toKey);
      return UnmodifiableSortedMap.<K, V>unmodifiableSortedMap(sm);
   }

   public SortedBidiMap<V, K> inverseBidiMap() {
      if (this.inverse == null) {
         this.inverse = new UnmodifiableSortedBidiMap<V, K>(this.decorated().inverseBidiMap());
         this.inverse.inverse = this;
      }

      return this.inverse;
   }

   public Set<K> keySet() {
      Set<K> set = super.keySet();
      return UnmodifiableSet.<K>unmodifiableSet(set);
   }

   public OrderedMapIterator<K, V> mapIterator() {
      OrderedMapIterator<K, V> it = this.decorated().mapIterator();
      return UnmodifiableOrderedMapIterator.<K, V>unmodifiableOrderedMapIterator(it);
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

   public SortedMap<K, V> subMap(K fromKey, K toKey) {
      SortedMap<K, V> sm = this.decorated().subMap(fromKey, toKey);
      return UnmodifiableSortedMap.<K, V>unmodifiableSortedMap(sm);
   }

   public SortedMap<K, V> tailMap(K fromKey) {
      SortedMap<K, V> sm = this.decorated().tailMap(fromKey);
      return UnmodifiableSortedMap.<K, V>unmodifiableSortedMap(sm);
   }

   public Set<V> values() {
      Set<V> set = super.values();
      return UnmodifiableSet.<V>unmodifiableSet(set);
   }
}
