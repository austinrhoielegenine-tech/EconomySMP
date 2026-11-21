package net.dv8tion.jda.internal.interactions.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.commands.context.MessageContextInteraction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;

public class MessageContextInteractionImpl extends ContextInteractionImpl<Message> implements MessageContextInteraction {
   public MessageContextInteractionImpl(JDAImpl jda, DataObject data) {
      super(jda, data);
   }

   protected Message parse(DataObject interaction, DataObject resolved) {
      DataObject messages = resolved.getObject("messages");
      DataObject message = messages.getObject((String)messages.keys().iterator().next());
      Guild guild = this.getGuild();
      MessageChannel channel = this.getChannel();
      return this.api.getEntityBuilder().createMessageBestEffort(message, channel, guild);
   }

   public MessageChannelUnion getChannel() {
      return (MessageChannelUnion)super.getChannel();
   }
}
