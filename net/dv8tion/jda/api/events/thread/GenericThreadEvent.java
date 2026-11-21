package net.dv8tion.jda.api.events.thread;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.Event;

public class GenericThreadEvent extends Event {
   protected final ThreadChannel thread;

   public GenericThreadEvent(@Nonnull JDA api, long responseNumber, ThreadChannel thread) {
      super(api, responseNumber);
      this.thread = thread;
   }

   @Nonnull
   public ThreadChannel getThread() {
      return this.thread;
   }

   @Nonnull
   public Guild getGuild() {
      return this.thread.getGuild();
   }
}
