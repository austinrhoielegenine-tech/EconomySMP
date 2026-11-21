package org.apache.commons.collections4.iterators;

import java.util.NoSuchElementException;
import org.apache.commons.collections4.ResettableListIterator;

public class ObjectArrayListIterator<E> extends ObjectArrayIterator<E> implements ResettableListIterator<E> {
   private int lastItemIndex = -1;

   public ObjectArrayListIterator(E... array) {
      super(array);
   }

   public ObjectArrayListIterator(E[] array, int start) {
      super(array, start);
   }

   public ObjectArrayListIterator(E[] array, int start, int end) {
      super(array, start, end);
   }

   public void add(E obj) {
      throw new UnsupportedOperationException("add() method is not supported");
   }

   public boolean hasPrevious() {
      return this.index > this.getStartIndex();
   }

   public E next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         this.lastItemIndex = this.index;
         return (E)this.array[this.index++];
      }
   }

   public int nextIndex() {
      return this.index - this.getStartIndex();
   }

   public E previous() {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         this.lastItemIndex = --this.index;
         return (E)this.array[this.index];
      }
   }

   public int previousIndex() {
      return this.index - this.getStartIndex() - 1;
   }

   public void reset() {
      super.reset();
      this.lastItemIndex = -1;
   }

   public void set(E obj) {
      if (this.lastItemIndex == -1) {
         throw new IllegalStateException("must call next() or previous() before a call to set()");
      } else {
         this.array[this.lastItemIndex] = obj;
      }
   }
}
