package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.MemberImpl;

public class GuildMemberAddHandler extends SocketHandler {
   public GuildMemberAddHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      long id = content.getLong("guild_id");
      boolean setup = this.getJDA().getGuildSetupController().onAddMember(id, content);
      if (setup) {
         return null;
      } else {
         GuildImpl guild = (GuildImpl)this.getJDA().getGuildById(id);
         if (guild == null) {
            this.getJDA().getEventCache().cache(EventCache.Type.GUILD, id, this.responseNumber, this.allContent, this::handle);
            EventCache.LOG.debug((String)"Caching member for guild that is not yet cached. Guild ID: {} JSON: {}", (Object)id, (Object)content);
            return null;
         } else {
            guild.onMemberAdd();
            MemberImpl member = this.getJDA().getEntityBuilder().createMember(guild, content);
            this.getJDA().getEntityBuilder().updateMemberCache(member);
            this.getJDA().handleEvent(new GuildMemberJoinEvent(this.getJDA(), this.responseNumber, member));
            return null;
         }
      }
   }
}
