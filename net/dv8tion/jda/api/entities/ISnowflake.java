package net.dv8tion.jda.api.entities;

import java.time.OffsetDateTime;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.utils.TimeUtil;

public interface ISnowflake {
   @Nonnull
   default String getId() {
      return Long.toUnsignedString(this.getIdLong());
   }

   long getIdLong();

   @Nonnull
   default OffsetDateTime getTimeCreated() {
      return TimeUtil.getTimeCreated(this.getIdLong());
   }
}
