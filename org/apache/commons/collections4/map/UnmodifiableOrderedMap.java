package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.iterators.UnmodifiableOrderedMapIterator;
import org.apache.commons.collections4.set.UnmodifiableSet;

public final class UnmodifiableOrderedMap<K, V> extends AbstractOrderedMapDecorator<K, V> implements Unmodifiable, Serializable {
   private static final long serialVersionUID = 8136428161720526266L;

   public static <K, V> OrderedMap<K, V> unmodifiableOrderedMap(OrderedMap<? extends K, ? extends V> map) {
      return (OrderedMap<K, V>)(map instanceof Unmodifiable ? map : new UnmodifiableOrderedMap(map));
   }

   private UnmodifiableOrderedMap(OrderedMap<? extends K, ? extends V> map) {
      super(map);
   }

   public void clear() {
      throw new UnsupportedOperationException();
   }

   public Set<Map.Entry<K, V>> entrySet() {
      Set<Map.Entry<K, V>> set = super.entrySet();
      return UnmodifiableEntrySet.unmodifiableEntrySet(set);
   }

   public Set<K> keySet() {
      Set<K> set = super.keySet();
      return UnmodifiableSet.<K>unmodifiableSet(set);
   }

   public OrderedMapIterator<K, V> mapIterator() {
      OrderedMapIterator<K, V> it = this.decorated().mapIterator();
      return UnmodifiableOrderedMapIterator.<K, V>unmodifiableOrderedMapIterator(it);
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

   public Collection<V> values() {
      Collection<V> coll = super.values();
      return UnmodifiableCollection.<V>unmodifiableCollection(coll);
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      out.writeObject(this.map);
   }
}
