package net.dv8tion.jda.api.entities.channel.attribute;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.SoundboardSoundSnowflake;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.requests.RestAction;

public interface ISoundboardSoundChannel extends AudioChannel {
   @Nonnull
   @CheckReturnValue
   RestAction<Void> sendSoundboardSound(@Nonnull SoundboardSoundSnowflake var1, @Nullable String var2);

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> sendSoundboardSound(@Nonnull SoundboardSoundSnowflake sound, long sourceGuildId) {
      return this.sendSoundboardSound(sound, sourceGuildId == 0L ? null : Long.toUnsignedString(sourceGuildId));
   }
}
