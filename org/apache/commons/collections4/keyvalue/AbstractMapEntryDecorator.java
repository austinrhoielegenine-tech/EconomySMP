package org.apache.commons.collections4.keyvalue;

import java.util.Map;
import java.util.Objects;
import org.apache.commons.collections4.KeyValue;

public abstract class AbstractMapEntryDecorator<K, V> implements Map.Entry<K, V>, KeyValue<K, V> {
   private final Map.Entry<K, V> entry;

   public AbstractMapEntryDecorator(Map.Entry<K, V> entry) {
      this.entry = (Map.Entry)Objects.requireNonNull(entry, "entry");
   }

   public boolean equals(Object object) {
      return object == this ? true : this.entry.equals(object);
   }

   public K getKey() {
      return (K)this.entry.getKey();
   }

   protected Map.Entry<K, V> getMapEntry() {
      return this.entry;
   }

   public V getValue() {
      return (V)this.entry.getValue();
   }

   public int hashCode() {
      return this.entry.hashCode();
   }

   public V setValue(V value) {
      return (V)this.entry.setValue(value);
   }

   public String toString() {
      return this.entry.toString();
   }
}
