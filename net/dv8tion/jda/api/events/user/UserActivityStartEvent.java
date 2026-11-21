package net.dv8tion.jda.api.events.user;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.user.update.GenericUserPresenceEvent;

public class UserActivityStartEvent extends GenericUserEvent implements GenericUserPresenceEvent {
   private final Activity newActivity;
   private final Member member;

   public UserActivityStartEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nonnull Activity newActivity) {
      super(api, responseNumber, member.getUser());
      this.newActivity = newActivity;
      this.member = member;
   }

   public Activity getNewActivity() {
      return this.newActivity;
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
