package org.apache.commons.collections4.bag;

import java.util.Comparator;
import org.apache.commons.collections4.SortedBag;

public abstract class AbstractSortedBagDecorator<E> extends AbstractBagDecorator<E> implements SortedBag<E> {
   private static final long serialVersionUID = -8223473624050467718L;

   protected AbstractSortedBagDecorator() {
   }

   protected AbstractSortedBagDecorator(SortedBag<E> bag) {
      super(bag);
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
