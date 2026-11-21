package net.dv8tion.jda.api.interactions.commands;

import java.util.Arrays;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.internal.utils.Checks;

public class DefaultMemberPermissions {
   public static final DefaultMemberPermissions ENABLED = new DefaultMemberPermissions((Long)null);
   public static final DefaultMemberPermissions DISABLED = new DefaultMemberPermissions(0L);
   private final Long permissions;

   private DefaultMemberPermissions(@Nullable Long permissions) {
      this.permissions = permissions;
   }

   @Nullable
   public Long getPermissionsRaw() {
      return this.permissions;
   }

   @Nonnull
   public static DefaultMemberPermissions enabledFor(@Nonnull Collection<Permission> permissions) {
      Checks.noneNull(permissions, "Permissions");
      return permissions.isEmpty() ? ENABLED : enabledFor(Permission.getRaw(permissions));
   }

   @Nonnull
   public static DefaultMemberPermissions enabledFor(@Nonnull Permission... permissions) {
      return enabledFor(Arrays.asList(permissions));
   }

   @Nonnull
   public static DefaultMemberPermissions enabledFor(long permissions) {
      return new DefaultMemberPermissions(permissions);
   }
}
