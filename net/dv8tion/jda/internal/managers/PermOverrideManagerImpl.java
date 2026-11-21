package net.dv8tion.jda.internal.managers;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.PermOverrideManager;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IPermissionContainerMixin;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;

public class PermOverrideManagerImpl extends ManagerBase<PermOverrideManager> implements PermOverrideManager {
   protected final boolean role;
   protected PermissionOverride override;
   protected long allowed;
   protected long denied;

   public PermOverrideManagerImpl(PermissionOverride override) {
      super(override.getJDA(), Route.Channels.MODIFY_PERM_OVERRIDE.compile(override.getChannel().getId(), override.getId()));
      this.override = override;
      this.role = override.isRoleOverride();
      this.allowed = override.getAllowedRaw();
      this.denied = override.getDeniedRaw();
      if (isPermissionChecksEnabled()) {
         this.checkPermissions();
      }

   }

   private void setupValues() {
      if (!this.shouldUpdate(2L)) {
         this.allowed = this.getPermissionOverride().getAllowedRaw();
      }

      if (!this.shouldUpdate(1L)) {
         this.denied = this.getPermissionOverride().getDeniedRaw();
      }

   }

   @Nonnull
   public PermissionOverride getPermissionOverride() {
      IPermissionContainerMixin<?> channel = (IPermissionContainerMixin)this.override.getChannel();
      PermissionOverride realOverride = (PermissionOverride)channel.getPermissionOverrideMap().get(this.override.getIdLong());
      if (realOverride != null) {
         this.override = realOverride;
      }

      return this.override;
   }

   @Nonnull
   @CheckReturnValue
   public PermOverrideManagerImpl reset(long fields) {
      super.reset(fields);
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public PermOverrideManagerImpl reset(@Nonnull long... fields) {
      super.reset(fields);
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public PermOverrideManagerImpl reset() {
      super.reset();
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public PermOverrideManagerImpl grant(long permissions) {
      if (permissions == 0L) {
         return this;
      } else {
         this.setupValues();
         this.allowed |= permissions;
         this.denied &= ~permissions;
         this.set |= 3L;
         return this;
      }
   }

   @Nonnull
   @CheckReturnValue
   public PermOverrideManagerImpl deny(long permissions) {
      if (permissions == 0L) {
         return this;
      } else {
         this.setupValues();
         this.denied |= permissions;
         this.allowed &= ~permissions;
         this.set |= 3L;
         return this;
      }
   }

   @Nonnull
   @CheckReturnValue
   public PermOverrideManagerImpl clear(long permissions) {
      this.setupValues();
      if ((this.allowed & permissions) != 0L) {
         this.allowed &= ~permissions;
         this.set |= 2L;
      }

      if ((this.denied & permissions) != 0L) {
         this.denied &= ~permissions;
         this.set |= 1L;
      }

      return this;
   }

   protected RequestBody finalizeData() {
      String targetId = this.override.getId();
      this.setupValues();
      RequestBody data = this.getRequestBody(DataObject.empty().put("id", targetId).put("type", this.role ? "role" : "member").put("allow", this.allowed).put("deny", this.denied));
      this.reset();
      return data;
   }

   protected boolean checkPermissions() {
      Member selfMember = this.getGuild().getSelfMember();
      IPermissionContainer channel = this.getChannel();
      Checks.checkAccess(selfMember, channel);
      if (!selfMember.hasPermission(channel, new Permission[]{Permission.MANAGE_PERMISSIONS})) {
         throw new InsufficientPermissionException(channel, Permission.MANAGE_PERMISSIONS);
      } else {
         return super.checkPermissions();
      }
   }
}
