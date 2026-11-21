package net.dv8tion.jda.api.events.user;

import java.time.OffsetDateTime;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public class UserTypingEvent extends GenericUserEvent {
   private final Member member;
   private final MessageChannel channel;
   private final OffsetDateTime timestamp;

   public UserTypingEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user, @Nonnull MessageChannel channel, @Nonnull OffsetDateTime timestamp, @Nullable Member member) {
      super(api, responseNumber, user);
      this.member = member;
      this.channel = channel;
      this.timestamp = timestamp;
   }

   @Nonnull
   public OffsetDateTime getTimestamp() {
      return this.timestamp;
   }

   @Nonnull
   public MessageChannelUnion getChannel() {
      return (MessageChannelUnion)this.channel;
   }

   public boolean isFromType(@Nonnull ChannelType type) {
      return this.channel.getType() == type;
   }

   @Nonnull
   public ChannelType getType() {
      return this.channel.getType();
   }

   @Nullable
   public Guild getGuild() {
      return this.getType().isGuild() ? this.member.getGuild() : null;
   }

   @Nullable
   public Member getMember() {
      return this.member;
   }
}
