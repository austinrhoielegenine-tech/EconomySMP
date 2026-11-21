package net.dv8tion.jda.internal.entities;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.internal.utils.EntityString;

public class UserSnowflakeImpl implements UserSnowflake {
   protected final long id;

   public UserSnowflakeImpl(long id) {
      this.id = id;
   }

   public long getIdLong() {
      return this.id;
   }

   @Nonnull
   public String getAsMention() {
      return "<@" + this.getId() + ">";
   }

   @Nonnull
   public String getDefaultAvatarId() {
      return String.valueOf((this.id >> 22) % 6L);
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof UserSnowflakeImpl)) {
         return false;
      } else {
         return ((UserSnowflakeImpl)obj).getIdLong() == this.id;
      }
   }

   public String toString() {
      return (new EntityString(this)).toString();
   }
}
