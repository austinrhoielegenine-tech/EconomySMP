package org.apache.commons.collections4;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.iterators.SingletonIterator;

public class FluentIterable<E> implements Iterable<E> {
   private final Iterable<E> iterable;

   public static <T> FluentIterable<T> empty() {
      return IterableUtils.EMPTY_ITERABLE;
   }

   public static <T> FluentIterable<T> of(Iterable<T> iterable) {
      Objects.requireNonNull(iterable, "iterable");
      return iterable instanceof FluentIterable ? (FluentIterable)iterable : new FluentIterable(iterable);
   }

   public static <T> FluentIterable<T> of(T singleton) {
      return of(IteratorUtils.asIterable(new SingletonIterator(singleton, false)));
   }

   public static <T> FluentIterable<T> of(T... elements) {
      return of(Arrays.asList(elements));
   }

   FluentIterable() {
      this.iterable = this;
   }

   private FluentIterable(Iterable<E> iterable) {
      this.iterable = iterable;
   }

   public boolean allMatch(Predicate<? super E> predicate) {
      return IterableUtils.matchesAll(this.iterable, predicate);
   }

   public boolean anyMatch(Predicate<? super E> predicate) {
      return IterableUtils.matchesAny(this.iterable, predicate);
   }

   public FluentIterable<E> append(E... elements) {
      return this.append(Arrays.asList(elements));
   }

   public FluentIterable<E> append(Iterable<? extends E> other) {
      return of(IterableUtils.chainedIterable(this.iterable, other));
   }

   public Enumeration<E> asEnumeration() {
      return IteratorUtils.<E>asEnumeration(this.iterator());
   }

   public FluentIterable<E> collate(Iterable<? extends E> other) {
      return of(IterableUtils.collatedIterable(this.iterable, other));
   }

   public FluentIterable<E> collate(Iterable<? extends E> other, Comparator<? super E> comparator) {
      return of(IterableUtils.collatedIterable(comparator, this.iterable, other));
   }

   public boolean contains(Object object) {
      return IterableUtils.contains(this.iterable, object);
   }

   public void copyInto(Collection<? super E> collection) {
      Objects.requireNonNull(collection, "collection");
      CollectionUtils.addAll(collection, this.iterable);
   }

   public FluentIterable<E> eval() {
      return of(this.toList());
   }

   public FluentIterable<E> filter(Predicate<? super E> predicate) {
      return of(IterableUtils.filteredIterable(this.iterable, predicate));
   }

   public void forEach(Closure<? super E> closure) {
      IterableUtils.forEach(this.iterable, closure);
   }

   public E get(int position) {
      return (E)IterableUtils.get(this.iterable, position);
   }

   public boolean isEmpty() {
      return IterableUtils.isEmpty(this.iterable);
   }

   public Iterator<E> iterator() {
      return this.iterable.iterator();
   }

   public FluentIterable<E> limit(long maxSize) {
      return of(IterableUtils.boundedIterable(this.iterable, maxSize));
   }

   public FluentIterable<E> loop() {
      return of(IterableUtils.loopingIterable(this.iterable));
   }

   public FluentIterable<E> reverse() {
      return of(IterableUtils.reversedIterable(this.iterable));
   }

   public int size() {
      return IterableUtils.size(this.iterable);
   }

   public FluentIterable<E> skip(long elementsToSkip) {
      return of(IterableUtils.skippingIterable(this.iterable, elementsToSkip));
   }

   public E[] toArray(Class<E> arrayClass) {
      return (E[])IteratorUtils.toArray(this.iterator(), arrayClass);
   }

   public List<E> toList() {
      return IterableUtils.<E>toList(this.iterable);
   }

   public String toString() {
      return IterableUtils.toString(this.iterable);
   }

   public <O> FluentIterable<O> transform(Transformer<? super E, ? extends O> transformer) {
      return of(IterableUtils.transformedIterable(this.iterable, transformer));
   }

   public FluentIterable<E> unique() {
      return of(IterableUtils.uniqueIterable(this.iterable));
   }

   public FluentIterable<E> unmodifiable() {
      return of(IterableUtils.unmodifiableIterable(this.iterable));
   }

   public FluentIterable<E> zip(Iterable<? extends E> other) {
      return of(IterableUtils.zippingIterable(this.iterable, other));
   }

   public FluentIterable<E> zip(Iterable<? extends E>... others) {
      return of(IterableUtils.zippingIterable(this.iterable, others));
   }
}
