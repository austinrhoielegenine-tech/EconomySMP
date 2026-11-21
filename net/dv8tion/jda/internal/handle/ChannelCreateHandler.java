package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.requests.WebSocketClient;

public class ChannelCreateHandler extends SocketHandler {
   public ChannelCreateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      ChannelType type = ChannelType.fromId(content.getInt("type"));
      long guildId = 0L;
      JDAImpl jda = this.getJDA();
      if (type.isGuild()) {
         guildId = content.getLong("guild_id");
         if (jda.getGuildSetupController().isLocked(guildId)) {
            return guildId;
         }
      }

      Channel channel = this.buildChannel(type, content, guildId);
      if (channel == null) {
         WebSocketClient.LOG.debug((String)"Discord provided an CREATE_CHANNEL event with an unknown channel type! JSON: {}", (Object)content);
         return null;
      } else {
         jda.handleEvent(new ChannelCreateEvent(jda, this.responseNumber, channel));
         return null;
      }
   }

   private Channel buildChannel(ChannelType type, DataObject content, long guildId) {
      EntityBuilder builder = this.getJDA().getEntityBuilder();
      switch (type) {
         case TEXT:
            return builder.createTextChannel(content, guildId);
         case NEWS:
            return builder.createNewsChannel(content, guildId);
         case VOICE:
            return builder.createVoiceChannel(content, guildId);
         case STAGE:
            return builder.createStageChannel(content, guildId);
         case CATEGORY:
            return builder.createCategory(content, guildId);
         case FORUM:
            return builder.createForumChannel(content, guildId);
         case MEDIA:
            return builder.createMediaChannel(content, guildId);
         default:
            return null;
      }
   }
}
