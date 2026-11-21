package org.apache.commons.collections4.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import org.apache.commons.collections4.iterators.UnmodifiableListIterator;

public final class UnmodifiableList<E> extends AbstractSerializableListDecorator<E> implements Unmodifiable {
   private static final long serialVersionUID = 6595182819922443652L;

   public static <E> List<E> unmodifiableList(List<? extends E> list) {
      return (List<E>)(list instanceof Unmodifiable ? list : new UnmodifiableList(list));
   }

   public UnmodifiableList(List<? extends E> list) {
      super(list);
   }

   public void add(int index, E object) {
      throw new UnsupportedOperationException();
   }

   public boolean add(Object object) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(Collection<? extends E> coll) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(int index, Collection<? extends E> coll) {
      throw new UnsupportedOperationException();
   }

   public void clear() {
      throw new UnsupportedOperationException();
   }

   public Iterator<E> iterator() {
      return UnmodifiableIterator.<E>unmodifiableIterator(this.decorated().iterator());
   }

   public ListIterator<E> listIterator() {
      return UnmodifiableListIterator.<E>unmodifiableListIterator(this.decorated().listIterator());
   }

   public ListIterator<E> listIterator(int index) {
      return UnmodifiableListIterator.<E>unmodifiableListIterator(this.decorated().listIterator(index));
   }

   public E remove(int index) {
      throw new UnsupportedOperationException();
   }

   public boolean remove(Object object) {
      throw new UnsupportedOperationException();
   }

   public boolean removeAll(Collection<?> coll) {
      throw new UnsupportedOperationException();
   }

   public boolean removeIf(Predicate<? super E> filter) {
      throw new UnsupportedOperationException();
   }

   public boolean retainAll(Collection<?> coll) {
      throw new UnsupportedOperationException();
   }

   public E set(int index, E object) {
      throw new UnsupportedOperationException();
   }

   public List<E> subList(int fromIndex, int toIndex) {
      List<E> sub = this.decorated().subList(fromIndex, toIndex);
      return new UnmodifiableList<E>(sub);
   }
}
