package org.apache.commons.collections4.trie;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.iterators.UnmodifiableOrderedMapIterator;

public class UnmodifiableTrie<K, V> implements Trie<K, V>, Serializable, Unmodifiable {
   private static final long serialVersionUID = -7156426030315945159L;
   private final Trie<K, V> delegate;

   public static <K, V> Trie<K, V> unmodifiableTrie(Trie<K, ? extends V> trie) {
      return (Trie<K, V>)(trie instanceof Unmodifiable ? trie : new UnmodifiableTrie(trie));
   }

   public UnmodifiableTrie(Trie<K, ? extends V> trie) {
      Trie<K, V> tmpTrie = (Trie)Objects.requireNonNull(trie, "trie");
      this.delegate = tmpTrie;
   }

   public void clear() {
      throw new UnsupportedOperationException();
   }

   public Comparator<? super K> comparator() {
      return this.delegate.comparator();
   }

   public boolean containsKey(Object key) {
      return this.delegate.containsKey(key);
   }

   public boolean containsValue(Object value) {
      return this.delegate.containsValue(value);
   }

   public Set<Map.Entry<K, V>> entrySet() {
      return Collections.unmodifiableSet(this.delegate.entrySet());
   }

   public boolean equals(Object obj) {
      return this.delegate.equals(obj);
   }

   public K firstKey() {
      return (K)this.delegate.firstKey();
   }

   public V get(Object key) {
      return (V)this.delegate.get(key);
   }

   public int hashCode() {
      return this.delegate.hashCode();
   }

   public SortedMap<K, V> headMap(K toKey) {
      return Collections.unmodifiableSortedMap(this.delegate.headMap(toKey));
   }

   public boolean isEmpty() {
      return this.delegate.isEmpty();
   }

   public Set<K> keySet() {
      return Collections.unmodifiableSet(this.delegate.keySet());
   }

   public K lastKey() {
      return (K)this.delegate.lastKey();
   }

   public OrderedMapIterator<K, V> mapIterator() {
      OrderedMapIterator<K, V> it = this.delegate.mapIterator();
      return UnmodifiableOrderedMapIterator.<K, V>unmodifiableOrderedMapIterator(it);
   }

   public K nextKey(K key) {
      return (K)this.delegate.nextKey(key);
   }

   public SortedMap<K, V> prefixMap(K key) {
      return Collections.unmodifiableSortedMap(this.delegate.prefixMap(key));
   }

   public K previousKey(K key) {
      return (K)this.delegate.previousKey(key);
   }

   public V put(K key, V value) {
      throw new UnsupportedOperationException();
   }

   public void putAll(Map<? extends K, ? extends V> m) {
      throw new UnsupportedOperationException();
   }

   public V remove(Object key) {
      throw new UnsupportedOperationException();
   }

   public int size() {
      return this.delegate.size();
   }

   public SortedMap<K, V> subMap(K fromKey, K toKey) {
      return Collections.unmodifiableSortedMap(this.delegate.subMap(fromKey, toKey));
   }

   public SortedMap<K, V> tailMap(K fromKey) {
      return Collections.unmodifiableSortedMap(this.delegate.tailMap(fromKey));
   }

   public String toString() {
      return this.delegate.toString();
   }

   public Collection<V> values() {
      return Collections.unmodifiableCollection(this.delegate.values());
   }
}
