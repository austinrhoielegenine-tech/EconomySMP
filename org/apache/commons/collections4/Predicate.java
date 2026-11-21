package org.apache.commons.collections4;

/** @deprecated */
@Deprecated
public interface Predicate<T> extends java.util.function.Predicate<T> {
   boolean evaluate(T var1);

   default boolean test(T t) {
      return this.evaluate(t);
   }
}
