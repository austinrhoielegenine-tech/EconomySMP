package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class GuildUpdateMFALevelEvent extends GenericGuildUpdateEvent<Guild.MFALevel> {
   public static final String IDENTIFIER = "mfa_level";

   public GuildUpdateMFALevelEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull Guild.MFALevel oldMFALevel) {
      super(api, responseNumber, guild, oldMFALevel, guild.getRequiredMFALevel(), "mfa_level");
   }

   @Nonnull
   public Guild.MFALevel getOldMFALevel() {
      return this.getOldValue();
   }

   @Nonnull
   public Guild.MFALevel getNewMFALevel() {
      return this.getNewValue();
   }

   @Nonnull
   public Guild.MFALevel getOldValue() {
      return (Guild.MFALevel)super.getOldValue();
   }

   @Nonnull
   public Guild.MFALevel getNewValue() {
      return (Guild.MFALevel)super.getNewValue();
   }
}
