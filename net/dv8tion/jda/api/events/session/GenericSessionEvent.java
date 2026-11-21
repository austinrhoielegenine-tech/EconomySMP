package net.dv8tion.jda.api.events.session;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.Event;

public abstract class GenericSessionEvent extends Event {
   protected final SessionState state;

   public GenericSessionEvent(@Nonnull JDA api, @Nonnull SessionState state) {
      super(api);
      this.state = state;
   }

   @Nonnull
   public SessionState getState() {
      return this.state;
   }
}
