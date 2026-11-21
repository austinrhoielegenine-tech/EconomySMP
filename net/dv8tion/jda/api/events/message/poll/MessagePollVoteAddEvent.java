package net.dv8tion.jda.api.events.message.poll;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class MessagePollVoteAddEvent extends GenericMessagePollVoteEvent {
   public MessagePollVoteAddEvent(@Nonnull MessageChannel channel, long responseNumber, long messageId, long userId, long answerId) {
      super(channel, responseNumber, messageId, userId, answerId);
   }
}
