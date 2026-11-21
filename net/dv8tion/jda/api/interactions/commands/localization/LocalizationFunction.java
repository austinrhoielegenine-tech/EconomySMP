package net.dv8tion.jda.api.interactions.commands.localization;

import java.util.Map;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.interactions.DiscordLocale;

public interface LocalizationFunction {
   @Nonnull
   Map<DiscordLocale, String> apply(@Nonnull String var1);
}
