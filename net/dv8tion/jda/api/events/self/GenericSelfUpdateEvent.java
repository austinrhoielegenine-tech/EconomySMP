package net.dv8tion.jda.api.events.self;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.UpdateEvent;

public abstract class GenericSelfUpdateEvent<T> extends Event implements UpdateEvent<SelfUser, T> {
   protected final T previous;
   protected final T next;
   protected final String identifier;

   public GenericSelfUpdateEvent(@Nonnull JDA api, long responseNumber, @Nullable T previous, @Nullable T next, @Nonnull String identifier) {
      super(api, responseNumber);
      this.previous = previous;
      this.next = next;
      this.identifier = identifier;
   }

   @Nonnull
   public SelfUser getSelfUser() {
      return this.api.getSelfUser();
   }

   @Nonnull
   public SelfUser getEntity() {
      return this.getSelfUser();
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
