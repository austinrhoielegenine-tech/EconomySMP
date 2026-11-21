package net.dv8tion.jda.api.events.interaction.command;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public class SlashCommandInteractionEvent extends GenericCommandInteractionEvent implements SlashCommandInteraction {
   private final SlashCommandInteraction interaction;

   public SlashCommandInteractionEvent(@Nonnull JDA api, long responseNumber, @Nonnull SlashCommandInteraction interaction) {
      super(api, responseNumber, interaction);
      this.interaction = interaction;
   }

   @Nonnull
   public SlashCommandInteraction getInteraction() {
      return this.interaction;
   }

   @Nonnull
   public MessageChannelUnion getChannel() {
      return this.interaction.getChannel();
   }
}
