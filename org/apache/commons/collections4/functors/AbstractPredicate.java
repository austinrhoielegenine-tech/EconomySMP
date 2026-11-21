package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.Predicate;

public abstract class AbstractPredicate<T> implements Predicate<T> {
   public boolean evaluate(T object) {
      return this.test(object);
   }
}
