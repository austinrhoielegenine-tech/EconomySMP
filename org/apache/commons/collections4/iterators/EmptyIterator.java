package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import org.apache.commons.collections4.ResettableIterator;

public class EmptyIterator<E> extends AbstractEmptyIterator<E> {
   public static final ResettableIterator RESETTABLE_INSTANCE = new EmptyIterator();
   public static final Iterator INSTANCE;

   public static <E> Iterator<E> emptyIterator() {
      return INSTANCE;
   }

   public static <E> ResettableIterator<E> resettableEmptyIterator() {
      return RESETTABLE_INSTANCE;
   }

   protected EmptyIterator() {
   }

   static {
      INSTANCE = RESETTABLE_INSTANCE;
   }
}
