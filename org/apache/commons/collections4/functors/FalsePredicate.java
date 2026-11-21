package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Predicate;

public final class FalsePredicate<T> extends AbstractPredicate<T> implements Serializable {
   private static final long serialVersionUID = 7533784454832764388L;
   public static final Predicate INSTANCE = new FalsePredicate();

   public static <T> Predicate<T> falsePredicate() {
      return INSTANCE;
   }

   private FalsePredicate() {
   }

   private Object readResolve() {
      return INSTANCE;
   }

   public boolean test(T object) {
      return false;
   }
}
