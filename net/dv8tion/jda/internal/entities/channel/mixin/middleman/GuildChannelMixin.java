package net.dv8tion.jda.internal.entities.channel.mixin.middleman;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.MissingAccessException;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.internal.entities.channel.mixin.ChannelMixin;
import net.dv8tion.jda.internal.entities.detached.mixin.IDetachableEntityMixin;
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl;

public interface GuildChannelMixin<T extends GuildChannelMixin<T>> extends GuildChannel, GuildChannelUnion, ChannelMixin<T>, IDetachableEntityMixin {
   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> delete() {
      this.checkCanAccess();
      this.checkCanManage();
      Route.CompiledRoute route = Route.Channels.DELETE_CHANNEL.compile(this.getId());
      return new AuditableRestActionImpl<Void>(this.getJDA(), route);
   }

   default boolean hasPermission(Permission permission) {
      IPermissionContainer permChannel = this.getPermissionContainer();
      return this.getGuild().getSelfMember().hasPermission(permChannel, new Permission[]{permission});
   }

   default void checkPermission(Permission permission) {
      this.checkPermission(permission, (String)null);
   }

   default void checkPermission(Permission permission, String message) {
      if (!this.hasPermission(permission)) {
         if (message != null) {
            throw new InsufficientPermissionException(this, permission, message);
         } else {
            throw new InsufficientPermissionException(this, permission);
         }
      }
   }

   default void checkCanManage() {
      this.checkPermission(Permission.MANAGE_CHANNEL);
   }

   default void checkCanAccess() {
      this.checkAttached();
      if (!this.hasPermission(Permission.VIEW_CHANNEL)) {
         throw new MissingAccessException(this, Permission.VIEW_CHANNEL);
      }
   }
}
