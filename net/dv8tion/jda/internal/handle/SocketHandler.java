package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;

public abstract class SocketHandler {
   public static final ThreadLocal<DataObject> CURRENT_EVENT = new ThreadLocal();
   protected final JDAImpl api;
   protected long responseNumber;
   protected DataObject allContent;

   public SocketHandler(JDAImpl api) {
      this.api = api;
   }

   public final synchronized void handle(long responseTotal, DataObject o) {
      this.allContent = o;
      this.responseNumber = responseTotal;
      if (this.getJDA().isEventPassthrough()) {
         CURRENT_EVENT.set(o);
      }

      Long guildId = this.handleInternally(o.getObject("d"));
      if (guildId != null) {
         this.getJDA().getGuildSetupController().cacheEvent(guildId, o);
      }

      this.allContent = null;
      if (this.getJDA().isEventPassthrough()) {
         CURRENT_EVENT.set((Object)null);
      }

   }

   protected JDAImpl getJDA() {
      return this.api;
   }

   protected abstract Long handleInternally(DataObject var1);

   public static class NOPHandler extends SocketHandler {
      public NOPHandler(JDAImpl api) {
         super(api);
      }

      protected Long handleInternally(DataObject content) {
         return null;
      }
   }
}
