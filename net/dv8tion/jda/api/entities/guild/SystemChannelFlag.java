package net.dv8tion.jda.api.entities.guild;

import java.util.Collection;
import java.util.EnumSet;
import javax.annotation.Nonnull;

public enum SystemChannelFlag {
   SUPPRESS_JOIN_NOTIFICATIONS(0),
   SUPPRESS_PREMIUM_SUBSCRIPTIONS(1),
   SUPPRESS_GUILD_REMINDER_NOTIFICATIONS(2),
   SUPPRESS_JOIN_NOTIFICATION_REPLIES(3),
   SUPPRESS_ROLE_SUBSCRIPTION_PURCHASE_NOTIFICATIONS(4),
   SUPPRESS_ROLE_SUBSCRIPTION_PURCHASE_NOTIFICATION_REPLIES(5);

   private final int rawValue;

   private SystemChannelFlag(int offset) {
      this.rawValue = 1 << offset;
   }

   @Nonnull
   public static EnumSet<SystemChannelFlag> getFlags(int raw) {
      EnumSet<SystemChannelFlag> enumSet = EnumSet.noneOf(SystemChannelFlag.class);

      for(SystemChannelFlag flag : values()) {
         if ((flag.rawValue & raw) != 0) {
            enumSet.add(flag);
         }
      }

      return enumSet;
   }

   public static int getRaw(@Nonnull Collection<SystemChannelFlag> flags) {
      int raw = 0;

      for(SystemChannelFlag flag : flags) {
         raw |= flag.rawValue;
      }

      return raw;
   }

   // $FF: synthetic method
   private static SystemChannelFlag[] $values() {
      return new SystemChannelFlag[]{SUPPRESS_JOIN_NOTIFICATIONS, SUPPRESS_PREMIUM_SUBSCRIPTIONS, SUPPRESS_GUILD_REMINDER_NOTIFICATIONS, SUPPRESS_JOIN_NOTIFICATION_REPLIES, SUPPRESS_ROLE_SUBSCRIPTION_PURCHASE_NOTIFICATIONS, SUPPRESS_ROLE_SUBSCRIPTION_PURCHASE_NOTIFICATION_REPLIES};
   }
}
