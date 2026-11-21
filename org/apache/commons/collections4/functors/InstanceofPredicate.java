package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.collections4.Predicate;

public final class InstanceofPredicate extends AbstractPredicate<Object> implements Serializable {
   private static final long serialVersionUID = -6682656911025165584L;
   private final Class<?> iType;

   public static Predicate<Object> instanceOfPredicate(Class<?> type) {
      return new InstanceofPredicate((Class)Objects.requireNonNull(type, "type"));
   }

   public InstanceofPredicate(Class<?> type) {
      this.iType = type;
   }

   public Class<?> getType() {
      return this.iType;
   }

   public boolean test(Object object) {
      return this.iType.isInstance(object);
   }
}
