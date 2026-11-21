package net.dv8tion.jda.api.entities;

import java.util.EnumSet;
import javax.annotation.Nonnull;

public enum ActivityFlag {
   INSTANCE(0),
   JOIN(1),
   SPECTATE(2),
   JOIN_REQUEST(3),
   SYNC(4),
   PLAY(5);

   private final int offset;
   private final int raw;

   private ActivityFlag(int offset) {
      this.offset = offset;
      this.raw = 1 << offset;
   }

   public int getOffset() {
      return this.offset;
   }

   public int getRaw() {
      return this.raw;
   }

   @Nonnull
   public static EnumSet<ActivityFlag> getFlags(int raw) {
      EnumSet<ActivityFlag> set = EnumSet.noneOf(ActivityFlag.class);
      if (raw == 0) {
         return set;
      } else {
         for(ActivityFlag flag : values()) {
            if ((flag.getRaw() & raw) == flag.getRaw()) {
               set.add(flag);
            }
         }

         return set;
      }
   }

   // $FF: synthetic method
   private static ActivityFlag[] $values() {
      return new ActivityFlag[]{INSTANCE, JOIN, SPECTATE, JOIN_REQUEST, SYNC, PLAY};
   }
}
