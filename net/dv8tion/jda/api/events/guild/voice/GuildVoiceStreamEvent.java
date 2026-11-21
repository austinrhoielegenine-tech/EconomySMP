package net.dv8tion.jda.api.events.guild.voice;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;

public class GuildVoiceStreamEvent extends GenericGuildVoiceEvent {
   private final boolean stream;

   public GuildVoiceStreamEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, boolean stream) {
      super(api, responseNumber, member);
      this.stream = stream;
   }

   public boolean isStream() {
      return this.stream;
   }
}
