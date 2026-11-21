package net.dv8tion.jda.api.events.sticker.update;

import java.util.Set;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;

public class GuildStickerUpdateTagsEvent extends GenericGuildStickerUpdateEvent<Set<String>> {
   public static final String IDENTIFIER = "tags";

   public GuildStickerUpdateTagsEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull GuildSticker sticker, @Nonnull Set<String> oldValue) {
      super(api, responseNumber, guild, sticker, "tags", oldValue, sticker.getTags());
   }

   @Nonnull
   public Set<String> getOldValue() {
      return (Set)super.getOldValue();
   }

   @Nonnull
   public Set<String> getNewValue() {
      return (Set)super.getNewValue();
   }
}
