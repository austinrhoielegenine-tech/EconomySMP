package net.dv8tion.jda.internal.audio;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.internal.utils.EntityString;

public class ConnectionRequest {
   protected final long guildId;
   protected long nextAttemptEpoch;
   protected ConnectionStage stage;
   protected long channelId;

   public ConnectionRequest(Guild guild) {
      this.stage = ConnectionStage.DISCONNECT;
      this.guildId = guild.getIdLong();
   }

   public ConnectionRequest(AudioChannel channel, ConnectionStage stage) {
      this.channelId = channel.getIdLong();
      this.guildId = channel.getGuild().getIdLong();
      this.stage = stage;
      this.nextAttemptEpoch = System.currentTimeMillis();
   }

   public void setStage(ConnectionStage stage) {
      this.stage = stage;
   }

   public void setChannel(AudioChannel channel) {
      this.channelId = channel.getIdLong();
   }

   public void setNextAttemptEpoch(long epochMillis) {
      this.nextAttemptEpoch = epochMillis;
   }

   public AudioChannel getChannel(JDA api) {
      return (AudioChannel)api.getGuildChannelById(this.channelId);
   }

   public long getChannelId() {
      return this.channelId;
   }

   public ConnectionStage getStage() {
      return this.stage;
   }

   public long getNextAttemptEpoch() {
      return this.nextAttemptEpoch;
   }

   public long getGuildIdLong() {
      return this.guildId;
   }

   public String toString() {
      return (new EntityString(this)).setType(this.stage).addMetadata("guildId", Long.toUnsignedString(this.guildId)).addMetadata("channelId", Long.toUnsignedString(this.channelId)).toString();
   }
}
