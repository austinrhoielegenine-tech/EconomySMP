package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.Transformer;

public class ClosureTransformer<T> implements Transformer<T, T>, Serializable {
   private static final long serialVersionUID = 478466901448617286L;
   private final Closure<? super T> iClosure;

   public static <T> Transformer<T, T> closureTransformer(Closure<? super T> closure) {
      return new ClosureTransformer((Closure)Objects.requireNonNull(closure, "closure"));
   }

   public ClosureTransformer(Closure<? super T> closure) {
      this.iClosure = closure;
   }

   public Closure<? super T> getClosure() {
      return this.iClosure;
   }

   public T transform(T input) {
      this.iClosure.accept(input);
      return input;
   }
}
