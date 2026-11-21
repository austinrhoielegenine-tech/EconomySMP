package net.dv8tion.jda.api.hooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.DirectAudioController;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;

public interface VoiceDispatchInterceptor {
   void onVoiceServerUpdate(@Nonnull VoiceServerUpdate var1);

   boolean onVoiceStateUpdate(@Nonnull VoiceStateUpdate var1);

   public interface VoiceUpdate extends SerializableData {
      @Nonnull
      Guild getGuild();

      @Nonnull
      DataObject toData();

      @Nonnull
      default DirectAudioController getAudioController() {
         return this.getJDA().getDirectAudioController();
      }

      default long getGuildIdLong() {
         return this.getGuild().getIdLong();
      }

      @Nonnull
      default String getGuildId() {
         return Long.toUnsignedString(this.getGuildIdLong());
      }

      @Nonnull
      default JDA getJDA() {
         return this.getGuild().getJDA();
      }

      @Nullable
      default JDA.ShardInfo getShardInfo() {
         return this.getJDA().getShardInfo();
      }
   }

   public static class VoiceServerUpdate implements VoiceUpdate {
      private final Guild guild;
      private final String endpoint;
      private final String token;
      private final String sessionId;
      private final DataObject json;

      public VoiceServerUpdate(Guild guild, String endpoint, String token, String sessionId, DataObject json) {
         this.guild = guild;
         this.endpoint = endpoint;
         this.token = token;
         this.sessionId = sessionId;
         this.json = json;
      }

      @Nonnull
      public Guild getGuild() {
         return this.guild;
      }

      @Nonnull
      public DataObject toData() {
         return this.json;
      }

      @Nonnull
      public String getEndpoint() {
         return this.endpoint;
      }

      @Nonnull
      public String getToken() {
         return this.token;
      }

      @Nonnull
      public String getSessionId() {
         return this.sessionId;
      }
   }

   public static class VoiceStateUpdate implements VoiceUpdate {
      private final AudioChannel channel;
      private final GuildVoiceState voiceState;
      private final DataObject json;

      public VoiceStateUpdate(AudioChannel channel, GuildVoiceState voiceState, DataObject json) {
         this.channel = channel;
         this.voiceState = voiceState;
         this.json = json;
      }

      @Nonnull
      public Guild getGuild() {
         return this.voiceState.getGuild();
      }

      @Nonnull
      public DataObject toData() {
         return this.json;
      }

      @Nullable
      public AudioChannel getChannel() {
         return this.channel;
      }

      @Nonnull
      public GuildVoiceState getVoiceState() {
         return this.voiceState;
      }
   }
}
