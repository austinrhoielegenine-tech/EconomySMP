package org.apache.commons.collections4.functors;

import [Ljava.util.function.Consumer;;
import [Ljava.util.function.Function;;
import [Ljava.util.function.Predicate;;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

final class FunctorUtils {
   static <R extends Predicate<T>, P extends Predicate<? super T>, T> R coerce(P predicate) {
      return (R)predicate;
   }

   static <R extends Function<I, O>, P extends Function<? super I, ? extends O>, I, O> R coerce(P transformer) {
      return (R)transformer;
   }

   static <T extends Consumer<?>> T[] copy(T... consumers) {
      return (T[])(consumers == null ? null : (Consumer[])((Consumer;)consumers).clone());
   }

   static <T extends Predicate<?>> T[] copy(T... predicates) {
      return (T[])(predicates == null ? null : (Predicate[])((Predicate;)predicates).clone());
   }

   static <T extends Function<?, ?>> T[] copy(T... transformers) {
      return (T[])(transformers == null ? null : (Function[])((Function;)transformers).clone());
   }

   static <T> org.apache.commons.collections4.Predicate<? super T>[] validate(Collection<? extends Predicate<? super T>> predicates) {
      Objects.requireNonNull(predicates, "predicates");
      org.apache.commons.collections4.Predicate<? super T>[] preds = new org.apache.commons.collections4.Predicate[predicates.size()];
      int i = 0;

      for(Predicate<? super T> predicate : predicates) {
         preds[i] = (org.apache.commons.collections4.Predicate)predicate;
         if (preds[i] == null) {
            throw new NullPointerException("predicates[" + i + "]");
         }

         ++i;
      }

      return preds;
   }

   static void validate(Consumer<?>... consumers) {
      Objects.requireNonNull(consumers, "closures");

      for(int i = 0; i < consumers.length; ++i) {
         if (consumers[i] == null) {
            throw new NullPointerException("closures[" + i + "]");
         }
      }

   }

   static void validate(Function<?, ?>... functions) {
      Objects.requireNonNull(functions, "functions");

      for(int i = 0; i < functions.length; ++i) {
         if (functions[i] == null) {
            throw new NullPointerException("functions[" + i + "]");
         }
      }

   }

   static void validate(Predicate<?>... predicates) {
      Objects.requireNonNull(predicates, "predicates");

      for(int i = 0; i < predicates.length; ++i) {
         if (predicates[i] == null) {
            throw new NullPointerException("predicates[" + i + "]");
         }
      }

   }

   private FunctorUtils() {
   }
}
