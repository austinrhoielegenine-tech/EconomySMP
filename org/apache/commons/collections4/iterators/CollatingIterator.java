package org.apache.commons.collections4.iterators;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.apache.commons.collections4.list.UnmodifiableList;

public class CollatingIterator<E> implements Iterator<E> {
   private Comparator<? super E> comparator;
   private final List<Iterator<? extends E>> iterators;
   private List<E> values;
   private BitSet valueSet;
   private int lastReturned;

   public CollatingIterator() {
      this((Comparator)null, 2);
   }

   public CollatingIterator(Comparator<? super E> comp) {
      this(comp, 2);
   }

   public CollatingIterator(Comparator<? super E> comp, Collection<Iterator<? extends E>> iterators) {
      this(comp, iterators.size());

      for(Iterator<? extends E> iterator : iterators) {
         this.addIterator(iterator);
      }

   }

   public CollatingIterator(Comparator<? super E> comp, int initIterCapacity) {
      this.lastReturned = -1;
      this.iterators = new ArrayList(initIterCapacity);
      this.setComparator(comp);
   }

   public CollatingIterator(Comparator<? super E> comp, Iterator<? extends E> a, Iterator<? extends E> b) {
      this(comp, 2);
      this.addIterator(a);
      this.addIterator(b);
   }

   public CollatingIterator(Comparator<? super E> comp, Iterator<? extends E>[] iterators) {
      this(comp, iterators.length);

      for(Iterator<? extends E> iterator : iterators) {
         this.addIterator(iterator);
      }

   }

   public void addIterator(Iterator<? extends E> iterator) {
      this.checkNotStarted();
      Objects.requireNonNull(iterator, "iterator");
      this.iterators.add(iterator);
   }

   private boolean anyHasNext(List<Iterator<? extends E>> iterators) {
      for(Iterator<? extends E> iterator : iterators) {
         if (iterator.hasNext()) {
            return true;
         }
      }

      return false;
   }

   private boolean anyValueSet(BitSet set) {
      for(int i = 0; i < set.size(); ++i) {
         if (set.get(i)) {
            return true;
         }
      }

      return false;
   }

   private void checkNotStarted() throws IllegalStateException {
      if (this.values != null) {
         throw new IllegalStateException("Can't do that after next or hasNext has been called.");
      }
   }

   private void clear(int i) {
      this.values.set(i, (Object)null);
      this.valueSet.clear(i);
   }

   public Comparator<? super E> getComparator() {
      return this.comparator;
   }

   public int getIteratorIndex() {
      if (this.lastReturned == -1) {
         throw new IllegalStateException("No value has been returned yet");
      } else {
         return this.lastReturned;
      }
   }

   public List<Iterator<? extends E>> getIterators() {
      return UnmodifiableList.<Iterator<? extends E>>unmodifiableList(this.iterators);
   }

   public boolean hasNext() {
      this.start();
      return this.anyValueSet(this.valueSet) || this.anyHasNext(this.iterators);
   }

   private int least() {
      int leastIndex = -1;
      E leastObject = null;

      for(int i = 0; i < this.values.size(); ++i) {
         if (!this.valueSet.get(i)) {
            this.set(i);
         }

         if (this.valueSet.get(i)) {
            if (leastIndex == -1) {
               leastIndex = i;
               leastObject = (E)this.values.get(i);
            } else {
               E curObject = (E)this.values.get(i);
               Objects.requireNonNull(this.comparator, "You must invoke setComparator() to set a comparator first.");
               if (this.comparator.compare(curObject, leastObject) < 0) {
                  leastObject = curObject;
                  leastIndex = i;
               }
            }
         }
      }

      return leastIndex;
   }

   public E next() throws NoSuchElementException {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         int leastIndex = this.least();
         if (leastIndex == -1) {
            throw new NoSuchElementException();
         } else {
            E val = (E)this.values.get(leastIndex);
            this.clear(leastIndex);
            this.lastReturned = leastIndex;
            return val;
         }
      }
   }

   public void remove() {
      if (this.lastReturned == -1) {
         throw new IllegalStateException("No value can be removed at present");
      } else {
         ((Iterator)this.iterators.get(this.lastReturned)).remove();
      }
   }

   private boolean set(int index) {
      Iterator<? extends E> it = (Iterator)this.iterators.get(index);
      if (it.hasNext()) {
         this.values.set(index, it.next());
         this.valueSet.set(index);
         return true;
      } else {
         this.values.set(index, (Object)null);
         this.valueSet.clear(index);
         return false;
      }
   }

   public void setComparator(Comparator<? super E> comp) {
      this.checkNotStarted();
      this.comparator = comp;
   }

   public void setIterator(int index, Iterator<? extends E> iterator) {
      this.checkNotStarted();
      Objects.requireNonNull(iterator, "iterator");
      this.iterators.set(index, iterator);
   }

   private void start() {
      if (this.values == null) {
         this.values = new ArrayList(this.iterators.size());
         this.valueSet = new BitSet(this.iterators.size());

         for(int i = 0; i < this.iterators.size(); ++i) {
            this.values.add((Object)null);
            this.valueSet.clear(i);
         }
      }

   }
}
