package org.apache.commons.collections4.multimap;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiValuedMap;

public abstract class AbstractMultiValuedMapDecorator<K, V> implements MultiValuedMap<K, V>, Serializable {
   private static final long serialVersionUID = 20150612L;
   private final MultiValuedMap<K, V> map;

   protected AbstractMultiValuedMapDecorator(MultiValuedMap<K, V> map) {
      this.map = (MultiValuedMap)Objects.requireNonNull(map, "map");
   }

   public Map<K, Collection<V>> asMap() {
      return this.decorated().asMap();
   }

   public void clear() {
      this.decorated().clear();
   }

   public boolean containsKey(Object key) {
      return this.decorated().containsKey(key);
   }

   public boolean containsMapping(Object key, Object value) {
      return this.decorated().containsMapping(key, value);
   }

   public boolean containsValue(Object value) {
      return this.decorated().containsValue(value);
   }

   protected MultiValuedMap<K, V> decorated() {
      return this.map;
   }

   public Collection<Map.Entry<K, V>> entries() {
      return this.decorated().entries();
   }

   public boolean equals(Object object) {
      return object == this ? true : this.decorated().equals(object);
   }

   public Collection<V> get(K key) {
      return this.decorated().get(key);
   }

   public int hashCode() {
      return this.decorated().hashCode();
   }

   public boolean isEmpty() {
      return this.decorated().isEmpty();
   }

   public MultiSet<K> keys() {
      return this.decorated().keys();
   }

   public Set<K> keySet() {
      return this.decorated().keySet();
   }

   public MapIterator<K, V> mapIterator() {
      return this.decorated().mapIterator();
   }

   public boolean put(K key, V value) {
      return this.decorated().put(key, value);
   }

   public boolean putAll(K key, Iterable<? extends V> values) {
      return this.decorated().putAll(key, values);
   }

   public boolean putAll(Map<? extends K, ? extends V> map) {
      return this.decorated().putAll(map);
   }

   public boolean putAll(MultiValuedMap<? extends K, ? extends V> map) {
      return this.decorated().putAll(map);
   }

   public Collection<V> remove(Object key) {
      return this.decorated().remove(key);
   }

   public boolean removeMapping(Object key, Object item) {
      return this.decorated().removeMapping(key, item);
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
