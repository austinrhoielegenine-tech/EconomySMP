package org.apache.commons.collections4.keyvalue;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.collections4.KeyValue;

public class TiedMapEntry<K, V> implements Map.Entry<K, V>, KeyValue<K, V>, Serializable {
   private static final long serialVersionUID = -8453869361373831205L;
   private final Map<K, V> map;
   private final K key;

   public TiedMapEntry(Map<K, V> map, K key) {
      this.map = map;
      this.key = key;
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof Map.Entry)) {
         return false;
      } else {
         Map.Entry<?, ?> other = (Map.Entry)obj;
         return Objects.equals(this.key, other.getKey()) && Objects.equals(this.getValue(), other.getValue());
      }
   }

   public K getKey() {
      return this.key;
   }

   public V getValue() {
      return (V)this.map.get(this.key);
   }

   public int hashCode() {
      Object value = this.getValue();
      return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (value == null ? 0 : value.hashCode());
   }

   public V setValue(V value) {
      if (value == this) {
         throw new IllegalArgumentException("Cannot set value to this map entry");
      } else {
         return (V)this.map.put(this.key, value);
      }
   }

   public String toString() {
      return this.getKey() + "=" + this.getValue();
   }
}
