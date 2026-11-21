package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class PeekingIterator<E> implements Iterator<E> {
   private final Iterator<? extends E> iterator;
   private boolean exhausted;
   private boolean slotFilled;
   private E slot;

   public static <E> PeekingIterator<E> peekingIterator(Iterator<? extends E> iterator) {
      Objects.requireNonNull(iterator, "iterator");
      if (iterator instanceof PeekingIterator) {
         PeekingIterator<E> it = (PeekingIterator)iterator;
         return it;
      } else {
         return new PeekingIterator<E>(iterator);
      }
   }

   public PeekingIterator(Iterator<? extends E> iterator) {
      this.iterator = iterator;
   }

   public E element() {
      this.fill();
      if (this.exhausted) {
         throw new NoSuchElementException();
      } else {
         return this.slot;
      }
   }

   private void fill() {
      if (!this.exhausted && !this.slotFilled) {
         if (this.iterator.hasNext()) {
            this.slot = (E)this.iterator.next();
            this.slotFilled = true;
         } else {
            this.exhausted = true;
            this.slot = null;
            this.slotFilled = false;
         }

      }
   }

   public boolean hasNext() {
      if (this.exhausted) {
         return false;
      } else {
         return this.slotFilled || this.iterator.hasNext();
      }
   }

   public E next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         E x = (E)(this.slotFilled ? this.slot : this.iterator.next());
         this.slot = null;
         this.slotFilled = false;
         return x;
      }
   }

   public E peek() {
      this.fill();
      return (E)(this.exhausted ? null : this.slot);
   }

   public void remove() {
      if (this.slotFilled) {
         throw new IllegalStateException("peek() or element() called before remove()");
      } else {
         this.iterator.remove();
      }
   }
}
