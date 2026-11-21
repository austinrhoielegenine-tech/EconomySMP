package org.apache.commons.collections4.iterators;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.apache.commons.collections4.ResettableIterator;

public class LoopingIterator<E> implements ResettableIterator<E> {
   private final Collection<? extends E> collection;
   private Iterator<? extends E> iterator;

   public LoopingIterator(Collection<? extends E> collection) {
      this.collection = (Collection)Objects.requireNonNull(collection, "collection");
      this.reset();
   }

   public boolean hasNext() {
      return !this.collection.isEmpty();
   }

   public E next() {
      if (this.collection.isEmpty()) {
         throw new NoSuchElementException("There are no elements for this iterator to loop on");
      } else {
         if (!this.iterator.hasNext()) {
            this.reset();
         }

         return (E)this.iterator.next();
      }
   }

   public void remove() {
      this.iterator.remove();
   }

   public void reset() {
      this.iterator = this.collection.iterator();
   }

   public int size() {
      return this.collection.size();
   }
}
