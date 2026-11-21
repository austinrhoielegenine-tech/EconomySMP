package net.dv8tion.jda.api.events.channel.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelField;

public class ChannelUpdateSlowmodeEvent extends GenericChannelUpdateEvent<Integer> {
   public static final ChannelField FIELD;
   public static final String IDENTIFIER;

   public ChannelUpdateSlowmodeEvent(@Nonnull JDA api, long responseNumber, Channel channel, Integer oldValue, Integer newValue) {
      super(api, responseNumber, channel, FIELD, oldValue, newValue);
   }

   static {
      FIELD = ChannelField.SLOWMODE;
      IDENTIFIER = FIELD.getFieldName();
   }
}
