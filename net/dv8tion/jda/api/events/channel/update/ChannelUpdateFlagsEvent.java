package net.dv8tion.jda.api.events.channel.update;

import java.util.EnumSet;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelField;
import net.dv8tion.jda.api.entities.channel.ChannelFlag;

public class ChannelUpdateFlagsEvent extends GenericChannelUpdateEvent<EnumSet<ChannelFlag>> {
   public static final ChannelField FIELD;
   public static final String IDENTIFIER;

   public ChannelUpdateFlagsEvent(@Nonnull JDA api, long responseNumber, @Nonnull Channel channel, @Nonnull EnumSet<ChannelFlag> oldValue, @Nonnull EnumSet<ChannelFlag> newValue) {
      super(api, responseNumber, channel, FIELD, oldValue, newValue);
   }

   @Nonnull
   public EnumSet<ChannelFlag> getOldValue() {
      return (EnumSet)super.getOldValue();
   }

   @Nonnull
   public EnumSet<ChannelFlag> getNewValue() {
      return (EnumSet)super.getNewValue();
   }

   static {
      FIELD = ChannelField.FLAGS;
      IDENTIFIER = FIELD.getFieldName();
   }
}
