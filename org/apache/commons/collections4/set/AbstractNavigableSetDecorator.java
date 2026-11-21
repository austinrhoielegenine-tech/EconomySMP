package org.apache.commons.collections4.set;

import java.util.Iterator;
import java.util.NavigableSet;

public abstract class AbstractNavigableSetDecorator<E> extends AbstractSortedSetDecorator<E> implements NavigableSet<E> {
   private static final long serialVersionUID = 20150528L;

   protected AbstractNavigableSetDecorator() {
   }

   protected AbstractNavigableSetDecorator(NavigableSet<E> set) {
      super(set);
   }

   public E ceiling(E e) {
      return (E)this.decorated().ceiling(e);
   }

   protected NavigableSet<E> decorated() {
      return (NavigableSet)super.decorated();
   }

   public Iterator<E> descendingIterator() {
      return this.decorated().descendingIterator();
   }

   public NavigableSet<E> descendingSet() {
      return this.decorated().descendingSet();
   }

   public E floor(E e) {
      return (E)this.decorated().floor(e);
   }

   public NavigableSet<E> headSet(E toElement, boolean inclusive) {
      return this.decorated().headSet(toElement, inclusive);
   }

   public E higher(E e) {
      return (E)this.decorated().higher(e);
   }

   public E lower(E e) {
      return (E)this.decorated().lower(e);
   }

   public E pollFirst() {
      return (E)this.decorated().pollFirst();
   }

   public E pollLast() {
      return (E)this.decorated().pollLast();
   }

   public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
      return this.decorated().subSet(fromElement, fromInclusive, toElement, toInclusive);
   }

   public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
      return this.decorated().tailSet(fromElement, inclusive);
   }
}
