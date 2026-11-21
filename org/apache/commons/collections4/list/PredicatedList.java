package org.apache.commons.collections4.list;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.collection.PredicatedCollection;
import org.apache.commons.collections4.iterators.AbstractListIteratorDecorator;

public class PredicatedList<E> extends PredicatedCollection<E> implements List<E> {
   private static final long serialVersionUID = -5722039223898659102L;

   public static <T> PredicatedList<T> predicatedList(List<T> list, Predicate<? super T> predicate) {
      return new PredicatedList<T>(list, predicate);
   }

   protected PredicatedList(List<E> list, Predicate<? super E> predicate) {
      super(list, predicate);
   }

   public void add(int index, E object) {
      this.validate(object);
      this.decorated().add(index, object);
   }

   public boolean addAll(int index, Collection<? extends E> coll) {
      for(E aColl : coll) {
         this.validate(aColl);
      }

      return this.decorated().addAll(index, coll);
   }

   protected List<E> decorated() {
      return (List)super.decorated();
   }

   public boolean equals(Object object) {
      return object == this || this.decorated().equals(object);
   }

   public E get(int index) {
      return (E)this.decorated().get(index);
   }

   public int hashCode() {
      return this.decorated().hashCode();
   }

   public int indexOf(Object object) {
      return this.decorated().indexOf(object);
   }

   public int lastIndexOf(Object object) {
      return this.decorated().lastIndexOf(object);
   }

   public ListIterator<E> listIterator() {
      return this.listIterator(0);
   }

   public ListIterator<E> listIterator(int i) {
      return new PredicatedListIterator(this.decorated().listIterator(i));
   }

   public E remove(int index) {
      return (E)this.decorated().remove(index);
   }

   public E set(int index, E object) {
      this.validate(object);
      return (E)this.decorated().set(index, object);
   }

   public List<E> subList(int fromIndex, int toIndex) {
      List<E> sub = this.decorated().subList(fromIndex, toIndex);
      return new PredicatedList<E>(sub, this.predicate);
   }

   protected class PredicatedListIterator extends AbstractListIteratorDecorator<E> {
      protected PredicatedListIterator(final ListIterator<E> iterator) {
         super(iterator);
      }

      public void add(E object) {
         PredicatedList.this.validate(object);
         this.getListIterator().add(object);
      }

      public void set(E object) {
         PredicatedList.this.validate(object);
         this.getListIterator().set(object);
      }
   }
}
