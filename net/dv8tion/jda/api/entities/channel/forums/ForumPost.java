package net.dv8tion.jda.api.entities.channel.forums;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

public class ForumPost {
   private final Message message;
   private final ThreadChannel thread;

   public ForumPost(@Nonnull Message message, @Nonnull ThreadChannel thread) {
      this.message = message;
      this.thread = thread;
   }

   @Nonnull
   public Message getMessage() {
      return this.message;
   }

   @Nonnull
   public ThreadChannel getThreadChannel() {
      return this.thread;
   }
}
