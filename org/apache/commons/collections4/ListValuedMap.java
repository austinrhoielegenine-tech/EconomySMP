package org.apache.commons.collections4;

import java.util.List;

public interface ListValuedMap<K, V> extends MultiValuedMap<K, V> {
   List<V> get(K var1);

   List<V> remove(Object var1);
}
