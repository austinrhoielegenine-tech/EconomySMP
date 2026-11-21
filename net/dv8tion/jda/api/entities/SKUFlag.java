package net.dv8tion.jda.api.entities;

import java.util.EnumSet;
import javax.annotation.Nonnull;

public enum SKUFlag {
   AVAILABLE(2),
   GUILD_SUBSCRIPTION(7),
   USER_SUBSCRIPTION(8);

   private final int offset;
   private final int raw;

   private SKUFlag(int offset) {
      this.offset = offset;
      this.raw = 1 << offset;
   }

   public int getRaw() {
      return this.raw;
   }

   public int getOffset() {
      return this.offset;
   }

   @Nonnull
   public static EnumSet<SKUFlag> getFlags(int flags) {
      if (flags == 0) {
         return EnumSet.noneOf(SKUFlag.class);
      } else {
         EnumSet<SKUFlag> flagSet = EnumSet.noneOf(SKUFlag.class);

         for(SKUFlag flag : values()) {
            if ((flags & flag.raw) == flag.raw) {
               flagSet.add(flag);
            }
         }

         return flagSet;
      }
   }

   // $FF: synthetic method
   private static SKUFlag[] $values() {
      return new SKUFlag[]{AVAILABLE, GUILD_SUBSCRIPTION, USER_SUBSCRIPTION};
   }
}
