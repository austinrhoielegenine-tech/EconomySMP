package org.apache.commons.collections4;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface MultiValuedMap<K, V> {
   Map<K, Collection<V>> asMap();

   void clear();

   boolean containsKey(Object var1);

   boolean containsMapping(Object var1, Object var2);

   boolean containsValue(Object var1);

   Collection<Map.Entry<K, V>> entries();

   Collection<V> get(K var1);

   boolean isEmpty();

   MultiSet<K> keys();

   Set<K> keySet();

   MapIterator<K, V> mapIterator();

   boolean put(K var1, V var2);

   boolean putAll(K var1, Iterable<? extends V> var2);

   boolean putAll(Map<? extends K, ? extends V> var1);

   boolean putAll(MultiValuedMap<? extends K, ? extends V> var1);

   Collection<V> remove(Object var1);

   boolean removeMapping(Object var1, Object var2);

   int size();

   Collection<V> values();
}
