package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.Closure;

public class ForClosure<T> implements Closure<T> {
   private final int iCount;
   private final Closure<? super T> iClosure;

   public static <E> Closure<E> forClosure(int count, Closure<? super E> closure) {
      if (count > 0 && closure != null) {
         return (Closure<E>)(count == 1 ? closure : new ForClosure(count, closure));
      } else {
         return NOPClosure.<E>nopClosure();
      }
   }

   public ForClosure(int count, Closure<? super T> closure) {
      this.iCount = count;
      this.iClosure = closure;
   }

   public void execute(T input) {
      for(int i = 0; i < this.iCount; ++i) {
         this.iClosure.accept(input);
      }

   }

   public Closure<? super T> getClosure() {
      return this.iClosure;
   }

   public int getCount() {
      return this.iCount;
   }
}
