package net.dv8tion.jda.api.events.soundboard.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SoundboardSound;

public class SoundboardSoundUpdateNameEvent extends GenericSoundboardSoundUpdateEvent<String> {
   public static final String IDENTIFIER = "name";

   public SoundboardSoundUpdateNameEvent(@Nonnull JDA api, long responseNumber, @Nonnull SoundboardSound soundboardSound, @Nonnull String oldName) {
      super(api, responseNumber, soundboardSound, oldName, soundboardSound.getName(), "name");
   }

   @Nonnull
   public String getOldName() {
      return this.getOldValue();
   }

   @Nonnull
   public String getNewName() {
      return this.getNewValue();
   }

   @Nonnull
   public String getOldValue() {
      return (String)super.getOldValue();
   }

   @Nonnull
   public String getNewValue() {
      return (String)super.getNewValue();
   }
}
