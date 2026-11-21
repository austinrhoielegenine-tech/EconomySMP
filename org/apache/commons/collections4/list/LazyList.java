package org.apache.commons.collections4.list;

import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.Transformer;

public class LazyList<E> extends AbstractSerializableListDecorator<E> {
   private static final long serialVersionUID = -3677737457567429713L;
   private final Factory<? extends E> factory;
   private final Transformer<Integer, ? extends E> transformer;

   public static <E> LazyList<E> lazyList(List<E> list, Factory<? extends E> factory) {
      return new LazyList<E>(list, factory);
   }

   public static <E> LazyList<E> lazyList(List<E> list, Transformer<Integer, ? extends E> transformer) {
      return new LazyList<E>(list, transformer);
   }

   protected LazyList(List<E> list, Factory<? extends E> factory) {
      super(list);
      this.factory = (Factory)Objects.requireNonNull(factory);
      this.transformer = null;
   }

   protected LazyList(List<E> list, Transformer<Integer, ? extends E> transformer) {
      super(list);
      this.factory = null;
      this.transformer = (Transformer)Objects.requireNonNull(transformer);
   }

   private E element(int index) {
      if (this.factory != null) {
         return this.factory.get();
      } else if (this.transformer != null) {
         return this.transformer.apply(index);
      } else {
         throw new IllegalStateException("Factory and Transformer are both null!");
      }
   }

   public E get(int index) {
      int size = this.decorated().size();
      if (index < size) {
         E object = (E)this.decorated().get(index);
         if (object == null) {
            object = (E)this.element(index);
            this.decorated().set(index, object);
         }

         return object;
      } else {
         for(int i = size; i < index; ++i) {
            this.decorated().add((Object)null);
         }

         E object = (E)this.element(index);
         this.decorated().add(object);
         return object;
      }
   }

   public List<E> subList(int fromIndex, int toIndex) {
      List<E> sub = this.decorated().subList(fromIndex, toIndex);
      if (this.factory != null) {
         return new LazyList<E>(sub, this.factory);
      } else if (this.transformer != null) {
         return new LazyList<E>(sub, this.transformer);
      } else {
         throw new IllegalStateException("Factory and Transformer are both null!");
      }
   }
}
