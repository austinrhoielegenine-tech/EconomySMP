package net.dv8tion.jda.internal.entities;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.ApplicationTeam;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.utils.ImageFormat;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;

public class ApplicationInfoImpl implements ApplicationInfo {
   private final JDA api;
   private final boolean doesBotRequireCodeGrant;
   private final boolean isBotPublic;
   private final long id;
   private final long flags;
   private final String iconId;
   private final String description;
   private final String termsOfServiceUrl;
   private final String privacyPolicyUrl;
   private final String name;
   private final User owner;
   private final ApplicationTeam team;
   private final List<String> tags;
   private final List<String> redirectUris;
   private final String interactionsEndpointUrl;
   private final String roleConnectionsVerificationUrl;
   private final String customAuthUrl;
   private final long defaultAuthUrlPerms;
   private final List<String> defaultAuthUrlScopes;
   private final long approxUserInstallCount;
   private final Map<IntegrationType, ApplicationInfo.IntegrationTypeConfiguration> integrationTypesConfig;
   private String scopes = "bot";

   public ApplicationInfoImpl(JDA api, String description, boolean doesBotRequireCodeGrant, String iconId, long id, long flags, boolean isBotPublic, String name, String termsOfServiceUrl, String privacyPolicyUrl, User owner, ApplicationTeam team, List<String> tags, List<String> redirectUris, String interactionsEndpointUrl, String roleConnectionsVerificationUrl, String customAuthUrl, long defaultAuthUrlPerms, List<String> defaultAuthUrlScopes, long approxUserInstallCount, Map<IntegrationType, ApplicationInfo.IntegrationTypeConfiguration> integrationTypesConfig) {
      this.api = api;
      this.description = description;
      this.doesBotRequireCodeGrant = doesBotRequireCodeGrant;
      this.iconId = iconId;
      this.id = id;
      this.flags = flags;
      this.isBotPublic = isBotPublic;
      this.name = name;
      this.termsOfServiceUrl = termsOfServiceUrl;
      this.privacyPolicyUrl = privacyPolicyUrl;
      this.owner = owner;
      this.team = team;
      this.tags = Collections.unmodifiableList(tags);
      this.redirectUris = Collections.unmodifiableList(redirectUris);
      this.interactionsEndpointUrl = interactionsEndpointUrl;
      this.roleConnectionsVerificationUrl = roleConnectionsVerificationUrl;
      this.customAuthUrl = customAuthUrl;
      this.defaultAuthUrlPerms = defaultAuthUrlPerms;
      this.defaultAuthUrlScopes = Collections.unmodifiableList(defaultAuthUrlScopes);
      this.approxUserInstallCount = approxUserInstallCount;
      this.integrationTypesConfig = integrationTypesConfig;
   }

   public final boolean doesBotRequireCodeGrant() {
      return this.doesBotRequireCodeGrant;
   }

   public boolean equals(Object obj) {
      return obj instanceof ApplicationInfoImpl && this.id == ((ApplicationInfoImpl)obj).id;
   }

   @Nonnull
   public String getDescription() {
      return this.description;
   }

   public String getTermsOfServiceUrl() {
      return this.termsOfServiceUrl;
   }

   public String getPrivacyPolicyUrl() {
      return this.privacyPolicyUrl;
   }

   public String getIconId() {
      return this.iconId;
   }

   public String getIconUrl() {
      return this.getIconUrl(ImageFormat.PNG);
   }

   @Nonnull
   public ApplicationTeam getTeam() {
      return this.team;
   }

   @Nonnull
   public ApplicationInfo setRequiredScopes(@Nonnull Collection<String> scopes) {
      Checks.noneNull(scopes, "Scopes");
      this.scopes = String.join("+", scopes);
      if (!this.scopes.contains("bot")) {
         if (this.scopes.isEmpty()) {
            this.scopes = "bot";
         } else {
            this.scopes = this.scopes + "+bot";
         }
      }

      return this;
   }

   public long getIdLong() {
      return this.id;
   }

   @Nonnull
   public String getInviteUrl(String guildId, Collection<Permission> permissions) {
      StringBuilder builder = new StringBuilder("https://discord.com/oauth2/authorize?client_id=");
      builder.append(this.getId());
      builder.append("&scope=").append(this.scopes);
      if (permissions != null && !permissions.isEmpty()) {
         builder.append("&permissions=");
         builder.append(Permission.getRaw(permissions));
      }

      if (guildId != null) {
         builder.append("&guild_id=");
         builder.append(guildId);
      }

      return builder.toString();
   }

   @Nonnull
   public JDA getJDA() {
      return this.api;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   @Nonnull
   public User getOwner() {
      return this.owner;
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }

   public final boolean isBotPublic() {
      return this.isBotPublic;
   }

   @Nonnull
   public List<String> getTags() {
      return this.tags;
   }

   @Nonnull
   public List<String> getRedirectUris() {
      return this.redirectUris;
   }

   @Nullable
   public String getInteractionsEndpointUrl() {
      return this.interactionsEndpointUrl;
   }

   @Nullable
   public String getRoleConnectionsVerificationUrl() {
      return this.roleConnectionsVerificationUrl;
   }

   @Nullable
   public String getCustomAuthorizationUrl() {
      return this.customAuthUrl;
   }

   @Nonnull
   public EnumSet<Permission> getPermissions() {
      return Permission.getPermissions(this.defaultAuthUrlPerms);
   }

   public long getPermissionsRaw() {
      return this.defaultAuthUrlPerms;
   }

   public long getFlagsRaw() {
      return this.flags;
   }

   @Nonnull
   public List<String> getScopes() {
      return this.defaultAuthUrlScopes;
   }

   public long getUserInstallCount() {
      return this.approxUserInstallCount;
   }

   @Nonnull
   public Map<IntegrationType, ApplicationInfo.IntegrationTypeConfiguration> getIntegrationTypesConfig() {
      return this.integrationTypesConfig;
   }

   public String toString() {
      return (new EntityString(this)).toString();
   }

   static class IntegrationTypeConfigurationImpl implements ApplicationInfo.IntegrationTypeConfiguration {
      private final ApplicationInfo.InstallParameters installParameters;

      IntegrationTypeConfigurationImpl(ApplicationInfo.InstallParameters installParameters) {
         this.installParameters = installParameters;
      }

      @Nullable
      public ApplicationInfo.InstallParameters getInstallParameters() {
         return this.installParameters;
      }
   }

   static class InstallParametersImpl implements ApplicationInfo.InstallParameters {
      private final List<String> scopes;
      private final Set<Permission> permissions;

      InstallParametersImpl(List<String> scopes, Set<Permission> permissions) {
         this.scopes = Collections.unmodifiableList(scopes);
         this.permissions = Collections.unmodifiableSet(permissions);
      }

      @Nonnull
      public List<String> getScopes() {
         return this.scopes;
      }

      @Nonnull
      public Set<Permission> getPermissions() {
         return this.permissions;
      }
   }
}
