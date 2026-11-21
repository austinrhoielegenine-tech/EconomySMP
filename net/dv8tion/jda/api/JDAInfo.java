package net.dv8tion.jda.api;

public class JDAInfo {
   public static final int DISCORD_GATEWAY_VERSION = 10;
   public static final int DISCORD_REST_VERSION = 10;
   public static final int AUDIO_GATEWAY_VERSION = 8;
   public static final String GITHUB = "https://github.com/discord-jda/JDA";
   public static final String VERSION_MAJOR = "6";
   public static final String VERSION_MINOR = "4";
   public static final String VERSION_REVISION = "1";
   public static final String VERSION_CLASSIFIER = null;
   public static final String COMMIT_HASH = "2cda370";
   public static final String VERSION;

   static {
      VERSION = String.format("%s.%s.%s%s%s", "6", "4", "1", VERSION_CLASSIFIER == null ? "" : "-" + VERSION_CLASSIFIER, "2cda370" == null ? "" : "_2cda370");
   }
}
