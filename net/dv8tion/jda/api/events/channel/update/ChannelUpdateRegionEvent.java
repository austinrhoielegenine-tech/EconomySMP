package net.dv8tion.jda.api.events.channel.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelField;

public class ChannelUpdateRegionEvent extends GenericChannelUpdateEvent<Region> {
   public static final ChannelField FIELD;
   public static final String IDENTIFIER;

   public ChannelUpdateRegionEvent(@Nonnull JDA api, long responseNumber, Channel channel, Region oldValue, Region newValue) {
      super(api, responseNumber, channel, FIELD, oldValue, newValue);
   }

   static {
      FIELD = ChannelField.REGION;
      IDENTIFIER = FIELD.getFieldName();
   }
}
