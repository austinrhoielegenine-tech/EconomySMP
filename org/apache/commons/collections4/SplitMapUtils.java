package org.apache.commons.collections4;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections4.map.EntrySetToMapIteratorAdapter;
import org.apache.commons.collections4.map.UnmodifiableEntrySet;
import org.apache.commons.collections4.set.UnmodifiableSet;

public class SplitMapUtils {
   public static <K, V> IterableMap<K, V> readableMap(Get<K, V> get) {
      Objects.requireNonNull(get, "get");
      if (get instanceof Map) {
         return get instanceof IterableMap ? (IterableMap)get : MapUtils.iterableMap((Map)get);
      } else {
         return new WrappedGet<K, V>(get);
      }
   }

   public static <K, V> Map<K, V> writableMap(Put<K, V> put) {
      Objects.requireNonNull(put, "put");
      return (Map<K, V>)(put instanceof Map ? (Map)put : new WrappedPut(put));
   }

   private SplitMapUtils() {
   }

   private static final class WrappedGet<K, V> implements IterableMap<K, V>, Unmodifiable {
      private final Get<K, V> get;

      private WrappedGet(Get<K, V> get) {
         this.get = get;
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      public boolean containsKey(Object key) {
         return this.get.containsKey(key);
      }

      public boolean containsValue(Object value) {
         return this.get.containsValue(value);
      }

      public Set<Map.Entry<K, V>> entrySet() {
         return UnmodifiableEntrySet.unmodifiableEntrySet(this.get.entrySet());
      }

      public boolean equals(Object arg0) {
         if (arg0 == this) {
            return true;
         } else {
            return arg0 instanceof WrappedGet && ((WrappedGet)arg0).get.equals(this.get);
         }
      }

      public V get(Object key) {
         return this.get.get(key);
      }

      public int hashCode() {
         return "WrappedGet".hashCode() << 4 | this.get.hashCode();
      }

      public boolean isEmpty() {
         return this.get.isEmpty();
      }

      public Set<K> keySet() {
         return UnmodifiableSet.<K>unmodifiableSet(this.get.keySet());
      }

      public MapIterator<K, V> mapIterator() {
         MapIterator<K, V> it;
         if (this.get instanceof IterableGet) {
            it = ((IterableGet)this.get).mapIterator();
         } else {
            it = new EntrySetToMapIteratorAdapter<K, V>(this.get.entrySet());
         }

         return UnmodifiableMapIterator.<K, V>unmodifiableMapIterator(it);
      }

      public V put(K key, V value) {
         throw new UnsupportedOperationException();
      }

      public void putAll(Map<? extends K, ? extends V> t) {
         throw new UnsupportedOperationException();
      }

      public V remove(Object key) {
         return this.get.remove(key);
      }

      public int size() {
         return this.get.size();
      }

      public Collection<V> values() {
         return UnmodifiableCollection.<V>unmodifiableCollection(this.get.values());
      }
   }

   private static final class WrappedPut<K, V> implements Map<K, V>, Put<K, V> {
      private final Put<K, V> put;

      private WrappedPut(Put<K, V> put) {
         this.put = put;
      }

      public void clear() {
         this.put.clear();
      }

      public boolean containsKey(Object key) {
         throw new UnsupportedOperationException();
      }

      public boolean containsValue(Object value) {
         throw new UnsupportedOperationException();
      }

      public Set<Map.Entry<K, V>> entrySet() {
         throw new UnsupportedOperationException();
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else {
            return obj instanceof WrappedPut && ((WrappedPut)obj).put.equals(this.put);
         }
      }

      public V get(Object key) {
         throw new UnsupportedOperationException();
      }

      public int hashCode() {
         return "WrappedPut".hashCode() << 4 | this.put.hashCode();
      }

      public boolean isEmpty() {
         throw new UnsupportedOperationException();
      }

      public Set<K> keySet() {
         throw new UnsupportedOperationException();
      }

      public V put(K key, V value) {
         return (V)this.put.put(key, value);
      }

      public void putAll(Map<? extends K, ? extends V> t) {
         this.put.putAll(t);
      }

      public V remove(Object key) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         throw new UnsupportedOperationException();
      }

      public Collection<V> values() {
         throw new UnsupportedOperationException();
      }
   }
}
