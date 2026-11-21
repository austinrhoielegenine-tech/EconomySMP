package net.dv8tion.jda.api.events.emoji;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.Event;

public abstract class GenericEmojiEvent extends Event {
   protected final RichCustomEmoji emoji;

   public GenericEmojiEvent(@Nonnull JDA api, long responseNumber, @Nonnull RichCustomEmoji emoji) {
      super(api, responseNumber);
      this.emoji = emoji;
   }

   @Nonnull
   public Guild getGuild() {
      return this.emoji.getGuild();
   }

   @Nonnull
   public RichCustomEmoji getEmoji() {
      return this.emoji;
   }

   public boolean isManaged() {
      return this.emoji.isManaged();
   }
}
