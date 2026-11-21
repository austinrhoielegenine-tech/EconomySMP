package net.dv8tion.jda.api.events.sticker;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.events.Event;

public abstract class GenericGuildStickerEvent extends Event {
   protected final Guild guild;
   protected final GuildSticker sticker;

   public GenericGuildStickerEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull GuildSticker sticker) {
      super(api, responseNumber);
      this.guild = guild;
      this.sticker = sticker;
   }

   @Nonnull
   public GuildSticker getSticker() {
      return this.sticker;
   }

   @Nonnull
   public Guild getGuild() {
      return this.guild;
   }
}
