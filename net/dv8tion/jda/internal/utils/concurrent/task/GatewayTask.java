package net.dv8tion.jda.internal.utils.concurrent.task;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.utils.concurrent.Task;
import net.dv8tion.jda.internal.requests.WebSocketClient;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

public class GatewayTask<T> implements Task<T> {
   private static final Logger log = JDALogger.getLog(Task.class);
   private final Runnable onCancel;
   private final CompletableFuture<T> future;
   private LongConsumer setTimeout;

   public GatewayTask(CompletableFuture<T> future, Runnable onCancel) {
      this.future = future;
      this.onCancel = onCancel;
   }

   public GatewayTask<T> onSetTimeout(LongConsumer setTimeout) {
      this.setTimeout = setTimeout;
      return this;
   }

   public boolean isStarted() {
      return true;
   }

   @Nonnull
   public Task<T> onError(@Nonnull Consumer<? super Throwable> callback) {
      Checks.notNull(callback, "Callback");
      Consumer<Throwable> failureHandler = ContextException.here((error) -> log.error("Task Failure callback threw error", error));
      this.future.exceptionally((error) -> {
         try {
            callback.accept(error);
         } catch (Throwable e) {
            failureHandler.accept(e);
            if (e instanceof Error) {
               throw e;
            }
         }

         return null;
      });
      return this;
   }

   @Nonnull
   public Task<T> onSuccess(@Nonnull Consumer<? super T> callback) {
      Checks.notNull(callback, "Callback");
      Consumer<Throwable> failureHandler = ContextException.here((error) -> log.error("Task Success callback threw error", error));
      this.future.thenAccept((result) -> {
         try {
            callback.accept(result);
         } catch (Throwable error) {
            failureHandler.accept(error);
            if (error instanceof Error) {
               throw error;
            }
         }

      });
      return this;
   }

   @Nonnull
   public Task<T> setTimeout(@Nonnull Duration timeout) {
      Checks.notNull(timeout, "Timeout");
      long millis = timeout.toMillis();
      Checks.positive(millis, "Timeout");
      if (this.setTimeout != null) {
         this.setTimeout.accept(millis);
      }

      return this;
   }

   @Nonnull
   public T get() {
      if ((Boolean)WebSocketClient.WS_THREAD.get()) {
         throw new UnsupportedOperationException("Blocking operations are not permitted on the gateway thread");
      } else {
         return (T)this.future.join();
      }
   }

   public void cancel() {
      this.onCancel.run();
   }
}
