package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.set.UnmodifiableSet;

public final class UnmodifiableSortedMap<K, V> extends AbstractSortedMapDecorator<K, V> implements Unmodifiable, Serializable {
   private static final long serialVersionUID = 5805344239827376360L;

   public static <K, V> SortedMap<K, V> unmodifiableSortedMap(SortedMap<K, ? extends V> map) {
      return (SortedMap<K, V>)(map instanceof Unmodifiable ? map : new UnmodifiableSortedMap(map));
   }

   private UnmodifiableSortedMap(SortedMap<K, ? extends V> map) {
      super(map);
   }

   public void clear() {
      throw new UnsupportedOperationException();
   }

   public Comparator<? super K> comparator() {
      return this.decorated().comparator();
   }

   public Set<Map.Entry<K, V>> entrySet() {
      return UnmodifiableEntrySet.unmodifiableEntrySet(super.entrySet());
   }

   public K firstKey() {
      return (K)this.decorated().firstKey();
   }

   public SortedMap<K, V> headMap(K toKey) {
      return new UnmodifiableSortedMap<K, V>(this.decorated().headMap(toKey));
   }

   public Set<K> keySet() {
      return UnmodifiableSet.<K>unmodifiableSet(super.keySet());
   }

   public K lastKey() {
      return (K)this.decorated().lastKey();
   }

   public V put(K key, V value) {
      throw new UnsupportedOperationException();
   }

   public void putAll(Map<? extends K, ? extends V> mapToCopy) {
      throw new UnsupportedOperationException();
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      this.map = (Map)in.readObject();
   }

   public V remove(Object key) {
      throw new UnsupportedOperationException();
   }

   public SortedMap<K, V> subMap(K fromKey, K toKey) {
      return new UnmodifiableSortedMap<K, V>(this.decorated().subMap(fromKey, toKey));
   }

   public SortedMap<K, V> tailMap(K fromKey) {
      return new UnmodifiableSortedMap<K, V>(this.decorated().tailMap(fromKey));
   }

   public Collection<V> values() {
      return UnmodifiableCollection.<V>unmodifiableCollection(super.values());
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      out.writeObject(this.map);
   }
}
