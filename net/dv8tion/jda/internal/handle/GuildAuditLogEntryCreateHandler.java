package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;

public class GuildAuditLogEntryCreateHandler extends SocketHandler {
   public GuildAuditLogEntryCreateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      long id = content.getLong("guild_id");
      if (this.getJDA().getGuildSetupController().isLocked(id)) {
         return id;
      } else {
         GuildImpl guild = (GuildImpl)this.getJDA().getGuildById(id);
         if (guild == null) {
            this.getJDA().getEventCache().cache(EventCache.Type.GUILD, id, this.responseNumber, this.allContent, this::handle);
            EventCache.LOG.debug((String)"Received Guild Audit Log Create event for a Guild not yet cached. GuildId: {}", (Object)id);
            return null;
         } else {
            AuditLogEntry entry = this.api.getEntityBuilder().createAuditLogEntry(guild, content, (DataObject)null, (DataObject)null);
            this.api.handleEvent(new GuildAuditLogEntryCreateEvent(this.api, this.responseNumber, entry));
            return null;
         }
      }
   }
}
