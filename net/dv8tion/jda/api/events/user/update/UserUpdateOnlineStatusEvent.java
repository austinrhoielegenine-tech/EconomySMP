package net.dv8tion.jda.api.events.user.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class UserUpdateOnlineStatusEvent extends GenericUserUpdateEvent<OnlineStatus> implements GenericUserPresenceEvent {
   public static final String IDENTIFIER = "status";
   private final Guild guild;
   private final Member member;

   public UserUpdateOnlineStatusEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nonnull OnlineStatus oldOnlineStatus) {
      super(api, responseNumber, member.getUser(), oldOnlineStatus, member.getOnlineStatus(), "status");
      this.guild = member.getGuild();
      this.member = member;
   }

   @Nonnull
   public Guild getGuild() {
      return this.guild;
   }

   @Nonnull
   public Member getMember() {
      return this.member;
   }

   @Nonnull
   public OnlineStatus getOldOnlineStatus() {
      return this.getOldValue();
   }

   @Nonnull
   public OnlineStatus getNewOnlineStatus() {
      return this.getNewValue();
   }

   @Nonnull
   public OnlineStatus getOldValue() {
      return (OnlineStatus)super.getOldValue();
   }

   @Nonnull
   public OnlineStatus getNewValue() {
      return (OnlineStatus)super.getNewValue();
   }
}
