package net.dv8tion.jda.api.interactions.commands.context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public interface MessageContextInteraction extends ContextInteraction<Message> {
   @Nonnull
   default ContextInteraction.ContextTarget getTargetType() {
      return ContextInteraction.ContextTarget.MESSAGE;
   }

   @Nullable
   MessageChannelUnion getChannel();

   @Nonnull
   default GuildMessageChannelUnion getGuildChannel() {
      return (GuildMessageChannelUnion)ContextInteraction.super.getGuildChannel();
   }
}
