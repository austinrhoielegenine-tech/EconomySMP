package org.apache.commons.collections4.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;
import org.apache.commons.collections4.BoundedCollection;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;

public final class UnmodifiableBoundedCollection<E> extends AbstractCollectionDecorator<E> implements BoundedCollection<E>, Unmodifiable {
   private static final long serialVersionUID = -7112672385450340330L;

   public static <E> BoundedCollection<E> unmodifiableBoundedCollection(BoundedCollection<? extends E> coll) {
      return (BoundedCollection<E>)(coll instanceof Unmodifiable ? coll : new UnmodifiableBoundedCollection(coll));
   }

   public static <E> BoundedCollection<E> unmodifiableBoundedCollection(Collection<? extends E> collection) {
      Objects.requireNonNull(collection, "collection");

      for(int i = 0; i < 1000 && !(collection instanceof BoundedCollection); ++i) {
         if (collection instanceof AbstractCollectionDecorator) {
            collection = (collection).decorated();
         } else if (collection instanceof SynchronizedCollection) {
            collection = (collection).decorated();
         }
      }

      if (!(collection instanceof BoundedCollection)) {
         throw new IllegalArgumentException("Collection is not a bounded collection.");
      } else {
         return new UnmodifiableBoundedCollection<E>((BoundedCollection)collection);
      }
   }

   private UnmodifiableBoundedCollection(BoundedCollection<? extends E> coll) {
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

   protected BoundedCollection<E> decorated() {
      return (BoundedCollection)super.decorated();
   }

   public boolean isFull() {
      return this.decorated().isFull();
   }

   public Iterator<E> iterator() {
      return UnmodifiableIterator.<E>unmodifiableIterator(this.decorated().iterator());
   }

   public int maxSize() {
      return this.decorated().maxSize();
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
