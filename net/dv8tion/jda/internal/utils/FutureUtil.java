package net.dv8tion.jda.internal.utils;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FutureUtil {
   @Nonnull
   public static <T, U> CompletableFuture<U> thenApplyCancellable(@Nonnull CompletableFuture<T> future, @Nonnull Function<T, U> applyFunction, @Nullable Runnable onCancel) {
      CompletableFuture<U> cf = new CompletableFuture();
      future.thenAccept((t) -> cf.complete(applyFunction.apply(t))).exceptionally((throwable) -> {
         cf.completeExceptionally(throwable);
         return null;
      });
      cf.whenComplete((u, throwable) -> {
         if (cf.isCancelled()) {
            future.cancel(true);
            if (onCancel != null) {
               onCancel.run();
            }
         }

      });
      return cf;
   }

   @Nonnull
   public static <T, U> CompletableFuture<U> thenApplyCancellable(@Nonnull CompletableFuture<T> future, @Nonnull Function<T, U> applyFunction) {
      return thenApplyCancellable(future, applyFunction, (Runnable)null);
   }
}
