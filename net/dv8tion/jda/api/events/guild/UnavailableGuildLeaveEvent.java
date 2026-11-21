package net.dv8tion.jda.api.events.guild;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.Event;

public class UnavailableGuildLeaveEvent extends Event {
   private final long guildId;

   public UnavailableGuildLeaveEvent(@Nonnull JDA api, long responseNumber, long guildId) {
      super(api, responseNumber);
      this.guildId = guildId;
   }

   @Nonnull
   public String getGuildId() {
      return Long.toUnsignedString(this.guildId);
   }

   public long getGuildIdLong() {
      return this.guildId;
   }
}
