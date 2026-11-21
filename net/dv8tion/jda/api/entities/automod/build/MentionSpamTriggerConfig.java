package net.dv8tion.jda.api.entities.automod.build;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.automod.AutoModTriggerType;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Checks;

public class MentionSpamTriggerConfig extends AbstractTriggerConfig<MentionSpamTriggerConfig> implements TriggerConfig {
   private int mentionLimit;
   private boolean isMentionRaidProtectionEnabled;

   public MentionSpamTriggerConfig(int mentionLimit) {
      super(AutoModTriggerType.MENTION_SPAM);
      this.mentionLimit = mentionLimit;
   }

   @Nonnull
   public MentionSpamTriggerConfig setMentionLimit(int mentionLimit) {
      Checks.positive(mentionLimit, "Mention Limit");
      Checks.check(mentionLimit <= 50, "Mention Limit cannot be higher than %d. Provided: %d", 50, mentionLimit);
      this.mentionLimit = mentionLimit;
      return this;
   }

   @Nonnull
   public MentionSpamTriggerConfig setMentionRaidProtectionEnabled(boolean enabled) {
      this.isMentionRaidProtectionEnabled = enabled;
      return this;
   }

   @Nonnull
   public DataObject toData() {
      DataObject data = super.toData();
      data.put("mention_total_limit", this.mentionLimit);
      data.put("mention_raid_protection_enabled", this.isMentionRaidProtectionEnabled);
      return data;
   }
}
