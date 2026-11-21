package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import org.apache.commons.collections4.Transformer;

public class ChainedTransformer<T> implements Transformer<T, T>, Serializable {
   private static final Transformer[] EMPTY_TRANSFORMER_ARRAY = new Transformer[0];
   private static final long serialVersionUID = 3514945074733160196L;
   private final Transformer<? super T, ? extends T>[] iTransformers;

   public static <T> Transformer<T, T> chainedTransformer(Collection<? extends Transformer<? super T, ? extends T>> transformers) {
      Objects.requireNonNull(transformers, "transformers");
      if (transformers.isEmpty()) {
         return NOPTransformer.nopTransformer();
      } else {
         Transformer<T, T>[] cmds = (Transformer[])transformers.toArray(EMPTY_TRANSFORMER_ARRAY);
         FunctorUtils.validate(cmds);
         return new ChainedTransformer(false, cmds);
      }
   }

   public static <T> Transformer<T, T> chainedTransformer(Transformer<? super T, ? extends T>... transformers) {
      FunctorUtils.validate(transformers);
      return (Transformer<T, T>)(transformers.length == 0 ? NOPTransformer.nopTransformer() : new ChainedTransformer(transformers));
   }

   private ChainedTransformer(boolean clone, Transformer<? super T, ? extends T>[] transformers) {
      this.iTransformers = clone ? (Transformer[])FunctorUtils.copy(transformers) : transformers;
   }

   public ChainedTransformer(Transformer<? super T, ? extends T>... transformers) {
      this(true, transformers);
   }

   public Transformer<? super T, ? extends T>[] getTransformers() {
      return (Transformer[])FunctorUtils.copy(this.iTransformers);
   }

   public T transform(T object) {
      for(Transformer<? super T, ? extends T> iTransformer : this.iTransformers) {
         object = iTransformer.apply(object);
      }

      return object;
   }
}
