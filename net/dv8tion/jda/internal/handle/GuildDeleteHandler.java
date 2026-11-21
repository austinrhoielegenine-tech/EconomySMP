package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildUnavailableEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.requests.WebSocketClient;

public class GuildDeleteHandler extends SocketHandler {
   public GuildDeleteHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      long id = content.getLong("id");
      GuildSetupController setupController = this.getJDA().getGuildSetupController();
      boolean wasInit = setupController.onDelete(id, content);
      if (!wasInit && !setupController.isUnavailable(id)) {
         GuildImpl guild = (GuildImpl)this.getJDA().getGuildById(id);
         boolean unavailable = content.getBoolean("unavailable");
         if (guild == null) {
            WebSocketClient.LOG.debug((String)"Received GUILD_DELETE for a Guild that is not currently cached. ID: {} unavailable: {}", (Object)id, (Object)unavailable);
            return null;
         } else if (setupController.isUnavailable(id) && unavailable) {
            return null;
         } else {
            guild.invalidate();
            if (unavailable) {
               setupController.onUnavailable(id);
               this.getJDA().handleEvent(new GuildUnavailableEvent(this.getJDA(), this.responseNumber, guild));
            } else {
               this.getJDA().handleEvent(new GuildLeaveEvent(this.getJDA(), this.responseNumber, guild));
            }

            this.getJDA().getEventCache().clear(EventCache.Type.GUILD, id);
            return null;
         }
      } else {
         return null;
      }
   }
}
