package org.apache.commons.collections4;

import java.util.function.Function;

/** @deprecated */
@Deprecated
@FunctionalInterface
public interface Transformer<T, R> extends Function<T, R> {
   default R apply(T t) {
      return (R)this.transform(t);
   }

   R transform(T var1);
}
