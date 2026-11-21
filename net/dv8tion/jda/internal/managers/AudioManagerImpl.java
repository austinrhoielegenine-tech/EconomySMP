package net.dv8tion.jda.internal.managers;

import java.util.Collection;
import java.util.EnumSet;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.audio.hooks.ConnectionListener;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.audio.hooks.ListenerProxy;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.audio.AudioConnection;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.PermissionUtil;

public class AudioManagerImpl implements AudioManager {
   public final ReentrantLock CONNECTION_LOCK = new ReentrantLock();
   protected final ListenerProxy connectionListener = new ListenerProxy();
   protected final GuildImpl guild;
   protected AudioConnection audioConnection = null;
   protected EnumSet<SpeakingMode> speakingModes;
   protected AudioSendHandler sendHandler;
   protected AudioReceiveHandler receiveHandler;
   protected long queueTimeout;
   protected boolean shouldReconnect;
   protected boolean selfMuted;
   protected boolean selfDeafened;
   protected long timeout;

   public AudioManagerImpl(GuildImpl guild) {
      this.speakingModes = EnumSet.of(SpeakingMode.VOICE);
      this.queueTimeout = 100L;
      this.shouldReconnect = true;
      this.selfMuted = false;
      this.selfDeafened = false;
      this.timeout = 10000L;
      this.guild = guild;
   }

   public AudioConnection getAudioConnection() {
      return this.audioConnection;
   }

   public void openAudioConnection(@Nonnull AudioChannel channel) {
      Checks.notNull(channel, "Provided AudioChannel");
      if (!this.getGuild().equals(channel.getGuild())) {
         throw new IllegalArgumentException("The provided AudioChannel is not a part of the Guild that this AudioManager handles.Please provide a AudioChannel from the proper Guild");
      } else {
         Member self = this.getGuild().getSelfMember();
         if (this.audioConnection == null || !channel.equals(this.audioConnection.getChannel())) {
            this.checkChannel(channel, self);
            this.getJDA().getDirectAudioController().connect(channel);
            if (this.audioConnection != null) {
               this.audioConnection.setChannel(channel);
            }

         }
      }
   }

   private void checkChannel(AudioChannel channel, Member self) {
      EnumSet<Permission> perms = Permission.getPermissions(PermissionUtil.getEffectivePermission(channel.getPermissionContainer(), (Member)self));
      if (!perms.contains(Permission.VOICE_CONNECT)) {
         throw new InsufficientPermissionException(channel, Permission.VOICE_CONNECT);
      } else {
         int userLimit = channel instanceof VoiceChannel ? ((VoiceChannel)channel).getUserLimit() : 0;
         if (userLimit > 0 && !perms.contains(Permission.ADMINISTRATOR) && userLimit <= channel.getMembers().size() && !perms.contains(Permission.VOICE_MOVE_OTHERS)) {
            throw new InsufficientPermissionException(channel, Permission.VOICE_MOVE_OTHERS, "Unable to connect to AudioChannel due to userlimit! Requires permission VOICE_MOVE_OTHERS to bypass");
         }
      }
   }

   public void closeAudioConnection() {
      this.getJDA().getAudioLifeCyclePool().execute(() -> {
         this.getJDA().setContext();
         this.closeAudioConnection(ConnectionStatus.NOT_CONNECTED);
      });
   }

   public void closeAudioConnection(ConnectionStatus reason) {
      MiscUtil.locked(this.CONNECTION_LOCK, (Runnable)(() -> {
         if (this.audioConnection != null) {
            this.audioConnection.close(reason);
         } else if (reason != ConnectionStatus.DISCONNECTED_REMOVED_FROM_GUILD) {
            this.getJDA().getDirectAudioController().disconnect(this.getGuild());
         }

         this.audioConnection = null;
      }));
   }

   public void setSpeakingMode(@Nonnull Collection<SpeakingMode> mode) {
      Checks.notEmpty(mode, "Speaking Mode");
      this.speakingModes = EnumSet.copyOf(mode);
      if (this.audioConnection != null) {
         this.audioConnection.setSpeakingMode(this.speakingModes);
      }

   }

   @Nonnull
   public EnumSet<SpeakingMode> getSpeakingMode() {
      return EnumSet.copyOf(this.speakingModes);
   }

   @Nonnull
   public JDAImpl getJDA() {
      return this.getGuild().getJDA();
   }

   @Nonnull
   public GuildImpl getGuild() {
      return this.guild;
   }

   public AudioChannelUnion getConnectedChannel() {
      return this.audioConnection == null ? null : (AudioChannelUnion)this.audioConnection.getChannel();
   }

   public boolean isConnected() {
      return this.audioConnection != null;
   }

   public void setConnectTimeout(long timeout) {
      this.timeout = timeout;
   }

   public long getConnectTimeout() {
      return this.timeout;
   }

   public void setSendingHandler(AudioSendHandler handler) {
      this.sendHandler = handler;
      if (this.audioConnection != null) {
         this.audioConnection.setSendingHandler(handler);
      }

   }

   public AudioSendHandler getSendingHandler() {
      return this.sendHandler;
   }

   public void setReceivingHandler(AudioReceiveHandler handler) {
      this.receiveHandler = handler;
      if (this.audioConnection != null) {
         this.audioConnection.setReceivingHandler(handler);
      }

   }

   public AudioReceiveHandler getReceivingHandler() {
      return this.receiveHandler;
   }

   public void setConnectionListener(ConnectionListener listener) {
      this.connectionListener.setListener(listener);
   }

   public ConnectionListener getConnectionListener() {
      return this.connectionListener.getListener();
   }

   @Nonnull
   public ConnectionStatus getConnectionStatus() {
      return this.audioConnection != null ? this.audioConnection.getConnectionStatus() : ConnectionStatus.NOT_CONNECTED;
   }

   public void setAutoReconnect(boolean shouldReconnect) {
      this.shouldReconnect = shouldReconnect;
      if (this.audioConnection != null) {
         this.audioConnection.setAutoReconnect(shouldReconnect);
      }

   }

   public boolean isAutoReconnect() {
      return this.shouldReconnect;
   }

   public void setSelfMuted(boolean muted) {
      if (this.selfMuted != muted) {
         this.selfMuted = muted;
         this.updateVoiceState();
      }

   }

   public boolean isSelfMuted() {
      return this.selfMuted;
   }

   public void setSelfDeafened(boolean deafened) {
      if (this.selfDeafened != deafened) {
         this.selfDeafened = deafened;
         this.updateVoiceState();
      }

   }

   public boolean isSelfDeafened() {
      return this.selfDeafened;
   }

   public ConnectionListener getListenerProxy() {
      return this.connectionListener;
   }

   public void setAudioConnection(AudioConnection audioConnection) {
      if (audioConnection == null) {
         this.audioConnection = null;
      } else {
         if (this.audioConnection != null) {
            this.closeAudioConnection(ConnectionStatus.AUDIO_REGION_CHANGE);
         }

         this.audioConnection = audioConnection;
         audioConnection.setSendingHandler(this.sendHandler);
         audioConnection.setReceivingHandler(this.receiveHandler);
         audioConnection.setQueueTimeout(this.queueTimeout);
         audioConnection.setSpeakingMode(this.speakingModes);
      }
   }

   public void setConnectedChannel(AudioChannel channel) {
      if (this.audioConnection != null) {
         this.audioConnection.setChannel(channel);
      }

   }

   public void setQueueTimeout(long queueTimeout) {
      this.queueTimeout = queueTimeout;
      if (this.audioConnection != null) {
         this.audioConnection.setQueueTimeout(queueTimeout);
      }

   }

   protected void updateVoiceState() {
      AudioChannel channel = this.getConnectedChannel();
      if (channel != null) {
         this.getJDA().getDirectAudioController().connect(channel);
      }

   }

   protected void finalize() {
      if (this.audioConnection != null) {
         LOG.warn((String)"Finalized AudioManager with active audio connection. GuildId: {}", (Object)this.getGuild().getId());
         this.audioConnection.close(ConnectionStatus.DISCONNECTED_REMOVED_FROM_GUILD);
      }

      this.audioConnection = null;
   }
}
