package net.dv8tion.jda.internal.utils.config.flags;

import java.util.EnumSet;

public enum ConfigFlag {
   RAW_EVENTS,
   EVENT_PASSTHROUGH,
   USE_RELATIVE_RATELIMIT(true),
   RETRY_TIMEOUT(true),
   BULK_DELETE_SPLIT(true),
   SHUTDOWN_HOOK(true),
   MDC_CONTEXT(true),
   AUTO_RECONNECT(true);

   private final boolean isDefault;

   private ConfigFlag() {
      this(false);
   }

   private ConfigFlag(boolean isDefault) {
      this.isDefault = isDefault;
   }

   public static EnumSet<ConfigFlag> getDefault() {
      EnumSet<ConfigFlag> set = EnumSet.noneOf(ConfigFlag.class);

      for(ConfigFlag flag : values()) {
         if (flag.isDefault) {
            set.add(flag);
         }
      }

      return set;
   }

   // $FF: synthetic method
   private static ConfigFlag[] $values() {
      return new ConfigFlag[]{RAW_EVENTS, EVENT_PASSTHROUGH, USE_RELATIVE_RATELIMIT, RETRY_TIMEOUT, BULK_DELETE_SPLIT, SHUTDOWN_HOOK, MDC_CONTEXT, AUTO_RECONNECT};
   }
}
