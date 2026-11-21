package net.dv8tion.jda.internal.requests.restaction.operator;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.RestAction;

public class DelayRestAction<T> extends RestActionOperator<T, T> {
   private final TimeUnit unit;
   private final long delay;
   private final ScheduledExecutorService scheduler;

   public DelayRestAction(RestAction<T> action, TimeUnit unit, long delay, ScheduledExecutorService scheduler) {
      super(action);
      this.unit = unit;
      this.delay = delay;
      this.scheduler = scheduler == null ? action.getJDA().getRateLimitPool() : scheduler;
   }

   public void queue(@Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure) {
      this.handle(this.action, failure, (result) -> this.scheduler.schedule(() -> doSuccess(success, result), this.delay, this.unit));
   }

   public T complete(boolean shouldQueue) throws RateLimitedException {
      T result = this.action.complete(shouldQueue);

      try {
         this.unit.sleep(this.delay);
         return result;
      } catch (InterruptedException e) {
         throw new RuntimeException(e);
      }
   }

   @Nonnull
   public CompletableFuture<T> submit(boolean shouldQueue) {
      CompletableFuture<T> future = new CompletableFuture();
      Objects.requireNonNull(future);
      Consumer var10001 = future::complete;
      Objects.requireNonNull(future);
      this.queue(var10001, future::completeExceptionally);
      return future;
   }
}
