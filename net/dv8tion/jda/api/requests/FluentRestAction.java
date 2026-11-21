package net.dv8tion.jda.api.requests;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface FluentRestAction<T, R extends FluentRestAction<T, R>> extends RestAction<T> {
   @Nonnull
   @CheckReturnValue
   R setCheck(@Nullable BooleanSupplier var1);

   @Nonnull
   @CheckReturnValue
   default R addCheck(@Nonnull BooleanSupplier checks) {
      return (R)(RestAction.super.addCheck(checks));
   }

   @Nonnull
   @CheckReturnValue
   default R timeout(long timeout, @Nonnull TimeUnit unit) {
      return (R)(RestAction.super.timeout(timeout, unit));
   }

   @Nonnull
   @CheckReturnValue
   default R deadline(long timestamp) {
      return (R)(RestAction.super.deadline(timestamp));
   }
}
