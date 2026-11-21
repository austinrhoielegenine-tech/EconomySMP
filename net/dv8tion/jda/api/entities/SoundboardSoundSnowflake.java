package net.dv8tion.jda.api.entities;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.internal.entities.SoundboardSoundSnowflakeImpl;

public interface SoundboardSoundSnowflake extends ISnowflake {
   @Nonnull
   static SoundboardSoundSnowflake fromId(long id) {
      return new SoundboardSoundSnowflakeImpl(id);
   }

   @Nonnull
   static SoundboardSoundSnowflake fromId(@Nonnull String id) {
      return fromId(MiscUtil.parseSnowflake(id));
   }

   @Nonnull
   default String getUrl() {
      return String.format("https://cdn.discordapp.com/soundboard-sounds/%s", this.getId());
   }
}
