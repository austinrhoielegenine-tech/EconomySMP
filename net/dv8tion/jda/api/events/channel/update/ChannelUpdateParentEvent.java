package net.dv8tion.jda.api.events.channel.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelField;
import net.dv8tion.jda.api.entities.channel.concrete.Category;

public class ChannelUpdateParentEvent extends GenericChannelUpdateEvent<Category> {
   public static final ChannelField FIELD;
   public static final String IDENTIFIER;

   public ChannelUpdateParentEvent(@Nonnull JDA api, long responseNumber, Channel channel, Category oldValue, Category newValue) {
      super(api, responseNumber, channel, FIELD, oldValue, newValue);
   }

   static {
      FIELD = ChannelField.PARENT;
      IDENTIFIER = FIELD.getFieldName();
   }
}
