package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.Predicate;

public class IfClosure<T> implements Closure<T>, Serializable {
   private static final long serialVersionUID = 3518477308466486130L;
   private final Predicate<? super T> iPredicate;
   private final Closure<? super T> iTrueClosure;
   private final Closure<? super T> iFalseClosure;

   public static <E> Closure<E> ifClosure(Predicate<? super E> predicate, Closure<? super E> trueClosure) {
      return ifClosure(predicate, trueClosure, NOPClosure.nopClosure());
   }

   public static <E> Closure<E> ifClosure(Predicate<? super E> predicate, Closure<? super E> trueClosure, Closure<? super E> falseClosure) {
      return new IfClosure<E>((Predicate)Objects.requireNonNull(predicate, "predicate"), (Closure)Objects.requireNonNull(trueClosure, "trueClosure"), (Closure)Objects.requireNonNull(falseClosure, "falseClosure"));
   }

   public IfClosure(Predicate<? super T> predicate, Closure<? super T> trueClosure) {
      this(predicate, trueClosure, NOPClosure.nopClosure());
   }

   public IfClosure(Predicate<? super T> predicate, Closure<? super T> trueClosure, Closure<? super T> falseClosure) {
      this.iPredicate = predicate;
      this.iTrueClosure = trueClosure;
      this.iFalseClosure = falseClosure;
   }

   public void execute(T input) {
      if (this.iPredicate.test(input)) {
         this.iTrueClosure.accept(input);
      } else {
         this.iFalseClosure.accept(input);
      }

   }

   public Closure<? super T> getFalseClosure() {
      return this.iFalseClosure;
   }

   public Predicate<? super T> getPredicate() {
      return this.iPredicate;
   }

   public Closure<? super T> getTrueClosure() {
      return this.iTrueClosure;
   }
}
