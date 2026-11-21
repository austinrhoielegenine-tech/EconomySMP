package net.dv8tion.jda.api.entities.sticker;

import javax.annotation.Nonnull;

public interface StickerUnion extends RichSticker {
   @Nonnull
   StandardSticker asStandardSticker();

   @Nonnull
   GuildSticker asGuildSticker();
}
