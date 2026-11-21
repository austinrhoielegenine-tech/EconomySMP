package org.apache.commons.collections4.map;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import org.apache.commons.collections4.Transformer;

public class TransformedSortedMap<K, V> extends TransformedMap<K, V> implements SortedMap<K, V> {
   private static final long serialVersionUID = -8751771676410385778L;

   public static <K, V> TransformedSortedMap<K, V> transformedSortedMap(SortedMap<K, V> map, Transformer<? super K, ? extends K> keyTransformer, Transformer<? super V, ? extends V> valueTransformer) {
      TransformedSortedMap<K, V> decorated = new TransformedSortedMap<K, V>(map, keyTransformer, valueTransformer);
      if (!map.isEmpty()) {
         Map<K, V> transformed = decorated.transformMap(map);
         decorated.clear();
         decorated.decorated().putAll(transformed);
      }

      return decorated;
   }

   public static <K, V> TransformedSortedMap<K, V> transformingSortedMap(SortedMap<K, V> map, Transformer<? super K, ? extends K> keyTransformer, Transformer<? super V, ? extends V> valueTransformer) {
      return new TransformedSortedMap<K, V>(map, keyTransformer, valueTransformer);
   }

   protected TransformedSortedMap(SortedMap<K, V> map, Transformer<? super K, ? extends K> keyTransformer, Transformer<? super V, ? extends V> valueTransformer) {
      super(map, keyTransformer, valueTransformer);
   }

   public Comparator<? super K> comparator() {
      return this.getSortedMap().comparator();
   }

   public K firstKey() {
      return (K)this.getSortedMap().firstKey();
   }

   protected SortedMap<K, V> getSortedMap() {
      return (SortedMap)this.map;
   }

   public SortedMap<K, V> headMap(K toKey) {
      SortedMap<K, V> map = this.getSortedMap().headMap(toKey);
      return new TransformedSortedMap<K, V>(map, this.keyTransformer, this.valueTransformer);
   }

   public K lastKey() {
      return (K)this.getSortedMap().lastKey();
   }

   public SortedMap<K, V> subMap(K fromKey, K toKey) {
      SortedMap<K, V> map = this.getSortedMap().subMap(fromKey, toKey);
      return new TransformedSortedMap<K, V>(map, this.keyTransformer, this.valueTransformer);
   }

   public SortedMap<K, V> tailMap(K fromKey) {
      SortedMap<K, V> map = this.getSortedMap().tailMap(fromKey);
      return new TransformedSortedMap<K, V>(map, this.keyTransformer, this.valueTransformer);
   }
}
