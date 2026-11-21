package net.dv8tion.jda.api.entities.channel.forums;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Checks;

public class ForumTagData implements BaseForumTag {
   private String name;
   private Emoji emoji;
   private boolean moderated;
   private long id;

   public ForumTagData(@Nonnull String name) {
      this.setName(name);
   }

   @Nonnull
   public static ForumTagData from(@Nonnull BaseForumTag tag) {
      Checks.notNull(tag, "Tag");
      ForumTagData data = (new ForumTagData(tag.getName())).setEmoji(tag.getEmoji()).setModerated(tag.isModerated());
      if (tag instanceof ForumTagSnowflake) {
         data.id = ((ForumTagSnowflake)tag).getIdLong();
      }

      return data;
   }

   @Nonnull
   public ForumTagData setName(@Nonnull String name) {
      Checks.notEmpty(name, "Name");
      Checks.notLonger(name, 20, "Name");
      this.name = name;
      return this;
   }

   @Nonnull
   public ForumTagData setModerated(boolean moderated) {
      this.moderated = moderated;
      return this;
   }

   @Nonnull
   public ForumTagData setEmoji(@Nullable Emoji emoji) {
      this.emoji = emoji;
      return this;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   public boolean isModerated() {
      return this.moderated;
   }

   @Nullable
   public EmojiUnion getEmoji() {
      return (EmojiUnion)this.emoji;
   }

   @Nonnull
   public DataObject toData() {
      DataObject json = BaseForumTag.super.toData();
      if (this.id != 0L) {
         json.put("id", Long.toUnsignedString(this.id));
      }

      return json;
   }

   public String toString() {
      return this.toData().toString();
   }
}
