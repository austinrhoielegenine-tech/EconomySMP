package net.dv8tion.jda.api.events.message;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class MessageDeleteEvent extends GenericMessageEvent {
   public MessageDeleteEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull MessageChannel channel) {
      super(api, responseNumber, messageId, channel);
   }
}
