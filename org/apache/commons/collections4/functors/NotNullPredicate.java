package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Predicate;

public final class NotNullPredicate<T> extends AbstractPredicate<T> implements Serializable {
   private static final long serialVersionUID = 7533784454832764388L;
   public static final Predicate INSTANCE = new NotNullPredicate();

   public static <T> Predicate<T> notNullPredicate() {
      return INSTANCE;
   }

   private NotNullPredicate() {
   }

   private Object readResolve() {
      return INSTANCE;
   }

   public boolean test(T object) {
      return object != null;
   }
}
