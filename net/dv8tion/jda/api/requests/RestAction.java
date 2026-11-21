package net.dv8tion.jda.api.requests;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.annotations.UnknownNullability;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.utils.Result;
import net.dv8tion.jda.api.utils.concurrent.DelayedCompletableFuture;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.restaction.operator.CombineRestAction;
import net.dv8tion.jda.internal.requests.restaction.operator.DelayRestAction;
import net.dv8tion.jda.internal.requests.restaction.operator.FlatMapErrorRestAction;
import net.dv8tion.jda.internal.requests.restaction.operator.FlatMapRestAction;
import net.dv8tion.jda.internal.requests.restaction.operator.MapErrorRestAction;
import net.dv8tion.jda.internal.requests.restaction.operator.MapRestAction;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.ContextRunnable;
import org.jetbrains.annotations.Blocking;

public interface RestAction<T> {
   static void setPassContext(boolean enable) {
      RestActionImpl.setPassContext(enable);
   }

   static boolean isPassContext() {
      return RestActionImpl.isPassContext();
   }

   static void setDefaultFailure(@Nullable Consumer<? super Throwable> callback) {
      RestActionImpl.setDefaultFailure(callback);
   }

   static void setDefaultSuccess(@Nullable Consumer<Object> callback) {
      RestActionImpl.setDefaultSuccess(callback);
   }

   static void setDefaultTimeout(long timeout, @Nonnull TimeUnit unit) {
      RestActionImpl.setDefaultTimeout(timeout, unit);
   }

   static long getDefaultTimeout() {
      return RestActionImpl.getDefaultTimeout();
   }

   @Nonnull
   static Consumer<? super Throwable> getDefaultFailure() {
      return RestActionImpl.getDefaultFailure();
   }

   @Nonnull
   static Consumer<Object> getDefaultSuccess() {
      return RestActionImpl.getDefaultSuccess();
   }

   @Nonnull
   @SafeVarargs
   @CheckReturnValue
   static <E> RestAction<List<E>> allOf(@Nonnull RestAction<? extends E> first, @Nonnull RestAction<? extends E>... others) {
      Checks.notNull(first, "RestAction");
      Checks.noneNull(others, "RestAction");
      List<RestAction<? extends E>> list = new ArrayList(others.length + 1);
      list.add(first);
      Collections.addAll(list, others);
      return allOf(list);
   }

   @Nonnull
   @CheckReturnValue
   static <E> RestAction<List<E>> allOf(@Nonnull Collection<? extends RestAction<? extends E>> actions) {
      return accumulate(actions, Collectors.toList());
   }

   @Nonnull
   @CheckReturnValue
   static <E, A, O> RestAction<O> accumulate(@Nonnull Collection<? extends RestAction<? extends E>> actions, @Nonnull Collector<? super E, A, ? extends O> collector) {
      Checks.noneNull(actions, "RestAction");
      Checks.notEmpty(actions, "RestActions");
      Checks.notNull(collector, "Collector");
      Supplier<A> accumulator = collector.supplier();
      BiConsumer<A, ? super E> add = collector.accumulator();
      Function<A, ? extends O> output = collector.finisher();
      actions = new LinkedHashSet(actions);
      Iterator<? extends RestAction<? extends E>> iterator = actions.iterator();

      RestAction<A> result;
      RestAction<? extends E> next;
      for(result = ((RestAction)iterator.next()).<A>map((it) -> {
         A list = (A)accumulator.get();
         add.accept(list, it);
         return list;
      }); iterator.hasNext(); result = result.and(next, (list, b) -> {
         add.accept(list, b);
         return list;
      })) {
         next = (RestAction)iterator.next();
      }

      return result.<O>map(output);
   }

   @Nonnull
   JDA getJDA();

   @Nonnull
   @CheckReturnValue
   RestAction<T> setCheck(@Nullable BooleanSupplier var1);

   @Nullable
   default BooleanSupplier getCheck() {
      return null;
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<T> addCheck(@Nonnull BooleanSupplier checks) {
      Checks.notNull(checks, "Checks");
      BooleanSupplier check = this.getCheck();
      return this.setCheck(() -> (check == null || check.getAsBoolean()) && checks.getAsBoolean());
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<T> timeout(long timeout, @Nonnull TimeUnit unit) {
      Checks.notNull(unit, "TimeUnit");
      return this.deadline(timeout <= 0L ? 0L : System.currentTimeMillis() + unit.toMillis(timeout));
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<T> deadline(long timestamp) {
      throw new UnsupportedOperationException();
   }

   default void queue() {
      this.queue((Consumer)null);
   }

   default void queue(@Nullable Consumer<? super T> success) {
      this.queue(success, (Consumer)null);
   }

   void queue(@Nullable Consumer<? super T> var1, @Nullable Consumer<? super Throwable> var2);

   @UnknownNullability
   @Blocking
   default T complete() {
      try {
         return (T)this.complete(true);
      } catch (RateLimitedException e) {
         throw new AssertionError(e);
      }
   }

   @UnknownNullability
   @Blocking
   T complete(boolean var1) throws RateLimitedException;

   @Nonnull
   @CheckReturnValue
   default CompletableFuture<T> submit() {
      return this.submit(true);
   }

   @Nonnull
   @CheckReturnValue
   CompletableFuture<T> submit(boolean var1);

   @Nonnull
   @CheckReturnValue
   default RestAction<Result<T>> mapToResult() {
      return this.<Result<T>>map(Result::success).onErrorMap(Result::failure);
   }

   @Nonnull
   @CheckReturnValue
   default <O> RestAction<O> map(@Nonnull Function<? super T, ? extends O> map) {
      Checks.notNull(map, "Function");
      return new MapRestAction(this, map);
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<T> onSuccess(@Nonnull Consumer<? super T> consumer) {
      Checks.notNull(consumer, "Consumer");
      return this.<T>map((result) -> {
         consumer.accept(result);
         return result;
      });
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<T> onErrorMap(@Nonnull Function<? super Throwable, ? extends T> map) {
      return this.onErrorMap((Predicate)null, map);
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<T> onErrorMap(@Nullable Predicate<? super Throwable> condition, @Nonnull Function<? super Throwable, ? extends T> map) {
      Checks.notNull(map, "Function");
      return new MapErrorRestAction<T>(this, condition == null ? (x) -> true : condition, map);
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<T> onErrorFlatMap(@Nonnull Function<? super Throwable, ? extends RestAction<? extends T>> map) {
      return this.onErrorFlatMap((Predicate)null, map);
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<T> onErrorFlatMap(@Nullable Predicate<? super Throwable> condition, @Nonnull Function<? super Throwable, ? extends RestAction<? extends T>> map) {
      Checks.notNull(map, "Function");
      return new FlatMapErrorRestAction<T>(this, condition == null ? (x) -> true : condition, map);
   }

   @Nonnull
   @CheckReturnValue
   default <O> RestAction<O> flatMap(@Nonnull Function<? super T, ? extends RestAction<O>> flatMap) {
      return this.<O>flatMap((Predicate)null, flatMap);
   }

   @Nonnull
   @CheckReturnValue
   default <O> RestAction<O> flatMap(@Nullable Predicate<? super T> condition, @Nonnull Function<? super T, ? extends RestAction<O>> flatMap) {
      Checks.notNull(flatMap, "Function");
      return new FlatMapRestAction(this, condition, flatMap);
   }

   @Nonnull
   @CheckReturnValue
   default <U, O> RestAction<O> and(@Nonnull RestAction<U> other, @Nonnull BiFunction<? super T, ? super U, ? extends O> accumulator) {
      Checks.notNull(other, "RestAction");
      Checks.notNull(accumulator, "Accumulator");
      return new CombineRestAction(this, other, accumulator);
   }

   @Nonnull
   @CheckReturnValue
   default <U> RestAction<Void> and(@Nonnull RestAction<U> other) {
      return this.and(other, (a, b) -> null);
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<List<T>> zip(@Nonnull RestAction<? extends T> first, @Nonnull RestAction<? extends T>... other) {
      Checks.notNull(first, "RestAction");
      Checks.noneNull(other, "RestAction");
      List<RestAction<? extends T>> list = new ArrayList();
      list.add(this);
      list.add(first);
      Collections.addAll(list, other);
      return allOf(list);
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<T> delay(@Nonnull Duration duration) {
      return this.delay(duration, (ScheduledExecutorService)null);
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<T> delay(@Nonnull Duration duration, @Nullable ScheduledExecutorService scheduler) {
      Checks.notNull(duration, "Duration");
      return new DelayRestAction<T>(this, TimeUnit.MILLISECONDS, duration.toMillis(), scheduler);
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<T> delay(long delay, @Nonnull TimeUnit unit) {
      return this.delay(delay, unit, (ScheduledExecutorService)null);
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<T> delay(long delay, @Nonnull TimeUnit unit, @Nullable ScheduledExecutorService scheduler) {
      Checks.notNull(unit, "TimeUnit");
      return new DelayRestAction<T>(this, unit, delay, scheduler);
   }

   @Nonnull
   @CheckReturnValue
   default DelayedCompletableFuture<T> submitAfter(long delay, @Nonnull TimeUnit unit) {
      return this.submitAfter(delay, unit, (ScheduledExecutorService)null);
   }

   @Nonnull
   @CheckReturnValue
   default DelayedCompletableFuture<T> submitAfter(long delay, @Nonnull TimeUnit unit, @Nullable ScheduledExecutorService executor) {
      Checks.notNull(unit, "TimeUnit");
      if (executor == null) {
         executor = this.getJDA().getRateLimitPool();
      }

      return DelayedCompletableFuture.<T>make(executor, delay, unit, (task) -> {
         Consumer<? super Throwable> onFailure;
         if (isPassContext()) {
            Objects.requireNonNull(task);
            onFailure = ContextException.here(task::completeExceptionally);
         } else {
            Objects.requireNonNull(task);
            onFailure = task::completeExceptionally;
         }

         return new ContextRunnable(() -> {
            Objects.requireNonNull(task);
            this.queue(task::complete, onFailure);
         });
      });
   }

   @UnknownNullability
   @Blocking
   default T completeAfter(long delay, @Nonnull TimeUnit unit) {
      Checks.notNull(unit, "TimeUnit");

      try {
         unit.sleep(delay);
         return (T)this.complete();
      } catch (InterruptedException e) {
         throw new RuntimeException(e);
      }
   }

   @Nonnull
   default ScheduledFuture<?> queueAfter(long delay, @Nonnull TimeUnit unit) {
      return this.queueAfter(delay, unit, (Consumer)null, (Consumer)null, (ScheduledExecutorService)null);
   }

   @Nonnull
   default ScheduledFuture<?> queueAfter(long delay, @Nonnull TimeUnit unit, @Nullable Consumer<? super T> success) {
      return this.queueAfter(delay, unit, success, (Consumer)null, (ScheduledExecutorService)null);
   }

   @Nonnull
   default ScheduledFuture<?> queueAfter(long delay, @Nonnull TimeUnit unit, @Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure) {
      return this.queueAfter(delay, unit, success, failure, (ScheduledExecutorService)null);
   }

   @Nonnull
   default ScheduledFuture<?> queueAfter(long delay, @Nonnull TimeUnit unit, @Nullable ScheduledExecutorService executor) {
      return this.queueAfter(delay, unit, (Consumer)null, (Consumer)null, executor);
   }

   @Nonnull
   default ScheduledFuture<?> queueAfter(long delay, @Nonnull TimeUnit unit, @Nullable Consumer<? super T> success, @Nullable ScheduledExecutorService executor) {
      return this.queueAfter(delay, unit, success, (Consumer)null, executor);
   }

   @Nonnull
   default ScheduledFuture<?> queueAfter(long delay, @Nonnull TimeUnit unit, @Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure, @Nullable ScheduledExecutorService executor) {
      Checks.notNull(unit, "TimeUnit");
      if (executor == null) {
         executor = this.getJDA().getRateLimitPool();
      }

      Consumer<? super Throwable> onFailure;
      if (isPassContext()) {
         onFailure = ContextException.here(failure == null ? getDefaultFailure() : failure);
      } else {
         onFailure = failure;
      }

      Runnable task = new ContextRunnable(() -> this.queue(success, onFailure));
      return executor.schedule(task, delay, unit);
   }
}
