package org.apache.commons.collections4.multimap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections4.map.UnmodifiableMap;
import org.apache.commons.collections4.multiset.UnmodifiableMultiSet;
import org.apache.commons.collections4.set.UnmodifiableSet;

public final class UnmodifiableMultiValuedMap<K, V> extends AbstractMultiValuedMapDecorator<K, V> implements Unmodifiable {
   private static final long serialVersionUID = 20150612L;

   public static <K, V> UnmodifiableMultiValuedMap<K, V> unmodifiableMultiValuedMap(MultiValuedMap<? extends K, ? extends V> map) {
      return map instanceof Unmodifiable ? (UnmodifiableMultiValuedMap)map : new UnmodifiableMultiValuedMap(map);
   }

   private UnmodifiableMultiValuedMap(MultiValuedMap<? extends K, ? extends V> map) {
      super(map);
   }

   public Map<K, Collection<V>> asMap() {
      return UnmodifiableMap.<K, Collection<V>>unmodifiableMap(this.decorated().asMap());
   }

   public void clear() {
      throw new UnsupportedOperationException();
   }

   public Collection<Map.Entry<K, V>> entries() {
      return UnmodifiableCollection.<Map.Entry<K, V>>unmodifiableCollection(this.decorated().entries());
   }

   public Collection<V> get(K key) {
      return UnmodifiableCollection.<V>unmodifiableCollection(this.decorated().get(key));
   }

   public MultiSet<K> keys() {
      return UnmodifiableMultiSet.<K>unmodifiableMultiSet(this.decorated().keys());
   }

   public Set<K> keySet() {
      return UnmodifiableSet.<K>unmodifiableSet(this.decorated().keySet());
   }

   public MapIterator<K, V> mapIterator() {
      return UnmodifiableMapIterator.<K, V>unmodifiableMapIterator(this.decorated().mapIterator());
   }

   public boolean put(K key, V value) {
      throw new UnsupportedOperationException();
   }

   public boolean putAll(K key, Iterable<? extends V> values) {
      throw new UnsupportedOperationException();
   }

   public boolean putAll(Map<? extends K, ? extends V> map) {
      throw new UnsupportedOperationException();
   }

   public boolean putAll(MultiValuedMap<? extends K, ? extends V> map) {
      throw new UnsupportedOperationException();
   }

   public Collection<V> remove(Object key) {
      throw new UnsupportedOperationException();
   }

   public boolean removeMapping(Object key, Object item) {
      throw new UnsupportedOperationException();
   }

   public Collection<V> values() {
      return UnmodifiableCollection.<V>unmodifiableCollection(this.decorated().values());
   }
}
