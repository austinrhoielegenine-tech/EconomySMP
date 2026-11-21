package net.dv8tion.jda.api.events.channel.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelField;

public class ChannelUpdateArchivedEvent extends GenericChannelUpdateEvent<Boolean> {
   public static final ChannelField FIELD;
   public static final String IDENTIFIER;

   public ChannelUpdateArchivedEvent(@Nonnull JDA api, long responseNumber, Channel channel, Boolean oldValue, Boolean newValue) {
      super(api, responseNumber, channel, FIELD, oldValue, newValue);
   }

   static {
      FIELD = ChannelField.ARCHIVED;
      IDENTIFIER = FIELD.getFieldName();
   }
}
