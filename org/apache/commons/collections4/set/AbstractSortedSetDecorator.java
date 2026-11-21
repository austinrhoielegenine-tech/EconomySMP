package org.apache.commons.collections4.set;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;

public abstract class AbstractSortedSetDecorator<E> extends AbstractSetDecorator<E> implements SortedSet<E> {
   private static final long serialVersionUID = -3462240946294214398L;

   protected AbstractSortedSetDecorator() {
   }

   protected AbstractSortedSetDecorator(Set<E> set) {
      super(set);
   }

   public Comparator<? super E> comparator() {
      return this.decorated().comparator();
   }

   protected SortedSet<E> decorated() {
      return (SortedSet)super.decorated();
   }

   public E first() {
      return (E)this.decorated().first();
   }

   public SortedSet<E> headSet(E toElement) {
      return this.decorated().headSet(toElement);
   }

   public E last() {
      return (E)this.decorated().last();
   }

   public SortedSet<E> subSet(E fromElement, E toElement) {
      return this.decorated().subSet(fromElement, toElement);
   }

   public SortedSet<E> tailSet(E fromElement) {
      return this.decorated().tailSet(fromElement);
   }
}
