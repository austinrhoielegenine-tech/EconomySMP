package net.dv8tion.jda.api.requests.restaction;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface FluentAuditableRestAction<T, R extends FluentAuditableRestAction<T, R>> extends AuditableRestAction<T> {
   @Nonnull
   @CheckReturnValue
   R reason(@Nullable String var1);

   @Nonnull
   @CheckReturnValue
   R setCheck(@Nullable BooleanSupplier var1);

   @Nonnull
   @CheckReturnValue
   default R addCheck(@Nonnull BooleanSupplier checks) {
      return (R)(AuditableRestAction.super.addCheck(checks));
   }

   @Nonnull
   @CheckReturnValue
   default R timeout(long timeout, @Nonnull TimeUnit unit) {
      return (R)(AuditableRestAction.super.timeout(timeout, unit));
   }

   @Nonnull
   @CheckReturnValue
   default R deadline(long timestamp) {
      return (R)(AuditableRestAction.super.deadline(timestamp));
   }
}
