package net.dv8tion.jda.internal.entities.mixin;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.RoleColors;
import net.dv8tion.jda.internal.entities.detached.mixin.IDetachableEntityMixin;

public interface MemberMixin<T extends MemberMixin<T>> extends Member, IDetachableEntityMixin {
   T setNickname(String var1);

   T setAvatarId(String var1);

   T setJoinDate(long var1);

   T setBoostDate(long var1);

   T setTimeOutEnd(long var1);

   T setPending(boolean var1);

   T setFlags(int var1);

   @Nonnull
   default RoleColors getColors() {
      for(Role role : this.getRoles()) {
         RoleColors roleColors = role.getColors();
         if (!roleColors.isDefault()) {
            return roleColors;
         }
      }

      return RoleColors.DEFAULT;
   }
}
