package net.dv8tion.jda.internal.entities;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.emoji.CustomEmojiImpl;
import net.dv8tion.jda.internal.utils.EntityString;

public class ForumTagImpl extends ForumTagSnowflakeImpl implements ForumTag {
   private boolean moderated;
   private String name;
   private int position;
   private Emoji emoji;

   public ForumTagImpl(long id) {
      super(id);
   }

   public int getPosition() {
      return this.position;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   public boolean isModerated() {
      return this.moderated;
   }

   public EmojiUnion getEmoji() {
      return (EmojiUnion)this.emoji;
   }

   public ForumTagImpl setModerated(boolean moderated) {
      this.moderated = moderated;
      return this;
   }

   public ForumTagImpl setName(String name) {
      this.name = name;
      return this;
   }

   public ForumTagImpl setPosition(int position) {
      this.position = position;
      return this;
   }

   public ForumTagImpl setEmoji(DataObject json) {
      long id = json.getUnsignedLong("emoji_id", 0L);
      if (id != 0L) {
         this.emoji = new CustomEmojiImpl(json.getString("emoji_name", ""), id, false);
      } else if (!json.isNull("emoji_name")) {
         this.emoji = Emoji.fromUnicode(json.getString("emoji_name"));
      } else {
         this.emoji = null;
      }

      return this;
   }

   public String toString() {
      return (new EntityString(this)).setName(this.name).toString();
   }
}
