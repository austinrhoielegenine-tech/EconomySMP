package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class GuildUpdateRulesChannelEvent extends GenericGuildUpdateEvent<TextChannel> {
   public static final String IDENTIFIER = "rules_channel";

   public GuildUpdateRulesChannelEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable TextChannel oldRulesChannel) {
      super(api, responseNumber, guild, oldRulesChannel, guild.getRulesChannel(), "rules_channel");
   }

   @Nullable
   public TextChannel getOldRulesChannel() {
      return (TextChannel)this.getOldValue();
   }

   @Nullable
   public TextChannel getNewRulesChannel() {
      return (TextChannel)this.getNewValue();
   }
}
