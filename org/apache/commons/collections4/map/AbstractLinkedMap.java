package org.apache.commons.collections4.map;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.apache.commons.collections4.OrderedIterator;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.iterators.EmptyOrderedIterator;
import org.apache.commons.collections4.iterators.EmptyOrderedMapIterator;

public abstract class AbstractLinkedMap<K, V> extends AbstractHashedMap<K, V> implements OrderedMap<K, V> {
   transient LinkEntry<K, V> header;

   protected AbstractLinkedMap() {
   }

   protected AbstractLinkedMap(int initialCapacity) {
      super(initialCapacity);
   }

   protected AbstractLinkedMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   protected AbstractLinkedMap(int initialCapacity, float loadFactor, int threshold) {
      super(initialCapacity, loadFactor, threshold);
   }

   protected AbstractLinkedMap(Map<? extends K, ? extends V> map) {
      super(map);
   }

   protected void addEntry(AbstractHashedMap.HashEntry<K, V> entry, int hashIndex) {
      LinkEntry<K, V> link = (LinkEntry)entry;
      link.after = this.header;
      link.before = this.header.before;
      this.header.before.after = link;
      this.header.before = link;
      this.data[hashIndex] = link;
   }

   public void clear() {
      super.clear();
      this.header.before = this.header.after = this.header;
   }

   public boolean containsValue(Object value) {
      if (value == null) {
         for(LinkEntry<K, V> entry = this.header.after; entry != this.header; entry = entry.after) {
            if (entry.getValue() == null) {
               return true;
            }
         }
      } else {
         for(LinkEntry<K, V> entry = this.header.after; entry != this.header; entry = entry.after) {
            if (this.isEqualValue(value, entry.getValue())) {
               return true;
            }
         }
      }

      return false;
   }

   protected LinkEntry<K, V> createEntry(AbstractHashedMap.HashEntry<K, V> next, int hashCode, K key, V value) {
      return new LinkEntry<K, V>(next, hashCode, this.convertKey(key), value);
   }

   protected Iterator<Map.Entry<K, V>> createEntrySetIterator() {
      return (Iterator<Map.Entry<K, V>>)(this.isEmpty() ? EmptyOrderedIterator.emptyOrderedIterator() : new EntrySetIterator(this));
   }

   protected Iterator<K> createKeySetIterator() {
      return (Iterator<K>)(this.isEmpty() ? EmptyOrderedIterator.emptyOrderedIterator() : new KeySetIterator(this));
   }

   protected Iterator<V> createValuesIterator() {
      return (Iterator<V>)(this.isEmpty() ? EmptyOrderedIterator.emptyOrderedIterator() : new ValuesIterator(this));
   }

   protected LinkEntry<K, V> entryAfter(LinkEntry<K, V> entry) {
      return entry.after;
   }

   protected LinkEntry<K, V> entryBefore(LinkEntry<K, V> entry) {
      return entry.before;
   }

   public K firstKey() {
      if (this.size == 0) {
         throw new NoSuchElementException("Map is empty");
      } else {
         return (K)this.header.after.getKey();
      }
   }

   protected LinkEntry<K, V> getEntry(int index) {
      if (index < 0) {
         throw new IndexOutOfBoundsException("Index " + index + " is less than zero");
      } else if (index >= this.size) {
         throw new IndexOutOfBoundsException("Index " + index + " is invalid for size " + this.size);
      } else {
         LinkEntry<K, V> entry;
         if (index < this.size / 2) {
            entry = this.header.after;

            for(int currentIndex = 0; currentIndex < index; ++currentIndex) {
               entry = entry.after;
            }
         } else {
            entry = this.header;

            for(int currentIndex = this.size; currentIndex > index; --currentIndex) {
               entry = entry.before;
            }
         }

         return entry;
      }
   }

   protected LinkEntry<K, V> getEntry(Object key) {
      return (LinkEntry)super.getEntry(key);
   }

   protected void init() {
      this.header = this.createEntry((AbstractHashedMap.HashEntry)null, -1, (Object)null, (Object)null);
      this.header.before = this.header.after = this.header;
   }

   public K lastKey() {
      if (this.size == 0) {
         throw new NoSuchElementException("Map is empty");
      } else {
         return (K)this.header.before.getKey();
      }
   }

   public OrderedMapIterator<K, V> mapIterator() {
      return (OrderedMapIterator<K, V>)(this.size == 0 ? EmptyOrderedMapIterator.emptyOrderedMapIterator() : new LinkMapIterator(this));
   }

   public K nextKey(Object key) {
      LinkEntry<K, V> entry = this.getEntry(key);
      return (K)(entry != null && entry.after != this.header ? entry.after.getKey() : null);
   }

   public K previousKey(Object key) {
      LinkEntry<K, V> entry = this.getEntry(key);
      return (K)(entry != null && entry.before != this.header ? entry.before.getKey() : null);
   }

   protected void removeEntry(AbstractHashedMap.HashEntry<K, V> entry, int hashIndex, AbstractHashedMap.HashEntry<K, V> previous) {
      LinkEntry<K, V> link = (LinkEntry)entry;
      link.before.after = link.after;
      link.after.before = link.before;
      link.after = null;
      link.before = null;
      super.removeEntry(entry, hashIndex, previous);
   }

   protected static class EntrySetIterator<K, V> extends LinkIterator<K, V> implements OrderedIterator<Map.Entry<K, V>>, ResettableIterator<Map.Entry<K, V>> {
      protected EntrySetIterator(AbstractLinkedMap<K, V> parent) {
         super(parent);
      }

      public Map.Entry<K, V> next() {
         return super.nextEntry();
      }

      public Map.Entry<K, V> previous() {
         return super.previousEntry();
      }
   }

   protected static class KeySetIterator<K> extends LinkIterator<K, Object> implements OrderedIterator<K>, ResettableIterator<K> {
      protected KeySetIterator(AbstractLinkedMap<K, ?> parent) {
         super(parent);
      }

      public K next() {
         return (K)super.nextEntry().getKey();
      }

      public K previous() {
         return (K)super.previousEntry().getKey();
      }
   }

   protected static class LinkEntry<K, V> extends AbstractHashedMap.HashEntry<K, V> {
      protected LinkEntry<K, V> before;
      protected LinkEntry<K, V> after;

      protected LinkEntry(AbstractHashedMap.HashEntry<K, V> next, int hashCode, Object key, V value) {
         super(next, hashCode, key, value);
      }
   }

   protected abstract static class LinkIterator<K, V> {
      protected final AbstractLinkedMap<K, V> parent;
      protected LinkEntry<K, V> last;
      protected LinkEntry<K, V> next;
      protected int expectedModCount;

      protected LinkIterator(AbstractLinkedMap<K, V> parent) {
         this.parent = (AbstractLinkedMap)Objects.requireNonNull(parent);
         this.next = parent.header.after;
         this.expectedModCount = parent.modCount;
      }

      protected LinkEntry<K, V> currentEntry() {
         return this.last;
      }

      public boolean hasNext() {
         return this.next != this.parent.header;
      }

      public boolean hasPrevious() {
         return this.next.before != this.parent.header;
      }

      protected LinkEntry<K, V> nextEntry() {
         if (this.parent.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else if (this.next == this.parent.header) {
            throw new NoSuchElementException("No next() entry in the iteration");
         } else {
            this.last = this.next;
            this.next = this.next.after;
            return this.last;
         }
      }

      protected LinkEntry<K, V> previousEntry() {
         if (this.parent.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            LinkEntry<K, V> previous = this.next.before;
            if (previous == this.parent.header) {
               throw new NoSuchElementException("No previous() entry in the iteration");
            } else {
               this.next = previous;
               this.last = previous;
               return this.last;
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

      public void reset() {
         this.last = null;
         this.next = this.parent.header.after;
      }

      public String toString() {
         return this.last != null ? "Iterator[" + this.last.getKey() + "=" + this.last.getValue() + "]" : "Iterator[]";
      }
   }

   protected static class LinkMapIterator<K, V> extends LinkIterator<K, V> implements OrderedMapIterator<K, V>, ResettableIterator<K> {
      protected LinkMapIterator(AbstractLinkedMap<K, V> parent) {
         super(parent);
      }

      public K getKey() {
         LinkEntry<K, V> current = this.currentEntry();
         if (current == null) {
            throw new IllegalStateException("getKey() can only be called after next() and before remove()");
         } else {
            return (K)current.getKey();
         }
      }

      public V getValue() {
         LinkEntry<K, V> current = this.currentEntry();
         if (current == null) {
            throw new IllegalStateException("getValue() can only be called after next() and before remove()");
         } else {
            return (V)current.getValue();
         }
      }

      public K next() {
         return (K)super.nextEntry().getKey();
      }

      public K previous() {
         return (K)super.previousEntry().getKey();
      }

      public V setValue(V value) {
         LinkEntry<K, V> current = this.currentEntry();
         if (current == null) {
            throw new IllegalStateException("setValue() can only be called after next() and before remove()");
         } else {
            return (V)current.setValue(value);
         }
      }
   }

   protected static class ValuesIterator<V> extends LinkIterator<Object, V> implements OrderedIterator<V>, ResettableIterator<V> {
      protected ValuesIterator(AbstractLinkedMap<?, V> parent) {
         super(parent);
      }

      public V next() {
         return (V)super.nextEntry().getValue();
      }

      public V previous() {
         return (V)super.previousEntry().getValue();
      }
   }
}
