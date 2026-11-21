package net.dv8tion.jda.internal.handle;

import java.util.Objects;
import net.dv8tion.jda.api.entities.StageInstance;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.events.stage.update.StageInstanceUpdatePrivacyLevelEvent;
import net.dv8tion.jda.api.events.stage.update.StageInstanceUpdateTopicEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;

public class StageInstanceUpdateHandler extends SocketHandler {
   public StageInstanceUpdateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      long guildId = content.getUnsignedLong("guild_id", 0L);
      if (this.getJDA().getGuildSetupController().isLocked(guildId)) {
         return guildId;
      } else {
         GuildImpl guild = (GuildImpl)this.getJDA().getGuildById(guildId);
         if (guild == null) {
            EventCache.LOG.debug((String)"Caching STAGE_INSTANCE_UPDATE for uncached guild with id {}", (Object)guildId);
            this.getJDA().getEventCache().cache(EventCache.Type.GUILD, guildId, this.responseNumber, this.allContent, this::handle);
            return null;
         } else {
            StageChannel channel = this.getJDA().getStageChannelById(content.getUnsignedLong("channel_id"));
            if (channel == null) {
               return null;
            } else {
               StageInstance oldInstance = channel.getStageInstance();
               if (oldInstance == null) {
                  return null;
               } else {
                  String oldTopic = oldInstance.getTopic();
                  StageInstance.PrivacyLevel oldLevel = oldInstance.getPrivacyLevel();
                  StageInstance newInstance = this.getJDA().getEntityBuilder().createStageInstance(guild, content);
                  if (newInstance == null) {
                     return null;
                  } else {
                     if (!Objects.equals(oldTopic, newInstance.getTopic())) {
                        this.getJDA().handleEvent(new StageInstanceUpdateTopicEvent(this.getJDA(), this.responseNumber, newInstance, oldTopic));
                     }

                     if (oldLevel != newInstance.getPrivacyLevel()) {
                        this.getJDA().handleEvent(new StageInstanceUpdatePrivacyLevelEvent(this.getJDA(), this.responseNumber, newInstance, oldLevel));
                     }

                     return null;
                  }
               }
            }
         }
      }
   }
}
