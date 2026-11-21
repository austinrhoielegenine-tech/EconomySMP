package net.dv8tion.jda.api.managers.channel.attribute;

import java.util.Collection;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.managers.channel.ChannelManager;

public interface IPermissionContainerManager<T extends IPermissionContainer, M extends IPermissionContainerManager<T, M>> extends ChannelManager<T, M> {
   @Nonnull
   @CheckReturnValue
   M clearOverridesAdded();

   @Nonnull
   @CheckReturnValue
   M clearOverridesRemoved();

   @Nonnull
   @CheckReturnValue
   M putPermissionOverride(@Nonnull IPermissionHolder var1, long var2, long var4);

   @Nonnull
   @CheckReturnValue
   default M putPermissionOverride(@Nonnull IPermissionHolder permHolder, @Nullable Collection<Permission> allow, @Nullable Collection<Permission> deny) {
      long allowRaw = allow == null ? 0L : Permission.getRaw(allow);
      long denyRaw = deny == null ? 0L : Permission.getRaw(deny);
      return (M)this.putPermissionOverride(permHolder, allowRaw, denyRaw);
   }

   @Nonnull
   @CheckReturnValue
   M putRolePermissionOverride(long var1, long var3, long var5);

   @Nonnull
   @CheckReturnValue
   default M putRolePermissionOverride(long roleId, @Nullable Collection<Permission> allow, @Nullable Collection<Permission> deny) {
      long allowRaw = allow == null ? 0L : Permission.getRaw(allow);
      long denyRaw = deny == null ? 0L : Permission.getRaw(deny);
      return (M)this.putRolePermissionOverride(roleId, allowRaw, denyRaw);
   }

   @Nonnull
   @CheckReturnValue
   M putMemberPermissionOverride(long var1, long var3, long var5);

   @Nonnull
   @CheckReturnValue
   default M putMemberPermissionOverride(long memberId, @Nullable Collection<Permission> allow, @Nullable Collection<Permission> deny) {
      long allowRaw = allow == null ? 0L : Permission.getRaw(allow);
      long denyRaw = deny == null ? 0L : Permission.getRaw(deny);
      return (M)this.putMemberPermissionOverride(memberId, allowRaw, denyRaw);
   }

   @Nonnull
   @CheckReturnValue
   M removePermissionOverride(@Nonnull IPermissionHolder var1);

   @Nonnull
   @CheckReturnValue
   M removePermissionOverride(long var1);
}
