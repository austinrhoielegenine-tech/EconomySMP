package net.dv8tion.jda.internal.managers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.StageInstance;
import net.dv8tion.jda.api.managers.StageInstanceManager;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;

public class StageInstanceManagerImpl extends ManagerBase<StageInstanceManager> implements StageInstanceManager {
   private final StageInstance instance;
   private String topic;

   public StageInstanceManagerImpl(StageInstance instance) {
      super(instance.getChannel().getJDA(), Route.StageInstances.UPDATE_INSTANCE.compile(instance.getChannel().getId()));
      this.instance = instance;
   }

   @Nonnull
   public StageInstance getStageInstance() {
      return this.instance;
   }

   @Nonnull
   public StageInstanceManager setTopic(@Nullable String topic) {
      if (topic != null) {
         topic = topic.trim();
         Checks.notLonger(topic, 120, "Topic");
         if (topic.isEmpty()) {
            topic = null;
         }
      }

      this.topic = topic;
      this.set |= 1L;
      return this;
   }

   protected RequestBody finalizeData() {
      DataObject body = DataObject.empty();
      if (this.shouldUpdate(1L) && this.topic != null) {
         body.put("topic", this.topic);
      }

      return this.getRequestBody(body);
   }
}
