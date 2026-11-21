package net.dv8tion.jda.api.events.channel.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.ChannelField;
import net.dv8tion.jda.api.entities.channel.attribute.IPostContainer;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;

public class ChannelUpdateDefaultReactionEvent extends GenericChannelUpdateEvent<EmojiUnion> {
   public static final ChannelField FIELD;
   public static final String IDENTIFIER;

   public ChannelUpdateDefaultReactionEvent(@Nonnull JDA api, long responseNumber, @Nonnull IPostContainer channel, @Nullable EmojiUnion oldValue, @Nullable EmojiUnion newValue) {
      super(api, responseNumber, channel, FIELD, oldValue, newValue);
   }

   static {
      FIELD = ChannelField.DEFAULT_REACTION_EMOJI;
      IDENTIFIER = FIELD.getFieldName();
   }
}
