package net.dv8tion.jda.api.events.guild.scheduledevent;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ScheduledEvent;

public class ScheduledEventUserRemoveEvent extends GenericScheduledEventUserEvent {
   public ScheduledEventUserRemoveEvent(@Nonnull JDA api, long responseNumber, @Nonnull ScheduledEvent scheduledEvent, long userId) {
      super(api, responseNumber, scheduledEvent, userId);
   }
}
