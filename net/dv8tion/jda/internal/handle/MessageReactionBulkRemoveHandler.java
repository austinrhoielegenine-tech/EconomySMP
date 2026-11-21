package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.requests.WebSocketClient;

public class MessageReactionBulkRemoveHandler extends SocketHandler {
   public MessageReactionBulkRemoveHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      long messageId = content.getLong("message_id");
      long channelId = content.getLong("channel_id");
      JDAImpl jda = this.getJDA();
      Guild guild = null;
      if (!content.isNull("guild_id")) {
         long guildId = content.getUnsignedLong("guild_id");
         if (this.api.getGuildSetupController().isLocked(guildId)) {
            return guildId;
         }

         guild = this.getJDA().getGuildById(guildId);
         if (guild == null) {
            jda.getEventCache().cache(EventCache.Type.GUILD, guildId, this.responseNumber, this.allContent, this::handle);
            EventCache.LOG.debug((String)"Got MESSAGE_REACTION_REMOVE_ALL for a guild that is not yet cached. GuildId: {}", (Object)guildId);
            return null;
         }
      }

      MessageChannel channel = (MessageChannel)jda.getChannelById(MessageChannel.class, channelId);
      if (channel == null) {
         if (guild != null) {
            GuildChannel actual = guild.getGuildChannelById(channelId);
            if (actual != null) {
               WebSocketClient.LOG.debug((String)"Dropping MESSAGE_REACTION_REMOVE_ALL for unexpected channel of type {}", (Object)actual.getType());
               return null;
            }
         }

         jda.getEventCache().cache(EventCache.Type.CHANNEL, channelId, this.responseNumber, this.allContent, this::handle);
         EventCache.LOG.debug((String)"Received a reaction for a channel that JDA does not currently have cached channel_id: {} message_id: {}", (Object)channelId, (Object)messageId);
         return null;
      } else {
         jda.handleEvent(new MessageReactionRemoveAllEvent(jda, this.responseNumber, messageId, channel));
         return null;
      }
   }
}
