package net.dv8tion.jda.internal.handle;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.requests.WebSocketClient;

public class ReadyHandler extends SocketHandler {
   public ReadyHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      EntityBuilder builder = this.getJDA().getEntityBuilder();
      DataArray guilds = content.getArray("guilds");
      TLongObjectMap<DataObject> distinctGuilds = new TLongObjectHashMap<DataObject>();

      for(int i = 0; i < guilds.length(); ++i) {
         DataObject guild = guilds.getObject(i);
         long id = guild.getUnsignedLong("id");
         DataObject previous = distinctGuilds.put(id, guild);
         if (previous != null) {
            WebSocketClient.LOG.warn((String)"Found duplicate guild for id {} in ready payload", (Object)id);
         }
      }

      DataObject selfJson = content.getObject("user");
      selfJson.put("application_id", content.optObject("application").map((obj) -> obj.getUnsignedLong("id")).orElse(selfJson.getUnsignedLong("id")));
      builder.createSelfUser(selfJson);
      if (this.getJDA().getGuildSetupController().setIncompleteCount(distinctGuilds.size())) {
         distinctGuilds.forEachEntry((idx, guildx) -> {
            this.getJDA().getGuildSetupController().onReady(idx, guildx);
            return true;
         });
      }

      this.handleReady(content);
      return null;
   }

   public void handleReady(DataObject content) {
      EntityBuilder builder = this.getJDA().getEntityBuilder();
      DataArray privateChannels = content.getArray("private_channels");

      for(int i = 0; i < privateChannels.length(); ++i) {
         DataObject chan = privateChannels.getObject(i);
         ChannelType type = ChannelType.fromId(chan.getInt("type"));
         switch (type) {
            case PRIVATE:
               builder.createPrivateChannel(chan);
               break;
            default:
               WebSocketClient.LOG.warn((String)"Received a Channel in the private_channels array in READY of an unknown type! Type: {}", (Object)type);
         }
      }

   }
}
