package net.dv8tion.jda.api.events.message.react;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;

public class MessageReactionRemoveAllEvent extends GenericMessageEvent {
   public MessageReactionRemoveAllEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull MessageChannel channel) {
      super(api, responseNumber, messageId, channel);
   }
}
