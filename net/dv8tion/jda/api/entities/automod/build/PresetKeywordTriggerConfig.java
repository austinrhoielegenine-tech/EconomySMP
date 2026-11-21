package net.dv8tion.jda.api.entities.automod.build;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.automod.AutoModRule;
import net.dv8tion.jda.api.entities.automod.AutoModTriggerType;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;

public class PresetKeywordTriggerConfig extends AbstractKeywordTriggerConfig<PresetKeywordTriggerConfig> {
   private final EnumSet<AutoModRule.KeywordPreset> presets = EnumSet.noneOf(AutoModRule.KeywordPreset.class);

   protected PresetKeywordTriggerConfig() {
      super(AutoModTriggerType.KEYWORD_PRESET);
   }

   @Nonnull
   public PresetKeywordTriggerConfig enablePresets(@Nonnull AutoModRule.KeywordPreset... presets) {
      Checks.notNull(presets, "Presets");

      for(AutoModRule.KeywordPreset preset : presets) {
         checkKnown(preset);
      }

      Collections.addAll(this.presets, presets);
      return this;
   }

   @Nonnull
   public PresetKeywordTriggerConfig enablePresets(@Nonnull Collection<AutoModRule.KeywordPreset> presets) {
      Checks.notNull(presets, "Presets");
      presets.forEach(PresetKeywordTriggerConfig::checkKnown);
      this.presets.addAll(presets);
      return this;
   }

   @Nonnull
   public PresetKeywordTriggerConfig disablePresets(@Nonnull AutoModRule.KeywordPreset... presets) {
      Checks.noneNull(presets, "Presets");

      for(AutoModRule.KeywordPreset preset : presets) {
         this.presets.remove(preset);
      }

      return this;
   }

   @Nonnull
   public PresetKeywordTriggerConfig disablePresets(@Nonnull Collection<AutoModRule.KeywordPreset> presets) {
      Checks.noneNull(presets, "Presets");
      this.presets.removeAll(presets);
      return this;
   }

   protected int maxAllowListAmount() {
      return 1000;
   }

   private static void checkKnown(AutoModRule.KeywordPreset preset) {
      Checks.notNull(preset, "Presets");
      Checks.check(preset != AutoModRule.KeywordPreset.UNKNOWN, "Cannot use unknown preset");
   }

   @Nonnull
   public DataObject toData() {
      DataObject data = super.toData();
      data.put("presets", this.presets.stream().map(AutoModRule.KeywordPreset::getKey).collect(Helpers.toDataArray()));
      return data;
   }
}
