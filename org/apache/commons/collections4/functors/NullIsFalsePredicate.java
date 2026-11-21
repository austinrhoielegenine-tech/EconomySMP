package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.collections4.Predicate;

public final class NullIsFalsePredicate<T> extends AbstractPredicate<T> implements PredicateDecorator<T>, Serializable {
   private static final long serialVersionUID = -2997501534564735525L;
   private final Predicate<? super T> iPredicate;

   public static <T> Predicate<T> nullIsFalsePredicate(Predicate<? super T> predicate) {
      return new NullIsFalsePredicate<T>((Predicate)Objects.requireNonNull(predicate, "predicate"));
   }

   public NullIsFalsePredicate(Predicate<? super T> predicate) {
      this.iPredicate = predicate;
   }

   public Predicate<? super T>[] getPredicates() {
      return new Predicate[]{this.iPredicate};
   }

   public boolean test(T object) {
      return object == null ? false : this.iPredicate.test(object);
   }
}
