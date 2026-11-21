package net.dv8tion.jda.api.entities.emoji;

import javax.annotation.Nonnull;

public interface EmojiUnion extends Emoji {
   @Nonnull
   UnicodeEmoji asUnicode();

   @Nonnull
   CustomEmoji asCustom();

   @Nonnull
   RichCustomEmoji asRich();

   @Nonnull
   ApplicationEmoji asApplication();
}
