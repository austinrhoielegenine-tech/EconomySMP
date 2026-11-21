package net.dv8tion.jda.api.events.stage.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.StageInstance;
import net.dv8tion.jda.api.events.UpdateEvent;
import net.dv8tion.jda.api.events.stage.GenericStageInstanceEvent;

public abstract class GenericStageInstanceUpdateEvent<T> extends GenericStageInstanceEvent implements UpdateEvent<StageInstance, T> {
   protected final T previous;
   protected final T next;
   protected final String identifier;

   public GenericStageInstanceUpdateEvent(@Nonnull JDA api, long responseNumber, @Nonnull StageInstance stageInstance, T previous, T next, String identifier) {
      super(api, responseNumber, stageInstance);
      this.previous = previous;
      this.next = next;
      this.identifier = identifier;
   }

   @Nonnull
   public String getPropertyIdentifier() {
      return this.identifier;
   }

   @Nonnull
   public StageInstance getEntity() {
      return this.getInstance();
   }

   @Nullable
   public T getOldValue() {
      return this.previous;
   }

   @Nullable
   public T getNewValue() {
      return this.next;
   }
}
