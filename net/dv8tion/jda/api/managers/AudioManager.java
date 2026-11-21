package net.dv8tion.jda.api.managers;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.annotations.Incubating;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.audio.hooks.ConnectionListener;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

public interface AudioManager {
   long DEFAULT_CONNECTION_TIMEOUT = 10000L;
   Logger LOG = JDALogger.getLog(AudioManager.class);

   void openAudioConnection(@Nonnull AudioChannel var1);

   void closeAudioConnection();

   @Incubating
   void setSpeakingMode(@Nonnull Collection<SpeakingMode> var1);

   @Incubating
   default void setSpeakingMode(@Nonnull SpeakingMode... mode) {
      Checks.notNull(mode, "Speaking Mode");
      this.setSpeakingMode(Arrays.asList(mode));
   }

   @Nonnull
   @Incubating
   EnumSet<SpeakingMode> getSpeakingMode();

   @Nonnull
   JDA getJDA();

   @Nonnull
   Guild getGuild();

   @Nullable
   AudioChannelUnion getConnectedChannel();

   boolean isConnected();

   void setConnectTimeout(long var1);

   long getConnectTimeout();

   void setSendingHandler(@Nullable AudioSendHandler var1);

   @Nullable
   AudioSendHandler getSendingHandler();

   void setReceivingHandler(@Nullable AudioReceiveHandler var1);

   @Nullable
   AudioReceiveHandler getReceivingHandler();

   void setConnectionListener(@Nullable ConnectionListener var1);

   @Nullable
   ConnectionListener getConnectionListener();

   @Nonnull
   ConnectionStatus getConnectionStatus();

   void setAutoReconnect(boolean var1);

   boolean isAutoReconnect();

   void setSelfMuted(boolean var1);

   boolean isSelfMuted();

   void setSelfDeafened(boolean var1);

   boolean isSelfDeafened();
}
