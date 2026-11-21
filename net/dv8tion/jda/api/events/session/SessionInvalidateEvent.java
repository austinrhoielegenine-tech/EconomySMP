package net.dv8tion.jda.api.events.session;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;

public class SessionInvalidateEvent extends GenericSessionEvent {
   public SessionInvalidateEvent(@Nonnull JDA api) {
      super(api, SessionState.INVALIDATED);
   }
}
