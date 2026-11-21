package net.dv8tion.jda.internal.audio;

public enum ConnectionStage {
   CONNECT,
   RECONNECT,
   DISCONNECT;

   // $FF: synthetic method
   private static ConnectionStage[] $values() {
      return new ConnectionStage[]{CONNECT, RECONNECT, DISCONNECT};
   }
}
