package net.dv8tion.jda.internal.requests.restaction.pagination;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction;
import net.dv8tion.jda.api.utils.Procedure;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;

public abstract class PaginationActionImpl<T, M extends PaginationAction<T, M>> extends RestActionImpl<List<T>> implements PaginationAction<T, M> {
   protected final List<T> cached = new CopyOnWriteArrayList();
   protected final int maxLimit;
   protected final int minLimit;
   protected final AtomicInteger limit;
   protected PaginationAction.PaginationOrder order;
   protected volatile long iteratorIndex;
   protected volatile long lastKey;
   protected volatile T last;
   protected volatile boolean useCache;

   public PaginationActionImpl(JDA api, Route.CompiledRoute route, int minLimit, int maxLimit, int initialLimit) {
      super(api, route);
      this.order = PaginationAction.PaginationOrder.BACKWARD;
      this.iteratorIndex = 0L;
      this.lastKey = 0L;
      this.last = null;
      this.useCache = true;
      this.maxLimit = maxLimit;
      this.minLimit = minLimit;
      this.limit = new AtomicInteger(initialLimit);
   }

   public PaginationActionImpl(JDA api) {
      super(api, (Route.CompiledRoute)null);
      this.order = PaginationAction.PaginationOrder.BACKWARD;
      this.iteratorIndex = 0L;
      this.lastKey = 0L;
      this.last = null;
      this.useCache = true;
      this.maxLimit = 0;
      this.minLimit = 0;
      this.limit = new AtomicInteger(0);
   }

   @Nonnull
   public M skipTo(long id) {
      if (!this.cached.isEmpty()) {
         int cmp = Long.compareUnsigned(this.lastKey, id);
         if (cmp < 0) {
            throw new IllegalArgumentException("Cannot jump to that id, it is newer than the current oldest element.");
         }
      }

      if (this.lastKey != id) {
         this.last = null;
      }

      this.iteratorIndex = id;
      this.lastKey = id;
      return (M)this;
   }

   public long getLastKey() {
      return this.lastKey;
   }

   @Nonnull
   public PaginationAction.PaginationOrder getOrder() {
      return this.order;
   }

   @Nonnull
   public M order(@Nonnull PaginationAction.PaginationOrder order) {
      Checks.notNull(order, "PaginationOrder");
      if (order != this.order) {
         if (!this.isEmpty()) {
            throw new IllegalStateException("Cannot change pagination order after retrieving.");
         }

         if (!this.getSupportedOrders().contains(order)) {
            throw new IllegalArgumentException("Cannot use PaginationOrder." + order + " for this pagination endpoint.");
         }
      }

      this.order = order;
      return (M)this;
   }

   @Nonnull
   public M setCheck(BooleanSupplier checks) {
      return (M)(super.setCheck(checks));
   }

   @Nonnull
   public M timeout(long timeout, @Nonnull TimeUnit unit) {
      return (M)(super.timeout(timeout, unit));
   }

   @Nonnull
   public M deadline(long timestamp) {
      return (M)(super.deadline(timestamp));
   }

   public int cacheSize() {
      return this.cached.size();
   }

   public boolean isEmpty() {
      return this.cached.isEmpty();
   }

   @Nonnull
   public List<T> getCached() {
      return Collections.unmodifiableList(this.cached);
   }

   @Nonnull
   public T getLast() {
      T last = this.last;
      if (last == null) {
         throw new NoSuchElementException("No entities have been retrieved yet.");
      } else {
         return last;
      }
   }

   @Nonnull
   public T getFirst() {
      if (this.cached.isEmpty()) {
         throw new NoSuchElementException("No entities have been retrieved yet.");
      } else {
         return (T)this.cached.get(0);
      }
   }

   @Nonnull
   public M limit(int limit) {
      Checks.check(this.maxLimit == 0 || limit <= this.maxLimit, "Limit must not exceed %d!", (Object)this.maxLimit);
      Checks.check(this.minLimit == 0 || limit >= this.minLimit, "Limit must be greater or equal to %d", (Object)this.minLimit);
      this.limit.set(limit);
      return (M)this;
   }

   @Nonnull
   public M cache(boolean enableCache) {
      this.useCache = enableCache;
      return (M)this;
   }

   public boolean isCacheEnabled() {
      return this.useCache;
   }

   public final int getMaxLimit() {
      return this.maxLimit;
   }

   public final int getMinLimit() {
      return this.minLimit;
   }

   public final int getLimit() {
      return this.limit.get();
   }

   @Nonnull
   public CompletableFuture<List<T>> takeAsync(int amount) {
      return this.takeAsync0(amount, (task, list) -> {
         Procedure var10001 = (val) -> {
            list.add(val);
            return list.size() < amount;
         };
         Objects.requireNonNull(task);
         return this.forEachAsync(var10001, task::completeExceptionally);
      });
   }

   @Nonnull
   public CompletableFuture<List<T>> takeRemainingAsync(int amount) {
      return this.takeAsync0(amount, (task, list) -> {
         Procedure var10001 = (val) -> {
            list.add(val);
            return list.size() < amount;
         };
         Objects.requireNonNull(task);
         return this.forEachRemainingAsync(var10001, task::completeExceptionally);
      });
   }

   private CompletableFuture<List<T>> takeAsync0(int amount, BiFunction<CompletableFuture<?>, List<T>, CompletableFuture<?>> converter) {
      CompletableFuture<List<T>> task = new CompletableFuture();
      List<T> list = new ArrayList(amount);
      CompletableFuture<?> promise = (CompletableFuture)converter.apply(task, list);
      promise.thenRun(() -> task.complete(list));
      return task;
   }

   @Nonnull
   public PaginationAction.PaginationIterator<T> iterator() {
      return new PaginationAction.PaginationIterator<T>(this.cached, this::getNextChunk);
   }

   @Nonnull
   public CompletableFuture<?> forEachAsync(@Nonnull Procedure<? super T> action, @Nonnull Consumer<? super Throwable> failure) {
      Checks.notNull(action, "Procedure");
      Checks.notNull(failure, "Failure Consumer");
      CompletableFuture<?> task = new CompletableFuture();
      Consumer<List<T>> acceptor = new ChainedConsumer(task, action, (throwable) -> {
         task.completeExceptionally(throwable);
         failure.accept(throwable);
      });

      try {
         acceptor.accept(this.cached);
      } catch (Exception ex) {
         failure.accept(ex);
         task.completeExceptionally(ex);
      }

      return task;
   }

   @Nonnull
   public CompletableFuture<?> forEachRemainingAsync(@Nonnull Procedure<? super T> action, @Nonnull Consumer<? super Throwable> failure) {
      Checks.notNull(action, "Procedure");
      Checks.notNull(failure, "Failure Consumer");
      CompletableFuture<?> task = new CompletableFuture();
      Consumer<List<T>> acceptor = new ChainedConsumer(task, action, (throwable) -> {
         task.completeExceptionally(throwable);
         failure.accept(throwable);
      });

      try {
         acceptor.accept(this.getRemainingCache());
      } catch (Exception ex) {
         failure.accept(ex);
         task.completeExceptionally(ex);
      }

      return task;
   }

   public void forEachRemaining(@Nonnull Procedure<? super T> action) {
      Checks.notNull(action, "Procedure");
      Queue<T> queue = new ArrayDeque();

      while(queue.addAll(this.getNextChunk())) {
         while(!queue.isEmpty()) {
            T it = (T)queue.poll();
            if (!action.execute(it)) {
               this.updateIndex(it);
               return;
            }
         }
      }

   }

   @Nonnull
   protected String getPaginationLastEvaluatedKey(long lastId, T last) {
      return Long.toUnsignedString(lastId);
   }

   protected Route.CompiledRoute finalizeRoute() {
      Route.CompiledRoute route = super.finalizeRoute();
      String limit = String.valueOf(this.getLimit());
      long localLastKey = this.lastKey;
      route = route.withQueryParams("limit", limit);
      if (localLastKey != 0L) {
         route = route.withQueryParams(this.order.getKey(), this.getPaginationLastEvaluatedKey(localLastKey, this.last));
      } else if (this.order == PaginationAction.PaginationOrder.FORWARD) {
         route = route.withQueryParams("after", this.getPaginationLastEvaluatedKey(0L, this.last));
      }

      return route;
   }

   protected List<T> getRemainingCache() {
      int index = this.getIteratorIndex();
      return this.useCache && index > -1 && index < this.cached.size() ? this.cached.subList(index, this.cached.size()) : Collections.emptyList();
   }

   public List<T> getNextChunk() {
      List<T> list = this.getRemainingCache();
      if (!list.isEmpty()) {
         return list;
      } else {
         int current = this.limit.getAndSet(this.getMaxLimit());
         list = (List)this.complete();
         this.limit.set(current);
         return list;
      }
   }

   protected abstract long getKey(T var1);

   protected int getIteratorIndex() {
      for(int i = 0; i < this.cached.size(); ++i) {
         if (this.getKey(this.cached.get(i)) == this.iteratorIndex) {
            return i + 1;
         }
      }

      return -1;
   }

   protected void updateIndex(T it) {
      long key = this.getKey(it);
      this.iteratorIndex = key;
      if (!this.useCache) {
         this.lastKey = key;
         this.last = it;
      }

   }

   protected class ChainedConsumer implements Consumer<List<T>> {
      protected final CompletableFuture<?> task;
      protected final Procedure<? super T> action;
      protected final Consumer<Throwable> throwableConsumer;
      protected boolean initial = true;

      protected ChainedConsumer(CompletableFuture<?> task, Procedure<? super T> action, Consumer<Throwable> throwableConsumer) {
         this.task = task;
         this.action = action;
         this.throwableConsumer = throwableConsumer;
      }

      public void accept(List<T> list) {
         if (list.isEmpty() && !this.initial) {
            this.task.complete((Object)null);
         } else {
            this.initial = false;
            T previous = (T)null;

            for(T it : list) {
               if (this.task.isCancelled()) {
                  if (previous != null) {
                     PaginationActionImpl.this.updateIndex(previous);
                  }

                  return;
               }

               if (!this.action.execute(it)) {
                  PaginationActionImpl.this.updateIndex(it);
                  this.task.complete((Object)null);
                  return;
               }

               previous = it;
            }

            int currentLimit = PaginationActionImpl.this.limit.getAndSet(PaginationActionImpl.this.maxLimit);
            PaginationActionImpl.this.queue(this, this.throwableConsumer);
            PaginationActionImpl.this.limit.set(currentLimit);
         }
      }
   }
}
