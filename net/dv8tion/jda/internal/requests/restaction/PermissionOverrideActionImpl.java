package net.dv8tion.jda.internal.requests.restaction;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.PermissionOverrideImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IPermissionContainerMixin;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import okhttp3.RequestBody;

public class PermissionOverrideActionImpl extends AuditableRestActionImpl<PermissionOverride> implements PermissionOverrideAction {
   private boolean isOverride = true;
   private boolean allowSet = false;
   private boolean denySet = false;
   private long allow = 0L;
   private long deny = 0L;
   private final IPermissionContainerMixin<?> channel;
   private final IPermissionHolder permissionHolder;
   private final boolean isRole;
   private final long id;

   public PermissionOverrideActionImpl(PermissionOverride override) {
      super(override.getJDA(), Route.Channels.MODIFY_PERM_OVERRIDE.compile(override.getChannel().getId(), override.getId()));
      this.channel = (IPermissionContainerMixin)override.getChannel();
      this.permissionHolder = override.getPermissionHolder();
      this.isRole = override.isRoleOverride();
      this.id = override.getIdLong();
   }

   public PermissionOverrideActionImpl(JDA api, GuildChannel channel, IPermissionHolder permissionHolder) {
      super(api, Route.Channels.CREATE_PERM_OVERRIDE.compile(channel.getId(), permissionHolder.getId()));
      this.channel = (IPermissionContainerMixin)channel;
      this.permissionHolder = permissionHolder;
      this.isRole = permissionHolder instanceof Role;
      this.id = permissionHolder.getIdLong();
   }

   public PermissionOverrideActionImpl setOverride(boolean override) {
      this.isOverride = override;
      return this;
   }

   protected BooleanSupplier finalizeChecks() {
      return () -> {
         Member selfMember = this.getGuild().getSelfMember();
         Checks.checkAccess(selfMember, this.channel);
         if (!selfMember.hasPermission(this.channel, new Permission[]{Permission.MANAGE_PERMISSIONS})) {
            throw new InsufficientPermissionException(this.channel, Permission.MANAGE_PERMISSIONS);
         } else {
            return true;
         }
      };
   }

   @Nonnull
   public PermissionOverrideActionImpl setCheck(BooleanSupplier checks) {
      return (PermissionOverrideActionImpl)super.setCheck(checks);
   }

   @Nonnull
   public PermissionOverrideActionImpl timeout(long timeout, @Nonnull TimeUnit unit) {
      return (PermissionOverrideActionImpl)super.timeout(timeout, unit);
   }

   @Nonnull
   public PermissionOverrideActionImpl deadline(long timestamp) {
      return (PermissionOverrideActionImpl)super.deadline(timestamp);
   }

   @Nonnull
   public PermissionOverrideAction resetAllow() {
      this.allow = this.getOriginalAllow();
      this.allowSet = false;
      return this;
   }

   @Nonnull
   public PermissionOverrideAction resetDeny() {
      this.deny = this.getOriginalDeny();
      this.denySet = false;
      return this;
   }

   @Nonnull
   public IPermissionContainer getChannel() {
      return this.channel;
   }

   public Role getRole() {
      return this.isRole() ? (Role)this.permissionHolder : null;
   }

   public Member getMember() {
      return this.isMember() ? (Member)this.permissionHolder : null;
   }

   public long getAllowed() {
      return this.getCurrentAllow();
   }

   public long getDenied() {
      return this.getCurrentDeny();
   }

   public long getInherited() {
      return ~this.getAllowed() & ~this.getDenied();
   }

   public boolean isMember() {
      return !this.isRole;
   }

   public boolean isRole() {
      return this.isRole;
   }

   @Nonnull
   @CheckReturnValue
   public PermissionOverrideActionImpl setAllowed(long allowBits) {
      this.checkPermissions(this.getOriginalAllow() ^ allowBits);
      this.allow = allowBits;
      this.deny = this.getCurrentDeny() & ~allowBits;
      this.allowSet = this.denySet = true;
      return this;
   }

   @Nonnull
   public PermissionOverrideAction grant(long allowBits) {
      return this.setAllowed(this.getCurrentAllow() | allowBits);
   }

   @Nonnull
   @CheckReturnValue
   public PermissionOverrideActionImpl setDenied(long denyBits) {
      this.checkPermissions(this.getOriginalDeny() ^ denyBits);
      this.deny = denyBits;
      this.allow = this.getCurrentAllow() & ~denyBits;
      this.allowSet = this.denySet = true;
      return this;
   }

   @Nonnull
   public PermissionOverrideAction deny(long denyBits) {
      return this.setDenied(this.getCurrentDeny() | denyBits);
   }

   @Nonnull
   public PermissionOverrideAction clear(long inheritedBits) {
      return this.setAllowed(this.getCurrentAllow() & ~inheritedBits).setDenied(this.getCurrentDeny() & ~inheritedBits);
   }

   protected void checkPermissions(long changed) {
      Member selfMember = this.getGuild().getSelfMember();
      if (changed != 0L && !selfMember.hasPermission(new Permission[]{Permission.ADMINISTRATOR})) {
         long channelPermissions = PermissionUtil.getExplicitPermission(this.channel, (Member)selfMember, false);
         if ((channelPermissions & Permission.MANAGE_PERMISSIONS.getRawValue()) == 0L) {
            long botPerms = PermissionUtil.getEffectivePermission(this.channel, (Member)selfMember);
            EnumSet<Permission> missing = Permission.getPermissions(changed & ~botPerms);
            if (!missing.isEmpty()) {
               throw new InsufficientPermissionException(this.channel, Permission.MANAGE_PERMISSIONS, "You must have Permission.MANAGE_PERMISSIONS on the channel explicitly in order to set permissions you don't already have!");
            }
         }
      }

   }

   @Nonnull
   @CheckReturnValue
   public PermissionOverrideActionImpl setPermissions(long allowBits, long denyBits) {
      return this.setAllowed(allowBits).setDenied(denyBits);
   }

   private long getCurrentAllow() {
      if (this.allowSet) {
         return this.allow;
      } else {
         return this.isOverride ? 0L : this.getOriginalAllow();
      }
   }

   private long getCurrentDeny() {
      if (this.denySet) {
         return this.deny;
      } else {
         return this.isOverride ? 0L : this.getOriginalDeny();
      }
   }

   private long getOriginalDeny() {
      PermissionOverride override = (PermissionOverride)this.channel.getPermissionOverrideMap().get(this.id);
      return override == null ? 0L : override.getDeniedRaw();
   }

   private long getOriginalAllow() {
      PermissionOverride override = (PermissionOverride)this.channel.getPermissionOverrideMap().get(this.id);
      return override == null ? 0L : override.getAllowedRaw();
   }

   protected RequestBody finalizeData() {
      DataObject object = DataObject.empty();
      object.put("type", this.isRole() ? 0 : 1);
      object.put("allow", this.getCurrentAllow());
      object.put("deny", this.getCurrentDeny());
      this.reset();
      return this.getRequestBody(object);
   }

   protected void handleSuccess(Response response, Request<PermissionOverride> request) {
      DataObject object = (DataObject)request.getRawBody();
      PermissionOverrideImpl override = new PermissionOverrideImpl(this.channel, this.id, this.isRole());
      override.setAllow(object.getLong("allow"));
      override.setDeny(object.getLong("deny"));
      request.onSuccess(override);
   }
}
