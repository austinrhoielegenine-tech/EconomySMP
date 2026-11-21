package net.dv8tion.jda.api.events.guild.scheduledevent.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ScheduledEvent;

public class ScheduledEventUpdateLocationEvent extends GenericScheduledEventUpdateEvent<String> {
   public static final String IDENTIFIER = "location";

   public ScheduledEventUpdateLocationEvent(@Nonnull JDA api, long responseNumber, @Nonnull ScheduledEvent scheduledEvent, @Nonnull String previous) {
      super(api, responseNumber, scheduledEvent, previous, scheduledEvent.getLocation(), "location");
   }

   @Nonnull
   public String getOldLocation() {
      return this.getOldValue();
   }

   @Nonnull
   public String getNewLocation() {
      return this.getNewValue();
   }

   @Nonnull
   public String getOldValue() {
      return (String)super.getOldValue();
   }

   @Nonnull
   public String getNewValue() {
      return (String)super.getNewValue();
   }
}
