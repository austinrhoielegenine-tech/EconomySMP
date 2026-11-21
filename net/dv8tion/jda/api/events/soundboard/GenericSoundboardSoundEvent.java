package net.dv8tion.jda.api.events.soundboard;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SoundboardSound;
import net.dv8tion.jda.api.events.Event;

public abstract class GenericSoundboardSoundEvent extends Event {
   private final SoundboardSound soundboardSound;

   public GenericSoundboardSoundEvent(@Nonnull JDA api, long responseNumber, @Nonnull SoundboardSound soundboardSound) {
      super(api, responseNumber);
      this.soundboardSound = soundboardSound;
   }

   @Nonnull
   public Guild getGuild() {
      return this.soundboardSound.getGuild();
   }

   @Nonnull
   public SoundboardSound getSoundboardSound() {
      return this.soundboardSound;
   }
}
