package org.apache.commons.collections4.set;

import java.util.Iterator;
import java.util.NavigableSet;
import org.apache.commons.collections4.Transformer;

public class TransformedNavigableSet<E> extends TransformedSortedSet<E> implements NavigableSet<E> {
   private static final long serialVersionUID = 20150528L;

   public static <E> TransformedNavigableSet<E> transformedNavigableSet(NavigableSet<E> set, Transformer<? super E, ? extends E> transformer) {
      TransformedNavigableSet<E> decorated = new TransformedNavigableSet<E>(set, transformer);
      if (!set.isEmpty()) {
         E[] values = (E[])set.toArray();
         set.clear();

         for(E value : values) {
            decorated.decorated().add(transformer.apply(value));
         }
      }

      return decorated;
   }

   public static <E> TransformedNavigableSet<E> transformingNavigableSet(NavigableSet<E> set, Transformer<? super E, ? extends E> transformer) {
      return new TransformedNavigableSet<E>(set, transformer);
   }

   protected TransformedNavigableSet(NavigableSet<E> set, Transformer<? super E, ? extends E> transformer) {
      super(set, transformer);
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
      return transformingNavigableSet(this.decorated().descendingSet(), this.transformer);
   }

   public E floor(E e) {
      return (E)this.decorated().floor(e);
   }

   public NavigableSet<E> headSet(E toElement, boolean inclusive) {
      NavigableSet<E> head = this.decorated().headSet(toElement, inclusive);
      return transformingNavigableSet(head, this.transformer);
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
      return transformingNavigableSet(sub, this.transformer);
   }

   public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
      NavigableSet<E> tail = this.decorated().tailSet(fromElement, inclusive);
      return transformingNavigableSet(tail, this.transformer);
   }
}
