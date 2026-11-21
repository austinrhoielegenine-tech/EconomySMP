package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class GuildUpdateDescriptionEvent extends GenericGuildUpdateEvent<String> {
   public static final String IDENTIFIER = "description";

   public GuildUpdateDescriptionEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable String previous) {
      super(api, responseNumber, guild, previous, guild.getDescription(), "description");
   }

   @Nullable
   public String getOldDescription() {
      return (String)this.getOldValue();
   }

   @Nullable
   public String getNewDescription() {
      return (String)this.getNewValue();
   }
}
