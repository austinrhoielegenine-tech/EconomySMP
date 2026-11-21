package net.dv8tion.jda.api.managers;

import java.time.temporal.TemporalAccessor;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

public interface ScheduledEventManager extends Manager<ScheduledEventManager> {
   long NAME = 1L;
   long DESCRIPTION = 2L;
   long LOCATION = 4L;
   long START_TIME = 8L;
   long END_TIME = 16L;
   long IMAGE = 32L;
   long STATUS = 64L;

   @Nonnull
   @CheckReturnValue
   ScheduledEventManager reset(long var1);

   @Nonnull
   @CheckReturnValue
   ScheduledEventManager reset(@Nonnull long... var1);

   @Nonnull
   ScheduledEvent getScheduledEvent();

   @Nonnull
   default Guild getGuild() {
      return this.getScheduledEvent().getGuild();
   }

   @Nonnull
   @CheckReturnValue
   ScheduledEventManager setName(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   ScheduledEventManager setDescription(@Nullable String var1);

   @Nonnull
   @CheckReturnValue
   ScheduledEventManager setImage(@Nullable Icon var1);

   @Nonnull
   @CheckReturnValue
   ScheduledEventManager setLocation(@Nonnull GuildChannel var1);

   @Nonnull
   @CheckReturnValue
   ScheduledEventManager setLocation(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   ScheduledEventManager setStartTime(@Nonnull TemporalAccessor var1);

   @Nonnull
   @CheckReturnValue
   ScheduledEventManager setEndTime(@Nonnull TemporalAccessor var1);

   @Nonnull
   @CheckReturnValue
   ScheduledEventManager setStatus(@Nonnull ScheduledEvent.Status var1);
}
