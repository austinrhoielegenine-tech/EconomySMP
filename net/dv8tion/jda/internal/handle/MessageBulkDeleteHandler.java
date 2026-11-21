package net.dv8tion.jda.internal.handle;

import java.util.List;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.requests.WebSocketClient;

public class MessageBulkDeleteHandler extends SocketHandler {
   public MessageBulkDeleteHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      Guild guild = null;
      if (!content.isNull("guild_id")) {
         long guildId = content.getLong("guild_id");
         if (this.getJDA().getGuildSetupController().isLocked(guildId)) {
            return guildId;
         }

         guild = this.api.getGuildById(guildId);
         if (guild == null) {
            EventCache.LOG.debug((String)"Caching MESSAGE_DELETE event for guild that is not currently cached. GuildID: {}", (Object)guildId);
            this.api.getEventCache().cache(EventCache.Type.GUILD, guildId, this.responseNumber, this.allContent, this::handle);
            return null;
         }
      }

      long channelId = content.getLong("channel_id");
      if (this.getJDA().isBulkDeleteSplittingEnabled()) {
         SocketHandler handler = (SocketHandler)this.getJDA().getClient().getHandlers().get("MESSAGE_DELETE");
         content.getArray("ids").forEach((id) -> handler.handle(this.responseNumber, DataObject.empty().put("t", "MESSAGE_DELETE").put("d", DataObject.empty().put("channel_id", Long.toUnsignedString(channelId)).put("id", id))));
      } else {
         GuildMessageChannel channel = (GuildMessageChannel)this.getJDA().getChannelById(GuildMessageChannel.class, channelId);
         if (channel == null) {
            if (guild != null) {
               GuildChannel guildChannel = guild.getGuildChannelById(channelId);
               if (guildChannel != null) {
                  WebSocketClient.LOG.debug((String)"Discarding MESSAGE_DELETE event for unexpected channel type. Channel: {}", (Object)guildChannel);
                  return null;
               }
            }

            this.getJDA().getEventCache().cache(EventCache.Type.CHANNEL, channelId, this.responseNumber, this.allContent, this::handle);
            EventCache.LOG.debug("Received a Bulk Message Delete for a GuildMessageChannel that is not yet cached.");
            return null;
         }

         if (this.getJDA().getGuildSetupController().isLocked(channel.getGuild().getIdLong())) {
            return channel.getGuild().getIdLong();
         }

         DataArray array = content.getArray("ids");
         List<String> messages = (List)array.stream(DataArray::getString).collect(Collectors.toList());
         this.getJDA().handleEvent(new MessageBulkDeleteEvent(this.getJDA(), this.responseNumber, channel, messages));
      }

      return null;
   }
}
