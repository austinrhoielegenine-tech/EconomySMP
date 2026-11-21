package net.dv8tion.jda.api.events.session;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.handle.GuildSetupController;

public class ReadyEvent extends GenericSessionEvent {
   private final int availableGuilds = (int)this.getJDA().getGuildCache().size();
   private final int unavailableGuilds;

   public ReadyEvent(@Nonnull JDA api) {
      super(api, SessionState.READY);
      GuildSetupController setupController = ((JDAImpl)this.getJDA()).getGuildSetupController();
      this.unavailableGuilds = setupController.getSetupNodes(GuildSetupController.Status.UNAVAILABLE).size() + setupController.getUnavailableGuilds().size();
   }

   public int getGuildAvailableCount() {
      return this.availableGuilds;
   }

   public int getGuildUnavailableCount() {
      return this.unavailableGuilds;
   }

   public int getGuildTotalCount() {
      return this.getGuildAvailableCount() + this.getGuildUnavailableCount();
   }
}
