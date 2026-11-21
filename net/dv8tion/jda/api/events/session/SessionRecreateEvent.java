package net.dv8tion.jda.api.events.session;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;

public class SessionRecreateEvent extends GenericSessionEvent {
   public SessionRecreateEvent(@Nonnull JDA api) {
      super(api, SessionState.RECREATED);
   }
}
