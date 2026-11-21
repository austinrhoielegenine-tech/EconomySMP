package net.dv8tion.jda.internal.entities.detached;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ClientType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.exceptions.MissingEntityInteractionPermissionsException;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IInteractionPermissionMixin;
import net.dv8tion.jda.internal.entities.mixin.MemberMixin;
import net.dv8tion.jda.internal.interactions.ChannelInteractionPermissions;
import net.dv8tion.jda.internal.interactions.MemberInteractionPermissions;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;

public class DetachedMemberImpl implements Member, MemberMixin<DetachedMemberImpl> {
   private final JDAImpl api;
   private final DetachedGuildImpl guild;
   private User user;
   private String nickname;
   private String avatarId;
   private long joinDate;
   private long boostDate;
   private long timeOutEnd;
   private boolean pending = false;
   private int flags;
   private MemberInteractionPermissions interactionPermissions;

   public DetachedMemberImpl(DetachedGuildImpl guild, User user) {
      this.api = (JDAImpl)user.getJDA();
      this.guild = guild;
      this.user = user;
      this.joinDate = 0L;
   }

   public boolean isDetached() {
      return true;
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
   public Guild getGuild() {
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

   public GuildVoiceState getVoiceState() {
      throw this.detachedException();
   }

   @Nonnull
   public List<Activity> getActivities() {
      throw this.detachedException();
   }

   @Nonnull
   public OnlineStatus getOnlineStatus() {
      throw this.detachedException();
   }

   @Nonnull
   public OnlineStatus getOnlineStatus(@Nonnull ClientType type) {
      throw this.detachedException();
   }

   @Nonnull
   public EnumSet<ClientType> getActiveClients() {
      throw this.detachedException();
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
      throw this.detachedException();
   }

   @Nonnull
   public Set<Role> getUnsortedRoles() {
      throw this.detachedException();
   }

   public int getFlagsRaw() {
      return this.flags;
   }

   @Nonnull
   public EnumSet<Permission> getPermissions() {
      throw this.detachedRequiresChannelException();
   }

   @Nonnull
   public EnumSet<Permission> getPermissions(@Nonnull GuildChannel channel) {
      return Permission.getPermissions(this.getRawInteractionPermissions(channel));
   }

   @Nonnull
   public EnumSet<Permission> getPermissionsExplicit() {
      throw this.detachedRequiresChannelException();
   }

   @Nonnull
   public EnumSet<Permission> getPermissionsExplicit(@Nonnull GuildChannel channel) {
      return Permission.getPermissions(this.getRawInteractionPermissions(channel));
   }

   public boolean hasPermission(@Nonnull Permission... permissions) {
      throw this.detachedRequiresChannelException();
   }

   public boolean hasPermission(@Nonnull GuildChannel channel, @Nonnull Permission... permissions) {
      long rawPermissions = Permission.getRaw(permissions);
      return (this.getRawInteractionPermissions(channel) & rawPermissions) == rawPermissions;
   }

   private long getRawInteractionPermissions(@Nonnull GuildChannel channel) {
      if (this.interactionPermissions.getChannelId() == channel.getIdLong()) {
         return this.interactionPermissions.getPermissions();
      } else {
         if (channel instanceof IInteractionPermissionMixin) {
            ChannelInteractionPermissions channelInteractionPermissions = ((IInteractionPermissionMixin)channel).getInteractionPermissions();
            if (channelInteractionPermissions.getMemberId() == this.getIdLong()) {
               return channelInteractionPermissions.getPermissions();
            }
         }

         throw new MissingEntityInteractionPermissionsException("Detached member permissions can only be retrieved in the interaction channel, and channels only contain the permissions of the interaction caller");
      }
   }

   public boolean canSync(@Nonnull IPermissionContainer targetChannel, @Nonnull IPermissionContainer syncSource) {
      throw this.detachedException();
   }

   public boolean canSync(@Nonnull IPermissionContainer channel) {
      throw this.detachedException();
   }

   public boolean canInteract(@Nonnull Member member) {
      throw this.detachedException();
   }

   public boolean canInteract(@Nonnull Role role) {
      throw this.detachedException();
   }

   public boolean canInteract(@Nonnull RichCustomEmoji emoji) {
      throw this.detachedException();
   }

   public boolean isOwner() {
      throw this.detachedException();
   }

   public boolean isPending() {
      return this.pending;
   }

   public long getIdLong() {
      return this.user.getIdLong();
   }

   @Nonnull
   public String getAsMention() {
      return this.user.getAsMention();
   }

   @Nullable
   public DefaultGuildChannelUnion getDefaultChannel() {
      throw this.detachedException();
   }

   @Nonnull
   public String getDefaultAvatarId() {
      return this.user.getDefaultAvatarId();
   }

   @Nonnull
   public MemberInteractionPermissions getInteractionPermissions() {
      return this.interactionPermissions;
   }

   @Nonnull
   public AuditableRestAction<Void> modifyFlags(@Nonnull Collection<Member.MemberFlag> newFlags) {
      throw this.detachedException();
   }

   public DetachedMemberImpl setNickname(String nickname) {
      this.nickname = nickname;
      return this;
   }

   public DetachedMemberImpl setAvatarId(String avatarId) {
      this.avatarId = avatarId;
      return this;
   }

   public DetachedMemberImpl setJoinDate(long joinDate) {
      this.joinDate = joinDate;
      return this;
   }

   public DetachedMemberImpl setBoostDate(long boostDate) {
      this.boostDate = boostDate;
      return this;
   }

   public DetachedMemberImpl setTimeOutEnd(long time) {
      this.timeOutEnd = time;
      return this;
   }

   public DetachedMemberImpl setPending(boolean pending) {
      this.pending = pending;
      return this;
   }

   public DetachedMemberImpl setFlags(int flags) {
      this.flags = flags;
      return this;
   }

   public DetachedMemberImpl setInteractionPermissions(@Nonnull MemberInteractionPermissions interactionPermissions) {
      this.interactionPermissions = interactionPermissions;
      return this;
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
      } else if (!(o instanceof DetachedMemberImpl)) {
         return false;
      } else {
         DetachedMemberImpl oMember = (DetachedMemberImpl)o;
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
