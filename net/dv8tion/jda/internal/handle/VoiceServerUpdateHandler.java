package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.audio.AudioConnection;
import net.dv8tion.jda.internal.managers.AudioManagerImpl;
import net.dv8tion.jda.internal.requests.WebSocketClient;

public class VoiceServerUpdateHandler extends SocketHandler {
   public VoiceServerUpdateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      long guildId = content.getLong("guild_id");
      if (this.getJDA().getGuildSetupController().isLocked(guildId)) {
         return guildId;
      } else {
         Guild guild = this.getJDA().getGuildById(guildId);
         if (guild == null) {
            throw new IllegalArgumentException("Attempted to start audio connection with Guild that doesn't exist!");
         } else {
            this.getJDA().getDirectAudioController().update(guild, guild.getSelfMember().getVoiceState().getChannel());
            if (content.isNull("endpoint")) {
               return null;
            } else {
               String endpoint = content.getString("endpoint");
               String token = content.getString("token");
               String sessionId = guild.getSelfMember().getVoiceState().getSessionId();
               if (sessionId == null) {
                  throw new IllegalArgumentException("Attempted to create audio connection without having a session ID. Did VOICE_STATE_UPDATED fail?");
               } else {
                  VoiceDispatchInterceptor voiceInterceptor = this.getJDA().getVoiceInterceptor();
                  if (voiceInterceptor != null) {
                     voiceInterceptor.onVoiceServerUpdate(new VoiceDispatchInterceptor.VoiceServerUpdate(guild, endpoint, token, sessionId, this.allContent));
                     return null;
                  } else {
                     AudioManagerImpl audioManager = (AudioManagerImpl)this.getJDA().getAudioManagersView().get(guildId);
                     if (audioManager == null) {
                        WebSocketClient.LOG.debug("Received a VOICE_SERVER_UPDATE but JDA is not currently connected nor attempted to connect to a VoiceChannel. Assuming that this is caused by another client running on this account. Ignoring the event.");
                        return null;
                     } else {
                        MiscUtil.locked(audioManager.CONNECTION_LOCK, (Runnable)(() -> {
                           AudioChannel target = guild.getSelfMember().getVoiceState().getChannel();
                           if (target == null) {
                              WebSocketClient.LOG.warn("Ignoring VOICE_SERVER_UPDATE for unknown channel");
                           } else {
                              AudioConnection connection = new AudioConnection(audioManager, endpoint, sessionId, token, target);
                              audioManager.setAudioConnection(connection);
                              connection.startConnection();
                           }
                        }));
                        return null;
                     }
                  }
               }
            }
         }
      }
   }
}
