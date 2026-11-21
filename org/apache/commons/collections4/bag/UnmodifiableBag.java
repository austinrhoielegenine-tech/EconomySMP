package org.apache.commons.collections4.bag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import org.apache.commons.collections4.set.UnmodifiableSet;

public final class UnmodifiableBag<E> extends AbstractBagDecorator<E> implements Unmodifiable {
   private static final long serialVersionUID = -1873799975157099624L;

   public static <E> Bag<E> unmodifiableBag(Bag<? extends E> bag) {
      return (Bag<E>)(bag instanceof Unmodifiable ? bag : new UnmodifiableBag(bag));
   }

   private UnmodifiableBag(Bag<? extends E> bag) {
      super(bag);
   }

   public boolean add(E object) {
      throw new UnsupportedOperationException();
   }

   public boolean add(E object, int count) {
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

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      this.setCollection((Collection)in.readObject());
   }

   public boolean remove(Object object) {
      throw new UnsupportedOperationException();
   }

   public boolean remove(Object object, int count) {
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

   public Set<E> uniqueSet() {
      Set<E> set = this.decorated().uniqueSet();
      return UnmodifiableSet.<E>unmodifiableSet(set);
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      out.writeObject(this.decorated());
   }
}
