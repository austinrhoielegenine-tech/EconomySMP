package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import org.apache.commons.collections4.Closure;

public class ChainedClosure<T> implements Closure<T>, Serializable {
   private static final long serialVersionUID = -3520677225766901240L;
   private final Closure<? super T>[] iClosures;

   public static <E> Closure<E> chainedClosure(Closure<? super E>... closures) {
      FunctorUtils.validate(closures);
      return (Closure<E>)(closures.length == 0 ? NOPClosure.nopClosure() : new ChainedClosure(closures));
   }

   public static <E> Closure<E> chainedClosure(Collection<? extends Closure<? super E>> closures) {
      Objects.requireNonNull(closures, "closures");
      if (closures.isEmpty()) {
         return NOPClosure.<E>nopClosure();
      } else {
         Closure<? super E>[] cmds = new Closure[closures.size()];
         int i = 0;

         for(Closure<? super E> closure : closures) {
            cmds[i++] = closure;
         }

         FunctorUtils.validate(cmds);
         return new ChainedClosure<E>(false, cmds);
      }
   }

   private ChainedClosure(boolean clone, Closure<? super T>... closures) {
      this.iClosures = clone ? (Closure[])FunctorUtils.copy(closures) : closures;
   }

   public ChainedClosure(Closure<? super T>... closures) {
      this(true, closures);
   }

   public void execute(T input) {
      for(Closure<? super T> iClosure : this.iClosures) {
         iClosure.accept(input);
      }

   }

   public Closure<? super T>[] getClosures() {
      return (Closure[])FunctorUtils.copy(this.iClosures);
   }
}
