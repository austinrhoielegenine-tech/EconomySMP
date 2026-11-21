package org.apache.commons.collections4;

import java.util.Collection;

/** @deprecated */
@Deprecated
public interface MultiMap<K, V> extends IterableMap<K, Object> {
   boolean containsValue(Object var1);

   Object get(Object var1);

   Object put(K var1, Object var2);

   Object remove(Object var1);

   boolean removeMapping(K var1, V var2);

   int size();

   Collection<Object> values();
}
