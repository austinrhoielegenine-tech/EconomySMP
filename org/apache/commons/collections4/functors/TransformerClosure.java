package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.Transformer;

public class TransformerClosure<T> implements Closure<T>, Serializable {
   private static final long serialVersionUID = -5194992589193388969L;
   private final Transformer<? super T, ?> iTransformer;

   public static <E> Closure<E> transformerClosure(Transformer<? super E, ?> transformer) {
      return (Closure<E>)(transformer == null ? NOPClosure.nopClosure() : new TransformerClosure(transformer));
   }

   public TransformerClosure(Transformer<? super T, ?> transformer) {
      this.iTransformer = transformer;
   }

   public void execute(T input) {
      this.iTransformer.apply(input);
   }

   public Transformer<? super T, ?> getTransformer() {
      return this.iTransformer;
   }
}
