package net.dv8tion.jda.api.events.channel.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelField;
import net.dv8tion.jda.api.entities.channel.ChannelType;

public class ChannelUpdateTypeEvent extends GenericChannelUpdateEvent<ChannelType> {
   public static final ChannelField FIELD;
   public static final String IDENTIFIER;

   public ChannelUpdateTypeEvent(@Nonnull JDA api, long responseNumber, Channel channel, ChannelType oldValue, ChannelType newValue) {
      super(api, responseNumber, channel, FIELD, oldValue, newValue);
   }

   static {
      FIELD = ChannelField.TYPE;
      IDENTIFIER = FIELD.getFieldName();
   }
}
