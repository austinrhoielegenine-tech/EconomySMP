package net.dv8tion.jda.internal.entities.channel.mixin.attribute;

import gnu.trove.map.TLongObjectMap;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.unions.IPermissionContainerUnion;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.GuildChannelMixin;
import net.dv8tion.jda.internal.requests.restaction.PermissionOverrideActionImpl;
import net.dv8tion.jda.internal.utils.Checks;

public interface IPermissionContainerMixin<T extends IPermissionContainerMixin<T>> extends IPermissionContainer, IPermissionContainerUnion, GuildChannelMixin<T> {
   default PermissionOverride getPermissionOverride(@Nonnull IPermissionHolder permissionHolder) {
      Checks.notNull(permissionHolder, "Permission Holder");
      Checks.check(permissionHolder.getGuild().equals(this.getGuild()), "Provided permission holder is not from the same guild as this channel!");
      TLongObjectMap<PermissionOverride> overrides = this.getPermissionOverrideMap();
      return overrides.get(permissionHolder.getIdLong());
   }

   @Nonnull
   default List<PermissionOverride> getPermissionOverrides() {
      TLongObjectMap<PermissionOverride> overrides = this.getPermissionOverrideMap();
      return Arrays.asList((PermissionOverride[])overrides.values(new PermissionOverride[overrides.size()]));
   }

   @Nonnull
   default PermissionOverrideAction upsertPermissionOverride(@Nonnull IPermissionHolder permissionHolder) {
      this.checkAttached();
      this.checkPermission(Permission.MANAGE_PERMISSIONS);
      Checks.notNull(permissionHolder, "PermissionHolder");
      Checks.check(permissionHolder.getGuild().equals(this.getGuild()), "Provided permission holder is not from the same guild as this channel!");
      PermissionOverride override = this.getPermissionOverride(permissionHolder);
      return (PermissionOverrideAction)(override != null ? override.getManager() : new PermissionOverrideActionImpl(this.getJDA(), this, permissionHolder));
   }

   @Nonnull
   default IPermissionContainer getPermissionContainer() {
      return this;
   }

   TLongObjectMap<PermissionOverride> getPermissionOverrideMap();
}
