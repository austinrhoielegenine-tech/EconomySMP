package net.dv8tion.jda.api.entities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface RoleMemberCount {
   long getRoleIdLong();

   @Nonnull
   default String getRoleId() {
      return Long.toUnsignedString(this.getRoleIdLong());
   }

   @Nullable
   Role getRole();

   int getMemberCount();
}
