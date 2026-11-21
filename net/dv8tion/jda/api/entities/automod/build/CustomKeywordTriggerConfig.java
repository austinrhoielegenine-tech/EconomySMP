package net.dv8tion.jda.api.entities.automod.build;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.automod.AutoModTriggerType;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Checks;

public class CustomKeywordTriggerConfig extends AbstractKeywordTriggerConfig<CustomKeywordTriggerConfig> {
   protected final Set<String> keywords = new HashSet();
   protected final Set<String> patterns = new HashSet();

   protected CustomKeywordTriggerConfig() {
      super(AutoModTriggerType.KEYWORD);
   }

   @Nonnull
   public CustomKeywordTriggerConfig addKeywords(@Nonnull String... keywords) {
      Checks.noneNull(keywords, "Keywords");
      Checks.check(this.keywords.size() + keywords.length <= 1000, "Cannot add more than %d keywords!", (int)1000);

      for(String keyword : keywords) {
         checkKeyword(keyword);
      }

      Collections.addAll(this.keywords, keywords);
      return this;
   }

   @Nonnull
   public CustomKeywordTriggerConfig addKeywords(@Nonnull Collection<String> keywords) {
      Checks.noneNull(keywords, "Keywords");
      Checks.check(this.keywords.size() + keywords.size() <= 1000, "Cannot add more than %d keywords!", (int)1000);

      for(String keyword : keywords) {
         checkKeyword(keyword);
      }

      this.keywords.addAll(keywords);
      return this;
   }

   @Nonnull
   public CustomKeywordTriggerConfig setKeywords(@Nonnull Collection<String> keywords) {
      Checks.noneNull(keywords, "Keywords");
      Checks.check(keywords.size() <= 1000, "Cannot add more than %d keywords!", (int)1000);

      for(String keyword : keywords) {
         checkKeyword(keyword);
      }

      this.keywords.clear();
      this.keywords.addAll(keywords);
      return this;
   }

   @Nonnull
   public CustomKeywordTriggerConfig addPatterns(@Nonnull String... patterns) {
      Checks.noneNull(patterns, "Patterns");
      Checks.check(this.patterns.size() + patterns.length <= 10, "Cannot add more than %d patterns!", (int)10);

      for(String pattern : patterns) {
         checkPattern(pattern);
      }

      Collections.addAll(this.patterns, patterns);
      return this;
   }

   @Nonnull
   public CustomKeywordTriggerConfig addPatterns(@Nonnull Collection<String> patterns) {
      Checks.noneNull(patterns, "Patterns");
      Checks.check(this.patterns.size() + patterns.size() <= 10, "Cannot add more than %d patterns!", (int)10);

      for(String pattern : patterns) {
         checkPattern(pattern);
      }

      this.patterns.addAll(patterns);
      return this;
   }

   @Nonnull
   public CustomKeywordTriggerConfig setPatterns(@Nonnull Collection<String> patterns) {
      Checks.noneNull(patterns, "Patterns");
      Checks.check(patterns.size() <= 10, "Cannot add more than %d patterns!", (int)10);

      for(String pattern : patterns) {
         checkPattern(pattern);
      }

      this.patterns.clear();
      this.patterns.addAll(patterns);
      return this;
   }

   protected static void checkPattern(String pattern) {
      Checks.notBlank(pattern, "Pattern");
      Checks.notLonger(pattern, 260, "Pattern");
   }

   protected int maxAllowListAmount() {
      return 100;
   }

   @Nonnull
   public DataObject toData() {
      Checks.check(!this.keywords.isEmpty() || !this.patterns.isEmpty(), "Must have at least one keyword or pattern!");
      DataObject data = super.toData();
      data.put("keyword_filter", DataArray.fromCollection(this.keywords));
      data.put("regex_patterns", DataArray.fromCollection(this.patterns));
      return data;
   }
}
