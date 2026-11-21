package net.dv8tion.jda.internal.entities;

import gnu.trove.map.TLongObjectMap;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ClientType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.utils.cache.CacheView;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IPermissionContainerMixin;
import net.dv8tion.jda.internal.entities.mixin.MemberMixin;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;
import net.dv8tion.jda.internal.utils.PermissionUtil;

public class MemberImpl implements Member, MemberMixin<MemberImpl> {
   private final JDAImpl api;
   private final Set<Role> roles = ConcurrentHashMap.newKeySet();
   private GuildImpl guild;
   private User user;
   private String nickname;
   private String avatarId;
   private long joinDate;
   private long boostDate;
   private long timeOutEnd;
   private boolean pending = false;
   private int flags;

   public MemberImpl(GuildImpl guild, User user) {
      this.api = (JDAImpl)user.getJDA();
      this.guild = guild;
      this.user = user;
      this.joinDate = 0L;
   }

   public boolean isDetached() {
      return false;
   }

   public MemberPresenceImpl getPresence() {
      CacheView.SimpleCacheView<MemberPresenceImpl> presences = this.guild.getPresenceView();
      return presences == null ? null : (MemberPresenceImpl)presences.get(this.getIdLong());
   }

   @Nonnull
   public User getUser() {
      User realUser = this.getJDA().getUserById(this.user.getIdLong());
      if (realUser != null) {
         this.user = realUser;
      }

      return this.user;
   }

   @Nonnull
   public GuildImpl getGuild() {
      GuildImpl realGuild = (GuildImpl)this.api.getGuildById(this.guild.getIdLong());
      if (realGuild != null) {
         this.guild = realGuild;
      }

      return this.guild;
   }

   @Nonnull
   public JDA getJDA() {
      return this.api;
   }

   @Nonnull
   public OffsetDateTime getTimeJoined() {
      return this.hasTimeJoined() ? Helpers.toOffset(this.joinDate) : this.getGuild().getTimeCreated();
   }

   public boolean hasTimeJoined() {
      return this.joinDate != 0L;
   }

   @Nullable
   public OffsetDateTime getTimeBoosted() {
      return this.isBoosting() ? Helpers.toOffset(this.boostDate) : null;
   }

   public boolean isBoosting() {
      return this.boostDate != 0L;
   }

   @Nullable
   public OffsetDateTime getTimeOutEnd() {
      return this.timeOutEnd != 0L ? Helpers.toOffset(this.timeOutEnd) : null;
   }

   public GuildVoiceStateImpl getVoiceState() {
      return this.guild.getVoiceState(this);
   }

   @Nonnull
   public List<Activity> getActivities() {
      MemberPresenceImpl presence = this.getPresence();
      return presence == null ? Collections.emptyList() : presence.getActivities();
   }

   @Nonnull
   public OnlineStatus getOnlineStatus() {
      MemberPresenceImpl presence = this.getPresence();
      return presence == null ? OnlineStatus.OFFLINE : presence.getOnlineStatus();
   }

   @Nonnull
   public OnlineStatus getOnlineStatus(@Nonnull ClientType type) {
      Checks.notNull(type, "Type");
      MemberPresenceImpl presence = this.getPresence();
      if (presence == null) {
         return OnlineStatus.OFFLINE;
      } else {
         OnlineStatus status = (OnlineStatus)presence.getClientStatus().get(type);
         return status == null ? OnlineStatus.OFFLINE : status;
      }
   }

   @Nonnull
   public EnumSet<ClientType> getActiveClients() {
      MemberPresenceImpl presence = this.getPresence();
      return presence == null ? EnumSet.noneOf(ClientType.class) : Helpers.copyEnumSet(ClientType.class, presence.getClientStatus().keySet());
   }

   public String getNickname() {
      return this.nickname;
   }

   public String getAvatarId() {
      return this.avatarId;
   }

   @Nonnull
   public String getEffectiveName() {
      return this.nickname != null ? this.nickname : this.getUser().getEffectiveName();
   }

   @Nonnull
   public List<Role> getRoles() {
      List<Role> roleList = new ArrayList(this.roles);
      roleList.sort(Comparator.reverseOrder());
      return Collections.unmodifiableList(roleList);
   }

   @Nonnull
   public Set<Role> getUnsortedRoles() {
      return Collections.unmodifiableSet(this.roles);
   }

   public int getFlagsRaw() {
      return this.flags;
   }

   @Nonnull
   public EnumSet<Permission> getPermissions() {
      return Permission.getPermissions(PermissionUtil.getEffectivePermission(this));
   }

   @Nonnull
   public EnumSet<Permission> getPermissions(@Nonnull GuildChannel channel) {
      Checks.notNull(channel, "Channel");
      if (!this.getGuild().equals(channel.getGuild())) {
         throw new IllegalArgumentException("Provided channel is not in the same guild as this member!");
      } else {
         return Permission.getPermissions(PermissionUtil.getEffectivePermission(channel.getPermissionContainer(), (Member)this));
      }
   }

   @Nonnull
   public EnumSet<Permission> getPermissionsExplicit() {
      return Permission.getPermissions(PermissionUtil.getExplicitPermission(this));
   }

   @Nonnull
   public EnumSet<Permission> getPermissionsExplicit(@Nonnull GuildChannel channel) {
      return Permission.getPermissions(PermissionUtil.getExplicitPermission(channel.getPermissionContainer(), (Member)this));
   }

   public boolean hasPermission(@Nonnull Permission... permissions) {
      return PermissionUtil.checkPermission(this, permissions);
   }

   public boolean hasPermission(@Nonnull GuildChannel channel, @Nonnull Permission... permissions) {
      return PermissionUtil.checkPermission(channel.getPermissionContainer(), this, permissions);
   }

   public boolean canSync(@Nonnull IPermissionContainer targetChannel, @Nonnull IPermissionContainer syncSource) {
      Checks.notNull(targetChannel, "Channel");
      Checks.notNull(syncSource, "Channel");
      Checks.check(targetChannel.getGuild().equals(this.getGuild()), "Channels must be from the same guild!");
      Checks.check(syncSource.getGuild().equals(this.getGuild()), "Channels must be from the same guild!");
      long userPerms = PermissionUtil.getEffectivePermission(targetChannel, (Member)this);
      if ((userPerms & Permission.MANAGE_PERMISSIONS.getRawValue()) == 0L) {
         return false;
      } else {
         long channelPermissions = PermissionUtil.getExplicitPermission(targetChannel, (Member)this, false);
         boolean hasLocalAdmin = (userPerms & Permission.ADMINISTRATOR.getRawValue() | channelPermissions & Permission.MANAGE_PERMISSIONS.getRawValue()) != 0L;
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

               if (((allow | deny) & ~userPerms) != 0L) {
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
      long userPerms = PermissionUtil.getEffectivePermission(channel, (Member)this);
      if ((userPerms & Permission.MANAGE_PERMISSIONS.getRawValue()) == 0L) {
         return false;
      } else {
         long channelPermissions = PermissionUtil.getExplicitPermission(channel, (Member)this, false);
         return (userPerms & Permission.ADMINISTRATOR.getRawValue() | channelPermissions & Permission.MANAGE_PERMISSIONS.getRawValue()) != 0L;
      }
   }

   public boolean canInteract(@Nonnull Member member) {
      return PermissionUtil.canInteract((Member)this, (Member)member);
   }

   public boolean canInteract(@Nonnull Role role) {
      return PermissionUtil.canInteract((Member)this, (Role)role);
   }

   public boolean canInteract(@Nonnull RichCustomEmoji emoji) {
      return PermissionUtil.canInteract((Member)this, (RichCustomEmoji)emoji);
   }

   public boolean isOwner() {
      return this.user.getIdLong() == this.getGuild().getOwnerIdLong();
   }

   public boolean isPending() {
      return this.pending;
   }

   public long getIdLong() {
      return this.user.getIdLong();
   }

   @Nonnull
   public String getAsMention() {
      return "<@" + this.user.getId() + '>';
   }

   @Nullable
   public DefaultGuildChannelUnion getDefaultChannel() {
      return (DefaultGuildChannelUnion)Stream.concat(this.getGuild().getTextChannelCache().stream(), this.getGuild().getNewsChannelCache().stream()).filter((c) -> this.hasPermission(c, Permission.VIEW_CHANNEL)).min(Comparator.naturalOrder()).orElse((Object)null);
   }

   @Nonnull
   public String getDefaultAvatarId() {
      return this.user.getDefaultAvatarId();
   }

   public MemberImpl setNickname(String nickname) {
      this.nickname = nickname;
      return this;
   }

   public MemberImpl setAvatarId(String avatarId) {
      this.avatarId = avatarId;
      return this;
   }

   public MemberImpl setJoinDate(long joinDate) {
      this.joinDate = joinDate;
      return this;
   }

   public MemberImpl setBoostDate(long boostDate) {
      this.boostDate = boostDate;
      return this;
   }

   public MemberImpl setTimeOutEnd(long time) {
      this.timeOutEnd = time;
      return this;
   }

   public MemberImpl setPending(boolean pending) {
      this.pending = pending;
      return this;
   }

   public MemberImpl setFlags(int flags) {
      this.flags = flags;
      return this;
   }

   public Set<Role> getRoleSet() {
      return this.roles;
   }

   public long getBoostDateRaw() {
      return this.boostDate;
   }

   public long getTimeOutEndRaw() {
      return this.timeOutEnd;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof MemberImpl)) {
         return false;
      } else {
         MemberImpl oMember = (MemberImpl)o;
         return oMember.user.getIdLong() == this.user.getIdLong() && oMember.guild.getIdLong() == this.guild.getIdLong();
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.guild.getIdLong(), this.user.getIdLong()});
   }

   public String toString() {
      return (new EntityString(this)).setName(this.getEffectiveName()).addMetadata("user", this.getUser()).addMetadata("guild", this.getGuild()).toString();
   }
}
