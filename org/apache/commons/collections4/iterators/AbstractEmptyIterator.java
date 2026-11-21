package org.apache.commons.collections4.iterators;

import java.util.NoSuchElementException;
import org.apache.commons.collections4.ResettableIterator;

abstract class AbstractEmptyIterator<E> implements ResettableIterator<E> {
   protected AbstractEmptyIterator() {
   }

   /** @deprecated */
   @Deprecated
   public void add(E ignored) {
      throw new UnsupportedOperationException("add() not supported for empty Iterator");
   }

   public boolean hasNext() {
      return false;
   }

   public boolean hasPrevious() {
      return false;
   }

   public E next() {
      throw new NoSuchElementException("Iterator contains no elements");
   }

   public int nextIndex() {
      return 0;
   }

   public E previous() {
      throw new NoSuchElementException("Iterator contains no elements");
   }

   public int previousIndex() {
      return -1;
   }

   public void remove() {
      throw new IllegalStateException("Iterator contains no elements");
   }

   public void reset() {
   }

   public void set(E ignored) {
      throw new IllegalStateException("Iterator contains no elements");
   }
}
