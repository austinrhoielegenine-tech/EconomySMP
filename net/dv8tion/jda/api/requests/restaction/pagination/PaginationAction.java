package net.dv8tion.jda.api.requests.restaction.pagination;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.Procedure;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.Unmodifiable;

public interface PaginationAction<T, M extends PaginationAction<T, M>> extends RestAction<@Unmodifiable List<T>>, Iterable<T> {
   @Nonnull
   @CheckReturnValue
   M skipTo(long var1);

   long getLastKey();

   @Nonnull
   @CheckReturnValue
   M setCheck(@Nullable BooleanSupplier var1);

   @Nonnull
   @CheckReturnValue
   M timeout(long var1, @Nonnull TimeUnit var3);

   @Nonnull
   @CheckReturnValue
   M deadline(long var1);

   @Nonnull
   default EnumSet<PaginationOrder> getSupportedOrders() {
      return EnumSet.allOf(PaginationOrder.class);
   }

   @Nonnull
   PaginationOrder getOrder();

   @Nonnull
   @CheckReturnValue
   M order(@Nonnull PaginationOrder var1);

   @Nonnull
   @CheckReturnValue
   default M reverse() {
      return (M)(this.getOrder() == PaginationAction.PaginationOrder.BACKWARD ? this.order(PaginationAction.PaginationOrder.FORWARD) : this.order(PaginationAction.PaginationOrder.BACKWARD));
   }

   int cacheSize();

   boolean isEmpty();

   @Nonnull
   @Unmodifiable List<T> getCached();

   @Nonnull
   T getLast();

   @Nonnull
   T getFirst();

   @Nonnull
   @CheckReturnValue
   M limit(int var1);

   @Nonnull
   @CheckReturnValue
   M cache(boolean var1);

   boolean isCacheEnabled();

   int getMaxLimit();

   int getMinLimit();

   int getLimit();

   @Nonnull
   @CheckReturnValue
   default CompletableFuture<List<T>> takeWhileAsync(@Nonnull Predicate<? super T> rule) {
      Checks.notNull(rule, "Rule");
      return this.takeUntilAsync(rule.negate());
   }

   @Nonnull
   @CheckReturnValue
   default CompletableFuture<List<T>> takeWhileAsync(int limit, @Nonnull Predicate<? super T> rule) {
      Checks.notNull(rule, "Rule");
      return this.takeUntilAsync(limit, rule.negate());
   }

   @Nonnull
   @CheckReturnValue
   default CompletableFuture<List<T>> takeUntilAsync(@Nonnull Predicate<? super T> rule) {
      return this.takeUntilAsync(0, rule);
   }

   @Nonnull
   @CheckReturnValue
   default CompletableFuture<List<T>> takeUntilAsync(int limit, @Nonnull Predicate<? super T> rule) {
      Checks.notNull(rule, "Rule");
      Checks.notNegative(limit, "Limit");
      List<T> result = new ArrayList();
      CompletableFuture<List<T>> future = new CompletableFuture();
      CompletableFuture<?> handle = this.forEachAsync((element) -> {
         if (rule.test(element)) {
            return false;
         } else {
            result.add(element);
            return limit == 0 || limit > result.size();
         }
      });
      handle.whenComplete((r, t) -> {
         if (t != null) {
            future.completeExceptionally(t);
         } else {
            future.complete(result);
         }

      });
      return future;
   }

   @Nonnull
   @CheckReturnValue
   CompletableFuture<List<T>> takeAsync(int var1);

   @Nonnull
   @CheckReturnValue
   CompletableFuture<List<T>> takeRemainingAsync(int var1);

   @Nonnull
   @CheckReturnValue
   default CompletableFuture<?> forEachAsync(@Nonnull Procedure<? super T> action) {
      return this.forEachAsync(action, RestActionImpl.getDefaultFailure());
   }

   @Nonnull
   @CheckReturnValue
   CompletableFuture<?> forEachAsync(@Nonnull Procedure<? super T> var1, @Nonnull Consumer<? super Throwable> var2);

   @Nonnull
   @CheckReturnValue
   default CompletableFuture<?> forEachRemainingAsync(@Nonnull Procedure<? super T> action) {
      return this.forEachRemainingAsync(action, RestActionImpl.getDefaultFailure());
   }

   @Nonnull
   @CheckReturnValue
   CompletableFuture<?> forEachRemainingAsync(@Nonnull Procedure<? super T> var1, @Nonnull Consumer<? super Throwable> var2);

   @Blocking
   void forEachRemaining(@Nonnull Procedure<? super T> var1);

   @Nonnull
   @Blocking
   default Spliterator<T> spliterator() {
      return Spliterators.spliteratorUnknownSize(this.iterator(), 1024);
   }

   @Nonnull
   @Blocking
   default Stream<T> stream() {
      return StreamSupport.stream(this.spliterator(), false);
   }

   @Nonnull
   @Blocking
   default Stream<T> parallelStream() {
      return StreamSupport.stream(this.spliterator(), true);
   }

   @Nonnull
   @Blocking
   PaginationIterator<T> iterator();

   public static enum PaginationOrder {
      BACKWARD("before"),
      FORWARD("after");

      private final String key;

      private PaginationOrder(String key) {
         this.key = key;
      }

      @Nonnull
      public String getKey() {
         return this.key;
      }

      // $FF: synthetic method
      private static PaginationOrder[] $values() {
         return new PaginationOrder[]{BACKWARD, FORWARD};
      }
   }

   public static class PaginationIterator<E> implements Iterator<E> {
      protected Queue<E> items;
      protected final Supplier<List<E>> supply;

      public PaginationIterator(Collection<E> queue, Supplier<List<E>> supply) {
         this.items = new ArrayDeque(queue);
         this.supply = supply;
      }

      public boolean hasNext() {
         if (this.items == null) {
            return false;
         } else if (!this.hitEnd()) {
            return true;
         } else if (this.items.addAll((Collection)this.supply.get())) {
            return true;
         } else {
            this.items = null;
            return false;
         }
      }

      @Nonnull
      public E next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException("Reached End of pagination task!");
         } else {
            return (E)this.items.poll();
         }
      }

      protected boolean hitEnd() {
         return this.items.isEmpty();
      }
   }
}
