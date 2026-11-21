package org.apache.commons.collections4.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;

public final class UnmodifiableCollection<E> extends AbstractCollectionDecorator<E> implements Unmodifiable {
   private static final long serialVersionUID = -239892006883819945L;

   public static <T> Collection<T> unmodifiableCollection(Collection<? extends T> coll) {
      return (Collection<T>)(coll instanceof Unmodifiable ? coll : new UnmodifiableCollection(coll));
   }

   private UnmodifiableCollection(Collection<? extends E> coll) {
      super(coll);
   }

   public boolean add(E object) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(Collection<? extends E> coll) {
      throw new UnsupportedOperationException();
   }

   public void clear() {
      throw new UnsupportedOperationException();
   }

   public Iterator<E> iterator() {
      return UnmodifiableIterator.<E>unmodifiableIterator(this.decorated().iterator());
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
}
