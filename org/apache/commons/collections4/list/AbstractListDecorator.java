package org.apache.commons.collections4.list;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.collections4.collection.AbstractCollectionDecorator;

public abstract class AbstractListDecorator<E> extends AbstractCollectionDecorator<E> implements List<E> {
   private static final long serialVersionUID = 4500739654952315623L;

   protected AbstractListDecorator() {
   }

   protected AbstractListDecorator(List<E> list) {
      super(list);
   }

   public void add(int index, E object) {
      this.decorated().add(index, object);
   }

   public boolean addAll(int index, Collection<? extends E> coll) {
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
      return this.decorated().listIterator();
   }

   public ListIterator<E> listIterator(int index) {
      return this.decorated().listIterator(index);
   }

   public E remove(int index) {
      return (E)this.decorated().remove(index);
   }

   public E set(int index, E object) {
      return (E)this.decorated().set(index, object);
   }

   public List<E> subList(int fromIndex, int toIndex) {
      return this.decorated().subList(fromIndex, toIndex);
   }
}
