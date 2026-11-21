package net.dv8tion.jda.api.events.guild.voice;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;

public class GuildVoiceSelfMuteEvent extends GenericGuildVoiceEvent {
   protected final boolean selfMuted;

   public GuildVoiceSelfMuteEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, boolean isSelfMuted) {
      super(api, responseNumber, member);
      this.selfMuted = isSelfMuted;
   }

   public boolean isSelfMuted() {
      return this.selfMuted;
   }
}
