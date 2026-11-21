package org.apache.commons.collections4;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.collections4.functors.ChainedClosure;
import org.apache.commons.collections4.functors.EqualPredicate;
import org.apache.commons.collections4.functors.ExceptionClosure;
import org.apache.commons.collections4.functors.ForClosure;
import org.apache.commons.collections4.functors.IfClosure;
import org.apache.commons.collections4.functors.InvokerTransformer;
import org.apache.commons.collections4.functors.NOPClosure;
import org.apache.commons.collections4.functors.SwitchClosure;
import org.apache.commons.collections4.functors.TransformerClosure;
import org.apache.commons.collections4.functors.WhileClosure;

public class ClosureUtils {
   public static <E> Closure<E> asClosure(Transformer<? super E, ?> transformer) {
      return TransformerClosure.<E>transformerClosure(transformer);
   }

   public static <E> Closure<E> chainedClosure(Closure<? super E>... closures) {
      return ChainedClosure.chainedClosure(closures);
   }

   public static <E> Closure<E> chainedClosure(Collection<? extends Closure<? super E>> closures) {
      return ChainedClosure.chainedClosure(closures);
   }

   public static <E> Closure<E> doWhileClosure(Closure<? super E> closure, Predicate<? super E> predicate) {
      return WhileClosure.<E>whileClosure(predicate, closure, true);
   }

   public static <E> Closure<E> exceptionClosure() {
      return ExceptionClosure.<E>exceptionClosure();
   }

   public static <E> Closure<E> forClosure(int count, Closure<? super E> closure) {
      return ForClosure.<E>forClosure(count, closure);
   }

   public static <E> Closure<E> ifClosure(Predicate<? super E> predicate, Closure<? super E> trueClosure) {
      return IfClosure.<E>ifClosure(predicate, trueClosure);
   }

   public static <E> Closure<E> ifClosure(Predicate<? super E> predicate, Closure<? super E> trueClosure, Closure<? super E> falseClosure) {
      return IfClosure.<E>ifClosure(predicate, trueClosure, falseClosure);
   }

   public static <E> Closure<E> invokerClosure(String methodName) {
      return asClosure(InvokerTransformer.invokerTransformer(methodName));
   }

   public static <E> Closure<E> invokerClosure(String methodName, Class<?>[] paramTypes, Object[] args) {
      return asClosure(InvokerTransformer.invokerTransformer(methodName, paramTypes, args));
   }

   public static <E> Closure<E> nopClosure() {
      return NOPClosure.<E>nopClosure();
   }

   public static <E> Closure<E> switchClosure(Map<Predicate<E>, Closure<E>> predicatesAndClosures) {
      return SwitchClosure.<E>switchClosure(predicatesAndClosures);
   }

   public static <E> Closure<E> switchClosure(Predicate<? super E>[] predicates, Closure<? super E>[] closures) {
      return SwitchClosure.<E>switchClosure(predicates, closures, (Closure)null);
   }

   public static <E> Closure<E> switchClosure(Predicate<? super E>[] predicates, Closure<? super E>[] closures, Closure<? super E> defaultClosure) {
      return SwitchClosure.<E>switchClosure(predicates, closures, defaultClosure);
   }

   public static <E> Closure<E> switchMapClosure(Map<? extends E, Closure<E>> objectsAndClosures) {
      Objects.requireNonNull(objectsAndClosures, "objectsAndClosures");
      Closure<? super E> def = (Closure)objectsAndClosures.remove((Object)null);
      int size = objectsAndClosures.size();
      Closure<? super E>[] trs = new Closure[size];
      Predicate<E>[] preds = new Predicate[size];
      int i = 0;

      for(Map.Entry<? extends E, Closure<E>> entry : objectsAndClosures.entrySet()) {
         preds[i] = EqualPredicate.equalPredicate(entry.getKey());
         trs[i] = (Closure)entry.getValue();
         ++i;
      }

      return switchClosure(preds, trs, def);
   }

   public static <E> Closure<E> whileClosure(Predicate<? super E> predicate, Closure<? super E> closure) {
      return WhileClosure.<E>whileClosure(predicate, closure, false);
   }

   private ClosureUtils() {
   }
}
