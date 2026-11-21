package net.dv8tion.jda.api.requests;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.automod.AutoModExecutionEvent;
import net.dv8tion.jda.api.events.automod.GenericAutoModRuleEvent;
import net.dv8tion.jda.api.events.emoji.GenericEmojiEvent;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.invite.GenericGuildInviteEvent;
import net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.GenericScheduledEventUpdateEvent;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.sticker.GenericGuildStickerEvent;
import net.dv8tion.jda.api.events.user.UserTypingEvent;
import net.dv8tion.jda.api.events.user.update.GenericUserPresenceEvent;
import net.dv8tion.jda.api.events.user.update.GenericUserUpdateEvent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.internal.utils.Checks;

public enum GatewayIntent {
   GUILD_MEMBERS(1),
   GUILD_MODERATION(2),
   GUILD_EXPRESSIONS(3),
   GUILD_WEBHOOKS(5),
   GUILD_INVITES(6),
   GUILD_VOICE_STATES(7),
   GUILD_PRESENCES(8),
   GUILD_MESSAGES(9),
   GUILD_MESSAGE_REACTIONS(10),
   GUILD_MESSAGE_TYPING(11),
   DIRECT_MESSAGES(12),
   DIRECT_MESSAGE_REACTIONS(13),
   DIRECT_MESSAGE_TYPING(14),
   MESSAGE_CONTENT(15),
   SCHEDULED_EVENTS(16),
   AUTO_MODERATION_CONFIGURATION(20),
   AUTO_MODERATION_EXECUTION(21),
   GUILD_MESSAGE_POLLS(24),
   DIRECT_MESSAGE_POLLS(25);

   public static final int ALL_INTENTS = 1 | getRaw(EnumSet.allOf(GatewayIntent.class));
   public static final int DEFAULT = ALL_INTENTS & ~getRaw(GUILD_MEMBERS, GUILD_PRESENCES, MESSAGE_CONTENT, GUILD_WEBHOOKS, GUILD_MESSAGE_TYPING, DIRECT_MESSAGE_TYPING);
   private final int rawValue;
   private final int offset;

   private GatewayIntent(int offset) {
      this.offset = offset;
      this.rawValue = 1 << offset;
   }

   public int getRawValue() {
      return this.rawValue;
   }

   public int getOffset() {
      return this.offset;
   }

   @Nonnull
   public static EnumSet<GatewayIntent> getIntents(int raw) {
      EnumSet<GatewayIntent> set = EnumSet.noneOf(GatewayIntent.class);

      for(GatewayIntent intent : values()) {
         if ((intent.getRawValue() & raw) != 0) {
            set.add(intent);
         }
      }

      return set;
   }

   public static int getRaw(@Nonnull Collection<GatewayIntent> set) {
      int raw = 0;

      for(GatewayIntent intent : set) {
         raw |= intent.rawValue;
      }

      return raw;
   }

   public static int getRaw(@Nonnull GatewayIntent intent, @Nonnull GatewayIntent... set) {
      Checks.notNull(intent, "Intent");
      Checks.notNull(set, "Intent");
      return getRaw(EnumSet.of(intent, set));
   }

   @Nonnull
   public static EnumSet<GatewayIntent> fromCacheFlags(@Nonnull CacheFlag flag, @Nonnull CacheFlag... other) {
      Checks.notNull(flag, "CacheFlag");
      Checks.noneNull(other, "CacheFlag");
      return fromCacheFlags(EnumSet.of(flag, other));
   }

   @Nonnull
   public static EnumSet<GatewayIntent> fromCacheFlags(@Nonnull Collection<CacheFlag> flags) {
      EnumSet<GatewayIntent> intents = EnumSet.noneOf(GatewayIntent.class);

      for(CacheFlag flag : flags) {
         Checks.notNull(flag, "CacheFlag");
         GatewayIntent intent = flag.getRequiredIntent();
         if (intent != null) {
            intents.add(intent);
         }
      }

      return intents;
   }

   @Nonnull
   @SafeVarargs
   public static EnumSet<GatewayIntent> fromEvents(@Nonnull Class<? extends GenericEvent>... events) {
      Checks.noneNull(events, "Event");
      return fromEvents(Arrays.asList(events));
   }

   @Nonnull
   public static EnumSet<GatewayIntent> fromEvents(@Nonnull Collection<Class<? extends GenericEvent>> events) {
      EnumSet<GatewayIntent> intents = EnumSet.noneOf(GatewayIntent.class);

      for(Class<? extends GenericEvent> event : events) {
         Checks.notNull(event, "Event");
         if (GenericUserPresenceEvent.class.isAssignableFrom(event)) {
            intents.add(GUILD_PRESENCES);
         } else if (!GenericUserUpdateEvent.class.isAssignableFrom(event) && !GenericGuildMemberEvent.class.isAssignableFrom(event) && !GuildMemberRemoveEvent.class.isAssignableFrom(event)) {
            if (!GuildBanEvent.class.isAssignableFrom(event) && !GuildUnbanEvent.class.isAssignableFrom(event) && !GuildAuditLogEntryCreateEvent.class.isAssignableFrom(event)) {
               if (!GenericEmojiEvent.class.isAssignableFrom(event) && !GenericGuildStickerEvent.class.isAssignableFrom(event)) {
                  if (GenericScheduledEventUpdateEvent.class.isAssignableFrom(event)) {
                     intents.add(SCHEDULED_EVENTS);
                  } else if (GenericGuildInviteEvent.class.isAssignableFrom(event)) {
                     intents.add(GUILD_INVITES);
                  } else if (GenericGuildVoiceEvent.class.isAssignableFrom(event)) {
                     intents.add(GUILD_VOICE_STATES);
                  } else if (MessageBulkDeleteEvent.class.isAssignableFrom(event)) {
                     intents.add(GUILD_MESSAGES);
                  } else if (GenericMessageReactionEvent.class.isAssignableFrom(event)) {
                     Collections.addAll(intents, new GatewayIntent[]{GUILD_MESSAGE_REACTIONS, DIRECT_MESSAGE_REACTIONS});
                  } else if (GenericMessageEvent.class.isAssignableFrom(event)) {
                     Collections.addAll(intents, new GatewayIntent[]{GUILD_MESSAGES, DIRECT_MESSAGES});
                  } else if (UserTypingEvent.class.isAssignableFrom(event)) {
                     Collections.addAll(intents, new GatewayIntent[]{GUILD_MESSAGE_TYPING, DIRECT_MESSAGE_TYPING});
                  } else if (AutoModExecutionEvent.class.isAssignableFrom(event)) {
                     intents.add(AUTO_MODERATION_EXECUTION);
                  } else if (GenericAutoModRuleEvent.class.isAssignableFrom(event)) {
                     intents.add(AUTO_MODERATION_CONFIGURATION);
                  }
               } else {
                  intents.add(GUILD_EXPRESSIONS);
               }
            } else {
               intents.add(GUILD_MODERATION);
            }
         } else {
            intents.add(GUILD_MEMBERS);
         }
      }

      return intents;
   }

   @Nonnull
   public static EnumSet<GatewayIntent> from(@Nonnull Collection<Class<? extends GenericEvent>> events, @Nonnull Collection<CacheFlag> flags) {
      EnumSet<GatewayIntent> intents = fromEvents(events);
      intents.addAll(fromCacheFlags(flags));
      return intents;
   }

   // $FF: synthetic method
   private static GatewayIntent[] $values() {
      return new GatewayIntent[]{GUILD_MEMBERS, GUILD_MODERATION, GUILD_EXPRESSIONS, GUILD_WEBHOOKS, GUILD_INVITES, GUILD_VOICE_STATES, GUILD_PRESENCES, GUILD_MESSAGES, GUILD_MESSAGE_REACTIONS, GUILD_MESSAGE_TYPING, DIRECT_MESSAGES, DIRECT_MESSAGE_REACTIONS, DIRECT_MESSAGE_TYPING, MESSAGE_CONTENT, SCHEDULED_EVENTS, AUTO_MODERATION_CONFIGURATION, AUTO_MODERATION_EXECUTION, GUILD_MESSAGE_POLLS, DIRECT_MESSAGE_POLLS};
   }
}
