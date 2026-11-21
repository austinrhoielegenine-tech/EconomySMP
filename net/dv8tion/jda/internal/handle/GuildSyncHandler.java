package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;

public class GuildSyncHandler extends SocketHandler {
   public GuildSyncHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      long guildId = content.getLong("id");
      this.getJDA().getGuildSetupController().onSync(guildId, content);
      return null;
   }
}
