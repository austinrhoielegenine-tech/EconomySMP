package net.dv8tion.jda.api.events.guild.scheduledevent;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ScheduledEvent;

public class ScheduledEventCreateEvent extends GenericScheduledEventGatewayEvent {
   public ScheduledEventCreateEvent(@Nonnull JDA api, long responseNumber, @Nonnull ScheduledEvent scheduledEvent) {
      super(api, responseNumber, scheduledEvent);
   }
}
