package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.FunctorException;
import org.apache.commons.collections4.Predicate;

public final class ExceptionPredicate<T> extends AbstractPredicate<T> implements Serializable {
   private static final long serialVersionUID = 7179106032121985545L;
   public static final Predicate INSTANCE = new ExceptionPredicate();

   public static <T> Predicate<T> exceptionPredicate() {
      return INSTANCE;
   }

   private ExceptionPredicate() {
   }

   private Object readResolve() {
      return INSTANCE;
   }

   public boolean test(T object) {
      throw new FunctorException("ExceptionPredicate invoked");
   }
}
