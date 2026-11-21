package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.StageInstance;
import net.dv8tion.jda.api.events.stage.StageInstanceDeleteEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.StageChannelImpl;

public class StageInstanceDeleteHandler extends SocketHandler {
   public StageInstanceDeleteHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      long guildId = content.getUnsignedLong("guild_id", 0L);
      if (this.getJDA().getGuildSetupController().isLocked(guildId)) {
         return guildId;
      } else {
         GuildImpl guild = (GuildImpl)this.getJDA().getGuildById(guildId);
         if (guild == null) {
            EventCache.LOG.debug((String)"Caching STAGE_INSTANCE_DELETE for uncached guild with id {}", (Object)guildId);
            this.getJDA().getEventCache().cache(EventCache.Type.GUILD, guildId, this.responseNumber, this.allContent, this::handle);
            return null;
         } else {
            long channelId = content.getUnsignedLong("channel_id", 0L);
            StageChannelImpl channel = (StageChannelImpl)guild.getStageChannelById(channelId);
            if (channel == null) {
               return null;
            } else {
               StageInstance instance = channel.getStageInstance();
               channel.setStageInstance((StageInstance)null);
               if (instance != null) {
                  this.getJDA().handleEvent(new StageInstanceDeleteEvent(this.getJDA(), this.responseNumber, instance));
               }

               return null;
            }
         }
      }
   }
}
