package net.dv8tion.jda.api.requests.restaction;

import java.util.Collection;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.internal.utils.Checks;

public interface PermissionOverrideAction extends AuditableRestAction<PermissionOverride> {
   @Nonnull
   @CheckReturnValue
   PermissionOverrideAction setCheck(@Nullable BooleanSupplier var1);

   @Nonnull
   @CheckReturnValue
   PermissionOverrideAction timeout(long var1, @Nonnull TimeUnit var3);

   @Nonnull
   @CheckReturnValue
   PermissionOverrideAction deadline(long var1);

   @Nonnull
   @CheckReturnValue
   default PermissionOverrideAction reset() {
      return this.resetAllow().resetDeny();
   }

   @Nonnull
   @CheckReturnValue
   PermissionOverrideAction resetAllow();

   @Nonnull
   @CheckReturnValue
   PermissionOverrideAction resetDeny();

   @Nonnull
   IPermissionContainer getChannel();

   @Nullable
   Role getRole();

   @Nullable
   Member getMember();

   @Nonnull
   default Guild getGuild() {
      return this.getChannel().getGuild();
   }

   long getAllowed();

   @Nonnull
   default EnumSet<Permission> getAllowedPermissions() {
      return Permission.getPermissions(this.getAllowed());
   }

   long getDenied();

   @Nonnull
   default EnumSet<Permission> getDeniedPermissions() {
      return Permission.getPermissions(this.getDenied());
   }

   long getInherited();

   @Nonnull
   default EnumSet<Permission> getInheritedPermissions() {
      return Permission.getPermissions(this.getInherited());
   }

   boolean isMember();

   boolean isRole();

   @Nonnull
   @CheckReturnValue
   PermissionOverrideAction setAllowed(long var1);

   @Nonnull
   @CheckReturnValue
   default PermissionOverrideAction setAllowed(@Nullable Collection<Permission> permissions) {
      if (permissions != null && !permissions.isEmpty()) {
         Checks.noneNull(permissions, "Permissions");
         return this.setAllowed(Permission.getRaw(permissions));
      } else {
         return this.setAllowed(0L);
      }
   }

   @Nonnull
   @CheckReturnValue
   default PermissionOverrideAction setAllowed(@Nullable Permission... permissions) {
      if (permissions != null && permissions.length != 0) {
         Checks.noneNull(permissions, "Permissions");
         return this.setAllowed(Permission.getRaw(permissions));
      } else {
         return this.setAllowed(0L);
      }
   }

   @Nonnull
   @CheckReturnValue
   PermissionOverrideAction grant(long var1);

   @Nonnull
   @CheckReturnValue
   default PermissionOverrideAction grant(@Nonnull Collection<Permission> permissions) {
      return this.grant(Permission.getRaw(permissions));
   }

   @Nonnull
   @CheckReturnValue
   default PermissionOverrideAction grant(@Nonnull Permission... permissions) {
      return this.grant(Permission.getRaw(permissions));
   }

   @Nonnull
   @CheckReturnValue
   PermissionOverrideAction setDenied(long var1);

   @Nonnull
   @CheckReturnValue
   default PermissionOverrideAction setDenied(@Nullable Collection<Permission> permissions) {
      if (permissions != null && !permissions.isEmpty()) {
         Checks.noneNull(permissions, "Permissions");
         return this.setDenied(Permission.getRaw(permissions));
      } else {
         return this.setDenied(0L);
      }
   }

   @Nonnull
   @CheckReturnValue
   default PermissionOverrideAction setDenied(@Nullable Permission... permissions) {
      if (permissions != null && permissions.length != 0) {
         Checks.noneNull(permissions, "Permissions");
         return this.setDenied(Permission.getRaw(permissions));
      } else {
         return this.setDenied(0L);
      }
   }

   @Nonnull
   @CheckReturnValue
   PermissionOverrideAction deny(long var1);

   @Nonnull
   @CheckReturnValue
   default PermissionOverrideAction deny(@Nonnull Collection<Permission> permissions) {
      return this.deny(Permission.getRaw(permissions));
   }

   @Nonnull
   @CheckReturnValue
   default PermissionOverrideAction deny(@Nonnull Permission... permissions) {
      return this.deny(Permission.getRaw(permissions));
   }

   @Nonnull
   @CheckReturnValue
   PermissionOverrideAction clear(long var1);

   @Nonnull
   @CheckReturnValue
   default PermissionOverrideAction clear(@Nonnull Collection<Permission> permissions) {
      return this.clear(Permission.getRaw(permissions));
   }

   @Nonnull
   @CheckReturnValue
   default PermissionOverrideAction clear(@Nonnull Permission... permissions) {
      return this.clear(Permission.getRaw(permissions));
   }

   @Nonnull
   @CheckReturnValue
   PermissionOverrideAction setPermissions(long var1, long var3);

   @Nonnull
   @CheckReturnValue
   default PermissionOverrideAction setPermissions(@Nullable Collection<Permission> grantPermissions, @Nullable Collection<Permission> denyPermissions) {
      return this.setAllowed(grantPermissions).setDenied(denyPermissions);
   }
}
