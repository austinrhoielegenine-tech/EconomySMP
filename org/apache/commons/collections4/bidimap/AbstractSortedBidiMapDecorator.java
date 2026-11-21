package org.apache.commons.collections4.bidimap;

import java.util.Comparator;
import java.util.SortedMap;
import org.apache.commons.collections4.SortedBidiMap;

public abstract class AbstractSortedBidiMapDecorator<K, V> extends AbstractOrderedBidiMapDecorator<K, V> implements SortedBidiMap<K, V> {
   public AbstractSortedBidiMapDecorator(SortedBidiMap<K, V> map) {
      super(map);
   }

   public Comparator<? super K> comparator() {
      return this.decorated().comparator();
   }

   protected SortedBidiMap<K, V> decorated() {
      return (SortedBidiMap)super.decorated();
   }

   public SortedMap<K, V> headMap(K toKey) {
      return this.decorated().headMap(toKey);
   }

   public SortedBidiMap<V, K> inverseBidiMap() {
      return this.decorated().inverseBidiMap();
   }

   public SortedMap<K, V> subMap(K fromKey, K toKey) {
      return this.decorated().subMap(fromKey, toKey);
   }

   public SortedMap<K, V> tailMap(K fromKey) {
      return this.decorated().tailMap(fromKey);
   }

   public Comparator<? super V> valueComparator() {
      return this.decorated().valueComparator();
   }
}
