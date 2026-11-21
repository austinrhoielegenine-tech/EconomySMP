package org.apache.commons.collections4.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;
import org.apache.commons.collections4.BoundedCollection;
import org.apache.commons.collections4.iterators.AbstractListIteratorDecorator;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;

public class FixedSizeList<E> extends AbstractSerializableListDecorator<E> implements BoundedCollection<E> {
   private static final long serialVersionUID = -2218010673611160319L;

   public static <E> FixedSizeList<E> fixedSizeList(List<E> list) {
      return new FixedSizeList<E>(list);
   }

   private static UnsupportedOperationException unsupportedOperationException() {
      return new UnsupportedOperationException("List is fixed size");
   }

   protected FixedSizeList(List<E> list) {
      super(list);
   }

   public boolean add(E object) {
      throw unsupportedOperationException();
   }

   public void add(int index, E object) {
      throw unsupportedOperationException();
   }

   public boolean addAll(Collection<? extends E> coll) {
      throw unsupportedOperationException();
   }

   public boolean addAll(int index, Collection<? extends E> coll) {
      throw unsupportedOperationException();
   }

   public void clear() {
      throw unsupportedOperationException();
   }

   public E get(int index) {
      return (E)this.decorated().get(index);
   }

   public int indexOf(Object object) {
      return this.decorated().indexOf(object);
   }

   public boolean isFull() {
      return true;
   }

   public Iterator<E> iterator() {
      return UnmodifiableIterator.<E>unmodifiableIterator(this.decorated().iterator());
   }

   public int lastIndexOf(Object object) {
      return this.decorated().lastIndexOf(object);
   }

   public ListIterator<E> listIterator() {
      return new FixedSizeListIterator(this.decorated().listIterator(0));
   }

   public ListIterator<E> listIterator(int index) {
      return new FixedSizeListIterator(this.decorated().listIterator(index));
   }

   public int maxSize() {
      return this.size();
   }

   public E remove(int index) {
      throw unsupportedOperationException();
   }

   public boolean remove(Object object) {
      throw unsupportedOperationException();
   }

   public boolean removeAll(Collection<?> coll) {
      throw unsupportedOperationException();
   }

   public boolean removeIf(Predicate<? super E> filter) {
      throw unsupportedOperationException();
   }

   public boolean retainAll(Collection<?> coll) {
      throw unsupportedOperationException();
   }

   public E set(int index, E object) {
      return (E)this.decorated().set(index, object);
   }

   public List<E> subList(int fromIndex, int toIndex) {
      List<E> sub = this.decorated().subList(fromIndex, toIndex);
      return new FixedSizeList<E>(sub);
   }

   private final class FixedSizeListIterator extends AbstractListIteratorDecorator<E> {
      protected FixedSizeListIterator(final ListIterator<E> iterator) {
         super(iterator);
      }

      public void add(Object object) {
         throw FixedSizeList.unsupportedOperationException();
      }

      public void remove() {
         throw FixedSizeList.unsupportedOperationException();
      }
   }
}
