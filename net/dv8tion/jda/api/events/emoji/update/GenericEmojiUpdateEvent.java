package net.dv8tion.jda.api.events.emoji.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.UpdateEvent;
import net.dv8tion.jda.api.events.emoji.GenericEmojiEvent;

public abstract class GenericEmojiUpdateEvent<T> extends GenericEmojiEvent implements UpdateEvent<RichCustomEmoji, T> {
   protected final T previous;
   protected final T next;
   protected final String identifier;

   public GenericEmojiUpdateEvent(@Nonnull JDA api, long responseNumber, @Nonnull RichCustomEmoji emoji, @Nullable T previous, @Nullable T next, @Nonnull String identifier) {
      super(api, responseNumber, emoji);
      this.previous = previous;
      this.next = next;
      this.identifier = identifier;
   }

   @Nonnull
   public RichCustomEmoji getEntity() {
      return this.getEmoji();
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
