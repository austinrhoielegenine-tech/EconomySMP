package net.dv8tion.jda.api.managers;

import java.util.Arrays;
import java.util.Collection;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.guild.SystemChannelFlag;
import net.dv8tion.jda.internal.utils.Checks;

public interface GuildManager extends Manager<GuildManager> {
   long NAME = 1L;
   long ICON = 2L;
   long SPLASH = 4L;
   long AFK_CHANNEL = 8L;
   long AFK_TIMEOUT = 16L;
   long SYSTEM_CHANNEL = 32L;
   long NOTIFICATION_LEVEL = 128L;
   long EXPLICIT_CONTENT_LEVEL = 256L;
   long VERIFICATION_LEVEL = 512L;
   long BANNER = 1024L;
   long DESCRIPTION = 2048L;
   long RULES_CHANNEL = 4096L;
   long COMMUNITY_UPDATES_CHANNEL = 8192L;
   long BOOST_PROGRESS_BAR_ENABLED = 16384L;
   long FEATURES = 32768L;
   long SAFETY_ALERTS_CHANNEL = 65536L;
   long SYSTEM_CHANNEL_FLAGS = 131072L;

   @Nonnull
   @CheckReturnValue
   GuildManager reset(long var1);

   @Nonnull
   @CheckReturnValue
   GuildManager reset(@Nonnull long... var1);

   @Nonnull
   Guild getGuild();

   @Nonnull
   @CheckReturnValue
   GuildManager setName(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   GuildManager setIcon(@Nullable Icon var1);

   @Nonnull
   @CheckReturnValue
   GuildManager setSplash(@Nullable Icon var1);

   @Nonnull
   @CheckReturnValue
   GuildManager setAfkChannel(@Nullable VoiceChannel var1);

   @Nonnull
   @CheckReturnValue
   GuildManager setSystemChannel(@Nullable TextChannel var1);

   @Nonnull
   @CheckReturnValue
   GuildManager setRulesChannel(@Nullable TextChannel var1);

   @Nonnull
   @CheckReturnValue
   GuildManager setCommunityUpdatesChannel(@Nullable TextChannel var1);

   @Nonnull
   @CheckReturnValue
   GuildManager setSafetyAlertsChannel(@Nullable TextChannel var1);

   @Nonnull
   @CheckReturnValue
   GuildManager setAfkTimeout(@Nonnull Guild.Timeout var1);

   @Nonnull
   @CheckReturnValue
   GuildManager setVerificationLevel(@Nonnull Guild.VerificationLevel var1);

   @Nonnull
   @CheckReturnValue
   GuildManager setDefaultNotificationLevel(@Nonnull Guild.NotificationLevel var1);

   @Nonnull
   @CheckReturnValue
   GuildManager setExplicitContentLevel(@Nonnull Guild.ExplicitContentLevel var1);

   @Nonnull
   @CheckReturnValue
   GuildManager setBanner(@Nullable Icon var1);

   @Nonnull
   @CheckReturnValue
   GuildManager setDescription(@Nullable String var1);

   @Nonnull
   @CheckReturnValue
   GuildManager setBoostProgressBarEnabled(boolean var1);

   @Nonnull
   @CheckReturnValue
   GuildManager setFeatures(@Nonnull Collection<String> var1);

   @Nonnull
   @CheckReturnValue
   GuildManager addFeatures(@Nonnull Collection<String> var1);

   @Nonnull
   @CheckReturnValue
   default GuildManager addFeatures(@Nonnull String... features) {
      Checks.noneNull(features, "Features");
      return this.addFeatures(Arrays.asList(features));
   }

   @Nonnull
   @CheckReturnValue
   GuildManager removeFeatures(@Nonnull Collection<String> var1);

   @Nonnull
   @CheckReturnValue
   default GuildManager removeFeatures(@Nonnull String... features) {
      Checks.noneNull(features, "Features");
      return this.removeFeatures(Arrays.asList(features));
   }

   @Nonnull
   @CheckReturnValue
   GuildManager setSystemChannelFlags(@Nonnull Collection<SystemChannelFlag> var1);

   @Nonnull
   @CheckReturnValue
   default GuildManager setSystemChannelFlags(@Nonnull SystemChannelFlag... flags) {
      Checks.noneNull(flags, "System channel flags");
      return flags.length == 0 ? this : this.setSystemChannelFlags(Arrays.asList(flags));
   }

   @Nonnull
   @CheckReturnValue
   GuildManager enableSystemChannelFlags(@Nonnull Collection<SystemChannelFlag> var1);

   @Nonnull
   @CheckReturnValue
   default GuildManager enableSystemChannelFlags(@Nonnull SystemChannelFlag... flags) {
      Checks.noneNull(flags, "System channel flags");
      return flags.length == 0 ? this : this.enableSystemChannelFlags(Arrays.asList(flags));
   }

   @Nonnull
   @CheckReturnValue
   GuildManager disableSystemChannelFlags(@Nonnull Collection<SystemChannelFlag> var1);

   @Nonnull
   @CheckReturnValue
   default GuildManager disableSystemChannelFlags(@Nonnull SystemChannelFlag... flags) {
      Checks.noneNull(flags, "System channel flags");
      return flags.length == 0 ? this : this.disableSystemChannelFlags(Arrays.asList(flags));
   }

   @Nonnull
   @CheckReturnValue
   default GuildManager setInvitesDisabled(boolean disabled) {
      return disabled ? this.addFeatures("INVITES_DISABLED") : this.removeFeatures("INVITES_DISABLED");
   }
}
