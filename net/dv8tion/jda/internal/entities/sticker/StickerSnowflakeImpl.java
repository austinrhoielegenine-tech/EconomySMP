package net.dv8tion.jda.internal.entities.sticker;

import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.internal.utils.EntityString;

public class StickerSnowflakeImpl implements StickerSnowflake {
   private final long id;

   public StickerSnowflakeImpl(long id) {
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
      } else if (!(obj instanceof StickerSnowflakeImpl)) {
         return false;
      } else {
         return ((StickerSnowflakeImpl)obj).id == this.id;
      }
   }

   public String toString() {
      return (new EntityString(this)).toString();
   }
}
