package org.apache.commons.collections4.bloomfilter;

import java.util.function.LongPredicate;

class CountingLongPredicate implements LongPredicate {
   private int idx;
   private final long[] ary;
   private final LongBiPredicate func;

   CountingLongPredicate(long[] ary, LongBiPredicate func) {
      this.ary = ary;
      this.func = func;
   }

   boolean processRemaining() {
      int i = this.idx;
      long[] a = this.ary;

      int limit;
      for(limit = a.length; i != limit && this.func.test(a[i], 0L); ++i) {
      }

      return i == limit;
   }

   public boolean test(long other) {
      return this.func.test(this.idx == this.ary.length ? 0L : this.ary[this.idx++], other);
   }
}
