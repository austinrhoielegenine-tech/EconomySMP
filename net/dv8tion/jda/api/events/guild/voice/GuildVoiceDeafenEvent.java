package net.dv8tion.jda.api.events.guild.voice;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;

public class GuildVoiceDeafenEvent extends GenericGuildVoiceEvent {
   protected final boolean deafened;

   public GuildVoiceDeafenEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, boolean deafened) {
      super(api, responseNumber, member);
      this.deafened = deafened;
   }

   public boolean isDeafened() {
      return this.deafened;
   }
}
