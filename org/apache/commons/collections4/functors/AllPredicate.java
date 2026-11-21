package org.apache.commons.collections4.functors;

import java.util.Collection;
import org.apache.commons.collections4.Predicate;

public final class AllPredicate<T> extends AbstractQuantifierPredicate<T> {
   private static final long serialVersionUID = -3094696765038308799L;

   public static <T> Predicate<T> allPredicate(Collection<? extends Predicate<? super T>> predicates) {
      Predicate<? super T>[] preds = FunctorUtils.validate(predicates);
      if (preds.length == 0) {
         return TruePredicate.<T>truePredicate();
      } else {
         return (Predicate<T>)(preds.length == 1 ? (Predicate)FunctorUtils.coerce(preds[0]) : new AllPredicate(preds));
      }
   }

   public static <T> Predicate<T> allPredicate(Predicate<? super T>... predicates) {
      FunctorUtils.validate(predicates);
      if (predicates.length == 0) {
         return TruePredicate.<T>truePredicate();
      } else {
         return (Predicate<T>)(predicates.length == 1 ? (Predicate)FunctorUtils.coerce(predicates[0]) : new AllPredicate((Predicate[])FunctorUtils.copy(predicates)));
      }
   }

   public AllPredicate(Predicate<? super T>... predicates) {
      super(predicates);
   }

   public boolean test(T object) {
      for(Predicate<? super T> iPredicate : this.iPredicates) {
         if (!iPredicate.test(object)) {
            return false;
         }
      }

      return true;
   }
}
