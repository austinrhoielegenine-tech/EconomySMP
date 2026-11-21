package net.dv8tion.jda.internal.requests.restaction.operator;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jetbrains.annotations.Contract;

public class MapErrorRestAction<T> extends RestActionOperator<T, T> {
   private final Predicate<? super Throwable> filter;
   private final Function<? super Throwable, ? extends T> map;

   public MapErrorRestAction(RestAction<T> action, Predicate<? super Throwable> filter, Function<? super Throwable, ? extends T> map) {
      super(action);
      this.filter = filter;
      this.map = map;
   }

   public void queue(@Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure) {
      this.action.queue(success, this.contextWrap((error) -> {
         try {
            if (this.filter.test(error)) {
               doSuccess(success, this.map.apply(error));
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
               return (T)this.map.apply(error);
            }
         } catch (Throwable e) {
            this.fail(Helpers.appendCause(e, var5));
         }

         if (var5 instanceof RateLimitedException) {
            throw (RateLimitedException)var5;
         } else {
            this.fail(var5);
            throw new AssertionError("Unreachable");
         }
      }
   }

   @Nonnull
   public CompletableFuture<T> submit(boolean shouldQueue) {
      return this.action.submit(shouldQueue).handle((value, error) -> {
         T result = (T)value;
         if (error != null) {
            error = error instanceof CompletionException && error.getCause() != null ? error.getCause() : error;
            if (this.filter.test(error)) {
               result = (T)this.map.apply(error);
            } else {
               this.fail(error);
            }
         }

         return result;
      });
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
