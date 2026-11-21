package net.dv8tion.jda.api.entities.channel.forums;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;

public interface BaseForumTag extends SerializableData {
   @Nonnull
   String getName();

   boolean isModerated();

   @Nullable
   EmojiUnion getEmoji();

   @Nonnull
   default DataObject toData() {
      DataObject json = DataObject.empty().put("name", this.getName()).put("moderated", this.isModerated());
      EmojiUnion emoji = this.getEmoji();
      if (emoji instanceof UnicodeEmoji) {
         json.put("emoji_name", emoji.getName());
      } else if (emoji instanceof CustomEmoji) {
         json.put("emoji_id", ((CustomEmoji)emoji).getId());
      }

      return json;
   }
}
