package org.apache.commons.collections4;

import org.apache.commons.collections4.multiset.HashMultiSet;
import org.apache.commons.collections4.multiset.PredicatedMultiSet;
import org.apache.commons.collections4.multiset.SynchronizedMultiSet;
import org.apache.commons.collections4.multiset.UnmodifiableMultiSet;

public class MultiSetUtils {
   public static final MultiSet EMPTY_MULTISET = UnmodifiableMultiSet.unmodifiableMultiSet(new HashMultiSet());

   public static <E> MultiSet<E> emptyMultiSet() {
      return EMPTY_MULTISET;
   }

   public static <E> MultiSet<E> predicatedMultiSet(MultiSet<E> multiset, Predicate<? super E> predicate) {
      return PredicatedMultiSet.<E>predicatedMultiSet(multiset, predicate);
   }

   public static <E> MultiSet<E> synchronizedMultiSet(MultiSet<E> multiset) {
      return SynchronizedMultiSet.<E>synchronizedMultiSet(multiset);
   }

   public static <E> MultiSet<E> unmodifiableMultiSet(MultiSet<? extends E> multiset) {
      return UnmodifiableMultiSet.<E>unmodifiableMultiSet(multiset);
   }

   private MultiSetUtils() {
   }
}
