package org.apache.commons.collections4.map;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.collection.CompositeCollection;
import org.apache.commons.collections4.set.CompositeSet;

public class CompositeMap<K, V> extends AbstractIterableMap<K, V> implements Serializable {
   private static final Map[] EMPTY_MAP_ARRAY = new Map[0];
   private static final long serialVersionUID = -6096931280583808322L;
   private Map<K, V>[] composite;
   private MapMutator<K, V> mutator;

   public CompositeMap() {
      this(new Map[0], (MapMutator)null);
   }

   public CompositeMap(Map<K, V>... composite) {
      this(composite, (MapMutator)null);
   }

   public CompositeMap(Map<K, V> one, Map<K, V> two) {
      this(new Map[]{one, two}, (MapMutator)null);
   }

   public CompositeMap(Map<K, V> one, Map<K, V> two, MapMutator<K, V> mutator) {
      this(new Map[]{one, two}, mutator);
   }

   public CompositeMap(Map<K, V>[] composite, MapMutator<K, V> mutator) {
      this.mutator = mutator;
      this.composite = EMPTY_MAP_ARRAY;

      for(int i = composite.length - 1; i >= 0; --i) {
         this.addComposited(composite[i]);
      }

   }

   public synchronized void addComposited(Map<K, V> map) throws IllegalArgumentException {
      if (map != null) {
         for(int i = this.composite.length - 1; i >= 0; --i) {
            Collection<K> intersect = CollectionUtils.<K>intersection(this.composite[i].keySet(), map.keySet());
            if (!intersect.isEmpty()) {
               if (this.mutator == null) {
                  throw new IllegalArgumentException("Key collision adding Map to CompositeMap");
               }

               this.mutator.resolveCollision(this, this.composite[i], map, intersect);
            }
         }

         Map<K, V>[] temp = (Map[])Arrays.copyOf(this.composite, this.composite.length + 1);
         temp[temp.length - 1] = map;
         this.composite = temp;
      }

   }

   public void clear() {
      for(int i = this.composite.length - 1; i >= 0; --i) {
         this.composite[i].clear();
      }

   }

   public boolean containsKey(Object key) {
      for(int i = this.composite.length - 1; i >= 0; --i) {
         if (this.composite[i].containsKey(key)) {
            return true;
         }
      }

      return false;
   }

   public boolean containsValue(Object value) {
      for(int i = this.composite.length - 1; i >= 0; --i) {
         if (this.composite[i].containsValue(value)) {
            return true;
         }
      }

      return false;
   }

   public Set<Map.Entry<K, V>> entrySet() {
      CompositeSet<Map.Entry<K, V>> entries = new CompositeSet<Map.Entry<K, V>>();

      for(int i = this.composite.length - 1; i >= 0; --i) {
         entries.addComposited(this.composite[i].entrySet());
      }

      return entries;
   }

   public boolean equals(Object obj) {
      if (obj instanceof Map) {
         Map<?, ?> map = (Map)obj;
         return this.entrySet().equals(map.entrySet());
      } else {
         return false;
      }
   }

   public V get(Object key) {
      for(int i = this.composite.length - 1; i >= 0; --i) {
         if (this.composite[i].containsKey(key)) {
            return (V)this.composite[i].get(key);
         }
      }

      return null;
   }

   public int hashCode() {
      int code = 0;

      for(Map.Entry<K, V> entry : this.entrySet()) {
         code += entry.hashCode();
      }

      return code;
   }

   public boolean isEmpty() {
      for(int i = this.composite.length - 1; i >= 0; --i) {
         if (!this.composite[i].isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public Set<K> keySet() {
      CompositeSet<K> keys = new CompositeSet<K>();

      for(int i = this.composite.length - 1; i >= 0; --i) {
         keys.addComposited(this.composite[i].keySet());
      }

      return keys;
   }

   public V put(K key, V value) {
      if (this.mutator == null) {
         throw new UnsupportedOperationException("No mutator specified");
      } else {
         return this.mutator.put(this, this.composite, key, value);
      }
   }

   public void putAll(Map<? extends K, ? extends V> map) {
      if (this.mutator == null) {
         throw new UnsupportedOperationException("No mutator specified");
      } else {
         this.mutator.putAll(this, this.composite, map);
      }
   }

   public V remove(Object key) {
      for(int i = this.composite.length - 1; i >= 0; --i) {
         if (this.composite[i].containsKey(key)) {
            return (V)this.composite[i].remove(key);
         }
      }

      return null;
   }

   public synchronized Map<K, V> removeComposited(Map<K, V> map) {
      int size = this.composite.length;

      for(int i = 0; i < size; ++i) {
         if (this.composite[i].equals(map)) {
            Map<K, V>[] temp = new Map[size - 1];
            System.arraycopy(this.composite, 0, temp, 0, i);
            System.arraycopy(this.composite, i + 1, temp, i, size - i - 1);
            this.composite = temp;
            return map;
         }
      }

      return null;
   }

   public void setMutator(MapMutator<K, V> mutator) {
      this.mutator = mutator;
   }

   public int size() {
      int size = 0;

      for(int i = this.composite.length - 1; i >= 0; --i) {
         size += this.composite[i].size();
      }

      return size;
   }

   public Collection<V> values() {
      CompositeCollection<V> values = new CompositeCollection<V>();

      for(int i = this.composite.length - 1; i >= 0; --i) {
         values.addComposited(this.composite[i].values());
      }

      return values;
   }

   public interface MapMutator<K, V> extends Serializable {
      V put(CompositeMap<K, V> var1, Map<K, V>[] var2, K var3, V var4);

      void putAll(CompositeMap<K, V> var1, Map<K, V>[] var2, Map<? extends K, ? extends V> var3);

      void resolveCollision(CompositeMap<K, V> var1, Map<K, V> var2, Map<K, V> var3, Collection<K> var4);
   }
}
