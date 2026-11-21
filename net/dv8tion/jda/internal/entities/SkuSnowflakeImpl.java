package net.dv8tion.jda.internal.entities;

import net.dv8tion.jda.api.entities.SkuSnowflake;
import net.dv8tion.jda.internal.utils.EntityString;

public class SkuSnowflakeImpl implements SkuSnowflake {
   protected final long id;

   public SkuSnowflakeImpl(long id) {
      this.id = id;
   }

   public long getIdLong() {
      return this.id;
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof SkuSnowflakeImpl)) {
         return false;
      } else {
         return ((SkuSnowflakeImpl)obj).getIdLong() == this.id;
      }
   }

   public String toString() {
      return (new EntityString(this)).toString();
   }
}
