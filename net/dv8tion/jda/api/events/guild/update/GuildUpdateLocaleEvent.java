package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.DiscordLocale;

public class GuildUpdateLocaleEvent extends GenericGuildUpdateEvent<DiscordLocale> {
   public static final String IDENTIFIER = "locale";

   public GuildUpdateLocaleEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull DiscordLocale previous) {
      super(api, responseNumber, guild, previous, guild.getLocale(), "locale");
   }

   @Nonnull
   public DiscordLocale getOldValue() {
      return (DiscordLocale)super.getOldValue();
   }

   @Nonnull
   public DiscordLocale getNewValue() {
      return (DiscordLocale)super.getNewValue();
   }
}
