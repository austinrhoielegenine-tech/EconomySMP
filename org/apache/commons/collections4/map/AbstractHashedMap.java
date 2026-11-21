package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.collections4.IterableMap;
import org.apache.commons.collections4.KeyValue;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.iterators.EmptyIterator;
import org.apache.commons.collections4.iterators.EmptyMapIterator;

public class AbstractHashedMap<K, V> extends AbstractMap<K, V> implements IterableMap<K, V> {
   protected static final String NO_NEXT_ENTRY = "No next() entry in the iteration";
   protected static final String NO_PREVIOUS_ENTRY = "No previous() entry in the iteration";
   protected static final String REMOVE_INVALID = "remove() can only be called once after next()";
   protected static final String GETKEY_INVALID = "getKey() can only be called after next() and before remove()";
   protected static final String GETVALUE_INVALID = "getValue() can only be called after next() and before remove()";
   protected static final String SETVALUE_INVALID = "setValue() can only be called after next() and before remove()";
   protected static final int DEFAULT_CAPACITY = 16;
   protected static final int DEFAULT_THRESHOLD = 12;
   protected static final float DEFAULT_LOAD_FACTOR = 0.75F;
   protected static final int MAXIMUM_CAPACITY = 1073741824;
   protected static final Object NULL = new Object();
   transient float loadFactor;
   transient int size;
   transient HashEntry<K, V>[] data;
   transient int threshold;
   transient int modCount;
   transient EntrySet<K, V> entrySet;
   transient KeySet<K> keySet;
   transient Values<V> values;

   protected AbstractHashedMap() {
   }

   protected AbstractHashedMap(int initialCapacity) {
      this(initialCapacity, 0.75F);
   }

   protected AbstractHashedMap(int initialCapacity, float loadFactor) {
      if (initialCapacity < 0) {
         throw new IllegalArgumentException("Initial capacity must be a non negative number");
      } else if (!(loadFactor <= 0.0F) && !Float.isNaN(loadFactor)) {
         this.loadFactor = loadFactor;
         initialCapacity = this.calculateNewCapacity(initialCapacity);
         this.threshold = this.calculateThreshold(initialCapacity, loadFactor);
         this.data = new HashEntry[initialCapacity];
         this.init();
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0");
      }
   }

   protected AbstractHashedMap(int initialCapacity, float loadFactor, int threshold) {
      this.loadFactor = loadFactor;
      this.data = new HashEntry[initialCapacity];
      this.threshold = threshold;
      this.init();
   }

   protected AbstractHashedMap(Map<? extends K, ? extends V> map) {
      this(Math.max(2 * map.size(), 16), 0.75F);
      this.putAll(map);
   }

   protected void addEntry(HashEntry<K, V> entry, int hashIndex) {
      this.data[hashIndex] = entry;
   }

   protected void addMapping(int hashIndex, int hashCode, K key, V value) {
      ++this.modCount;
      HashEntry<K, V> entry = this.createEntry(this.data[hashIndex], hashCode, key, value);
      this.addEntry(entry, hashIndex);
      ++this.size;
      this.checkCapacity();
   }

   protected int calculateNewCapacity(int proposedCapacity) {
      int newCapacity = 1;
      if (proposedCapacity > 1073741824) {
         newCapacity = 1073741824;
      } else {
         while(newCapacity < proposedCapacity) {
            newCapacity <<= 1;
         }

         if (newCapacity > 1073741824) {
            newCapacity = 1073741824;
         }
      }

      return newCapacity;
   }

   protected int calculateThreshold(int newCapacity, float factor) {
      return (int)((float)newCapacity * factor);
   }

   protected void checkCapacity() {
      if (this.size >= this.threshold) {
         int newCapacity = this.data.length * 2;
         if (newCapacity <= 1073741824) {
            this.ensureCapacity(newCapacity);
         }
      }

   }

   public void clear() {
      ++this.modCount;
      HashEntry<K, V>[] data = this.data;
      Arrays.fill(data, (Object)null);
      this.size = 0;
   }

   protected AbstractHashedMap<K, V> clone() {
      try {
         AbstractHashedMap<K, V> cloned = (AbstractHashedMap)super.clone();
         cloned.data = new HashEntry[this.data.length];
         cloned.entrySet = null;
         cloned.keySet = null;
         cloned.values = null;
         cloned.modCount = 0;
         cloned.size = 0;
         cloned.init();
         cloned.putAll(this);
         return cloned;
      } catch (CloneNotSupportedException ex) {
         throw new UnsupportedOperationException(ex);
      }
   }

   public boolean containsKey(Object key) {
      key = this.convertKey(key);
      int hashCode = this.hash(key);

      for(HashEntry<K, V> entry = this.data[this.hashIndex(hashCode, this.data.length)]; entry != null; entry = entry.next) {
         if (entry.hashCode == hashCode && this.isEqualKey(key, entry.key)) {
            return true;
         }
      }

      return false;
   }

   public boolean containsValue(Object value) {
      if (value == null) {
         for(HashEntry<K, V> element : this.data) {
            for(HashEntry<K, V> entry = element; entry != null; entry = entry.next) {
               if (entry.getValue() == null) {
                  return true;
               }
            }
         }
      } else {
         for(HashEntry<K, V> element : this.data) {
            for(HashEntry<K, V> entry = element; entry != null; entry = entry.next) {
               if (this.isEqualValue(value, entry.getValue())) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   protected Object convertKey(Object key) {
      return key == null ? NULL : key;
   }

   protected HashEntry<K, V> createEntry(HashEntry<K, V> next, int hashCode, K key, V value) {
      return new HashEntry<K, V>(next, hashCode, this.convertKey(key), value);
   }

   protected Iterator<Map.Entry<K, V>> createEntrySetIterator() {
      return (Iterator<Map.Entry<K, V>>)(this.isEmpty() ? EmptyIterator.emptyIterator() : new EntrySetIterator(this));
   }

   protected Iterator<K> createKeySetIterator() {
      return (Iterator<K>)(this.isEmpty() ? EmptyIterator.emptyIterator() : new KeySetIterator(this));
   }

   protected Iterator<V> createValuesIterator() {
      return (Iterator<V>)(this.isEmpty() ? EmptyIterator.emptyIterator() : new ValuesIterator(this));
   }

   protected void destroyEntry(HashEntry<K, V> entry) {
      entry.next = null;
      entry.key = null;
      entry.value = null;
   }

   protected void doReadObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      this.loadFactor = in.readFloat();
      int capacity = in.readInt();
      int size = in.readInt();
      this.init();
      this.threshold = this.calculateThreshold(capacity, this.loadFactor);
      this.data = new HashEntry[capacity];

      for(int i = 0; i < size; ++i) {
         K key = (K)in.readObject();
         V value = (V)in.readObject();
         this.put(key, value);
      }

   }

   protected void doWriteObject(ObjectOutputStream out) throws IOException {
      out.writeFloat(this.loadFactor);
      out.writeInt(this.data.length);
      out.writeInt(this.size);
      MapIterator<K, V> it = this.mapIterator();

      while(it.hasNext()) {
         out.writeObject(it.next());
         out.writeObject(it.getValue());
      }

   }

   protected void ensureCapacity(int newCapacity) {
      int oldCapacity = this.data.length;
      if (newCapacity > oldCapacity) {
         if (this.size == 0) {
            this.threshold = this.calculateThreshold(newCapacity, this.loadFactor);
            this.data = new HashEntry[newCapacity];
         } else {
            HashEntry<K, V>[] oldEntries = this.data;
            HashEntry<K, V>[] newEntries = new HashEntry[newCapacity];
            ++this.modCount;

            for(int i = oldCapacity - 1; i >= 0; --i) {
               HashEntry<K, V> entry = oldEntries[i];
               if (entry != null) {
                  oldEntries[i] = null;

                  while(true) {
                     HashEntry<K, V> next = entry.next;
                     int index = this.hashIndex(entry.hashCode, newCapacity);
                     entry.next = newEntries[index];
                     newEntries[index] = entry;
                     entry = next;
                     if (next == null) {
                        break;
                     }
                  }
               }
            }

            this.threshold = this.calculateThreshold(newCapacity, this.loadFactor);
            this.data = newEntries;
         }

      }
   }

   protected int entryHashCode(HashEntry<K, V> entry) {
      return entry.hashCode;
   }

   protected K entryKey(HashEntry<K, V> entry) {
      return entry.getKey();
   }

   protected HashEntry<K, V> entryNext(HashEntry<K, V> entry) {
      return entry.next;
   }

   public Set<Map.Entry<K, V>> entrySet() {
      if (this.entrySet == null) {
         this.entrySet = new EntrySet<K, V>(this);
      }

      return this.entrySet;
   }

   protected V entryValue(HashEntry<K, V> entry) {
      return entry.getValue();
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof Map)) {
         return false;
      } else {
         Map<?, ?> map = (Map)obj;
         if (map.size() != this.size()) {
            return false;
         } else {
            MapIterator<?, ?> it = this.mapIterator();

            try {
               while(it.hasNext()) {
                  Object key = it.next();
                  Object value = it.getValue();
                  if (value == null) {
                     if (map.get(key) != null || !map.containsKey(key)) {
                        return false;
                     }
                  } else if (!value.equals(map.get(key))) {
                     return false;
                  }
               }

               return true;
            } catch (NullPointerException | ClassCastException var6) {
               return false;
            }
         }
      }
   }

   public V get(Object key) {
      key = this.convertKey(key);
      int hashCode = this.hash(key);

      for(HashEntry<K, V> entry = this.data[this.hashIndex(hashCode, this.data.length)]; entry != null; entry = entry.next) {
         if (entry.hashCode == hashCode && this.isEqualKey(key, entry.key)) {
            return entry.getValue();
         }
      }

      return null;
   }

   protected HashEntry<K, V> getEntry(Object key) {
      key = this.convertKey(key);
      int hashCode = this.hash(key);

      for(HashEntry<K, V> entry = this.data[this.hashIndex(hashCode, this.data.length)]; entry != null; entry = entry.next) {
         if (entry.hashCode == hashCode && this.isEqualKey(key, entry.key)) {
            return entry;
         }
      }

      return null;
   }

   protected int hash(Object key) {
      int h = key.hashCode();
      h += ~(h << 9);
      h ^= h >>> 14;
      h += h << 4;
      h ^= h >>> 10;
      return h;
   }

   public int hashCode() {
      int total = 0;

      for(Iterator<Map.Entry<K, V>> it = this.createEntrySetIterator(); it.hasNext(); total += ((Map.Entry)it.next()).hashCode()) {
      }

      return total;
   }

   protected int hashIndex(int hashCode, int dataSize) {
      return hashCode & dataSize - 1;
   }

   protected void init() {
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   protected boolean isEqualKey(Object key1, Object key2) {
      return Objects.equals(key1, key2);
   }

   protected boolean isEqualValue(Object value1, Object value2) {
      return Objects.equals(value1, value2);
   }

   public Set<K> keySet() {
      if (this.keySet == null) {
         this.keySet = new KeySet<K>(this);
      }

      return this.keySet;
   }

   public MapIterator<K, V> mapIterator() {
      return (MapIterator<K, V>)(this.size == 0 ? EmptyMapIterator.emptyMapIterator() : new HashMapIterator(this));
   }

   public V put(K key, V value) {
      Object convertedKey = this.convertKey(key);
      int hashCode = this.hash(convertedKey);
      int index = this.hashIndex(hashCode, this.data.length);

      for(HashEntry<K, V> entry = this.data[index]; entry != null; entry = entry.next) {
         if (entry.hashCode == hashCode && this.isEqualKey(convertedKey, entry.key)) {
            V oldValue = entry.getValue();
            this.updateEntry(entry, value);
            return oldValue;
         }
      }

      this.addMapping(index, hashCode, key, value);
      return null;
   }

   public void putAll(Map<? extends K, ? extends V> map) {
      int mapSize = map.size();
      if (mapSize != 0) {
         int newSize = (int)((float)(this.size + mapSize) / this.loadFactor + 1.0F);
         this.ensureCapacity(this.calculateNewCapacity(newSize));

         for(Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
         }

      }
   }

   public V remove(Object key) {
      key = this.convertKey(key);
      int hashCode = this.hash(key);
      int index = this.hashIndex(hashCode, this.data.length);
      HashEntry<K, V> entry = this.data[index];

      for(HashEntry<K, V> previous = null; entry != null; entry = entry.next) {
         if (entry.hashCode == hashCode && this.isEqualKey(key, entry.key)) {
            V oldValue = entry.getValue();
            this.removeMapping(entry, index, previous);
            return oldValue;
         }

         previous = entry;
      }

      return null;
   }

   protected void removeEntry(HashEntry<K, V> entry, int hashIndex, HashEntry<K, V> previous) {
      if (previous == null) {
         this.data[hashIndex] = entry.next;
      } else {
         previous.next = entry.next;
      }

   }

   protected void removeMapping(HashEntry<K, V> entry, int hashIndex, HashEntry<K, V> previous) {
      ++this.modCount;
      this.removeEntry(entry, hashIndex, previous);
      --this.size;
      this.destroyEntry(entry);
   }

   protected void reuseEntry(HashEntry<K, V> entry, int hashIndex, int hashCode, K key, V value) {
      entry.next = this.data[hashIndex];
      entry.hashCode = hashCode;
      entry.key = key;
      entry.value = value;
   }

   public int size() {
      return this.size;
   }

   public String toString() {
      if (this.isEmpty()) {
         return "{}";
      } else {
         StringBuilder buf = new StringBuilder(32 * this.size());
         buf.append('{');
         MapIterator<K, V> it = this.mapIterator();
         boolean hasNext = it.hasNext();

         while(hasNext) {
            K key = it.next();
            V value = it.getValue();
            buf.append(key == this ? "(this Map)" : key).append('=').append(value == this ? "(this Map)" : value);
            hasNext = it.hasNext();
            if (hasNext) {
               buf.append(",").append(' ');
            }
         }

         buf.append('}');
         return buf.toString();
      }
   }

   protected void updateEntry(HashEntry<K, V> entry, V newValue) {
      entry.setValue(newValue);
   }

   public Collection<V> values() {
      if (this.values == null) {
         this.values = new Values<V>(this);
      }

      return this.values;
   }

   protected static class EntrySet<K, V> extends AbstractSet<Map.Entry<K, V>> {
      private final AbstractHashedMap<K, V> parent;

      protected EntrySet(AbstractHashedMap<K, V> parent) {
         this.parent = parent;
      }

      public void clear() {
         this.parent.clear();
      }

      public boolean contains(Object entry) {
         if (!(entry instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry<?, ?> e = (Map.Entry)entry;
            Map.Entry<K, V> match = this.parent.getEntry(e.getKey());
            return match != null && match.equals(e);
         }
      }

      public Iterator<Map.Entry<K, V>> iterator() {
         return this.parent.createEntrySetIterator();
      }

      public boolean remove(Object obj) {
         if (!(obj instanceof Map.Entry)) {
            return false;
         } else if (!this.contains(obj)) {
            return false;
         } else {
            Map.Entry<?, ?> entry = (Map.Entry)obj;
            this.parent.remove(entry.getKey());
            return true;
         }
      }

      public int size() {
         return this.parent.size();
      }
   }

   protected static class EntrySetIterator<K, V> extends HashIterator<K, V> implements Iterator<Map.Entry<K, V>> {
      protected EntrySetIterator(AbstractHashedMap<K, V> parent) {
         super(parent);
      }

      public Map.Entry<K, V> next() {
         return super.nextEntry();
      }
   }

   protected static class HashEntry<K, V> implements Map.Entry<K, V>, KeyValue<K, V> {
      protected HashEntry<K, V> next;
      protected int hashCode;
      protected Object key;
      protected Object value;

      protected HashEntry(HashEntry<K, V> next, int hashCode, Object key, V value) {
         this.next = next;
         this.hashCode = hashCode;
         this.key = key;
         this.value = value;
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry<?, ?> other = (Map.Entry)obj;
            return Objects.equals(this.getKey(), other.getKey()) && Objects.equals(this.getValue(), other.getValue());
         }
      }

      public K getKey() {
         return (K)(this.key == AbstractHashedMap.NULL ? null : this.key);
      }

      public V getValue() {
         return (V)this.value;
      }

      public int hashCode() {
         return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
      }

      public V setValue(V value) {
         Object old = this.value;
         this.value = value;
         return (V)old;
      }

      public String toString() {
         return "" + this.getKey() + '=' + this.getValue();
      }
   }

   protected abstract static class HashIterator<K, V> {
      private final AbstractHashedMap<K, V> parent;
      private int hashIndex;
      private HashEntry<K, V> last;
      private HashEntry<K, V> next;
      private int expectedModCount;

      protected HashIterator(AbstractHashedMap<K, V> parent) {
         this.parent = parent;
         HashEntry<K, V>[] data = parent.data;
         int i = data.length;

         HashEntry<K, V> next;
         for(next = null; i > 0 && next == null; next = data[i]) {
            --i;
         }

         this.next = next;
         this.hashIndex = i;
         this.expectedModCount = parent.modCount;
      }

      protected HashEntry<K, V> currentEntry() {
         return this.last;
      }

      public boolean hasNext() {
         return this.next != null;
      }

      protected HashEntry<K, V> nextEntry() {
         if (this.parent.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            HashEntry<K, V> newCurrent = this.next;
            if (newCurrent == null) {
               throw new NoSuchElementException("No next() entry in the iteration");
            } else {
               HashEntry<K, V>[] data = this.parent.data;
               int i = this.hashIndex;

               HashEntry<K, V> n;
               for(n = newCurrent.next; n == null && i > 0; n = data[i]) {
                  --i;
               }

               this.next = n;
               this.hashIndex = i;
               this.last = newCurrent;
               return newCurrent;
            }
         }
      }

      public void remove() {
         if (this.last == null) {
            throw new IllegalStateException("remove() can only be called once after next()");
         } else if (this.parent.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            this.parent.remove(this.last.getKey());
            this.last = null;
            this.expectedModCount = this.parent.modCount;
         }
      }

      public String toString() {
         return this.last != null ? "Iterator[" + this.last.getKey() + "=" + this.last.getValue() + "]" : "Iterator[]";
      }
   }

   protected static class HashMapIterator<K, V> extends HashIterator<K, V> implements MapIterator<K, V> {
      protected HashMapIterator(AbstractHashedMap<K, V> parent) {
         super(parent);
      }

      public K getKey() {
         HashEntry<K, V> current = this.currentEntry();
         if (current == null) {
            throw new IllegalStateException("getKey() can only be called after next() and before remove()");
         } else {
            return current.getKey();
         }
      }

      public V getValue() {
         HashEntry<K, V> current = this.currentEntry();
         if (current == null) {
            throw new IllegalStateException("getValue() can only be called after next() and before remove()");
         } else {
            return current.getValue();
         }
      }

      public K next() {
         return (K)super.nextEntry().getKey();
      }

      public V setValue(V value) {
         HashEntry<K, V> current = this.currentEntry();
         if (current == null) {
            throw new IllegalStateException("setValue() can only be called after next() and before remove()");
         } else {
            return current.setValue(value);
         }
      }
   }

   protected static class KeySet<K> extends AbstractSet<K> {
      private final AbstractHashedMap<K, ?> parent;

      protected KeySet(AbstractHashedMap<K, ?> parent) {
         this.parent = parent;
      }

      public void clear() {
         this.parent.clear();
      }

      public boolean contains(Object key) {
         return this.parent.containsKey(key);
      }

      public Iterator<K> iterator() {
         return this.parent.createKeySetIterator();
      }

      public boolean remove(Object key) {
         boolean result = this.parent.containsKey(key);
         this.parent.remove(key);
         return result;
      }

      public int size() {
         return this.parent.size();
      }
   }

   protected static class KeySetIterator<K> extends HashIterator<K, Object> implements Iterator<K> {
      protected KeySetIterator(AbstractHashedMap<K, ?> parent) {
         super(parent);
      }

      public K next() {
         return (K)super.nextEntry().getKey();
      }
   }

   protected static class Values<V> extends AbstractCollection<V> {
      private final AbstractHashedMap<?, V> parent;

      protected Values(AbstractHashedMap<?, V> parent) {
         this.parent = parent;
      }

      public void clear() {
         this.parent.clear();
      }

      public boolean contains(Object value) {
         return this.parent.containsValue(value);
      }

      public Iterator<V> iterator() {
         return this.parent.createValuesIterator();
      }

      public int size() {
         return this.parent.size();
      }
   }

   protected static class ValuesIterator<V> extends HashIterator<Object, V> implements Iterator<V> {
      protected ValuesIterator(AbstractHashedMap<?, V> parent) {
         super(parent);
      }

      public V next() {
         return (V)super.nextEntry().getValue();
      }
   }
}
