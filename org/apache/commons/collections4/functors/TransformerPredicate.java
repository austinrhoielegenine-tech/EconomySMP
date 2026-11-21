package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.collections4.FunctorException;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;

public final class TransformerPredicate<T> extends AbstractPredicate<T> implements Serializable {
   private static final long serialVersionUID = -2407966402920578741L;
   private final Transformer<? super T, Boolean> iTransformer;

   public static <T> Predicate<T> transformerPredicate(Transformer<? super T, Boolean> transformer) {
      return new TransformerPredicate<T>((Transformer)Objects.requireNonNull(transformer, "transformer"));
   }

   public TransformerPredicate(Transformer<? super T, Boolean> transformer) {
      this.iTransformer = transformer;
   }

   public Transformer<? super T, Boolean> getTransformer() {
      return this.iTransformer;
   }

   public boolean test(T object) {
      Boolean result = this.iTransformer.apply(object);
      if (result == null) {
         throw new FunctorException("Transformer must return an instanceof Boolean, it was a null object");
      } else {
         return result;
      }
   }
}
