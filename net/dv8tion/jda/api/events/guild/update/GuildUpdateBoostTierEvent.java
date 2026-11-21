package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class GuildUpdateBoostTierEvent extends GenericGuildUpdateEvent<Guild.BoostTier> {
   public static final String IDENTIFIER = "boost_tier";

   public GuildUpdateBoostTierEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull Guild.BoostTier previous) {
      super(api, responseNumber, guild, previous, guild.getBoostTier(), "boost_tier");
   }

   @Nonnull
   public Guild.BoostTier getOldBoostTier() {
      return this.getOldValue();
   }

   @Nonnull
   public Guild.BoostTier getNewBoostTier() {
      return this.getNewValue();
   }

   @Nonnull
   public Guild.BoostTier getOldValue() {
      return (Guild.BoostTier)super.getOldValue();
   }

   @Nonnull
   public Guild.BoostTier getNewValue() {
      return (Guild.BoostTier)super.getNewValue();
   }
}
