package net.dv8tion.jda.api.events.soundboard;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SoundboardSound;

public class SoundboardSoundDeleteEvent extends GenericSoundboardSoundEvent {
   public SoundboardSoundDeleteEvent(@Nonnull JDA api, long responseNumber, @Nonnull SoundboardSound soundboardSound) {
      super(api, responseNumber, soundboardSound);
   }
}
