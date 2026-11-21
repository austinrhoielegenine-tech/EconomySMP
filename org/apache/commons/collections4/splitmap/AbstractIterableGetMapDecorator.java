package org.apache.commons.collections4.splitmap;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.collections4.IterableGet;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.EntrySetToMapIteratorAdapter;

public class AbstractIterableGetMapDecorator<K, V> implements IterableGet<K, V> {
   transient Map<K, V> map;

   protected AbstractIterableGetMapDecorator() {
   }

   public AbstractIterableGetMapDecorator(Map<K, V> map) {
      this.map = (Map)Objects.requireNonNull(map, "map");
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

   public MapIterator<K, V> mapIterator() {
      return new EntrySetToMapIteratorAdapter<K, V>(this.entrySet());
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
