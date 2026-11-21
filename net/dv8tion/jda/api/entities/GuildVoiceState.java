package net.dv8tion.jda.api.entities;

import java.time.OffsetDateTime;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.requests.RestAction;

public interface GuildVoiceState extends ISnowflake {
   @Nonnull
   JDA getJDA();

   boolean isSelfMuted();

   boolean isSelfDeafened();

   boolean isMuted();

   boolean isDeafened();

   boolean isGuildMuted();

   boolean isGuildDeafened();

   boolean isSuppressed();

   boolean isStream();

   boolean isSendingVideo();

   @Nullable
   AudioChannelUnion getChannel();

   @Nonnull
   Guild getGuild();

   @Nonnull
   Member getMember();

   boolean inAudioChannel();

   @Nullable
   String getSessionId();

   @Nullable
   OffsetDateTime getRequestToSpeakTimestamp();

   @Nonnull
   @CheckReturnValue
   RestAction<Void> approveSpeaker();

   @Nonnull
   @CheckReturnValue
   RestAction<Void> declineSpeaker();

   @Nonnull
   @CheckReturnValue
   RestAction<Void> inviteSpeaker();
}
