package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.collections4.Predicate;

public final class NullIsTruePredicate<T> extends AbstractPredicate<T> implements PredicateDecorator<T>, Serializable {
   private static final long serialVersionUID = -7625133768987126273L;
   private final Predicate<? super T> iPredicate;

   public static <T> Predicate<T> nullIsTruePredicate(Predicate<? super T> predicate) {
      return new NullIsTruePredicate<T>((Predicate)Objects.requireNonNull(predicate, "predicate"));
   }

   public NullIsTruePredicate(Predicate<? super T> predicate) {
      this.iPredicate = predicate;
   }

   public Predicate<? super T>[] getPredicates() {
      return new Predicate[]{this.iPredicate};
   }

   public boolean test(T object) {
      return object == null ? true : this.iPredicate.test(object);
   }
}
