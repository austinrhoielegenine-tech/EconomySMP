package org.apache.commons.collections4.functors;

import [Ljava.lang.Class;;
import [Ljava.lang.Object;;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import org.apache.commons.collections4.FunctorException;
import org.apache.commons.collections4.Transformer;

public class InvokerTransformer<T, R> implements Transformer<T, R> {
   private final String iMethodName;
   private final Class<?>[] iParamTypes;
   private final Object[] iArgs;

   public static <I, O> Transformer<I, O> invokerTransformer(String methodName) {
      return new InvokerTransformer<I, O>((String)Objects.requireNonNull(methodName, "methodName"));
   }

   public static <I, O> Transformer<I, O> invokerTransformer(String methodName, Class<?>[] paramTypes, Object[] args) {
      Objects.requireNonNull(methodName, "methodName");
      if ((paramTypes != null || args == null) && (paramTypes == null || args != null) && (paramTypes == null || args == null || paramTypes.length == args.length)) {
         return paramTypes != null && paramTypes.length != 0 ? new InvokerTransformer(methodName, paramTypes, args) : new InvokerTransformer(methodName);
      } else {
         throw new IllegalArgumentException("The parameter types must match the arguments");
      }
   }

   private InvokerTransformer(String methodName) {
      this.iMethodName = methodName;
      this.iParamTypes = null;
      this.iArgs = null;
   }

   public InvokerTransformer(String methodName, Class<?>[] paramTypes, Object[] args) {
      this.iMethodName = methodName;
      this.iParamTypes = paramTypes != null ? (Class[])((Class;)paramTypes).clone() : null;
      this.iArgs = args != null ? (Object[])((Object;)args).clone() : null;
   }

   public R transform(Object input) {
      if (input == null) {
         return null;
      } else {
         try {
            Class<?> cls = input.getClass();
            Method method = cls.getMethod(this.iMethodName, this.iParamTypes);
            return (R)method.invoke(input, this.iArgs);
         } catch (NoSuchMethodException var4) {
            throw new FunctorException("InvokerTransformer: The method '" + this.iMethodName + "' on '" + input.getClass() + "' does not exist");
         } catch (IllegalAccessException var5) {
            throw new FunctorException("InvokerTransformer: The method '" + this.iMethodName + "' on '" + input.getClass() + "' cannot be accessed");
         } catch (InvocationTargetException ex) {
            throw new FunctorException("InvokerTransformer: The method '" + this.iMethodName + "' on '" + input.getClass() + "' threw an exception", ex);
         }
      }
   }
}
