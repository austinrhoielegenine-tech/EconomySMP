package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.collections4.FunctorException;
import org.apache.commons.collections4.Predicate;

public final class NullIsExceptionPredicate<T> extends AbstractPredicate<T> implements PredicateDecorator<T>, Serializable {
   private static final long serialVersionUID = 3243449850504576071L;
   private final Predicate<? super T> iPredicate;

   public static <T> Predicate<T> nullIsExceptionPredicate(Predicate<? super T> predicate) {
      return new NullIsExceptionPredicate<T>((Predicate)Objects.requireNonNull(predicate, "predicate"));
   }

   public NullIsExceptionPredicate(Predicate<? super T> predicate) {
      this.iPredicate = predicate;
   }

   public Predicate<? super T>[] getPredicates() {
      return new Predicate[]{this.iPredicate};
   }

   public boolean test(T object) {
      if (object == null) {
         throw new FunctorException("Input Object must not be null");
      } else {
         return this.iPredicate.test(object);
      }
   }
}
