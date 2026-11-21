package net.dv8tion.jda.api.events.guild.voice;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;

public class GuildVoiceGuildDeafenEvent extends GenericGuildVoiceEvent {
   protected final boolean guildDeafened;

   public GuildVoiceGuildDeafenEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, boolean guildDeafened) {
      super(api, responseNumber, member);
      this.guildDeafened = guildDeafened;
   }

   public boolean isGuildDeafened() {
      return this.guildDeafened;
   }
}
