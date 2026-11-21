package net.dv8tion.jda.api.events.guild.member.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.UpdateEvent;
import net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent;

public abstract class GenericGuildMemberUpdateEvent<T> extends GenericGuildMemberEvent implements UpdateEvent<Member, T> {
   protected final T previous;
   protected final T next;
   protected final String identifier;

   public GenericGuildMemberUpdateEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nullable T previous, @Nullable T next, @Nonnull String identifier) {
      super(api, responseNumber, member);
      this.previous = previous;
      this.next = next;
      this.identifier = identifier;
   }

   @Nonnull
   public String getPropertyIdentifier() {
      return this.identifier;
   }

   @Nonnull
   public Member getEntity() {
      return this.getMember();
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
