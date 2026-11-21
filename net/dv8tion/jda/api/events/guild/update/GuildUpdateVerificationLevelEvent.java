package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class GuildUpdateVerificationLevelEvent extends GenericGuildUpdateEvent<Guild.VerificationLevel> {
   public static final String IDENTIFIER = "verification_level";

   public GuildUpdateVerificationLevelEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull Guild.VerificationLevel oldVerificationLevel) {
      super(api, responseNumber, guild, oldVerificationLevel, guild.getVerificationLevel(), "verification_level");
   }

   @Nonnull
   public Guild.VerificationLevel getOldVerificationLevel() {
      return this.getOldValue();
   }

   @Nonnull
   public Guild.VerificationLevel getNewVerificationLevel() {
      return this.getNewValue();
   }

   @Nonnull
   public Guild.VerificationLevel getOldValue() {
      return (Guild.VerificationLevel)super.getOldValue();
   }

   @Nonnull
   public Guild.VerificationLevel getNewValue() {
      return (Guild.VerificationLevel)super.getNewValue();
   }
}
