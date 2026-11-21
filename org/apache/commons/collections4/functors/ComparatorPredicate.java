package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import org.apache.commons.collections4.Predicate;

public class ComparatorPredicate<T> extends AbstractPredicate<T> implements Serializable {
   private static final long serialVersionUID = -1863209236504077399L;
   private final T object;
   private final Comparator<T> comparator;
   private final Criterion criterion;

   public static <T> Predicate<T> comparatorPredicate(T object, Comparator<T> comparator) {
      return comparatorPredicate(object, comparator, ComparatorPredicate.Criterion.EQUAL);
   }

   public static <T> Predicate<T> comparatorPredicate(T object, Comparator<T> comparator, Criterion criterion) {
      return new ComparatorPredicate<T>(object, (Comparator)Objects.requireNonNull(comparator, "comparator"), (Criterion)Objects.requireNonNull(criterion, "criterion"));
   }

   public ComparatorPredicate(T object, Comparator<T> comparator, Criterion criterion) {
      this.object = object;
      this.comparator = comparator;
      this.criterion = criterion;
   }

   public boolean test(T target) {
      boolean result = false;
      int comparison = this.comparator.compare(this.object, target);
      switch (this.criterion.ordinal()) {
         case 0:
            result = comparison == 0;
            break;
         case 1:
            result = comparison > 0;
            break;
         case 2:
            result = comparison < 0;
            break;
         case 3:
            result = comparison >= 0;
            break;
         case 4:
            result = comparison <= 0;
            break;
         default:
            throw new IllegalStateException("The current criterion '" + this.criterion + "' is invalid.");
      }

      return result;
   }

   public static enum Criterion {
      EQUAL,
      GREATER,
      LESS,
      GREATER_OR_EQUAL,
      LESS_OR_EQUAL;

      // $FF: synthetic method
      private static Criterion[] $values() {
         return new Criterion[]{EQUAL, GREATER, LESS, GREATER_OR_EQUAL, LESS_OR_EQUAL};
      }
   }
}
