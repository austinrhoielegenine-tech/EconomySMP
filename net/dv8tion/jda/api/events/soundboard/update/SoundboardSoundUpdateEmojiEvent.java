package net.dv8tion.jda.api.events.soundboard.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SoundboardSound;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;

public class SoundboardSoundUpdateEmojiEvent extends GenericSoundboardSoundUpdateEvent<EmojiUnion> {
   public static final String IDENTIFIER = "emoji";

   public SoundboardSoundUpdateEmojiEvent(@Nonnull JDA api, long responseNumber, @Nonnull SoundboardSound soundboardSound, EmojiUnion oldEmoji) {
      super(api, responseNumber, soundboardSound, oldEmoji, soundboardSound.getEmoji(), "emoji");
   }

   @Nullable
   public EmojiUnion getOldEmoji() {
      return this.getOldValue();
   }

   @Nullable
   public EmojiUnion getNewEmoji() {
      return this.getNewValue();
   }

   @Nullable
   public EmojiUnion getOldValue() {
      return (EmojiUnion)super.getOldValue();
   }

   @Nullable
   public EmojiUnion getNewValue() {
      return (EmojiUnion)super.getNewValue();
   }
}
