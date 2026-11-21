package net.dv8tion.jda.api.entities.emoji;

import javax.annotation.Nonnull;

public interface UnicodeEmoji extends Emoji {
   @Nonnull
   String getAsCodepoints();

   @Nonnull
   default Emoji.Type getType() {
      return Emoji.Type.UNICODE;
   }

   @Nonnull
   default String getFormatted() {
      return this.getName();
   }
}
