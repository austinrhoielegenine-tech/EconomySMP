package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Factory;

public class ConstantFactory<T> implements Factory<T>, Serializable {
   private static final long serialVersionUID = -3520677225766901240L;
   public static final Factory NULL_INSTANCE = new ConstantFactory((Object)null);
   private final T iConstant;

   public static <T> Factory<T> constantFactory(T constantToReturn) {
      return (Factory<T>)(constantToReturn == null ? NULL_INSTANCE : new ConstantFactory(constantToReturn));
   }

   public ConstantFactory(T constantToReturn) {
      this.iConstant = constantToReturn;
   }

   public T create() {
      return this.iConstant;
   }

   public T getConstant() {
      return this.iConstant;
   }
}
