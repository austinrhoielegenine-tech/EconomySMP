package org.apache.commons.collections4;

public interface OrderedMap<K, V> extends IterableMap<K, V> {
   K firstKey();

   K lastKey();

   OrderedMapIterator<K, V> mapIterator();

   K nextKey(K var1);

   K previousKey(K var1);
}
