package net.dv8tion.jda.api.utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.utils.concurrent.Task;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.JDALogger;
import net.dv8tion.jda.internal.utils.concurrent.task.GatewayTask;
import org.slf4j.Logger;

public class Once<E extends GenericEvent> implements EventListener {
   private static final Logger LOG = JDALogger.getLog(Once.class);
   private final JDA jda;
   private final Class<E> eventType;
   private final List<Predicate<? super E>> filters;
   private final CompletableFuture<E> future;
   private final GatewayTask<E> task;
   private final ScheduledFuture<?> timeoutFuture;
   private final Runnable timeoutCallback;

   protected Once(JDA jda, Class<E> eventType, List<Predicate<? super E>> filters, Runnable timeoutCallback, Duration timeout, ScheduledExecutorService timeoutPool) {
      this.jda = jda;
      this.eventType = eventType;
      this.filters = new ArrayList(filters);
      this.timeoutCallback = timeoutCallback;
      this.future = new CompletableFuture();
      this.task = this.createTask();
      this.timeoutFuture = this.scheduleTimeout(timeout, timeoutPool);
   }

   @Nonnull
   private GatewayTask<E> createTask() {
      GatewayTask<E> task = new GatewayTask<E>(this.future, () -> {
         this.jda.removeEventListener(this);
         this.future.completeExceptionally(new CancellationException());
         if (this.timeoutFuture != null) {
            this.timeoutFuture.cancel(false);
         }

      });
      task.onSetTimeout((e) -> {
         throw new UnsupportedOperationException("You must set the timeout on Once.Builder#timeout");
      });
      return task;
   }

   @Nullable
   private ScheduledFuture<?> scheduleTimeout(@Nullable Duration timeout, @Nullable ScheduledExecutorService timeoutPool) {
      if (timeout == null) {
         return null;
      } else {
         if (timeoutPool == null) {
            timeoutPool = this.jda.getGatewayPool();
         }

         return timeoutPool.schedule(() -> {
            this.jda.removeEventListener(this);
            if (this.future.completeExceptionally(new TimeoutException())) {
               if (this.timeoutCallback != null) {
                  try {
                     this.timeoutCallback.run();
                  } catch (Throwable e) {
                     LOG.error("An error occurred while running the timeout callback", e);
                     if (e instanceof Error) {
                        throw (Error)e;
                     }
                  }
               }

            }
         }, timeout.toMillis(), TimeUnit.MILLISECONDS);
      }
   }

   @SubscribeEvent
   public void onEvent(@Nonnull GenericEvent event) {
      if (this.eventType.isInstance(event)) {
         E casted = (E)(this.eventType.cast(event));

         try {
            if (this.filters.stream().allMatch((p) -> p.test(casted))) {
               if (this.timeoutFuture != null) {
                  this.timeoutFuture.cancel(false);
               }

               event.getJDA().removeEventListener(this);
               this.future.complete(casted);
            }
         } catch (Throwable e) {
            if (this.future.completeExceptionally(e)) {
               event.getJDA().removeEventListener(this);
            }

            if (e instanceof Error) {
               throw (Error)e;
            }
         }

      }
   }

   public static class Builder<E extends GenericEvent> {
      private final JDA jda;
      private final Class<E> eventType;
      private final List<Predicate<? super E>> filters = new ArrayList();
      private ScheduledExecutorService timeoutPool;
      private Duration timeout;
      private Runnable timeoutCallback;

      public Builder(@Nonnull JDA jda, @Nonnull Class<E> eventType) {
         Checks.notNull(jda, "JDA");
         Checks.notNull(eventType, "Event type");
         this.jda = jda;
         this.eventType = eventType;
      }

      @Nonnull
      public Builder<E> filter(@Nonnull Predicate<? super E> filter) {
         Checks.notNull(filter, "Filter");
         this.filters.add(filter);
         return this;
      }

      @Nonnull
      public Builder<E> timeout(@Nonnull Duration timeout) {
         return this.timeout(timeout, (Runnable)null);
      }

      @Nonnull
      public Builder<E> timeout(@Nonnull Duration timeout, @Nullable Runnable timeoutCallback) {
         Checks.notNull(timeout, "Timeout");
         this.timeout = timeout;
         this.timeoutCallback = timeoutCallback;
         return this;
      }

      @Nonnull
      public Builder<E> setTimeoutPool(@Nonnull ScheduledExecutorService timeoutPool) {
         Checks.notNull(timeoutPool, "Timeout pool");
         this.timeoutPool = timeoutPool;
         return this;
      }

      @Nonnull
      public Task<E> subscribe(@Nonnull Consumer<E> callback) {
         Once<E> once = new Once<E>(this.jda, this.eventType, this.filters, this.timeoutCallback, this.timeout, this.timeoutPool);
         this.jda.addEventListener(once);
         return once.task.onSuccess(callback);
      }
   }
}
