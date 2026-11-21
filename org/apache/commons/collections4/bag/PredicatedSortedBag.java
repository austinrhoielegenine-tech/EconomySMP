package org.apache.commons.collections4.bag;

import java.util.Comparator;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.SortedBag;

public class PredicatedSortedBag<E> extends PredicatedBag<E> implements SortedBag<E> {
   private static final long serialVersionUID = 3448581314086406616L;

   public static <E> PredicatedSortedBag<E> predicatedSortedBag(SortedBag<E> bag, Predicate<? super E> predicate) {
      return new PredicatedSortedBag<E>(bag, predicate);
   }

   protected PredicatedSortedBag(SortedBag<E> bag, Predicate<? super E> predicate) {
      super(bag, predicate);
   }

   public Comparator<? super E> comparator() {
      return this.decorated().comparator();
   }

   protected SortedBag<E> decorated() {
      return (SortedBag)super.decorated();
   }

   public E first() {
      return (E)this.decorated().first();
   }

   public E last() {
      return (E)this.decorated().last();
   }
}
