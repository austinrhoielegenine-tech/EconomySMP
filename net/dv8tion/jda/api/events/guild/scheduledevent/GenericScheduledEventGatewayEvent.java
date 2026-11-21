package net.dv8tion.jda.api.events.guild.scheduledevent;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;

public abstract class GenericScheduledEventGatewayEvent extends GenericGuildEvent {
   protected final ScheduledEvent scheduledEvent;

   public GenericScheduledEventGatewayEvent(@Nonnull JDA api, long responseNumber, @Nonnull ScheduledEvent scheduledEvent) {
      super(api, responseNumber, scheduledEvent.getGuild());
      this.scheduledEvent = scheduledEvent;
   }

   @Nonnull
   public ScheduledEvent getScheduledEvent() {
      return this.scheduledEvent;
   }
}
