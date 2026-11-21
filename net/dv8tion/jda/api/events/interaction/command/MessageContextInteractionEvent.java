package net.dv8tion.jda.api.events.interaction.command;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.commands.context.MessageContextInteraction;

public class MessageContextInteractionEvent extends GenericContextInteractionEvent<Message> implements MessageContextInteraction {
   public MessageContextInteractionEvent(@Nonnull JDA api, long responseNumber, @Nonnull MessageContextInteraction interaction) {
      super(api, responseNumber, interaction);
   }

   @Nonnull
   public MessageContextInteraction getInteraction() {
      return (MessageContextInteraction)super.getInteraction();
   }

   @Nullable
   public MessageChannelUnion getChannel() {
      return this.getInteraction().getChannel();
   }
}
