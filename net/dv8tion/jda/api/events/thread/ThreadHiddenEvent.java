package net.dv8tion.jda.api.events.thread;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

public class ThreadHiddenEvent extends GenericThreadEvent {
   public ThreadHiddenEvent(@Nonnull JDA api, long responseNumber, ThreadChannel thread) {
      super(api, responseNumber, thread);
   }
}
