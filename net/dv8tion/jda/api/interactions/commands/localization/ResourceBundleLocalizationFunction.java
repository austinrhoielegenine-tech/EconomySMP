package net.dv8tion.jda.api.interactions.commands.localization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.internal.utils.Checks;

public class ResourceBundleLocalizationFunction implements LocalizationFunction {
   private final Set<Bundle> bundles;

   private ResourceBundleLocalizationFunction(Set<Bundle> bundles) {
      this.bundles = bundles;
   }

   @Nonnull
   public Map<DiscordLocale, String> apply(@Nonnull String localizationKey) {
      Map<DiscordLocale, String> map = new HashMap();

      for(Bundle bundle : this.bundles) {
         ResourceBundle resourceBundle = bundle.resourceBundle;
         if (resourceBundle.containsKey(localizationKey)) {
            map.put(bundle.targetLocale, resourceBundle.getString(localizationKey));
         }
      }

      return map;
   }

   @Nonnull
   public static Builder fromBundle(@Nonnull ResourceBundle resourceBundle, @Nonnull DiscordLocale locale) {
      return (new Builder()).addBundle(resourceBundle, locale);
   }

   @Nonnull
   public static Builder fromBundles(@Nonnull String baseName, @Nonnull DiscordLocale... locales) {
      return (new Builder()).addBundles(baseName, locales);
   }

   @Nonnull
   public static Builder empty() {
      return new Builder();
   }

   public static class Builder {
      private final Set<Bundle> bundles = new HashSet();

      protected Builder() {
      }

      @Nonnull
      public Builder addBundle(@Nonnull ResourceBundle resourceBundle, @Nonnull DiscordLocale locale) {
         Checks.notNull(resourceBundle, "Resource bundle");
         Checks.notNull(locale, "Locale");
         Checks.check(locale != DiscordLocale.UNKNOWN, "Cannot use UNKNOWN DiscordLocale");
         this.bundles.add(new Bundle(locale, resourceBundle));
         return this;
      }

      @Nonnull
      public Builder addBundles(@Nonnull String baseName, @Nonnull DiscordLocale... locales) {
         Checks.notNull(baseName, "Base name");
         Checks.noneNull(locales, "Locale");

         for(DiscordLocale locale : locales) {
            Checks.check(locale != DiscordLocale.UNKNOWN, "Cannot use UNKNOWN DiscordLocale");
            ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, Locale.forLanguageTag(locale.getLocale()));
            this.bundles.add(new Bundle(locale, resourceBundle));
         }

         return this;
      }

      @Nonnull
      public ResourceBundleLocalizationFunction build() {
         return new ResourceBundleLocalizationFunction(this.bundles);
      }
   }

   private static final class Bundle {
      private final DiscordLocale targetLocale;
      private final ResourceBundle resourceBundle;

      public Bundle(DiscordLocale targetLocale, ResourceBundle resourceBundle) {
         this.targetLocale = targetLocale;
         this.resourceBundle = resourceBundle;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof Bundle)) {
            return false;
         } else {
            Bundle bundle = (Bundle)o;
            return !this.targetLocale.equals(bundle.targetLocale) ? false : this.resourceBundle.equals(bundle.resourceBundle);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.targetLocale, this.resourceBundle});
      }
   }
}
