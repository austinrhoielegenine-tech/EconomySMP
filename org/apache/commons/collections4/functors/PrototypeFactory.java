package org.apache.commons.collections4.functors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.FunctorException;

public class PrototypeFactory {
   public static <T> Factory<T> prototypeFactory(T prototype) {
      if (prototype == null) {
         return ConstantFactory.<T>constantFactory((Object)null);
      } else {
         try {
            Method method = prototype.getClass().getMethod("clone", (Class[])null);
            return new PrototypeCloneFactory<T>(prototype, method);
         } catch (NoSuchMethodException var4) {
            try {
               prototype.getClass().getConstructor(prototype.getClass());
               return new InstantiateFactory<T>(prototype.getClass(), new Class[]{prototype.getClass()}, new Object[]{prototype});
            } catch (NoSuchMethodException var3) {
               if (prototype instanceof Serializable) {
                  return new PrototypeSerializationFactory<T>((Serializable)prototype);
               } else {
                  throw new IllegalArgumentException("The prototype must be cloneable via a public clone method");
               }
            }
         }
      }
   }

   private PrototypeFactory() {
   }

   static class PrototypeCloneFactory<T> implements Factory<T> {
      private final T iPrototype;
      private transient Method iCloneMethod;

      private PrototypeCloneFactory(T prototype, Method method) {
         this.iPrototype = prototype;
         this.iCloneMethod = method;
      }

      public T create() {
         if (this.iCloneMethod == null) {
            this.findCloneMethod();
         }

         try {
            return (T)this.iCloneMethod.invoke(this.iPrototype, (Object[])null);
         } catch (IllegalAccessException ex) {
            throw new FunctorException("PrototypeCloneFactory: Clone method must be public", ex);
         } catch (InvocationTargetException ex) {
            throw new FunctorException("PrototypeCloneFactory: Clone method threw an exception", ex);
         }
      }

      private void findCloneMethod() {
         try {
            this.iCloneMethod = this.iPrototype.getClass().getMethod("clone", (Class[])null);
         } catch (NoSuchMethodException var2) {
            throw new IllegalArgumentException("PrototypeCloneFactory: The clone method must exist and be public ");
         }
      }
   }

   static class PrototypeSerializationFactory<T extends Serializable> implements Factory<T> {
      private final T iPrototype;

      private PrototypeSerializationFactory(T prototype) {
         this.iPrototype = prototype;
      }

      public T create() {
         ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
         ByteArrayInputStream bais = null;

         Serializable var5;
         try {
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(this.iPrototype);
            bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream in = new ObjectInputStream(bais);
            var5 = (Serializable)in.readObject();
         } catch (IOException | ClassNotFoundException ex) {
            throw new FunctorException(ex);
         } finally {
            try {
               if (bais != null) {
                  bais.close();
               }
            } catch (IOException var16) {
            }

            try {
               baos.close();
            } catch (IOException var15) {
            }

         }

         return (T)var5;
      }
   }
}
