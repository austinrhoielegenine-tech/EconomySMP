package net.dv8tion.jda.api.entities.automod.build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.automod.AutoModTriggerType;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Checks;

public abstract class AbstractKeywordTriggerConfig<B extends AbstractKeywordTriggerConfig<B>> extends AbstractTriggerConfig<B> {
   protected final List<String> allowList = new ArrayList();

   protected AbstractKeywordTriggerConfig(AutoModTriggerType triggerType) {
      super(triggerType);
   }

   @Nonnull
   public B addAllowList(@Nonnull String... keywords) {
      Checks.noneNull(keywords, "Keywords");
      Checks.check(this.allowList.size() + keywords.length <= this.maxAllowListAmount(), "Cannot add more than %d keywords!", (Object)this.maxAllowListAmount());
      Arrays.stream(keywords).forEach(AbstractKeywordTriggerConfig::checkKeyword);
      Collections.addAll(this.allowList, keywords);
      return (B)this;
   }

   @Nonnull
   public B addAllowList(@Nonnull Collection<String> keywords) {
      Checks.noneNull(keywords, "Keywords");
      Checks.check(this.allowList.size() + keywords.size() <= this.maxAllowListAmount(), "Cannot add more than %d keywords!", (Object)this.maxAllowListAmount());
      keywords.forEach(AbstractKeywordTriggerConfig::checkKeyword);
      this.allowList.addAll(keywords);
      return (B)this;
   }

   @Nonnull
   public B setAllowList(@Nonnull Collection<String> keywords) {
      Checks.noneNull(keywords, "Keywords");
      Checks.check(keywords.size() <= this.maxAllowListAmount(), "Cannot add more than %d keywords!", (Object)this.maxAllowListAmount());
      keywords.forEach(AbstractKeywordTriggerConfig::checkKeyword);
      this.allowList.clear();
      this.allowList.addAll(keywords);
      return (B)this;
   }

   protected abstract int maxAllowListAmount();

   protected static void checkKeyword(String keyword) {
      Checks.notEmpty(keyword, "Keyword");
      Checks.notLonger(keyword, 60, "Keyword");
   }

   @Nonnull
   public DataObject toData() {
      DataObject data = super.toData();
      data.put("allow_list", DataArray.fromCollection(this.allowList));
      return data;
   }
}
