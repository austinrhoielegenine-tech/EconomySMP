package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventUserAddEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventUserRemoveEvent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;

public class ScheduledEventUserHandler extends SocketHandler {
   private final boolean add;

   public ScheduledEventUserHandler(JDAImpl api, boolean add) {
      super(api);
      this.add = add;
   }

   protected Long handleInternally(DataObject content) {
      if (!this.getJDA().isCacheFlagSet(CacheFlag.SCHEDULED_EVENTS)) {
         return null;
      } else {
         long guildId = content.getUnsignedLong("guild_id", 0L);
         if (this.getJDA().getGuildSetupController().isLocked(guildId)) {
            return guildId;
         } else {
            GuildImpl guild = (GuildImpl)this.getJDA().getGuildById(guildId);
            if (guild == null) {
               EventCache.LOG.debug((String)"Caching SCHEDULED_EVENT_USER_ADD for uncached guild with id {}", (Object)guildId);
               this.getJDA().getEventCache().cache(EventCache.Type.GUILD, guildId, this.responseNumber, this.allContent, this::handle);
               return null;
            } else {
               ScheduledEvent event = guild.getScheduledEventById(content.getUnsignedLong("guild_scheduled_event_id"));
               long userId = content.getUnsignedLong("user_id");
               if (event == null) {
                  return null;
               } else {
                  if (this.add) {
                     this.getJDA().handleEvent(new ScheduledEventUserAddEvent(this.getJDA(), this.responseNumber, event, userId));
                  } else {
                     this.getJDA().handleEvent(new ScheduledEventUserRemoveEvent(this.getJDA(), this.responseNumber, event, userId));
                  }

                  return null;
               }
            }
         }
      }
   }
}
