package net.dv8tion.jda.api.requests.restaction;

import java.awt.Color;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.RoleColors;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.internal.utils.Checks;

public interface RoleAction extends AuditableRestAction<Role> {
   @Nonnull
   @CheckReturnValue
   RoleAction setCheck(@Nullable BooleanSupplier var1);

   @Nonnull
   @CheckReturnValue
   RoleAction timeout(long var1, @Nonnull TimeUnit var3);

   @Nonnull
   @CheckReturnValue
   RoleAction deadline(long var1);

   @Nonnull
   Guild getGuild();

   @Nonnull
   @CheckReturnValue
   RoleAction setName(@Nullable String var1);

   @Nonnull
   @CheckReturnValue
   RoleAction setHoisted(@Nullable Boolean var1);

   @Nonnull
   @CheckReturnValue
   RoleAction setMentionable(@Nullable Boolean var1);

   @Nonnull
   @CheckReturnValue
   default RoleAction setColor(@Nullable Color color) {
      return this.setColor(color != null ? color.getRGB() : null);
   }

   @Nonnull
   @CheckReturnValue
   RoleAction setColor(@Nullable Integer var1);

   @Nonnull
   @CheckReturnValue
   RoleAction setColors(@Nullable RoleColors var1);

   @Nonnull
   @CheckReturnValue
   default RoleAction setGradientColors(@Nonnull Color primary, @Nonnull Color secondary) {
      Checks.notNull(primary, "Primary");
      Checks.notNull(secondary, "Secondary");
      return this.setGradientColors(primary.getRGB(), secondary.getRGB());
   }

   @Nonnull
   @CheckReturnValue
   RoleAction setGradientColors(int var1, int var2);

   @Nonnull
   @CheckReturnValue
   RoleAction useHolographicStyle();

   @Nonnull
   @CheckReturnValue
   default RoleAction setPermissions(@Nullable Permission... permissions) {
      if (permissions != null) {
         Checks.noneNull(permissions, "Permissions");
      }

      return this.setPermissions(permissions == null ? null : Permission.getRaw(permissions));
   }

   @Nonnull
   @CheckReturnValue
   default RoleAction setPermissions(@Nullable Collection<Permission> permissions) {
      if (permissions != null) {
         Checks.noneNull(permissions, "Permissions");
      }

      return this.setPermissions(permissions == null ? null : Permission.getRaw(permissions));
   }

   @Nonnull
   @CheckReturnValue
   RoleAction setPermissions(@Nullable Long var1);

   @Nonnull
   @CheckReturnValue
   RoleAction setIcon(@Nullable Icon var1);

   @Nonnull
   @CheckReturnValue
   RoleAction setIcon(@Nullable String var1);

   @Nonnull
   @CheckReturnValue
   default RoleAction setIcon(@Nullable UnicodeEmoji emoji) {
      return this.setIcon(emoji == null ? null : emoji.getFormatted());
   }
}
