package org.apache.commons.collections4.iterators;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.apache.commons.collections4.IteratorUtils;

public final class ExtendedIterator<T> implements IteratorOperations<T> {
   private final boolean throwOnRemove;
   private final Iterator<? extends T> base;

   public static <T> ExtendedIterator<T> create(Iterator<T> it) {
      return it instanceof ExtendedIterator ? (ExtendedIterator)it : new ExtendedIterator(it, false);
   }

   public static <T> ExtendedIterator<T> create(Stream<T> stream) {
      return new ExtendedIterator<T>(stream.iterator(), true);
   }

   public static <T> ExtendedIterator<T> createNoRemove(Iterator<T> it) {
      return new ExtendedIterator<T>(it, true);
   }

   public static ExtendedIterator<?> emptyIterator() {
      return new ExtendedIterator(Collections.emptyIterator(), false);
   }

   public static <T> ExtendedIterator<T> flatten(Iterator<Iterator<T>> iterators) {
      return create(IteratorUtils.chainedIterator(iterators));
   }

   private ExtendedIterator(Iterator<? extends T> base, boolean throwOnRemove) {
      this.base = base;
      this.throwOnRemove = throwOnRemove;
   }

   public <X extends T> ExtendedIterator<T> andThen(Iterator<X> other) {
      if (this.base instanceof IteratorChain) {
         ((IteratorChain)this.base).addIterator(other);
         return this;
      } else {
         return new ExtendedIterator<T>(new IteratorChain(this.base, other), this.throwOnRemove);
      }
   }

   public ExtendedIterator<T> filter(Predicate<T> predicate) {
      Objects.requireNonNull(predicate);
      return new ExtendedIterator<T>(new FilterIterator(this, predicate::test), this.throwOnRemove);
   }

   public void forEachRemaining(Consumer<? super T> action) {
      this.base.forEachRemaining(action);
   }

   public boolean hasNext() {
      return this.base.hasNext();
   }

   public <U> ExtendedIterator<U> map(Function<T, U> function) {
      Objects.requireNonNull(function);
      return new ExtendedIterator<U>(new TransformIterator(this, function::apply), false);
   }

   public T next() {
      return (T)this.base.next();
   }

   public void remove() {
      if (this.throwOnRemove) {
         throw new UnsupportedOperationException();
      } else {
         this.base.remove();
      }
   }
}
