package org.apache.commons.collections4;

import org.apache.commons.collections4.functors.ConstantFactory;
import org.apache.commons.collections4.functors.ExceptionFactory;
import org.apache.commons.collections4.functors.InstantiateFactory;
import org.apache.commons.collections4.functors.PrototypeFactory;

public class FactoryUtils {
   public static <T> Factory<T> constantFactory(T constantToReturn) {
      return ConstantFactory.<T>constantFactory(constantToReturn);
   }

   public static <T> Factory<T> exceptionFactory() {
      return ExceptionFactory.<T>exceptionFactory();
   }

   public static <T> Factory<T> instantiateFactory(Class<T> classToInstantiate) {
      return InstantiateFactory.<T>instantiateFactory(classToInstantiate, (Class[])null, (Object[])null);
   }

   public static <T> Factory<T> instantiateFactory(Class<T> classToInstantiate, Class<?>[] paramTypes, Object[] args) {
      return InstantiateFactory.<T>instantiateFactory(classToInstantiate, paramTypes, args);
   }

   public static <T> Factory<T> nullFactory() {
      return ConstantFactory.<T>constantFactory((Object)null);
   }

   public static <T> Factory<T> prototypeFactory(T prototype) {
      return PrototypeFactory.<T>prototypeFactory(prototype);
   }

   private FactoryUtils() {
   }
}
