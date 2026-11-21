package org.apache.commons.collections4.iterators;

import java.util.NoSuchElementException;
import org.apache.commons.collections4.ResettableIterator;

public class SingletonIterator<E> implements ResettableIterator<E> {
   private final boolean removeAllowed;
   private boolean beforeFirst;
   private boolean removed;
   private E object;

   public SingletonIterator(E object) {
      this(object, true);
   }

   public SingletonIterator(E object, boolean removeAllowed) {
      this.beforeFirst = true;
      this.object = object;
      this.removeAllowed = removeAllowed;
   }

   public boolean hasNext() {
      return this.beforeFirst && !this.removed;
   }

   public E next() {
      if (this.beforeFirst && !this.removed) {
         this.beforeFirst = false;
         return this.object;
      } else {
         throw new NoSuchElementException();
      }
   }

   public void remove() {
      if (!this.removeAllowed) {
         throw new UnsupportedOperationException();
      } else if (!this.removed && !this.beforeFirst) {
         this.object = null;
         this.removed = true;
      } else {
         throw new IllegalStateException();
      }
   }

   public void reset() {
      this.beforeFirst = true;
   }
}
