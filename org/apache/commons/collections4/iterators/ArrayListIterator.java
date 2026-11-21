package org.apache.commons.collections4.iterators;

import java.lang.reflect.Array;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.ResettableListIterator;

public class ArrayListIterator<E> extends ArrayIterator<E> implements ResettableListIterator<E> {
   private int lastItemIndex = -1;

   public ArrayListIterator(Object array) {
      super(array);
   }

   public ArrayListIterator(Object array, int startIndex) {
      super(array, startIndex);
   }

   public ArrayListIterator(Object array, int startIndex, int endIndex) {
      super(array, startIndex, endIndex);
   }

   public void add(Object o) {
      throw new UnsupportedOperationException("add() method is not supported");
   }

   public boolean hasPrevious() {
      return this.index > this.startIndex;
   }

   public E next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         this.lastItemIndex = this.index;
         return (E)Array.get(this.array, this.index++);
      }
   }

   public int nextIndex() {
      return this.index - this.startIndex;
   }

   public E previous() {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         this.lastItemIndex = --this.index;
         return (E)Array.get(this.array, this.index);
      }
   }

   public int previousIndex() {
      return this.index - this.startIndex - 1;
   }

   public void reset() {
      super.reset();
      this.lastItemIndex = -1;
   }

   public void set(Object o) {
      if (this.lastItemIndex == -1) {
         throw new IllegalStateException("must call next() or previous() before a call to set()");
      } else {
         Array.set(this.array, this.lastItemIndex, o);
      }
   }
}
