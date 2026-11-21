package org.apache.commons.collections4.iterators;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Objects;

public class PushbackIterator<E> implements Iterator<E> {
   private final Iterator<? extends E> iterator;
   private final Deque<E> items = new ArrayDeque();

   public static <E> PushbackIterator<E> pushbackIterator(Iterator<? extends E> iterator) {
      Objects.requireNonNull(iterator, "iterator");
      if (iterator instanceof PushbackIterator) {
         PushbackIterator<E> it = (PushbackIterator)iterator;
         return it;
      } else {
         return new PushbackIterator<E>(iterator);
      }
   }

   public PushbackIterator(Iterator<? extends E> iterator) {
      this.iterator = iterator;
   }

   public boolean hasNext() {
      return !this.items.isEmpty() || this.iterator.hasNext();
   }

   public E next() {
      return (E)(!this.items.isEmpty() ? this.items.pop() : this.iterator.next());
   }

   public void pushback(E item) {
      this.items.push(item);
   }

   public void remove() {
      throw new UnsupportedOperationException();
   }
}
