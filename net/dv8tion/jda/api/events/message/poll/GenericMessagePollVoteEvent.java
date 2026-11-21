package net.dv8tion.jda.api.events.message.poll;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.requests.RestAction;

public class GenericMessagePollVoteEvent extends GenericMessageEvent {
   protected final long userId;
   protected final long answerId;

   public GenericMessagePollVoteEvent(@Nonnull MessageChannel channel, long responseNumber, long messageId, long userId, long answerId) {
      super(channel.getJDA(), responseNumber, messageId, channel);
      this.userId = userId;
      this.answerId = answerId;
   }

   @Nonnull
   public String getUserId() {
      return Long.toUnsignedString(this.userId);
   }

   public long getUserIdLong() {
      return this.userId;
   }

   public long getAnswerId() {
      return this.answerId;
   }

   @Nonnull
   @CheckReturnValue
   public RestAction<User> retrieveUser() {
      return this.getJDA().retrieveUserById(this.getUserIdLong());
   }

   @Nonnull
   @CheckReturnValue
   public RestAction<Member> retrieveMember() {
      if (!this.getChannel().getType().isGuild()) {
         throw new IllegalStateException("Cannot retrieve member for a vote that happened outside of a guild");
      } else {
         return this.getGuild().retrieveMemberById(this.getUserIdLong());
      }
   }

   @Nonnull
   @CheckReturnValue
   public RestAction<Message> retrieveMessage() {
      return this.getChannel().retrieveMessageById(this.getMessageId());
   }
}
