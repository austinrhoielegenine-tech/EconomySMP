package net.dv8tion.jda.api.events.guild.voice;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;

public class GuildVoiceVideoEvent extends GenericGuildVoiceEvent {
   private final boolean video;

   public GuildVoiceVideoEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, boolean video) {
      super(api, responseNumber, member);
      this.video = video;
   }

   public boolean isSendingVideo() {
      return this.video;
   }
}
