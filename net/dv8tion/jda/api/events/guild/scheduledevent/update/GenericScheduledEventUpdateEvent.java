package net.dv8tion.jda.api.events.guild.scheduledevent.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.events.UpdateEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.GenericScheduledEventGatewayEvent;

public abstract class GenericScheduledEventUpdateEvent<T> extends GenericScheduledEventGatewayEvent implements UpdateEvent<ScheduledEvent, T> {
   protected final T previous;
   protected final T next;
   protected final String identifier;

   public GenericScheduledEventUpdateEvent(@Nonnull JDA api, long responseNumber, @Nonnull ScheduledEvent scheduledEvent, @Nullable T previous, @Nullable T next, @Nonnull String identifier) {
      super(api, responseNumber, scheduledEvent);
      this.previous = previous;
      this.next = next;
      this.identifier = identifier;
   }

   @Nonnull
   public ScheduledEvent getEntity() {
      return this.getScheduledEvent();
   }

   @Nonnull
   public String getPropertyIdentifier() {
      return this.identifier;
   }

   @Nullable
   public T getOldValue() {
      return this.previous;
   }

   @Nullable
   public T getNewValue() {
      return this.next;
   }

   public String toString() {
      return "ScheduledEventUpdate[" + this.getPropertyIdentifier() + "](" + this.getOldValue() + "->" + this.getNewValue() + ')';
   }
}
