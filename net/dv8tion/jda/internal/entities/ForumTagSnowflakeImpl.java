package net.dv8tion.jda.internal.entities;

import net.dv8tion.jda.api.entities.channel.forums.ForumTagSnowflake;
import net.dv8tion.jda.internal.utils.EntityString;

public class ForumTagSnowflakeImpl implements ForumTagSnowflake {
   protected final long id;

   public ForumTagSnowflakeImpl(long id) {
      this.id = id;
   }

   public long getIdLong() {
      return this.id;
   }

   public String toString() {
      return (new EntityString(this)).toString();
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof ForumTagSnowflakeImpl)) {
         return false;
      } else {
         return ((ForumTagSnowflakeImpl)obj).id == this.id;
      }
   }
}
