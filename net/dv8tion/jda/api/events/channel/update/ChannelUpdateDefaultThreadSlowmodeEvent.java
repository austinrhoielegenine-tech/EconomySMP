package net.dv8tion.jda.api.events.channel.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelField;

public class ChannelUpdateDefaultThreadSlowmodeEvent extends GenericChannelUpdateEvent<Integer> {
   public static final ChannelField FIELD;
   public static final String IDENTIFIER;

   public ChannelUpdateDefaultThreadSlowmodeEvent(@Nonnull JDA api, long responseNumber, @Nonnull Channel channel, int oldValue, int newValue) {
      super(api, responseNumber, channel, FIELD, oldValue, newValue);
   }

   @Nonnull
   public Integer getOldValue() {
      return (Integer)super.getOldValue();
   }

   @Nonnull
   public Integer getNewValue() {
      return (Integer)super.getNewValue();
   }

   static {
      FIELD = ChannelField.DEFAULT_THREAD_SLOWMODE;
      IDENTIFIER = FIELD.getFieldName();
   }
}
