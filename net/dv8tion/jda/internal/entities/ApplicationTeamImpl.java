package net.dv8tion.jda.internal.entities;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.ApplicationTeam;
import net.dv8tion.jda.api.entities.TeamMember;
import net.dv8tion.jda.internal.utils.EntityString;

public class ApplicationTeamImpl implements ApplicationTeam {
   private final String iconId;
   private final List<TeamMember> members;
   private final long id;
   private final long ownerId;

   public ApplicationTeamImpl(String iconId, List<TeamMember> members, long id, long ownerId) {
      this.iconId = iconId;
      this.members = Collections.unmodifiableList(members);
      this.id = id;
      this.ownerId = ownerId;
   }

   public long getOwnerIdLong() {
      return this.ownerId;
   }

   public String getIconId() {
      return this.iconId;
   }

   @Nonnull
   public List<TeamMember> getMembers() {
      return this.members;
   }

   public long getIdLong() {
      return this.id;
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof ApplicationTeamImpl)) {
         return false;
      } else {
         ApplicationTeamImpl app = (ApplicationTeamImpl)obj;
         return app.id == this.id;
      }
   }

   public String toString() {
      return (new EntityString(this)).toString();
   }
}
