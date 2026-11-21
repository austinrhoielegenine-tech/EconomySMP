package net.dv8tion.jda.api.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.Contract;

public final class DiscordAssets {
   private DiscordAssets() {
   }

   @Contract("_, _, null -> null; _, _, !null -> !null")
   public static ImageProxy applicationIcon(@Nonnull ImageFormat format, @Nonnull String applicationId, @Nullable String iconId) {
      Checks.notNull(format, "Format");
      Checks.isSnowflake(applicationId, "Application ID");
      if (iconId == null) {
         return null;
      } else {
         HttpUrl.Builder builder = newUrl().addEncodedPathSegment("app-icons").addPathSegment(applicationId);
         return format.finishProxy(builder, iconId);
      }
   }

   @Contract("_, _, null -> null; _, _, !null -> !null")
   public static ImageProxy applicationCover(@Nonnull ImageFormat format, @Nonnull String applicationId, @Nullable String coverId) {
      Checks.notNull(format, "Format");
      Checks.isSnowflake(applicationId, "Application ID");
      if (coverId == null) {
         return null;
      } else {
         HttpUrl.Builder builder = newUrl().addEncodedPathSegment("application").addPathSegment(applicationId);
         return format.finishProxy(builder, coverId);
      }
   }

   @Contract("_, _, null -> null; _, _, !null -> !null")
   public static ImageProxy applicationTeamIcon(@Nonnull ImageFormat format, @Nonnull String teamId, @Nullable String iconId) {
      Checks.notNull(format, "Format");
      Checks.isSnowflake(teamId, "Team ID");
      if (iconId == null) {
         return null;
      } else {
         HttpUrl.Builder builder = newUrl().addEncodedPathSegment("team-icons").addPathSegment(teamId);
         return format.finishProxy(builder, iconId);
      }
   }

   @Contract("_, _, null -> null; _, _, !null -> !null")
   public static ImageProxy channelIcon(@Nonnull ImageFormat format, @Nonnull String channelId, @Nullable String iconId) {
      Checks.notNull(format, "Format");
      Checks.isSnowflake(channelId, "Channel ID");
      if (iconId == null) {
         return null;
      } else {
         HttpUrl.Builder builder = newUrl().addEncodedPathSegment("channel-icons").addPathSegment(channelId);
         return format.finishProxy(builder, iconId);
      }
   }

   @Nonnull
   public static ImageProxy customEmoji(@Nonnull ImageFormat format, @Nonnull String id) {
      Checks.notNull(format, "Format");
      Checks.isSnowflake(id, "ID");
      HttpUrl.Builder builder = newUrl().addEncodedPathSegment("emojis");
      return format.finishProxy(builder, id);
   }

   @Contract("_, _, null -> null; _, _, !null -> !null")
   public static ImageProxy guildIcon(@Nonnull ImageFormat format, @Nonnull String guildId, @Nullable String iconId) {
      Checks.notNull(format, "Format");
      Checks.isSnowflake(guildId, "Guild ID");
      if (iconId == null) {
         return null;
      } else {
         HttpUrl.Builder builder = newUrl().addEncodedPathSegment("icons").addPathSegment(guildId);
         return format.finishProxy(builder, iconId);
      }
   }

   @Contract("_, _, null -> null; _, _, !null -> !null")
   public static ImageProxy guildSplash(@Nonnull ImageFormat format, @Nonnull String guildId, @Nullable String splashId) {
      Checks.notNull(format, "Format");
      Checks.isSnowflake(guildId, "Guild ID");
      if (splashId == null) {
         return null;
      } else {
         HttpUrl.Builder builder = newUrl().addEncodedPathSegment("splashes").addPathSegment(guildId);
         return format.finishProxy(builder, splashId);
      }
   }

   @Contract("_, _, null -> null; _, _, !null -> !null")
   public static ImageProxy guildBanner(@Nonnull ImageFormat format, @Nonnull String guildId, @Nullable String bannerId) {
      Checks.notNull(format, "Format");
      Checks.isSnowflake(guildId, "Guild ID");
      if (bannerId == null) {
         return null;
      } else {
         HttpUrl.Builder builder = newUrl().addEncodedPathSegment("banners").addPathSegment(guildId);
         return format.finishProxy(builder, bannerId);
      }
   }

   @Contract("_, _, _, null -> null; _, _, _, !null -> !null")
   public static ImageProxy memberAvatar(@Nonnull ImageFormat format, @Nonnull String guildId, @Nonnull String userId, @Nullable String avatarId) {
      Checks.notNull(format, "Format");
      Checks.isSnowflake(guildId, "Guild ID");
      Checks.isSnowflake(userId, "User ID");
      if (avatarId == null) {
         return null;
      } else {
         HttpUrl.Builder builder = newUrl().addEncodedPathSegment("guilds").addPathSegment(guildId).addEncodedPathSegment("users").addPathSegment(userId).addEncodedPathSegment("avatars");
         return format.finishProxy(builder, avatarId);
      }
   }

   @Contract("_, _, null -> null; _, _, !null -> !null")
   public static ImageProxy roleIcon(@Nonnull ImageFormat format, @Nonnull String roleId, @Nullable String iconId) {
      Checks.notNull(format, "Format");
      Checks.isSnowflake(roleId, "Role ID");
      if (iconId == null) {
         return null;
      } else {
         HttpUrl.Builder builder = newUrl().addEncodedPathSegment("role-icons").addPathSegment(roleId);
         return format.finishProxy(builder, iconId);
      }
   }

   @Contract("_, _, null -> null; _, _, !null -> !null")
   public static ImageProxy scheduledEventCoverImage(@Nonnull ImageFormat format, @Nonnull String eventId, @Nullable String imageId) {
      Checks.notNull(format, "Format");
      Checks.isSnowflake(eventId, "Event ID");
      if (imageId == null) {
         return null;
      } else {
         HttpUrl.Builder builder = newUrl().addEncodedPathSegment("guild-events").addPathSegment(eventId);
         return format.finishProxy(builder, imageId);
      }
   }

   @Contract("_, null -> null; _, !null -> !null")
   public static ImageProxy stickerPackBanner(@Nonnull ImageFormat format, @Nullable String bannerId) {
      Checks.notNull(format, "Format");
      if (bannerId == null) {
         return null;
      } else {
         HttpUrl.Builder builder = newUrl().addEncodedPathSegment("app-assets").addEncodedPathSegment("710982414301790216").addEncodedPathSegment("store");
         return format.finishProxy(builder, bannerId);
      }
   }

   @Contract("_, _, null -> null; _, _, !null -> !null")
   public static ImageProxy userAvatar(@Nonnull ImageFormat format, @Nonnull String userId, @Nullable String avatarId) {
      Checks.notNull(format, "Format");
      Checks.isSnowflake(userId, "User ID");
      if (avatarId == null) {
         return null;
      } else {
         HttpUrl.Builder builder = newUrl().addEncodedPathSegment("avatars").addPathSegment(userId);
         return format.finishProxy(builder, avatarId);
      }
   }

   @Contract("_, _, null -> null; _, _, !null -> !null")
   public static ImageProxy userBanner(@Nonnull ImageFormat format, @Nonnull String userId, @Nullable String bannerId) {
      Checks.notNull(format, "Format");
      Checks.isSnowflake(userId, "User ID");
      if (bannerId == null) {
         return null;
      } else {
         HttpUrl.Builder builder = newUrl().addEncodedPathSegment("banners").addPathSegment(userId);
         return format.finishProxy(builder, bannerId);
      }
   }

   @Nonnull
   public static ImageProxy userDefaultAvatar(@Nonnull ImageFormat format, @Nonnull String avatarId) {
      Checks.notNull(format, "Format");
      Checks.notNull(avatarId, "Avatar ID");
      HttpUrl.Builder builder = newUrl().addEncodedPathSegment("embed").addEncodedPathSegment("avatars");
      return format.finishProxy(builder, avatarId);
   }

   @Contract("_, _, null -> null; _, _, !null -> !null")
   public static ImageProxy userTagBadge(@Nonnull ImageFormat format, @Nonnull String guildId, @Nullable String badgeId) {
      Checks.notNull(format, "Format");
      Checks.isSnowflake(guildId, "Guild ID");
      if (badgeId == null) {
         return null;
      } else {
         HttpUrl.Builder builder = newUrl().addEncodedPathSegment("guild-tag-badges").addPathSegment(guildId);
         return format.finishProxy(builder, badgeId);
      }
   }

   private static HttpUrl.Builder newUrl() {
      return (new HttpUrl.Builder()).scheme("https").host("cdn.discordapp.com");
   }
}
