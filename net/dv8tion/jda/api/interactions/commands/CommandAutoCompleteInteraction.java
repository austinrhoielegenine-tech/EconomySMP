package net.dv8tion.jda.api.interactions.commands;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.callbacks.IAutoCompleteCallback;

public interface CommandAutoCompleteInteraction extends IAutoCompleteCallback, CommandInteractionPayload {
   @Nonnull
   AutoCompleteQuery getFocusedOption();

   @Nonnull
   MessageChannelUnion getChannel();

   @Nonnull
   default GuildMessageChannelUnion getGuildChannel() {
      return (GuildMessageChannelUnion)IAutoCompleteCallback.super.getGuildChannel();
   }
}
