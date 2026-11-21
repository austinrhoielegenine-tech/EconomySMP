package net.dv8tion.jda.api.entities;

import java.awt.Color;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.annotations.Incubating;
import net.dv8tion.jda.annotations.ReplaceWith;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.entities.detached.IDetachableEntity;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.utils.DiscordAssets;
import net.dv8tion.jda.api.utils.ImageFormat;
import net.dv8tion.jda.api.utils.ImageProxy;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jetbrains.annotations.Unmodifiable;

public interface Member extends IMentionable, IPermissionHolder, IDetachableEntity, UserSnowflake {
   /** @deprecated */
   @Deprecated
   String AVATAR_URL = "https://cdn.discordapp.com/guilds/%s/users/%s/avatars/%s.%s";
   int MAX_TIME_OUT_LENGTH = 28;
   int MAX_NICKNAME_LENGTH = 32;

   @Nonnull
   User getUser();

   @Nonnull
   Guild getGuild();

   @Nonnull
   JDA getJDA();

   @Nonnull
   OffsetDateTime getTimeJoined();

   boolean hasTimeJoined();

   @Nullable
   OffsetDateTime getTimeBoosted();

   boolean isBoosting();

   @Nullable
   OffsetDateTime getTimeOutEnd();

   default boolean isTimedOut() {
      return this.getTimeOutEnd() != null && this.getTimeOutEnd().isAfter(OffsetDateTime.now());
   }

   @Nullable
   GuildVoiceState getVoiceState();

   @Nonnull
   @Unmodifiable List<Activity> getActivities();

   @Nonnull
   OnlineStatus getOnlineStatus();

   @Nonnull
   OnlineStatus getOnlineStatus(@Nonnull ClientType var1);

   @Nonnull
   EnumSet<ClientType> getActiveClients();

   @Nullable
   String getNickname();

   @Nonnull
   String getEffectiveName();

   @Nullable
   String getAvatarId();

   @Nullable
   default String getAvatarUrl() {
      String avatarId = this.getAvatarId();
      return avatarId == null ? null : this.getAvatarUrl(avatarId.startsWith("a_") ? ImageFormat.ANIMATED_WEBP : ImageFormat.PNG);
   }

   @Nullable
   default String getAvatarUrl(@Nonnull ImageFormat format) {
      ImageProxy proxy = this.getAvatar(format);
      return proxy == null ? null : proxy.getUrl();
   }

   @Nullable
   default ImageProxy getAvatar() {
      String avatarUrl = this.getAvatarUrl();
      return avatarUrl == null ? null : new ImageProxy(avatarUrl);
   }

   @Nullable
   default ImageProxy getAvatar(@Nonnull ImageFormat format) {
      return DiscordAssets.memberAvatar(format, this.getGuild().getId(), this.getId(), this.getAvatarId());
   }

   @Nonnull
   default String getEffectiveAvatarUrl() {
      String avatarUrl = this.getAvatarUrl();
      return avatarUrl == null ? this.getUser().getEffectiveAvatarUrl() : avatarUrl;
   }

   @Nonnull
   default String getEffectiveAvatarUrl(@Nonnull ImageFormat preferredFormat) {
      String avatarUrl = this.getAvatarUrl(preferredFormat);
      return avatarUrl == null ? this.getUser().getEffectiveAvatarUrl(preferredFormat) : avatarUrl;
   }

   @Nonnull
   default ImageProxy getEffectiveAvatar() {
      ImageProxy avatar = this.getAvatar();
      return avatar == null ? this.getUser().getEffectiveAvatar() : avatar;
   }

   @Nonnull
   default ImageProxy getEffectiveAvatar(@Nonnull ImageFormat preferredFormat) {
      ImageProxy avatar = this.getAvatar(preferredFormat);
      return avatar == null ? this.getUser().getEffectiveAvatar(preferredFormat) : avatar;
   }

   @Nonnull
   @Unmodifiable List<Role> getRoles();

   @Nonnull
   @Unmodifiable Set<Role> getUnsortedRoles();

   @Nonnull
   RoleColors getColors();

   /** @deprecated */
   @Nullable
   @Deprecated
   @ReplaceWith("getColors().getPrimary()")
   default Color getColor() {
      return this.getColors().getPrimary();
   }

   /** @deprecated */
   @Deprecated
   @ReplaceWith("getColors().getPrimaryRaw()")
   default int getColorRaw() {
      return this.getColors().getPrimaryRaw();
   }

   int getFlagsRaw();

   @Nonnull
   default EnumSet<MemberFlag> getFlags() {
      return Member.MemberFlag.fromRaw(this.getFlagsRaw());
   }

   boolean canInteract(@Nonnull Member var1);

   boolean canInteract(@Nonnull Role var1);

   boolean canInteract(@Nonnull RichCustomEmoji var1);

   boolean isOwner();

   @Incubating
   boolean isPending();

   @Nullable
   DefaultGuildChannelUnion getDefaultChannel();

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> ban(int deletionTimeframe, @Nonnull TimeUnit unit) {
      return this.getGuild().ban(this, deletionTimeframe, unit);
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> kick() {
      return this.getGuild().kick(this);
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> timeoutFor(long amount, @Nonnull TimeUnit unit) {
      return this.getGuild().timeoutFor(this, amount, unit);
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> timeoutFor(@Nonnull Duration duration) {
      return this.getGuild().timeoutFor(this, duration);
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> timeoutUntil(@Nonnull TemporalAccessor temporal) {
      return this.getGuild().timeoutUntil(this, temporal);
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> removeTimeout() {
      return this.getGuild().removeTimeout(this);
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> mute(boolean mute) {
      return this.getGuild().mute(this, mute);
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> deafen(boolean deafen) {
      return this.getGuild().deafen(this, deafen);
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> modifyNickname(@Nullable String nickname) {
      return this.getGuild().modifyNickname(this, nickname);
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> modifyFlags(@Nonnull Collection<MemberFlag> newFlags) {
      Checks.noneNull(newFlags, "Flags");
      if (!this.getGuild().getSelfMember().hasPermission(new Permission[]{Permission.MODERATE_MEMBERS})) {
         throw new InsufficientPermissionException(this.getGuild(), Permission.MODERATE_MEMBERS);
      } else {
         int flags = this.getFlagsRaw();
         EnumSet<MemberFlag> updated = Helpers.<MemberFlag>copyEnumSet(MemberFlag.class, newFlags);

         for(MemberFlag flag : Member.MemberFlag.values()) {
            if (flag.modifiable) {
               if (updated.contains(flag)) {
                  flags |= flag.raw;
               } else {
                  flags &= ~flag.raw;
               }
            }
         }

         DataObject body = DataObject.empty().put("flags", flags);
         Route.CompiledRoute route = Route.Guilds.MODIFY_MEMBER.compile(this.getGuild().getId(), this.getId());
         return new AuditableRestActionImpl<Void>(this.getJDA(), route, body);
      }
   }

   public static enum MemberFlag {
      DID_REJOIN(1, false),
      COMPLETED_ONBOARDING(2, false),
      BYPASSES_VERIFICATION(4, true),
      STARTED_ONBOARDING(8, false);

      private final int raw;
      private final boolean modifiable;

      private MemberFlag(int raw, boolean modifiable) {
         this.raw = raw;
         this.modifiable = modifiable;
      }

      public int getRaw() {
         return this.raw;
      }

      public boolean isModifiable() {
         return this.modifiable;
      }

      @Nonnull
      public static EnumSet<MemberFlag> fromRaw(int raw) {
         EnumSet<MemberFlag> flags = EnumSet.noneOf(MemberFlag.class);

         for(MemberFlag flag : values()) {
            if ((raw & flag.raw) == flag.raw) {
               flags.add(flag);
            }
         }

         return flags;
      }

      public static int toRaw(@Nonnull Collection<MemberFlag> flags) {
         Checks.noneNull(flags, "Flags");
         int raw = 0;

         for(MemberFlag flag : flags) {
            raw |= flag.raw;
         }

         return raw;
      }

      // $FF: synthetic method
      private static MemberFlag[] $values() {
         return new MemberFlag[]{DID_REJOIN, COMPLETED_ONBOARDING, BYPASSES_VERIFICATION, STARTED_ONBOARDING};
      }
   }
}
