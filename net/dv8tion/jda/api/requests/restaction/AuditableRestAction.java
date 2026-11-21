package net.dv8tion.jda.api.requests.restaction;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.requests.RestAction;

public interface AuditableRestAction<T> extends RestAction<T> {
   int MAX_REASON_LENGTH = 512;

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<T> reason(@Nullable String var1);

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<T> setCheck(@Nullable BooleanSupplier var1);

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<T> timeout(long timeout, @Nonnull TimeUnit unit) {
      return (AuditableRestAction)RestAction.super.timeout(timeout, unit);
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<T> deadline(long timestamp) {
      return (AuditableRestAction)RestAction.super.deadline(timestamp);
   }
}
