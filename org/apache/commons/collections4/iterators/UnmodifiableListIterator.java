package org.apache.commons.collections4.iterators;

import java.util.ListIterator;
import java.util.Objects;
import org.apache.commons.collections4.Unmodifiable;

public final class UnmodifiableListIterator<E> implements ListIterator<E>, Unmodifiable {
   private final ListIterator<? extends E> iterator;

   /** @deprecated */
   @Deprecated
   public static <E> ListIterator<E> umodifiableListIterator(ListIterator<? extends E> iterator) {
      return unmodifiableListIterator(iterator);
   }

   public static <E> ListIterator<E> unmodifiableListIterator(ListIterator<? extends E> iterator) {
      Objects.requireNonNull(iterator, "iterator");
      return (ListIterator<E>)(iterator instanceof Unmodifiable ? iterator : new UnmodifiableListIterator(iterator));
   }

   private UnmodifiableListIterator(ListIterator<? extends E> iterator) {
      this.iterator = iterator;
   }

   public void add(E obj) {
      throw new UnsupportedOperationException("add() is not supported");
   }

   public boolean hasNext() {
      return this.iterator.hasNext();
   }

   public boolean hasPrevious() {
      return this.iterator.hasPrevious();
   }

   public E next() {
      return (E)this.iterator.next();
   }

   public int nextIndex() {
      return this.iterator.nextIndex();
   }

   public E previous() {
      return (E)this.iterator.previous();
   }

   public int previousIndex() {
      return this.iterator.previousIndex();
   }

   public void remove() {
      throw new UnsupportedOperationException("remove() is not supported");
   }

   public void set(E ignored) {
      throw new UnsupportedOperationException("set() is not supported");
   }
}
