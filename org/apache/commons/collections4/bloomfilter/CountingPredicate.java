package org.apache.commons.collections4.bloomfilter;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

class CountingPredicate<T> implements Predicate<T> {
   private int idx;
   private final T[] ary;
   private final BiPredicate<T, T> func;

   CountingPredicate(T[] ary, BiPredicate<T, T> func) {
      this.ary = ary;
      this.func = func;
   }

   boolean processRemaining() {
      int i = this.idx;
      T[] a = this.ary;

      int limit;
      for(limit = a.length; i != limit && this.func.test(a[i], (Object)null); ++i) {
      }

      return i == limit;
   }

   public boolean test(T other) {
      return this.func.test(this.idx == this.ary.length ? null : this.ary[this.idx++], other);
   }
}
