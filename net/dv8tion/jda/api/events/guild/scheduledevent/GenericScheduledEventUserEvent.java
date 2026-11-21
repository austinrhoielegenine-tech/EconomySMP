package net.dv8tion.jda.api.events.guild.scheduledevent;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;

public abstract class GenericScheduledEventUserEvent extends GenericScheduledEventGatewayEvent {
   private final long userId;

   public GenericScheduledEventUserEvent(@Nonnull JDA api, long responseNumber, @Nonnull ScheduledEvent scheduledEvent, long userId) {
      super(api, responseNumber, scheduledEvent);
      this.userId = userId;
   }

   public long getUserIdLong() {
      return this.userId;
   }

   @Nonnull
   public String getUserId() {
      return Long.toUnsignedString(this.userId);
   }

   @Nullable
   public User getUser() {
      return this.api.getUserById(this.userId);
   }

   @Nullable
   public Member getMember() {
      return this.guild.getMemberById(this.userId);
   }

   @Nonnull
   @CheckReturnValue
   public CacheRestAction<User> retrieveUser() {
      return this.getJDA().retrieveUserById(this.getUserIdLong());
   }

   @Nonnull
   @CheckReturnValue
   public CacheRestAction<Member> retrieveMember() {
      return this.getGuild().retrieveMemberById(this.getUserIdLong());
   }
}
