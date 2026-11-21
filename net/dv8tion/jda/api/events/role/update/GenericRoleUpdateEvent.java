package net.dv8tion.jda.api.events.role.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.UpdateEvent;
import net.dv8tion.jda.api.events.role.GenericRoleEvent;

public abstract class GenericRoleUpdateEvent<T> extends GenericRoleEvent implements UpdateEvent<Role, T> {
   protected final T previous;
   protected final T next;
   protected final String identifier;

   public GenericRoleUpdateEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role role, @Nullable T previous, @Nullable T next, @Nonnull String identifier) {
      super(api, responseNumber, role);
      this.previous = previous;
      this.next = next;
      this.identifier = identifier;
   }

   @Nonnull
   public Role getEntity() {
      return this.role;
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
