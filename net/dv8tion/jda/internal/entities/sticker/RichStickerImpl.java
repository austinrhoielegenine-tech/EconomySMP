package net.dv8tion.jda.internal.entities.sticker;

import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.entities.sticker.RichSticker;
import net.dv8tion.jda.api.entities.sticker.StandardSticker;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import net.dv8tion.jda.api.entities.sticker.StickerUnion;
import net.dv8tion.jda.internal.utils.EntityString;

public abstract class RichStickerImpl extends StickerItemImpl implements RichSticker, StickerUnion {
   protected Set<String> tags;
   protected String description;

   public RichStickerImpl(long id, Sticker.StickerFormat format, String name, Set<String> tags, String description) {
      super(id, format, name);
      this.tags = Collections.unmodifiableSet(tags);
      this.description = description;
   }

   @Nonnull
   public StandardSticker asStandardSticker() {
      throw new IllegalStateException("Cannot convert sticker of type " + this.getType() + " to StandardSticker!");
   }

   @Nonnull
   public GuildSticker asGuildSticker() {
      throw new IllegalStateException("Cannot convert sticker of type " + this.getType() + " to GuildSticker!");
   }

   @Nonnull
   public Set<String> getTags() {
      return this.tags;
   }

   @Nonnull
   public String getDescription() {
      return this.description;
   }

   public RichStickerImpl setTags(Set<String> tags) {
      this.tags = Collections.unmodifiableSet(tags);
      return this;
   }

   public RichStickerImpl setDescription(String description) {
      this.description = description;
      return this;
   }

   public String toString() {
      return (new EntityString(this)).setType(this.getType()).setName(this.name).toString();
   }
}
