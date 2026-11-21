package net.dv8tion.jda.api.entities;

import java.awt.Color;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.api.utils.DiscordAssets;
import net.dv8tion.jda.api.utils.ImageFormat;
import net.dv8tion.jda.api.utils.ImageProxy;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.internal.entities.UserSnowflakeImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;
import org.jetbrains.annotations.Unmodifiable;

public interface User extends UserSnowflake {
   Pattern USER_TAG = Pattern.compile("(.{2,32})#(\\d{4})");
   /** @deprecated */
   @Deprecated
   String AVATAR_URL = "https://cdn.discordapp.com/avatars/%s/%s.%s";
   /** @deprecated */
   @Deprecated
   String DEFAULT_AVATAR_URL = "https://cdn.discordapp.com/embed/avatars/%s.png";
   /** @deprecated */
   @Deprecated
   String BANNER_URL = "https://cdn.discordapp.com/banners/%s/%s.%s";
   /** @deprecated */
   @Deprecated
   String TAG_BADGE_URL = "https://cdn.discordapp.com/guild-tag-badges/%s/%s.png";
   int DEFAULT_ACCENT_COLOR_RAW = 536870911;

   @Nonnull
   static UserSnowflake fromId(long id) {
      return new UserSnowflakeImpl(id);
   }

   @Nonnull
   static UserSnowflake fromId(@Nonnull String id) {
      return fromId(MiscUtil.parseSnowflake(id));
   }

   @Nonnull
   String getName();

   @Nullable
   String getGlobalName();

   @Nonnull
   default String getEffectiveName() {
      String globalName = this.getGlobalName();
      return globalName != null ? globalName : this.getName();
   }

   @Nonnull
   String getDiscriminator();

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
      return DiscordAssets.userAvatar(format, this.getId(), this.getAvatarId());
   }

   @Nonnull
   default String getEffectiveAvatarUrl() {
      String avatarUrl = this.getAvatarUrl();
      return avatarUrl == null ? this.getDefaultAvatarUrl() : avatarUrl;
   }

   @Nonnull
   default String getEffectiveAvatarUrl(@Nonnull ImageFormat preferredFormat) {
      String avatarUrl = this.getAvatarUrl(preferredFormat);
      return avatarUrl == null ? this.getDefaultAvatarUrl() : avatarUrl;
   }

   @Nonnull
   default ImageProxy getEffectiveAvatar() {
      ImageProxy avatar = this.getAvatar();
      return avatar == null ? this.getDefaultAvatar() : avatar;
   }

   @Nonnull
   default ImageProxy getEffectiveAvatar(@Nonnull ImageFormat preferredFormat) {
      ImageProxy avatar = this.getAvatar(preferredFormat);
      return avatar == null ? this.getDefaultAvatar() : avatar;
   }

   @Nonnull
   @CheckReturnValue
   CacheRestAction<Profile> retrieveProfile();

   @Nonnull
   String getAsTag();

   boolean hasPrivateChannel();

   @Nonnull
   @CheckReturnValue
   CacheRestAction<PrivateChannel> openPrivateChannel();

   @Nonnull
   @Unmodifiable List<Guild> getMutualGuilds();

   boolean isBot();

   boolean isSystem();

   @Nonnull
   JDA getJDA();

   @Nonnull
   EnumSet<UserFlag> getFlags();

   int getFlagsRaw();

   @Nullable
   PrimaryGuild getPrimaryGuild();

   public static class Profile {
      private final long userId;
      private final String bannerId;
      private final int accentColor;

      public Profile(long userId, String bannerId, int accentColor) {
         this.userId = userId;
         this.bannerId = bannerId;
         this.accentColor = accentColor;
      }

      @Nullable
      public String getBannerId() {
         return this.bannerId;
      }

      @Nullable
      public String getBannerUrl() {
         return this.bannerId == null ? null : this.getBannerUrl(this.bannerId.startsWith("a_") ? ImageFormat.ANIMATED_WEBP : ImageFormat.PNG);
      }

      @Nullable
      public String getBannerUrl(@Nonnull ImageFormat format) {
         ImageProxy proxy = this.getBanner(format);
         return proxy == null ? null : proxy.getUrl();
      }

      @Nullable
      public ImageProxy getBanner() {
         String bannerUrl = this.getBannerUrl();
         return bannerUrl == null ? null : new ImageProxy(bannerUrl);
      }

      @Nullable
      public ImageProxy getBanner(@Nonnull ImageFormat format) {
         return DiscordAssets.userBanner(format, Long.toUnsignedString(this.userId), this.getBannerId());
      }

      @Nullable
      public Color getAccentColor() {
         return this.accentColor == 536870911 ? null : new Color(this.accentColor);
      }

      public int getAccentColorRaw() {
         return this.accentColor;
      }

      public String toString() {
         return (new EntityString(this)).addMetadata("userId", this.userId).addMetadata("bannerId", this.bannerId).addMetadata("accentColor", this.accentColor).toString();
      }
   }

   public static enum UserFlag {
      STAFF(0, "Discord Employee"),
      PARTNER(1, "Partnered Server Owner"),
      HYPESQUAD(2, "HypeSquad Events"),
      BUG_HUNTER_LEVEL_1(3, "Bug Hunter Level 1"),
      HYPESQUAD_BRAVERY(6, "HypeSquad Bravery"),
      HYPESQUAD_BRILLIANCE(7, "HypeSquad Brilliance"),
      HYPESQUAD_BALANCE(8, "HypeSquad Balance"),
      EARLY_SUPPORTER(9, "Early Supporter"),
      TEAM_USER(10, "Team User"),
      BUG_HUNTER_LEVEL_2(14, "Bug Hunter Level 2"),
      VERIFIED_BOT(16, "Verified Bot"),
      VERIFIED_DEVELOPER(17, "Early Verified Bot Developer"),
      CERTIFIED_MODERATOR(18, "Discord Certified Moderator"),
      BOT_HTTP_INTERACTIONS(19, "HTTP Interactions Bot"),
      ACTIVE_DEVELOPER(22, "Active Developer"),
      UNKNOWN(-1, "Unknown");

      public static final UserFlag[] EMPTY_FLAGS = new UserFlag[0];
      private final int offset;
      private final int raw;
      private final String name;

      private UserFlag(int offset, @Nonnull String name) {
         this.offset = offset;
         this.raw = 1 << offset;
         this.name = name;
      }

      @Nonnull
      public String getName() {
         return this.name;
      }

      public int getOffset() {
         return this.offset;
      }

      public int getRawValue() {
         return this.raw;
      }

      @Nonnull
      public static UserFlag getFromOffset(int offset) {
         for(UserFlag flag : values()) {
            if (flag.offset == offset) {
               return flag;
            }
         }

         return UNKNOWN;
      }

      @Nonnull
      public static EnumSet<UserFlag> getFlags(int flags) {
         EnumSet<UserFlag> foundFlags = EnumSet.noneOf(UserFlag.class);
         if (flags == 0) {
            return foundFlags;
         } else {
            for(UserFlag flag : values()) {
               if (flag != UNKNOWN && (flags & flag.raw) == flag.raw) {
                  foundFlags.add(flag);
               }
            }

            return foundFlags;
         }
      }

      public static int getRaw(@Nonnull UserFlag... flags) {
         Checks.noneNull(flags, "UserFlags");
         int raw = 0;

         for(UserFlag flag : flags) {
            if (flag != null && flag != UNKNOWN) {
               raw |= flag.raw;
            }
         }

         return raw;
      }

      public static int getRaw(@Nonnull Collection<UserFlag> flags) {
         Checks.notNull(flags, "Flag Collection");
         return getRaw((UserFlag[])flags.toArray(EMPTY_FLAGS));
      }

      // $FF: synthetic method
      private static UserFlag[] $values() {
         return new UserFlag[]{STAFF, PARTNER, HYPESQUAD, BUG_HUNTER_LEVEL_1, HYPESQUAD_BRAVERY, HYPESQUAD_BRILLIANCE, HYPESQUAD_BALANCE, EARLY_SUPPORTER, TEAM_USER, BUG_HUNTER_LEVEL_2, VERIFIED_BOT, VERIFIED_DEVELOPER, CERTIFIED_MODERATOR, BOT_HTTP_INTERACTIONS, ACTIVE_DEVELOPER, UNKNOWN};
      }
   }

   public static class PrimaryGuild implements ISnowflake {
      private final long guildId;
      private final boolean identityEnabled;
      private final String tag;
      private final String badge;

      public PrimaryGuild(long guildId, boolean identityEnabled, String tag, String badge) {
         this.guildId = guildId;
         this.identityEnabled = identityEnabled;
         this.tag = tag;
         this.badge = badge;
      }

      public long getIdLong() {
         return this.guildId;
      }

      public boolean isIdentityEnabled() {
         return this.identityEnabled;
      }

      @Nullable
      public String getTag() {
         return this.tag;
      }

      @Nullable
      public String getBadgeHash() {
         return this.badge;
      }

      @Nullable
      public String getBadgeUrl() {
         return this.getBadgeUrl(ImageFormat.PNG);
      }

      @Nullable
      public String getBadgeUrl(@Nonnull ImageFormat format) {
         ImageProxy proxy = this.getBadge(format);
         return proxy == null ? null : proxy.getUrl();
      }

      @Nullable
      public ImageProxy getBadge() {
         String badgeUrl = this.getBadgeUrl();
         return badgeUrl == null ? null : new ImageProxy(badgeUrl);
      }

      @Nullable
      public ImageProxy getBadge(@Nonnull ImageFormat format) {
         return DiscordAssets.userTagBadge(format, Long.toUnsignedString(this.guildId), this.getBadgeHash());
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof PrimaryGuild)) {
            return false;
         } else {
            PrimaryGuild other = (PrimaryGuild)obj;
            return this.guildId == other.guildId && this.identityEnabled == other.identityEnabled && Objects.equals(this.tag, other.tag) && Objects.equals(this.badge, other.badge);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.guildId, this.identityEnabled, this.tag, this.badge});
      }

      public String toString() {
         return (new EntityString(this)).addMetadata("guildId", this.guildId).addMetadata("identityEnabled", this.identityEnabled).addMetadata("tag", this.tag).addMetadata("badge", this.badge).toString();
      }
   }
}
