package net.dv8tion.jda.internal.requests.restaction.operator;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.Checks;

public class CombineRestAction<I1, I2, O> implements RestAction<O> {
   private final RestAction<I1> action1;
   private final RestAction<I2> action2;
   private final BiFunction<? super I1, ? super I2, ? extends O> accumulator;
   private volatile boolean failed = false;

   public CombineRestAction(RestAction<I1> action1, RestAction<I2> action2, BiFunction<? super I1, ? super I2, ? extends O> accumulator) {
      Checks.check(action1 != action2, "Cannot combine a RestAction with itself!");
      this.action1 = action1;
      this.action2 = action2;
      this.accumulator = accumulator;
      BooleanSupplier checks = () -> !this.failed;
      action1.addCheck(checks);
      action2.addCheck(checks);
   }

   @Nonnull
   public JDA getJDA() {
      return this.action1.getJDA();
   }

   @Nonnull
   public RestAction<O> setCheck(@Nullable BooleanSupplier checks) {
      BooleanSupplier check = () -> !this.failed && (checks == null || checks.getAsBoolean());
      this.action1.setCheck(check);
      this.action2.setCheck(check);
      return this;
   }

   @Nonnull
   public RestAction<O> addCheck(@Nonnull BooleanSupplier checks) {
      this.action1.addCheck(checks);
      this.action2.addCheck(checks);
      return this;
   }

   @Nullable
   public BooleanSupplier getCheck() {
      BooleanSupplier check1 = this.action1.getCheck();
      BooleanSupplier check2 = this.action2.getCheck();
      return () -> (check1 == null || check1.getAsBoolean()) && (check2 == null || check2.getAsBoolean()) && !this.failed;
   }

   @Nonnull
   public RestAction<O> deadline(long timestamp) {
      this.action1.deadline(timestamp);
      this.action2.deadline(timestamp);
      return this;
   }

   public void queue(@Nullable Consumer<? super O> success, @Nullable Consumer<? super Throwable> failure) {
      AtomicInteger count = new AtomicInteger(0);
      AtomicReference<I1> result1 = new AtomicReference();
      AtomicReference<I2> result2 = new AtomicReference();
      Consumer<Throwable> failureCallback = (e) -> {
         if (!this.failed) {
            this.failed = true;
            RestActionOperator.doFailure(failure, e);
         }
      };
      this.action1.queue((s) -> {
         try {
            result1.set(s);
            if (count.incrementAndGet() == 2) {
               RestActionOperator.doSuccess(success, this.accumulator.apply(result1.get(), result2.get()));
            }
         } catch (Exception e) {
            failureCallback.accept(e);
         }

      }, failureCallback);
      this.action2.queue((s) -> {
         try {
            result2.set(s);
            if (count.incrementAndGet() == 2) {
               RestActionOperator.doSuccess(success, this.accumulator.apply(result1.get(), result2.get()));
            }
         } catch (Exception e) {
            failureCallback.accept(e);
         }

      }, failureCallback);
   }

   public O complete(boolean shouldQueue) throws RateLimitedException {
      if (!shouldQueue) {
         return (O)this.accumulator.apply(this.action1.complete(false), this.action2.complete(false));
      } else {
         try {
            return (O)this.submit(true).join();
         } catch (CompletionException e) {
            if (e.getCause() instanceof RuntimeException) {
               throw (RuntimeException)e.getCause();
            } else if (e.getCause() instanceof RateLimitedException) {
               throw (RateLimitedException)e.getCause();
            } else {
               throw e;
            }
         }
      }
   }

   @Nonnull
   public CompletableFuture<O> submit(boolean shouldQueue) {
      return this.action1.submit(shouldQueue).thenCombine(this.action2.submit(shouldQueue), this.accumulator);
   }
}
