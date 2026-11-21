package net.dv8tion.jda.api.managers;

import java.util.Arrays;
import java.util.Collection;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.automod.AutoModResponse;
import net.dv8tion.jda.api.entities.automod.build.TriggerConfig;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.internal.utils.Checks;

public interface AutoModRuleManager extends Manager<AutoModRuleManager> {
   long NAME = 1L;
   long ENABLED = 2L;
   long RESPONSE = 4L;
   long EXEMPT_ROLES = 8L;
   long EXEMPT_CHANNELS = 16L;
   long TRIGGER_METADATA = 32L;

   @Nonnull
   @CheckReturnValue
   AutoModRuleManager reset(long var1);

   @Nonnull
   @CheckReturnValue
   AutoModRuleManager reset(@Nonnull long... var1);

   @Nonnull
   @CheckReturnValue
   AutoModRuleManager setName(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   AutoModRuleManager setEnabled(boolean var1);

   @Nonnull
   @CheckReturnValue
   AutoModRuleManager setResponses(@Nonnull Collection<? extends AutoModResponse> var1);

   @Nonnull
   @CheckReturnValue
   default AutoModRuleManager setResponses(@Nonnull AutoModResponse... responses) {
      Checks.noneNull(responses, "Responses");
      return this.setResponses(Arrays.asList(responses));
   }

   @Nonnull
   @CheckReturnValue
   AutoModRuleManager setExemptRoles(@Nonnull Collection<Role> var1);

   @Nonnull
   @CheckReturnValue
   default AutoModRuleManager setExemptRoles(@Nonnull Role... roles) {
      Checks.noneNull(roles, "Roles");
      return this.setExemptRoles(Arrays.asList(roles));
   }

   @Nonnull
   @CheckReturnValue
   AutoModRuleManager setExemptChannels(@Nonnull Collection<? extends GuildChannel> var1);

   @Nonnull
   @CheckReturnValue
   default AutoModRuleManager setExemptChannels(@Nonnull GuildChannel... channels) {
      Checks.noneNull(channels, "Channels");
      return this.setExemptChannels(Arrays.asList(channels));
   }

   @Nonnull
   @CheckReturnValue
   AutoModRuleManager setTriggerConfig(@Nonnull TriggerConfig var1);
}
