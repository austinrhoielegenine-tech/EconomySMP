package net.dv8tion.jda.api.events.guild;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class GuildAvailableEvent extends GenericGuildEvent {
   public GuildAvailableEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild) {
      super(api, responseNumber, guild);
   }
}
