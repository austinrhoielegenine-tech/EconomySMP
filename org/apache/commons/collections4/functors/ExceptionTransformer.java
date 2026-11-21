package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.FunctorException;
import org.apache.commons.collections4.Transformer;

public final class ExceptionTransformer<T, R> implements Transformer<T, R>, Serializable {
   private static final long serialVersionUID = 7179106032121985545L;
   public static final Transformer INSTANCE = new ExceptionTransformer();

   public static <I, O> Transformer<I, O> exceptionTransformer() {
      return INSTANCE;
   }

   private ExceptionTransformer() {
   }

   private Object readResolve() {
      return INSTANCE;
   }

   public R transform(T input) {
      throw new FunctorException("ExceptionTransformer invoked");
   }
}
