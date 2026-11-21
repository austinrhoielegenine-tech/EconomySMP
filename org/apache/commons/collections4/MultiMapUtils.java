package org.apache.commons.collections4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections4.bag.HashBag;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.collections4.multimap.TransformedMultiValuedMap;
import org.apache.commons.collections4.multimap.UnmodifiableMultiValuedMap;

public class MultiMapUtils {
   public static final MultiValuedMap EMPTY_MULTI_VALUED_MAP = UnmodifiableMultiValuedMap.unmodifiableMultiValuedMap(new ArrayListValuedHashMap(0, 0));

   public static <K, V> MultiValuedMap<K, V> emptyIfNull(MultiValuedMap<K, V> map) {
      return map == null ? EMPTY_MULTI_VALUED_MAP : map;
   }

   public static <K, V> MultiValuedMap<K, V> emptyMultiValuedMap() {
      return EMPTY_MULTI_VALUED_MAP;
   }

   public static <K, V> Collection<V> getCollection(MultiValuedMap<K, V> map, K key) {
      return map != null ? map.get(key) : null;
   }

   public static <K, V> Bag<V> getValuesAsBag(MultiValuedMap<K, V> map, K key) {
      if (map != null) {
         Collection<V> col = map.get(key);
         return (Bag<V>)(col instanceof Bag ? (Bag)col : new HashBag(col));
      } else {
         return null;
      }
   }

   public static <K, V> List<V> getValuesAsList(MultiValuedMap<K, V> map, K key) {
      if (map != null) {
         Collection<V> col = map.get(key);
         return (List<V>)(col instanceof List ? (List)col : new ArrayList(col));
      } else {
         return null;
      }
   }

   public static <K, V> Set<V> getValuesAsSet(MultiValuedMap<K, V> map, K key) {
      if (map != null) {
         Collection<V> col = map.get(key);
         return (Set<V>)(col instanceof Set ? (Set)col : new HashSet(col));
      } else {
         return null;
      }
   }

   public static boolean isEmpty(MultiValuedMap<?, ?> map) {
      return map == null || map.isEmpty();
   }

   public static <K, V> ListValuedMap<K, V> newListValuedHashMap() {
      return new ArrayListValuedHashMap<K, V>();
   }

   public static <K, V> SetValuedMap<K, V> newSetValuedHashMap() {
      return new HashSetValuedHashMap<K, V>();
   }

   public static <K, V> MultiValuedMap<K, V> transformedMultiValuedMap(MultiValuedMap<K, V> map, Transformer<? super K, ? extends K> keyTransformer, Transformer<? super V, ? extends V> valueTransformer) {
      return TransformedMultiValuedMap.<K, V>transformingMap(map, keyTransformer, valueTransformer);
   }

   public static <K, V> MultiValuedMap<K, V> unmodifiableMultiValuedMap(MultiValuedMap<? extends K, ? extends V> map) {
      return UnmodifiableMultiValuedMap.<K, V>unmodifiableMultiValuedMap(map);
   }

   private MultiMapUtils() {
   }
}
