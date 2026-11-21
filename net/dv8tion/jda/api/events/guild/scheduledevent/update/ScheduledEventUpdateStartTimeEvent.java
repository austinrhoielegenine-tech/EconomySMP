package net.dv8tion.jda.api.events.guild.scheduledevent.update;

import java.time.OffsetDateTime;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ScheduledEvent;

public class ScheduledEventUpdateStartTimeEvent extends GenericScheduledEventUpdateEvent<OffsetDateTime> {
   public static final String IDENTIFIER = "start_time";

   public ScheduledEventUpdateStartTimeEvent(@Nonnull JDA api, long responseNumber, @Nonnull ScheduledEvent scheduledEvent, @Nonnull OffsetDateTime previous) {
      super(api, responseNumber, scheduledEvent, previous, scheduledEvent.getStartTime(), "start_time");
   }

   @Nonnull
   public OffsetDateTime getOldStartTime() {
      return this.getOldValue();
   }

   @Nonnull
   public OffsetDateTime getNewStartTime() {
      return this.getNewValue();
   }

   @Nonnull
   public OffsetDateTime getOldValue() {
      return (OffsetDateTime)super.getOldValue();
   }

   @Nonnull
   public OffsetDateTime getNewValue() {
      return (OffsetDateTime)super.getNewValue();
   }
}
