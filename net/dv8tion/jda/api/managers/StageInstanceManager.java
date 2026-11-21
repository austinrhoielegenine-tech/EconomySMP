package net.dv8tion.jda.api.managers;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.StageInstance;

public interface StageInstanceManager extends Manager<StageInstanceManager> {
   long TOPIC = 1L;

   @Nonnull
   @CheckReturnValue
   StageInstanceManager reset(long var1);

   @Nonnull
   @CheckReturnValue
   StageInstanceManager reset(@Nonnull long... var1);

   @Nonnull
   StageInstance getStageInstance();

   @Nonnull
   @CheckReturnValue
   StageInstanceManager setTopic(@Nullable String var1);
}
