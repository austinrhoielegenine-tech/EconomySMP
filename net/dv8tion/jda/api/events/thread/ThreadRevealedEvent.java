package net.dv8tion.jda.api.events.thread;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

public class ThreadRevealedEvent extends GenericThreadEvent {
   public ThreadRevealedEvent(@Nonnull JDA api, long responseNumber, ThreadChannel thread) {
      super(api, responseNumber, thread);
   }
}
