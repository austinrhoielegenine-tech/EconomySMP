package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.MemberImpl;
import net.dv8tion.jda.internal.entities.RoleImpl;
import net.dv8tion.jda.internal.entities.emoji.RichCustomEmojiImpl;
import net.dv8tion.jda.internal.requests.WebSocketClient;

public class GuildRoleDeleteHandler extends SocketHandler {
   public GuildRoleDeleteHandler(JDAImpl api) {
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
            EventCache.LOG.debug((String)"GUILD_ROLE_DELETE was received for a Guild that is not yet cached: {}", (Object)content);
            return null;
         } else {
            long roleId = content.getLong("role_id");
            RoleImpl removedRole = (RoleImpl)guild.getRolesView().get(roleId);
            if (removedRole == null) {
               WebSocketClient.LOG.debug((String)"GUILD_ROLE_DELETE was received for a Role that is not yet cached: {}", (Object)content);
               return null;
            } else {
               removedRole.freezePosition();
               guild.getRolesView().remove(roleId);
               guild.getMembersView().forEach((m) -> {
                  MemberImpl member = (MemberImpl)m;
                  member.getRoleSet().remove(removedRole);
               });

               for(RichCustomEmoji emoji : guild.getEmojiCache()) {
                  RichCustomEmojiImpl impl = (RichCustomEmojiImpl)emoji;
                  impl.getRoleSet().remove(removedRole);
               }

               this.getJDA().handleEvent(new RoleDeleteEvent(this.getJDA(), this.responseNumber, removedRole));
               this.getJDA().getEventCache().clear(EventCache.Type.ROLE, roleId);
               return null;
            }
         }
      }
   }
}
