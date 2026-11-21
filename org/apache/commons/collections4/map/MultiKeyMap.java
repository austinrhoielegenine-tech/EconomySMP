package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;

public class MultiKeyMap<K, V> extends AbstractMapDecorator<MultiKey<? extends K>, V> implements Serializable, Cloneable {
   private static final long serialVersionUID = -1788199231038721040L;

   public static <K, V> MultiKeyMap<K, V> multiKeyMap(AbstractHashedMap<MultiKey<? extends K>, V> map) {
      Objects.requireNonNull(map, "map");
      if (map.isEmpty()) {
         return new MultiKeyMap<K, V>(map);
      } else {
         throw new IllegalArgumentException("Map must be empty");
      }
   }

   public MultiKeyMap() {
      this(new HashedMap());
   }

   protected MultiKeyMap(AbstractHashedMap<MultiKey<? extends K>, V> map) {
      super(map);
      this.map = map;
   }

   protected void checkKey(MultiKey<?> key) {
      Objects.requireNonNull(key, "key");
   }

   public MultiKeyMap<K, V> clone() {
      try {
         return (MultiKeyMap)super.clone();
      } catch (CloneNotSupportedException e) {
         throw new UnsupportedOperationException(e);
      }
   }

   public boolean containsKey(Object key1, Object key2) {
      int hashCode = this.hash(key1, key2);

      for(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decoratedHashEntry(hashCode); entry != null; entry = entry.next) {
         if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2)) {
            return true;
         }
      }

      return false;
   }

   public boolean containsKey(Object key1, Object key2, Object key3) {
      int hashCode = this.hash(key1, key2, key3);

      for(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decoratedHashEntry(hashCode); entry != null; entry = entry.next) {
         if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3)) {
            return true;
         }
      }

      return false;
   }

   public boolean containsKey(Object key1, Object key2, Object key3, Object key4) {
      int hashCode = this.hash(key1, key2, key3, key4);

      for(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decoratedHashEntry(hashCode); entry != null; entry = entry.next) {
         if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4)) {
            return true;
         }
      }

      return false;
   }

   public boolean containsKey(Object key1, Object key2, Object key3, Object key4, Object key5) {
      int hashCode = this.hash(key1, key2, key3, key4, key5);

      for(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decoratedHashEntry(hashCode); entry != null; entry = entry.next) {
         if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4, key5)) {
            return true;
         }
      }

      return false;
   }

   protected AbstractHashedMap<MultiKey<? extends K>, V> decorated() {
      return (AbstractHashedMap)super.decorated();
   }

   AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> decoratedHashEntry(int hashCode) {
      return this.decorated().data[this.decoratedHashIndex(hashCode)];
   }

   int decoratedHashIndex(int hashCode) {
      return this.decorated().hashIndex(hashCode, this.decorated().data.length);
   }

   public V get(Object key1, Object key2) {
      int hashCode = this.hash(key1, key2);

      for(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decoratedHashEntry(hashCode); entry != null; entry = entry.next) {
         if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2)) {
            return entry.getValue();
         }
      }

      return null;
   }

   public V get(Object key1, Object key2, Object key3) {
      int hashCode = this.hash(key1, key2, key3);

      for(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decoratedHashEntry(hashCode); entry != null; entry = entry.next) {
         if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3)) {
            return entry.getValue();
         }
      }

      return null;
   }

   public V get(Object key1, Object key2, Object key3, Object key4) {
      int hashCode = this.hash(key1, key2, key3, key4);

      for(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decoratedHashEntry(hashCode); entry != null; entry = entry.next) {
         if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4)) {
            return entry.getValue();
         }
      }

      return null;
   }

   public V get(Object key1, Object key2, Object key3, Object key4, Object key5) {
      int hashCode = this.hash(key1, key2, key3, key4, key5);

      for(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decoratedHashEntry(hashCode); entry != null; entry = entry.next) {
         if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4, key5)) {
            return entry.getValue();
         }
      }

      return null;
   }

   protected int hash(Object key1, Object key2) {
      int h = 0;
      if (key1 != null) {
         h ^= key1.hashCode();
      }

      if (key2 != null) {
         h ^= key2.hashCode();
      }

      h += ~(h << 9);
      h ^= h >>> 14;
      h += h << 4;
      h ^= h >>> 10;
      return h;
   }

   protected int hash(Object key1, Object key2, Object key3) {
      int h = 0;
      if (key1 != null) {
         h ^= key1.hashCode();
      }

      if (key2 != null) {
         h ^= key2.hashCode();
      }

      if (key3 != null) {
         h ^= key3.hashCode();
      }

      h += ~(h << 9);
      h ^= h >>> 14;
      h += h << 4;
      h ^= h >>> 10;
      return h;
   }

   protected int hash(Object key1, Object key2, Object key3, Object key4) {
      int h = 0;
      if (key1 != null) {
         h ^= key1.hashCode();
      }

      if (key2 != null) {
         h ^= key2.hashCode();
      }

      if (key3 != null) {
         h ^= key3.hashCode();
      }

      if (key4 != null) {
         h ^= key4.hashCode();
      }

      h += ~(h << 9);
      h ^= h >>> 14;
      h += h << 4;
      h ^= h >>> 10;
      return h;
   }

   protected int hash(Object key1, Object key2, Object key3, Object key4, Object key5) {
      int h = 0;
      if (key1 != null) {
         h ^= key1.hashCode();
      }

      if (key2 != null) {
         h ^= key2.hashCode();
      }

      if (key3 != null) {
         h ^= key3.hashCode();
      }

      if (key4 != null) {
         h ^= key4.hashCode();
      }

      if (key5 != null) {
         h ^= key5.hashCode();
      }

      h += ~(h << 9);
      h ^= h >>> 14;
      h += h << 4;
      h ^= h >>> 10;
      return h;
   }

   protected boolean isEqualKey(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry, Object key1, Object key2) {
      MultiKey<? extends K> multi = entry.getKey();
      return multi.size() == 2 && Objects.equals(key1, multi.getKey(0)) && Objects.equals(key2, multi.getKey(1));
   }

   protected boolean isEqualKey(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry, Object key1, Object key2, Object key3) {
      MultiKey<? extends K> multi = entry.getKey();
      return multi.size() == 3 && Objects.equals(key1, multi.getKey(0)) && Objects.equals(key2, multi.getKey(1)) && Objects.equals(key3, multi.getKey(2));
   }

   protected boolean isEqualKey(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry, Object key1, Object key2, Object key3, Object key4) {
      MultiKey<? extends K> multi = entry.getKey();
      return multi.size() == 4 && Objects.equals(key1, multi.getKey(0)) && Objects.equals(key2, multi.getKey(1)) && Objects.equals(key3, multi.getKey(2)) && Objects.equals(key4, multi.getKey(3));
   }

   protected boolean isEqualKey(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry, Object key1, Object key2, Object key3, Object key4, Object key5) {
      MultiKey<? extends K> multi = entry.getKey();
      return multi.size() == 5 && Objects.equals(key1, multi.getKey(0)) && Objects.equals(key2, multi.getKey(1)) && Objects.equals(key3, multi.getKey(2)) && Objects.equals(key4, multi.getKey(3)) && Objects.equals(key5, multi.getKey(4));
   }

   public MapIterator<MultiKey<? extends K>, V> mapIterator() {
      return this.decorated().mapIterator();
   }

   public V put(K key1, K key2, K key3, K key4, K key5, V value) {
      int hashCode = this.hash(key1, key2, key3, key4, key5);
      int index = this.decoratedHashIndex(hashCode);

      for(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[index]; entry != null; entry = entry.next) {
         if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4, key5)) {
            V oldValue = entry.getValue();
            this.decorated().updateEntry(entry, value);
            return oldValue;
         }
      }

      this.decorated().addMapping(index, hashCode, new MultiKey(key1, key2, key3, key4, key5), value);
      return null;
   }

   public V put(K key1, K key2, K key3, K key4, V value) {
      int hashCode = this.hash(key1, key2, key3, key4);
      int index = this.decoratedHashIndex(hashCode);

      for(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[index]; entry != null; entry = entry.next) {
         if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4)) {
            V oldValue = entry.getValue();
            this.decorated().updateEntry(entry, value);
            return oldValue;
         }
      }

      this.decorated().addMapping(index, hashCode, new MultiKey(key1, key2, key3, key4), value);
      return null;
   }

   public V put(K key1, K key2, K key3, V value) {
      int hashCode = this.hash(key1, key2, key3);
      int index = this.decoratedHashIndex(hashCode);

      for(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[index]; entry != null; entry = entry.next) {
         if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3)) {
            V oldValue = entry.getValue();
            this.decorated().updateEntry(entry, value);
            return oldValue;
         }
      }

      this.decorated().addMapping(index, hashCode, new MultiKey(key1, key2, key3), value);
      return null;
   }

   public V put(K key1, K key2, V value) {
      int hashCode = this.hash(key1, key2);
      int index = this.decoratedHashIndex(hashCode);

      for(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[index]; entry != null; entry = entry.next) {
         if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2)) {
            V oldValue = entry.getValue();
            this.decorated().updateEntry(entry, value);
            return oldValue;
         }
      }

      this.decorated().addMapping(index, hashCode, new MultiKey(key1, key2), value);
      return null;
   }

   public V put(MultiKey<? extends K> key, V value) {
      this.checkKey(key);
      return (V)super.put(key, value);
   }

   public void putAll(Map<? extends MultiKey<? extends K>, ? extends V> mapToCopy) {
      for(MultiKey<? extends K> key : mapToCopy.keySet()) {
         this.checkKey(key);
      }

      super.putAll(mapToCopy);
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      this.map = (Map)in.readObject();
   }

   public boolean removeAll(Object key1) {
      boolean modified = false;
      MapIterator<MultiKey<? extends K>, V> it = this.mapIterator();

      while(it.hasNext()) {
         MultiKey<? extends K> multi = it.next();
         if (multi.size() >= 1 && Objects.equals(key1, multi.getKey(0))) {
            it.remove();
            modified = true;
         }
      }

      return modified;
   }

   public boolean removeAll(Object key1, Object key2) {
      boolean modified = false;
      MapIterator<MultiKey<? extends K>, V> it = this.mapIterator();

      while(it.hasNext()) {
         MultiKey<? extends K> multi = it.next();
         if (multi.size() >= 2 && Objects.equals(key1, multi.getKey(0)) && Objects.equals(key2, multi.getKey(1))) {
            it.remove();
            modified = true;
         }
      }

      return modified;
   }

   public boolean removeAll(Object key1, Object key2, Object key3) {
      boolean modified = false;
      MapIterator<MultiKey<? extends K>, V> it = this.mapIterator();

      while(it.hasNext()) {
         MultiKey<? extends K> multi = it.next();
         if (multi.size() >= 3 && Objects.equals(key1, multi.getKey(0)) && Objects.equals(key2, multi.getKey(1)) && Objects.equals(key3, multi.getKey(2))) {
            it.remove();
            modified = true;
         }
      }

      return modified;
   }

   public boolean removeAll(Object key1, Object key2, Object key3, Object key4) {
      boolean modified = false;
      MapIterator<MultiKey<? extends K>, V> it = this.mapIterator();

      while(it.hasNext()) {
         MultiKey<? extends K> multi = it.next();
         if (multi.size() >= 4 && Objects.equals(key1, multi.getKey(0)) && Objects.equals(key2, multi.getKey(1)) && Objects.equals(key3, multi.getKey(2)) && Objects.equals(key4, multi.getKey(3))) {
            it.remove();
            modified = true;
         }
      }

      return modified;
   }

   public V removeMultiKey(Object key1, Object key2) {
      int hashCode = this.hash(key1, key2);
      int index = this.decoratedHashIndex(hashCode);
      AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[index];

      for(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> previous = null; entry != null; entry = entry.next) {
         if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2)) {
            V oldValue = entry.getValue();
            this.decorated().removeMapping(entry, index, previous);
            return oldValue;
         }

         previous = entry;
      }

      return null;
   }

   public V removeMultiKey(Object key1, Object key2, Object key3) {
      int hashCode = this.hash(key1, key2, key3);
      int index = this.decoratedHashIndex(hashCode);
      AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[index];

      for(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> previous = null; entry != null; entry = entry.next) {
         if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3)) {
            V oldValue = entry.getValue();
            this.decorated().removeMapping(entry, index, previous);
            return oldValue;
         }

         previous = entry;
      }

      return null;
   }

   public V removeMultiKey(Object key1, Object key2, Object key3, Object key4) {
      int hashCode = this.hash(key1, key2, key3, key4);
      int index = this.decoratedHashIndex(hashCode);
      AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[index];

      for(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> previous = null; entry != null; entry = entry.next) {
         if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4)) {
            V oldValue = entry.getValue();
            this.decorated().removeMapping(entry, index, previous);
            return oldValue;
         }

         previous = entry;
      }

      return null;
   }

   public V removeMultiKey(Object key1, Object key2, Object key3, Object key4, Object key5) {
      int hashCode = this.hash(key1, key2, key3, key4, key5);
      int index = this.decoratedHashIndex(hashCode);
      AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[index];

      for(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> previous = null; entry != null; entry = entry.next) {
         if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4, key5)) {
            V oldValue = entry.getValue();
            this.decorated().removeMapping(entry, index, previous);
            return oldValue;
         }

         previous = entry;
      }

      return null;
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      out.writeObject(this.map);
   }
}
