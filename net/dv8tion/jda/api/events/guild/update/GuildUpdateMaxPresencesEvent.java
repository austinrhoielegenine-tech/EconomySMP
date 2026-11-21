package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class GuildUpdateMaxPresencesEvent extends GenericGuildUpdateEvent<Integer> {
   public static final String IDENTIFIER = "max_presences";

   public GuildUpdateMaxPresencesEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, int previous) {
      super(api, responseNumber, guild, previous, guild.getMaxPresences(), "max_presences");
   }

   public int getOldMaxPresences() {
      return this.getOldValue();
   }

   public int getNewMaxPresences() {
      return this.getNewValue();
   }

   @Nonnull
   public Integer getOldValue() {
      return (Integer)super.getOldValue();
   }

   @Nonnull
   public Integer getNewValue() {
      return (Integer)super.getNewValue();
   }
}
