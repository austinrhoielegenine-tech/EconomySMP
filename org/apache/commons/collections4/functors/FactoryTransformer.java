package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.Transformer;

public class FactoryTransformer<T, R> implements Transformer<T, R>, Serializable {
   private static final long serialVersionUID = -6817674502475353160L;
   private final Factory<? extends R> iFactory;

   public static <I, O> Transformer<I, O> factoryTransformer(Factory<? extends O> factory) {
      return new FactoryTransformer<I, O>((Factory)Objects.requireNonNull(factory, "factory"));
   }

   public FactoryTransformer(Factory<? extends R> factory) {
      this.iFactory = factory;
   }

   public Factory<? extends R> getFactory() {
      return this.iFactory;
   }

   public R transform(T input) {
      return this.iFactory.get();
   }
}
