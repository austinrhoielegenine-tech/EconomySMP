package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Predicate;

public final class IdentityPredicate<T> extends AbstractPredicate<T> implements Serializable {
   private static final long serialVersionUID = -89901658494523293L;
   private final T iValue;

   public static <T> Predicate<T> identityPredicate(T object) {
      return (Predicate<T>)(object == null ? NullPredicate.nullPredicate() : new IdentityPredicate(object));
   }

   public IdentityPredicate(T object) {
      this.iValue = object;
   }

   public T getValue() {
      return this.iValue;
   }

   public boolean test(T object) {
      return this.iValue == object;
   }
}
