package org.apache.commons.collections4.iterators;

import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.Predicate;

public class FilterListIterator<E> implements ListIterator<E> {
   private ListIterator<? extends E> iterator;
   private Predicate<? super E> predicate;
   private E nextObject;
   private boolean nextObjectSet;
   private E previousObject;
   private boolean previousObjectSet;
   private int nextIndex;

   public FilterListIterator() {
   }

   public FilterListIterator(ListIterator<? extends E> iterator) {
      this.iterator = iterator;
   }

   public FilterListIterator(ListIterator<? extends E> iterator, Predicate<? super E> predicate) {
      this.iterator = iterator;
      this.predicate = predicate;
   }

   public FilterListIterator(Predicate<? super E> predicate) {
      this.predicate = predicate;
   }

   public void add(E o) {
      throw new UnsupportedOperationException("FilterListIterator.add(Object) is not supported.");
   }

   private void clearNextObject() {
      this.nextObject = null;
      this.nextObjectSet = false;
   }

   private void clearPreviousObject() {
      this.previousObject = null;
      this.previousObjectSet = false;
   }

   public ListIterator<? extends E> getListIterator() {
      return this.iterator;
   }

   public Predicate<? super E> getPredicate() {
      return this.predicate;
   }

   public boolean hasNext() {
      return this.nextObjectSet || this.setNextObject();
   }

   public boolean hasPrevious() {
      return this.previousObjectSet || this.setPreviousObject();
   }

   public E next() {
      if (!this.nextObjectSet && !this.setNextObject()) {
         throw new NoSuchElementException();
      } else {
         ++this.nextIndex;
         E temp = this.nextObject;
         this.clearNextObject();
         return temp;
      }
   }

   public int nextIndex() {
      return this.nextIndex;
   }

   public E previous() {
      if (!this.previousObjectSet && !this.setPreviousObject()) {
         throw new NoSuchElementException();
      } else {
         --this.nextIndex;
         E temp = this.previousObject;
         this.clearPreviousObject();
         return temp;
      }
   }

   public int previousIndex() {
      return this.nextIndex - 1;
   }

   public void remove() {
      throw new UnsupportedOperationException("FilterListIterator.remove() is not supported.");
   }

   public void set(E ignored) {
      throw new UnsupportedOperationException("FilterListIterator.set(Object) is not supported.");
   }

   public void setListIterator(ListIterator<? extends E> iterator) {
      this.iterator = iterator;
   }

   private boolean setNextObject() {
      if (this.previousObjectSet) {
         this.clearPreviousObject();
         if (!this.setNextObject()) {
            return false;
         }

         this.clearNextObject();
      }

      if (this.iterator == null) {
         return false;
      } else {
         while(this.iterator.hasNext()) {
            E object = (E)this.iterator.next();
            if (this.predicate.test(object)) {
               this.nextObject = object;
               this.nextObjectSet = true;
               return true;
            }
         }

         return false;
      }
   }

   public void setPredicate(Predicate<? super E> predicate) {
      this.predicate = predicate;
   }

   private boolean setPreviousObject() {
      if (this.nextObjectSet) {
         this.clearNextObject();
         if (!this.setPreviousObject()) {
            return false;
         }

         this.clearPreviousObject();
      }

      if (this.iterator == null) {
         return false;
      } else {
         while(this.iterator.hasPrevious()) {
            E object = (E)this.iterator.previous();
            if (this.predicate.test(object)) {
               this.previousObject = object;
               this.previousObjectSet = true;
               return true;
            }
         }

         return false;
      }
   }
}
