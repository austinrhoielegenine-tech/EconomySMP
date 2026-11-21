package net.dv8tion.jda.api.entities;

import java.util.Collection;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.internal.utils.Checks;

public interface IPermissionHolder extends ISnowflake {
   @Nonnull
   Guild getGuild();

   @Nonnull
   EnumSet<Permission> getPermissions();

   @Nonnull
   EnumSet<Permission> getPermissions(@Nonnull GuildChannel var1);

   @Nonnull
   EnumSet<Permission> getPermissionsExplicit();

   @Nonnull
   EnumSet<Permission> getPermissionsExplicit(@Nonnull GuildChannel var1);

   boolean hasPermission(@Nonnull Permission... var1);

   default boolean hasPermission(@Nonnull Collection<Permission> permissions) {
      Checks.notNull(permissions, "Permission Collection");
      return this.hasPermission((Permission[])permissions.toArray(Permission.EMPTY_PERMISSIONS));
   }

   boolean hasPermission(@Nonnull GuildChannel var1, @Nonnull Permission... var2);

   default boolean hasPermission(@Nonnull GuildChannel channel, @Nonnull Collection<Permission> permissions) {
      Checks.notNull(permissions, "Permission Collection");
      return this.hasPermission(channel, (Permission[])permissions.toArray(Permission.EMPTY_PERMISSIONS));
   }

   default boolean hasAccess(@Nonnull GuildChannel channel) {
      Checks.notNull(channel, "Channel");
      return channel.getType().isAudio() ? this.hasPermission(channel, Permission.VOICE_CONNECT, Permission.VIEW_CHANNEL) : this.hasPermission(channel, Permission.VIEW_CHANNEL);
   }

   boolean canSync(@Nonnull IPermissionContainer var1, @Nonnull IPermissionContainer var2);

   boolean canSync(@Nonnull IPermissionContainer var1);
}
