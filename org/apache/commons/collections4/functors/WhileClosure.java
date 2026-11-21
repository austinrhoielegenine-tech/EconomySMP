package org.apache.commons.collections4.functors;

import java.util.Objects;
import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.Predicate;

public class WhileClosure<T> implements Closure<T> {
   private final Predicate<? super T> iPredicate;
   private final Closure<? super T> iClosure;
   private final boolean iDoLoop;

   public static <E> Closure<E> whileClosure(Predicate<? super E> predicate, Closure<? super E> closure, boolean doLoop) {
      return new WhileClosure<E>((Predicate)Objects.requireNonNull(predicate, "predicate"), (Closure)Objects.requireNonNull(closure, "closure"), doLoop);
   }

   public WhileClosure(Predicate<? super T> predicate, Closure<? super T> closure, boolean doLoop) {
      this.iPredicate = predicate;
      this.iClosure = closure;
      this.iDoLoop = doLoop;
   }

   public void execute(T input) {
      if (this.iDoLoop) {
         this.iClosure.accept(input);
      }

      while(this.iPredicate.test(input)) {
         this.iClosure.accept(input);
      }

   }

   public Closure<? super T> getClosure() {
      return this.iClosure;
   }

   public Predicate<? super T> getPredicate() {
      return this.iPredicate;
   }

   public boolean isDoLoop() {
      return this.iDoLoop;
   }
}
