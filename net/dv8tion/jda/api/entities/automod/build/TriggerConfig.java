package net.dv8tion.jda.api.entities.automod.build;

import java.util.Collection;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.automod.AutoModRule;
import net.dv8tion.jda.api.entities.automod.AutoModTriggerType;
import net.dv8tion.jda.api.utils.data.SerializableData;

public interface TriggerConfig extends SerializableData {
   @Nonnull
   AutoModTriggerType getType();

   @Nonnull
   static MentionSpamTriggerConfig mentionSpam(int mentionLimit) {
      return new MentionSpamTriggerConfig(mentionLimit);
   }

   @Nonnull
   static AntiSpamTriggerConfig antiSpam() {
      return new AntiSpamTriggerConfig();
   }

   @Nonnull
   static CustomKeywordTriggerConfig keywordFilter(@Nonnull Collection<String> keywords) {
      return (new CustomKeywordTriggerConfig()).addKeywords(keywords);
   }

   @Nonnull
   static CustomKeywordTriggerConfig keywordFilter(@Nonnull String... keywords) {
      return (new CustomKeywordTriggerConfig()).addKeywords(keywords);
   }

   @Nonnull
   static CustomKeywordTriggerConfig patternFilter(@Nonnull Collection<String> patterns) {
      return (new CustomKeywordTriggerConfig()).addPatterns(patterns);
   }

   @Nonnull
   static CustomKeywordTriggerConfig patternFilter(@Nonnull String... patterns) {
      return (new CustomKeywordTriggerConfig()).addPatterns(patterns);
   }

   @Nonnull
   static PresetKeywordTriggerConfig presetKeywordFilter(@Nonnull Collection<AutoModRule.KeywordPreset> presets) {
      return (new PresetKeywordTriggerConfig()).enablePresets(presets);
   }

   @Nonnull
   static PresetKeywordTriggerConfig presetKeywordFilter(@Nonnull AutoModRule.KeywordPreset... presets) {
      return (new PresetKeywordTriggerConfig()).enablePresets(presets);
   }
}
