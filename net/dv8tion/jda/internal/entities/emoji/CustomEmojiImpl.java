package net.dv8tion.jda.internal.entities.emoji;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.emoji.ApplicationEmoji;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;

public class CustomEmojiImpl implements CustomEmoji, EmojiUnion {
   private final String name;
   private final long id;
   private final boolean animated;

   public CustomEmojiImpl(String name, long id, boolean animated) {
      this.name = name;
      this.id = id;
      this.animated = animated;
   }

   @Nonnull
   public String getAsReactionCode() {
      return this.name + ":" + this.id;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   public long getIdLong() {
      return this.id;
   }

   public boolean isAnimated() {
      return this.animated;
   }

   @Nonnull
   public DataObject toData() {
      return DataObject.empty().put("name", this.name).put("id", this.id).put("animated", this.animated);
   }

   @Nonnull
   public String getAsMention() {
      return Helpers.format("<%s:%s:%s>", this.animated ? "a" : "", this.name, this.getId());
   }

   @Nonnull
   public String getFormatted() {
      return CustomEmoji.super.getFormatted();
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof CustomEmoji)) {
         return false;
      } else {
         CustomEmoji other = (CustomEmoji)obj;
         return this.id == other.getIdLong();
      }
   }

   public String toString() {
      return (new EntityString(this)).setName(this.name).toString();
   }

   @Nonnull
   public UnicodeEmoji asUnicode() {
      throw new IllegalStateException("Cannot convert CustomEmoji into UnicodeEmoji!");
   }

   @Nonnull
   public CustomEmoji asCustom() {
      return this;
   }

   @Nonnull
   public RichCustomEmoji asRich() {
      throw new IllegalStateException("Cannot convert CustomEmoji to RichCustomEmoji!");
   }

   @Nonnull
   public ApplicationEmoji asApplication() {
      throw new IllegalStateException("Cannot convert CustomEmoji to ApplicationEmoji!");
   }
}
