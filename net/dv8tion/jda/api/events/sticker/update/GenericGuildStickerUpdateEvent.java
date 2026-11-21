package net.dv8tion.jda.api.events.sticker.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.events.UpdateEvent;
import net.dv8tion.jda.api.events.sticker.GenericGuildStickerEvent;

public abstract class GenericGuildStickerUpdateEvent<T> extends GenericGuildStickerEvent implements UpdateEvent<GuildSticker, T> {
   protected final String identifier;
   protected final T previous;
   protected final T next;

   public GenericGuildStickerUpdateEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull GuildSticker sticker, @Nonnull String identifier, T oldValue, T newValue) {
      super(api, responseNumber, guild, sticker);
      this.identifier = identifier;
      this.previous = oldValue;
      this.next = newValue;
   }

   @Nonnull
   public String getPropertyIdentifier() {
      return this.identifier;
   }

   @Nonnull
   public GuildSticker getEntity() {
      return this.getSticker();
   }

   @Nullable
   public T getOldValue() {
      return this.previous;
   }

   @Nullable
   public T getNewValue() {
      return this.next;
   }
}
