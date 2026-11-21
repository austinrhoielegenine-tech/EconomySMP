package org.apache.commons.collections4;

import java.util.Collection;
import org.apache.commons.collections4.functors.AllPredicate;
import org.apache.commons.collections4.functors.AndPredicate;
import org.apache.commons.collections4.functors.AnyPredicate;
import org.apache.commons.collections4.functors.EqualPredicate;
import org.apache.commons.collections4.functors.ExceptionPredicate;
import org.apache.commons.collections4.functors.FalsePredicate;
import org.apache.commons.collections4.functors.IdentityPredicate;
import org.apache.commons.collections4.functors.InstanceofPredicate;
import org.apache.commons.collections4.functors.InvokerTransformer;
import org.apache.commons.collections4.functors.NonePredicate;
import org.apache.commons.collections4.functors.NotNullPredicate;
import org.apache.commons.collections4.functors.NotPredicate;
import org.apache.commons.collections4.functors.NullIsExceptionPredicate;
import org.apache.commons.collections4.functors.NullIsFalsePredicate;
import org.apache.commons.collections4.functors.NullIsTruePredicate;
import org.apache.commons.collections4.functors.NullPredicate;
import org.apache.commons.collections4.functors.OnePredicate;
import org.apache.commons.collections4.functors.OrPredicate;
import org.apache.commons.collections4.functors.TransformedPredicate;
import org.apache.commons.collections4.functors.TransformerPredicate;
import org.apache.commons.collections4.functors.TruePredicate;
import org.apache.commons.collections4.functors.UniquePredicate;

public class PredicateUtils {
   public static <T> Predicate<T> allPredicate(Collection<? extends Predicate<? super T>> predicates) {
      return AllPredicate.allPredicate(predicates);
   }

   public static <T> Predicate<T> allPredicate(Predicate<? super T>... predicates) {
      return AllPredicate.allPredicate(predicates);
   }

   public static <T> Predicate<T> andPredicate(Predicate<? super T> predicate1, Predicate<? super T> predicate2) {
      return AndPredicate.<T>andPredicate(predicate1, predicate2);
   }

   public static <T> Predicate<T> anyPredicate(Collection<? extends Predicate<? super T>> predicates) {
      return AnyPredicate.anyPredicate(predicates);
   }

   public static <T> Predicate<T> anyPredicate(Predicate<? super T>... predicates) {
      return AnyPredicate.anyPredicate(predicates);
   }

   public static <T> Predicate<T> asPredicate(Transformer<? super T, Boolean> transformer) {
      return TransformerPredicate.<T>transformerPredicate(transformer);
   }

   public static <T> Predicate<T> eitherPredicate(Predicate<? super T> predicate1, Predicate<? super T> predicate2) {
      Predicate<T> onePredicate = onePredicate(predicate1, predicate2);
      return onePredicate;
   }

   public static <T> Predicate<T> equalPredicate(T value) {
      return EqualPredicate.<T>equalPredicate(value);
   }

   public static <T> Predicate<T> exceptionPredicate() {
      return ExceptionPredicate.<T>exceptionPredicate();
   }

   public static <T> Predicate<T> falsePredicate() {
      return FalsePredicate.<T>falsePredicate();
   }

   public static <T> Predicate<T> identityPredicate(T value) {
      return IdentityPredicate.<T>identityPredicate(value);
   }

   public static Predicate<Object> instanceofPredicate(Class<?> type) {
      return InstanceofPredicate.instanceOfPredicate(type);
   }

   public static <T> Predicate<T> invokerPredicate(String methodName) {
      return asPredicate(InvokerTransformer.invokerTransformer(methodName));
   }

   public static <T> Predicate<T> invokerPredicate(String methodName, Class<?>[] paramTypes, Object[] args) {
      return asPredicate(InvokerTransformer.invokerTransformer(methodName, paramTypes, args));
   }

   public static <T> Predicate<T> neitherPredicate(Predicate<? super T> predicate1, Predicate<? super T> predicate2) {
      Predicate<T> nonePredicate = nonePredicate(predicate1, predicate2);
      return nonePredicate;
   }

   public static <T> Predicate<T> nonePredicate(Collection<? extends Predicate<? super T>> predicates) {
      return NonePredicate.nonePredicate(predicates);
   }

   public static <T> Predicate<T> nonePredicate(Predicate<? super T>... predicates) {
      return NonePredicate.nonePredicate(predicates);
   }

   public static <T> Predicate<T> notNullPredicate() {
      return NotNullPredicate.<T>notNullPredicate();
   }

   public static <T> Predicate<T> notPredicate(Predicate<? super T> predicate) {
      return NotPredicate.<T>notPredicate(predicate);
   }

   public static <T> Predicate<T> nullIsExceptionPredicate(Predicate<? super T> predicate) {
      return NullIsExceptionPredicate.<T>nullIsExceptionPredicate(predicate);
   }

   public static <T> Predicate<T> nullIsFalsePredicate(Predicate<? super T> predicate) {
      return NullIsFalsePredicate.<T>nullIsFalsePredicate(predicate);
   }

   public static <T> Predicate<T> nullIsTruePredicate(Predicate<? super T> predicate) {
      return NullIsTruePredicate.<T>nullIsTruePredicate(predicate);
   }

   public static <T> Predicate<T> nullPredicate() {
      return NullPredicate.<T>nullPredicate();
   }

   public static <T> Predicate<T> onePredicate(Collection<? extends Predicate<? super T>> predicates) {
      return OnePredicate.onePredicate(predicates);
   }

   public static <T> Predicate<T> onePredicate(Predicate<? super T>... predicates) {
      return OnePredicate.onePredicate(predicates);
   }

   public static <T> Predicate<T> orPredicate(Predicate<? super T> predicate1, Predicate<? super T> predicate2) {
      return OrPredicate.<T>orPredicate(predicate1, predicate2);
   }

   public static <T> Predicate<T> transformedPredicate(Transformer<? super T, ? extends T> transformer, Predicate<? super T> predicate) {
      return TransformedPredicate.<T>transformedPredicate(transformer, predicate);
   }

   public static <T> Predicate<T> truePredicate() {
      return TruePredicate.<T>truePredicate();
   }

   public static <T> Predicate<T> uniquePredicate() {
      return UniquePredicate.<T>uniquePredicate();
   }

   private PredicateUtils() {
   }
}
