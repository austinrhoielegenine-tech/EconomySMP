package org.apache.commons.collections4.set;

import java.util.Iterator;
import java.util.NavigableSet;
import org.apache.commons.collections4.Predicate;

public class PredicatedNavigableSet<E> extends PredicatedSortedSet<E> implements NavigableSet<E> {
   private static final long serialVersionUID = 20150528L;

   public static <E> PredicatedNavigableSet<E> predicatedNavigableSet(NavigableSet<E> set, Predicate<? super E> predicate) {
      return new PredicatedNavigableSet<E>(set, predicate);
   }

   protected PredicatedNavigableSet(NavigableSet<E> set, Predicate<? super E> predicate) {
      super(set, predicate);
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
      return predicatedNavigableSet(this.decorated().descendingSet(), this.predicate);
   }

   public E floor(E e) {
      return (E)this.decorated().floor(e);
   }

   public NavigableSet<E> headSet(E toElement, boolean inclusive) {
      NavigableSet<E> head = this.decorated().headSet(toElement, inclusive);
      return predicatedNavigableSet(head, this.predicate);
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
      NavigableSet<E> sub = this.decorated().subSet(fromElement, fromInclusive, toElement, toInclusive);
      return predicatedNavigableSet(sub, this.predicate);
   }

   public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
      NavigableSet<E> tail = this.decorated().tailSet(fromElement, inclusive);
      return predicatedNavigableSet(tail, this.predicate);
   }
}
