package net.dv8tion.jda.internal.handle;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.guild.SecurityIncidentActions;
import net.dv8tion.jda.api.entities.guild.SecurityIncidentDetections;
import net.dv8tion.jda.api.entities.guild.SystemChannelFlag;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateAfkChannelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateAfkTimeoutEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBannerEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateCommunityUpdatesChannelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateDescriptionEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateExplicitContentLevelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateFeaturesEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateIconEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateLocaleEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateMFALevelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateMaxMembersEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateMaxPresencesEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNSFWLevelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNotificationLevelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateOwnerEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateRulesChannelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSafetyAlertsChannelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSecurityIncidentActionsEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSecurityIncidentDetectionsEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSplashEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSystemChannelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSystemChannelFlagsEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateVanityCodeEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateVerificationLevelEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.requests.WebSocketClient;

public class GuildUpdateHandler extends SocketHandler {
   public GuildUpdateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      long id = content.getLong("id");
      if (this.getJDA().getGuildSetupController().isLocked(id)) {
         return id;
      } else {
         GuildImpl guild = (GuildImpl)this.getJDA().getGuildById(id);
         if (guild == null) {
            EventCache.LOG.debug((String)"Caching GUILD_UPDATE for guild with id: {}", (Object)id);
            this.getJDA().getEventCache().cache(EventCache.Type.GUILD, id, this.responseNumber, this.allContent, this::handle);
            return null;
         } else {
            int maxMembers = content.getInt("max_members", 0);
            int maxPresences = content.getInt("max_presences", 5000);
            if (guild.getMaxMembers() == 0) {
               guild.setMaxPresences(maxPresences);
               guild.setMaxMembers(maxMembers);
            }

            long ownerId = content.getLong("owner_id");
            int boostCount = content.getInt("premium_subscription_count", 0);
            int boostTier = content.getInt("premium_tier", 0);
            String description = content.getString("description", (String)null);
            String vanityCode = content.getString("vanity_url_code", (String)null);
            String bannerId = content.getString("banner", (String)null);
            String name = content.getString("name");
            String iconId = content.getString("icon", (String)null);
            String splashId = content.getString("splash", (String)null);
            Optional var10000 = content.optObject("incidents_data");
            EntityBuilder var10001 = this.api.getEntityBuilder();
            Objects.requireNonNull(var10001);
            SecurityIncidentActions securityIncidentActions = (SecurityIncidentActions)var10000.map(var10001::createSecurityIncidentsActions).orElse(SecurityIncidentActions.disabled());
            var10000 = content.optObject("incidents_data");
            var10001 = this.api.getEntityBuilder();
            Objects.requireNonNull(var10001);
            SecurityIncidentDetections securityIncidentDetections = (SecurityIncidentDetections)var10000.map(var10001::createSecurityIncidentsDetections).orElse(SecurityIncidentDetections.EMPTY);
            Guild.VerificationLevel verificationLevel = Guild.VerificationLevel.fromKey(content.getInt("verification_level"));
            Guild.NotificationLevel notificationLevel = Guild.NotificationLevel.fromKey(content.getInt("default_message_notifications"));
            Guild.MFALevel mfaLevel = Guild.MFALevel.fromKey(content.getInt("mfa_level"));
            Guild.NSFWLevel nsfwLevel = Guild.NSFWLevel.fromKey(content.getInt("nsfw_level", -1));
            Guild.ExplicitContentLevel explicitContentLevel = Guild.ExplicitContentLevel.fromKey(content.getInt("explicit_content_filter"));
            Guild.Timeout afkTimeout = Guild.Timeout.fromKey(content.getInt("afk_timeout"));
            DiscordLocale locale = DiscordLocale.from(content.getString("preferred_locale", "en-US"));
            VoiceChannel afkChannel = content.isNull("afk_channel_id") ? null : (VoiceChannel)guild.getChannelById(VoiceChannel.class, content.getLong("afk_channel_id"));
            TextChannel systemChannel = content.isNull("system_channel_id") ? null : (TextChannel)guild.getChannelById(TextChannel.class, content.getLong("system_channel_id"));
            TextChannel rulesChannel = content.isNull("rules_channel_id") ? null : (TextChannel)guild.getChannelById(TextChannel.class, content.getLong("rules_channel_id"));
            TextChannel communityUpdatesChannel = content.isNull("public_updates_channel_id") ? null : (TextChannel)guild.getChannelById(TextChannel.class, content.getLong("public_updates_channel_id"));
            TextChannel safetyAlertsChannel = content.isNull("safety_alerts_channel_id") ? null : (TextChannel)guild.getChannelById(TextChannel.class, content.getLong("safety_alerts_channel_id"));
            int systemChannelFlagBitmask = content.getInt("system_channel_flags", 0);
            Set<String> features;
            if (!content.isNull("features")) {
               DataArray featureArr = content.getArray("features");
               features = (Set)featureArr.stream(DataArray::getString).map(String::intern).collect(Collectors.toSet());
            } else {
               features = Collections.emptySet();
            }

            if (ownerId != guild.getOwnerIdLong()) {
               long oldOwnerId = guild.getOwnerIdLong();
               Member oldOwner = guild.getOwner();
               Member newOwner = (Member)guild.getMembersView().get(ownerId);
               if (newOwner == null) {
                  WebSocketClient.LOG.debug("Received {} with owner not in cache. UserId: {} GuildId: {}", this.allContent.get("t"), ownerId, id);
               }

               guild.setOwner(newOwner);
               guild.setOwnerId(ownerId);
               this.getJDA().handleEvent(new GuildUpdateOwnerEvent(this.getJDA(), this.responseNumber, guild, oldOwner, oldOwnerId, ownerId));
            }

            if (systemChannelFlagBitmask != guild.getSystemChannelFlagsRaw()) {
               Set<SystemChannelFlag> oldSystemChannelFlags = guild.getSystemChannelFlags();
               Set<SystemChannelFlag> systemChannelFlags = Collections.unmodifiableSet(SystemChannelFlag.getFlags(systemChannelFlagBitmask));
               guild.setSystemChannelFlags(systemChannelFlagBitmask);
               this.getJDA().handleEvent(new GuildUpdateSystemChannelFlagsEvent(this.getJDA(), this.responseNumber, guild, oldSystemChannelFlags, systemChannelFlags));
            }

            if (!Objects.equals(description, guild.getDescription())) {
               String oldDescription = guild.getDescription();
               guild.setDescription(description);
               this.getJDA().handleEvent(new GuildUpdateDescriptionEvent(this.getJDA(), this.responseNumber, guild, oldDescription));
            }

            if (!Objects.equals(bannerId, guild.getBannerId())) {
               String oldBanner = guild.getBannerId();
               guild.setBannerId(bannerId);
               this.getJDA().handleEvent(new GuildUpdateBannerEvent(this.getJDA(), this.responseNumber, guild, oldBanner));
            }

            if (!Objects.equals(vanityCode, guild.getVanityCode())) {
               String oldCode = guild.getVanityCode();
               guild.setVanityCode(vanityCode);
               this.getJDA().handleEvent(new GuildUpdateVanityCodeEvent(this.getJDA(), this.responseNumber, guild, oldCode));
            }

            if (maxMembers != guild.getMaxMembers()) {
               int oldMax = guild.getMaxMembers();
               guild.setMaxMembers(maxMembers);
               this.getJDA().handleEvent(new GuildUpdateMaxMembersEvent(this.getJDA(), this.responseNumber, guild, oldMax));
            }

            if (maxPresences != guild.getMaxPresences()) {
               int oldMax = guild.getMaxPresences();
               guild.setMaxPresences(maxPresences);
               this.getJDA().handleEvent(new GuildUpdateMaxPresencesEvent(this.getJDA(), this.responseNumber, guild, oldMax));
            }

            if (boostCount != guild.getBoostCount()) {
               int oldCount = guild.getBoostCount();
               guild.setBoostCount(boostCount);
               this.getJDA().handleEvent(new GuildUpdateBoostCountEvent(this.getJDA(), this.responseNumber, guild, oldCount));
            }

            if (Guild.BoostTier.fromKey(boostTier) != guild.getBoostTier()) {
               Guild.BoostTier oldTier = guild.getBoostTier();
               guild.setBoostTier(boostTier);
               this.getJDA().handleEvent(new GuildUpdateBoostTierEvent(this.getJDA(), this.responseNumber, guild, oldTier));
            }

            if (!Objects.equals(name, guild.getName())) {
               String oldName = guild.getName();
               guild.setName(name);
               this.getJDA().handleEvent(new GuildUpdateNameEvent(this.getJDA(), this.responseNumber, guild, oldName));
            }

            if (!Objects.equals(iconId, guild.getIconId())) {
               String oldIconId = guild.getIconId();
               guild.setIconId(iconId);
               this.getJDA().handleEvent(new GuildUpdateIconEvent(this.getJDA(), this.responseNumber, guild, oldIconId));
            }

            if (!features.equals(guild.getFeatures())) {
               Set<String> oldFeatures = guild.getFeatures();
               guild.setFeatures(features);
               this.getJDA().handleEvent(new GuildUpdateFeaturesEvent(this.getJDA(), this.responseNumber, guild, oldFeatures));
            }

            if (!Objects.equals(splashId, guild.getSplashId())) {
               String oldSplashId = guild.getSplashId();
               guild.setSplashId(splashId);
               this.getJDA().handleEvent(new GuildUpdateSplashEvent(this.getJDA(), this.responseNumber, guild, oldSplashId));
            }

            if (!Objects.equals(verificationLevel, guild.getVerificationLevel())) {
               Guild.VerificationLevel oldVerificationLevel = guild.getVerificationLevel();
               guild.setVerificationLevel(verificationLevel);
               this.getJDA().handleEvent(new GuildUpdateVerificationLevelEvent(this.getJDA(), this.responseNumber, guild, oldVerificationLevel));
            }

            if (!Objects.equals(notificationLevel, guild.getDefaultNotificationLevel())) {
               Guild.NotificationLevel oldNotificationLevel = guild.getDefaultNotificationLevel();
               guild.setDefaultNotificationLevel(notificationLevel);
               this.getJDA().handleEvent(new GuildUpdateNotificationLevelEvent(this.getJDA(), this.responseNumber, guild, oldNotificationLevel));
            }

            if (!Objects.equals(mfaLevel, guild.getRequiredMFALevel())) {
               Guild.MFALevel oldMfaLevel = guild.getRequiredMFALevel();
               guild.setRequiredMFALevel(mfaLevel);
               this.getJDA().handleEvent(new GuildUpdateMFALevelEvent(this.getJDA(), this.responseNumber, guild, oldMfaLevel));
            }

            if (!Objects.equals(explicitContentLevel, guild.getExplicitContentLevel())) {
               Guild.ExplicitContentLevel oldExplicitContentLevel = guild.getExplicitContentLevel();
               guild.setExplicitContentLevel(explicitContentLevel);
               this.getJDA().handleEvent(new GuildUpdateExplicitContentLevelEvent(this.getJDA(), this.responseNumber, guild, oldExplicitContentLevel));
            }

            if (!Objects.equals(afkTimeout, guild.getAfkTimeout())) {
               Guild.Timeout oldAfkTimeout = guild.getAfkTimeout();
               guild.setAfkTimeout(afkTimeout);
               this.getJDA().handleEvent(new GuildUpdateAfkTimeoutEvent(this.getJDA(), this.responseNumber, guild, oldAfkTimeout));
            }

            if (!Objects.equals(locale, guild.getLocale())) {
               DiscordLocale oldLocale = guild.getLocale();
               guild.setLocale(locale);
               this.getJDA().handleEvent(new GuildUpdateLocaleEvent(this.getJDA(), this.responseNumber, guild, oldLocale));
            }

            if (!Objects.equals(afkChannel, guild.getAfkChannel())) {
               VoiceChannel oldAfkChannel = guild.getAfkChannel();
               guild.setAfkChannel(afkChannel);
               this.getJDA().handleEvent(new GuildUpdateAfkChannelEvent(this.getJDA(), this.responseNumber, guild, oldAfkChannel));
            }

            if (!Objects.equals(systemChannel, guild.getSystemChannel())) {
               TextChannel oldSystemChannel = guild.getSystemChannel();
               guild.setSystemChannel(systemChannel);
               this.getJDA().handleEvent(new GuildUpdateSystemChannelEvent(this.getJDA(), this.responseNumber, guild, oldSystemChannel));
            }

            if (!Objects.equals(rulesChannel, guild.getRulesChannel())) {
               TextChannel oldRulesChannel = guild.getRulesChannel();
               guild.setRulesChannel(rulesChannel);
               this.getJDA().handleEvent(new GuildUpdateRulesChannelEvent(this.getJDA(), this.responseNumber, guild, oldRulesChannel));
            }

            if (!Objects.equals(communityUpdatesChannel, guild.getCommunityUpdatesChannel())) {
               TextChannel oldCommunityUpdatesChannel = guild.getCommunityUpdatesChannel();
               guild.setCommunityUpdatesChannel(communityUpdatesChannel);
               this.getJDA().handleEvent(new GuildUpdateCommunityUpdatesChannelEvent(this.getJDA(), this.responseNumber, guild, oldCommunityUpdatesChannel));
            }

            if (!Objects.equals(safetyAlertsChannel, guild.getSafetyAlertsChannel())) {
               TextChannel oldSafetyAlertsChannel = guild.getSafetyAlertsChannel();
               guild.setSafetyAlertsChannel(safetyAlertsChannel);
               this.getJDA().handleEvent(new GuildUpdateSafetyAlertsChannelEvent(this.getJDA(), this.responseNumber, guild, oldSafetyAlertsChannel));
            }

            if (!Objects.equals(securityIncidentActions, guild.getSecurityIncidentActions())) {
               SecurityIncidentActions oldIncidentActions = guild.getSecurityIncidentActions();
               guild.setSecurityIncidentActions(securityIncidentActions);
               this.api.handleEvent(new GuildUpdateSecurityIncidentActionsEvent(this.getJDA(), this.responseNumber, guild, oldIncidentActions));
            }

            if (!Objects.equals(securityIncidentDetections, guild.getSecurityIncidentDetections())) {
               SecurityIncidentDetections oldIncidentDetections = guild.getSecurityIncidentDetections();
               guild.setSecurityIncidentDetections(securityIncidentDetections);
               this.api.handleEvent(new GuildUpdateSecurityIncidentDetectionsEvent(this.getJDA(), this.responseNumber, guild, oldIncidentDetections));
            }

            if (content.hasKey("nsfw_level") && nsfwLevel != guild.getNSFWLevel()) {
               Guild.NSFWLevel oldNSFWLevel = guild.getNSFWLevel();
               guild.setNSFWLevel(nsfwLevel);
               this.getJDA().handleEvent(new GuildUpdateNSFWLevelEvent(this.getJDA(), this.responseNumber, guild, oldNSFWLevel));
            }

            return null;
         }
      }
   }
}
