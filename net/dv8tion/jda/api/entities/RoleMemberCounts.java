package net.dv8tion.jda.api.entities;

import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.Unmodifiable;

public interface RoleMemberCounts {
   int get(long var1);

   default int get(@Nonnull String roleId) {
      return this.get(MiscUtil.parseSnowflake(roleId));
   }

   default int get(@Nonnull Role role) {
      Checks.notNull(role, "Role");
      return this.get(role.getIdLong());
   }

   boolean contains(long var1);

   default boolean contains(@Nonnull String roleId) {
      return this.contains(MiscUtil.parseSnowflake(roleId));
   }

   default boolean contains(@Nonnull Role role) {
      Checks.notNull(role, "Role");
      return this.contains(role.getIdLong());
   }

   @Nonnull
   @Unmodifiable List<RoleMemberCount> asList();
}
