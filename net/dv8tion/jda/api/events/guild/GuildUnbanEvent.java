package net.dv8tion.jda.api.events.guild;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class GuildUnbanEvent extends GenericGuildEvent {
   private final User user;

   public GuildUnbanEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull User user) {
      super(api, responseNumber, guild);
      this.user = user;
   }

   @Nonnull
   public User getUser() {
      return this.user;
   }
}
