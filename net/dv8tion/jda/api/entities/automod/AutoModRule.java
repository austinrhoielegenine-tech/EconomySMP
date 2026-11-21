package net.dv8tion.jda.api.entities.automod;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.managers.AutoModRuleManager;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

public interface AutoModRule extends ISnowflake {
   int MAX_RULE_NAME_LENGTH = 100;
   int MAX_KEYWORD_LENGTH = 60;
   int MAX_KEYWORD_AMOUNT = 1000;
   int MAX_ALLOWLIST_CUSTOM_AMOUNT = 100;
   int MAX_ALLOWLIST_PRESET_AMOUNT = 1000;
   int MAX_PATTERN_LENGTH = 260;
   int MAX_PATTERN_AMOUNT = 10;
   int MAX_MENTION_LIMIT = 50;
   int MAX_EXEMPT_ROLES = 20;
   int MAX_EXEMPT_CHANNELS = 50;

   @Nonnull
   Guild getGuild();

   long getCreatorIdLong();

   @Nonnull
   default String getCreatorId() {
      return Long.toUnsignedString(this.getCreatorIdLong());
   }

   @Nonnull
   String getName();

   @Nonnull
   AutoModEventType getEventType();

   @Nonnull
   AutoModTriggerType getTriggerType();

   boolean isEnabled();

   @Nonnull
   List<Role> getExemptRoles();

   @Nonnull
   List<GuildChannel> getExemptChannels();

   @Nonnull
   List<AutoModResponse> getActions();

   @Nonnull
   List<String> getFilteredKeywords();

   @Nonnull
   List<String> getFilteredRegex();

   @Nonnull
   EnumSet<KeywordPreset> getFilteredPresets();

   @Nonnull
   List<String> getAllowlist();

   int getMentionLimit();

   boolean isMentionRaidProtectionEnabled();

   @Nonnull
   @CheckReturnValue
   default AutoModRuleManager getManager() {
      return this.getGuild().modifyAutoModRuleById(this.getId());
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> delete() {
      return this.getGuild().deleteAutoModRuleById(this.getId());
   }

   public static enum KeywordPreset {
      PROFANITY(1),
      SEXUAL_CONTENT(2),
      SLURS(3),
      UNKNOWN(-1);

      private final int key;

      private KeywordPreset(int key) {
         this.key = key;
      }

      public int getKey() {
         return this.key;
      }

      @Nonnull
      public static KeywordPreset fromKey(int key) {
         for(KeywordPreset preset : values()) {
            if (preset.key == key) {
               return preset;
            }
         }

         return UNKNOWN;
      }

      // $FF: synthetic method
      private static KeywordPreset[] $values() {
         return new KeywordPreset[]{PROFANITY, SEXUAL_CONTENT, SLURS, UNKNOWN};
      }
   }
}
