package net.dv8tion.jda.api.events.user.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.UpdateEvent;
import net.dv8tion.jda.api.events.user.GenericUserEvent;

public abstract class GenericUserUpdateEvent<T> extends GenericUserEvent implements UpdateEvent<User, T> {
   protected final T previous;
   protected final T next;
   protected final String identifier;

   public GenericUserUpdateEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user, @Nullable T previous, @Nullable T next, @Nonnull String identifier) {
      super(api, responseNumber, user);
      this.previous = previous;
      this.next = next;
      this.identifier = identifier;
   }

   @Nonnull
   public User getEntity() {
      return this.getUser();
   }

   @Nonnull
   public String getPropertyIdentifier() {
      return this.identifier;
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
