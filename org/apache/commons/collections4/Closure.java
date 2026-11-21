package org.apache.commons.collections4;

import java.util.function.Consumer;

/** @deprecated */
@Deprecated
public interface Closure<T> extends Consumer<T> {
   default void accept(T input) {
      this.execute(input);
   }

   void execute(T var1);
}
