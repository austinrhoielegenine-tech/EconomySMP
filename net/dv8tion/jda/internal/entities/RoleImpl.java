package net.dv8tion.jda.internal.entities;

import gnu.trove.map.TLongObjectMap;
import java.util.EnumSet;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.RoleColors;
import net.dv8tion.jda.api.entities.RoleIcon;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.RoleManager;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IPermissionContainerMixin;
import net.dv8tion.jda.internal.entities.mixin.RoleMixin;
import net.dv8tion.jda.internal.managers.RoleManagerImpl;
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import net.dv8tion.jda.internal.utils.cache.SortedSnowflakeCacheViewImpl;

public class RoleImpl implements Role, RoleMixin<RoleImpl> {
   private final long id;
   private final JDAImpl api;
   private Guild guild;
   private RoleTagsImpl tags;
   private String name;
   private boolean managed;
   private boolean hoisted;
   private boolean mentionable;
   private long rawPermissions;
   private int primaryColor;
   private int secondaryColor = 536870911;
   private int tertiaryColor = 536870911;
   private int rawPosition;
   private int frozenPosition = Integer.MIN_VALUE;
   private RoleIcon icon;

   public RoleImpl(long id, Guild guild) {
      this.id = id;
      this.api = (JDAImpl)guild.getJDA();
      this.guild = guild;
      this.tags = this.api.isCacheFlagSet(CacheFlag.ROLE_TAGS) ? new RoleTagsImpl() : null;
   }

   public boolean isDetached() {
      return false;
   }

   public int getPosition() {
      if (this.frozenPosition > Integer.MIN_VALUE) {
         return this.frozenPosition;
      } else {
         Guild guild = this.getGuild();
         if (this.equals(guild.getPublicRole())) {
            return -1;
         } else {
            int i = guild.getRoles().size() - 2;

            for(Role r : guild.getRoles()) {
               if (this.equals(r)) {
                  return i;
               }

               --i;
            }

            throw new IllegalStateException("Somehow when determining position we never found the role in the Guild's roles? wtf?");
         }
      }
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
   public EnumSet<Permission> getPermissions() {
      return Permission.getPermissions(this.rawPermissions);
   }

   @Nonnull
   public EnumSet<Permission> getPermissions(@Nonnull GuildChannel channel) {
      return Permission.getPermissions(PermissionUtil.getEffectivePermission(channel.getPermissionContainer(), (Role)this));
   }

   @Nonnull
   public EnumSet<Permission> getPermissionsExplicit() {
      return this.getPermissions();
   }

   @Nonnull
   public EnumSet<Permission> getPermissionsExplicit(@Nonnull GuildChannel channel) {
      return Permission.getPermissions(PermissionUtil.getExplicitPermission(channel.getPermissionContainer(), (Role)this));
   }

   @Nonnull
   public RoleColors getColors() {
      return new RoleColors(this.primaryColor, this.secondaryColor, this.tertiaryColor);
   }

   public boolean isPublicRole() {
      return this.equals(this.getGuild().getPublicRole());
   }

   public boolean hasPermission(@Nonnull Permission... permissions) {
      long effectivePerms = this.rawPermissions | this.getGuild().getPublicRole().getPermissionsRaw();

      for(Permission perm : permissions) {
         long rawValue = perm.getRawValue();
         if ((effectivePerms & rawValue) != rawValue) {
            return false;
         }
      }

      return true;
   }

   public boolean hasPermission(@Nonnull GuildChannel channel, @Nonnull Permission... permissions) {
      long effectivePerms = PermissionUtil.getEffectivePermission(channel.getPermissionContainer(), (Role)this);

      for(Permission perm : permissions) {
         long rawValue = perm.getRawValue();
         if ((effectivePerms & rawValue) != rawValue) {
            return false;
         }
      }

      return true;
   }

   public boolean canSync(@Nonnull IPermissionContainer targetChannel, @Nonnull IPermissionContainer syncSource) {
      Checks.notNull(targetChannel, "Channel");
      Checks.notNull(syncSource, "Channel");
      Checks.check(targetChannel.getGuild().equals(this.getGuild()), "Channels must be from the same guild!");
      Checks.check(syncSource.getGuild().equals(this.getGuild()), "Channels must be from the same guild!");
      long rolePerms = PermissionUtil.getEffectivePermission(targetChannel, (Role)this);
      if ((rolePerms & Permission.MANAGE_PERMISSIONS.getRawValue()) == 0L) {
         return false;
      } else {
         long channelPermissions = PermissionUtil.getExplicitPermission(targetChannel, (Role)this, false);
         boolean hasLocalAdmin = (rolePerms & Permission.ADMINISTRATOR.getRawValue() | channelPermissions & Permission.MANAGE_PERMISSIONS.getRawValue()) != 0L;
         if (hasLocalAdmin) {
            return true;
         } else {
            TLongObjectMap<PermissionOverride> existingOverrides = ((IPermissionContainerMixin)targetChannel).getPermissionOverrideMap();

            for(PermissionOverride override : syncSource.getPermissionOverrides()) {
               PermissionOverride existing = existingOverrides.get(override.getIdLong());
               long allow = override.getAllowedRaw();
               long deny = override.getDeniedRaw();
               if (existing != null) {
                  allow ^= existing.getAllowedRaw();
                  deny ^= existing.getDeniedRaw();
               }

               if (((allow | deny) & ~rolePerms) != 0L) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public boolean canSync(@Nonnull IPermissionContainer channel) {
      Checks.notNull(channel, "Channel");
      Checks.check(channel.getGuild().equals(this.getGuild()), "Channels must be from the same guild!");
      long rolePerms = PermissionUtil.getEffectivePermission(channel, (Role)this);
      if ((rolePerms & Permission.MANAGE_PERMISSIONS.getRawValue()) == 0L) {
         return false;
      } else {
         long channelPermissions = PermissionUtil.getExplicitPermission(channel, (Role)this, false);
         return (rolePerms & Permission.ADMINISTRATOR.getRawValue() | channelPermissions & Permission.MANAGE_PERMISSIONS.getRawValue()) != 0L;
      }
   }

   public boolean canInteract(@Nonnull Role role) {
      return PermissionUtil.canInteract((Role)this, (Role)role);
   }

   @Nonnull
   public Guild getGuild() {
      Guild realGuild = this.api.getGuildById(this.guild.getIdLong());
      if (realGuild != null) {
         this.guild = realGuild;
      }

      return this.guild;
   }

   @Nonnull
   public RoleManager getManager() {
      return new RoleManagerImpl(this);
   }

   @Nonnull
   public AuditableRestAction<Void> delete() {
      Guild guild = this.getGuild();
      if (!guild.getSelfMember().hasPermission(new Permission[]{Permission.MANAGE_ROLES})) {
         throw new InsufficientPermissionException(guild, Permission.MANAGE_ROLES);
      } else if (!PermissionUtil.canInteract((Member)guild.getSelfMember(), (Role)this)) {
         throw new HierarchyException("Can't delete role >= highest self-role");
      } else if (this.managed) {
         throw new UnsupportedOperationException("Cannot delete a Role that is managed. ");
      } else {
         Route.CompiledRoute route = Route.Roles.DELETE_ROLE.compile(guild.getId(), this.getId());
         return new AuditableRestActionImpl<Void>(this.getJDA(), route);
      }
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
      } else if (!(o instanceof RoleImpl)) {
         return false;
      } else {
         RoleImpl oRole = (RoleImpl)o;
         return this.getIdLong() == oRole.getIdLong();
      }
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }

   public String toString() {
      return (new EntityString(this)).setName(this.getName()).toString();
   }

   public RoleImpl setName(String name) {
      this.name = name;
      return this;
   }

   public RoleImpl setPrimaryColor(int color) {
      this.primaryColor = color;
      return this;
   }

   public RoleImpl setSecondaryColor(int color) {
      this.secondaryColor = color;
      return this;
   }

   public RoleImpl setTertiaryColor(int color) {
      this.tertiaryColor = color;
      return this;
   }

   public RoleImpl setManaged(boolean managed) {
      this.managed = managed;
      return this;
   }

   public RoleImpl setHoisted(boolean hoisted) {
      this.hoisted = hoisted;
      return this;
   }

   public RoleImpl setMentionable(boolean mentionable) {
      this.mentionable = mentionable;
      return this;
   }

   public RoleImpl setRawPermissions(long rawPermissions) {
      this.rawPermissions = rawPermissions;
      return this;
   }

   public RoleImpl setRawPosition(int rawPosition) {
      SortedSnowflakeCacheViewImpl<Role> roleCache = (SortedSnowflakeCacheViewImpl)this.getGuild().getRoleCache();
      roleCache.clearCachedLists();
      this.rawPosition = rawPosition;
      return this;
   }

   public RoleImpl setTags(DataObject tags) {
      if (this.tags == null) {
         return this;
      } else {
         this.tags = new RoleTagsImpl(tags);
         return this;
      }
   }

   public RoleImpl setIcon(RoleIcon icon) {
      this.icon = icon;
      return this;
   }

   public void freezePosition() {
      this.frozenPosition = this.getPosition();
   }

   public static class RoleTagsImpl implements Role.RoleTags {
      public static final Role.RoleTags EMPTY = new RoleTagsImpl();
      private final long botId;
      private final long integrationId;
      private final long subscriptionListingId;
      private final boolean premiumSubscriber;
      private final boolean availableForPurchase;
      private final boolean isGuildConnections;

      public RoleTagsImpl() {
         this.botId = 0L;
         this.integrationId = 0L;
         this.subscriptionListingId = 0L;
         this.premiumSubscriber = false;
         this.availableForPurchase = false;
         this.isGuildConnections = false;
      }

      public RoleTagsImpl(DataObject tags) {
         this.botId = tags.getUnsignedLong("bot_id", 0L);
         this.integrationId = tags.getUnsignedLong("integration_id", 0L);
         this.subscriptionListingId = tags.getUnsignedLong("subscription_listing_id", 0L);
         this.premiumSubscriber = tags.hasKey("premium_subscriber");
         this.availableForPurchase = tags.hasKey("available_for_purchase");
         this.isGuildConnections = tags.hasKey("guild_connections");
      }

      public boolean isBot() {
         return this.botId != 0L;
      }

      public long getBotIdLong() {
         return this.botId;
      }

      public boolean isBoost() {
         return this.premiumSubscriber;
      }

      public boolean isIntegration() {
         return this.integrationId != 0L;
      }

      public long getIntegrationIdLong() {
         return this.integrationId;
      }

      public long getSubscriptionIdLong() {
         return this.subscriptionListingId;
      }

      public boolean isAvailableForPurchase() {
         return this.availableForPurchase;
      }

      public boolean isLinkedRole() {
         return this.isGuildConnections;
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.botId, this.integrationId, this.premiumSubscriber, this.availableForPurchase, this.subscriptionListingId, this.isGuildConnections});
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof RoleTagsImpl)) {
            return false;
         } else {
            RoleTagsImpl other = (RoleTagsImpl)obj;
            return this.botId == other.botId && this.integrationId == other.integrationId && this.premiumSubscriber == other.premiumSubscriber && this.availableForPurchase == other.availableForPurchase && this.subscriptionListingId == other.subscriptionListingId && this.isGuildConnections == other.isGuildConnections;
         }
      }

      public String toString() {
         return (new EntityString(this)).addMetadata("bot", this.getBotId()).addMetadata("integration", this.getIntegrationId()).addMetadata("subscriptionListing", this.getSubscriptionId()).addMetadata("isBoost", this.isBoost()).addMetadata("isAvailableForPurchase", this.isAvailableForPurchase()).addMetadata("isGuildConnections", this.isLinkedRole()).toString();
      }
   }
}
