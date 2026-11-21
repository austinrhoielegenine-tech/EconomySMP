package net.dv8tion.jda.api.events.guild.scheduledevent.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ScheduledEvent;

public class ScheduledEventUpdateCoverImageEvent extends GenericScheduledEventUpdateEvent<String> {
   public static final String IDENTIFIER = "image";

   public ScheduledEventUpdateCoverImageEvent(@Nonnull JDA api, long responseNumber, @Nonnull ScheduledEvent scheduledEvent, @Nullable String previous) {
      super(api, responseNumber, scheduledEvent, previous, scheduledEvent.getCoverImageId(), "image");
   }

   @Nullable
   public String getOldImageUrl() {
      return (String)this.getOldValue();
   }

   @Nullable
   public String getNewImageUrl() {
      return (String)this.getNewValue();
   }
}
