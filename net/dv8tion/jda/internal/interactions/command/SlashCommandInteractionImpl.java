package net.dv8tion.jda.internal.interactions.command;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;

public class SlashCommandInteractionImpl extends CommandInteractionImpl implements SlashCommandInteraction {
   public SlashCommandInteractionImpl(JDAImpl jda, DataObject data) {
      super(jda, data);
   }

   @Nonnull
   public MessageChannelUnion getChannel() {
      return (MessageChannelUnion)super.getChannel();
   }
}
