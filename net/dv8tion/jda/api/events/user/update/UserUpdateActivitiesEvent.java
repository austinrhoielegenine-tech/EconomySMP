package net.dv8tion.jda.api.events.user.update;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class UserUpdateActivitiesEvent extends GenericUserUpdateEvent<List<Activity>> implements GenericUserPresenceEvent {
   public static final String IDENTIFIER = "activities";
   private final Member member;

   public UserUpdateActivitiesEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nullable List<Activity> previous) {
      super(api, responseNumber, member.getUser(), previous, member.getActivities(), "activities");
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
}
