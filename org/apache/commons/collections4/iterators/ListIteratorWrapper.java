package org.apache.commons.collections4.iterators;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.apache.commons.collections4.ResettableListIterator;

public class ListIteratorWrapper<E> implements ResettableListIterator<E> {
   private static final String UNSUPPORTED_OPERATION_MESSAGE = "ListIteratorWrapper does not support optional operations of ListIterator.";
   private static final String CANNOT_REMOVE_MESSAGE = "Cannot remove element at index {0}.";
   private final Iterator<? extends E> iterator;
   private final List<E> list = new ArrayList();
   private int currentIndex;
   private int wrappedIteratorIndex;
   private boolean removeState;

   public ListIteratorWrapper(Iterator<? extends E> iterator) {
      this.iterator = (Iterator)Objects.requireNonNull(iterator, "iterator");
   }

   public void add(E obj) throws UnsupportedOperationException {
      if (this.iterator instanceof ListIterator) {
         ListIterator<E> li = (ListIterator)this.iterator;
         li.add(obj);
      } else {
         throw new UnsupportedOperationException("ListIteratorWrapper does not support optional operations of ListIterator.");
      }
   }

   public boolean hasNext() {
      return this.currentIndex != this.wrappedIteratorIndex && !(this.iterator instanceof ListIterator) ? true : this.iterator.hasNext();
   }

   public boolean hasPrevious() {
      if (this.iterator instanceof ListIterator) {
         ListIterator<?> li = (ListIterator)this.iterator;
         return li.hasPrevious();
      } else {
         return this.currentIndex > 0;
      }
   }

   public E next() throws NoSuchElementException {
      if (this.iterator instanceof ListIterator) {
         return (E)this.iterator.next();
      } else if (this.currentIndex < this.wrappedIteratorIndex) {
         ++this.currentIndex;
         return (E)this.list.get(this.currentIndex - 1);
      } else {
         E retval = (E)this.iterator.next();
         this.list.add(retval);
         ++this.currentIndex;
         ++this.wrappedIteratorIndex;
         this.removeState = true;
         return retval;
      }
   }

   public int nextIndex() {
      if (this.iterator instanceof ListIterator) {
         ListIterator<?> li = (ListIterator)this.iterator;
         return li.nextIndex();
      } else {
         return this.currentIndex;
      }
   }

   public E previous() throws NoSuchElementException {
      if (this.iterator instanceof ListIterator) {
         ListIterator<E> li = (ListIterator)this.iterator;
         return (E)li.previous();
      } else if (this.currentIndex == 0) {
         throw new NoSuchElementException();
      } else {
         this.removeState = this.wrappedIteratorIndex == this.currentIndex;
         return (E)this.list.get(--this.currentIndex);
      }
   }

   public int previousIndex() {
      if (this.iterator instanceof ListIterator) {
         ListIterator<?> li = (ListIterator)this.iterator;
         return li.previousIndex();
      } else {
         return this.currentIndex - 1;
      }
   }

   public void remove() throws IllegalStateException {
      if (this.iterator instanceof ListIterator) {
         this.iterator.remove();
      } else {
         int removeIndex = this.currentIndex;
         if (this.currentIndex == this.wrappedIteratorIndex) {
            --removeIndex;
         }

         if (this.removeState && this.wrappedIteratorIndex - this.currentIndex <= 1) {
            this.iterator.remove();
            this.list.remove(removeIndex);
            this.currentIndex = removeIndex;
            --this.wrappedIteratorIndex;
            this.removeState = false;
         } else {
            throw new IllegalStateException(MessageFormat.format("Cannot remove element at index {0}.", removeIndex));
         }
      }
   }

   public void reset() {
      if (!(this.iterator instanceof ListIterator)) {
         this.currentIndex = 0;
      } else {
         ListIterator<?> li = (ListIterator)this.iterator;

         while(li.previousIndex() >= 0) {
            li.previous();
         }

      }
   }

   public void set(E obj) throws UnsupportedOperationException {
      if (this.iterator instanceof ListIterator) {
         ListIterator<E> li = (ListIterator)this.iterator;
         li.set(obj);
      } else {
         throw new UnsupportedOperationException("ListIteratorWrapper does not support optional operations of ListIterator.");
      }
   }
}
