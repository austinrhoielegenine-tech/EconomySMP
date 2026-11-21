package net.dv8tion.jda.api.exceptions;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.internal.utils.Checks;

public class PermissionException extends RuntimeException {
   private final Permission permission;

   public PermissionException(String reason) {
      this(Permission.UNKNOWN, reason);
   }

   protected PermissionException(@Nonnull Permission permission) {
      this(permission, "Cannot perform action due to a lack of Permission. Missing permission: " + permission.toString());
   }

   protected PermissionException(@Nonnull Permission permission, String reason) {
      super(reason);
      Checks.notNull(permission, "permission");
      this.permission = permission;
   }

   @Nonnull
   public Permission getPermission() {
      return this.permission;
   }
}
