package net.dv8tion.jda.internal.utils;

public class ShutdownReason {
   public static final ShutdownReason USER_SHUTDOWN = new ShutdownReason("User requested shutdown");
   public static final ShutdownReason INVALID_SHARDS = new ShutdownReason("Invalid shard configuration");
   public static final ShutdownReason DISALLOWED_INTENTS = new ShutdownReason("You tried turning on an intent you aren't allowed to use. For more information check https://jda.wiki/using-jda/troubleshooting/#im-getting-closecode4014-disallowed-intents");
   protected final String reason;

   public ShutdownReason(String reason) {
      this.reason = reason;
   }

   public String getReason() {
      return this.reason;
   }

   public String toString() {
      return this.reason;
   }
}
