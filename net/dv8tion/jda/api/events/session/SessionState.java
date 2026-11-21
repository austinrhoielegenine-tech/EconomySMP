package net.dv8tion.jda.api.events.session;

public enum SessionState {
   READY,
   INVALIDATED,
   DISCONNECTED,
   RESUMED,
   RECREATED,
   SHUTDOWN;

   // $FF: synthetic method
   private static SessionState[] $values() {
      return new SessionState[]{READY, INVALIDATED, DISCONNECTED, RESUMED, RECREATED, SHUTDOWN};
   }
}
