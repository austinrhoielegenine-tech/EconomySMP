package net.dv8tion.jda.api.events.message;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public class MessageReceivedEvent extends GenericMessageEvent {
   private final Message message;

   public MessageReceivedEvent(@Nonnull JDA api, long responseNumber, @Nonnull Message message) {
      super(api, responseNumber, message.getIdLong(), message.getChannel());
      this.message = message;
   }

   @Nonnull
   public Message getMessage() {
      return this.message;
   }

   @Nonnull
   public User getAuthor() {
      return this.message.getAuthor();
   }

   @Nullable
   public Member getMember() {
      return this.message.getMember();
   }

   public boolean isWebhookMessage() {
      return this.getMessage().isWebhookMessage();
   }
}
