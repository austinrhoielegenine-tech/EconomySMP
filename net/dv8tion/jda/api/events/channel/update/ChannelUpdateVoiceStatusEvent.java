package net.dv8tion.jda.api.events.channel.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelField;

public class ChannelUpdateVoiceStatusEvent extends GenericChannelUpdateEvent<String> {
   public static final ChannelField FIELD;
   public static final String IDENTIFIER;

   public ChannelUpdateVoiceStatusEvent(@Nonnull JDA api, long responseNumber, Channel channel, String oldValue, String newValue) {
      super(api, responseNumber, channel, FIELD, oldValue, newValue);
   }

   static {
      FIELD = ChannelField.VOICE_STATUS;
      IDENTIFIER = FIELD.getFieldName();
   }
}
