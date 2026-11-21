package net.dv8tion.jda.api.entities;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.utils.DiscordAssets;
import net.dv8tion.jda.api.utils.ImageFormat;
import net.dv8tion.jda.api.utils.ImageProxy;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.Unmodifiable;

public interface ApplicationInfo extends ISnowflake {
   int MAX_DESCRIPTION_LENGTH = 400;
   int MAX_URL_LENGTH = 2048;
   int MAX_TAGS = 5;
   int MAX_TAG_LENGTH = 20;

   boolean doesBotRequireCodeGrant();

   @Nonnull
   String getDescription();

   @Nullable
   String getTermsOfServiceUrl();

   @Nullable
   String getPrivacyPolicyUrl();

   @Nullable
   String getIconId();

   @Nullable
   String getIconUrl();

   @Nullable
   default String getIconUrl(@Nonnull ImageFormat format) {
      ImageProxy icon = this.getIcon(format);
      return icon == null ? null : icon.getUrl();
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

   @Nullable
   ApplicationTeam getTeam();

   @Nonnull
   default ApplicationInfo setRequiredScopes(@Nonnull String... scopes) {
      Checks.noneNull(scopes, "Scopes");
      return this.setRequiredScopes((Collection)Arrays.asList(scopes));
   }

   @Nonnull
   ApplicationInfo setRequiredScopes(@Nonnull Collection<String> var1);

   @Nonnull
   default String getInviteUrl(@Nullable Collection<Permission> permissions) {
      return this.getInviteUrl((String)null, permissions);
   }

   @Nonnull
   default String getInviteUrl(@Nullable Permission... permissions) {
      return this.getInviteUrl((String)null, permissions);
   }

   @Nonnull
   String getInviteUrl(@Nullable String var1, @Nullable Collection<Permission> var2);

   @Nonnull
   default String getInviteUrl(long guildId, @Nullable Collection<Permission> permissions) {
      return this.getInviteUrl(Long.toUnsignedString(guildId), permissions);
   }

   @Nonnull
   default String getInviteUrl(@Nullable String guildId, @Nullable Permission... permissions) {
      return this.getInviteUrl(guildId, permissions == null ? null : Arrays.asList(permissions));
   }

   @Nonnull
   default String getInviteUrl(long guildId, @Nullable Permission... permissions) {
      return this.getInviteUrl(Long.toUnsignedString(guildId), permissions);
   }

   @Nonnull
   JDA getJDA();

   @Nonnull
   String getName();

   @Nonnull
   User getOwner();

   boolean isBotPublic();

   @Nonnull
   @Unmodifiable List<String> getTags();

   @Nonnull
   @Unmodifiable List<String> getRedirectUris();

   @Nullable
   String getInteractionsEndpointUrl();

   @Nullable
   String getRoleConnectionsVerificationUrl();

   @Nullable
   String getCustomAuthorizationUrl();

   @Nonnull
   @Unmodifiable List<String> getScopes();

   @Nonnull
   EnumSet<Permission> getPermissions();

   long getPermissionsRaw();

   @Nonnull
   default EnumSet<Flag> getFlags() {
      return ApplicationInfo.Flag.fromRaw(this.getFlagsRaw());
   }

   long getFlagsRaw();

   long getUserInstallCount();

   @Nonnull
   Map<IntegrationType, IntegrationTypeConfiguration> getIntegrationTypesConfig();

   public static enum Flag {
      GATEWAY_PRESENCE(4096L),
      GATEWAY_PRESENCE_LIMITED(8192L),
      GATEWAY_GUILD_MEMBERS(16384L),
      GATEWAY_GUILD_MEMBERS_LIMITED(32768L),
      VERIFICATION_PENDING_GUILD_LIMIT(65536L),
      EMBEDDED(131072L),
      GATEWAY_MESSAGE_CONTENT(262144L),
      GATEWAY_MESSAGE_CONTENT_LIMITED(524288L);

      private final long value;

      private Flag(long value) {
         this.value = value;
      }

      @Nonnull
      public static EnumSet<Flag> fromRaw(long raw) {
         EnumSet<Flag> set = EnumSet.noneOf(Flag.class);

         for(Flag flag : values()) {
            if ((raw & flag.value) != 0L) {
               set.add(flag);
            }
         }

         return set;
      }

      // $FF: synthetic method
      private static Flag[] $values() {
         return new Flag[]{GATEWAY_PRESENCE, GATEWAY_PRESENCE_LIMITED, GATEWAY_GUILD_MEMBERS, GATEWAY_GUILD_MEMBERS_LIMITED, VERIFICATION_PENDING_GUILD_LIMIT, EMBEDDED, GATEWAY_MESSAGE_CONTENT, GATEWAY_MESSAGE_CONTENT_LIMITED};
      }
   }

   public interface InstallParameters {
      @Nonnull
      List<String> getScopes();

      @Nonnull
      Set<Permission> getPermissions();
   }

   public interface IntegrationTypeConfiguration {
      @Nullable
      InstallParameters getInstallParameters();
   }
}
