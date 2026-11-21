package net.dv8tion.jda.api.managers;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
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

public interface RoleManager extends Manager<RoleManager> {
   long NAME = 1L;
   long COLOR = 2L;
   long PERMISSION = 4L;
   long HOIST = 8L;
   long MENTIONABLE = 16L;
   long ICON = 32L;

   @Nonnull
   @CheckReturnValue
   RoleManager reset(long var1);

   @Nonnull
   @CheckReturnValue
   RoleManager reset(@Nonnull long... var1);

   @Nonnull
   Role getRole();

   @Nonnull
   default Guild getGuild() {
      return this.getRole().getGuild();
   }

   @Nonnull
   @CheckReturnValue
   RoleManager setName(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   RoleManager setPermissions(long var1);

   @Nonnull
   @CheckReturnValue
   default RoleManager setPermissions(@Nonnull Permission... permissions) {
      Checks.notNull(permissions, "Permissions");
      return this.setPermissions((Collection)Arrays.asList(permissions));
   }

   @Nonnull
   @CheckReturnValue
   default RoleManager setPermissions(@Nonnull Collection<Permission> permissions) {
      Checks.noneNull(permissions, "Permissions");
      return this.setPermissions(Permission.getRaw(permissions));
   }

   @Nonnull
   @CheckReturnValue
   default RoleManager setColor(@Nullable Color color) {
      return this.setColor(color == null ? 536870911 : color.getRGB());
   }

   @Nonnull
   @CheckReturnValue
   RoleManager setColor(int var1);

   @Nonnull
   @CheckReturnValue
   RoleManager setColors(@Nullable RoleColors var1);

   @Nonnull
   @CheckReturnValue
   default RoleManager setGradientColors(@Nonnull Color primary, @Nonnull Color secondary) {
      Checks.notNull(primary, "Primary");
      Checks.notNull(secondary, "Secondary");
      return this.setGradientColors(primary.getRGB(), secondary.getRGB());
   }

   @Nonnull
   @CheckReturnValue
   RoleManager setGradientColors(int var1, int var2);

   @Nonnull
   @CheckReturnValue
   RoleManager useHolographicStyle();

   @Nonnull
   @CheckReturnValue
   RoleManager setHoisted(boolean var1);

   @Nonnull
   @CheckReturnValue
   RoleManager setMentionable(boolean var1);

   @Nonnull
   @CheckReturnValue
   RoleManager setIcon(@Nullable Icon var1);

   @Nonnull
   @CheckReturnValue
   RoleManager setIcon(@Nullable String var1);

   @Nonnull
   @CheckReturnValue
   default RoleManager setIcon(@Nullable UnicodeEmoji emoji) {
      return this.setIcon(emoji == null ? null : emoji.getFormatted());
   }

   @Nonnull
   @CheckReturnValue
   default RoleManager givePermissions(@Nonnull Permission... perms) {
      Checks.notNull(perms, "Permissions");
      return this.givePermissions((Collection)Arrays.asList(perms));
   }

   @Nonnull
   @CheckReturnValue
   RoleManager givePermissions(@Nonnull Collection<Permission> var1);

   @Nonnull
   @CheckReturnValue
   default RoleManager revokePermissions(@Nonnull Permission... perms) {
      Checks.notNull(perms, "Permissions");
      return this.revokePermissions((Collection)Arrays.asList(perms));
   }

   @Nonnull
   @CheckReturnValue
   RoleManager revokePermissions(@Nonnull Collection<Permission> var1);
}
