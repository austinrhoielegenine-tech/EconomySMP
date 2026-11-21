package net.dv8tion.jda.internal.entities.emoji;

import java.util.Objects;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.emoji.ApplicationEmoji;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.EncodingUtil;
import net.dv8tion.jda.internal.utils.EntityString;

public class UnicodeEmojiImpl implements UnicodeEmoji, EmojiUnion {
   private final String name;

   public UnicodeEmojiImpl(String name) {
      this.name = name;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   @Nonnull
   public String getAsReactionCode() {
      return this.name;
   }

   @Nonnull
   public String getAsCodepoints() {
      return EncodingUtil.encodeCodepoints(this.name);
   }

   @Nonnull
   public DataObject toData() {
      return DataObject.empty().put("name", this.name);
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.name});
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else {
         return !(obj instanceof UnicodeEmoji) ? false : this.name.equals(((UnicodeEmoji)obj).getName());
      }
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("codepoints", this.getAsCodepoints()).toString();
   }

   @Nonnull
   public UnicodeEmoji asUnicode() {
      return this;
   }

   @Nonnull
   public CustomEmoji asCustom() {
      throw new IllegalStateException("Cannot convert UnicodeEmoji into CustomEmoji!");
   }

   @Nonnull
   public RichCustomEmoji asRich() {
      throw new IllegalStateException("Cannot convert UnicodeEmoji into RichCustomEmoji!");
   }

   @Nonnull
   public ApplicationEmoji asApplication() {
      throw new IllegalStateException("Cannot convert UnicodeEmoji to ApplicationEmoji!");
   }
}
