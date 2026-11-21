package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;

public final class TransformedPredicate<T> extends AbstractPredicate<T> implements PredicateDecorator<T>, Serializable {
   private static final long serialVersionUID = -5596090919668315834L;
   private final Transformer<? super T, ? extends T> iTransformer;
   private final Predicate<? super T> iPredicate;

   public static <T> Predicate<T> transformedPredicate(Transformer<? super T, ? extends T> transformer, Predicate<? super T> predicate) {
      return new TransformedPredicate<T>((Transformer)Objects.requireNonNull(transformer, "transformer"), (Predicate)Objects.requireNonNull(predicate, "predicate"));
   }

   public TransformedPredicate(Transformer<? super T, ? extends T> transformer, Predicate<? super T> predicate) {
      this.iTransformer = transformer;
      this.iPredicate = predicate;
   }

   public Predicate<? super T>[] getPredicates() {
      return new Predicate[]{this.iPredicate};
   }

   public Transformer<? super T, ? extends T> getTransformer() {
      return this.iTransformer;
   }

   public boolean test(T object) {
      T result = this.iTransformer.apply(object);
      return this.iPredicate.test(result);
   }
}
