package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class GuildUpdateNSFWLevelEvent extends GenericGuildUpdateEvent<Guild.NSFWLevel> {
   public static final String IDENTIFIER = "nsfw_level";

   public GuildUpdateNSFWLevelEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull Guild.NSFWLevel oldNSFWLevel) {
      super(api, responseNumber, guild, oldNSFWLevel, guild.getNSFWLevel(), "nsfw_level");
   }

   @Nonnull
   public Guild.NSFWLevel getOldNSFWLevel() {
      return this.getOldValue();
   }

   @Nonnull
   public Guild.NSFWLevel getNewNSFWLevel() {
      return this.getNewValue();
   }

   @Nonnull
   public Guild.NSFWLevel getOldValue() {
      return (Guild.NSFWLevel)super.getOldValue();
   }

   @Nonnull
   public Guild.NSFWLevel getNewValue() {
      return (Guild.NSFWLevel)super.getNewValue();
   }
}
