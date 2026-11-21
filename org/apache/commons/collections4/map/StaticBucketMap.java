package org.apache.commons.collections4.map;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.collections4.KeyValue;

public final class StaticBucketMap<K, V> extends AbstractIterableMap<K, V> {
   private static final int DEFAULT_BUCKETS = 255;
   private final Node<K, V>[] buckets;
   private final Lock[] locks;

   public StaticBucketMap() {
      this(255);
   }

   public StaticBucketMap(int numBuckets) {
      int size = Math.max(17, numBuckets);
      if (size % 2 == 0) {
         --size;
      }

      this.buckets = new Node[size];
      this.locks = new Lock[size];

      for(int i = 0; i < size; ++i) {
         this.locks[i] = new Lock();
      }

   }

   public void atomic(Runnable runnable) {
      this.atomic((Runnable)Objects.requireNonNull(runnable, "runnable"), 0);
   }

   private void atomic(Runnable r, int bucket) {
      if (bucket >= this.buckets.length) {
         r.run();
      } else {
         synchronized(this.locks[bucket]) {
            this.atomic(r, bucket + 1);
         }
      }
   }

   public void clear() {
      for(int i = 0; i < this.buckets.length; ++i) {
         Lock lock = this.locks[i];
         synchronized(lock) {
            this.buckets[i] = null;
            lock.size = 0;
         }
      }

   }

   public boolean containsKey(Object key) {
      int hash = this.getHash(key);
      synchronized(this.locks[hash]) {
         for(Node<K, V> n = this.buckets[hash]; n != null; n = n.next) {
            if (Objects.equals(n.key, key)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean containsValue(Object value) {
      for(int i = 0; i < this.buckets.length; ++i) {
         synchronized(this.locks[i]) {
            for(Node<K, V> n = this.buckets[i]; n != null; n = n.next) {
               if (Objects.equals(n.value, value)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public Set<Map.Entry<K, V>> entrySet() {
      return new EntrySet();
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof Map)) {
         return false;
      } else {
         Map<?, ?> other = (Map)obj;
         return this.entrySet().equals(other.entrySet());
      }
   }

   public V get(Object key) {
      int hash = this.getHash(key);
      synchronized(this.locks[hash]) {
         for(Node<K, V> n = this.buckets[hash]; n != null; n = n.next) {
            if (Objects.equals(n.key, key)) {
               return n.value;
            }
         }

         return null;
      }
   }

   private int getHash(Object key) {
      if (key == null) {
         return 0;
      } else {
         int hash = key.hashCode();
         hash += ~(hash << 15);
         hash ^= hash >>> 10;
         hash += hash << 3;
         hash ^= hash >>> 6;
         hash += ~(hash << 11);
         hash ^= hash >>> 16;
         hash %= this.buckets.length;
         return hash < 0 ? hash * -1 : hash;
      }
   }

   public int hashCode() {
      int hashCode = 0;

      for(int i = 0; i < this.buckets.length; ++i) {
         synchronized(this.locks[i]) {
            for(Node<K, V> n = this.buckets[i]; n != null; n = n.next) {
               hashCode += n.hashCode();
            }
         }
      }

      return hashCode;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public Set<K> keySet() {
      return new KeySet();
   }

   public V put(K key, V value) {
      int hash = this.getHash(key);
      synchronized(this.locks[hash]) {
         Node<K, V> n = this.buckets[hash];
         if (n == null) {
            n = new Node<K, V>();
            n.key = key;
            n.value = value;
            this.buckets[hash] = n;
            ++this.locks[hash].size;
            return null;
         } else {
            for(Node<K, V> next = n; next != null; next = next.next) {
               n = next;
               if (Objects.equals(next.key, key)) {
                  V returnVal = next.value;
                  next.value = value;
                  return returnVal;
               }
            }

            Node<K, V> newNode = new Node<K, V>();
            newNode.key = key;
            newNode.value = value;
            n.next = newNode;
            ++this.locks[hash].size;
            return null;
         }
      }
   }

   public void putAll(Map<? extends K, ? extends V> map) {
      for(Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
         this.put(entry.getKey(), entry.getValue());
      }

   }

   public V remove(Object key) {
      int hash = this.getHash(key);
      synchronized(this.locks[hash]) {
         Node<K, V> n = this.buckets[hash];

         for(Node<K, V> prev = null; n != null; n = n.next) {
            if (Objects.equals(n.key, key)) {
               if (null == prev) {
                  this.buckets[hash] = n.next;
               } else {
                  prev.next = n.next;
               }

               --this.locks[hash].size;
               return n.value;
            }

            prev = n;
         }

         return null;
      }
   }

   public int size() {
      int cnt = 0;

      for(int i = 0; i < this.buckets.length; ++i) {
         synchronized(this.locks[i]) {
            cnt += this.locks[i].size;
         }
      }

      return cnt;
   }

   public Collection<V> values() {
      return new Values();
   }

   class BaseIterator {
      private final ArrayList<Map.Entry<K, V>> current = new ArrayList();
      private int bucket;
      private Map.Entry<K, V> last;

      public boolean hasNext() {
         if (!this.current.isEmpty()) {
            return true;
         } else {
            while(this.bucket < StaticBucketMap.this.buckets.length) {
               synchronized(StaticBucketMap.this.locks[this.bucket]) {
                  for(Node<K, V> n = StaticBucketMap.this.buckets[this.bucket]; n != null; n = n.next) {
                     this.current.add(n);
                  }

                  ++this.bucket;
                  if (!this.current.isEmpty()) {
                     return true;
                  }
               }
            }

            return false;
         }
      }

      protected Map.Entry<K, V> nextEntry() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.last = (Map.Entry)this.current.remove(this.current.size() - 1);
            return this.last;
         }
      }

      public void remove() {
         if (this.last == null) {
            throw new IllegalStateException();
         } else {
            StaticBucketMap.this.remove(this.last.getKey());
            this.last = null;
         }
      }
   }

   private final class EntryIterator extends StaticBucketMap<K, V>.BaseIterator implements Iterator<Map.Entry<K, V>> {
      private EntryIterator() {
      }

      public Map.Entry<K, V> next() {
         return this.nextEntry();
      }
   }

   private final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
      private EntrySet() {
      }

      public void clear() {
         StaticBucketMap.this.clear();
      }

      public boolean contains(Object obj) {
         Map.Entry<?, ?> entry = (Map.Entry)obj;
         int hash = StaticBucketMap.this.getHash(entry.getKey());
         synchronized(StaticBucketMap.this.locks[hash]) {
            for(Node<K, V> n = StaticBucketMap.this.buckets[hash]; n != null; n = n.next) {
               if (n.equals(entry)) {
                  return true;
               }
            }

            return false;
         }
      }

      public Iterator<Map.Entry<K, V>> iterator() {
         return StaticBucketMap.this.new EntryIterator();
      }

      public boolean remove(Object obj) {
         if (!(obj instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry<?, ?> entry = (Map.Entry)obj;
            int hash = StaticBucketMap.this.getHash(entry.getKey());
            synchronized(StaticBucketMap.this.locks[hash]) {
               for(Node<K, V> n = StaticBucketMap.this.buckets[hash]; n != null; n = n.next) {
                  if (n.equals(entry)) {
                     StaticBucketMap.this.remove(n.getKey());
                     return true;
                  }
               }

               return false;
            }
         }
      }

      public int size() {
         return StaticBucketMap.this.size();
      }
   }

   private final class KeyIterator extends StaticBucketMap<K, V>.BaseIterator implements Iterator<K> {
      private KeyIterator() {
      }

      public K next() {
         return (K)this.nextEntry().getKey();
      }
   }

   private final class KeySet extends AbstractSet<K> {
      private KeySet() {
      }

      public void clear() {
         StaticBucketMap.this.clear();
      }

      public boolean contains(Object obj) {
         return StaticBucketMap.this.containsKey(obj);
      }

      public Iterator<K> iterator() {
         return StaticBucketMap.this.new KeyIterator();
      }

      public boolean remove(Object obj) {
         int hash = StaticBucketMap.this.getHash(obj);
         synchronized(StaticBucketMap.this.locks[hash]) {
            for(Node<K, V> n = StaticBucketMap.this.buckets[hash]; n != null; n = n.next) {
               Object k = n.getKey();
               if (Objects.equals(k, obj)) {
                  StaticBucketMap.this.remove(k);
                  return true;
               }
            }

            return false;
         }
      }

      public int size() {
         return StaticBucketMap.this.size();
      }
   }

   private static final class Lock {
      public int size;

      private Lock() {
      }
   }

   private static final class Node<K, V> implements Map.Entry<K, V>, KeyValue<K, V> {
      protected K key;
      protected V value;
      protected Node<K, V> next;

      private Node() {
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry<?, ?> e2 = (Map.Entry)obj;
            return Objects.equals(this.key, e2.getKey()) && Objects.equals(this.value, e2.getValue());
         }
      }

      public K getKey() {
         return this.key;
      }

      public V getValue() {
         return this.value;
      }

      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
      }

      public V setValue(V value) {
         V old = this.value;
         this.value = value;
         return old;
      }
   }

   private final class ValueIterator extends StaticBucketMap<K, V>.BaseIterator implements Iterator<V> {
      private ValueIterator() {
      }

      public V next() {
         return (V)this.nextEntry().getValue();
      }
   }

   private final class Values extends AbstractCollection<V> {
      private Values() {
      }

      public void clear() {
         StaticBucketMap.this.clear();
      }

      public Iterator<V> iterator() {
         return StaticBucketMap.this.new ValueIterator();
      }

      public int size() {
         return StaticBucketMap.this.size();
      }
   }
}
