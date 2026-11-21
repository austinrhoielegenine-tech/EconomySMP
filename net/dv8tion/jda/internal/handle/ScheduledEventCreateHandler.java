package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;

public class ScheduledEventCreateHandler extends SocketHandler {
   public ScheduledEventCreateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      if (!this.getJDA().isCacheFlagSet(CacheFlag.SCHEDULED_EVENTS)) {
         return null;
      } else {
         long guildId = content.getUnsignedLong("guild_id");
         if (this.getJDA().getGuildSetupController().isLocked(guildId)) {
            return guildId;
         } else {
            GuildImpl guild = (GuildImpl)this.getJDA().getGuildById(guildId);
            if (guild == null) {
               EventCache.LOG.debug((String)"Caching SCHEDULED_EVENT_CREATE for uncached guild with id {}", (Object)guildId);
               this.getJDA().getEventCache().cache(EventCache.Type.GUILD, guildId, this.responseNumber, this.allContent, this::handle);
               return null;
            } else {
               ScheduledEvent event = this.getJDA().getEntityBuilder().createScheduledEvent(guild, content);
               this.getJDA().handleEvent(new ScheduledEventCreateEvent(this.getJDA(), this.responseNumber, event));
               return null;
            }
         }
      }
   }
}
