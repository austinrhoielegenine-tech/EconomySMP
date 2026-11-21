package net.dv8tion.jda.api.requests.restaction;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.requests.RestAction;

public interface CacheRestAction<T> extends RestAction<T> {
   @Nonnull
   @CheckReturnValue
   CacheRestAction<T> setCheck(@Nullable BooleanSupplier var1);

   @Nonnull
   @CheckReturnValue
   default CacheRestAction<T> addCheck(@Nonnull BooleanSupplier checks) {
      return (CacheRestAction)RestAction.super.addCheck(checks);
   }

   @Nonnull
   @CheckReturnValue
   default CacheRestAction<T> timeout(long timeout, @Nonnull TimeUnit unit) {
      return (CacheRestAction)RestAction.super.timeout(timeout, unit);
   }

   @Nonnull
   @CheckReturnValue
   default CacheRestAction<T> deadline(long timestamp) {
      return (CacheRestAction)RestAction.super.deadline(timestamp);
   }

   @Nonnull
   @CheckReturnValue
   CacheRestAction<T> useCache(boolean var1);
}
