package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class GuildUpdateNotificationLevelEvent extends GenericGuildUpdateEvent<Guild.NotificationLevel> {
   public static final String IDENTIFIER = "notification_level";

   public GuildUpdateNotificationLevelEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull Guild.NotificationLevel oldNotificationLevel) {
      super(api, responseNumber, guild, oldNotificationLevel, guild.getDefaultNotificationLevel(), "notification_level");
   }

   @Nonnull
   public Guild.NotificationLevel getOldNotificationLevel() {
      return this.getOldValue();
   }

   @Nonnull
   public Guild.NotificationLevel getNewNotificationLevel() {
      return this.getNewValue();
   }

   @Nonnull
   public Guild.NotificationLevel getOldValue() {
      return (Guild.NotificationLevel)super.getOldValue();
   }

   @Nonnull
   public Guild.NotificationLevel getNewValue() {
      return (Guild.NotificationLevel)super.getNewValue();
   }
}
