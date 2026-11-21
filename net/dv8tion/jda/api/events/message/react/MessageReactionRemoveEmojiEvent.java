package net.dv8tion.jda.api.events.message.react;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;

public class MessageReactionRemoveEmojiEvent extends GenericMessageEvent {
   private final MessageReaction reaction;

   public MessageReactionRemoveEmojiEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull MessageChannel channel, @Nonnull MessageReaction reaction) {
      super(api, responseNumber, messageId, channel);
      this.reaction = reaction;
   }

   @Nonnull
   public MessageReaction getReaction() {
      return this.reaction;
   }

   @Nonnull
   public EmojiUnion getEmoji() {
      return this.reaction.getEmoji();
   }
}
