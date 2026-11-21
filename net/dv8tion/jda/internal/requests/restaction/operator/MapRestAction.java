package net.dv8tion.jda.internal.requests.restaction.operator;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.RestAction;

public class MapRestAction<I, O> extends RestActionOperator<I, O> {
   private final Function<? super I, ? extends O> function;

   public MapRestAction(RestAction<I> action, Function<? super I, ? extends O> function) {
      super(action);
      this.function = function;
   }

   public void queue(@Nullable Consumer<? super O> success, @Nullable Consumer<? super Throwable> failure) {
      this.handle(this.action, failure, (result) -> doSuccess(success, this.function.apply(result)));
   }

   public O complete(boolean shouldQueue) throws RateLimitedException {
      return (O)this.function.apply(this.action.complete(shouldQueue));
   }

   @Nonnull
   public CompletableFuture<O> submit(boolean shouldQueue) {
      return this.action.submit(shouldQueue).thenApply(this.function);
   }
}
