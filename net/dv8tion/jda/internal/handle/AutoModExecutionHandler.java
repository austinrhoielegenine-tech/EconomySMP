package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.automod.AutoModExecutionEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.automod.AutoModExecutionImpl;

public class AutoModExecutionHandler extends SocketHandler {
   public AutoModExecutionHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      long guildId = content.getUnsignedLong("guild_id");
      if (this.api.getGuildSetupController().isLocked(guildId)) {
         return guildId;
      } else {
         Guild guild = this.api.getGuildById(guildId);
         if (guild == null) {
            this.api.getEventCache().cache(EventCache.Type.GUILD, guildId, this.responseNumber, this.allContent, this::handle);
            EventCache.LOG.debug((String)"Received a AUTO_MODERATION_ACTION_EXECUTION for a guild that is not yet cached. JSON: {}", (Object)content);
            return null;
         } else {
            AutoModExecutionImpl execution = new AutoModExecutionImpl(guild, content);
            this.api.handleEvent(new AutoModExecutionEvent(this.api, this.responseNumber, execution));
            return null;
         }
      }
   }
}
