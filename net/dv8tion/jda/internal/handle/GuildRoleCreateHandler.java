package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;

public class GuildRoleCreateHandler extends SocketHandler {
   public GuildRoleCreateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      long guildId = content.getLong("guild_id");
      if (this.getJDA().getGuildSetupController().isLocked(guildId)) {
         return guildId;
      } else {
         GuildImpl guild = (GuildImpl)this.getJDA().getGuildById(guildId);
         if (guild == null) {
            this.getJDA().getEventCache().cache(EventCache.Type.GUILD, guildId, this.responseNumber, this.allContent, this::handle);
            EventCache.LOG.debug((String)"GUILD_ROLE_CREATE was received for a Guild that is not yet cached: {}", (Object)content);
            return null;
         } else {
            Role newRole = this.getJDA().getEntityBuilder().createRole(guild, content.getObject("role"), guild.getIdLong());
            this.getJDA().handleEvent(new RoleCreateEvent(this.getJDA(), this.responseNumber, newRole));
            return null;
         }
      }
   }
}
