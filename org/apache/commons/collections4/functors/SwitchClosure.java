package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.Predicate;

public class SwitchClosure<T> implements Closure<T>, Serializable {
   private static final long serialVersionUID = 3518477308466486130L;
   private final Predicate<? super T>[] iPredicates;
   private final Closure<? super T>[] iClosures;
   private final Closure<? super T> iDefault;

   public static <E> Closure<E> switchClosure(Map<Predicate<E>, Closure<E>> predicatesAndClosures) {
      Objects.requireNonNull(predicatesAndClosures, "predicatesAndClosures");
      Closure<? super E> defaultClosure = (Closure)predicatesAndClosures.remove((Object)null);
      int size = predicatesAndClosures.size();
      if (size == 0) {
         return defaultClosure == null ? NOPClosure.nopClosure() : defaultClosure;
      } else {
         Closure<E>[] closures = new Closure[size];
         Predicate<E>[] preds = new Predicate[size];
         int i = 0;

         for(Map.Entry<Predicate<E>, Closure<E>> entry : predicatesAndClosures.entrySet()) {
            preds[i] = (Predicate)entry.getKey();
            closures[i] = (Closure)entry.getValue();
            ++i;
         }

         return new SwitchClosure<E>(false, preds, closures, defaultClosure);
      }
   }

   public static <E> Closure<E> switchClosure(Predicate<? super E>[] predicates, Closure<? super E>[] closures, Closure<? super E> defaultClosure) {
      FunctorUtils.validate(predicates);
      FunctorUtils.validate(closures);
      if (predicates.length != closures.length) {
         throw new IllegalArgumentException("The predicate and closure arrays must be the same size");
      } else if (predicates.length == 0) {
         return defaultClosure == null ? NOPClosure.nopClosure() : defaultClosure;
      } else {
         return new SwitchClosure<E>(predicates, closures, defaultClosure);
      }
   }

   private SwitchClosure(boolean clone, Predicate<? super T>[] predicates, Closure<? super T>[] closures, Closure<? super T> defaultClosure) {
      this.iPredicates = clone ? (Predicate[])FunctorUtils.copy(predicates) : predicates;
      this.iClosures = clone ? (Closure[])FunctorUtils.copy(closures) : closures;
      this.iDefault = defaultClosure == null ? NOPClosure.nopClosure() : defaultClosure;
   }

   public SwitchClosure(Predicate<? super T>[] predicates, Closure<? super T>[] closures, Closure<? super T> defaultClosure) {
      this(true, predicates, closures, defaultClosure);
   }

   public void execute(T input) {
      for(int i = 0; i < this.iPredicates.length; ++i) {
         if (this.iPredicates[i].test(input)) {
            this.iClosures[i].accept(input);
            return;
         }
      }

      this.iDefault.accept(input);
   }

   public Closure<? super T>[] getClosures() {
      return (Closure[])FunctorUtils.copy(this.iClosures);
   }

   public Closure<? super T> getDefaultClosure() {
      return this.iDefault;
   }

   public Predicate<? super T>[] getPredicates() {
      return (Predicate[])FunctorUtils.copy(this.iPredicates);
   }
}
