package org.apache.commons.collections4.map;

import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import org.apache.commons.collections4.IterableSortedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.iterators.ListIteratorWrapper;

public abstract class AbstractSortedMapDecorator<K, V> extends AbstractMapDecorator<K, V> implements IterableSortedMap<K, V> {
   protected AbstractSortedMapDecorator() {
   }

   public AbstractSortedMapDecorator(SortedMap<K, V> map) {
      super(map);
   }

   public Comparator<? super K> comparator() {
      return this.decorated().comparator();
   }

   protected SortedMap<K, V> decorated() {
      return (SortedMap)super.decorated();
   }

   public K firstKey() {
      return (K)this.decorated().firstKey();
   }

   public SortedMap<K, V> headMap(K toKey) {
      return this.decorated().headMap(toKey);
   }

   public K lastKey() {
      return (K)this.decorated().lastKey();
   }

   public OrderedMapIterator<K, V> mapIterator() {
      return new SortedMapIterator<K, V>(this.entrySet());
   }

   public K nextKey(K key) {
      Iterator<K> it = this.tailMap(key).keySet().iterator();
      it.next();
      return (K)(it.hasNext() ? it.next() : null);
   }

   public K previousKey(K key) {
      SortedMap<K, V> headMap = this.headMap(key);
      return (K)(headMap.isEmpty() ? null : headMap.lastKey());
   }

   public SortedMap<K, V> subMap(K fromKey, K toKey) {
      return this.decorated().subMap(fromKey, toKey);
   }

   public SortedMap<K, V> tailMap(K fromKey) {
      return this.decorated().tailMap(fromKey);
   }

   protected static class SortedMapIterator<K, V> extends EntrySetToMapIteratorAdapter<K, V> implements OrderedMapIterator<K, V> {
      protected SortedMapIterator(Set<Map.Entry<K, V>> entrySet) {
         super(entrySet);
      }

      public boolean hasPrevious() {
         return ((ListIterator)this.iterator).hasPrevious();
      }

      public K previous() {
         this.entry = (Map.Entry)((ListIterator)this.iterator).previous();
         return (K)this.getKey();
      }

      public synchronized void reset() {
         super.reset();
         this.iterator = new ListIteratorWrapper<Map.Entry<K, V>>(this.iterator);
      }
   }
}
