package net.dv8tion.jda.api.interactions.commands;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public interface SlashCommandInteraction extends CommandInteraction {
   @Nonnull
   MessageChannelUnion getChannel();

   @Nonnull
   default GuildMessageChannelUnion getGuildChannel() {
      return (GuildMessageChannelUnion)CommandInteraction.super.getGuildChannel();
   }
}
