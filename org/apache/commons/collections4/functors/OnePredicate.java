package org.apache.commons.collections4.functors;

import java.util.Collection;
import org.apache.commons.collections4.Predicate;

public final class OnePredicate<T> extends AbstractQuantifierPredicate<T> {
   private static final long serialVersionUID = -8125389089924745785L;

   public static <T> Predicate<T> onePredicate(Collection<? extends Predicate<? super T>> predicates) {
      Predicate<? super T>[] preds = FunctorUtils.validate(predicates);
      return new OnePredicate<T>(preds);
   }

   public static <T> Predicate<T> onePredicate(Predicate<? super T>... predicates) {
      FunctorUtils.validate(predicates);
      if (predicates.length == 0) {
         return FalsePredicate.<T>falsePredicate();
      } else {
         return (Predicate<T>)(predicates.length == 1 ? predicates[0] : new OnePredicate((Predicate[])FunctorUtils.copy(predicates)));
      }
   }

   public OnePredicate(Predicate<? super T>... predicates) {
      super(predicates);
   }

   public boolean test(T object) {
      boolean match = false;

      for(Predicate<? super T> iPredicate : this.iPredicates) {
         if (iPredicate.test(object)) {
            if (match) {
               return false;
            }

            match = true;
         }
      }

      return match;
   }
}
