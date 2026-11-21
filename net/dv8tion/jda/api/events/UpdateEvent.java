package net.dv8tion.jda.api.events;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface UpdateEvent<E, T> extends GenericEvent {
   @Nonnull
   default Class<E> getEntityType() {
      return this.getEntity().getClass();
   }

   @Nonnull
   String getPropertyIdentifier();

   @Nonnull
   E getEntity();

   @Nullable
   T getOldValue();

   @Nullable
   T getNewValue();
}
