package net.dv8tion.jda.api.events.user.update;

import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class UserUpdateActivityOrderEvent extends GenericUserUpdateEvent<List<Activity>> implements GenericUserPresenceEvent {
   public static final String IDENTIFIER = "activity_order";
   private final Member member;

   public UserUpdateActivityOrderEvent(@Nonnull JDA api, long responseNumber, @Nonnull List<Activity> previous, @Nonnull Member member) {
      super(api, responseNumber, member.getUser(), previous, member.getActivities(), "activity_order");
      this.member = member;
   }

   @Nonnull
   public Guild getGuild() {
      return this.member.getGuild();
   }

   @Nonnull
   public Member getMember() {
      return this.member;
   }

   @Nonnull
   public List<Activity> getOldValue() {
      return (List)super.getOldValue();
   }

   @Nonnull
   public List<Activity> getNewValue() {
      return (List)super.getNewValue();
   }
}
