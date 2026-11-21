package net.dv8tion.jda.internal.requests.restaction.operator;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jetbrains.annotations.Contract;

public class FlatMapErrorRestAction<T> extends RestActionOperator<T, T> {
   private final Predicate<? super Throwable> filter;
   private final Function<? super Throwable, ? extends RestAction<? extends T>> map;

   public FlatMapErrorRestAction(RestAction<T> action, Predicate<? super Throwable> filter, Function<? super Throwable, ? extends RestAction<? extends T>> map) {
      super(action);
      this.filter = filter;
      this.map = map;
   }

   public void queue(@Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure) {
      Consumer<? super Throwable> contextFailure = this.contextWrap(failure);
      this.action.queue(success, this.contextWrap((error) -> {
         try {
            if (this.filter.test(error)) {
               RestAction<? extends T> then = (RestAction)this.map.apply(error);
               if (then == null) {
                  doFailure(failure, new IllegalStateException("FlatMapError operand is null", error));
               } else {
                  then.queue(success, contextFailure);
               }
            } else {
               doFailure(failure, error);
            }
         } catch (Throwable e) {
            doFailure(failure, Helpers.appendCause(e, error));
         }

      }));
   }

   public T complete(boolean shouldQueue) throws RateLimitedException {
      try {
         return this.action.complete(shouldQueue);
      } catch (Throwable var5) {
         Throwable error = var5;

         try {
            if (this.filter.test(error)) {
               RestAction<? extends T> then = (RestAction)this.map.apply(error);
               if (then == null) {
                  throw new IllegalStateException("FlatMapError operand is null", error);
               }

               return then.complete(shouldQueue);
            }
         } catch (Throwable var4) {
            if (var4 instanceof IllegalStateException && var4.getCause() == var5) {
               throw (IllegalStateException)var4;
            }

            if (var4 instanceof RateLimitedException) {
               throw (RateLimitedException)Helpers.appendCause(var4, var5);
            }

            this.fail(Helpers.appendCause(var4, var5));
         }

         this.fail(var5);
         throw new AssertionError("Unreachable");
      }
   }

   @Nonnull
   public CompletableFuture<T> submit(boolean shouldQueue) {
      return this.action.submit(shouldQueue).handle((result, error) -> this.filter.test(error) ? ((RestAction)this.map.apply(error)).submit(shouldQueue).thenApply((x) -> x) : CompletableFuture.completedFuture(result)).thenCompose(Function.identity());
   }

   @Contract("_ -> fail")
   private void fail(Throwable error) {
      if (error instanceof RuntimeException) {
         throw (RuntimeException)error;
      } else if (error instanceof Error) {
         throw (Error)error;
      } else {
         throw new RuntimeException(error);
      }
   }
}
