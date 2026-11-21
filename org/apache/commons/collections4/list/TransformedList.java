package org.apache.commons.collections4.list;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.collection.TransformedCollection;
import org.apache.commons.collections4.iterators.AbstractListIteratorDecorator;

public class TransformedList<E> extends TransformedCollection<E> implements List<E> {
   private static final long serialVersionUID = 1077193035000013141L;

   public static <E> TransformedList<E> transformedList(List<E> list, Transformer<? super E, ? extends E> transformer) {
      TransformedList<E> decorated = new TransformedList<E>(list, transformer);
      if (!list.isEmpty()) {
         E[] values = (E[])list.toArray();
         list.clear();

         for(E value : values) {
            decorated.decorated().add(transformer.apply(value));
         }
      }

      return decorated;
   }

   public static <E> TransformedList<E> transformingList(List<E> list, Transformer<? super E, ? extends E> transformer) {
      return new TransformedList<E>(list, transformer);
   }

   protected TransformedList(List<E> list, Transformer<? super E, ? extends E> transformer) {
      super(list, transformer);
   }

   public void add(int index, E object) {
      object = (E)this.transform(object);
      this.getList().add(index, object);
   }

   public boolean addAll(int index, Collection<? extends E> coll) {
      coll = this.transform(coll);
      return this.getList().addAll(index, coll);
   }

   public boolean equals(Object object) {
      return object == this || this.decorated().equals(object);
   }

   public E get(int index) {
      return (E)this.getList().get(index);
   }

   protected List<E> getList() {
      return (List)this.decorated();
   }

   public int hashCode() {
      return this.decorated().hashCode();
   }

   public int indexOf(Object object) {
      return this.getList().indexOf(object);
   }

   public int lastIndexOf(Object object) {
      return this.getList().lastIndexOf(object);
   }

   public ListIterator<E> listIterator() {
      return this.listIterator(0);
   }

   public ListIterator<E> listIterator(int i) {
      return new TransformedListIterator(this.getList().listIterator(i));
   }

   public E remove(int index) {
      return (E)this.getList().remove(index);
   }

   public E set(int index, E object) {
      object = (E)this.transform(object);
      return (E)this.getList().set(index, object);
   }

   public List<E> subList(int fromIndex, int toIndex) {
      List<E> sub = this.getList().subList(fromIndex, toIndex);
      return new TransformedList<E>(sub, this.transformer);
   }

   protected class TransformedListIterator extends AbstractListIteratorDecorator<E> {
      protected TransformedListIterator(final ListIterator<E> iterator) {
         super(iterator);
      }

      public void add(E object) {
         object = (E)TransformedList.this.transform(object);
         this.getListIterator().add(object);
      }

      public void set(E object) {
         this.getListIterator().set(TransformedList.this.transform(object));
      }
   }
}
