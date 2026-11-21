package net.dv8tion.jda.api.managers;

import java.util.Collection;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.unions.IPermissionContainerUnion;
import net.dv8tion.jda.internal.utils.Checks;

public interface PermOverrideManager extends Manager<PermOverrideManager> {
   long DENIED = 1L;
   long ALLOWED = 2L;
   long PERMISSIONS = 3L;

   @Nonnull
   @CheckReturnValue
   PermOverrideManager reset(long var1);

   @Nonnull
   @CheckReturnValue
   PermOverrideManager reset(@Nonnull long... var1);

   @Nonnull
   default Guild getGuild() {
      return this.getPermissionOverride().getGuild();
   }

   @Nonnull
   default IPermissionContainerUnion getChannel() {
      return this.getPermissionOverride().getChannel();
   }

   @Nonnull
   PermissionOverride getPermissionOverride();

   @Nonnull
   @CheckReturnValue
   PermOverrideManager grant(long var1);

   @Nonnull
   @CheckReturnValue
   default PermOverrideManager grant(@Nonnull Permission... permissions) {
      Checks.notNull(permissions, "Permissions");
      return this.grant(Permission.getRaw(permissions));
   }

   @Nonnull
   @CheckReturnValue
   default PermOverrideManager grant(@Nonnull Collection<Permission> permissions) {
      return this.grant(Permission.getRaw(permissions));
   }

   @Nonnull
   @CheckReturnValue
   PermOverrideManager deny(long var1);

   @Nonnull
   @CheckReturnValue
   default PermOverrideManager deny(@Nonnull Permission... permissions) {
      Checks.notNull(permissions, "Permissions");
      return this.deny(Permission.getRaw(permissions));
   }

   @Nonnull
   @CheckReturnValue
   default PermOverrideManager deny(@Nonnull Collection<Permission> permissions) {
      return this.deny(Permission.getRaw(permissions));
   }

   @Nonnull
   @CheckReturnValue
   PermOverrideManager clear(long var1);

   @Nonnull
   @CheckReturnValue
   default PermOverrideManager clear(@Nonnull Permission... permissions) {
      Checks.notNull(permissions, "Permissions");
      return this.clear(Permission.getRaw(permissions));
   }

   @Nonnull
   @CheckReturnValue
   default PermOverrideManager clear(@Nonnull Collection<Permission> permissions) {
      return this.clear(Permission.getRaw(permissions));
   }
}
