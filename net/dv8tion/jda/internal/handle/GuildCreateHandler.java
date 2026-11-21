package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;

public class GuildCreateHandler extends SocketHandler {
   public GuildCreateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      long id = content.getLong("id");
      GuildImpl guild = (GuildImpl)this.getJDA().getGuildById(id);
      if (guild == null) {
         this.getJDA().getGuildSetupController().onCreate(id, content);
      }

      return null;
   }
}
