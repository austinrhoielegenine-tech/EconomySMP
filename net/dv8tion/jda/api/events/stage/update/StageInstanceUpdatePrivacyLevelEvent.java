package net.dv8tion.jda.api.events.stage.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.StageInstance;

public class StageInstanceUpdatePrivacyLevelEvent extends GenericStageInstanceUpdateEvent<StageInstance.PrivacyLevel> {
   public static final String IDENTIFIER = "privacy_level";

   public StageInstanceUpdatePrivacyLevelEvent(@Nonnull JDA api, long responseNumber, @Nonnull StageInstance stageInstance, @Nonnull StageInstance.PrivacyLevel previous) {
      super(api, responseNumber, stageInstance, previous, stageInstance.getPrivacyLevel(), "privacy_level");
   }

   @Nonnull
   public StageInstance.PrivacyLevel getOldValue() {
      return (StageInstance.PrivacyLevel)super.getOldValue();
   }

   @Nonnull
   public StageInstance.PrivacyLevel getNewValue() {
      return (StageInstance.PrivacyLevel)super.getNewValue();
   }
}
