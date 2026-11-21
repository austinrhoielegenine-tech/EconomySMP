package net.dv8tion.jda.internal.handle;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.MemberImpl;
import net.dv8tion.jda.internal.requests.WebSocketClient;
import net.dv8tion.jda.internal.utils.Helpers;

public class GuildMembersChunkHandler extends SocketHandler {
   public GuildMembersChunkHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      long guildId = content.getLong("guild_id");
      DataArray members = content.getArray("members");
      GuildImpl guild = (GuildImpl)this.getJDA().getGuildById(guildId);
      if (guild == null) {
         this.getJDA().getGuildSetupController().onMemberChunk(guildId, content);
         return null;
      } else if (this.api.getClient().getChunkManager().handleChunk(guildId, content)) {
         return null;
      } else {
         WebSocketClient.LOG.debug("Received member chunk for guild that is already in cache. GuildId: {} Count: {} Index: {}/{}", guildId, members.length(), content.getInt("chunk_index"), content.getInt("chunk_count"));
         EntityBuilder builder = this.getJDA().getEntityBuilder();
         TLongObjectMap<DataObject> presences = (TLongObjectMap)content.optArray("presences").map((it) -> Helpers.convertToMap((o) -> o.getObject("user").getUnsignedLong("id"), it)).orElseGet(TLongObjectHashMap::new);

         for(int i = 0; i < members.length(); ++i) {
            DataObject object = members.getObject(i);
            long userId = object.getObject("user").getUnsignedLong("id");
            DataObject presence = presences.get(userId);
            MemberImpl member = builder.createMember(guild, object, (DataObject)null, presence);
            builder.updateMemberCache(member);
         }

         return null;
      }
   }
}
