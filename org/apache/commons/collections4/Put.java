package org.apache.commons.collections4;

import java.util.Map;

public interface Put<K, V> {
   void clear();

   Object put(K var1, V var2);

   void putAll(Map<? extends K, ? extends V> var1);
}
