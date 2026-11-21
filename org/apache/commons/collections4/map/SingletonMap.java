package org.apache.commons.collections4.map;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.collections4.BoundedMap;
import org.apache.commons.collections4.KeyValue;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.iterators.SingletonIterator;
import org.apache.commons.collections4.keyvalue.TiedMapEntry;

public class SingletonMap<K, V> implements OrderedMap<K, V>, BoundedMap<K, V>, KeyValue<K, V>, Serializable, Cloneable {
   private static final long serialVersionUID = -8931271118676803261L;
   private final K key;
   private V value;

   public SingletonMap() {
      this.key = null;
   }

   public SingletonMap(K key, V value) {
      this.key = key;
      this.value = value;
   }

   public SingletonMap(KeyValue<K, V> keyValue) {
      this.key = keyValue.getKey();
      this.value = keyValue.getValue();
   }

   public SingletonMap(Map.Entry<? extends K, ? extends V> mapEntry) {
      this.key = (K)mapEntry.getKey();
      this.value = (V)mapEntry.getValue();
   }

   public SingletonMap(Map<? extends K, ? extends V> map) {
      if (map.size() != 1) {
         throw new IllegalArgumentException("The map size must be 1");
      } else {
         Map.Entry<? extends K, ? extends V> entry = (Map.Entry)map.entrySet().iterator().next();
         this.key = (K)entry.getKey();
         this.value = (V)entry.getValue();
      }
   }

   public void clear() {
      throw new UnsupportedOperationException();
   }

   public SingletonMap<K, V> clone() {
      try {
         return (SingletonMap)super.clone();
      } catch (CloneNotSupportedException ex) {
         throw new UnsupportedOperationException(ex);
      }
   }

   public boolean containsKey(Object key) {
      return this.isEqualKey(key);
   }

   public boolean containsValue(Object value) {
      return this.isEqualValue(value);
   }

   public Set<Map.Entry<K, V>> entrySet() {
      Map.Entry<K, V> entry = new TiedMapEntry<K, V>(this, this.getKey());
      return Collections.singleton(entry);
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof Map)) {
         return false;
      } else {
         Map<?, ?> other = (Map)obj;
         if (other.size() != 1) {
            return false;
         } else {
            Map.Entry<?, ?> entry = (Map.Entry)other.entrySet().iterator().next();
            return this.isEqualKey(entry.getKey()) && this.isEqualValue(entry.getValue());
         }
      }
   }

   public K firstKey() {
      return (K)this.getKey();
   }

   public V get(Object key) {
      return (V)(this.isEqualKey(key) ? this.value : null);
   }

   public K getKey() {
      return this.key;
   }

   public V getValue() {
      return this.value;
   }

   public int hashCode() {
      return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
   }

   public boolean isEmpty() {
      return false;
   }

   protected boolean isEqualKey(Object key) {
      return Objects.equals(key, this.getKey());
   }

   protected boolean isEqualValue(Object value) {
      return Objects.equals(value, this.getValue());
   }

   public boolean isFull() {
      return true;
   }

   public Set<K> keySet() {
      return Collections.singleton(this.key);
   }

   public K lastKey() {
      return (K)this.getKey();
   }

   public OrderedMapIterator<K, V> mapIterator() {
      return new SingletonMapIterator<K, V>(this);
   }

   public int maxSize() {
      return 1;
   }

   public K nextKey(K key) {
      return null;
   }

   public K previousKey(K key) {
      return null;
   }

   public V put(K key, V value) {
      if (this.isEqualKey(key)) {
         return (V)this.setValue(value);
      } else {
         throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size singleton");
      }
   }

   public void putAll(Map<? extends K, ? extends V> map) {
      switch (map.size()) {
         case 0:
            return;
         case 1:
            Map.Entry<? extends K, ? extends V> entry = (Map.Entry)map.entrySet().iterator().next();
            this.put(entry.getKey(), entry.getValue());
            return;
         default:
            throw new IllegalArgumentException("The map size must be 0 or 1");
      }
   }

   public V remove(Object key) {
      throw new UnsupportedOperationException();
   }

   public V setValue(V value) {
      V old = this.value;
      this.value = value;
      return old;
   }

   public int size() {
      return 1;
   }

   public String toString() {
      return (new StringBuilder(128)).append('{').append(this.getKey() == this ? "(this Map)" : this.getKey()).append('=').append(this.getValue() == this ? "(this Map)" : this.getValue()).append('}').toString();
   }

   public Collection<V> values() {
      return new SingletonValues<V>(this);
   }

   static class SingletonMapIterator<K, V> implements OrderedMapIterator<K, V>, ResettableIterator<K> {
      private final SingletonMap<K, V> parent;
      private boolean hasNext = true;
      private boolean canGetSet;

      SingletonMapIterator(SingletonMap<K, V> parent) {
         this.parent = parent;
      }

      public K getKey() {
         if (!this.canGetSet) {
            throw new IllegalStateException("getKey() can only be called after next() and before remove()");
         } else {
            return this.parent.getKey();
         }
      }

      public V getValue() {
         if (!this.canGetSet) {
            throw new IllegalStateException("getValue() can only be called after next() and before remove()");
         } else {
            return this.parent.getValue();
         }
      }

      public boolean hasNext() {
         return this.hasNext;
      }

      public boolean hasPrevious() {
         return !this.hasNext;
      }

      public K next() {
         if (!this.hasNext) {
            throw new NoSuchElementException("No next() entry in the iteration");
         } else {
            this.hasNext = false;
            this.canGetSet = true;
            return this.parent.getKey();
         }
      }

      public K previous() {
         if (this.hasNext) {
            throw new NoSuchElementException("No previous() entry in the iteration");
         } else {
            this.hasNext = true;
            return this.parent.getKey();
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }

      public void reset() {
         this.hasNext = true;
      }

      public V setValue(V value) {
         if (!this.canGetSet) {
            throw new IllegalStateException("setValue() can only be called after next() and before remove()");
         } else {
            return this.parent.setValue(value);
         }
      }

      public String toString() {
         return this.hasNext ? "Iterator[]" : "Iterator[" + this.getKey() + "=" + this.getValue() + "]";
      }
   }

   static class SingletonValues<V> extends AbstractSet<V> implements Serializable {
      private static final long serialVersionUID = -3689524741863047872L;
      private final SingletonMap<?, V> parent;

      SingletonValues(SingletonMap<?, V> parent) {
         this.parent = parent;
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      public boolean contains(Object object) {
         return this.parent.containsValue(object);
      }

      public boolean isEmpty() {
         return false;
      }

      public Iterator<V> iterator() {
         return new SingletonIterator<V>(this.parent.getValue(), false);
      }

      public int size() {
         return 1;
      }
   }
}
