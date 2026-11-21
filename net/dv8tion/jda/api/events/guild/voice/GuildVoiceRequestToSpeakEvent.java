package net.dv8tion.jda.api.events.guild.voice;

import java.time.OffsetDateTime;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.RestAction;

public class GuildVoiceRequestToSpeakEvent extends GenericGuildVoiceEvent {
   private final OffsetDateTime oldTime;
   private final OffsetDateTime newTime;

   public GuildVoiceRequestToSpeakEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nullable OffsetDateTime oldTime, @Nullable OffsetDateTime newTime) {
      super(api, responseNumber, member);
      this.oldTime = oldTime;
      this.newTime = newTime;
   }

   @Nullable
   public OffsetDateTime getOldTime() {
      return this.oldTime;
   }

   @Nullable
   public OffsetDateTime getNewTime() {
      return this.newTime;
   }

   @Nonnull
   @CheckReturnValue
   public RestAction<Void> approveSpeaker() {
      return this.getVoiceState().approveSpeaker();
   }

   @Nonnull
   @CheckReturnValue
   public RestAction<Void> declineSpeaker() {
      return this.getVoiceState().declineSpeaker();
   }
}
