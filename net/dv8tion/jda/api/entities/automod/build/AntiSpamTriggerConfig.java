package net.dv8tion.jda.api.entities.automod.build;

import net.dv8tion.jda.api.entities.automod.AutoModTriggerType;

public class AntiSpamTriggerConfig extends AbstractTriggerConfig<AntiSpamTriggerConfig> {
   protected AntiSpamTriggerConfig() {
      super(AutoModTriggerType.SPAM);
   }
}
