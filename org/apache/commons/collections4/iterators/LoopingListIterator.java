package org.apache.commons.collections4.iterators;

import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.apache.commons.collections4.ResettableListIterator;

public class LoopingListIterator<E> implements ResettableListIterator<E> {
   private final List<E> list;
   private ListIterator<E> iterator;

   public LoopingListIterator(List<E> list) {
      this.list = (List)Objects.requireNonNull(list, "collection");
      this.init();
   }

   public void add(E obj) {
      this.iterator.add(obj);
   }

   public boolean hasNext() {
      return !this.list.isEmpty();
   }

   public boolean hasPrevious() {
      return !this.list.isEmpty();
   }

   private void init() {
      this.iterator = this.list.listIterator();
   }

   public E next() {
      if (this.list.isEmpty()) {
         throw new NoSuchElementException("There are no elements for this iterator to loop on");
      } else {
         if (!this.iterator.hasNext()) {
            this.reset();
         }

         return (E)this.iterator.next();
      }
   }

   public int nextIndex() {
      if (this.list.isEmpty()) {
         throw new NoSuchElementException("There are no elements for this iterator to loop on");
      } else {
         return !this.iterator.hasNext() ? 0 : this.iterator.nextIndex();
      }
   }

   public E previous() {
      if (this.list.isEmpty()) {
         throw new NoSuchElementException("There are no elements for this iterator to loop on");
      } else if (this.iterator.hasPrevious()) {
         return (E)this.iterator.previous();
      } else {
         E result;
         for(result = null; this.iterator.hasNext(); result = (E)this.iterator.next()) {
         }

         this.iterator.previous();
         return result;
      }
   }

   public int previousIndex() {
      if (this.list.isEmpty()) {
         throw new NoSuchElementException("There are no elements for this iterator to loop on");
      } else {
         return !this.iterator.hasPrevious() ? this.list.size() - 1 : this.iterator.previousIndex();
      }
   }

   public void remove() {
      this.iterator.remove();
   }

   public void reset() {
      this.init();
   }

   public void set(E obj) {
      this.iterator.set(obj);
   }

   public int size() {
      return this.list.size();
   }
}
