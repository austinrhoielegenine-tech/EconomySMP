package net.dv8tion.jda.api.events.stage.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.StageInstance;

public class StageInstanceUpdateTopicEvent extends GenericStageInstanceUpdateEvent<String> {
   public static final String IDENTIFIER = "topic";

   public StageInstanceUpdateTopicEvent(@Nonnull JDA api, long responseNumber, @Nonnull StageInstance stageInstance, String previous) {
      super(api, responseNumber, stageInstance, previous, stageInstance.getTopic(), "topic");
   }

   @Nonnull
   public String getOldValue() {
      return (String)super.getOldValue();
   }

   @Nonnull
   public String getNewValue() {
      return (String)super.getNewValue();
   }
}
