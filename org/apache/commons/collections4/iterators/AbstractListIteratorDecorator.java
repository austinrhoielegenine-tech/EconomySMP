package org.apache.commons.collections4.iterators;

import java.util.ListIterator;
import java.util.Objects;

public class AbstractListIteratorDecorator<E> implements ListIterator<E> {
   private final ListIterator<E> iterator;

   public AbstractListIteratorDecorator(ListIterator<E> iterator) {
      this.iterator = (ListIterator)Objects.requireNonNull(iterator, "iterator");
   }

   public void add(E obj) {
      this.iterator.add(obj);
   }

   protected ListIterator<E> getListIterator() {
      return this.iterator;
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
      this.iterator.remove();
   }

   public void set(E obj) {
      this.iterator.set(obj);
   }
}
