package net.dv8tion.jda.api.events.session;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;

public class SessionResumeEvent extends GenericSessionEvent {
   public SessionResumeEvent(@Nonnull JDA api) {
      super(api, SessionState.RESUMED);
   }
}
