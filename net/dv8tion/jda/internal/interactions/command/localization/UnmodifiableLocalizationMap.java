package net.dv8tion.jda.internal.interactions.command.localization;

import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationMap;

public class UnmodifiableLocalizationMap extends LocalizationMap {
   public static final Consumer<String> UNMODIFIABLE_CHECK = (s) -> {
      throw new IllegalStateException("This LocalizationMap is unmodifiable.");
   };

   public UnmodifiableLocalizationMap(@Nonnull Map<DiscordLocale, String> map) {
      super(UNMODIFIABLE_CHECK);
      this.map.putAll(map);
   }
}
