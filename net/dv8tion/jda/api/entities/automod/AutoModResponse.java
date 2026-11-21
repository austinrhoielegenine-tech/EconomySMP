package net.dv8tion.jda.api.entities.automod;

import java.time.Duration;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.annotations.Incubating;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.entities.automod.AutoModResponseImpl;
import net.dv8tion.jda.internal.utils.Checks;

public interface AutoModResponse extends SerializableData {
   int MAX_CUSTOM_MESSAGE_LENGTH = 150;

   @Nonnull
   Type getType();

   @Nullable
   GuildMessageChannel getChannel();

   @Nullable
   String getCustomMessage();

   @Nullable
   Duration getTimeoutDuration();

   @Nonnull
   static AutoModResponse blockMessage() {
      return blockMessage((String)null);
   }

   @Nonnull
   static AutoModResponse blockMessage(@Nullable String customMessage) {
      return new AutoModResponseImpl(AutoModResponse.Type.BLOCK_MESSAGE, customMessage);
   }

   @Nonnull
   static AutoModResponse sendAlert(@Nonnull GuildMessageChannel channel) {
      Checks.notNull(channel, "Channel");
      return new AutoModResponseImpl(AutoModResponse.Type.SEND_ALERT_MESSAGE, channel);
   }

   @Nonnull
   static AutoModResponse timeoutMember(@Nonnull Duration duration) {
      Checks.notNull(duration, "Duration");
      Checks.check(!duration.isNegative() && !duration.isZero(), "Duration must be positive");
      return new AutoModResponseImpl(AutoModResponse.Type.TIMEOUT, duration);
   }

   @Nonnull
   @Incubating
   static AutoModResponse blockMemberInteraction() {
      return new AutoModResponseImpl(AutoModResponse.Type.BLOCK_MEMBER_INTERACTION);
   }

   public static enum Type {
      BLOCK_MESSAGE(1, EnumSet.of(AutoModTriggerType.KEYWORD, AutoModTriggerType.KEYWORD_PRESET, AutoModTriggerType.SPAM, AutoModTriggerType.MENTION_SPAM)),
      SEND_ALERT_MESSAGE(2, EnumSet.of(AutoModTriggerType.KEYWORD, AutoModTriggerType.KEYWORD_PRESET, AutoModTriggerType.SPAM, AutoModTriggerType.MENTION_SPAM)),
      TIMEOUT(3, EnumSet.of(AutoModTriggerType.KEYWORD, AutoModTriggerType.MENTION_SPAM)),
      @Incubating
      BLOCK_MEMBER_INTERACTION(4, EnumSet.of(AutoModTriggerType.MEMBER_PROFILE_KEYWORD)),
      UNKNOWN(-1, EnumSet.noneOf(AutoModTriggerType.class));

      private final int key;
      private final EnumSet<AutoModTriggerType> supportedTypes;

      private Type(int key) {
         this.key = key;
         this.supportedTypes = EnumSet.complementOf(EnumSet.of(AutoModTriggerType.UNKNOWN));
      }

      private Type(int key, EnumSet<AutoModTriggerType> supportedTypes) {
         this.key = key;
         this.supportedTypes = supportedTypes;
      }

      public int getKey() {
         return this.key;
      }

      @Nonnull
      public EnumSet<AutoModTriggerType> getSupportedTypes() {
         return EnumSet.copyOf(this.supportedTypes);
      }

      public boolean isSupportedTrigger(@Nonnull AutoModTriggerType type) {
         Checks.notNull(type, "AutoModTriggerType");
         return this.supportedTypes.contains(type);
      }

      @Nonnull
      public static Type fromKey(int key) {
         for(Type type : values()) {
            if (type.key == key) {
               return type;
            }
         }

         return UNKNOWN;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{BLOCK_MESSAGE, SEND_ALERT_MESSAGE, TIMEOUT, BLOCK_MEMBER_INTERACTION, UNKNOWN};
      }
   }
}
