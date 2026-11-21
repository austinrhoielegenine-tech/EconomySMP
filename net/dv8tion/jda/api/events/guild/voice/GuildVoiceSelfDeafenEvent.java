package net.dv8tion.jda.api.events.guild.voice;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;

public class GuildVoiceSelfDeafenEvent extends GenericGuildVoiceEvent {
   protected final boolean selfDeafened;

   public GuildVoiceSelfDeafenEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, boolean isSelfDeafened) {
      super(api, responseNumber, member);
      this.selfDeafened = isSelfDeafened;
   }

   public boolean isSelfDeafened() {
      return this.selfDeafened;
   }
}
