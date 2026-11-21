package net.dv8tion.jda.api.utils.cache;

import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.requests.GatewayIntent;

public enum CacheFlag {
   ACTIVITY(GatewayIntent.GUILD_PRESENCES),
   VOICE_STATE(GatewayIntent.GUILD_VOICE_STATES),
   EMOJI(GatewayIntent.GUILD_EXPRESSIONS),
   STICKER(GatewayIntent.GUILD_EXPRESSIONS),
   SOUNDBOARD_SOUNDS(GatewayIntent.GUILD_EXPRESSIONS),
   CLIENT_STATUS(GatewayIntent.GUILD_PRESENCES),
   MEMBER_OVERRIDES,
   ROLE_TAGS,
   FORUM_TAGS,
   ONLINE_STATUS(GatewayIntent.GUILD_PRESENCES),
   SCHEDULED_EVENTS(GatewayIntent.SCHEDULED_EVENTS);

   private static final EnumSet<CacheFlag> privileged = EnumSet.of(ACTIVITY, CLIENT_STATUS, ONLINE_STATUS);
   private final GatewayIntent requiredIntent;

   private CacheFlag() {
      this((GatewayIntent)null);
   }

   private CacheFlag(GatewayIntent requiredIntent) {
      this.requiredIntent = requiredIntent;
   }

   @Nullable
   public GatewayIntent getRequiredIntent() {
      return this.requiredIntent;
   }

   public boolean isPresence() {
      return this.requiredIntent == GatewayIntent.GUILD_PRESENCES;
   }

   @Nonnull
   public static EnumSet<CacheFlag> getPrivileged() {
      return EnumSet.copyOf(privileged);
   }

   // $FF: synthetic method
   private static CacheFlag[] $values() {
      return new CacheFlag[]{ACTIVITY, VOICE_STATE, EMOJI, STICKER, SOUNDBOARD_SOUNDS, CLIENT_STATUS, MEMBER_OVERRIDES, ROLE_TAGS, FORUM_TAGS, ONLINE_STATUS, SCHEDULED_EVENTS};
   }
}
