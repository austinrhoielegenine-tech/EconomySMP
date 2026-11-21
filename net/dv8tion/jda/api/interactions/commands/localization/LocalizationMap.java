package net.dv8tion.jda.api.interactions.commands.localization;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

public class LocalizationMap implements SerializableData {
   public static final Logger LOG = JDALogger.getLog(LocalizationMap.class);
   protected final Map<DiscordLocale, String> map = new HashMap();
   private final Consumer<String> checkConsumer;

   public LocalizationMap(@Nonnull Consumer<String> checkConsumer) {
      this.checkConsumer = checkConsumer;
   }

   private void putTranslation(DiscordLocale locale, String translation) {
      Checks.check(locale != DiscordLocale.UNKNOWN, "Cannot put an 'UNKNOWN' DiscordLocale");
      this.map.put(locale, translation);
   }

   @Nonnull
   public DataObject toData() {
      DataObject data = DataObject.empty();
      this.map.forEach((locale, localizedString) -> data.put(locale.getLocale(), localizedString));
      return data;
   }

   public void setTranslation(@Nonnull DiscordLocale locale, @Nonnull String localizedString) {
      Checks.notNull(locale, "Locale");
      Checks.notNull(localizedString, "Localized string");
      this.checkConsumer.accept(localizedString);
      this.putTranslation(locale, localizedString);
   }

   public void setTranslations(@Nonnull Map<DiscordLocale, String> map) {
      Checks.notNull(map, "Map");
      map.forEach((discordLocale, localizedString) -> {
         this.checkConsumer.accept(localizedString);
         this.putTranslation(discordLocale, localizedString);
      });
   }

   @Nullable
   public String get(@Nonnull DiscordLocale locale) {
      Checks.notNull(locale, "Locale");
      return (String)this.map.get(locale);
   }

   @Nonnull
   public Map<DiscordLocale, String> toMap() {
      return Collections.unmodifiableMap(this.map);
   }
}
