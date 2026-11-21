package org.apache.commons.collections4.iterators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class CartesianProductIterator<E> implements Iterator<List<E>> {
   private final List<Iterable<? extends E>> iterables;
   private final List<Iterator<? extends E>> iterators;
   private List<E> previousTuple;

   @SafeVarargs
   public CartesianProductIterator(Iterable<? extends E>... iterables) {
      Objects.requireNonNull(iterables, "iterables");
      this.iterables = new ArrayList(iterables.length);
      this.iterators = new ArrayList(iterables.length);

      for(Iterable<? extends E> iterable : iterables) {
         Objects.requireNonNull(iterable, "iterable");
         this.iterables.add(iterable);
         Iterator<? extends E> iterator = iterable.iterator();
         if (!iterator.hasNext()) {
            this.iterators.clear();
            break;
         }

         this.iterators.add(iterator);
      }

   }

   public boolean hasNext() {
      return this.iterators.stream().anyMatch(Iterator::hasNext);
   }

   public List<E> next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else if (this.previousTuple != null) {
         for(int i = this.iterators.size() - 1; i >= 0; --i) {
            Iterator<? extends E> iterator = (Iterator)this.iterators.get(i);
            if (iterator.hasNext()) {
               this.previousTuple.set(i, iterator.next());
               return new ArrayList(this.previousTuple);
            }

            iterator = ((Iterable)this.iterables.get(i)).iterator();
            this.iterators.set(i, iterator);
            this.previousTuple.set(i, iterator.next());
         }

         throw new IllegalStateException("reached unreachable code");
      } else {
         this.previousTuple = new ArrayList(this.iterables.size());

         for(Iterator<? extends E> iterator : this.iterators) {
            this.previousTuple.add(iterator.next());
         }

         return new ArrayList(this.previousTuple);
      }
   }

   public void remove() {
      throw new UnsupportedOperationException("remove");
   }
}
