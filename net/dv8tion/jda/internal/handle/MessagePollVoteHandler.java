package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.poll.MessagePollVoteAddEvent;
import net.dv8tion.jda.api.events.message.poll.MessagePollVoteRemoveEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.requests.WebSocketClient;

public class MessagePollVoteHandler extends SocketHandler {
   private final boolean add;

   public MessagePollVoteHandler(JDAImpl api, boolean add) {
      super(api);
      this.add = add;
   }

   protected Long handleInternally(DataObject content) {
      long answerId = content.getLong("answer_id");
      long userId = content.getUnsignedLong("user_id");
      long messageId = content.getUnsignedLong("message_id");
      long channelId = content.getUnsignedLong("channel_id");
      long guildId = content.getUnsignedLong("guild_id", 0L);
      if (this.api.getGuildSetupController().isLocked(guildId)) {
         return guildId;
      } else {
         Guild guild = this.api.getGuildById(guildId);
         MessageChannel channel = (MessageChannel)this.api.getChannelById(MessageChannel.class, channelId);
         if (channel == null) {
            if (guild != null) {
               GuildChannel actual = guild.getGuildChannelById(channelId);
               if (actual != null) {
                  WebSocketClient.LOG.debug((String)"Dropping message poll vote event for unexpected channel of type {}", (Object)actual.getType());
                  return null;
               }
            }

            if (guildId != 0L) {
               this.api.getEventCache().cache(EventCache.Type.CHANNEL, channelId, this.responseNumber, this.allContent, this::handle);
               EventCache.LOG.debug("Received a vote for a channel that JDA does not currently have cached");
               return null;
            }

            channel = this.getJDA().getEntityBuilder().createPrivateChannel(DataObject.empty().put("id", channelId));
         }

         if (this.add) {
            this.api.handleEvent(new MessagePollVoteAddEvent(channel, this.responseNumber, messageId, userId, answerId));
         } else {
            this.api.handleEvent(new MessagePollVoteRemoveEvent(channel, this.responseNumber, messageId, userId, answerId));
         }

         return null;
      }
   }
}
