package net.dv8tion.jda.api.events.guild.voice;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;

public class GuildVoiceGuildMuteEvent extends GenericGuildVoiceEvent {
   protected final boolean guildMuted;

   public GuildVoiceGuildMuteEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, boolean guildMuted) {
      super(api, responseNumber, member);
      this.guildMuted = guildMuted;
   }

   public boolean isGuildMuted() {
      return this.guildMuted;
   }
}
