package net.dv8tion.jda.api.events.sticker.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;

public class GuildStickerUpdateDescriptionEvent extends GenericGuildStickerUpdateEvent<String> {
   public static final String IDENTIFIER = "description";

   public GuildStickerUpdateDescriptionEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull GuildSticker sticker, @Nonnull String oldValue) {
      super(api, responseNumber, guild, sticker, "description", oldValue, sticker.getDescription());
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
