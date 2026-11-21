package net.dv8tion.jda.internal.handle;

import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.MemberImpl;

public class GuildMemberUpdateHandler extends SocketHandler {
   public GuildMemberUpdateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      long id = content.getLong("guild_id");
      if (this.getJDA().getGuildSetupController().isLocked(id)) {
         return id;
      } else {
         DataObject userJson = content.getObject("user");
         long userId = userJson.getLong("id");
         GuildImpl guild = (GuildImpl)this.getJDA().getGuildById(id);
         if (guild == null) {
            EventCache.LOG.debug((String)"Got GuildMember update but JDA currently does not have the Guild cached. Ignoring. {}", (Object)content);
            return null;
         } else {
            MemberImpl member = (MemberImpl)guild.getMembersView().get(userId);
            if (member == null) {
               member = this.getJDA().getEntityBuilder().createMember(guild, content);
            } else {
               List<Role> newRoles = this.toRolesList(guild, content.getArray("roles"));
               this.getJDA().getEntityBuilder().updateMember(guild, member, content, newRoles);
            }

            this.getJDA().getEntityBuilder().updateMemberCache(member);
            this.getJDA().handleEvent(new GuildMemberUpdateEvent(this.getJDA(), this.responseNumber, member));
            return null;
         }
      }
   }

   private List<Role> toRolesList(GuildImpl guild, DataArray array) {
      List<Role> roles = new ArrayList();

      for(int i = 0; i < array.length(); ++i) {
         long id = array.getLong(i);
         Role r = (Role)guild.getRolesView().get(id);
         if (r == null) {
            this.getJDA().getEventCache().cache(EventCache.Type.ROLE, id, this.responseNumber, this.allContent, this::handle);
            EventCache.LOG.debug("Got GuildMember update but one of the Roles for the Member is not yet cached.");
            return null;
         }

         roles.add(r);
      }

      return roles;
   }
}
