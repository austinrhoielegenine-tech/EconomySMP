package org.apache.commons.collections4.functors;

import java.util.Collection;
import org.apache.commons.collections4.Predicate;

public final class NonePredicate<T> extends AbstractQuantifierPredicate<T> {
   private static final long serialVersionUID = 2007613066565892961L;

   public static <T> Predicate<T> nonePredicate(Collection<? extends Predicate<? super T>> predicates) {
      Predicate<? super T>[] preds = FunctorUtils.validate(predicates);
      return (Predicate<T>)(preds.length == 0 ? TruePredicate.truePredicate() : new NonePredicate(preds));
   }

   public static <T> Predicate<T> nonePredicate(Predicate<? super T>... predicates) {
      FunctorUtils.validate(predicates);
      return (Predicate<T>)(predicates.length == 0 ? TruePredicate.truePredicate() : new NonePredicate((Predicate[])FunctorUtils.copy(predicates)));
   }

   public NonePredicate(Predicate<? super T>... predicates) {
      super(predicates);
   }

   public boolean test(T object) {
      for(Predicate<? super T> iPredicate : this.iPredicates) {
         if (iPredicate.test(object)) {
            return false;
         }
      }

      return true;
   }
}
