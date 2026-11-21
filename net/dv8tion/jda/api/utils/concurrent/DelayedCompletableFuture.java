package net.dv8tion.jda.api.utils.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Delayed;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public class DelayedCompletableFuture<T> extends CompletableFuture<T> implements ScheduledFuture<T> {
   private ScheduledFuture<?> future;

   private DelayedCompletableFuture() {
   }

   @Nonnull
   @CheckReturnValue
   public static <E> DelayedCompletableFuture<E> make(@Nonnull ScheduledExecutorService executor, long delay, @Nonnull TimeUnit unit, @Nonnull Function<? super DelayedCompletableFuture<E>, ? extends Runnable> mapping) {
      DelayedCompletableFuture<E> handle = new DelayedCompletableFuture<E>();
      ScheduledFuture<?> future = executor.schedule((Runnable)mapping.apply(handle), delay, unit);
      handle.initProxy(future);
      return handle;
   }

   private void initProxy(ScheduledFuture<?> future) {
      if (this.future == null) {
         this.future = future;
      } else {
         throw new IllegalStateException("Cannot initialize twice");
      }
   }

   public boolean cancel(boolean mayInterruptIfRunning) {
      if (this.future != null && !this.future.isDone()) {
         this.future.cancel(mayInterruptIfRunning);
      }

      return super.cancel(mayInterruptIfRunning);
   }

   public long getDelay(@Nonnull TimeUnit unit) {
      return this.future.getDelay(unit);
   }

   public int compareTo(@Nonnull Delayed o) {
      return this.future.compareTo(o);
   }
}
