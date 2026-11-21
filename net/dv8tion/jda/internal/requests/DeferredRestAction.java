package net.dv8tion.jda.internal.requests;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.internal.utils.Checks;

public class DeferredRestAction<T, R extends RestAction<T>> implements AuditableRestAction<T>, CacheRestAction<T> {
   private final JDA api;
   private final Class<T> type;
   private final Supplier<T> valueSupplier;
   private final Supplier<R> actionSupplier;
   private boolean useCache;
   private String reason;
   private long deadline;
   private BooleanSupplier isAction;
   private BooleanSupplier transitiveChecks;

   public DeferredRestAction(JDA api, Supplier<R> actionSupplier) {
      this(api, (Class)null, (Supplier)null, actionSupplier);
   }

   public DeferredRestAction(JDA api, Class<T> type, Supplier<T> valueSupplier, Supplier<R> actionSupplier) {
      this.useCache = true;
      this.deadline = -1L;
      this.api = api;
      this.type = type;
      this.valueSupplier = valueSupplier;
      this.actionSupplier = actionSupplier;
   }

   @Nonnull
   public JDA getJDA() {
      return this.api;
   }

   @Nonnull
   public AuditableRestAction<T> reason(String reason) {
      this.reason = reason;
      return this;
   }

   @Nonnull
   public DeferredRestAction<T, R> setCheck(BooleanSupplier checks) {
      this.transitiveChecks = checks;
      return this;
   }

   @Nullable
   public BooleanSupplier getCheck() {
      return this.transitiveChecks;
   }

   @Nonnull
   public DeferredRestAction<T, R> timeout(long timeout, @Nonnull TimeUnit unit) {
      Checks.notNull(unit, "TimeUnit");
      return this.deadline(timeout <= 0L ? 0L : System.currentTimeMillis() + unit.toMillis(timeout));
   }

   @Nonnull
   public DeferredRestAction<T, R> deadline(long timestamp) {
      this.deadline = timestamp;
      return this;
   }

   @Nonnull
   public CacheRestAction<T> useCache(boolean useCache) {
      this.useCache = useCache;
      return this;
   }

   public AuditableRestAction<T> setCacheCheck(BooleanSupplier checks) {
      this.isAction = checks;
      return this;
   }

   public void queue(Consumer<? super T> success, Consumer<? super Throwable> failure) {
      Consumer<? super T> finalSuccess;
      if (success != null) {
         finalSuccess = success;
      } else {
         finalSuccess = RestAction.getDefaultSuccess();
      }

      if (this.type == null) {
         BooleanSupplier checks = this.isAction;
         if (checks != null && checks.getAsBoolean()) {
            this.getAction().queue(success, failure);
         } else {
            finalSuccess.accept((Object)null);
         }

      } else {
         T value = (T)this.valueSupplier.get();
         if (this.useCache && value != null) {
            finalSuccess.accept(value);
         } else {
            this.getAction().queue(success, failure);
         }

      }
   }

   @Nonnull
   public CompletableFuture<T> submit(boolean shouldQueue) {
      if (this.type == null) {
         BooleanSupplier checks = this.isAction;
         return checks != null && checks.getAsBoolean() ? this.getAction().submit(shouldQueue) : CompletableFuture.completedFuture((Object)null);
      } else {
         T value = (T)this.valueSupplier.get();
         return this.useCache && value != null ? CompletableFuture.completedFuture(value) : this.getAction().submit(shouldQueue);
      }
   }

   public T complete(boolean shouldQueue) throws RateLimitedException {
      if (this.type == null) {
         BooleanSupplier checks = this.isAction;
         return (T)(checks != null && checks.getAsBoolean() ? this.getAction().complete(shouldQueue) : null);
      } else {
         T value = (T)this.valueSupplier.get();
         return (T)(this.useCache && value != null ? value : this.getAction().complete(shouldQueue));
      }
   }

   private R getAction() {
      R action = (R)(this.actionSupplier.get());
      action.setCheck(this.transitiveChecks);
      if (this.deadline >= 0L) {
         action.deadline(this.deadline);
      }

      if (action instanceof AuditableRestAction && this.reason != null) {
         ((AuditableRestAction)action).reason(this.reason);
      }

      return action;
   }
}
