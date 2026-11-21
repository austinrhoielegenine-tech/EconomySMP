package net.dv8tion.jda.api.events.emoji;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;

public class EmojiAddedEvent extends GenericEmojiEvent {
   public EmojiAddedEvent(@Nonnull JDA api, long responseNumber, @Nonnull RichCustomEmoji emoji) {
      super(api, responseNumber, emoji);
   }
}
