package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventDeleteEvent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;

public class ScheduledEventDeleteHandler extends SocketHandler {
   public ScheduledEventDeleteHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      if (!this.getJDA().isCacheFlagSet(CacheFlag.SCHEDULED_EVENTS)) {
         return null;
      } else {
         long guildId = content.getLong("guild_id");
         if (this.getJDA().getGuildSetupController().isLocked(guildId)) {
            return guildId;
         } else {
            GuildImpl guild = (GuildImpl)this.getJDA().getGuildById(guildId);
            if (guild == null) {
               EventCache.LOG.debug((String)"SCHEDULED_EVENT_DELETE was received for a Guild that is not yet cached: {}", (Object)content);
               return null;
            } else {
               long eventId = content.getLong("id");
               ScheduledEvent removedEvent = (ScheduledEvent)guild.getScheduledEventsView().remove(eventId);
               if (removedEvent != null) {
                  this.getJDA().handleEvent(new ScheduledEventDeleteEvent(this.getJDA(), this.responseNumber, removedEvent));
               }

               return null;
            }
         }
      }
   }
}
