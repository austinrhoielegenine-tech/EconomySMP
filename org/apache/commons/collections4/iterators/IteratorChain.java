package org.apache.commons.collections4.iterators;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class IteratorChain<E> implements Iterator<E> {
   private final Queue<Iterator<? extends E>> iteratorQueue = new LinkedList();
   private Iterator<? extends E> currentIterator;
   private Iterator<? extends E> lastUsedIterator;
   private boolean isLocked;

   public IteratorChain() {
   }

   public IteratorChain(Collection<? extends Iterator<? extends E>> iteratorQueue) {
      for(Iterator<? extends E> iterator : iteratorQueue) {
         this.addIterator(iterator);
      }

   }

   public IteratorChain(Iterator<? extends E> iterator) {
      this.addIterator(iterator);
   }

   public IteratorChain(Iterator<? extends E>... iteratorQueue) {
      for(Iterator<? extends E> element : iteratorQueue) {
         this.addIterator(element);
      }

   }

   public IteratorChain(Iterator<? extends E> first, Iterator<? extends E> second) {
      this.addIterator(first);
      this.addIterator(second);
   }

   public void addIterator(Iterator<? extends E> iterator) {
      this.checkLocked();
      this.iteratorQueue.add((Iterator)Objects.requireNonNull(iterator, "iterator"));
   }

   private void checkLocked() {
      if (this.isLocked) {
         throw new UnsupportedOperationException("IteratorChain cannot be changed after the first use of a method from the Iterator interface");
      }
   }

   public boolean hasNext() {
      this.lockChain();
      this.updateCurrentIterator();
      this.lastUsedIterator = this.currentIterator;
      return this.currentIterator.hasNext();
   }

   public boolean isLocked() {
      return this.isLocked;
   }

   private void lockChain() {
      if (!this.isLocked) {
         this.isLocked = true;
      }

   }

   public E next() {
      this.lockChain();
      this.updateCurrentIterator();
      this.lastUsedIterator = this.currentIterator;
      return (E)this.currentIterator.next();
   }

   public void remove() {
      this.lockChain();
      if (this.currentIterator == null) {
         this.updateCurrentIterator();
      }

      this.lastUsedIterator.remove();
   }

   public int size() {
      return this.iteratorQueue.size();
   }

   protected void updateCurrentIterator() {
      if (this.currentIterator == null) {
         if (this.iteratorQueue.isEmpty()) {
            this.currentIterator = EmptyIterator.<E>emptyIterator();
         } else {
            this.currentIterator = (Iterator)this.iteratorQueue.remove();
         }

         this.lastUsedIterator = this.currentIterator;
      }

      while(!this.currentIterator.hasNext() && !this.iteratorQueue.isEmpty()) {
         this.currentIterator = (Iterator)this.iteratorQueue.remove();
      }

   }
}
