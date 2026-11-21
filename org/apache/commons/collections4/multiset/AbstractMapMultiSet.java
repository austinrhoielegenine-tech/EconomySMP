package org.apache.commons.collections4.multiset;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;

public abstract class AbstractMapMultiSet<E> extends AbstractMultiSet<E> {
   private transient Map<E, MutableInteger> map;
   private transient int size;
   private transient int modCount;

   protected AbstractMapMultiSet() {
   }

   protected AbstractMapMultiSet(Map<E, MutableInteger> map) {
      this.map = map;
   }

   public int add(E object, int occurrences) {
      if (occurrences < 0) {
         throw new IllegalArgumentException("Occurrences must not be negative.");
      } else {
         MutableInteger mut = (MutableInteger)this.map.get(object);
         int oldCount = mut != null ? mut.value : 0;
         if (occurrences > 0) {
            ++this.modCount;
            this.size += occurrences;
            if (mut == null) {
               this.map.put(object, new MutableInteger(occurrences));
            } else {
               mut.value += occurrences;
            }
         }

         return oldCount;
      }
   }

   public void clear() {
      ++this.modCount;
      this.map.clear();
      this.size = 0;
   }

   public boolean contains(Object object) {
      return this.map.containsKey(object);
   }

   protected Iterator<MultiSet.Entry<E>> createEntrySetIterator() {
      return new EntrySetIterator<MultiSet.Entry<E>>(this.map.entrySet().iterator(), this);
   }

   protected Iterator<E> createUniqueSetIterator() {
      return new UniqueSetIterator<E>(this.getMap().keySet().iterator(), this);
   }

   protected void doReadObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      int entrySize = in.readInt();

      for(int i = 0; i < entrySize; ++i) {
         E obj = (E)in.readObject();
         int count = in.readInt();
         this.map.put(obj, new MutableInteger(count));
         this.size += count;
      }

   }

   protected void doWriteObject(ObjectOutputStream out) throws IOException {
      out.writeInt(this.map.size());

      for(Map.Entry<E, MutableInteger> entry : this.map.entrySet()) {
         out.writeObject(entry.getKey());
         out.writeInt(((MutableInteger)entry.getValue()).value);
      }

   }

   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof MultiSet)) {
         return false;
      } else {
         MultiSet<?> other = (MultiSet)object;
         if (other.size() != this.size()) {
            return false;
         } else {
            for(E element : this.map.keySet()) {
               if (other.getCount(element) != this.getCount(element)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public int getCount(Object object) {
      MutableInteger count = (MutableInteger)this.map.get(object);
      return count != null ? count.value : 0;
   }

   protected Map<E, MutableInteger> getMap() {
      return this.map;
   }

   public int hashCode() {
      int total = 0;

      for(Map.Entry<E, MutableInteger> entry : this.map.entrySet()) {
         E element = (E)entry.getKey();
         MutableInteger count = (MutableInteger)entry.getValue();
         total += (element == null ? 0 : element.hashCode()) ^ count.value;
      }

      return total;
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public Iterator<E> iterator() {
      return new MapBasedMultiSetIterator<E>(this);
   }

   public int remove(Object object, int occurrences) {
      if (occurrences < 0) {
         throw new IllegalArgumentException("Occurrences must not be negative.");
      } else {
         MutableInteger mut = (MutableInteger)this.map.get(object);
         if (mut == null) {
            return 0;
         } else {
            int oldCount = mut.value;
            if (occurrences > 0) {
               ++this.modCount;
               if (occurrences < mut.value) {
                  mut.value -= occurrences;
                  this.size -= occurrences;
               } else {
                  this.map.remove(object);
                  this.size -= mut.value;
                  mut.value = 0;
               }
            }

            return oldCount;
         }
      }
   }

   protected void setMap(Map<E, MutableInteger> map) {
      this.map = map;
   }

   public int size() {
      return this.size;
   }

   public Object[] toArray() {
      Object[] result = new Object[this.size()];
      int i = 0;

      for(Map.Entry<E, MutableInteger> entry : this.map.entrySet()) {
         E current = (E)entry.getKey();
         MutableInteger count = (MutableInteger)entry.getValue();

         for(int index = count.value; index > 0; --index) {
            result[i++] = current;
         }
      }

      return result;
   }

   public <T> T[] toArray(T[] array) {
      int size = this.size();
      if (array.length < size) {
         T[] unchecked = (T[])((Object[])Array.newInstance(array.getClass().getComponentType(), size));
         array = unchecked;
      }

      int i = 0;

      for(Map.Entry<E, MutableInteger> entry : this.map.entrySet()) {
         E current = (E)entry.getKey();
         MutableInteger count = (MutableInteger)entry.getValue();

         for(int index = count.value; index > 0; --index) {
            array[i++] = current;
         }
      }

      while(i < array.length) {
         array[i++] = null;
      }

      return array;
   }

   protected int uniqueElements() {
      return this.map.size();
   }

   protected static class EntrySetIterator<E> implements Iterator<MultiSet.Entry<E>> {
      protected final AbstractMapMultiSet<E> parent;
      protected final Iterator<Map.Entry<E, MutableInteger>> decorated;
      protected MultiSet.Entry<E> last;
      protected boolean canRemove;

      protected EntrySetIterator(Iterator<Map.Entry<E, MutableInteger>> decorated, AbstractMapMultiSet<E> parent) {
         this.decorated = decorated;
         this.parent = parent;
      }

      public boolean hasNext() {
         return this.decorated.hasNext();
      }

      public MultiSet.Entry<E> next() {
         this.last = new MultiSetEntry<E>((Map.Entry)this.decorated.next());
         this.canRemove = true;
         return this.last;
      }

      public void remove() {
         if (!this.canRemove) {
            throw new IllegalStateException("Iterator remove() can only be called once after next()");
         } else {
            this.decorated.remove();
            this.last = null;
            this.canRemove = false;
         }
      }
   }

   private static final class MapBasedMultiSetIterator<E> implements Iterator<E> {
      private final AbstractMapMultiSet<E> parent;
      private final Iterator<Map.Entry<E, MutableInteger>> entryIterator;
      private Map.Entry<E, MutableInteger> current;
      private int itemCount;
      private final int mods;
      private boolean canRemove;

      MapBasedMultiSetIterator(AbstractMapMultiSet<E> parent) {
         this.parent = parent;
         this.entryIterator = parent.map.entrySet().iterator();
         this.current = null;
         this.mods = parent.modCount;
         this.canRemove = false;
      }

      public boolean hasNext() {
         return this.itemCount > 0 || this.entryIterator.hasNext();
      }

      public E next() {
         if (this.parent.modCount != this.mods) {
            throw new ConcurrentModificationException();
         } else {
            if (this.itemCount == 0) {
               this.current = (Map.Entry)this.entryIterator.next();
               this.itemCount = ((MutableInteger)this.current.getValue()).value;
            }

            this.canRemove = true;
            --this.itemCount;
            return (E)this.current.getKey();
         }
      }

      public void remove() {
         if (this.parent.modCount != this.mods) {
            throw new ConcurrentModificationException();
         } else if (!this.canRemove) {
            throw new IllegalStateException();
         } else {
            MutableInteger mut = (MutableInteger)this.current.getValue();
            if (mut.value > 1) {
               --mut.value;
            } else {
               this.entryIterator.remove();
            }

            this.parent.size--;
            this.canRemove = false;
         }
      }
   }

   protected static class MultiSetEntry<E> extends AbstractMultiSet.AbstractEntry<E> {
      protected final Map.Entry<E, MutableInteger> parentEntry;

      protected MultiSetEntry(Map.Entry<E, MutableInteger> parentEntry) {
         this.parentEntry = parentEntry;
      }

      public int getCount() {
         return ((MutableInteger)this.parentEntry.getValue()).value;
      }

      public E getElement() {
         return (E)this.parentEntry.getKey();
      }
   }

   protected static class MutableInteger {
      protected int value;

      MutableInteger(int value) {
         this.value = value;
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof MutableInteger)) {
            return false;
         } else {
            return ((MutableInteger)obj).value == this.value;
         }
      }

      public int hashCode() {
         return this.value;
      }
   }

   protected static class UniqueSetIterator<E> extends AbstractIteratorDecorator<E> {
      protected final AbstractMapMultiSet<E> parent;
      protected E lastElement;
      protected boolean canRemove;

      protected UniqueSetIterator(Iterator<E> iterator, AbstractMapMultiSet<E> parent) {
         super(iterator);
         this.parent = parent;
      }

      public E next() {
         this.lastElement = (E)super.next();
         this.canRemove = true;
         return this.lastElement;
      }

      public void remove() {
         if (!this.canRemove) {
            throw new IllegalStateException("Iterator remove() can only be called once after next()");
         } else {
            int count = this.parent.getCount(this.lastElement);
            super.remove();
            this.parent.remove(this.lastElement, count);
            this.lastElement = null;
            this.canRemove = false;
         }
      }
   }
}
