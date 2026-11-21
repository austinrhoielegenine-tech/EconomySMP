package net.dv8tion.jda.api.entities;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.utils.ImageFormat;
import net.dv8tion.jda.api.utils.ImageProxy;

public interface Widget extends ISnowflake {
   boolean isAvailable();

   @Nonnull
   String getName();

   @Nullable
   String getInviteCode();

   @Nonnull
   List<VoiceChannel> getVoiceChannels();

   @Nullable
   VoiceChannel getVoiceChannelById(@Nonnull String var1);

   @Nullable
   VoiceChannel getVoiceChannelById(long var1);

   @Nonnull
   List<Member> getMembers();

   @Nullable
   Member getMemberById(@Nonnull String var1);

   @Nullable
   Member getMemberById(long var1);

   public interface Member extends IMentionable {
      boolean isBot();

      @Nonnull
      String getName();

      @Nonnull
      String getDiscriminator();

      @Nullable
      String getAvatarId();

      @Nullable
      String getAvatarUrl();

      @Nullable
      String getAvatarUrl(@Nonnull ImageFormat var1);

      @Nullable
      ImageProxy getAvatar();

      @Nullable
      ImageProxy getAvatar(@Nonnull ImageFormat var1);

      @Nonnull
      String getDefaultAvatarId();

      @Nonnull
      String getDefaultAvatarUrl();

      @Nonnull
      ImageProxy getDefaultAvatar();

      @Nonnull
      String getEffectiveAvatarUrl();

      @Nonnull
      String getEffectiveAvatarUrl(@Nonnull ImageFormat var1);

      @Nonnull
      ImageProxy getEffectiveAvatar();

      @Nonnull
      ImageProxy getEffectiveAvatar(@Nonnull ImageFormat var1);

      @Nullable
      String getNickname();

      @Nonnull
      String getEffectiveName();

      @Nonnull
      OnlineStatus getOnlineStatus();

      @Nullable
      Activity getActivity();

      @Nonnull
      VoiceState getVoiceState();

      @Nonnull
      Widget getWidget();
   }

   public interface VoiceChannel extends ISnowflake {
      int getPosition();

      @Nonnull
      String getName();

      @Nonnull
      List<Member> getMembers();

      @Nonnull
      Widget getWidget();
   }

   public interface VoiceState {
      @Nullable
      VoiceChannel getChannel();

      boolean inVoiceChannel();

      boolean isGuildMuted();

      boolean isGuildDeafened();

      boolean isSuppressed();

      boolean isSelfMuted();

      boolean isSelfDeafened();

      boolean isMuted();

      boolean isDeafened();

      @Nonnull
      Member getMember();

      @Nonnull
      Widget getWidget();
   }
}
