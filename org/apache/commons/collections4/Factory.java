package org.apache.commons.collections4;

import java.util.function.Supplier;

/** @deprecated */
@Deprecated
public interface Factory<T> extends Supplier<T> {
   T create();

   default T get() {
      return (T)this.create();
   }
}
