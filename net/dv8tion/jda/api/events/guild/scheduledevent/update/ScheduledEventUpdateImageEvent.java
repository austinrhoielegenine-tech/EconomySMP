package net.dv8tion.jda.api.events.guild.scheduledevent.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ScheduledEvent;

/** @deprecated */
@Deprecated
public class ScheduledEventUpdateImageEvent extends GenericScheduledEventUpdateEvent<String> {
   public static final String IDENTIFIER = "image";

   public ScheduledEventUpdateImageEvent(@Nonnull JDA api, long responseNumber, @Nonnull ScheduledEvent scheduledEvent, @Nonnull String previous) {
      super(api, responseNumber, scheduledEvent, previous, scheduledEvent.getImageUrl(), "image");
   }

   @Nonnull
   public String getOldImageUrl() {
      return this.getOldValue();
   }

   @Nonnull
   public String getNewImageUrl() {
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
