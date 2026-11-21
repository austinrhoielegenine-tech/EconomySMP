package net.dv8tion.jda.api.events.sticker;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;

public class GuildStickerRemovedEvent extends GenericGuildStickerEvent {
   public GuildStickerRemovedEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull GuildSticker sticker) {
      super(api, responseNumber, guild, sticker);
   }
}
