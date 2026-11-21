package net.dv8tion.jda.api.events.guild;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;

public abstract class GenericGuildEvent extends Event {
   protected final Guild guild;

   public GenericGuildEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild) {
      super(api, responseNumber);
      this.guild = guild;
   }

   @Nonnull
   public Guild getGuild() {
      return this.guild;
   }
}
