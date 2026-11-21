package net.dv8tion.jda.api.events.guild.update;

import java.util.Set;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class GuildUpdateFeaturesEvent extends GenericGuildUpdateEvent<Set<String>> {
   public static final String IDENTIFIER = "features";

   public GuildUpdateFeaturesEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull Set<String> oldFeatures) {
      super(api, responseNumber, guild, oldFeatures, guild.getFeatures(), "features");
   }

   @Nonnull
   public Set<String> getOldFeatures() {
      return this.getOldValue();
   }

   @Nonnull
   public Set<String> getNewFeatures() {
      return this.getNewValue();
   }

   @Nonnull
   public Set<String> getOldValue() {
      return (Set)super.getOldValue();
   }

   @Nonnull
   public Set<String> getNewValue() {
      return (Set)super.getNewValue();
   }
}
