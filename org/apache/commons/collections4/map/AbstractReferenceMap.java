package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.DefaultMapEntry;

public abstract class AbstractReferenceMap<K, V> extends AbstractHashedMap<K, V> {
   private ReferenceStrength keyType;
   private ReferenceStrength valueType;
   private boolean purgeValues;
   private transient ReferenceQueue<Object> queue;

   protected AbstractReferenceMap() {
   }

   protected AbstractReferenceMap(ReferenceStrength keyType, ReferenceStrength valueType, int capacity, float loadFactor, boolean purgeValues) {
      super(capacity, loadFactor);
      this.keyType = keyType;
      this.valueType = valueType;
      this.purgeValues = purgeValues;
   }

   public void clear() {
      super.clear();

      while(this.queue.poll() != null) {
      }

   }

   public boolean containsKey(Object key) {
      this.purgeBeforeRead();
      Map.Entry<K, V> entry = this.getEntry(key);
      if (entry == null) {
         return false;
      } else {
         return entry.getValue() != null;
      }
   }

   public boolean containsValue(Object value) {
      this.purgeBeforeRead();
      return value == null ? false : super.containsValue(value);
   }

   protected ReferenceEntry<K, V> createEntry(AbstractHashedMap.HashEntry<K, V> next, int hashCode, K key, V value) {
      return new ReferenceEntry<K, V>(this, next, hashCode, key, value);
   }

   protected Iterator<Map.Entry<K, V>> createEntrySetIterator() {
      return new ReferenceEntrySetIterator(this);
   }

   protected Iterator<K> createKeySetIterator() {
      return new ReferenceKeySetIterator<K>(this);
   }

   protected Iterator<V> createValuesIterator() {
      return new ReferenceValuesIterator<V>(this);
   }

   protected void doReadObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      this.keyType = AbstractReferenceMap.ReferenceStrength.resolve(in.readInt());
      this.valueType = AbstractReferenceMap.ReferenceStrength.resolve(in.readInt());
      this.purgeValues = in.readBoolean();
      this.loadFactor = in.readFloat();
      int capacity = in.readInt();
      this.init();
      this.data = new AbstractHashedMap.HashEntry[capacity];
      this.threshold = this.calculateThreshold(this.data.length, this.loadFactor);

      while(true) {
         K key = (K)in.readObject();
         if (key == null) {
            return;
         }

         V value = (V)in.readObject();
         this.put(key, value);
      }
   }

   protected void doWriteObject(ObjectOutputStream out) throws IOException {
      out.writeInt(this.keyType.value);
      out.writeInt(this.valueType.value);
      out.writeBoolean(this.purgeValues);
      out.writeFloat(this.loadFactor);
      out.writeInt(this.data.length);
      MapIterator<K, V> it = this.mapIterator();

      while(it.hasNext()) {
         out.writeObject(it.next());
         out.writeObject(it.getValue());
      }

      out.writeObject((Object)null);
   }

   public Set<Map.Entry<K, V>> entrySet() {
      if (this.entrySet == null) {
         this.entrySet = new ReferenceEntrySet<K, V>(this);
      }

      return this.entrySet;
   }

   public V get(Object key) {
      this.purgeBeforeRead();
      Map.Entry<K, V> entry = this.getEntry(key);
      return (V)(entry == null ? null : entry.getValue());
   }

   protected AbstractHashedMap.HashEntry<K, V> getEntry(Object key) {
      return key == null ? null : super.getEntry(key);
   }

   protected int hashEntry(Object key, Object value) {
      return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
   }

   protected void init() {
      this.queue = new ReferenceQueue();
   }

   public boolean isEmpty() {
      this.purgeBeforeRead();
      return super.isEmpty();
   }

   protected boolean isEqualKey(Object key1, Object key2) {
      key2 = this.keyType == AbstractReferenceMap.ReferenceStrength.HARD ? key2 : ((Reference)key2).get();
      return Objects.equals(key1, key2);
   }

   protected boolean isKeyType(ReferenceStrength type) {
      return this.keyType == type;
   }

   protected boolean isValueType(ReferenceStrength type) {
      return this.valueType == type;
   }

   public Set<K> keySet() {
      if (this.keySet == null) {
         this.keySet = new ReferenceKeySet<K>(this);
      }

      return this.keySet;
   }

   public MapIterator<K, V> mapIterator() {
      return new ReferenceMapIterator<K, V>(this);
   }

   protected void purge() {
      for(Reference<?> ref = this.queue.poll(); ref != null; ref = this.queue.poll()) {
         this.purge(ref);
      }

   }

   protected void purge(Reference<?> ref) {
      int hash = ref.hashCode();
      int index = this.hashIndex(hash, this.data.length);
      AbstractHashedMap.HashEntry<K, V> previous = null;

      for(AbstractHashedMap.HashEntry<K, V> entry = this.data[index]; entry != null; entry = entry.next) {
         ReferenceEntry<K, V> refEntry = (ReferenceEntry)entry;
         if (refEntry.purge(ref)) {
            if (previous == null) {
               this.data[index] = entry.next;
            } else {
               previous.next = entry.next;
            }

            --this.size;
            refEntry.onPurge();
            return;
         }

         previous = entry;
      }

   }

   protected void purgeBeforeRead() {
      this.purge();
   }

   protected void purgeBeforeWrite() {
      this.purge();
   }

   public V put(K key, V value) {
      Objects.requireNonNull(key, "key");
      Objects.requireNonNull(value, "value");
      this.purgeBeforeWrite();
      return (V)super.put(key, value);
   }

   public V remove(Object key) {
      if (key == null) {
         return null;
      } else {
         this.purgeBeforeWrite();
         return (V)super.remove(key);
      }
   }

   public int size() {
      this.purgeBeforeRead();
      return super.size();
   }

   public Collection<V> values() {
      if (this.values == null) {
         this.values = new ReferenceValues<V>(this);
      }

      return this.values;
   }

   static class ReferenceBaseIterator<K, V> {
      final AbstractReferenceMap<K, V> parent;
      int index;
      ReferenceEntry<K, V> next;
      ReferenceEntry<K, V> current;
      K currentKey;
      K nextKey;
      V currentValue;
      V nextValue;
      int expectedModCount;

      ReferenceBaseIterator(AbstractReferenceMap<K, V> parent) {
         this.parent = parent;
         this.index = !parent.isEmpty() ? parent.data.length : 0;
         this.expectedModCount = parent.modCount;
      }

      private void checkMod() {
         if (this.parent.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         }
      }

      protected ReferenceEntry<K, V> currentEntry() {
         this.checkMod();
         return this.current;
      }

      public boolean hasNext() {
         this.checkMod();

         while(this.nextNull()) {
            ReferenceEntry<K, V> e = this.next;

            int i;
            for(i = this.index; e == null && i > 0; e = (ReferenceEntry)this.parent.data[i]) {
               --i;
            }

            this.next = e;
            this.index = i;
            if (e == null) {
               return false;
            }

            this.nextKey = e.getKey();
            this.nextValue = e.getValue();
            if (this.nextNull()) {
               this.next = this.next.next();
            }
         }

         return true;
      }

      protected ReferenceEntry<K, V> nextEntry() {
         this.checkMod();
         if (this.nextNull() && !this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.current = this.next;
            this.next = this.next.next();
            this.currentKey = this.nextKey;
            this.currentValue = this.nextValue;
            this.nextKey = null;
            this.nextValue = null;
            return this.current;
         }
      }

      private boolean nextNull() {
         return this.nextKey == null || this.nextValue == null;
      }

      public void remove() {
         this.checkMod();
         if (this.current == null) {
            throw new IllegalStateException();
         } else {
            this.parent.remove(this.currentKey);
            this.current = null;
            this.currentKey = null;
            this.currentValue = null;
            this.expectedModCount = this.parent.modCount;
         }
      }
   }

   protected static class ReferenceEntry<K, V> extends AbstractHashedMap.HashEntry<K, V> {
      private final AbstractReferenceMap<K, V> parent;

      public ReferenceEntry(AbstractReferenceMap<K, V> parent, AbstractHashedMap.HashEntry<K, V> next, int hashCode, K key, V value) {
         super(next, hashCode, (Object)null, (Object)null);
         this.parent = parent;
         this.key = this.toReference(parent.keyType, key, hashCode);
         this.value = this.toReference(parent.valueType, value, hashCode);
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry<?, ?> entry = (Map.Entry)obj;
            Object entryKey = entry.getKey();
            Object entryValue = entry.getValue();
            if (entryKey != null && entryValue != null) {
               return this.parent.isEqualKey(entryKey, this.key) && this.parent.isEqualValue(entryValue, this.getValue());
            } else {
               return false;
            }
         }
      }

      public K getKey() {
         return (K)(this.parent.keyType == AbstractReferenceMap.ReferenceStrength.HARD ? this.key : ((Reference)this.key).get());
      }

      public V getValue() {
         return (V)(this.parent.valueType == AbstractReferenceMap.ReferenceStrength.HARD ? this.value : ((Reference)this.value).get());
      }

      public int hashCode() {
         return this.parent.hashEntry(this.getKey(), this.getValue());
      }

      protected ReferenceEntry<K, V> next() {
         return (ReferenceEntry)this.next;
      }

      protected void nullValue() {
         this.value = null;
      }

      protected void onPurge() {
      }

      protected boolean purge(Reference<?> ref) {
         boolean r = this.parent.keyType != AbstractReferenceMap.ReferenceStrength.HARD && this.key == ref;
         r = r || this.parent.valueType != AbstractReferenceMap.ReferenceStrength.HARD && this.value == ref;
         if (r) {
            if (this.parent.keyType != AbstractReferenceMap.ReferenceStrength.HARD) {
               ((Reference)this.key).clear();
            }

            if (this.parent.valueType != AbstractReferenceMap.ReferenceStrength.HARD) {
               ((Reference)this.value).clear();
            } else if (this.parent.purgeValues) {
               this.nullValue();
            }
         }

         return r;
      }

      public V setValue(V value) {
         V old = (V)this.getValue();
         if (this.parent.valueType != AbstractReferenceMap.ReferenceStrength.HARD) {
            ((Reference)this.value).clear();
         }

         this.value = this.toReference(this.parent.valueType, value, this.hashCode);
         return old;
      }

      protected <T> Object toReference(ReferenceStrength type, T referent, int hash) {
         switch (type.ordinal()) {
            case 0:
               return referent;
            case 1:
               return new SoftRef(hash, referent, this.parent.queue);
            case 2:
               return new WeakRef(hash, referent, this.parent.queue);
            default:
               throw new Error();
         }
      }
   }

   static class ReferenceEntrySet<K, V> extends AbstractHashedMap.EntrySet<K, V> {
      protected ReferenceEntrySet(AbstractHashedMap<K, V> parent) {
         super(parent);
      }

      public Object[] toArray() {
         return this.toArray(new Object[this.size()]);
      }

      public <T> T[] toArray(T[] arr) {
         ArrayList<Map.Entry<K, V>> list = new ArrayList(this.size());

         for(Map.Entry<K, V> entry : this) {
            list.add(new DefaultMapEntry(entry));
         }

         return (T[])list.toArray(arr);
      }
   }

   static class ReferenceEntrySetIterator<K, V> extends ReferenceBaseIterator<K, V> implements Iterator<Map.Entry<K, V>> {
      ReferenceEntrySetIterator(AbstractReferenceMap<K, V> parent) {
         super(parent);
      }

      public Map.Entry<K, V> next() {
         return this.nextEntry();
      }
   }

   static class ReferenceKeySet<K> extends AbstractHashedMap.KeySet<K> {
      protected ReferenceKeySet(AbstractHashedMap<K, ?> parent) {
         super(parent);
      }

      public Object[] toArray() {
         return this.toArray(new Object[this.size()]);
      }

      public <T> T[] toArray(T[] arr) {
         List<K> list = new ArrayList(this.size());
         Objects.requireNonNull(list);
         this.forEach(list::add);
         return (T[])list.toArray(arr);
      }
   }

   static class ReferenceKeySetIterator<K> extends ReferenceBaseIterator<K, Object> implements Iterator<K> {
      ReferenceKeySetIterator(AbstractReferenceMap<K, ?> parent) {
         super(parent);
      }

      public K next() {
         return (K)this.nextEntry().getKey();
      }
   }

   static class ReferenceMapIterator<K, V> extends ReferenceBaseIterator<K, V> implements MapIterator<K, V> {
      protected ReferenceMapIterator(AbstractReferenceMap<K, V> parent) {
         super(parent);
      }

      public K getKey() {
         AbstractHashedMap.HashEntry<K, V> current = this.currentEntry();
         if (current == null) {
            throw new IllegalStateException("getKey() can only be called after next() and before remove()");
         } else {
            return current.getKey();
         }
      }

      public V getValue() {
         AbstractHashedMap.HashEntry<K, V> current = this.currentEntry();
         if (current == null) {
            throw new IllegalStateException("getValue() can only be called after next() and before remove()");
         } else {
            return current.getValue();
         }
      }

      public K next() {
         return (K)this.nextEntry().getKey();
      }

      public V setValue(V value) {
         AbstractHashedMap.HashEntry<K, V> current = this.currentEntry();
         if (current == null) {
            throw new IllegalStateException("setValue() can only be called after next() and before remove()");
         } else {
            return current.setValue(value);
         }
      }
   }

   public static enum ReferenceStrength {
      HARD(0),
      SOFT(1),
      WEAK(2);

      public final int value;

      public static ReferenceStrength resolve(int value) {
         switch (value) {
            case 0:
               return HARD;
            case 1:
               return SOFT;
            case 2:
               return WEAK;
            default:
               throw new IllegalArgumentException();
         }
      }

      private ReferenceStrength(final int value) {
         this.value = value;
      }

      // $FF: synthetic method
      private static ReferenceStrength[] $values() {
         return new ReferenceStrength[]{HARD, SOFT, WEAK};
      }
   }

   static class ReferenceValues<V> extends AbstractHashedMap.Values<V> {
      protected ReferenceValues(AbstractHashedMap<?, V> parent) {
         super(parent);
      }

      public Object[] toArray() {
         return this.toArray(new Object[this.size()]);
      }

      public <T> T[] toArray(T[] arr) {
         List<V> list = new ArrayList(this.size());
         Objects.requireNonNull(list);
         this.forEach(list::add);
         return (T[])list.toArray(arr);
      }
   }

   static class ReferenceValuesIterator<V> extends ReferenceBaseIterator<Object, V> implements Iterator<V> {
      ReferenceValuesIterator(AbstractReferenceMap<?, V> parent) {
         super(parent);
      }

      public V next() {
         return (V)this.nextEntry().getValue();
      }
   }

   static class SoftRef<T> extends SoftReference<T> {
      private final int hash;

      SoftRef(int hash, T r, ReferenceQueue<? super T> q) {
         super(r, q);
         this.hash = hash;
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            SoftRef<?> other = (SoftRef)obj;
            return this.hash == other.hash;
         }
      }

      public int hashCode() {
         return this.hash;
      }
   }

   static class WeakRef<T> extends WeakReference<T> {
      private final int hash;

      WeakRef(int hash, T r, ReferenceQueue<? super T> q) {
         super(r, q);
         this.hash = hash;
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            WeakRef<?> other = (WeakRef)obj;
            return this.hash == other.hash;
         }
      }

      public int hashCode() {
         return this.hash;
      }
   }
}
