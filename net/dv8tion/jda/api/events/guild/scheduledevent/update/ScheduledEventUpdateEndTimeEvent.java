package net.dv8tion.jda.api.events.guild.scheduledevent.update;

import java.time.OffsetDateTime;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ScheduledEvent;

public class ScheduledEventUpdateEndTimeEvent extends GenericScheduledEventUpdateEvent<OffsetDateTime> {
   public static final String IDENTIFIER = "end_time";

   public ScheduledEventUpdateEndTimeEvent(@Nonnull JDA api, long responseNumber, @Nonnull ScheduledEvent scheduledEvent, @Nullable OffsetDateTime previous) {
      super(api, responseNumber, scheduledEvent, previous, scheduledEvent.getEndTime(), "end_time");
   }

   @Nullable
   public OffsetDateTime getOldEndTime() {
      return (OffsetDateTime)this.getOldValue();
   }

   @Nullable
   public OffsetDateTime getNewEndTime() {
      return (OffsetDateTime)this.getNewValue();
   }
}
