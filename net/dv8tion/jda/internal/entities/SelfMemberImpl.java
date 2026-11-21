package net.dv8tion.jda.internal.entities;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.SelfMember;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.managers.SelfMemberManager;
import net.dv8tion.jda.internal.managers.SelfMemberManagerImpl;

public class SelfMemberImpl extends MemberImpl implements SelfMember {
   public SelfMemberImpl(GuildImpl guild, SelfUser user) {
      super(guild, user);
   }

   @Nonnull
   public SelfUser getUser() {
      return (SelfUser)super.getUser();
   }

   @Nonnull
   @CheckReturnValue
   public SelfMemberManager getManager() {
      return new SelfMemberManagerImpl(this);
   }
}
