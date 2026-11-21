package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class GuildUpdateAfkTimeoutEvent extends GenericGuildUpdateEvent<Guild.Timeout> {
   public static final String IDENTIFIER = "afk_timeout";

   public GuildUpdateAfkTimeoutEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull Guild.Timeout oldAfkTimeout) {
      super(api, responseNumber, guild, oldAfkTimeout, guild.getAfkTimeout(), "afk_timeout");
   }

   @Nonnull
   public Guild.Timeout getOldAfkTimeout() {
      return this.getOldValue();
   }

   @Nonnull
   public Guild.Timeout getNewAfkTimeout() {
      return this.getNewValue();
   }

   @Nonnull
   public Guild.Timeout getOldValue() {
      return (Guild.Timeout)super.getOldValue();
   }

   @Nonnull
   public Guild.Timeout getNewValue() {
      return (Guild.Timeout)super.getNewValue();
   }
}
