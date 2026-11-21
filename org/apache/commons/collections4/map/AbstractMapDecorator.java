package org.apache.commons.collections4.map;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractMapDecorator<K, V> extends AbstractIterableMap<K, V> {
   transient Map<K, V> map;

   protected AbstractMapDecorator() {
   }

   protected AbstractMapDecorator(Map<K, V> map) {
      this.map = (Map)Objects.requireNonNull(map, "map");
   }

   public void clear() {
      this.decorated().clear();
   }

   public boolean containsKey(Object key) {
      return this.decorated().containsKey(key);
   }

   public boolean containsValue(Object value) {
      return this.decorated().containsValue(value);
   }

   protected Map<K, V> decorated() {
      return this.map;
   }

   public Set<Map.Entry<K, V>> entrySet() {
      return this.decorated().entrySet();
   }

   public boolean equals(Object object) {
      return object == this ? true : this.decorated().equals(object);
   }

   public V get(Object key) {
      return (V)this.decorated().get(key);
   }

   public int hashCode() {
      return this.decorated().hashCode();
   }

   public boolean isEmpty() {
      return this.decorated().isEmpty();
   }

   public Set<K> keySet() {
      return this.decorated().keySet();
   }

   public V put(K key, V value) {
      return (V)this.decorated().put(key, value);
   }

   public void putAll(Map<? extends K, ? extends V> mapToCopy) {
      this.decorated().putAll(mapToCopy);
   }

   public V remove(Object key) {
      return (V)this.decorated().remove(key);
   }

   public int size() {
      return this.decorated().size();
   }

   public String toString() {
      return this.decorated().toString();
   }

   public Collection<V> values() {
      return this.decorated().values();
   }
}
