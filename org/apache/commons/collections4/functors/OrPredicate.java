package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.collections4.Predicate;

public final class OrPredicate<T> extends AbstractPredicate<T> implements PredicateDecorator<T>, Serializable {
   private static final long serialVersionUID = -8791518325735182855L;
   private final Predicate<? super T> iPredicate1;
   private final Predicate<? super T> iPredicate2;

   public static <T> Predicate<T> orPredicate(Predicate<? super T> predicate1, Predicate<? super T> predicate2) {
      return new OrPredicate<T>((Predicate)Objects.requireNonNull(predicate1, "predicate1"), (Predicate)Objects.requireNonNull(predicate2, "predicate2"));
   }

   public OrPredicate(Predicate<? super T> predicate1, Predicate<? super T> predicate2) {
      this.iPredicate1 = predicate1;
      this.iPredicate2 = predicate2;
   }

   public Predicate<? super T>[] getPredicates() {
      return new Predicate[]{this.iPredicate1, this.iPredicate2};
   }

   public boolean test(T object) {
      return this.iPredicate1.test(object) || this.iPredicate2.test(object);
   }
}
