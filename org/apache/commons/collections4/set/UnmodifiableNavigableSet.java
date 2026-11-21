package org.apache.commons.collections4.set;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.function.Predicate;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;

public final class UnmodifiableNavigableSet<E> extends AbstractNavigableSetDecorator<E> implements Unmodifiable {
   private static final long serialVersionUID = 20150528L;

   public static <E> NavigableSet<E> unmodifiableNavigableSet(NavigableSet<E> set) {
      return (NavigableSet<E>)(set instanceof Unmodifiable ? set : new UnmodifiableNavigableSet(set));
   }

   private UnmodifiableNavigableSet(NavigableSet<E> set) {
      super(set);
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

   public Iterator<E> descendingIterator() {
      return UnmodifiableIterator.<E>unmodifiableIterator(this.decorated().descendingIterator());
   }

   public NavigableSet<E> descendingSet() {
      return unmodifiableNavigableSet(this.decorated().descendingSet());
   }

   public SortedSet<E> headSet(E toElement) {
      SortedSet<E> head = this.decorated().headSet(toElement);
      return UnmodifiableSortedSet.<E>unmodifiableSortedSet(head);
   }

   public NavigableSet<E> headSet(E toElement, boolean inclusive) {
      NavigableSet<E> head = this.decorated().headSet(toElement, inclusive);
      return unmodifiableNavigableSet(head);
   }

   public Iterator<E> iterator() {
      return UnmodifiableIterator.<E>unmodifiableIterator(this.decorated().iterator());
   }

   public E pollFirst() {
      throw new UnsupportedOperationException();
   }

   public E pollLast() {
      throw new UnsupportedOperationException();
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      this.setCollection((Collection)in.readObject());
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

   public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
      NavigableSet<E> sub = this.decorated().subSet(fromElement, fromInclusive, toElement, toInclusive);
      return unmodifiableNavigableSet(sub);
   }

   public SortedSet<E> subSet(E fromElement, E toElement) {
      SortedSet<E> sub = this.decorated().subSet(fromElement, toElement);
      return UnmodifiableSortedSet.<E>unmodifiableSortedSet(sub);
   }

   public SortedSet<E> tailSet(E fromElement) {
      SortedSet<E> tail = this.decorated().tailSet(fromElement);
      return UnmodifiableSortedSet.<E>unmodifiableSortedSet(tail);
   }

   public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
      NavigableSet<E> tail = this.decorated().tailSet(fromElement, inclusive);
      return unmodifiableNavigableSet(tail);
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      out.writeObject(this.decorated());
   }
}
