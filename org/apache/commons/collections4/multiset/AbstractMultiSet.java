package org.apache.commons.collections4.multiset;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.Transformer;

public abstract class AbstractMultiSet<E> extends AbstractCollection<E> implements MultiSet<E> {
   private transient Set<E> uniqueSet;
   private transient Set<MultiSet.Entry<E>> entrySet;

   protected AbstractMultiSet() {
   }

   public boolean add(E object) {
      this.add(object, 1);
      return true;
   }

   public int add(E object, int occurrences) {
      throw new UnsupportedOperationException();
   }

   public void clear() {
      Iterator<MultiSet.Entry<E>> it = this.entrySet().iterator();

      while(it.hasNext()) {
         it.next();
         it.remove();
      }

   }

   public boolean contains(Object object) {
      return this.getCount(object) > 0;
   }

   protected Set<MultiSet.Entry<E>> createEntrySet() {
      return new EntrySet<MultiSet.Entry<E>>(this);
   }

   protected abstract Iterator<MultiSet.Entry<E>> createEntrySetIterator();

   protected Set<E> createUniqueSet() {
      return new UniqueSet<E>(this);
   }

   protected Iterator<E> createUniqueSetIterator() {
      Transformer<MultiSet.Entry<E>, E> transformer = MultiSet.Entry::getElement;
      return IteratorUtils.transformedIterator(this.entrySet().iterator(), transformer);
   }

   protected void doReadObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      int entrySize = in.readInt();

      for(int i = 0; i < entrySize; ++i) {
         E obj = (E)in.readObject();
         int count = in.readInt();
         this.setCount(obj, count);
      }

   }

   protected void doWriteObject(ObjectOutputStream out) throws IOException {
      out.writeInt(this.entrySet().size());

      for(MultiSet.Entry<E> entry : this.entrySet()) {
         out.writeObject(entry.getElement());
         out.writeInt(entry.getCount());
      }

   }

   public Set<MultiSet.Entry<E>> entrySet() {
      if (this.entrySet == null) {
         this.entrySet = this.createEntrySet();
      }

      return this.entrySet;
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
            for(MultiSet.Entry<E> entry : this.entrySet()) {
               if (other.getCount(entry.getElement()) != this.getCount(entry.getElement())) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public int getCount(Object object) {
      for(MultiSet.Entry<E> entry : this.entrySet()) {
         E element = entry.getElement();
         if (Objects.equals(element, object)) {
            return entry.getCount();
         }
      }

      return 0;
   }

   public int hashCode() {
      return this.entrySet().hashCode();
   }

   public Iterator<E> iterator() {
      return new MultiSetIterator<E>(this);
   }

   public boolean remove(Object object) {
      return this.remove(object, 1) != 0;
   }

   public int remove(Object object, int occurrences) {
      throw new UnsupportedOperationException();
   }

   public boolean removeAll(Collection<?> coll) {
      boolean result = false;

      for(Object obj : coll) {
         boolean changed = this.remove(obj, this.getCount(obj)) != 0;
         result = result || changed;
      }

      return result;
   }

   public int setCount(E object, int count) {
      if (count < 0) {
         throw new IllegalArgumentException("Count must not be negative.");
      } else {
         int oldCount = this.getCount(object);
         if (oldCount < count) {
            this.add(object, count - oldCount);
         } else {
            this.remove(object, oldCount - count);
         }

         return oldCount;
      }
   }

   public int size() {
      int totalSize = 0;

      for(MultiSet.Entry<E> entry : this.entrySet()) {
         totalSize += entry.getCount();
      }

      return totalSize;
   }

   public String toString() {
      return this.entrySet().toString();
   }

   protected abstract int uniqueElements();

   public Set<E> uniqueSet() {
      if (this.uniqueSet == null) {
         this.uniqueSet = this.createUniqueSet();
      }

      return this.uniqueSet;
   }

   protected abstract static class AbstractEntry<E> implements MultiSet.Entry<E> {
      public AbstractEntry() {
      }

      public boolean equals(Object object) {
         if (!(object instanceof MultiSet.Entry)) {
            return false;
         } else {
            MultiSet.Entry<?> other = (MultiSet.Entry)object;
            E element = (E)this.getElement();
            Object otherElement = other.getElement();
            return this.getCount() == other.getCount() && Objects.equals(element, otherElement);
         }
      }

      public int hashCode() {
         E element = (E)this.getElement();
         return (element == null ? 0 : element.hashCode()) ^ this.getCount();
      }

      public String toString() {
         return String.format("%s:%d", this.getElement(), this.getCount());
      }
   }

   protected static class EntrySet<E> extends AbstractSet<MultiSet.Entry<E>> {
      private final AbstractMultiSet<E> parent;

      protected EntrySet(AbstractMultiSet<E> parent) {
         this.parent = parent;
      }

      public boolean contains(Object obj) {
         if (!(obj instanceof MultiSet.Entry)) {
            return false;
         } else {
            MultiSet.Entry<?> entry = (MultiSet.Entry)obj;
            Object element = entry.getElement();
            return this.parent.getCount(element) == entry.getCount();
         }
      }

      public Iterator<MultiSet.Entry<E>> iterator() {
         return this.parent.createEntrySetIterator();
      }

      public boolean remove(Object obj) {
         if (!(obj instanceof MultiSet.Entry)) {
            return false;
         } else {
            MultiSet.Entry<?> entry = (MultiSet.Entry)obj;
            Object element = entry.getElement();
            if (this.parent.contains(element)) {
               int count = this.parent.getCount(element);
               if (entry.getCount() == count) {
                  this.parent.remove(element, count);
                  return true;
               }
            }

            return false;
         }
      }

      public int size() {
         return this.parent.uniqueElements();
      }
   }

   private static final class MultiSetIterator<E> implements Iterator<E> {
      private final AbstractMultiSet<E> parent;
      private final Iterator<MultiSet.Entry<E>> entryIterator;
      private MultiSet.Entry<E> current;
      private int itemCount;
      private boolean canRemove;

      MultiSetIterator(AbstractMultiSet<E> parent) {
         this.parent = parent;
         this.entryIterator = parent.entrySet().iterator();
         this.current = null;
         this.canRemove = false;
      }

      public boolean hasNext() {
         return this.itemCount > 0 || this.entryIterator.hasNext();
      }

      public E next() {
         if (this.itemCount == 0) {
            this.current = (MultiSet.Entry)this.entryIterator.next();
            this.itemCount = this.current.getCount();
         }

         this.canRemove = true;
         --this.itemCount;
         return this.current.getElement();
      }

      public void remove() {
         if (!this.canRemove) {
            throw new IllegalStateException();
         } else {
            int count = this.current.getCount();
            if (count > 1) {
               this.parent.remove(this.current.getElement());
            } else {
               this.entryIterator.remove();
            }

            this.canRemove = false;
         }
      }
   }

   protected static class UniqueSet<E> extends AbstractSet<E> {
      protected final AbstractMultiSet<E> parent;

      protected UniqueSet(AbstractMultiSet<E> parent) {
         this.parent = parent;
      }

      public void clear() {
         this.parent.clear();
      }

      public boolean contains(Object key) {
         return this.parent.contains(key);
      }

      public boolean containsAll(Collection<?> coll) {
         return this.parent.containsAll(coll);
      }

      public Iterator<E> iterator() {
         return this.parent.createUniqueSetIterator();
      }

      public boolean remove(Object key) {
         return this.parent.remove(key, this.parent.getCount(key)) != 0;
      }

      public int size() {
         return this.parent.uniqueElements();
      }
   }
}
