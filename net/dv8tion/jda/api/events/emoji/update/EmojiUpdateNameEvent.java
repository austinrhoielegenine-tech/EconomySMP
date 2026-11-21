package net.dv8tion.jda.api.events.emoji.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;

public class EmojiUpdateNameEvent extends GenericEmojiUpdateEvent<String> {
   public static final String IDENTIFIER = "name";

   public EmojiUpdateNameEvent(@Nonnull JDA api, long responseNumber, @Nonnull RichCustomEmoji emoji, @Nonnull String oldName) {
      super(api, responseNumber, emoji, oldName, emoji.getName(), "name");
   }

   @Nonnull
   public String getOldName() {
      return this.getOldValue();
   }

   @Nonnull
   public String getNewName() {
      return this.getNewValue();
   }

   @Nonnull
   public String getOldValue() {
      return (String)super.getOldValue();
   }

   @Nonnull
   public String getNewValue() {
      return (String)super.getNewValue();
   }
}
