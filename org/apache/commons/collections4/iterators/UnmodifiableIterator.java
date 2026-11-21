package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import java.util.Objects;
import org.apache.commons.collections4.Unmodifiable;

public final class UnmodifiableIterator<E> implements Iterator<E>, Unmodifiable {
   private final Iterator<? extends E> iterator;

   public static <E> Iterator<E> unmodifiableIterator(Iterator<? extends E> iterator) {
      Objects.requireNonNull(iterator, "iterator");
      return (Iterator<E>)(iterator instanceof Unmodifiable ? iterator : new UnmodifiableIterator(iterator));
   }

   private UnmodifiableIterator(Iterator<? extends E> iterator) {
      this.iterator = iterator;
   }

   public boolean hasNext() {
      return this.iterator.hasNext();
   }

   public E next() {
      return (E)this.iterator.next();
   }

   public void remove() {
      throw new UnsupportedOperationException("remove() is not supported");
   }
}
