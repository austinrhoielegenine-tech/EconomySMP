package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.automod.AutoModRule;
import net.dv8tion.jda.api.events.automod.AutoModRuleCreateEvent;
import net.dv8tion.jda.api.events.automod.AutoModRuleDeleteEvent;
import net.dv8tion.jda.api.events.automod.AutoModRuleUpdateEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.automod.AutoModRuleImpl;

public class AutoModRuleHandler extends SocketHandler {
   private final String type;

   public AutoModRuleHandler(JDAImpl api, String type) {
      super(api);
      this.type = type;
   }

   protected Long handleInternally(DataObject content) {
      long guildId = content.getUnsignedLong("guild_id");
      if (this.api.getGuildSetupController().isLocked(guildId)) {
         return guildId;
      } else {
         Guild guild = this.api.getGuildById(guildId);
         if (guild == null) {
            this.api.getEventCache().cache(EventCache.Type.GUILD, guildId, this.responseNumber, this.allContent, this::handle);
            EventCache.LOG.debug((String)"Received a AUTO_MODERATION_RULE_{} for a guild that is not yet cached. JSON: {}", (Object)this.type, (Object)content);
            return null;
         } else {
            AutoModRule rule = AutoModRuleImpl.fromData(guild, content);
            switch (this.type) {
               case "CREATE":
                  this.api.handleEvent(new AutoModRuleCreateEvent(this.api, this.responseNumber, rule));
                  break;
               case "UPDATE":
                  this.api.handleEvent(new AutoModRuleUpdateEvent(this.api, this.responseNumber, rule));
                  break;
               case "DELETE":
                  this.api.handleEvent(new AutoModRuleDeleteEvent(this.api, this.responseNumber, rule));
            }

            return null;
         }
      }
   }
}
