package org.apache.commons.collections4.map;

import java.util.Comparator;
import java.util.SortedMap;
import org.apache.commons.collections4.Predicate;

public class PredicatedSortedMap<K, V> extends PredicatedMap<K, V> implements SortedMap<K, V> {
   private static final long serialVersionUID = 3359846175935304332L;

   public static <K, V> PredicatedSortedMap<K, V> predicatedSortedMap(SortedMap<K, V> map, Predicate<? super K> keyPredicate, Predicate<? super V> valuePredicate) {
      return new PredicatedSortedMap<K, V>(map, keyPredicate, valuePredicate);
   }

   protected PredicatedSortedMap(SortedMap<K, V> map, Predicate<? super K> keyPredicate, Predicate<? super V> valuePredicate) {
      super(map, keyPredicate, valuePredicate);
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
      return new PredicatedSortedMap<K, V>(map, this.keyPredicate, this.valuePredicate);
   }

   public K lastKey() {
      return (K)this.getSortedMap().lastKey();
   }

   public SortedMap<K, V> subMap(K fromKey, K toKey) {
      SortedMap<K, V> map = this.getSortedMap().subMap(fromKey, toKey);
      return new PredicatedSortedMap<K, V>(map, this.keyPredicate, this.valuePredicate);
   }

   public SortedMap<K, V> tailMap(K fromKey) {
      SortedMap<K, V> map = this.getSortedMap().tailMap(fromKey);
      return new PredicatedSortedMap<K, V>(map, this.keyPredicate, this.valuePredicate);
   }
}
