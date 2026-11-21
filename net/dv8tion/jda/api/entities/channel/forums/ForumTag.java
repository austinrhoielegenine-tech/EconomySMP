package net.dv8tion.jda.api.entities.channel.forums;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Checks;

public interface ForumTag extends ForumTagSnowflake, Comparable<ForumTag>, BaseForumTag {
   int MAX_NAME_LENGTH = 20;

   int getPosition();

   default int compareTo(@Nonnull ForumTag o) {
      Checks.notNull(o, "ForumTag");
      return Integer.compare(this.getPosition(), o.getPosition());
   }

   @Nonnull
   default DataObject toData() {
      return BaseForumTag.super.toData().put("id", this.getId());
   }
}
