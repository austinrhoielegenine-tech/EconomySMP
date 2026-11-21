package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.TruePredicate;

public class FilterIterator<E> implements IteratorOperations<E> {
   private Iterator<? extends E> iterator;
   private Predicate<? super E> predicate = TruePredicate.<E>truePredicate();
   private E nextObject;
   private boolean nextObjectSet;

   public FilterIterator() {
   }

   public FilterIterator(Iterator<? extends E> iterator) {
      this.iterator = iterator;
   }

   public FilterIterator(Iterator<? extends E> iterator, Predicate<? super E> predicate) {
      this.iterator = iterator;
      this.predicate = this.safePredicate(predicate);
   }

   public Iterator<? extends E> getIterator() {
      return this.iterator;
   }

   public Predicate<? super E> getPredicate() {
      return this.predicate;
   }

   public boolean hasNext() {
      return this.nextObjectSet || this.setNextObject();
   }

   public E next() {
      if (!this.nextObjectSet && !this.setNextObject()) {
         throw new NoSuchElementException();
      } else {
         this.nextObjectSet = false;
         return this.nextObject;
      }
   }

   public void remove() {
      if (this.nextObjectSet) {
         throw new IllegalStateException("remove() cannot be called");
      } else {
         this.iterator.remove();
      }
   }

   private Predicate<? super E> safePredicate(Predicate<? super E> predicate) {
      return predicate != null ? predicate : TruePredicate.truePredicate();
   }

   public void setIterator(Iterator<? extends E> iterator) {
      this.iterator = iterator;
      this.nextObject = null;
      this.nextObjectSet = false;
   }

   private boolean setNextObject() {
      while(true) {
         if (this.iterator.hasNext()) {
            E object = (E)this.iterator.next();
            if (!this.predicate.test(object)) {
               continue;
            }

            this.nextObject = object;
            this.nextObjectSet = true;
            return true;
         }

         return false;
      }
   }

   public void setPredicate(Predicate<? super E> predicate) {
      this.predicate = this.safePredicate(predicate);
      this.nextObject = null;
      this.nextObjectSet = false;
   }
}
