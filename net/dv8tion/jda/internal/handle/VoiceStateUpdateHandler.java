package net.dv8tion.jda.internal.handle;

import java.time.OffsetDateTime;
import java.util.Objects;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMuteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceRequestToSpeakEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceSelfDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceSelfMuteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceStreamEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceSuppressEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceVideoEvent;
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.GuildVoiceStateImpl;
import net.dv8tion.jda.internal.entities.MemberImpl;
import net.dv8tion.jda.internal.managers.AudioManagerImpl;
import net.dv8tion.jda.internal.requests.WebSocketClient;

public class VoiceStateUpdateHandler extends SocketHandler {
   public VoiceStateUpdateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      Long guildId = content.isNull("guild_id") ? null : content.getLong("guild_id");
      if (guildId == null) {
         return null;
      } else if (this.getJDA().getGuildSetupController().isLocked(guildId)) {
         return guildId;
      } else if (content.isNull("member")) {
         WebSocketClient.LOG.debug((String)"Discarding VOICE_STATE_UPDATE with missing member. JSON: {}", (Object)content);
         return null;
      } else {
         this.handleGuildVoiceState(content);
         return null;
      }
   }

   private void handleGuildVoiceState(DataObject content) {
      long userId = content.getLong("user_id");
      long guildId = content.getLong("guild_id");
      Long channelId = !content.isNull("channel_id") ? content.getLong("channel_id") : null;
      String sessionId = !content.isNull("session_id") ? content.getString("session_id") : null;
      boolean selfMuted = content.getBoolean("self_mute");
      boolean selfDeafened = content.getBoolean("self_deaf");
      boolean guildMuted = content.getBoolean("mute");
      boolean guildDeafened = content.getBoolean("deaf");
      boolean suppressed = content.getBoolean("suppress");
      boolean stream = content.getBoolean("self_stream");
      boolean video = content.getBoolean("self_video", false);
      String requestToSpeak = content.getString("request_to_speak_timestamp", (String)null);
      OffsetDateTime requestToSpeakTime = null;
      long requestToSpeakTimestamp = 0L;
      if (requestToSpeak != null) {
         requestToSpeakTime = OffsetDateTime.parse(requestToSpeak);
         requestToSpeakTimestamp = requestToSpeakTime.toInstant().toEpochMilli();
      }

      GuildImpl guild = (GuildImpl)this.getJDA().getGuildById(guildId);
      if (guild == null) {
         this.getJDA().getEventCache().cache(EventCache.Type.GUILD, guildId, this.responseNumber, this.allContent, this::handle);
         EventCache.LOG.debug((String)"Received a VOICE_STATE_UPDATE for a Guild that has yet to be cached. JSON: {}", (Object)content);
      } else {
         AudioChannel channel = null;
         if (channelId != null) {
            channel = (AudioChannel)guild.getGuildChannelById(channelId);
         }

         if (channel == null && channelId != null) {
            this.getJDA().getEventCache().cache(EventCache.Type.CHANNEL, channelId, this.responseNumber, this.allContent, this::handle);
            EventCache.LOG.debug((String)"Received VOICE_STATE_UPDATE for an AudioChannel that has yet to be cached. JSON: {}", (Object)content);
         } else {
            DataObject memberJson = content.getObject("member");
            MemberImpl member = this.getJDA().getEntityBuilder().createMember(guild, memberJson);
            GuildVoiceStateImpl vState = member.getVoiceState();
            if (vState == null) {
               if (!guild.shouldCacheVoiceState(userId)) {
                  return;
               }

               vState = new GuildVoiceStateImpl(member);
            }

            vState.setSessionId(sessionId);
            VoiceDispatchInterceptor voiceInterceptor = this.getJDA().getVoiceInterceptor();
            boolean isSelf = guild.getSelfMember().equals(member);
            boolean wasMute = vState.isMuted();
            boolean wasDeaf = vState.isDeafened();
            if (selfMuted != vState.isSelfMuted()) {
               vState.setSelfMuted(selfMuted);
               this.getJDA().getEntityBuilder().updateMemberCache(member);
               this.getJDA().handleEvent(new GuildVoiceSelfMuteEvent(this.getJDA(), this.responseNumber, member, selfMuted));
            }

            if (selfDeafened != vState.isSelfDeafened()) {
               vState.setSelfDeafened(selfDeafened);
               this.getJDA().getEntityBuilder().updateMemberCache(member);
               this.getJDA().handleEvent(new GuildVoiceSelfDeafenEvent(this.getJDA(), this.responseNumber, member, selfDeafened));
            }

            if (guildMuted != vState.isGuildMuted()) {
               vState.setGuildMuted(guildMuted);
               this.getJDA().getEntityBuilder().updateMemberCache(member);
               this.getJDA().handleEvent(new GuildVoiceGuildMuteEvent(this.getJDA(), this.responseNumber, member, guildMuted));
            }

            if (guildDeafened != vState.isGuildDeafened()) {
               vState.setGuildDeafened(guildDeafened);
               this.getJDA().getEntityBuilder().updateMemberCache(member);
               this.getJDA().handleEvent(new GuildVoiceGuildDeafenEvent(this.getJDA(), this.responseNumber, member, guildDeafened));
            }

            if (suppressed != vState.isSuppressed()) {
               vState.setSuppressed(suppressed);
               this.getJDA().getEntityBuilder().updateMemberCache(member);
               this.getJDA().handleEvent(new GuildVoiceSuppressEvent(this.getJDA(), this.responseNumber, member, suppressed));
            }

            if (stream != vState.isStream()) {
               vState.setStream(stream);
               this.getJDA().getEntityBuilder().updateMemberCache(member);
               this.getJDA().handleEvent(new GuildVoiceStreamEvent(this.getJDA(), this.responseNumber, member, stream));
            }

            if (video != vState.isSendingVideo()) {
               vState.setVideo(video);
               this.getJDA().getEntityBuilder().updateMemberCache(member);
               this.getJDA().handleEvent(new GuildVoiceVideoEvent(this.getJDA(), this.responseNumber, member, video));
            }

            if (wasMute != vState.isMuted()) {
               this.getJDA().handleEvent(new GuildVoiceMuteEvent(this.getJDA(), this.responseNumber, member, vState.isMuted()));
            }

            if (wasDeaf != vState.isDeafened()) {
               this.getJDA().handleEvent(new GuildVoiceDeafenEvent(this.getJDA(), this.responseNumber, member, vState.isDeafened()));
            }

            if (requestToSpeakTimestamp != vState.getRequestToSpeak()) {
               OffsetDateTime oldRequestToSpeak = vState.getRequestToSpeakTimestamp();
               vState.setRequestToSpeak(requestToSpeakTime);
               this.getJDA().handleEvent(new GuildVoiceRequestToSpeakEvent(this.getJDA(), this.responseNumber, member, oldRequestToSpeak, requestToSpeakTime));
            }

            if (!Objects.equals(channel, vState.getChannel())) {
               AudioChannel oldChannel = vState.getChannel();
               vState.updateConnectedChannel(channel);
               if (oldChannel == null) {
                  this.getJDA().getEntityBuilder().updateMemberCache(member);
               } else if (channel == null) {
                  if (isSelf) {
                     this.getJDA().getDirectAudioController().update(guild, (AudioChannel)null);
                  }

                  this.getJDA().getEntityBuilder().updateMemberCache(member, memberJson.isNull("joined_at"));
               } else {
                  AudioManagerImpl mng = (AudioManagerImpl)this.getJDA().getAudioManagersView().get(guildId);
                  if (isSelf && mng != null && voiceInterceptor == null) {
                     if (mng.isConnected()) {
                        mng.setConnectedChannel(channel);
                     }

                     if (mng.isConnected()) {
                        this.getJDA().getDirectAudioController().update(guild, channel);
                     }
                  }

                  this.getJDA().getEntityBuilder().updateMemberCache(member);
               }

               this.getJDA().handleEvent(new GuildVoiceUpdateEvent(this.getJDA(), this.responseNumber, member, oldChannel));
            }

            if (isSelf && voiceInterceptor != null && voiceInterceptor.onVoiceStateUpdate(new VoiceDispatchInterceptor.VoiceStateUpdate(channel, vState, this.allContent))) {
               this.getJDA().getDirectAudioController().update(guild, channel);
            }

            guild.updateRequestToSpeak();
         }
      }
   }
}
