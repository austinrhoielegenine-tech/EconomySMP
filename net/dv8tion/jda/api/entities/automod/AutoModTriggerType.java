package net.dv8tion.jda.api.entities.automod;

import java.util.EnumSet;
import javax.annotation.Nonnull;
import net.dv8tion.jda.annotations.Incubating;
import net.dv8tion.jda.internal.utils.Helpers;

public enum AutoModTriggerType {
   KEYWORD(1, 6, new AutoModEventType[]{AutoModEventType.MESSAGE_SEND, AutoModEventType.MEMBER_UPDATE}),
   SPAM(3, 1, new AutoModEventType[]{AutoModEventType.MESSAGE_SEND}),
   KEYWORD_PRESET(4, 1, new AutoModEventType[]{AutoModEventType.MESSAGE_SEND}),
   MENTION_SPAM(5, 1, new AutoModEventType[]{AutoModEventType.MESSAGE_SEND}),
   @Incubating
   MEMBER_PROFILE_KEYWORD(6, 1, new AutoModEventType[]{AutoModEventType.MEMBER_UPDATE}),
   UNKNOWN(-1, 0, new AutoModEventType[0]);

   private final int key;
   private final int maxPerGuild;
   private final EnumSet<AutoModEventType> eventTypes;

   private AutoModTriggerType(int key, int maxPerGuild, AutoModEventType... supportedEvents) {
      this.key = key;
      this.maxPerGuild = maxPerGuild;
      if (supportedEvents.length > 0) {
         this.eventTypes = EnumSet.of(supportedEvents[0], supportedEvents);
      } else {
         this.eventTypes = EnumSet.noneOf(AutoModEventType.class);
      }

   }

   public int getKey() {
      return this.key;
   }

   public int getMaxPerGuild() {
      return this.maxPerGuild;
   }

   @Nonnull
   public EnumSet<AutoModEventType> getSupportedEventTypes() {
      return Helpers.<AutoModEventType>copyEnumSet(AutoModEventType.class, this.eventTypes);
   }

   public boolean isEventTypeSupported(@Nonnull AutoModEventType type) {
      return type != null && this.eventTypes.contains(type);
   }

   @Nonnull
   public static AutoModTriggerType fromKey(int key) {
      for(AutoModTriggerType trigger : values()) {
         if (trigger.key == key) {
            return trigger;
         }
      }

      return UNKNOWN;
   }

   // $FF: synthetic method
   private static AutoModTriggerType[] $values() {
      return new AutoModTriggerType[]{KEYWORD, SPAM, KEYWORD_PRESET, MENTION_SPAM, MEMBER_PROFILE_KEYWORD, UNKNOWN};
   }
}
