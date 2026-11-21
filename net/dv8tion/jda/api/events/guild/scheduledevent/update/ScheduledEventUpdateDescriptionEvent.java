package net.dv8tion.jda.api.events.guild.scheduledevent.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ScheduledEvent;

public class ScheduledEventUpdateDescriptionEvent extends GenericScheduledEventUpdateEvent<String> {
   public static final String IDENTIFIER = "description";

   public ScheduledEventUpdateDescriptionEvent(@Nonnull JDA api, long responseNumber, @Nonnull ScheduledEvent scheduledEvent, @Nullable String previous) {
      super(api, responseNumber, scheduledEvent, previous, scheduledEvent.getDescription(), "description");
   }

   @Nullable
   public String getOldDescription() {
      return (String)this.getOldValue();
   }

   @Nullable
   public String getNewDescription() {
      return (String)this.getNewValue();
   }
}
