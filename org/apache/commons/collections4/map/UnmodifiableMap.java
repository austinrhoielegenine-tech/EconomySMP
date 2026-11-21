package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.IterableMap;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.iterators.EntrySetMapIterator;
import org.apache.commons.collections4.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections4.set.UnmodifiableSet;

public final class UnmodifiableMap<K, V> extends AbstractMapDecorator<K, V> implements Unmodifiable, Serializable {
   private static final long serialVersionUID = 2737023427269031941L;

   public static <K, V> Map<K, V> unmodifiableMap(Map<? extends K, ? extends V> map) {
      return (Map<K, V>)(map instanceof Unmodifiable ? map : new UnmodifiableMap(map));
   }

   private UnmodifiableMap(Map<? extends K, ? extends V> map) {
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

   public MapIterator<K, V> mapIterator() {
      if (this.map instanceof IterableMap) {
         MapIterator<K, V> it = ((IterableMap)this.map).mapIterator();
         return UnmodifiableMapIterator.<K, V>unmodifiableMapIterator(it);
      } else {
         MapIterator<K, V> it = new EntrySetMapIterator<K, V>(this.map);
         return UnmodifiableMapIterator.<K, V>unmodifiableMapIterator(it);
      }
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
