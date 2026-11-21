package net.dv8tion.jda.internal.entities;

import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.RoleMemberCount;

public class RoleMemberCountImpl implements RoleMemberCount {
   private final Guild guild;
   private final long roleId;
   private final int memberCount;

   public RoleMemberCountImpl(Guild guild, long roleId, int memberCount) {
      this.guild = guild;
      this.roleId = roleId;
      this.memberCount = memberCount;
   }

   public long getRoleIdLong() {
      return this.roleId;
   }

   @Nullable
   public Role getRole() {
      return this.guild.getRoleById(this.roleId);
   }

   public int getMemberCount() {
      return this.memberCount;
   }
}
