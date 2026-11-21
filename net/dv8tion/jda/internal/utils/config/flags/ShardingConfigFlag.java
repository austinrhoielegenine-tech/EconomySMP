package net.dv8tion.jda.internal.utils.config.flags;

import java.util.EnumSet;

public enum ShardingConfigFlag {
   SHUTDOWN_NOW;

   public static EnumSet<ShardingConfigFlag> getDefault() {
      return EnumSet.noneOf(ShardingConfigFlag.class);
   }

   // $FF: synthetic method
   private static ShardingConfigFlag[] $values() {
      return new ShardingConfigFlag[]{SHUTDOWN_NOW};
   }
}
