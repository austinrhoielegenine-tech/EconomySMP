package net.dv8tion.jda.api.entities.automod.build;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.automod.AutoModTriggerType;
import net.dv8tion.jda.api.utils.data.DataObject;

public class AbstractTriggerConfig<B extends AbstractTriggerConfig<B>> implements TriggerConfig {
   protected final AutoModTriggerType type;

   protected AbstractTriggerConfig(AutoModTriggerType type) {
      this.type = type;
   }

   @Nonnull
   public AutoModTriggerType getType() {
      return this.type;
   }

   @Nonnull
   public DataObject toData() {
      return DataObject.empty();
   }
}
