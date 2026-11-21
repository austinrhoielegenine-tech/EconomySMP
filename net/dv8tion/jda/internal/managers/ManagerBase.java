package net.dv8tion.jda.internal.managers;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.managers.Manager;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;

public abstract class ManagerBase<M extends Manager<M>> extends AuditableRestActionImpl<Void> implements Manager<M> {
   private static boolean enablePermissionChecks = true;
   protected long set = 0L;

   public static void setPermissionChecksEnabled(boolean enable) {
      enablePermissionChecks = enable;
   }

   public static boolean isPermissionChecksEnabled() {
      return enablePermissionChecks;
   }

   protected ManagerBase(JDA api, Route.CompiledRoute route) {
      super(api, route);
   }

   @Nonnull
   public M setCheck(@Nonnull BooleanSupplier checks) {
      return (M)(super.setCheck(checks));
   }

   @Nonnull
   public M timeout(long timeout, @Nonnull TimeUnit unit) {
      return (M)(super.timeout(timeout, unit));
   }

   @Nonnull
   public M deadline(long timestamp) {
      return (M)(super.deadline(timestamp));
   }

   @Nonnull
   public M reset(long fields) {
      this.set &= ~fields;
      return (M)this;
   }

   @Nonnull
   public M reset(@Nonnull long... fields) {
      Checks.notNull(fields, "Fields");
      if (fields.length == 0) {
         return (M)this;
      } else if (fields.length == 1) {
         return (M)this.reset(fields[0]);
      } else {
         long sum = fields[0];

         for(int i = 1; i < fields.length; ++i) {
            sum |= fields[i];
         }

         return (M)this.reset(sum);
      }
   }

   @Nonnull
   public M reset() {
      this.set = 0L;
      return (M)this;
   }

   public void queue(Consumer<? super Void> success, Consumer<? super Throwable> failure) {
      if (this.shouldUpdate()) {
         super.queue(success, failure);
      } else if (success != null) {
         success.accept((Object)null);
      } else {
         getDefaultSuccess().accept((Object)null);
      }

   }

   public Void complete(boolean shouldQueue) throws RateLimitedException {
      return this.shouldUpdate() ? (Void)super.complete(shouldQueue) : null;
   }

   protected BooleanSupplier finalizeChecks() {
      return enablePermissionChecks ? this::checkPermissions : super.finalizeChecks();
   }

   protected boolean shouldUpdate() {
      return this.set != 0L;
   }

   protected boolean shouldUpdate(long bit) {
      return (this.set & bit) != 0L;
   }

   protected <E> void withLock(E object, Consumer<? super E> consumer) {
      synchronized(object) {
         consumer.accept(object);
      }
   }

   protected boolean checkPermissions() {
      return true;
   }
}
