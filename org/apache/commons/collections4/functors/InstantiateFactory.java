package org.apache.commons.collections4.functors;

import [Ljava.lang.Class;;
import [Ljava.lang.Object;;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.FunctorException;

public class InstantiateFactory<T> implements Factory<T> {
   private final Class<T> iClassToInstantiate;
   private final Class<?>[] iParamTypes;
   private final Object[] iArgs;
   private transient Constructor<T> iConstructor;

   public static <T> Factory<T> instantiateFactory(Class<T> classToInstantiate, Class<?>[] paramTypes, Object[] args) {
      Objects.requireNonNull(classToInstantiate, "classToInstantiate");
      if ((paramTypes != null || args == null) && (paramTypes == null || args != null) && (paramTypes == null || args == null || paramTypes.length == args.length)) {
         return paramTypes != null && paramTypes.length != 0 ? new InstantiateFactory(classToInstantiate, paramTypes, args) : new InstantiateFactory(classToInstantiate);
      } else {
         throw new IllegalArgumentException("Parameter types must match the arguments");
      }
   }

   public InstantiateFactory(Class<T> classToInstantiate) {
      this.iClassToInstantiate = classToInstantiate;
      this.iParamTypes = null;
      this.iArgs = null;
      this.findConstructor();
   }

   public InstantiateFactory(Class<T> classToInstantiate, Class<?>[] paramTypes, Object[] args) {
      this.iClassToInstantiate = classToInstantiate;
      this.iParamTypes = (Class[])((Class;)paramTypes).clone();
      this.iArgs = ((Object;)args).clone();
      this.findConstructor();
   }

   public T create() {
      if (this.iConstructor == null) {
         this.findConstructor();
      }

      try {
         return (T)this.iConstructor.newInstance(this.iArgs);
      } catch (InstantiationException ex) {
         throw new FunctorException("InstantiateFactory: InstantiationException", ex);
      } catch (IllegalAccessException ex) {
         throw new FunctorException("InstantiateFactory: Constructor must be public", ex);
      } catch (InvocationTargetException ex) {
         throw new FunctorException("InstantiateFactory: Constructor threw an exception", ex);
      }
   }

   private void findConstructor() {
      try {
         this.iConstructor = this.iClassToInstantiate.getConstructor(this.iParamTypes);
      } catch (NoSuchMethodException var2) {
         throw new IllegalArgumentException("InstantiateFactory: The constructor must exist and be public ");
      }
   }
}
