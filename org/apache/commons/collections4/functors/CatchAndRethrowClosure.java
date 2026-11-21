package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.FunctorException;

public abstract class CatchAndRethrowClosure<T> implements Closure<T> {
   public void execute(T input) {
      try {
         this.executeAndThrow(input);
      } catch (RuntimeException ex) {
         throw ex;
      } catch (Throwable t) {
         throw new FunctorException(t);
      }
   }

   protected abstract void executeAndThrow(T var1) throws Throwable;
}
