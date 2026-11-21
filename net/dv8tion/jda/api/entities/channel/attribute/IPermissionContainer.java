package net.dv8tion.jda.api.entities.channel.attribute;

import java.util.List;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.managers.channel.attribute.IPermissionContainerManager;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jetbrains.annotations.Unmodifiable;

public interface IPermissionContainer extends GuildChannel {
   @Nonnull
   @CheckReturnValue
   IPermissionContainerManager<?, ?> getManager();

   @Nullable
   PermissionOverride getPermissionOverride(@Nonnull IPermissionHolder var1);

   @Nonnull
   @Unmodifiable List<PermissionOverride> getPermissionOverrides();

   @Nonnull
   default @Unmodifiable List<PermissionOverride> getMemberPermissionOverrides() {
      return (List)this.getPermissionOverrides().stream().filter(PermissionOverride::isMemberOverride).collect(Helpers.toUnmodifiableList());
   }

   @Nonnull
   default @Unmodifiable List<PermissionOverride> getRolePermissionOverrides() {
      return (List)this.getPermissionOverrides().stream().filter(PermissionOverride::isRoleOverride).collect(Helpers.toUnmodifiableList());
   }

   @Nonnull
   @CheckReturnValue
   PermissionOverrideAction upsertPermissionOverride(@Nonnull IPermissionHolder var1);
}
