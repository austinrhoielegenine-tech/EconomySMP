package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.thread.ThreadRevealedEvent;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;

public class ThreadListSyncHandler extends SocketHandler {
   public ThreadListSyncHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      long guildId = content.getLong("guild_id");
      if (this.api.getGuildSetupController().isLocked(guildId)) {
         return guildId;
      } else {
         EntityBuilder entityBuilder = this.api.getEntityBuilder();
         DataArray threadsArrayJson = content.getArray("threads");

         for(int i = 0; i < threadsArrayJson.length(); ++i) {
            DataObject threadJson = threadsArrayJson.getObject(i);

            try {
               ThreadChannel thread = entityBuilder.createThreadChannel(threadJson, guildId);
               this.api.handleEvent(new ThreadRevealedEvent(this.api, this.responseNumber, thread));
            } catch (IllegalArgumentException ex) {
               if (!"MISSING_CHANNEL".equals(ex.getMessage())) {
                  throw ex;
               }

               EntityBuilder.LOG.debug((String)"Discarding thread on sync because of missing parent channel cache. JSON: {}", (Object)threadJson);
            }
         }

         return null;
      }
   }
}
