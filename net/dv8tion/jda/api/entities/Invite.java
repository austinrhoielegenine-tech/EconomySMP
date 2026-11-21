package net.dv8tion.jda.api.entities;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.utils.DiscordAssets;
import net.dv8tion.jda.api.utils.ImageFormat;
import net.dv8tion.jda.api.utils.ImageProxy;
import net.dv8tion.jda.internal.entities.InviteImpl;

public interface Invite {
   @Nonnull
   @CheckReturnValue
   static RestAction<Invite> resolve(@Nonnull JDA api, @Nonnull String code) {
      return resolve(api, code, false);
   }

   @Nonnull
   @CheckReturnValue
   static RestAction<Invite> resolve(@Nonnull JDA api, @Nonnull String code, boolean withCounts) {
      return InviteImpl.resolve(api, code, withCounts);
   }

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> delete();

   @Nonnull
   @CheckReturnValue
   RestAction<Invite> expand();

   @Nonnull
   InviteType getType();

   @Nonnull
   TargetType getTargetType();

   @Nullable
   Channel getChannel();

   @Nullable
   Group getGroup();

   @Nullable
   InviteTarget getTarget();

   @Nonnull
   String getCode();

   @Nonnull
   default String getUrl() {
      return "https://discord.gg/" + this.getCode();
   }

   @Nullable
   Guild getGuild();

   @Nullable
   User getInviter();

   @Nonnull
   JDA getJDA();

   int getMaxAge();

   int getMaxUses();

   @Nonnull
   OffsetDateTime getTimeCreated();

   int getUses();

   boolean isExpanded();

   boolean isTemporary();

   boolean isGuest();

   public interface Guild extends ISnowflake {
      @Nullable
      String getVanityCode();

      @Nullable
      default String getVanityUrl() {
         return this.getVanityCode() == null ? null : "https://discord.gg/" + this.getVanityCode();
      }

      @Nullable
      String getBannerId();

      @Nullable
      default String getBannerUrl() {
         String bannerId = this.getBannerId();
         return bannerId == null ? null : this.getBannerUrl(bannerId.startsWith("a_") ? ImageFormat.ANIMATED_WEBP : ImageFormat.PNG);
      }

      @Nullable
      default String getBannerUrl(@Nonnull ImageFormat format) {
         ImageProxy proxy = this.getBanner(format);
         return proxy == null ? null : proxy.getUrl();
      }

      @Nullable
      default ImageProxy getBanner() {
         String bannerUrl = this.getBannerUrl();
         return bannerUrl == null ? null : new ImageProxy(bannerUrl);
      }

      @Nullable
      default ImageProxy getBanner(@Nonnull ImageFormat format) {
         return DiscordAssets.guildBanner(format, this.getId(), this.getBannerId());
      }

      @Nullable
      String getDescription();

      @Nullable
      String getIconId();

      @Nullable
      String getIconUrl();

      @Nullable
      default String getIconUrl(@Nonnull ImageFormat format) {
         ImageProxy proxy = this.getIcon(format);
         return proxy == null ? null : proxy.getUrl();
      }

      @Nullable
      default ImageProxy getIcon() {
         String iconUrl = this.getIconUrl();
         return iconUrl == null ? null : new ImageProxy(iconUrl);
      }

      @Nullable
      default ImageProxy getIcon(@Nonnull ImageFormat format) {
         return DiscordAssets.guildIcon(format, this.getId(), this.getIconId());
      }

      @Nonnull
      String getName();

      @Nullable
      String getSplashId();

      @Nullable
      String getSplashUrl();

      @Nullable
      default String getSplashUrl(@Nonnull ImageFormat format) {
         ImageProxy proxy = this.getSplash(format);
         return proxy == null ? null : proxy.getUrl();
      }

      @Nullable
      default ImageProxy getSplash() {
         String splashUrl = this.getSplashUrl();
         return splashUrl == null ? null : new ImageProxy(splashUrl);
      }

      @Nullable
      default ImageProxy getSplash(@Nonnull ImageFormat format) {
         return DiscordAssets.guildSplash(format, this.getId(), this.getSplashId());
      }

      @Nonnull
      net.dv8tion.jda.api.entities.Guild.VerificationLevel getVerificationLevel();

      @Nonnull
      net.dv8tion.jda.api.entities.Guild.NSFWLevel getNSFWLevel();

      int getOnlineCount();

      int getMemberCount();

      @Nonnull
      Set<String> getFeatures();

      @Nullable
      GuildWelcomeScreen getWelcomeScreen();
   }

   public interface Group extends ISnowflake {
      @Nullable
      String getIconId();

      @Nullable
      String getIconUrl();

      @Nullable
      default String getIconUrl(@Nonnull ImageFormat format) {
         ImageProxy proxy = this.getIcon(format);
         return proxy == null ? null : proxy.getUrl();
      }

      @Nullable
      default ImageProxy getIcon() {
         String iconUrl = this.getIconUrl();
         return iconUrl == null ? null : new ImageProxy(iconUrl);
      }

      @Nullable
      default ImageProxy getIcon(@Nonnull ImageFormat format) {
         return DiscordAssets.channelIcon(format, this.getId(), this.getIconUrl());
      }

      @Nullable
      String getName();

      @Nullable
      List<String> getUsers();
   }

   public interface EmbeddedApplication extends ISnowflake {
      @Nonnull
      String getName();

      @Nonnull
      String getDescription();

      @Nullable
      String getSummary();

      @Nullable
      String getIconId();

      @Nullable
      String getIconUrl();

      @Nullable
      default String getIconUrl(@Nonnull ImageFormat format) {
         ImageProxy proxy = this.getIcon(format);
         return proxy == null ? null : proxy.getUrl();
      }

      @Nullable
      default ImageProxy getIcon() {
         String iconUrl = this.getIconUrl();
         return iconUrl == null ? null : new ImageProxy(iconUrl);
      }

      @Nullable
      default ImageProxy getIcon(@Nonnull ImageFormat format) {
         return DiscordAssets.applicationIcon(format, this.getId(), this.getIconId());
      }

      int getMaxParticipants();
   }

   public static enum InviteType {
      GUILD,
      GROUP,
      UNKNOWN;

      // $FF: synthetic method
      private static InviteType[] $values() {
         return new InviteType[]{GUILD, GROUP, UNKNOWN};
      }
   }

   public static enum TargetType {
      NONE(0),
      STREAM(1),
      EMBEDDED_APPLICATION(2),
      ROLE_SUBSCRIPTIONS_PURCHASE(3),
      UNKNOWN(-1);

      private final int id;

      private TargetType(int id) {
         this.id = id;
      }

      public int getId() {
         return this.id;
      }

      @Nonnull
      public static TargetType fromId(int id) {
         for(TargetType type : values()) {
            if (type.id == id) {
               return type;
            }
         }

         return UNKNOWN;
      }

      // $FF: synthetic method
      private static TargetType[] $values() {
         return new TargetType[]{NONE, STREAM, EMBEDDED_APPLICATION, ROLE_SUBSCRIPTIONS_PURCHASE, UNKNOWN};
      }
   }

   public interface Channel extends ISnowflake {
      @Nonnull
      String getName();

      @Nonnull
      ChannelType getType();
   }

   public interface InviteTarget {
      @Nonnull
      TargetType getType();

      @Nonnull
      String getId();

      long getIdLong();

      @Nullable
      User getUser();

      @Nullable
      EmbeddedApplication getApplication();
   }
}
