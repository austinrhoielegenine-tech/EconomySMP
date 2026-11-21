package net.dv8tion.jda.api.events.sticker.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;

public class GuildStickerUpdateAvailableEvent extends GenericGuildStickerUpdateEvent<Boolean> {
   public static final String IDENTIFIER = "available";

   public GuildStickerUpdateAvailableEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull GuildSticker sticker, boolean oldValue) {
      super(api, responseNumber, guild, sticker, "available", oldValue, sticker.isAvailable());
   }

   @Nonnull
   public Boolean getOldValue() {
      return (Boolean)super.getOldValue();
   }

   @Nonnull
   public Boolean getNewValue() {
      return (Boolean)super.getNewValue();
   }
}
