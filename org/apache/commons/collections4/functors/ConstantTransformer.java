package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.collections4.Transformer;

public class ConstantTransformer<T, R> implements Transformer<T, R>, Serializable {
   private static final long serialVersionUID = 6374440726369055124L;
   public static final Transformer NULL_INSTANCE = new ConstantTransformer((Object)null);
   private final R iConstant;

   public static <I, O> Transformer<I, O> constantTransformer(O constantToReturn) {
      return (Transformer<I, O>)(constantToReturn == null ? nullTransformer() : new ConstantTransformer(constantToReturn));
   }

   public static <I, O> Transformer<I, O> nullTransformer() {
      return NULL_INSTANCE;
   }

   public ConstantTransformer(R constantToReturn) {
      this.iConstant = constantToReturn;
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof ConstantTransformer)) {
         return false;
      } else {
         Object otherConstant = ((ConstantTransformer)obj).getConstant();
         return Objects.equals(otherConstant, this.getConstant());
      }
   }

   public R getConstant() {
      return this.iConstant;
   }

   public int hashCode() {
      int result = "ConstantTransformer".hashCode() << 2;
      if (this.getConstant() != null) {
         result |= this.getConstant().hashCode();
      }

      return result;
   }

   public R transform(T input) {
      return this.iConstant;
   }
}
