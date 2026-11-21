package org.apache.commons.collections4.keyvalue;

import java.util.Map;
import java.util.Objects;

public abstract class AbstractMapEntry<K, V> extends AbstractKeyValue<K, V> implements Map.Entry<K, V> {
   protected AbstractMapEntry(K key, V value) {
      super(key, value);
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof Map.Entry)) {
         return false;
      } else {
         Map.Entry<?, ?> other = (Map.Entry)obj;
         return Objects.equals(this.getKey(), other.getKey()) && Objects.equals(this.getValue(), other.getValue());
      }
   }

   public int hashCode() {
      return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
   }

   public V setValue(V value) {
      return (V)super.setValue(value);
   }
}
