package net.dv8tion.jda.api.managers;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;

public interface ApplicationManager extends Manager<ApplicationManager> {
   long DESCRIPTION = 1L;
   long ICON = 2L;
   long COVER_IMAGE = 4L;
   long INTERACTIONS_ENDPOINT_URL = 8L;
   long TAGS = 16L;
   long CUSTOM_INSTALL_URL = 32L;
   long INSTALL_PARAMS = 64L;
   long INTEGRATION_TYPES_CONFIG = 128L;

   @Nonnull
   @CheckReturnValue
   ApplicationManager reset(long var1);

   @Nonnull
   @CheckReturnValue
   ApplicationManager reset(@Nonnull long... var1);

   @Nonnull
   @CheckReturnValue
   ApplicationManager setDescription(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   ApplicationManager setIcon(@Nullable Icon var1);

   @Nonnull
   @CheckReturnValue
   ApplicationManager setCoverImage(@Nullable Icon var1);

   @Nonnull
   @CheckReturnValue
   ApplicationManager setTags(@Nonnull Collection<String> var1);

   @Nonnull
   @CheckReturnValue
   ApplicationManager setInteractionsEndpointUrl(@Nullable String var1);

   @Nonnull
   @CheckReturnValue
   ApplicationManager setCustomInstallUrl(@Nullable String var1);

   @Nonnull
   @CheckReturnValue
   ApplicationManager setInstallParams(@Nullable IntegrationTypeConfig var1);

   @Nonnull
   @CheckReturnValue
   ApplicationManager setIntegrationTypeConfig(@Nullable Map<IntegrationType, IntegrationTypeConfig> var1);

   public static class IntegrationTypeConfig implements SerializableData {
      private final Set<String> scopes;
      private final long permissions;

      private IntegrationTypeConfig(Set<String> scopes, long permissions) {
         this.scopes = scopes;
         this.permissions = permissions;
      }

      @Nonnull
      public static IntegrationTypeConfig of(@Nullable Collection<String> scopes, @Nullable Collection<Permission> permissions) {
         return new IntegrationTypeConfig((Set)(scopes == null ? Collections.emptySet() : new LinkedHashSet(scopes)), permissions == null ? 0L : Permission.getRaw(permissions));
      }

      @Nonnull
      public DataObject toData() {
         return DataObject.empty().put("scopes", this.scopes).put("permissions", this.permissions);
      }
   }
}
