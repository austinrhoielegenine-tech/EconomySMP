package net.dv8tion.jda.internal.entities;

import net.dv8tion.jda.api.entities.SoundboardSoundSnowflake;
import net.dv8tion.jda.internal.utils.EntityString;

public class SoundboardSoundSnowflakeImpl implements SoundboardSoundSnowflake {
   private final long id;

   public SoundboardSoundSnowflakeImpl(long id) {
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
      } else if (!(obj instanceof SoundboardSoundSnowflakeImpl)) {
         return false;
      } else {
         return ((SoundboardSoundSnowflakeImpl)obj).getIdLong() == this.id;
      }
   }

   public String toString() {
      return (new EntityString(this)).toString();
   }
}
