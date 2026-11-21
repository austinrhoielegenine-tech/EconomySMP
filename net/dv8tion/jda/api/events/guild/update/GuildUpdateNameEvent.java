package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class GuildUpdateNameEvent extends GenericGuildUpdateEvent<String> {
   public static final String IDENTIFIER = "name";

   public GuildUpdateNameEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull String oldName) {
      super(api, responseNumber, guild, oldName, guild.getName(), "name");
   }

   @Nonnull
   public String getOldName() {
      return this.getOldValue();
   }

   @Nonnull
   public String getNewName() {
      return this.getNewValue();
   }

   @Nonnull
   public String getOldValue() {
      return (String)super.getOldValue();
   }

   @Nonnull
   public String getNewValue() {
      return (String)super.getNewValue();
   }
}
