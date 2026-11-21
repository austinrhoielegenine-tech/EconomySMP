package net.dv8tion.jda.api.events.guild.scheduledevent.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ScheduledEvent;

public class ScheduledEventUpdateStatusEvent extends GenericScheduledEventUpdateEvent<ScheduledEvent.Status> {
   public static final String IDENTIFIER = "status";

   public ScheduledEventUpdateStatusEvent(@Nonnull JDA api, long responseNumber, @Nonnull ScheduledEvent scheduledEvent, @Nonnull ScheduledEvent.Status previous) {
      super(api, responseNumber, scheduledEvent, previous, scheduledEvent.getStatus(), "status");
   }

   @Nonnull
   public ScheduledEvent.Status getOldStatus() {
      return this.getOldValue();
   }

   @Nonnull
   public ScheduledEvent.Status getNewStatus() {
      return this.getNewValue();
   }

   @Nonnull
   public ScheduledEvent.Status getOldValue() {
      return (ScheduledEvent.Status)super.getOldValue();
   }

   @Nonnull
   public ScheduledEvent.Status getNewValue() {
      return (ScheduledEvent.Status)super.getNewValue();
   }
}
