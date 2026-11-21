package net.dv8tion.jda.internal.entities;

import java.util.Objects;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.TeamMember;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.utils.EntityString;

public class TeamMemberImpl implements TeamMember {
   private final User user;
   private final TeamMember.MembershipState state;
   private final TeamMember.RoleType roleType;
   private final long teamId;

   public TeamMemberImpl(User user, TeamMember.MembershipState state, TeamMember.RoleType roleType, long teamId) {
      this.user = user;
      this.state = state;
      this.roleType = roleType;
      this.teamId = teamId;
   }

   @Nonnull
   public User getUser() {
      return this.user;
   }

   @Nonnull
   public TeamMember.MembershipState getMembershipState() {
      return this.state;
   }

   @Nonnull
   public TeamMember.RoleType getRoleType() {
      return this.roleType;
   }

   public long getTeamIdLong() {
      return this.teamId;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.user, this.teamId});
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof TeamMemberImpl)) {
         return false;
      } else {
         TeamMemberImpl member = (TeamMemberImpl)obj;
         return member.teamId == this.teamId && member.user.equals(this.user);
      }
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("teamId", this.getTeamId()).addMetadata("user", this.user).toString();
   }
}
