package net.dv8tion.jda.internal.requests.restaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import okhttp3.RequestBody;

public class TriggerRestAction<T> extends RestActionImpl<T> {
   private final ReentrantLock mutex = new ReentrantLock();
   private final List<Runnable> callbacks = new ArrayList();
   private volatile boolean isReady;
   private volatile Throwable exception;

   public TriggerRestAction(JDA api, Route.CompiledRoute route) {
      super(api, route);
   }

   public TriggerRestAction(JDA api, Route.CompiledRoute route, DataObject data) {
      super(api, route, data);
   }

   public TriggerRestAction(JDA api, Route.CompiledRoute route, RequestBody data) {
      super(api, route, data);
   }

   public TriggerRestAction(JDA api, Route.CompiledRoute route, BiFunction<Response, Request<T>, T> handler) {
      super(api, route, handler);
   }

   public TriggerRestAction(JDA api, Route.CompiledRoute route, DataObject data, BiFunction<Response, Request<T>, T> handler) {
      super(api, route, data, handler);
   }

   public TriggerRestAction(JDA api, Route.CompiledRoute route, RequestBody data, BiFunction<Response, Request<T>, T> handler) {
      super(api, route, data, handler);
   }

   public void run() {
      MiscUtil.locked(this.mutex, (Runnable)(() -> {
         this.isReady = true;
         this.callbacks.forEach(Runnable::run);
      }));
   }

   public void fail(Throwable throwable) {
      MiscUtil.locked(this.mutex, (Runnable)(() -> {
         this.exception = throwable;
         this.callbacks.forEach(Runnable::run);
      }));
   }

   public void onReady(Runnable callback) {
      MiscUtil.locked(this.mutex, (Runnable)(() -> {
         if (!this.isReady && this.exception == null) {
            this.callbacks.add(callback);
         } else {
            callback.run();
         }

      }));
   }

   public void queue(Consumer<? super T> success, Consumer<? super Throwable> failure) {
      if (this.isReady) {
         super.queue(success, failure);
      } else {
         Consumer<? super Throwable> onFailure = this.wrapContext(failure);
         this.onReady(() -> {
            if (this.exception != null) {
               onFailure.accept(this.exception);
            } else {
               super.queue(success, onFailure);
            }

         });
      }
   }

   @Nonnull
   public CompletableFuture<T> submit(boolean shouldQueue) {
      if (this.isReady) {
         return super.submit(shouldQueue);
      } else {
         CompletableFuture<T> future = new CompletableFuture();
         Objects.requireNonNull(future);
         Consumer<? super Throwable> onFailure = this.wrapContext(future::completeExceptionally);
         this.onReady(() -> {
            if (this.exception != null) {
               onFailure.accept(this.exception);
            } else {
               CompletableFuture<T> handle = super.submit(shouldQueue);
               handle.whenComplete((success, error) -> {
                  if (error != null) {
                     onFailure.accept(error);
                  } else {
                     future.complete(success);
                  }

               });
               future.whenComplete((r, e) -> {
                  if (future.isCancelled()) {
                     handle.cancel(false);
                  }

               });
            }
         });
         return future;
      }
   }

   private Consumer<? super Throwable> wrapContext(Consumer<? super Throwable> failure) {
      failure = failure == null ? getDefaultFailure() : failure;
      return isPassContext() && !(failure instanceof ContextException.ContextConsumer) ? ContextException.here(failure) : failure;
   }
}
