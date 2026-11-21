package net.dv8tion.jda.api.managers;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.internal.managers.ManagerBase;

public interface Manager<M extends Manager<M>> extends AuditableRestAction<Void> {
   static void setPermissionChecksEnabled(boolean enable) {
      ManagerBase.setPermissionChecksEnabled(enable);
   }

   static boolean isPermissionChecksEnabled() {
      return ManagerBase.isPermissionChecksEnabled();
   }

   @Nonnull
   @CheckReturnValue
   M setCheck(@Nonnull BooleanSupplier var1);

   @Nonnull
   @CheckReturnValue
   M timeout(long var1, @Nonnull TimeUnit var3);

   @Nonnull
   @CheckReturnValue
   M deadline(long var1);

   @Nonnull
   @CheckReturnValue
   M reset(long var1);

   @Nonnull
   @CheckReturnValue
   M reset(@Nonnull long... var1);

   @Nonnull
   @CheckReturnValue
   M reset();
}
