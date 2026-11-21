package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;

public class IfTransformer<T, R> implements Transformer<T, R>, Serializable {
   private static final long serialVersionUID = 8069309411242014252L;
   private final Predicate<? super T> iPredicate;
   private final Transformer<? super T, ? extends R> iTrueTransformer;
   private final Transformer<? super T, ? extends R> iFalseTransformer;

   public static <I, O> Transformer<I, O> ifTransformer(Predicate<? super I> predicate, Transformer<? super I, ? extends O> trueTransformer, Transformer<? super I, ? extends O> falseTransformer) {
      return new IfTransformer<I, O>((Predicate)Objects.requireNonNull(predicate, "predicate"), (Transformer)Objects.requireNonNull(trueTransformer, "trueTransformer"), (Transformer)Objects.requireNonNull(falseTransformer, "falseTransformer"));
   }

   public static <T> Transformer<T, T> ifTransformer(Predicate<? super T> predicate, Transformer<? super T, ? extends T> trueTransformer) {
      return new IfTransformer<T, T>((Predicate)Objects.requireNonNull(predicate, "predicate"), (Transformer)Objects.requireNonNull(trueTransformer, "trueTransformer"), NOPTransformer.nopTransformer());
   }

   public IfTransformer(Predicate<? super T> predicate, Transformer<? super T, ? extends R> trueTransformer, Transformer<? super T, ? extends R> falseTransformer) {
      this.iPredicate = predicate;
      this.iTrueTransformer = trueTransformer;
      this.iFalseTransformer = falseTransformer;
   }

   public Transformer<? super T, ? extends R> getFalseTransformer() {
      return this.iFalseTransformer;
   }

   public Predicate<? super T> getPredicate() {
      return this.iPredicate;
   }

   public Transformer<? super T, ? extends R> getTrueTransformer() {
      return this.iTrueTransformer;
   }

   public R transform(T input) {
      return (R)(this.iPredicate.test(input) ? this.iTrueTransformer.apply(input) : this.iFalseTransformer.apply(input));
   }
}
