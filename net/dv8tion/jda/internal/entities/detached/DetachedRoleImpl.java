package net.dv8tion.jda.internal.entities.detached;

import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.RoleColors;
import net.dv8tion.jda.api.entities.RoleIcon;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.exceptions.DetachedEntityException;
import net.dv8tion.jda.api.managers.RoleManager;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.RoleImpl;
import net.dv8tion.jda.internal.entities.mixin.RoleMixin;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.PermissionUtil;

public class DetachedRoleImpl implements Role, RoleMixin<DetachedRoleImpl> {
   private final long id;
   private final JDAImpl api;
   private final DetachedGuildImpl guild;
   private RoleImpl.RoleTagsImpl tags;
   private String name;
   private boolean managed;
   private boolean hoisted;
   private boolean mentionable;
   private long rawPermissions;
   private int primaryColor;
   private int secondaryColor = 536870911;
   private int tertiaryColor = 536870911;
   private int rawPosition;
   private RoleIcon icon;

   public DetachedRoleImpl(long id, DetachedGuildImpl guild) {
      this.id = id;
      this.api = guild.getJDA();
      this.guild = guild;
      this.tags = this.api.isCacheFlagSet(CacheFlag.ROLE_TAGS) ? new RoleImpl.RoleTagsImpl() : null;
   }

   public boolean isDetached() {
      return true;
   }

   public int getPosition() {
      throw new DetachedEntityException("Cannot get the position of a detached role, only the raw position is available");
   }

   public int getPositionRaw() {
      return this.rawPosition;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   public boolean isManaged() {
      return this.managed;
   }

   public boolean isHoisted() {
      return this.hoisted;
   }

   public boolean isMentionable() {
      return this.mentionable;
   }

   public long getPermissionsRaw() {
      return this.rawPermissions;
   }

   @Nonnull
   public RoleColors getColors() {
      return new RoleColors(this.primaryColor, this.secondaryColor, this.tertiaryColor);
   }

   @Nonnull
   public EnumSet<Permission> getPermissions() {
      return Permission.getPermissions(this.rawPermissions);
   }

   @Nonnull
   public EnumSet<Permission> getPermissions(@Nonnull GuildChannel channel) {
      throw this.detachedException();
   }

   @Nonnull
   public EnumSet<Permission> getPermissionsExplicit() {
      return this.getPermissions();
   }

   @Nonnull
   public EnumSet<Permission> getPermissionsExplicit(@Nonnull GuildChannel channel) {
      throw this.detachedException();
   }

   public boolean isPublicRole() {
      return this.getIdLong() == this.getGuild().getIdLong();
   }

   public boolean hasPermission(@Nonnull Permission... permissions) {
      long effectivePerms = this.rawPermissions;

      for(Permission perm : permissions) {
         long rawValue = perm.getRawValue();
         if ((effectivePerms & rawValue) != rawValue) {
            return false;
         }
      }

      return true;
   }

   public boolean hasPermission(@Nonnull GuildChannel channel, @Nonnull Permission... permissions) {
      throw this.detachedException();
   }

   public boolean canSync(@Nonnull IPermissionContainer targetChannel, @Nonnull IPermissionContainer syncSource) {
      throw this.detachedException();
   }

   public boolean canSync(@Nonnull IPermissionContainer channel) {
      throw this.detachedException();
   }

   public boolean canInteract(@Nonnull Role role) {
      return PermissionUtil.canInteract((Role)this, (Role)role);
   }

   @Nonnull
   public Guild getGuild() {
      return this.guild;
   }

   @Nonnull
   public RoleManager getManager() {
      throw this.detachedException();
   }

   @Nonnull
   public AuditableRestAction<Void> delete() {
      throw this.detachedException();
   }

   @Nonnull
   public JDA getJDA() {
      return this.api;
   }

   @Nonnull
   public Role.RoleTags getTags() {
      return (Role.RoleTags)(this.tags == null ? RoleImpl.RoleTagsImpl.EMPTY : this.tags);
   }

   @Nullable
   public RoleIcon getIcon() {
      return this.icon;
   }

   @Nonnull
   public String getAsMention() {
      return this.isPublicRole() ? "@everyone" : "<@&" + this.getId() + '>';
   }

   public long getIdLong() {
      return this.id;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof DetachedRoleImpl)) {
         return false;
      } else {
         DetachedRoleImpl oRole = (DetachedRoleImpl)o;
         return this.getIdLong() == oRole.getIdLong();
      }
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }

   public String toString() {
      return (new EntityString(this)).setName(this.getName()).toString();
   }

   public DetachedRoleImpl setName(String name) {
      this.name = name;
      return this;
   }

   public DetachedRoleImpl setPrimaryColor(int color) {
      this.primaryColor = color;
      return this;
   }

   public DetachedRoleImpl setSecondaryColor(int color) {
      this.secondaryColor = color;
      return this;
   }

   public DetachedRoleImpl setTertiaryColor(int color) {
      this.tertiaryColor = color;
      return this;
   }

   public DetachedRoleImpl setManaged(boolean managed) {
      this.managed = managed;
      return this;
   }

   public DetachedRoleImpl setHoisted(boolean hoisted) {
      this.hoisted = hoisted;
      return this;
   }

   public DetachedRoleImpl setMentionable(boolean mentionable) {
      this.mentionable = mentionable;
      return this;
   }

   public DetachedRoleImpl setRawPermissions(long rawPermissions) {
      this.rawPermissions = rawPermissions;
      return this;
   }

   public DetachedRoleImpl setRawPosition(int rawPosition) {
      this.rawPosition = rawPosition;
      return this;
   }

   public DetachedRoleImpl setTags(DataObject tags) {
      if (this.tags == null) {
         return this;
      } else {
         this.tags = new RoleImpl.RoleTagsImpl(tags);
         return this;
      }
   }

   public DetachedRoleImpl setIcon(RoleIcon icon) {
      this.icon = icon;
      return this;
   }
}
