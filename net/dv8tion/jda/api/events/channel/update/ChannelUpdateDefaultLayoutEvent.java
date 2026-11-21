package net.dv8tion.jda.api.events.channel.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelField;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;

public class ChannelUpdateDefaultLayoutEvent extends GenericChannelUpdateEvent<ForumChannel.Layout> {
   public static final ChannelField FIELD;
   public static final String IDENTIFIER;

   public ChannelUpdateDefaultLayoutEvent(@Nonnull JDA api, long responseNumber, @Nonnull Channel channel, @Nonnull ForumChannel.Layout oldValue, @Nonnull ForumChannel.Layout newValue) {
      super(api, responseNumber, channel, ChannelField.DEFAULT_FORUM_LAYOUT, oldValue, newValue);
   }

   @Nonnull
   public ForumChannel.Layout getOldValue() {
      return (ForumChannel.Layout)super.getOldValue();
   }

   @Nonnull
   public ForumChannel.Layout getNewValue() {
      return (ForumChannel.Layout)super.getNewValue();
   }

   static {
      FIELD = ChannelField.DEFAULT_FORUM_LAYOUT;
      IDENTIFIER = FIELD.getFieldName();
   }
}
