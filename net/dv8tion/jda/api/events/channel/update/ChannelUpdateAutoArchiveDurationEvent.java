package net.dv8tion.jda.api.events.channel.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelField;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

public class ChannelUpdateAutoArchiveDurationEvent extends GenericChannelUpdateEvent<ThreadChannel.AutoArchiveDuration> {
   public static final ChannelField FIELD;
   public static final String IDENTIFIER;

   public ChannelUpdateAutoArchiveDurationEvent(@Nonnull JDA api, long responseNumber, Channel channel, ThreadChannel.AutoArchiveDuration oldValue, ThreadChannel.AutoArchiveDuration newValue) {
      super(api, responseNumber, channel, FIELD, oldValue, newValue);
   }

   static {
      FIELD = ChannelField.AUTO_ARCHIVE_DURATION;
      IDENTIFIER = FIELD.getFieldName();
   }
}
