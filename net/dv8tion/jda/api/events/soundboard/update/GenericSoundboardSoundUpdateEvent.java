package net.dv8tion.jda.api.events.soundboard.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SoundboardSound;
import net.dv8tion.jda.api.events.UpdateEvent;
import net.dv8tion.jda.api.events.soundboard.GenericSoundboardSoundEvent;

public abstract class GenericSoundboardSoundUpdateEvent<T> extends GenericSoundboardSoundEvent implements UpdateEvent<SoundboardSound, T> {
   protected final T previous;
   protected final T next;
   protected final String identifier;

   public GenericSoundboardSoundUpdateEvent(@Nonnull JDA api, long responseNumber, @Nonnull SoundboardSound soundboardSound, @Nullable T previous, @Nullable T next, @Nonnull String identifier) {
      super(api, responseNumber, soundboardSound);
      this.previous = previous;
      this.next = next;
      this.identifier = identifier;
   }

   @Nonnull
   public SoundboardSound getEntity() {
      return this.getSoundboardSound();
   }

   @Nonnull
   public String getPropertyIdentifier() {
      return this.identifier;
   }

   @Nullable
   public T getOldValue() {
      return this.previous;
   }

   @Nullable
   public T getNewValue() {
      return this.next;
   }
}
