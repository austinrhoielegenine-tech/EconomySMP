package net.dv8tion.jda.api.events.soundboard.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SoundboardSound;

public class SoundboardSoundUpdateVolumeEvent extends GenericSoundboardSoundUpdateEvent<Double> {
   public static final String IDENTIFIER = "volume";

   public SoundboardSoundUpdateVolumeEvent(@Nonnull JDA api, long responseNumber, @Nonnull SoundboardSound soundboardSound, double oldVolume) {
      super(api, responseNumber, soundboardSound, oldVolume, soundboardSound.getVolume(), "volume");
   }

   public double getOldVolume() {
      return this.getOldValue();
   }

   public double getNewVolume() {
      return this.getNewValue();
   }

   @Nonnull
   public Double getOldValue() {
      return (Double)super.getOldValue();
   }

   @Nonnull
   public Double getNewValue() {
      return (Double)super.getNewValue();
   }
}
