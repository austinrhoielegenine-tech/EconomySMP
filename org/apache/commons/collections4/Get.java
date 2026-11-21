package org.apache.commons.collections4;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface Get<K, V> {
   boolean containsKey(Object var1);

   boolean containsValue(Object var1);

   Set<Map.Entry<K, V>> entrySet();

   V get(Object var1);

   boolean isEmpty();

   Set<K> keySet();

   V remove(Object var1);

   int size();

   Collection<V> values();
}
