package net.dv8tion.jda.api.events;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;

public class ExceptionEvent extends Event {
   protected final Throwable throwable;
   protected final boolean logged;

   public ExceptionEvent(@Nonnull JDA api, @Nonnull Throwable throwable, boolean logged) {
      super(api);
      this.throwable = throwable;
      this.logged = logged;
   }

   public boolean isLogged() {
      return this.logged;
   }

   @Nonnull
   public Throwable getCause() {
      return this.throwable;
   }
}
