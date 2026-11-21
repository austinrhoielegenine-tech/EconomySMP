package net.dv8tion.jda.api.events.message.react;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.requests.CompletedRestAction;

public class GenericMessageReactionEvent extends GenericMessageEvent {
   protected final long userId;
   protected User issuer;
   protected Member member;
   protected MessageReaction reaction;

   public GenericMessageReactionEvent(@Nonnull JDA api, long responseNumber, @Nullable User user, @Nullable Member member, @Nonnull MessageReaction reaction, long userId) {
      super(api, responseNumber, reaction.getMessageIdLong(), reaction.getChannel());
      this.userId = userId;
      this.issuer = user;
      this.member = member;
      this.reaction = reaction;
   }

   @Nonnull
   public String getUserId() {
      return Long.toUnsignedString(this.userId);
   }

   public long getUserIdLong() {
      return this.userId;
   }

   @Nullable
   public User getUser() {
      return this.issuer == null && this.isFromType(ChannelType.PRIVATE) ? this.getChannel().asPrivateChannel().getUser() : this.issuer;
   }

   @Nullable
   public Member getMember() {
      return this.member;
   }

   @Nonnull
   public MessageReaction getReaction() {
      return this.reaction;
   }

   @Nonnull
   public EmojiUnion getEmoji() {
      return this.reaction.getEmoji();
   }

   @Nonnull
   @CheckReturnValue
   public RestAction<User> retrieveUser() {
      User user = this.getUser();
      return (RestAction<User>)(user != null ? new CompletedRestAction(this.getJDA(), user) : this.getJDA().retrieveUserById(this.getUserIdLong()));
   }

   @Nonnull
   @CheckReturnValue
   public RestAction<Member> retrieveMember() {
      if (this.member != null) {
         return new CompletedRestAction<Member>(this.getJDA(), this.member);
      } else if (!this.getChannel().getType().isGuild()) {
         throw new IllegalStateException("Cannot retrieve member for a private reaction not from a guild");
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
