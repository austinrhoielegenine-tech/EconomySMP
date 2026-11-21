package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class GuildUpdateExplicitContentLevelEvent extends GenericGuildUpdateEvent<Guild.ExplicitContentLevel> {
   public static final String IDENTIFIER = "explicit_content_filter";

   public GuildUpdateExplicitContentLevelEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull Guild.ExplicitContentLevel oldLevel) {
      super(api, responseNumber, guild, oldLevel, guild.getExplicitContentLevel(), "explicit_content_filter");
   }

   @Nonnull
   public Guild.ExplicitContentLevel getOldLevel() {
      return this.getOldValue();
   }

   @Nonnull
   public Guild.ExplicitContentLevel getNewLevel() {
      return this.getNewValue();
   }

   @Nonnull
   public Guild.ExplicitContentLevel getOldValue() {
      return (Guild.ExplicitContentLevel)super.getOldValue();
   }

   @Nonnull
   public Guild.ExplicitContentLevel getNewValue() {
      return (Guild.ExplicitContentLevel)super.getNewValue();
   }
}
