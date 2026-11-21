package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.ThreadChannelImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.MessageChannelMixin;
import net.dv8tion.jda.internal.requests.WebSocketClient;

public class MessageCreateHandler extends SocketHandler {
   public MessageCreateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      MessageType type = MessageType.fromId(content.getInt("type"));
      if (type == MessageType.UNKNOWN) {
         WebSocketClient.LOG.debug((String)"JDA received a message of unknown type. Type: {}  JSON: {}", (Object)type, (Object)content);
         return null;
      } else if ((content.getInt("flags", 0) & 64) != 0) {
         return null;
      } else {
         JDAImpl jda = this.getJDA();
         Guild guild = null;
         if (!content.isNull("guild_id")) {
            long guildId = content.getLong("guild_id");
            if (jda.getGuildSetupController().isLocked(guildId)) {
               return guildId;
            }

            guild = this.api.getGuildById(guildId);
            if (guild == null) {
               this.api.getEventCache().cache(EventCache.Type.GUILD, guildId, this.responseNumber, this.allContent, this::handle);
               EventCache.LOG.debug("Received message for a guild that JDA does not currently have cached");
               return null;
            }
         }

         Message message;
         try {
            message = jda.getEntityBuilder().createMessageWithLookup(content, guild, true);
            if (!message.hasChannel()) {
               throw new IllegalArgumentException("MISSING_CHANNEL");
            }
         } catch (IllegalArgumentException e) {
            switch (e.getMessage()) {
               case "MISSING_CHANNEL":
                  long channelId = content.getLong("channel_id");
                  if (guild != null) {
                     GuildChannel actual = guild.getGuildChannelById(channelId);
                     if (actual != null) {
                        WebSocketClient.LOG.debug((String)"Dropping MESSAGE_CREATE for unexpected channel of type {}", (Object)actual.getType());
                        return null;
                     }
                  }

                  jda.getEventCache().cache(EventCache.Type.CHANNEL, channelId, this.responseNumber, this.allContent, this::handle);
                  EventCache.LOG.debug("Received a message for a channel that JDA does not currently have cached");
                  return null;
               case "MISSING_USER":
                  long authorId = content.getObject("author").getLong("id");
                  jda.getEventCache().cache(EventCache.Type.USER, authorId, this.responseNumber, this.allContent, this::handle);
                  EventCache.LOG.debug("Received a message for a user that JDA does not currently have cached");
                  return null;
               case "UNKNOWN_MESSAGE_TYPE":
                  WebSocketClient.LOG.debug((String)"Ignoring message with unknown type: {}", (Object)content);
                  return null;
               default:
                  throw e;
            }
         }

         MessageChannel channel = message.getChannel();
         ChannelType channelType = channel.getType();
         ((MessageChannelMixin)channel).setLatestMessageIdLong(message.getIdLong());
         if (channelType.isGuild()) {
            if (channelType.isThread()) {
               ThreadChannelImpl gThread = (ThreadChannelImpl)channel;
               gThread.setMessageCount(gThread.getMessageCount() + 1);
               gThread.setTotalMessageCount(gThread.getTotalMessageCount() + 1);
            }
         } else {
            this.api.usedPrivateChannel(channel.getIdLong());
         }

         jda.handleEvent(new MessageReceivedEvent(jda, this.responseNumber, message));
         return null;
      }
   }
}
