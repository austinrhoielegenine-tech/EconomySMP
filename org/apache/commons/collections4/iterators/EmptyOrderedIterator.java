package org.apache.commons.collections4.iterators;

import org.apache.commons.collections4.OrderedIterator;

public class EmptyOrderedIterator<E> extends AbstractEmptyIterator<E> implements OrderedIterator<E> {
   public static final OrderedIterator INSTANCE = new EmptyOrderedIterator();

   public static <E> OrderedIterator<E> emptyOrderedIterator() {
      return INSTANCE;
   }

   protected EmptyOrderedIterator() {
   }
}
