package net.dv8tion.jda.internal.managers;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.DirectAudioController;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.requests.WebSocketClient;
import net.dv8tion.jda.internal.utils.Checks;

public class DirectAudioControllerImpl implements DirectAudioController {
   private final JDAImpl api;

   public DirectAudioControllerImpl(JDAImpl api) {
      this.api = api;
   }

   @Nonnull
   public JDAImpl getJDA() {
      return this.api;
   }

   public void connect(@Nonnull AudioChannel channel) {
      Checks.notNull(channel, "Audio Channel");
      JDAImpl jda = this.getJDA();
      WebSocketClient client = jda.getClient();
      client.queueAudioConnect(channel);
   }

   public void disconnect(@Nonnull Guild guild) {
      Checks.notNull(guild, "Guild");
      JDAImpl jda = this.getJDA();
      WebSocketClient client = jda.getClient();
      client.queueAudioDisconnect(guild);
   }

   public void reconnect(@Nonnull AudioChannel channel) {
      Checks.notNull(channel, "Audio Channel");
      JDAImpl jda = this.getJDA();
      WebSocketClient client = jda.getClient();
      client.queueAudioReconnect(channel);
   }

   public void update(Guild guild, AudioChannel channel) {
      Checks.notNull(guild, "Guild");
      JDAImpl jda = this.getJDA();
      WebSocketClient client = jda.getClient();
      client.updateAudioConnection(guild.getIdLong(), channel);
   }
}
