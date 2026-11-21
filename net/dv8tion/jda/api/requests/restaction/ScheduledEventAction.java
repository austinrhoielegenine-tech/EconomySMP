package net.dv8tion.jda.api.requests.restaction;

import java.time.temporal.TemporalAccessor;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.ScheduledEvent;

public interface ScheduledEventAction extends FluentAuditableRestAction<ScheduledEvent, ScheduledEventAction> {
   @Nonnull
   Guild getGuild();

   @Nonnull
   @CheckReturnValue
   ScheduledEventAction setName(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   ScheduledEventAction setDescription(@Nullable String var1);

   @Nonnull
   @CheckReturnValue
   ScheduledEventAction setStartTime(@Nonnull TemporalAccessor var1);

   @Nonnull
   @CheckReturnValue
   ScheduledEventAction setEndTime(@Nullable TemporalAccessor var1);

   @Nonnull
   @CheckReturnValue
   ScheduledEventAction setImage(@Nullable Icon var1);
}
