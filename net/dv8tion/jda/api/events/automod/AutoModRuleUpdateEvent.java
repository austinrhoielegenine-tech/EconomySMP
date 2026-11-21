package net.dv8tion.jda.api.events.automod;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.automod.AutoModRule;

public class AutoModRuleUpdateEvent extends GenericAutoModRuleEvent {
   public AutoModRuleUpdateEvent(@Nonnull JDA api, long responseNumber, @Nonnull AutoModRule rule) {
      super(api, responseNumber, rule);
   }
}
