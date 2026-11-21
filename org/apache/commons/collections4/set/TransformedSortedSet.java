package org.apache.commons.collections4.set;

import java.util.Comparator;
import java.util.SortedSet;
import org.apache.commons.collections4.Transformer;

public class TransformedSortedSet<E> extends TransformedSet<E> implements SortedSet<E> {
   private static final long serialVersionUID = -1675486811351124386L;

   public static <E> TransformedSortedSet<E> transformedSortedSet(SortedSet<E> set, Transformer<? super E, ? extends E> transformer) {
      TransformedSortedSet<E> decorated = new TransformedSortedSet<E>(set, transformer);
      if (!set.isEmpty()) {
         E[] values = (E[])set.toArray();
         set.clear();

         for(E value : values) {
            decorated.decorated().add(transformer.apply(value));
         }
      }

      return decorated;
   }

   public static <E> TransformedSortedSet<E> transformingSortedSet(SortedSet<E> set, Transformer<? super E, ? extends E> transformer) {
      return new TransformedSortedSet<E>(set, transformer);
   }

   protected TransformedSortedSet(SortedSet<E> set, Transformer<? super E, ? extends E> transformer) {
      super(set, transformer);
   }

   public Comparator<? super E> comparator() {
      return this.getSortedSet().comparator();
   }

   public E first() {
      return (E)this.getSortedSet().first();
   }

   protected SortedSet<E> getSortedSet() {
      return (SortedSet)this.decorated();
   }

   public SortedSet<E> headSet(E toElement) {
      SortedSet<E> set = this.getSortedSet().headSet(toElement);
      return new TransformedSortedSet<E>(set, this.transformer);
   }

   public E last() {
      return (E)this.getSortedSet().last();
   }

   public SortedSet<E> subSet(E fromElement, E toElement) {
      SortedSet<E> set = this.getSortedSet().subSet(fromElement, toElement);
      return new TransformedSortedSet<E>(set, this.transformer);
   }

   public SortedSet<E> tailSet(E fromElement) {
      SortedSet<E> set = this.getSortedSet().tailSet(fromElement);
      return new TransformedSortedSet<E>(set, this.transformer);
   }
}
