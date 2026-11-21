package net.dv8tion.jda.api.managers;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.emoji.Emoji;

public interface SoundboardSoundManager extends Manager<SoundboardSoundManager> {
   long NAME = 1L;
   long VOLUME = 2L;
   long EMOJI = 4L;

   @Nonnull
   @CheckReturnValue
   SoundboardSoundManager reset(long var1);

   @Nonnull
   @CheckReturnValue
   SoundboardSoundManager reset(@Nonnull long... var1);

   @Nonnull
   Guild getGuild();

   @Nonnull
   @CheckReturnValue
   SoundboardSoundManager setName(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   SoundboardSoundManager setVolume(double var1);

   @Nonnull
   @CheckReturnValue
   SoundboardSoundManager setEmoji(@Nullable Emoji var1);
}
