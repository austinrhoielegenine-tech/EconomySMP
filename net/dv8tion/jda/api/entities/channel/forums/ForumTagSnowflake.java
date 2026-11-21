package net.dv8tion.jda.api.entities.channel.forums;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.internal.entities.ForumTagSnowflakeImpl;

public interface ForumTagSnowflake extends ISnowflake {
   @Nonnull
   static ForumTagSnowflake fromId(long id) {
      return new ForumTagSnowflakeImpl(id);
   }

   @Nonnull
   static ForumTagSnowflake fromId(@Nonnull String id) {
      return new ForumTagSnowflakeImpl(MiscUtil.parseSnowflake(id));
   }
}
