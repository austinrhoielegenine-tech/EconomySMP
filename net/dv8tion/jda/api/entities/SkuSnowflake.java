package net.dv8tion.jda.api.entities;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.internal.entities.SkuSnowflakeImpl;

public interface SkuSnowflake extends ISnowflake {
   @Nonnull
   static SkuSnowflake fromId(long id) {
      return new SkuSnowflakeImpl(id);
   }

   @Nonnull
   static SkuSnowflake fromId(@Nonnull String id) {
      return fromId(MiscUtil.parseSnowflake(id));
   }
}
