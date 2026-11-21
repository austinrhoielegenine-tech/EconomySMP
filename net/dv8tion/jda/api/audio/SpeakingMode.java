package net.dv8tion.jda.api.audio;

import java.util.Collection;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum SpeakingMode {
   VOICE(1),
   SOUNDSHARE(2),
   PRIORITY(4);

   private final int raw;

   private SpeakingMode(int raw) {
      this.raw = raw;
   }

   public int getRaw() {
      return this.raw;
   }

   @Nonnull
   public static EnumSet<SpeakingMode> getModes(int mask) {
      EnumSet<SpeakingMode> modes = EnumSet.noneOf(SpeakingMode.class);
      if (mask == 0) {
         return modes;
      } else {
         SpeakingMode[] values = values();

         for(SpeakingMode mode : values) {
            if ((mode.raw & mask) == mode.raw) {
               modes.add(mode);
            }
         }

         return modes;
      }
   }

   public static int getRaw(@Nullable SpeakingMode... modes) {
      if (modes != null && modes.length != 0) {
         int mask = 0;

         for(SpeakingMode m : modes) {
            mask |= m.raw;
         }

         return mask;
      } else {
         return 0;
      }
   }

   public static int getRaw(@Nullable Collection<SpeakingMode> modes) {
      if (modes == null) {
         return 0;
      } else {
         int raw = 0;

         for(SpeakingMode mode : modes) {
            raw |= mode.getRaw();
         }

         return raw;
      }
   }

   // $FF: synthetic method
   private static SpeakingMode[] $values() {
      return new SpeakingMode[]{VOICE, SOUNDSHARE, PRIORITY};
   }
}
