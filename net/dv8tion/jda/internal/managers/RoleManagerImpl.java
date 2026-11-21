package net.dv8tion.jda.internal.managers;

import java.util.Collection;
import java.util.EnumSet;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.RoleColors;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.RoleManager;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import okhttp3.RequestBody;

public class RoleManagerImpl extends ManagerBase<RoleManager> implements RoleManager {
   protected Role role;
   protected String name;
   protected RoleColors colors;
   protected long permissions;
   protected boolean hoist;
   protected boolean mentionable;
   protected Icon icon;
   protected String emoji;

   public RoleManagerImpl(Role role) {
      super(role.getJDA(), Route.Roles.MODIFY_ROLE.compile(role.getGuild().getId(), role.getId()));
      this.role = role;
      if (isPermissionChecksEnabled()) {
         this.checkPermissions();
      }

   }

   @Nonnull
   public Role getRole() {
      Role realRole = this.role.getGuild().getRoleById(this.role.getIdLong());
      if (realRole != null) {
         this.role = realRole;
      }

      return this.role;
   }

   @Nonnull
   @CheckReturnValue
   public RoleManagerImpl reset(long fields) {
      super.reset(fields);
      if ((fields & 1L) == 1L) {
         this.name = null;
      }

      if ((fields & 2L) == 2L) {
         this.colors = null;
      }

      return this;
   }

   @Nonnull
   @CheckReturnValue
   public RoleManagerImpl reset(@Nonnull long... fields) {
      super.reset(fields);
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public RoleManagerImpl reset() {
      super.reset();
      this.name = null;
      this.colors = null;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public RoleManagerImpl setName(@Nonnull String name) {
      Checks.notBlank(name, "Name");
      name = name.trim();
      Checks.notEmpty(name, "Name");
      Checks.notLonger(name, 100, "Name");
      this.name = name;
      this.set |= 1L;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public RoleManagerImpl setPermissions(long perms) {
      long selfPermissions = PermissionUtil.getEffectivePermission(this.getGuild().getSelfMember());
      this.setupPermissions();
      long missingPerms = perms & ~selfPermissions;
      missingPerms &= ~this.permissions;
      if (missingPerms != 0L && isPermissionChecksEnabled()) {
         EnumSet<Permission> permissionList = Permission.getPermissions(missingPerms);
         if (!permissionList.isEmpty()) {
            throw new InsufficientPermissionException(this.getGuild(), (Permission)permissionList.iterator().next());
         }
      }

      this.permissions = perms;
      this.set |= 4L;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public RoleManagerImpl setColor(int rgb) {
      this.colors = new RoleColors(rgb, 536870911, 536870911);
      this.set |= 2L;
      return this;
   }

   @Nonnull
   public RoleManager setColors(RoleColors colors) {
      this.colors = colors;
      this.set |= 2L;
      return this;
   }

   @Nonnull
   public RoleManager setGradientColors(int primaryRgb, int secondaryRgb) {
      this.colors = new RoleColors(primaryRgb, secondaryRgb, 536870911);
      this.set |= 2L;
      return this;
   }

   @Nonnull
   public RoleManager useHolographicStyle() {
      this.colors = RoleColors.DEFAULT_HOLOGRAPHIC;
      this.set |= 2L;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public RoleManagerImpl setHoisted(boolean hoisted) {
      this.hoist = hoisted;
      this.set |= 8L;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public RoleManagerImpl setMentionable(boolean mentionable) {
      this.mentionable = mentionable;
      this.set |= 16L;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public RoleManagerImpl setIcon(Icon icon) {
      this.icon = icon;
      this.emoji = null;
      this.set |= 32L;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public RoleManagerImpl setIcon(String emoji) {
      this.emoji = emoji;
      this.icon = null;
      this.set |= 32L;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public RoleManagerImpl givePermissions(@Nonnull Collection<Permission> perms) {
      Checks.noneNull(perms, "Permissions");
      this.setupPermissions();
      return this.setPermissions(this.permissions | Permission.getRaw(perms));
   }

   @Nonnull
   @CheckReturnValue
   public RoleManagerImpl revokePermissions(@Nonnull Collection<Permission> perms) {
      Checks.noneNull(perms, "Permissions");
      this.setupPermissions();
      return this.setPermissions(this.permissions & ~Permission.getRaw(perms));
   }

   protected RequestBody finalizeData() {
      DataObject object = DataObject.empty().put("name", this.getRole().getName());
      if (this.shouldUpdate(1L)) {
         object.put("name", this.name);
      }

      if (this.shouldUpdate(4L)) {
         object.put("permissions", this.permissions);
      }

      if (this.shouldUpdate(8L)) {
         object.put("hoist", this.hoist);
      }

      if (this.shouldUpdate(16L)) {
         object.put("mentionable", this.mentionable);
      }

      if (this.shouldUpdate(2L)) {
         object.put("colors", this.colors);
      }

      if (this.shouldUpdate(32L)) {
         object.put("icon", this.icon == null ? null : this.icon.getEncoding());
         object.put("unicode_emoji", this.emoji);
      }

      this.reset();
      return this.getRequestBody(object);
   }

   protected boolean checkPermissions() {
      Member selfMember = this.getGuild().getSelfMember();
      if (!selfMember.hasPermission(new Permission[]{Permission.MANAGE_ROLES})) {
         throw new InsufficientPermissionException(this.getGuild(), Permission.MANAGE_ROLES);
      } else if (!selfMember.canInteract(this.getRole())) {
         throw new HierarchyException("Cannot modify a role that is higher or equal in hierarchy");
      } else {
         return super.checkPermissions();
      }
   }

   private void setupPermissions() {
      if (!this.shouldUpdate(4L)) {
         this.permissions = this.getRole().getPermissionsRaw();
      }

   }
}
