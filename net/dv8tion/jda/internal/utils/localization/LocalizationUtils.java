package net.dv8tion.jda.internal.utils.localization;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationMap;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.interactions.command.localization.UnmodifiableLocalizationMap;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

public class LocalizationUtils {
   public static final Logger LOG = JDALogger.getLog(LocalizationUtils.class);

   @Nonnull
   public static Map<DiscordLocale, String> mapFromData(@Nonnull DataObject data) {
      Checks.notNull(data, "Data");
      Map<DiscordLocale, String> map = new HashMap();

      for(String key : data.keys()) {
         DiscordLocale locale = DiscordLocale.from(key);
         if (locale == DiscordLocale.UNKNOWN) {
            LOG.debug((String)"Discord provided an unknown locale, locale tag: {}", (Object)key);
         } else {
            map.put(locale, data.getString(key));
         }
      }

      return map;
   }

   @Nonnull
   public static Map<DiscordLocale, String> mapFromProperty(@Nonnull DataObject json, @Nonnull String localizationProperty) {
      return (Map)json.optObject(localizationProperty).map(LocalizationUtils::mapFromData).orElse(Collections.emptyMap());
   }

   @Nonnull
   public static LocalizationMap unmodifiableFromProperty(@Nonnull DataObject json, @Nonnull String localizationProperty) {
      return new UnmodifiableLocalizationMap(mapFromProperty(json, localizationProperty));
   }
}
