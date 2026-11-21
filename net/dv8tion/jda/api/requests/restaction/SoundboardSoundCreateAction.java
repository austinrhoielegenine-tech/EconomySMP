package net.dv8tion.jda.api.requests.restaction;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.SoundboardSound;
import net.dv8tion.jda.api.entities.emoji.Emoji;

public interface SoundboardSoundCreateAction extends AuditableRestAction<SoundboardSound> {
   @Nonnull
   @CheckReturnValue
   SoundboardSoundCreateAction timeout(long var1, @Nonnull TimeUnit var3);

   @Nonnull
   @CheckReturnValue
   SoundboardSoundCreateAction deadline(long var1);

   @Nonnull
   @CheckReturnValue
   SoundboardSoundCreateAction setCheck(@Nullable BooleanSupplier var1);

   @Nonnull
   @CheckReturnValue
   SoundboardSoundCreateAction addCheck(@Nonnull BooleanSupplier var1);

   @Nonnull
   @CheckReturnValue
   SoundboardSoundCreateAction setVolume(double var1);

   @Nonnull
   @CheckReturnValue
   SoundboardSoundCreateAction setEmoji(@Nullable Emoji var1);
}
