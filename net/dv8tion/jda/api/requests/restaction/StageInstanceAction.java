package net.dv8tion.jda.api.requests.restaction;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.StageInstance;
import net.dv8tion.jda.api.requests.RestAction;

public interface StageInstanceAction extends RestAction<StageInstance> {
   @Nonnull
   @CheckReturnValue
   StageInstanceAction setCheck(@Nullable BooleanSupplier var1);

   @Nonnull
   @CheckReturnValue
   StageInstanceAction timeout(long var1, @Nonnull TimeUnit var3);

   @Nonnull
   @CheckReturnValue
   StageInstanceAction deadline(long var1);

   @Nonnull
   @CheckReturnValue
   StageInstanceAction setTopic(@Nonnull String var1);
}
