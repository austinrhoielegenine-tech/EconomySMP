package net.dv8tion.jda.internal.entities;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.utils.ComponentDeserializer;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.ApplicationTeam;
import net.dv8tion.jda.api.entities.ClientType;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.GuildWelcomeScreen;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageActivity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.SKU;
import net.dv8tion.jda.api.entities.SKUFlag;
import net.dv8tion.jda.api.entities.SKUType;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.SoundboardSound;
import net.dv8tion.jda.api.entities.StageInstance;
import net.dv8tion.jda.api.entities.TeamMember;
import net.dv8tion.jda.api.entities.ThreadMember;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.WebhookType;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
import net.dv8tion.jda.api.entities.channel.attribute.IWebhookContainer;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.GroupChannel;
import net.dv8tion.jda.api.entities.channel.concrete.MediaChannel;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.guild.SecurityIncidentActions;
import net.dv8tion.jda.api.entities.guild.SecurityIncidentDetections;
import net.dv8tion.jda.api.entities.messages.MessagePoll;
import net.dv8tion.jda.api.entities.messages.MessageSnapshot;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.entities.sticker.RichSticker;
import net.dv8tion.jda.api.entities.sticker.StandardSticker;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import net.dv8tion.jda.api.entities.sticker.StickerItem;
import net.dv8tion.jda.api.entities.sticker.StickerPack;
import net.dv8tion.jda.api.entities.templates.Template;
import net.dv8tion.jda.api.entities.templates.TemplateChannel;
import net.dv8tion.jda.api.entities.templates.TemplateGuild;
import net.dv8tion.jda.api.entities.templates.TemplateRole;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateAvatarEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateFlagsEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdatePendingEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateTimeOutEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateAvatarEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateDiscriminatorEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateFlagsEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateGlobalNameEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdatePrimaryGuildEvent;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.IntegrationOwners;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.cache.CacheView;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.CategoryImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.ForumChannelImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.MediaChannelImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.NewsChannelImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.PrivateChannelImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.StageChannelImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.TextChannelImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.ThreadChannelImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.VoiceChannelImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IPermissionContainerMixin;
import net.dv8tion.jda.internal.entities.emoji.ApplicationEmojiImpl;
import net.dv8tion.jda.internal.entities.emoji.CustomEmojiImpl;
import net.dv8tion.jda.internal.entities.emoji.RichCustomEmojiImpl;
import net.dv8tion.jda.internal.entities.emoji.UnicodeEmojiImpl;
import net.dv8tion.jda.internal.entities.messages.MessagePollImpl;
import net.dv8tion.jda.internal.entities.sticker.GuildStickerImpl;
import net.dv8tion.jda.internal.entities.sticker.RichStickerImpl;
import net.dv8tion.jda.internal.entities.sticker.StandardStickerImpl;
import net.dv8tion.jda.internal.entities.sticker.StickerItemImpl;
import net.dv8tion.jda.internal.entities.sticker.StickerPackImpl;
import net.dv8tion.jda.internal.handle.EventCache;
import net.dv8tion.jda.internal.interactions.IntegrationOwnersImpl;
import net.dv8tion.jda.internal.utils.Helpers;
import net.dv8tion.jda.internal.utils.JDALogger;
import net.dv8tion.jda.internal.utils.UnlockHook;
import net.dv8tion.jda.internal.utils.cache.ChannelCacheViewImpl;
import net.dv8tion.jda.internal.utils.cache.MemberCacheViewImpl;
import net.dv8tion.jda.internal.utils.cache.SnowflakeCacheViewImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.slf4j.Logger;

public class EntityBuilder extends AbstractEntityBuilder {
   public static final Logger LOG = JDALogger.getLog(EntityBuilder.class);
   public static final String MISSING_CHANNEL = "MISSING_CHANNEL";
   public static final String MISSING_USER = "MISSING_USER";
   public static final String UNKNOWN_MESSAGE_TYPE = "UNKNOWN_MESSAGE_TYPE";
   public static final ComponentDeserializer DEFAULT_COMPONENT_DESERIALIZER = new ComponentDeserializer(Collections.emptyList());
   private static final Set<String> richGameFields;

   public EntityBuilder(JDAImpl api) {
      super(api);
   }

   public SelfUser createSelfUser(DataObject self) {
      SelfUserImpl selfUser = (SelfUserImpl)(this.getJDA().hasSelfUser() ? this.getJDA().getSelfUser() : null);
      if (selfUser == null) {
         long id = self.getLong("id");
         selfUser = new SelfUserImpl(id, this.getJDA());
         this.getJDA().setSelfUser(selfUser);
      }

      SnowflakeCacheViewImpl<User> userView = this.getJDA().getUsersView();
      UnlockHook hook = userView.writeLock();

      try {
         if (userView.getElementById(selfUser.getIdLong()) == null) {
            userView.getMap().put(selfUser.getIdLong(), selfUser);
         }
      } catch (Throwable var8) {
         if (hook != null) {
            try {
               hook.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (hook != null) {
         hook.close();
      }

      if (!self.isNull("application_id")) {
         selfUser.setApplicationId(self.getUnsignedLong("application_id"));
      }

      selfUser.setVerified(self.getBoolean("verified")).setMfaEnabled(self.getBoolean("mfa_enabled")).setName(self.getString("username")).setGlobalName(self.getString("global_name", (String)null)).setDiscriminator(Short.parseShort(self.getString("discriminator", "0"))).setAvatarId(self.getString("avatar", (String)null)).setBot(self.getBoolean("bot")).setSystem(false);
      return selfUser;
   }

   public static Activity createActivity(String name, String url, Activity.ActivityType type) {
      return new ActivityImpl(name, url, type);
   }

   public static EmojiUnion createEmoji(DataObject emoji) {
      return createEmoji(emoji, "name", "id");
   }

   public static EmojiUnion createEmoji(DataObject emoji, String nameKey, String idKey) {
      long id = emoji.getUnsignedLong(idKey, 0L);
      return (EmojiUnion)(id == 0L ? new UnicodeEmojiImpl(emoji.getString(nameKey)) : new CustomEmojiImpl(emoji.getString(nameKey, ""), id, emoji.getBoolean("animated")));
   }

   public SoundboardSound createSoundboardSound(DataObject json) {
      String name = json.getString("name");
      long id = json.getLong("sound_id");
      double volume = json.getDouble("volume");
      EmojiUnion emoji;
      if (json.isNull("emoji_name") && json.isNull("emoji_id")) {
         emoji = null;
      } else {
         emoji = createEmoji(json, "emoji_name", "emoji_id");
      }

      Guild guild = this.getJDA().getGuildById(json.getLong("guild_id", 0L));
      boolean available = json.getBoolean("available");
      User user = (User)json.optObject("user").map(this::createUser).orElse((Object)null);
      return new SoundboardSoundImpl(this.api, id, name, volume, emoji, guild, available, user);
   }

   public static SKU createSKU(DataObject object) {
      return new SKUImpl(object.getLong("id"), SKUType.fromId(object.getInt("type")), object.getString("name"), object.getString("slug"), SKUFlag.getFlags(object.getInt("flags")));
   }

   private void createGuildEmojiPass(GuildImpl guildObj, DataArray array) {
      if (this.getJDA().isCacheFlagSet(CacheFlag.EMOJI)) {
         SnowflakeCacheViewImpl<RichCustomEmoji> emojiView = guildObj.getEmojisView();
         UnlockHook hook = emojiView.writeLock();

         try {
            TLongObjectMap<RichCustomEmoji> emojiMap = emojiView.getMap();

            for(int i = 0; i < array.length(); ++i) {
               DataObject object = array.getObject(i);
               if (object.isNull("id")) {
                  LOG.error((String)"Received GUILD_CREATE with an emoji with a null ID. JSON: {}", (Object)object);
               } else {
                  long emojiId = object.getLong("id");
                  emojiMap.put(emojiId, this.createEmoji(guildObj, object));
               }
            }
         } catch (Throwable var11) {
            if (hook != null) {
               try {
                  hook.close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }
            }

            throw var11;
         }

         if (hook != null) {
            hook.close();
         }

      }
   }

   private void createScheduledEventPass(GuildImpl guildObj, DataArray array) {
      if (this.getJDA().isCacheFlagSet(CacheFlag.SCHEDULED_EVENTS)) {
         for(int i = 0; i < array.length(); ++i) {
            DataObject object = array.getObject(i);

            try {
               if (object.isNull("id")) {
                  LOG.error((String)"Received GUILD_CREATE with a scheduled event with a null ID. JSON: {}", (Object)object);
               } else {
                  this.createScheduledEvent(guildObj, object);
               }
            } catch (ParsingException exception) {
               LOG.error((String)"Received GUILD_CREATE with a scheduled event that failed to parse. JSON: {}", (Object)object, (Object)exception);
            }
         }

      }
   }

   private void createGuildStickerPass(GuildImpl guildObj, DataArray array) {
      if (this.getJDA().isCacheFlagSet(CacheFlag.STICKER)) {
         SnowflakeCacheViewImpl<GuildSticker> stickerView = guildObj.getStickersView();
         UnlockHook hook = stickerView.writeLock();

         try {
            TLongObjectMap<GuildSticker> stickerMap = stickerView.getMap();

            for(int i = 0; i < array.length(); ++i) {
               DataObject object = array.getObject(i);
               if (object.isNull("id")) {
                  LOG.error((String)"Received GUILD_CREATE with a sticker with a null ID. GuildId: {} JSON: {}", (Object)guildObj.getId(), (Object)object);
               } else if (object.getInt("type", -1) != Sticker.Type.GUILD.getId()) {
                  LOG.error("Received GUILD_CREATE with sticker that had an unexpected type. GuildId: {} Type: {} JSON: {}", guildObj.getId(), object.getInt("type", -1), object);
               } else {
                  RichSticker sticker = this.createRichSticker(object);
                  stickerMap.put(sticker.getIdLong(), (GuildSticker)sticker);
               }
            }
         } catch (Throwable var10) {
            if (hook != null) {
               try {
                  hook.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }
            }

            throw var10;
         }

         if (hook != null) {
            hook.close();
         }

      }
   }

   private void createGuildSoundboardSoundPass(GuildImpl guildObj, DataArray array) {
      if (this.getJDA().isCacheFlagSet(CacheFlag.SOUNDBOARD_SOUNDS)) {
         SnowflakeCacheViewImpl<SoundboardSound> soundboardView = guildObj.getSoundboardSoundsView();
         UnlockHook hook = soundboardView.writeLock();

         try {
            TLongObjectMap<SoundboardSound> soundboardMap = soundboardView.getMap();

            for(int i = 0; i < array.length(); ++i) {
               DataObject object = array.getObject(i);
               if (object.isNull("sound_id")) {
                  LOG.error((String)"Received GUILD_CREATE with a sound with a null ID. GuildId: {} JSON: {}", (Object)guildObj.getId(), (Object)object);
               } else {
                  SoundboardSound sound = this.createSoundboardSound(object);
                  soundboardMap.put(sound.getIdLong(), sound);
               }
            }
         } catch (Throwable var10) {
            if (hook != null) {
               try {
                  hook.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }
            }

            throw var10;
         }

         if (hook != null) {
            hook.close();
         }

      }
   }

   public SecurityIncidentActions createSecurityIncidentsActions(DataObject data) {
      OffsetDateTime invitesDisabledUntil = data.getOffsetDateTime("invites_disabled_until", (OffsetDateTime)null);
      OffsetDateTime dmsDisabledUntil = data.getOffsetDateTime("dms_disabled_until", (OffsetDateTime)null);
      return invitesDisabledUntil == null && dmsDisabledUntil == null ? null : SecurityIncidentActions.enabled(invitesDisabledUntil, dmsDisabledUntil);
   }

   public SecurityIncidentDetections createSecurityIncidentsDetections(DataObject data) {
      String timeDmSpamDetected = data.getString("dm_spam_detected_at", (String)null);
      String timeRaidDetected = data.getString("raid_detected_at", (String)null);
      return timeRaidDetected == null && timeDmSpamDetected == null ? null : new SecurityIncidentDetections(timeDmSpamDetected == null ? 0L : Helpers.toTimestamp(timeDmSpamDetected), timeRaidDetected == null ? 0L : Helpers.toTimestamp(timeRaidDetected));
   }

   public GuildImpl createGuild(long guildId, DataObject guildJson, TLongObjectMap<DataObject> members, int memberCount) {
      GuildImpl guildObj = new GuildImpl(this.getJDA(), guildId);
      String name = guildJson.getString("name", "");
      String iconId = guildJson.getString("icon", (String)null);
      String splashId = guildJson.getString("splash", (String)null);
      String description = guildJson.getString("description", (String)null);
      String vanityCode = guildJson.getString("vanity_url_code", (String)null);
      String bannerId = guildJson.getString("banner", (String)null);
      String locale = guildJson.getString("preferred_locale", "en-US");
      SecurityIncidentActions securityIncidentActions = (SecurityIncidentActions)guildJson.optObject("incidents_data").map(this::createSecurityIncidentsActions).orElse((Object)null);
      SecurityIncidentDetections securityIncidentDetections = (SecurityIncidentDetections)guildJson.optObject("incidents_data").map(this::createSecurityIncidentsDetections).orElse((Object)null);
      DataArray roleArray = guildJson.getArray("roles");
      DataArray channelArray = guildJson.getArray("channels");
      DataArray threadArray = guildJson.getArray("threads");
      DataArray scheduledEventsArray = guildJson.getArray("guild_scheduled_events");
      DataArray emojisArray = guildJson.getArray("emojis");
      DataArray voiceStateArray = guildJson.getArray("voice_states");
      Optional<DataArray> stickersArray = guildJson.optArray("stickers");
      Optional<DataArray> soundboardSoundsArray = guildJson.optArray("soundboard_sounds");
      Optional<DataArray> featuresArray = guildJson.optArray("features");
      Optional<DataArray> presencesArray = guildJson.optArray("presences");
      long ownerId = guildJson.getUnsignedLong("owner_id", 0L);
      long afkChannelId = guildJson.getUnsignedLong("afk_channel_id", 0L);
      long systemChannelId = guildJson.getUnsignedLong("system_channel_id", 0L);
      long rulesChannelId = guildJson.getUnsignedLong("rules_channel_id", 0L);
      long communityUpdatesChannelId = guildJson.getUnsignedLong("public_updates_channel_id", 0L);
      long safetyAlertsChannelId = guildJson.getUnsignedLong("safety_alerts_channel_id", 0L);
      int boostCount = guildJson.getInt("premium_subscription_count", 0);
      int boostTier = guildJson.getInt("premium_tier", 0);
      int maxMembers = guildJson.getInt("max_members", 0);
      int maxPresences = guildJson.getInt("max_presences", 5000);
      int mfaLevel = guildJson.getInt("mfa_level", 0);
      int afkTimeout = guildJson.getInt("afk_timeout", 0);
      int verificationLevel = guildJson.getInt("verification_level", 0);
      int notificationLevel = guildJson.getInt("default_message_notifications", 0);
      int explicitContentLevel = guildJson.getInt("explicit_content_filter", 0);
      int nsfwLevel = guildJson.getInt("nsfw_level", -1);
      boolean boostProgressBarEnabled = guildJson.getBoolean("premium_progress_bar_enabled");
      int systemChannelFlags = guildJson.getInt("system_channel_flags", 0);
      guildObj.setName(name).setIconId(iconId).setSplashId(splashId).setDescription(description).setBannerId(bannerId).setVanityCode(vanityCode).setMaxMembers(maxMembers).setMaxPresences(maxPresences).setOwnerId(ownerId).setAfkTimeout(Guild.Timeout.fromKey(afkTimeout)).setSecurityIncidentActions(securityIncidentActions).setSecurityIncidentDetections(securityIncidentDetections).setVerificationLevel(Guild.VerificationLevel.fromKey(verificationLevel)).setDefaultNotificationLevel(Guild.NotificationLevel.fromKey(notificationLevel)).setExplicitContentLevel(Guild.ExplicitContentLevel.fromKey(explicitContentLevel)).setRequiredMFALevel(Guild.MFALevel.fromKey(mfaLevel)).setLocale(DiscordLocale.from(locale)).setBoostCount(boostCount).setBoostTier(boostTier).setMemberCount(memberCount).setNSFWLevel(Guild.NSFWLevel.fromKey(nsfwLevel)).setBoostProgressBarEnabled(boostProgressBarEnabled).setSystemChannelFlags(systemChannelFlags);
      SnowflakeCacheViewImpl<Guild> guildView = this.getJDA().getGuildsView();
      UnlockHook hook = guildView.writeLock();

      try {
         guildView.getMap().put(guildId, guildObj);
      } catch (Throwable var67) {
         if (hook != null) {
            try {
               hook.close();
            } catch (Throwable var66) {
               var67.addSuppressed(var66);
            }
         }

         throw var67;
      }

      if (hook != null) {
         hook.close();
      }

      Set<String> features = (Set)featuresArray.map((array) -> (Set)array.stream(DataArray::getString).map(String::intern).collect(Collectors.toSet())).orElse(Collections.emptySet());
      guildObj.setFeatures(features);
      SnowflakeCacheViewImpl<Role> roleView = guildObj.getRolesView();
      UnlockHook hook = roleView.writeLock();

      try {
         TLongObjectMap<Role> map = roleView.getMap();

         for(int i = 0; i < roleArray.length(); ++i) {
            DataObject obj = roleArray.getObject(i);
            Role role = this.createRole(guildObj, obj, guildId);
            map.put(role.getIdLong(), role);
            if (role.getIdLong() == guildObj.getIdLong()) {
               guildObj.setPublicRole(role);
            }
         }
      } catch (Throwable var71) {
         if (hook != null) {
            try {
               hook.close();
            } catch (Throwable var65) {
               var71.addSuppressed(var65);
            }
         }

         throw var71;
      }

      if (hook != null) {
         hook.close();
      }

      for(int i = 0; i < channelArray.length(); ++i) {
         DataObject channelJson = channelArray.getObject(i);
         this.createGuildChannel(guildObj, channelJson);
      }

      TLongObjectMap<DataObject> voiceStates = Helpers.convertToMap((o) -> o.getUnsignedLong("user_id", 0L), voiceStateArray);
      TLongObjectMap<DataObject> presences = (TLongObjectMap)presencesArray.map((o1) -> Helpers.convertToMap((o2) -> o2.getObject("user").getUnsignedLong("id"), o1)).orElseGet(TLongObjectHashMap::new);
      UnlockHook h1 = guildObj.getMembersView().writeLock();

      try {
         UnlockHook h2 = this.getJDA().getUsersView().writeLock();

         try {
            for(DataObject memberJson : members.valueCollection()) {
               long userId = memberJson.getObject("user").getUnsignedLong("id");
               DataObject voiceState = voiceStates.get(userId);
               DataObject presence = presences.get(userId);
               this.updateMemberCache(this.createMember(guildObj, memberJson, voiceState, presence));
            }
         } catch (Throwable var69) {
            if (h2 != null) {
               try {
                  h2.close();
               } catch (Throwable var64) {
                  var69.addSuppressed(var64);
               }
            }

            throw var69;
         }

         if (h2 != null) {
            h2.close();
         }
      } catch (Throwable var70) {
         if (h1 != null) {
            try {
               h1.close();
            } catch (Throwable var63) {
               var70.addSuppressed(var63);
            }
         }

         throw var70;
      }

      if (h1 != null) {
         h1.close();
      }

      if (guildObj.getOwner() == null) {
         LOG.debug((String)"Finished setup for guild with a null owner. GuildId: {} OwnerId: {}", (Object)guildId, (Object)guildJson.opt("owner_id").orElse((Object)null));
      }

      if (guildObj.getMember(this.api.getSelfUser()) == null) {
         LOG.error((String)"Guild is missing a SelfMember. GuildId: {}", (Object)guildId);
         LOG.debug((String)"Guild is missing a SelfMember. GuildId: {} JSON: \n{}", (Object)guildId, (Object)guildJson);
         guildObj.retrieveMembersByIds(new long[]{this.api.getSelfUser().getIdLong()}).onSuccess((m) -> {
            if (m.isEmpty()) {
               LOG.warn((String)"Was unable to recover SelfMember for guild with id {}. This guild might be corrupted!", (Object)guildId);
            } else {
               LOG.debug((String)"Successfully recovered SelfMember for guild with id {}.", (Object)guildId);
            }

         });
      }

      for(int i = 0; i < threadArray.length(); ++i) {
         DataObject threadJson = threadArray.getObject(i);

         try {
            this.createThreadChannel(guildObj, threadJson, guildObj.getIdLong());
         } catch (Exception ex) {
            if ("MISSING_CHANNEL".equals(ex.getMessage())) {
               LOG.debug((String)"Discarding thread without cached parent channel. JSON: {}", (Object)threadJson);
            } else {
               LOG.warn("Failed to create thread channel for guild with id {}.\nJSON: {}", guildId, threadJson, ex);
            }
         }
      }

      this.createScheduledEventPass(guildObj, scheduledEventsArray);
      this.createGuildEmojiPass(guildObj, emojisArray);
      stickersArray.ifPresent((stickers) -> this.createGuildStickerPass(guildObj, stickers));
      soundboardSoundsArray.ifPresent((sound) -> this.createGuildSoundboardSoundPass(guildObj, sound));
      guildJson.optArray("stage_instances").map((arr) -> arr.stream(DataArray::getObject)).ifPresent((list) -> list.forEach((it) -> this.createStageInstance(guildObj, it)));
      guildObj.setAfkChannel(guildObj.getVoiceChannelById(afkChannelId)).setSystemChannel(guildObj.getTextChannelById(systemChannelId)).setRulesChannel(guildObj.getTextChannelById(rulesChannelId)).setCommunityUpdatesChannel(guildObj.getTextChannelById(communityUpdatesChannelId)).setSafetyAlertsChannel(guildObj.getTextChannelById(safetyAlertsChannelId));
      return guildObj;
   }

   public GuildChannel createGuildChannel(GuildImpl guildObj, DataObject channelData) {
      ChannelType channelType = ChannelType.fromId(channelData.getInt("type"));
      switch (channelType) {
         case TEXT:
            return this.createTextChannel(guildObj, channelData, guildObj.getIdLong());
         case NEWS:
            return this.createNewsChannel(guildObj, channelData, guildObj.getIdLong());
         case STAGE:
            return this.createStageChannel(guildObj, channelData, guildObj.getIdLong());
         case VOICE:
            return this.createVoiceChannel(guildObj, channelData, guildObj.getIdLong());
         case CATEGORY:
            return this.createCategory(guildObj, channelData, guildObj.getIdLong());
         case FORUM:
            return this.createForumChannel(guildObj, channelData, guildObj.getIdLong());
         case MEDIA:
            return this.createMediaChannel(guildObj, channelData, guildObj.getIdLong());
         default:
            LOG.debug("Cannot create channel for type " + channelData.getInt("type"));
            return null;
      }
   }

   public User.PrimaryGuild createPrimaryGuild(DataObject obj) {
      return obj.isNull("identity_guild_id") ? null : new User.PrimaryGuild(obj.getUnsignedLong("identity_guild_id"), obj.getBoolean("identity_enabled"), obj.getString("tag", (String)null), obj.getString("badge", (String)null));
   }

   public UserImpl createUser(DataObject user) {
      boolean newUser = false;
      long id = user.getLong("id");
      SnowflakeCacheViewImpl<User> userView = this.getJDA().getUsersView();
      UnlockHook hook = userView.readLock();

      UserImpl userObj;
      try {
         userObj = (UserImpl)userView.getElementById(id);
         if (userObj == null) {
            userObj = new UserImpl(id, this.getJDA());
            newUser = true;
         }
      } catch (Throwable var11) {
         if (hook != null) {
            try {
               hook.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }
         }

         throw var11;
      }

      if (hook != null) {
         hook.close();
      }

      User.Profile profile = user.hasKey("banner") ? new User.Profile(id, user.getString("banner", (String)null), user.getInt("accent_color", 536870911)) : null;
      if (newUser) {
         User.PrimaryGuild primaryGuild = (User.PrimaryGuild)user.optObject("primary_guild").map(this::createPrimaryGuild).orElse((Object)null);
         userObj.setName(user.getString("username")).setGlobalName(user.getString("global_name", (String)null)).setDiscriminator(Short.parseShort(user.getString("discriminator", "0"))).setAvatarId(user.getString("avatar", (String)null)).setBot(user.getBoolean("bot")).setSystem(user.getBoolean("system")).setFlags(user.getInt("public_flags", 0)).setPrimaryGuild(primaryGuild).setProfile(profile);
      } else {
         this.updateUser(userObj, user);
      }

      return userObj;
   }

   public void updateUser(UserImpl userObj, DataObject user) {
      String oldName = userObj.getName();
      String newName = user.getString("username");
      String oldGlobalName = userObj.getGlobalName();
      String newGlobalName = user.getString("global_name", (String)null);
      short oldDiscriminator = userObj.getDiscriminatorInt();
      short newDiscriminator = Short.parseShort(user.getString("discriminator", "0"));
      String oldAvatar = userObj.getAvatarId();
      String newAvatar = user.getString("avatar", (String)null);
      int oldFlags = userObj.getFlagsRaw();
      int newFlags = user.getInt("public_flags", 0);
      User.PrimaryGuild oldPrimaryGuild = userObj.getPrimaryGuild();
      User.PrimaryGuild newPrimaryGuild = (User.PrimaryGuild)user.optObject("primary_guild").map(this::createPrimaryGuild).orElse((Object)null);
      JDAImpl jda = this.getJDA();
      long responseNumber = jda.getResponseTotal();
      if (!oldName.equals(newName)) {
         userObj.setName(newName);
         jda.handleEvent(new UserUpdateNameEvent(jda, responseNumber, userObj, oldName));
      }

      if (!Objects.equals(oldGlobalName, newGlobalName)) {
         userObj.setGlobalName(newGlobalName);
         jda.handleEvent(new UserUpdateGlobalNameEvent(jda, responseNumber, userObj, oldGlobalName));
      }

      if (oldDiscriminator != newDiscriminator) {
         String oldDiscrimString = userObj.getDiscriminator();
         userObj.setDiscriminator(newDiscriminator);
         jda.handleEvent(new UserUpdateDiscriminatorEvent(jda, responseNumber, userObj, oldDiscrimString));
      }

      if (!Objects.equals(oldAvatar, newAvatar)) {
         userObj.setAvatarId(newAvatar);
         jda.handleEvent(new UserUpdateAvatarEvent(jda, responseNumber, userObj, oldAvatar));
      }

      if (oldFlags != newFlags) {
         userObj.setFlags(newFlags);
         jda.handleEvent(new UserUpdateFlagsEvent(jda, responseNumber, userObj, User.UserFlag.getFlags(oldFlags)));
      }

      if (!Objects.equals(oldPrimaryGuild, newPrimaryGuild)) {
         userObj.setPrimaryGuild(newPrimaryGuild);
         jda.handleEvent(new UserUpdatePrimaryGuildEvent(jda, responseNumber, userObj, oldPrimaryGuild));
      }

   }

   public boolean updateMemberCache(MemberImpl member) {
      return this.updateMemberCache(member, false);
   }

   public boolean updateMemberCache(MemberImpl member, boolean forceRemove) {
      GuildImpl guild = member.getGuild();
      UserImpl user = (UserImpl)member.getUser();
      MemberCacheViewImpl membersView = guild.getMembersView();
      if (!forceRemove && this.getJDA().cacheMember(member)) {
         if (guild.getMemberById(member.getIdLong()) != null) {
            return true;
         } else {
            LOG.trace((String)"Loading member {}", (Object)member);
            if (this.getJDA().getUserById(user.getIdLong()) == null) {
               SnowflakeCacheViewImpl<User> usersView = this.getJDA().getUsersView();
               UnlockHook hook1 = usersView.writeLock();

               try {
                  usersView.getMap().put(user.getIdLong(), user);
               } catch (Throwable var12) {
                  if (hook1 != null) {
                     try {
                        hook1.close();
                     } catch (Throwable var11) {
                        var12.addSuppressed(var11);
                     }
                  }

                  throw var12;
               }

               if (hook1 != null) {
                  hook1.close();
               }
            }

            UnlockHook hook = membersView.writeLock();

            try {
               membersView.getMap().put(member.getIdLong(), member);
               if (member.isOwner()) {
                  guild.setOwner(member);
               }
            } catch (Throwable var13) {
               if (hook != null) {
                  try {
                     hook.close();
                  } catch (Throwable var10) {
                     var13.addSuppressed(var10);
                  }
               }

               throw var13;
            }

            if (hook != null) {
               hook.close();
            }

            long hashId = guild.getIdLong() ^ user.getIdLong();
            this.getJDA().getEventCache().playbackCache(EventCache.Type.USER, member.getIdLong());
            this.getJDA().getEventCache().playbackCache(EventCache.Type.MEMBER, hashId);
            return true;
         }
      } else if (membersView.remove(member.getIdLong()) == null) {
         return false;
      } else {
         LOG.trace((String)"Unloading member {}", (Object)member);
         if (user.getMutualGuilds().isEmpty()) {
            this.getJDA().getUsersView().remove(user.getIdLong());
         }

         return false;
      }
   }

   public MemberImpl createMember(GuildImpl guild, DataObject memberJson) {
      return this.createMember(guild, memberJson, (DataObject)null, (DataObject)null);
   }

   public MemberImpl createMember(GuildImpl guild, DataObject memberJson, DataObject voiceStateJson, DataObject presence) {
      User user = this.createUser(memberJson.getObject("user"));
      DataArray roleArray = memberJson.getArray("roles");
      MemberImpl member = (MemberImpl)guild.getMember(user);
      if (member == null) {
         if (user.getIdLong() == this.getJDA().getSelfUser().getIdLong()) {
            member = new SelfMemberImpl(guild, (SelfUser)user);
         } else {
            member = new MemberImpl(guild, user);
         }

         this.configureMember(memberJson, member);
         Set<Role> roles = member.getRoleSet();

         for(int i = 0; i < roleArray.length(); ++i) {
            long roleId = roleArray.getUnsignedLong(i);
            Role role = guild.getRoleById(roleId);
            if (role != null) {
               roles.add(role);
            }
         }
      } else {
         List<Role> roles = new ArrayList(roleArray.length());

         for(int i = 0; i < roleArray.length(); ++i) {
            long roleId = roleArray.getUnsignedLong(i);
            Role role = guild.getRoleById(roleId);
            if (role != null) {
               roles.add(role);
            }
         }

         this.updateMember(guild, member, memberJson, roles);
      }

      if (voiceStateJson != null) {
         this.createGuildVoiceState(member, voiceStateJson);
      }

      if (presence != null) {
         this.createPresence(member, presence);
      }

      guild.updateCacheVoiceStateMember(member);
      return member;
   }

   public GuildVoiceState createGuildVoiceState(MemberImpl member, DataObject voiceStateJson) {
      GuildVoiceStateImpl voiceState = member.getVoiceState();
      if (voiceState == null) {
         voiceState = new GuildVoiceStateImpl(member);
      }

      this.updateGuildVoiceState(voiceState, voiceStateJson, member);
      return voiceState;
   }

   private void updateGuildVoiceState(GuildVoiceStateImpl currentVoiceState, DataObject newVoiceStateJson, MemberImpl member) {
      GuildImpl guild = member.getGuild();
      long channelId = newVoiceStateJson.getLong("channel_id");
      AudioChannel audioChannel = (AudioChannel)guild.getChannelById(AudioChannel.class, channelId);
      if (audioChannel == null) {
         LOG.warn("Received a GuildVoiceState with a channel ID for a non-existent channel! ChannelId: {} GuildId: {} UserId: {}", channelId, guild.getId(), member.getId());
      }

      String requestToSpeak = newVoiceStateJson.getString("request_to_speak_timestamp", (String)null);
      OffsetDateTime timestamp = null;
      if (requestToSpeak != null) {
         timestamp = OffsetDateTime.parse(requestToSpeak);
      }

      currentVoiceState.setSelfMuted(newVoiceStateJson.getBoolean("self_mute")).setSelfDeafened(newVoiceStateJson.getBoolean("self_deaf")).setGuildMuted(newVoiceStateJson.getBoolean("mute")).setGuildDeafened(newVoiceStateJson.getBoolean("deaf")).setSuppressed(newVoiceStateJson.getBoolean("suppress")).setSessionId(newVoiceStateJson.getString("session_id")).setStream(newVoiceStateJson.getBoolean("self_stream")).setRequestToSpeak(timestamp).updateConnectedChannel(audioChannel);
   }

   public void updateMember(GuildImpl guild, MemberImpl member, DataObject content, List<Role> newRoles) {
      long responseNumber = this.getJDA().getResponseTotal();
      if (newRoles != null) {
         this.updateMemberRoles(member, newRoles, responseNumber);
      }

      if (content.hasKey("nick")) {
         String oldNick = member.getNickname();
         String newNick = content.getString("nick", (String)null);
         if (!Objects.equals(oldNick, newNick)) {
            member.setNickname(newNick);
            this.getJDA().handleEvent(new GuildMemberUpdateNicknameEvent(this.getJDA(), responseNumber, member, oldNick));
         }
      }

      if (content.hasKey("avatar")) {
         String oldAvatarId = member.getAvatarId();
         String newAvatarId = content.getString("avatar", (String)null);
         if (!Objects.equals(oldAvatarId, newAvatarId)) {
            member.setAvatarId(newAvatarId);
            this.getJDA().handleEvent(new GuildMemberUpdateAvatarEvent(this.getJDA(), responseNumber, member, oldAvatarId));
         }
      }

      if (content.hasKey("premium_since")) {
         long epoch = 0L;
         if (!content.isNull("premium_since")) {
            epoch = Helpers.toTimestamp(content.getString("premium_since"));
         }

         if (epoch != member.getBoostDateRaw()) {
            OffsetDateTime oldTime = member.getTimeBoosted();
            member.setBoostDate(epoch);
            this.getJDA().handleEvent(new GuildMemberUpdateBoostTimeEvent(this.getJDA(), responseNumber, member, oldTime));
         }
      }

      if (content.hasKey("communication_disabled_until")) {
         long epoch = 0L;
         if (!content.isNull("communication_disabled_until")) {
            epoch = Helpers.toTimestamp(content.getString("communication_disabled_until"));
         }

         if (epoch != member.getTimeOutEndRaw()) {
            OffsetDateTime oldTime = member.getTimeOutEnd();
            member.setTimeOutEnd(epoch);
            this.getJDA().handleEvent(new GuildMemberUpdateTimeOutEvent(this.getJDA(), responseNumber, member, oldTime));
         }
      }

      if (!content.isNull("joined_at") && !member.hasTimeJoined()) {
         String joinedAtRaw = content.getString("joined_at");
         TemporalAccessor joinedAt = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(joinedAtRaw);
         long joinEpoch = Instant.from(joinedAt).toEpochMilli();
         member.setJoinDate(joinEpoch);
      }

      if (!content.isNull("pending")) {
         boolean pending = content.getBoolean("pending");
         boolean oldPending = member.isPending();
         if (pending != oldPending) {
            member.setPending(pending);
            this.getJDA().handleEvent(new GuildMemberUpdatePendingEvent(this.getJDA(), responseNumber, member, oldPending));
         }
      }

      if (!content.isNull("flags")) {
         int flags = content.getInt("flags");
         int oldFlags = member.getFlagsRaw();
         if (flags != oldFlags) {
            member.setFlags(flags);
            this.getJDA().handleEvent(new GuildMemberUpdateFlagsEvent(this.getJDA(), responseNumber, member, Member.MemberFlag.fromRaw(oldFlags)));
         }
      }

      this.updateUser((UserImpl)member.getUser(), content.getObject("user"));
   }

   private void updateMemberRoles(MemberImpl member, List<Role> newRoles, long responseNumber) {
      Set<Role> currentRoles = member.getRoleSet();
      List<Role> removedRoles = new ArrayList();

      label39:
      for(Role role : currentRoles) {
         Iterator<Role> it = newRoles.iterator();

         while(it.hasNext()) {
            Role r = (Role)it.next();
            if (role.equals(r)) {
               it.remove();
               continue label39;
            }
         }

         removedRoles.add(role);
      }

      if (removedRoles.size() > 0) {
         currentRoles.removeAll(removedRoles);
      }

      if (newRoles.size() > 0) {
         currentRoles.addAll(newRoles);
      }

      if (removedRoles.size() > 0) {
         this.getJDA().handleEvent(new GuildMemberRoleRemoveEvent(this.getJDA(), responseNumber, member, removedRoles));
      }

      if (newRoles.size() > 0) {
         this.getJDA().handleEvent(new GuildMemberRoleAddEvent(this.getJDA(), responseNumber, member, newRoles));
      }

   }

   public void createPresence(MemberImpl member, DataObject presenceJson) {
      if (member == null) {
         throw new NullPointerException("Provided member was null!");
      } else {
         OnlineStatus onlineStatus = OnlineStatus.fromKey(presenceJson.getString("status"));
         if (onlineStatus != OnlineStatus.OFFLINE) {
            MemberPresenceImpl presence = member.getPresence();
            if (presence == null) {
               CacheView.SimpleCacheView<MemberPresenceImpl> view = member.getGuild().getPresenceView();
               if (view == null) {
                  return;
               }

               presence = new MemberPresenceImpl();
               UnlockHook lock = view.writeLock();

               try {
                  view.getMap().put(member.getIdLong(), presence);
               } catch (Throwable var16) {
                  if (lock != null) {
                     try {
                        lock.close();
                     } catch (Throwable var15) {
                        var16.addSuppressed(var15);
                     }
                  }

                  throw var16;
               }

               if (lock != null) {
                  lock.close();
               }
            }

            boolean cacheGame = this.getJDA().isCacheFlagSet(CacheFlag.ACTIVITY);
            boolean cacheStatus = this.getJDA().isCacheFlagSet(CacheFlag.CLIENT_STATUS);
            DataArray activityArray = cacheGame && !presenceJson.isNull("activities") ? presenceJson.getArray("activities") : null;
            DataObject clientStatusJson = cacheStatus && !presenceJson.isNull("client_status") ? presenceJson.getObject("client_status") : null;
            List<Activity> activities = new ArrayList();
            boolean parsedActivity = false;
            if (cacheGame && activityArray != null) {
               for(int i = 0; i < activityArray.length(); ++i) {
                  try {
                     activities.add(createActivity(activityArray.getObject(i)));
                     parsedActivity = true;
                  } catch (Exception ex) {
                     String userId = member.getId();
                     if (LOG.isDebugEnabled()) {
                        LOG.warn("Encountered exception trying to parse a presence! UserId: {} JSON: {}", userId, activityArray, ex);
                     } else {
                        LOG.warn((String)"Encountered exception trying to parse a presence! UserId: {} Message: {} Enable debug for details", (Object)userId, (Object)ex.getMessage());
                     }
                  }
               }
            }

            if (cacheGame && parsedActivity) {
               presence.setActivities(activities);
            }

            presence.setOnlineStatus(onlineStatus);
            if (clientStatusJson != null) {
               for(String key : clientStatusJson.keys()) {
                  ClientType type = ClientType.fromKey(key);
                  OnlineStatus status = OnlineStatus.fromKey(clientStatusJson.getString(key));
                  presence.setOnlineStatus(type, status);
               }
            }

         }
      }
   }

   public static Activity createActivity(DataObject gameJson) {
      String name = String.valueOf(gameJson.get("name"));
      String url = gameJson.isNull("url") ? null : String.valueOf(gameJson.get("url"));

      Activity.ActivityType type;
      try {
         type = gameJson.isNull("type") ? Activity.ActivityType.PLAYING : Activity.ActivityType.fromKey(Integer.parseInt(gameJson.get("type").toString()));
      } catch (NumberFormatException var21) {
         type = Activity.ActivityType.PLAYING;
      }

      Activity.Timestamps timestamps = null;
      if (!gameJson.isNull("timestamps")) {
         DataObject obj = gameJson.getObject("timestamps");
         long start = obj.getLong("start", 0L);
         long end = obj.getLong("end", 0L);
         timestamps = new Activity.Timestamps(start, end);
      }

      EmojiUnion emoji = null;
      if (!gameJson.isNull("emoji")) {
         emoji = createEmoji(gameJson.getObject("emoji"));
      }

      if (type == Activity.ActivityType.CUSTOM_STATUS && gameJson.hasKey("state")) {
         name = gameJson.getString("state", "");
         gameJson = gameJson.remove("state");
      }

      String state = gameJson.isNull("state") ? null : String.valueOf(gameJson.get("state"));
      if (!CollectionUtils.containsAny(gameJson.keys(), richGameFields)) {
         return new ActivityImpl(name, state, url, type, timestamps, emoji);
      } else {
         long id = gameJson.getLong("application_id", 0L);
         String sessionId = gameJson.getString("session_id", (String)null);
         String syncId = gameJson.getString("sync_id", (String)null);
         int flags = gameJson.getInt("flags", 0);
         String details = gameJson.isNull("details") ? null : String.valueOf(gameJson.get("details"));
         RichPresence.Party party = null;
         if (!gameJson.isNull("party")) {
            DataObject obj = gameJson.getObject("party");
            String partyId = obj.isNull("id") ? null : obj.getString("id");
            DataArray sizeArr = obj.isNull("size") ? null : obj.getArray("size");
            long size = 0L;
            long max = 0L;
            if (sizeArr != null && sizeArr.length() > 0) {
               size = sizeArr.getLong(0);
               max = sizeArr.length() < 2 ? 0L : sizeArr.getLong(1);
            }

            party = new RichPresence.Party(partyId, size, max);
         }

         String smallImageKey = null;
         String smallImageText = null;
         String largeImageKey = null;
         String largeImageText = null;
         if (!gameJson.isNull("assets")) {
            DataObject assets = gameJson.getObject("assets");
            if (!assets.isNull("small_image")) {
               smallImageKey = String.valueOf(assets.get("small_image"));
               smallImageText = assets.isNull("small_text") ? null : String.valueOf(assets.get("small_text"));
            }

            if (!assets.isNull("large_image")) {
               largeImageKey = String.valueOf(assets.get("large_image"));
               largeImageText = assets.isNull("large_text") ? null : String.valueOf(assets.get("large_text"));
            }
         }

         return new RichPresenceImpl(type, name, url, id, emoji, party, details, state, timestamps, syncId, sessionId, flags, largeImageKey, largeImageText, smallImageKey, smallImageText);
      }
   }

   public RichCustomEmojiImpl createEmoji(GuildImpl guildObj, DataObject json) {
      DataArray emojiRoles = (DataArray)json.optArray("roles").orElseGet(DataArray::empty);
      long emojiId = json.getLong("id");
      User user = json.isNull("user") ? null : this.createUser(json.getObject("user"));
      RichCustomEmojiImpl emojiObj = (RichCustomEmojiImpl)guildObj.getEmojiById(emojiId);
      if (emojiObj == null) {
         emojiObj = new RichCustomEmojiImpl(emojiId, guildObj);
      }

      Set<Role> roleSet = emojiObj.getRoleSet();
      roleSet.clear();

      for(int j = 0; j < emojiRoles.length(); ++j) {
         Role role = guildObj.getRoleById(emojiRoles.getString(j));
         if (role != null) {
            roleSet.add(role);
         }
      }

      if (user != null) {
         emojiObj.setOwner(user);
      }

      return emojiObj.setName(json.getString("name", "")).setAnimated(json.getBoolean("animated")).setManaged(json.getBoolean("managed")).setAvailable(json.getBoolean("available", true));
   }

   public ApplicationEmojiImpl createApplicationEmoji(JDAImpl api, DataObject json, User owner) {
      long emojiId = json.getUnsignedLong("id");
      return (new ApplicationEmojiImpl(emojiId, api, owner)).setAnimated(json.getBoolean("animated")).setName(json.getString("name"));
   }

   public ScheduledEvent createScheduledEvent(GuildImpl guild, DataObject json) {
      long id = json.getLong("id");
      ScheduledEventImpl scheduledEvent = (ScheduledEventImpl)guild.getScheduledEventsView().get(id);
      if (scheduledEvent == null) {
         SnowflakeCacheViewImpl<ScheduledEvent> scheduledEventView = guild.getScheduledEventsView();
         UnlockHook hook = scheduledEventView.writeLock();

         try {
            scheduledEvent = new ScheduledEventImpl(id, guild);
            if (this.getJDA().isCacheFlagSet(CacheFlag.SCHEDULED_EVENTS)) {
               scheduledEventView.getMap().put(id, scheduledEvent);
            }
         } catch (Throwable var11) {
            if (hook != null) {
               try {
                  hook.close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }
            }

            throw var11;
         }

         if (hook != null) {
            hook.close();
         }
      }

      scheduledEvent.setName(json.getString("name")).setDescription(json.getString("description", (String)null)).setStatus(ScheduledEvent.Status.fromKey(json.getInt("status", -1))).setInterestedUserCount(json.getInt("user_count", -1)).setStartTime(json.getOffsetDateTime("scheduled_start_time")).setEndTime(json.getOffsetDateTime("scheduled_end_time", (OffsetDateTime)null)).setCoverImage(json.getString("image", (String)null));
      long creatorId = json.getLong("creator_id", 0L);
      scheduledEvent.setCreatorId(creatorId);
      if (creatorId != 0L) {
         if (json.hasKey("creator")) {
            scheduledEvent.setCreator(this.createUser(json.getObject("creator")));
         } else {
            scheduledEvent.setCreator(this.getJDA().getUserById(creatorId));
         }
      }

      ScheduledEvent.Type type = ScheduledEvent.Type.fromKey(json.getInt("entity_type"));
      scheduledEvent.setType(type);
      switch (type) {
         case UNKNOWN:
         default:
            break;
         case STAGE_INSTANCE:
         case VOICE:
            scheduledEvent.setLocation(json.getString("channel_id"));
            break;
         case EXTERNAL:
            String externalLocation;
            if (!json.isNull("entity_metadata") && !json.getObject("entity_metadata").isNull("location")) {
               externalLocation = json.getObject("entity_metadata").getString("location");
            } else {
               externalLocation = "";
            }

            scheduledEvent.setLocation(externalLocation);
      }

      return scheduledEvent;
   }

   public Category createCategory(DataObject json, long guildId) {
      return this.createCategory((GuildImpl)null, json, guildId);
   }

   public Category createCategory(GuildImpl guild, DataObject json, long guildId) {
      boolean playbackCache = false;
      long id = json.getLong("id");
      if (guild == null) {
         guild = (GuildImpl)this.getJDA().getGuildsView().get(guildId);
      }

      if (guild == null) {
         throw new IllegalStateException("Missing guild for id " + Long.toUnsignedString(guildId));
      } else {
         CategoryImpl channel = (CategoryImpl)guild.getCategoryById(id);
         if (channel == null) {
            ChannelCacheViewImpl<GuildChannel> guildView = guild.getChannelView();
            ChannelCacheViewImpl<Channel> globalView = this.getJDA().getChannelsView();
            UnlockHook glock = guildView.writeLock();

            try {
               UnlockHook jlock = globalView.writeLock();

               try {
                  channel = new CategoryImpl(id, guild);
                  guildView.put(channel);
                  playbackCache = globalView.put(channel) == null;
               } catch (Throwable var17) {
                  if (jlock != null) {
                     try {
                        jlock.close();
                     } catch (Throwable var16) {
                        var17.addSuppressed(var16);
                     }
                  }

                  throw var17;
               }

               if (jlock != null) {
                  jlock.close();
               }
            } catch (Throwable var18) {
               if (glock != null) {
                  try {
                     glock.close();
                  } catch (Throwable var15) {
                     var18.addSuppressed(var15);
                  }
               }

               throw var18;
            }

            if (glock != null) {
               glock.close();
            }
         }

         this.configureCategory(json, channel);
         Optional<DataArray> permissionOverwrites = json.optArray("permission_overwrites");
         if (permissionOverwrites.isPresent()) {
            this.createOverridesPass(channel, (DataArray)permissionOverwrites.get());
         }

         if (playbackCache) {
            this.getJDA().getEventCache().playbackCache(EventCache.Type.CHANNEL, id);
         }

         return channel;
      }
   }

   public TextChannel createTextChannel(DataObject json, long guildId) {
      return this.createTextChannel((GuildImpl)null, json, guildId);
   }

   public TextChannel createTextChannel(GuildImpl guildObj, DataObject json, long guildId) {
      boolean playbackCache = false;
      long id = json.getLong("id");
      if (guildObj == null) {
         guildObj = (GuildImpl)this.getJDA().getGuildsView().get(guildId);
      }

      if (guildObj == null) {
         throw new IllegalStateException("Missing guild for id " + Long.toUnsignedString(guildId));
      } else {
         TextChannelImpl channel = (TextChannelImpl)guildObj.getTextChannelById(id);
         if (channel == null) {
            ChannelCacheViewImpl<GuildChannel> guildView = guildObj.getChannelView();
            ChannelCacheViewImpl<Channel> globalView = this.getJDA().getChannelsView();
            UnlockHook glock = guildView.writeLock();

            try {
               UnlockHook jlock = globalView.writeLock();

               try {
                  channel = new TextChannelImpl(id, guildObj);
                  guildView.put(channel);
                  playbackCache = globalView.put(channel) == null;
               } catch (Throwable var17) {
                  if (jlock != null) {
                     try {
                        jlock.close();
                     } catch (Throwable var16) {
                        var17.addSuppressed(var16);
                     }
                  }

                  throw var17;
               }

               if (jlock != null) {
                  jlock.close();
               }
            } catch (Throwable var18) {
               if (glock != null) {
                  try {
                     glock.close();
                  } catch (Throwable var15) {
                     var18.addSuppressed(var15);
                  }
               }

               throw var18;
            }

            if (glock != null) {
               glock.close();
            }
         }

         this.configureTextChannel(json, channel);
         Optional<DataArray> permissionOverwrites = json.optArray("permission_overwrites");
         if (permissionOverwrites.isPresent()) {
            this.createOverridesPass(channel, (DataArray)permissionOverwrites.get());
         }

         if (playbackCache) {
            this.getJDA().getEventCache().playbackCache(EventCache.Type.CHANNEL, id);
         }

         return channel;
      }
   }

   public NewsChannel createNewsChannel(DataObject json, long guildId) {
      return this.createNewsChannel((GuildImpl)null, json, guildId);
   }

   public NewsChannel createNewsChannel(GuildImpl guildObj, DataObject json, long guildId) {
      boolean playbackCache = false;
      long id = json.getLong("id");
      if (guildObj == null) {
         guildObj = (GuildImpl)this.getJDA().getGuildsView().get(guildId);
      }

      if (guildObj == null) {
         throw new IllegalStateException("Missing guild for id " + Long.toUnsignedString(guildId));
      } else {
         NewsChannelImpl channel = (NewsChannelImpl)guildObj.getNewsChannelById(id);
         if (channel == null) {
            ChannelCacheViewImpl<GuildChannel> guildView = guildObj.getChannelView();
            ChannelCacheViewImpl<Channel> globalView = this.getJDA().getChannelsView();
            UnlockHook glock = guildView.writeLock();

            try {
               UnlockHook jlock = globalView.writeLock();

               try {
                  channel = new NewsChannelImpl(id, guildObj);
                  guildView.put(channel);
                  playbackCache = globalView.put(channel) == null;
               } catch (Throwable var17) {
                  if (jlock != null) {
                     try {
                        jlock.close();
                     } catch (Throwable var16) {
                        var17.addSuppressed(var16);
                     }
                  }

                  throw var17;
               }

               if (jlock != null) {
                  jlock.close();
               }
            } catch (Throwable var18) {
               if (glock != null) {
                  try {
                     glock.close();
                  } catch (Throwable var15) {
                     var18.addSuppressed(var15);
                  }
               }

               throw var18;
            }

            if (glock != null) {
               glock.close();
            }
         }

         this.configureNewsChannel(json, channel);
         Optional<DataArray> permissionOverwrites = json.optArray("permission_overwrites");
         if (permissionOverwrites.isPresent()) {
            this.createOverridesPass(channel, (DataArray)permissionOverwrites.get());
         }

         if (playbackCache) {
            this.getJDA().getEventCache().playbackCache(EventCache.Type.CHANNEL, id);
         }

         return channel;
      }
   }

   public VoiceChannel createVoiceChannel(DataObject json, long guildId) {
      return this.createVoiceChannel((GuildImpl)null, json, guildId);
   }

   public VoiceChannel createVoiceChannel(GuildImpl guild, DataObject json, long guildId) {
      boolean playbackCache = false;
      long id = json.getLong("id");
      if (guild == null) {
         guild = (GuildImpl)this.getJDA().getGuildsView().get(guildId);
      }

      if (guild == null) {
         throw new IllegalStateException("Missing guild for id " + Long.toUnsignedString(guildId));
      } else {
         VoiceChannelImpl channel = (VoiceChannelImpl)guild.getVoiceChannelById(id);
         if (channel == null) {
            ChannelCacheViewImpl<GuildChannel> guildView = guild.getChannelView();
            ChannelCacheViewImpl<Channel> globalView = this.getJDA().getChannelsView();
            UnlockHook glock = guildView.writeLock();

            try {
               UnlockHook jlock = globalView.writeLock();

               try {
                  channel = new VoiceChannelImpl(id, guild);
                  guildView.put(channel);
                  playbackCache = globalView.put(channel) == null;
               } catch (Throwable var17) {
                  if (jlock != null) {
                     try {
                        jlock.close();
                     } catch (Throwable var16) {
                        var17.addSuppressed(var16);
                     }
                  }

                  throw var17;
               }

               if (jlock != null) {
                  jlock.close();
               }
            } catch (Throwable var18) {
               if (glock != null) {
                  try {
                     glock.close();
                  } catch (Throwable var15) {
                     var18.addSuppressed(var15);
                  }
               }

               throw var18;
            }

            if (glock != null) {
               glock.close();
            }
         }

         this.configureVoiceChannel(json, channel);
         Optional<DataArray> permissionOverwrites = json.optArray("permission_overwrites");
         if (permissionOverwrites.isPresent()) {
            this.createOverridesPass(channel, (DataArray)permissionOverwrites.get());
         }

         if (playbackCache) {
            this.getJDA().getEventCache().playbackCache(EventCache.Type.CHANNEL, id);
         }

         return channel;
      }
   }

   public StageChannel createStageChannel(DataObject json, long guildId) {
      return this.createStageChannel((GuildImpl)null, json, guildId);
   }

   public StageChannel createStageChannel(GuildImpl guild, DataObject json, long guildId) {
      boolean playbackCache = false;
      long id = json.getLong("id");
      if (guild == null) {
         guild = (GuildImpl)this.getJDA().getGuildsView().get(guildId);
      }

      if (guild == null) {
         throw new IllegalStateException("Missing guild for id " + Long.toUnsignedString(guildId));
      } else {
         StageChannelImpl channel = (StageChannelImpl)guild.getStageChannelById(id);
         if (channel == null) {
            ChannelCacheViewImpl<GuildChannel> guildView = guild.getChannelView();
            ChannelCacheViewImpl<Channel> globalView = this.getJDA().getChannelsView();
            UnlockHook glock = guildView.writeLock();

            try {
               UnlockHook jlock = globalView.writeLock();

               try {
                  channel = new StageChannelImpl(id, guild);
                  guildView.put(channel);
                  playbackCache = globalView.put(channel) == null;
               } catch (Throwable var17) {
                  if (jlock != null) {
                     try {
                        jlock.close();
                     } catch (Throwable var16) {
                        var17.addSuppressed(var16);
                     }
                  }

                  throw var17;
               }

               if (jlock != null) {
                  jlock.close();
               }
            } catch (Throwable var18) {
               if (glock != null) {
                  try {
                     glock.close();
                  } catch (Throwable var15) {
                     var18.addSuppressed(var15);
                  }
               }

               throw var18;
            }

            if (glock != null) {
               glock.close();
            }
         }

         this.configureStageChannel(json, channel);
         Optional<DataArray> permissionOverwrites = json.optArray("permission_overwrites");
         if (permissionOverwrites.isPresent()) {
            this.createOverridesPass(channel, (DataArray)permissionOverwrites.get());
         }

         if (playbackCache) {
            this.getJDA().getEventCache().playbackCache(EventCache.Type.CHANNEL, id);
         }

         return channel;
      }
   }

   public ThreadChannel createThreadChannel(DataObject json, long guildId) {
      return this.createThreadChannel((GuildImpl)null, json, guildId);
   }

   public ThreadChannel createThreadChannel(GuildImpl guild, DataObject json, long guildId) {
      return this.createThreadChannel(guild, json, guildId, true);
   }

   public ThreadChannel createThreadChannel(GuildImpl guild, DataObject json, long guildId, boolean modifyCache) {
      boolean playbackCache = false;
      long id = json.getUnsignedLong("id");
      long parentId = json.getUnsignedLong("parent_id");
      if (guild == null) {
         guild = (GuildImpl)this.getJDA().getGuildsView().get(guildId);
      }

      if (guild == null) {
         throw new IllegalStateException("Missing guild for id " + Long.toUnsignedString(guildId));
      } else {
         IThreadContainer parent = (IThreadContainer)guild.getChannelById(IThreadContainer.class, parentId);
         if (parent == null) {
            throw new IllegalArgumentException("MISSING_CHANNEL");
         } else {
            ThreadChannelImpl channel = (ThreadChannelImpl)this.getJDA().getThreadChannelById(id);
            if (channel == null) {
               ChannelCacheViewImpl<GuildChannel> guildThreadView = guild.getChannelView();
               ChannelCacheViewImpl<Channel> threadView = this.getJDA().getChannelsView();
               UnlockHook vlock = guildThreadView.writeLock();

               try {
                  UnlockHook jlock = threadView.writeLock();

                  try {
                     ChannelType type = ChannelType.fromId(json.getInt("type"));
                     channel = new ThreadChannelImpl(id, guild, type);
                     if (modifyCache) {
                        guildThreadView.put(channel);
                        playbackCache = threadView.put(channel) == null;
                     }
                  } catch (Throwable var23) {
                     if (jlock != null) {
                        try {
                           jlock.close();
                        } catch (Throwable var21) {
                           var23.addSuppressed(var21);
                        }
                     }

                     throw var23;
                  }

                  if (jlock != null) {
                     jlock.close();
                  }
               } catch (Throwable var24) {
                  if (vlock != null) {
                     try {
                        vlock.close();
                     } catch (Throwable var20) {
                        var24.addSuppressed(var20);
                     }
                  }

                  throw var24;
               }

               if (vlock != null) {
                  vlock.close();
               }
            }

            this.configureThreadChannel(json, channel);
            if (!json.isNull("applied_tags") && this.api.isCacheFlagSet(CacheFlag.FORUM_TAGS)) {
               DataArray array = json.getArray("applied_tags");
               IntStream var10001 = IntStream.range(0, array.length());
               Objects.requireNonNull(array);
               channel.setAppliedTags(var10001.mapToLong(array::getUnsignedLong));
            }

            channel.setParentChannel(parent);
            if (!json.isNull("member")) {
               ThreadMember selfThreadMember = this.createThreadMember((ThreadChannelImpl)channel, (Member)guild.getSelfMember(), json.getObject("member"));
               CacheView.SimpleCacheView<ThreadMember> view = channel.getThreadMemberView();
               UnlockHook lock = view.writeLock();

               try {
                  view.getMap().put(selfThreadMember.getIdLong(), selfThreadMember);
               } catch (Throwable var22) {
                  if (lock != null) {
                     try {
                        lock.close();
                     } catch (Throwable var19) {
                        var22.addSuppressed(var19);
                     }
                  }

                  throw var22;
               }

               if (lock != null) {
                  lock.close();
               }
            }

            if (playbackCache) {
               this.getJDA().getEventCache().playbackCache(EventCache.Type.CHANNEL, id);
            }

            return channel;
         }
      }
   }

   public ThreadMember createThreadMember(GuildImpl guild, ThreadChannelImpl threadChannel, DataObject json) {
      DataObject memberJson = json.getObject("member");
      DataObject presenceJson = json.isNull("presence") ? null : json.getObject("presence");
      Member member = this.createMember(guild, memberJson, (DataObject)null, presenceJson);
      return this.createThreadMember(threadChannel, member, json);
   }

   public ThreadMember createThreadMember(ThreadChannelImpl threadChannel, Member member, DataObject json) {
      ThreadMemberImpl threadMember = new ThreadMemberImpl(member, threadChannel);
      threadMember.setJoinedTimestamp(Helpers.toTimestamp(json.getString("join_timestamp")));
      return threadMember;
   }

   public ForumChannel createForumChannel(DataObject json, long guildId) {
      return this.createForumChannel((GuildImpl)null, json, guildId);
   }

   public ForumChannel createForumChannel(GuildImpl guild, DataObject json, long guildId) {
      boolean playbackCache = false;
      long id = json.getLong("id");
      if (guild == null) {
         guild = (GuildImpl)this.getJDA().getGuildsView().get(guildId);
      }

      if (guild == null) {
         throw new IllegalStateException("Missing guild for id " + Long.toUnsignedString(guildId));
      } else {
         ForumChannelImpl channel = (ForumChannelImpl)guild.getForumChannelById(id);
         if (channel == null) {
            ChannelCacheViewImpl<GuildChannel> guildView = guild.getChannelView();
            ChannelCacheViewImpl<Channel> globalView = this.getJDA().getChannelsView();
            UnlockHook glock = guildView.writeLock();

            try {
               UnlockHook jlock = globalView.writeLock();

               try {
                  channel = new ForumChannelImpl(id, guild);
                  guildView.put(channel);
                  playbackCache = globalView.put(channel) == null;
               } catch (Throwable var17) {
                  if (jlock != null) {
                     try {
                        jlock.close();
                     } catch (Throwable var16) {
                        var17.addSuppressed(var16);
                     }
                  }

                  throw var17;
               }

               if (jlock != null) {
                  jlock.close();
               }
            } catch (Throwable var18) {
               if (glock != null) {
                  try {
                     glock.close();
                  } catch (Throwable var15) {
                     var18.addSuppressed(var15);
                  }
               }

               throw var18;
            }

            if (glock != null) {
               glock.close();
            }
         }

         this.configureForumChannel(json, channel);
         Optional<DataArray> permissionOverwrites = json.optArray("permission_overwrites");
         if (permissionOverwrites.isPresent()) {
            this.createOverridesPass(channel, (DataArray)permissionOverwrites.get());
         }

         if (playbackCache) {
            this.getJDA().getEventCache().playbackCache(EventCache.Type.CHANNEL, id);
         }

         return channel;
      }
   }

   public MediaChannel createMediaChannel(DataObject json, long guildId) {
      return this.createMediaChannel((GuildImpl)null, json, guildId);
   }

   public MediaChannel createMediaChannel(GuildImpl guild, DataObject json, long guildId) {
      boolean playbackCache = false;
      long id = json.getLong("id");
      if (guild == null) {
         guild = (GuildImpl)this.getJDA().getGuildsView().get(guildId);
      }

      if (guild == null) {
         throw new IllegalStateException("Missing guild for id " + Long.toUnsignedString(guildId));
      } else {
         MediaChannelImpl channel = (MediaChannelImpl)guild.getMediaChannelById(id);
         if (channel == null) {
            ChannelCacheViewImpl<GuildChannel> guildView = guild.getChannelView();
            ChannelCacheViewImpl<Channel> globalView = this.getJDA().getChannelsView();
            UnlockHook glock = guildView.writeLock();

            try {
               UnlockHook jlock = globalView.writeLock();

               try {
                  channel = new MediaChannelImpl(id, guild);
                  guildView.put(channel);
                  playbackCache = globalView.put(channel) == null;
               } catch (Throwable var17) {
                  if (jlock != null) {
                     try {
                        jlock.close();
                     } catch (Throwable var16) {
                        var17.addSuppressed(var16);
                     }
                  }

                  throw var17;
               }

               if (jlock != null) {
                  jlock.close();
               }
            } catch (Throwable var18) {
               if (glock != null) {
                  try {
                     glock.close();
                  } catch (Throwable var15) {
                     var18.addSuppressed(var15);
                  }
               }

               throw var18;
            }

            if (glock != null) {
               glock.close();
            }
         }

         this.configureMediaChannel(json, channel);
         Optional<DataArray> permissionOverwrites = json.optArray("permission_overwrites");
         if (permissionOverwrites.isPresent()) {
            this.createOverridesPass(channel, (DataArray)permissionOverwrites.get());
         }

         if (playbackCache) {
            this.getJDA().getEventCache().playbackCache(EventCache.Type.CHANNEL, id);
         }

         return channel;
      }
   }

   public PrivateChannel createPrivateChannel(DataObject json) {
      return this.createPrivateChannel(json, (UserImpl)null);
   }

   public PrivateChannel createPrivateChannel(DataObject json, UserImpl user) {
      long channelId = json.getUnsignedLong("id");
      PrivateChannelImpl channel = (PrivateChannelImpl)this.api.getPrivateChannelById(channelId);
      if (channel == null) {
         channel = new PrivateChannelImpl(this.getJDA(), channelId, user);
      }

      this.configurePrivateChannel(json, channel);
      UserImpl recipient = user;
      if (channel.getUser() == null) {
         if (user == null && (json.hasKey("recipients") || json.hasKey("recipient"))) {
            DataObject recipientJson = json.hasKey("recipients") ? json.getArray("recipients").getObject(0) : json.getObject("recipient");
            long userId = recipientJson.getUnsignedLong("id");
            recipient = (UserImpl)this.getJDA().getUserById(userId);
            if (recipient == null) {
               recipient = this.createUser(recipientJson);
            }
         }

         if (recipient != null) {
            channel.setUser(recipient);
         }
      }

      if (recipient != null) {
         recipient.setPrivateChannel(channel);
      }

      this.cachePrivateChannel(channel);
      this.api.usedPrivateChannel(channelId);
      return channel;
   }

   private void cachePrivateChannel(PrivateChannelImpl priv) {
      ChannelCacheViewImpl<Channel> privateView = this.getJDA().getChannelsView();
      privateView.put(priv);
      this.api.usedPrivateChannel(priv.getIdLong());
      this.getJDA().getEventCache().playbackCache(EventCache.Type.CHANNEL, priv.getIdLong());
   }

   @Nullable
   public StageInstance createStageInstance(GuildImpl guild, DataObject json) {
      long channelId = json.getUnsignedLong("channel_id");
      StageChannelImpl channel = (StageChannelImpl)guild.getStageChannelById(channelId);
      if (channel == null) {
         return null;
      } else {
         long id = json.getUnsignedLong("id");
         String topic = json.getString("topic");
         StageInstance.PrivacyLevel level = StageInstance.PrivacyLevel.fromKey(json.getInt("privacy_level", -1));
         StageInstanceImpl instance = (StageInstanceImpl)channel.getStageInstance();
         if (instance == null) {
            instance = new StageInstanceImpl(id, channel);
            channel.setStageInstance(instance);
         }

         return instance.setPrivacyLevel(level).setTopic(topic);
      }
   }

   public void createOverridesPass(IPermissionContainerMixin<?> channel, DataArray overrides) {
      for(int i = 0; i < overrides.length(); ++i) {
         try {
            this.createPermissionOverride(overrides.getObject(i), channel);
         } catch (NoSuchElementException e) {
            LOG.debug((String)"{}. Ignoring PermissionOverride.", (Object)e.getMessage());
         } catch (IllegalArgumentException e) {
            LOG.warn((String)"{}. Ignoring PermissionOverride.", (Object)e.getMessage());
         }
      }

   }

   public Role createRole(GuildImpl guild, DataObject roleJson, long guildId) {
      boolean playbackCache = false;
      long id = roleJson.getLong("id");
      if (guild == null) {
         guild = (GuildImpl)this.getJDA().getGuildsView().get(guildId);
      }

      RoleImpl role = (RoleImpl)guild.getRolesView().get(id);
      if (role == null) {
         SnowflakeCacheViewImpl<Role> roleView = guild.getRolesView();
         UnlockHook hook = roleView.writeLock();

         try {
            role = new RoleImpl(id, guild);
            playbackCache = roleView.getMap().put(id, role) == null;
         } catch (Throwable var14) {
            if (hook != null) {
               try {
                  hook.close();
               } catch (Throwable var13) {
                  var14.addSuppressed(var13);
               }
            }

            throw var14;
         }

         if (hook != null) {
            hook.close();
         }
      }

      this.configureRole(roleJson, role, id);
      if (playbackCache) {
         this.getJDA().getEventCache().playbackCache(EventCache.Type.ROLE, id);
      }

      return role;
   }

   public ReceivedMessage createMessageBestEffort(DataObject json, MessageChannel channel, Guild guild) {
      return channel != null ? this.createMessageWithChannel(json, channel, false) : this.createMessageFromWebhook(json, guild);
   }

   public ReceivedMessage createMessageFromWebhook(DataObject json, @Nullable Guild guild) {
      return this.createMessage0(json, (MessageChannel)null, (GuildImpl)guild, false);
   }

   public ReceivedMessage createMessageWithChannel(DataObject json, @Nonnull MessageChannel channel, boolean modifyCache) {
      if (channel instanceof GuildMessageChannel) {
         GuildMessageChannel messageChannel = (GuildMessageChannel)channel;
         return this.createMessage0(json, channel, messageChannel.isDetached() ? null : (GuildImpl)messageChannel.getGuild(), modifyCache);
      } else if (channel instanceof GroupChannel) {
         return this.createMessage0(json, channel, (GuildImpl)null, modifyCache);
      } else if (channel instanceof PrivateChannel) {
         return this.createMessageWithLookup(json, (Guild)null, modifyCache);
      } else {
         throw new IllegalArgumentException("MISSING_CHANNEL");
      }
   }

   public ReceivedMessage createMessageWithLookup(DataObject json, @Nullable Guild guild, boolean modifyCache) {
      if (guild == null) {
         return this.createMessage0(json, this.createPrivateChannelByMessage(json), (GuildImpl)null, modifyCache);
      } else {
         MessageChannel channel = (MessageChannel)guild.getChannelById(GuildMessageChannel.class, json.getUnsignedLong("channel_id"));
         return this.createMessage0(json, channel, (GuildImpl)guild, modifyCache);
      }
   }

   private PrivateChannel createPrivateChannelByMessage(DataObject message) {
      long channelId = message.getLong("channel_id");
      DataObject author = message.getObject("author");
      PrivateChannelImpl channel = (PrivateChannelImpl)this.getJDA().getPrivateChannelById(channelId);
      boolean isRecipient = !author.getBoolean("bot");
      if (channel == null) {
         DataObject channelData = DataObject.empty().put("id", channelId);
         if (isRecipient) {
            channelData.put("recipient", author);
         }

         channel = (PrivateChannelImpl)this.createPrivateChannel(channelData);
      } else if (channel.getUser() == null && isRecipient) {
         UserImpl user = this.createUser(author);
         channel.setUser(user);
         user.setPrivateChannel(channel);
      }

      return channel;
   }

   private ReceivedMessage createMessage0(DataObject jsonObject, @Nullable MessageChannel channel, @Nullable GuildImpl guild, boolean modifyCache) {
      MessageType type = MessageType.fromId(jsonObject.getInt("type"));
      if (type == MessageType.UNKNOWN) {
         throw new IllegalArgumentException("UNKNOWN_MESSAGE_TYPE");
      } else {
         long id = jsonObject.getLong("id");
         DataObject author = jsonObject.getObject("author");
         long authorId = author.getLong("id");
         long channelId = jsonObject.getUnsignedLong("channel_id");
         long guildId = channel instanceof GuildChannel ? ((GuildChannel)channel).getGuild().getIdLong() : jsonObject.getUnsignedLong("guild_id", 0L);
         MemberImpl member = null;
         if (guild != null && !jsonObject.isNull("member")) {
            DataObject memberJson = jsonObject.getObject("member");
            memberJson.put("user", author);
            member = this.createMember(guild, memberJson);
            if (modifyCache) {
               this.updateMemberCache(member);
            }
         }

         String content = jsonObject.getString("content", "");
         boolean fromWebhook = jsonObject.hasKey("webhook_id");
         long applicationId = jsonObject.getUnsignedLong("application_id", 0L);
         boolean pinned = jsonObject.getBoolean("pinned");
         boolean tts = jsonObject.getBoolean("tts");
         boolean mentionsEveryone = jsonObject.getBoolean("mention_everyone");
         OffsetDateTime editTime = jsonObject.isNull("edited_timestamp") ? null : OffsetDateTime.parse(jsonObject.getString("edited_timestamp"));
         String nonce = jsonObject.isNull("nonce") ? null : jsonObject.get("nonce").toString();
         int flags = jsonObject.getInt("flags", 0);
         List<Message.Attachment> attachments = this.<Message.Attachment>map(jsonObject, "attachments", this::createMessageAttachment);
         List<MessageEmbed> embeds = this.<MessageEmbed>map(jsonObject, "embeds", this::createMessageEmbed);
         List<MessageReaction> reactions = this.<MessageReaction>map(jsonObject, "reactions", (obj) -> this.createMessageReaction(channel, channelId, id, obj));
         List<StickerItem> stickers = this.<StickerItem>map(jsonObject, "sticker_items", this::createStickerItem);
         List<MessageTopLevelComponentUnion> components = this.<MessageTopLevelComponentUnion>map(jsonObject, "components", (obj) -> (MessageTopLevelComponentUnion)DEFAULT_COMPONENT_DESERIALIZER.deserializeAs(MessageTopLevelComponentUnion.class, obj));
         MessagePoll poll = (MessagePoll)jsonObject.optObject("poll").map(EntityBuilder::createMessagePoll).orElse((Object)null);
         MessageActivity activity = null;
         if (!jsonObject.isNull("activity")) {
            activity = createMessageActivity(jsonObject);
         }

         User user;
         if (guild != null) {
            if (member == null) {
               member = (MemberImpl)guild.getMemberById(authorId);
            }

            user = member != null ? member.getUser() : null;
            if (user == null) {
               if (!fromWebhook && modifyCache) {
                  throw new IllegalArgumentException("MISSING_USER");
               }

               user = this.createUser(author);
            }
         } else if (channel instanceof PrivateChannel) {
            if (authorId == this.getJDA().getSelfUser().getIdLong()) {
               user = this.getJDA().getSelfUser();
            } else {
               user = ((PrivateChannel)channel).getUser();
               if (user == null) {
                  user = this.createUser(author);
               }
            }
         } else {
            user = this.createUser(author);
         }

         if (modifyCache && !fromWebhook) {
            this.updateUser((UserImpl)user, author);
         }

         Message referencedMessage = null;
         if (!jsonObject.isNull("referenced_message")) {
            DataObject referenceJson = jsonObject.getObject("referenced_message");

            try {
               referencedMessage = this.createMessage0(referenceJson, channel, guild, false);
            } catch (IllegalArgumentException ex) {
               if ("UNKNOWN_MESSAGE_TYPE".equals(ex.getMessage())) {
                  LOG.debug((String)"Received referenced message with unknown type. Type: {}", (Object)referenceJson.getInt("type", -1));
               } else {
                  if (!"MISSING_CHANNEL".equals(ex.getMessage())) {
                     throw ex;
                  }

                  LOG.debug((String)"Received referenced message with unknown channel. channel_id: {} Type: {}", (Object)referenceJson.getUnsignedLong("channel_id", 0L), (Object)referenceJson.getInt("type", -1));
               }
            }
         }

         List<MessageSnapshot> snapshots = Collections.emptyList();
         MessageReference messageReference = null;
         if (!jsonObject.isNull("message_reference")) {
            DataObject messageReferenceJson = jsonObject.getObject("message_reference");
            messageReference = new MessageReference(messageReferenceJson.getInt("type", -1), messageReferenceJson.getLong("message_id", 0L), messageReferenceJson.getLong("channel_id", 0L), messageReferenceJson.getLong("guild_id", 0L), referencedMessage, this.api);
            snapshots = this.<MessageSnapshot>map(jsonObject, "message_snapshots", (obj) -> this.createMessageSnapshot(messageReference, obj.getObject("message")));
         }

         Message.Interaction messageInteraction = null;
         if (!jsonObject.isNull("interaction")) {
            messageInteraction = this.createMessageInteraction(guild, jsonObject.getObject("interaction"));
         }

         Message.InteractionMetadata interactionMetadata = null;
         if (!jsonObject.isNull("interaction_metadata")) {
            interactionMetadata = this.createMessageInteractionMetadata(jsonObject.getObject("interaction_metadata"));
         }

         Mentions mentions = new MessageMentionsImpl(this.api, guild, content, mentionsEveryone, jsonObject.getArray("mentions"), jsonObject.getArray("mention_roles"));
         ThreadChannel startedThread = null;
         if (guild != null && !jsonObject.isNull("thread")) {
            startedThread = this.createThreadChannel(guild, jsonObject.getObject("thread"), guild.getIdLong());
         }

         int position = jsonObject.getInt("position", -1);
         return new ReceivedMessage(id, channelId, guildId, this.api, guild, channel, type, messageReference, fromWebhook, applicationId, tts, pinned, content, nonce, user, member, activity, poll, editTime, mentions, reactions, attachments, embeds, stickers, components, snapshots, flags, messageInteraction, interactionMetadata, startedThread, position);
      }
   }

   private static MessageActivity createMessageActivity(DataObject jsonObject) {
      DataObject activityData = jsonObject.getObject("activity");
      MessageActivity.ActivityType activityType = MessageActivity.ActivityType.fromId(activityData.getInt("type"));
      String partyId = activityData.getString("party_id", (String)null);
      MessageActivity.Application application = null;
      if (!jsonObject.isNull("application")) {
         DataObject applicationData = jsonObject.getObject("application");
         String name = applicationData.getString("name");
         String description = applicationData.getString("description", "");
         String iconId = applicationData.getString("icon", (String)null);
         String coverId = applicationData.getString("cover_image", (String)null);
         long applicationId = applicationData.getLong("id");
         application = new MessageActivity.Application(name, description, iconId, coverId, applicationId);
      }

      if (activityType == MessageActivity.ActivityType.UNKNOWN) {
         LOG.debug((String)"Received an unknown ActivityType, Activity: {}", (Object)activityData);
      }

      return new MessageActivity(activityType, partyId, application);
   }

   public static MessagePollImpl createMessagePoll(DataObject data) {
      MessagePoll.LayoutType layout = MessagePoll.LayoutType.fromKey(data.getInt("layout_type"));
      OffsetDateTime expiresAt = data.isNull("expiry") ? null : data.getOffsetDateTime("expiry");
      boolean isMultiAnswer = data.getBoolean("allow_multiselect");
      DataArray answersData = data.getArray("answers");
      DataObject questionData = data.getObject("question");
      DataObject resultsData = (DataObject)data.optObject("results").orElseGet(() -> DataObject.empty().put("answer_counts", DataArray.empty()));
      boolean isFinalized = resultsData.getBoolean("is_finalized");
      DataArray resultVotes = resultsData.getArray("answer_counts");
      TLongObjectMap<DataObject> voteMapping = new TLongObjectHashMap<DataObject>();
      resultVotes.stream(DataArray::getObject).forEach((votes) -> voteMapping.put(votes.getLong("id"), votes));
      MessagePoll.Question question = new MessagePoll.Question(questionData.getString("text"), (Emoji)questionData.optObject("emoji").map(Emoji::fromData).orElse((Object)null));
      List<MessagePoll.Answer> answers = (List)answersData.stream(DataArray::getObject).map((answer) -> {
         long answerId = answer.getLong("answer_id");
         DataObject media = answer.getObject("poll_media");
         DataObject votes = voteMapping.get(answerId);
         return new MessagePoll.Answer(answerId, media.getString("text"), (EmojiUnion)media.optObject("emoji").map(Emoji::fromData).orElse((Object)null), votes != null ? votes.getInt("count") : 0, votes != null && votes.getBoolean("me_voted"));
      }).collect(Helpers.toUnmodifiableList());
      return new MessagePollImpl(layout, question, answers, expiresAt, isMultiAnswer, isFinalized);
   }

   public MessageReaction createMessageReaction(MessageChannel chan, long channelId, long messageId, DataObject obj) {
      DataObject emoji = obj.getObject("emoji");
      int[] count = new int[]{obj.getInt("count", 0), (Integer)obj.optObject("count_details").map((o) -> o.getInt("normal", 0)).orElse(0), (Integer)obj.optObject("count_details").map((o) -> o.getInt("burst", 0)).orElse(0)};
      boolean[] me = new boolean[]{obj.getBoolean("me"), obj.getBoolean("me_burst")};
      EmojiUnion emojiObj = createEmoji(emoji);
      return new MessageReaction(this.api, chan, emojiObj, channelId, messageId, me, count);
   }

   public Message.Attachment createMessageAttachment(DataObject jsonObject) {
      boolean ephemeral = jsonObject.getBoolean("ephemeral", false);
      int width = jsonObject.getInt("width", -1);
      int height = jsonObject.getInt("height", -1);
      int size = jsonObject.getInt("size");
      String url = jsonObject.getString("url");
      String proxyUrl = jsonObject.getString("proxy_url");
      String filename = jsonObject.getString("filename");
      String contentType = jsonObject.getString("content_type", (String)null);
      String description = jsonObject.getString("description", (String)null);
      long id = jsonObject.getLong("id");
      String waveform = jsonObject.getString("waveform", (String)null);
      double duration = jsonObject.getDouble("duration_secs", (double)0.0F);
      return new Message.Attachment(id, url, proxyUrl, filename, contentType, description, size, height, width, ephemeral, waveform, duration, this.getJDA());
   }

   public MessageEmbed createMessageEmbed(DataObject content) {
      if (content.isNull("type")) {
         throw new IllegalStateException("Encountered embed object with missing/null type field for Json: " + content);
      } else {
         EmbedType type = EmbedType.fromKey(content.getString("type"));
         String url = content.getString("url", (String)null);
         String title = content.getString("title", (String)null);
         String description = content.getString("description", (String)null);
         OffsetDateTime timestamp = content.isNull("timestamp") ? null : OffsetDateTime.parse(content.getString("timestamp"));
         int color = content.isNull("color") ? 536870911 : content.getInt("color");
         MessageEmbed.Thumbnail thumbnail;
         if (content.isNull("thumbnail")) {
            thumbnail = null;
         } else {
            DataObject obj = content.getObject("thumbnail");
            thumbnail = new MessageEmbed.Thumbnail(obj.getString("url", (String)null), obj.getString("proxy_url", (String)null), obj.getInt("width", -1), obj.getInt("height", -1));
         }

         MessageEmbed.Provider provider;
         if (content.isNull("provider")) {
            provider = null;
         } else {
            DataObject obj = content.getObject("provider");
            provider = new MessageEmbed.Provider(obj.getString("name", (String)null), obj.getString("url", (String)null));
         }

         MessageEmbed.AuthorInfo author;
         if (content.isNull("author")) {
            author = null;
         } else {
            DataObject obj = content.getObject("author");
            author = new MessageEmbed.AuthorInfo(obj.getString("name", (String)null), obj.getString("url", (String)null), obj.getString("icon_url", (String)null), obj.getString("proxy_icon_url", (String)null));
         }

         MessageEmbed.VideoInfo video;
         if (content.isNull("video")) {
            video = null;
         } else {
            DataObject obj = content.getObject("video");
            video = new MessageEmbed.VideoInfo(obj.getString("url", (String)null), obj.getString("proxy_url", (String)null), obj.getInt("width", -1), obj.getInt("height", -1));
         }

         MessageEmbed.Footer footer;
         if (content.isNull("footer")) {
            footer = null;
         } else {
            DataObject obj = content.getObject("footer");
            footer = new MessageEmbed.Footer(obj.getString("text", (String)null), obj.getString("icon_url", (String)null), obj.getString("proxy_icon_url", (String)null));
         }

         MessageEmbed.ImageInfo image;
         if (content.isNull("image")) {
            image = null;
         } else {
            DataObject obj = content.getObject("image");
            image = new MessageEmbed.ImageInfo(obj.getString("url", (String)null), obj.getString("proxy_url", (String)null), obj.getInt("width", -1), obj.getInt("height", -1));
         }

         List<MessageEmbed.Field> fields = this.<MessageEmbed.Field>map(content, "fields", (objx) -> new MessageEmbed.Field(objx.getString("name", (String)null), objx.getString("value", (String)null), objx.getBoolean("inline"), false));
         return createMessageEmbed(url, title, description, type, timestamp, color, thumbnail, provider, author, video, footer, image, fields);
      }
   }

   public static MessageEmbed createMessageEmbed(String url, String title, String description, EmbedType type, OffsetDateTime timestamp, int color, MessageEmbed.Thumbnail thumbnail, MessageEmbed.Provider siteProvider, MessageEmbed.AuthorInfo author, MessageEmbed.VideoInfo videoInfo, MessageEmbed.Footer footer, MessageEmbed.ImageInfo image, List<MessageEmbed.Field> fields) {
      return new MessageEmbed(url, title, description, type, timestamp, color, thumbnail, siteProvider, author, videoInfo, footer, image, fields);
   }

   public StickerItem createStickerItem(DataObject content) {
      long id = content.getLong("id");
      String name = content.getString("name");
      Sticker.StickerFormat format = Sticker.StickerFormat.fromId(content.getInt("format_type"));
      return new StickerItemImpl(id, format, name);
   }

   public RichStickerImpl createRichSticker(DataObject content) {
      long id = content.getLong("id");
      String name = content.getString("name");
      Sticker.StickerFormat format = Sticker.StickerFormat.fromId(content.getInt("format_type"));
      Sticker.Type type = Sticker.Type.fromId(content.getInt("type", -1));
      String description = content.getString("description", "");
      Set<String> tags = Collections.emptySet();
      if (!content.isNull("tags")) {
         String[] array = content.getString("tags").split(",\\s*");
         tags = Helpers.<String>setOf(array);
      }

      switch (type) {
         case GUILD:
            boolean available = content.getBoolean("available");
            long guildId = content.getUnsignedLong("guild_id", 0L);
            User owner = content.isNull("user") ? null : this.createUser(content.getObject("user"));
            return new GuildStickerImpl(id, format, name, tags, description, available, guildId, this.api, owner);
         case STANDARD:
            long packId = content.getUnsignedLong("pack_id", 0L);
            int sortValue = content.getInt("sort_value", -1);
            return new StandardStickerImpl(id, format, name, tags, description, packId, sortValue);
         default:
            throw new IllegalArgumentException("Unknown sticker type. Type: " + type + " JSON: " + content);
      }
   }

   public StickerPack createStickerPack(DataObject content) {
      long id = content.getUnsignedLong("id");
      String name = content.getString("name");
      String description = content.getString("description", "");
      long skuId = content.getUnsignedLong("sku_id", 0L);
      long coverId = content.getUnsignedLong("cover_sticker_id", 0L);
      long bannerId = content.getUnsignedLong("banner_asset_id", 0L);
      DataArray stickerArr = content.getArray("stickers");
      List<StandardSticker> stickers = new ArrayList(stickerArr.length());

      for(int i = 0; i < stickerArr.length(); ++i) {
         DataObject object = null;

         try {
            object = stickerArr.getObject(i);
            StandardSticker sticker = (StandardSticker)this.createRichSticker(object);
            stickers.add(sticker);
         } catch (ClassCastException | ParsingException var17) {
            LOG.error("Sticker contained in pack {} ({}) could not be parsed. JSON: {}", name, id, object);
         }
      }

      return new StickerPackImpl(id, stickers, name, description, coverId, bannerId, skuId);
   }

   public Message.Interaction createMessageInteraction(GuildImpl guildImpl, DataObject content) {
      long id = content.getLong("id");
      int type = content.getInt("type");
      String name = content.getString("name");
      DataObject userJson = content.getObject("user");
      User user = null;
      MemberImpl member = null;
      Object var11;
      if (!content.isNull("member") && guildImpl != null) {
         DataObject memberJson = content.getObject("member");
         memberJson.put("user", userJson);
         member = this.createMember(guildImpl, memberJson);
         var11 = member.getUser();
      } else {
         var11 = this.createUser(userJson);
      }

      return new Message.Interaction(id, type, name, (User)var11, member);
   }

   public Message.InteractionMetadata createMessageInteractionMetadata(DataObject content) {
      long id = content.getLong("id");
      int type = content.getInt("type");
      User user = this.createUser(content.getObject("user"));
      IntegrationOwners integrationOwners = new IntegrationOwnersImpl(content.getObject("authorizing_integration_owners"));
      long originalResponseMessageId = content.isNull("original_response_message_id") ? 0L : content.getLong("original_response_message_id");
      long interactedMessageId = content.isNull("interacted_message_id") ? 0L : content.getLong("interacted_message_id");
      Message.InteractionMetadata triggeringInteraction = (Message.InteractionMetadata)content.optObject("triggering_interaction_metadata").map(this::createMessageInteractionMetadata).orElse((Object)null);
      User targetUser = (User)content.optObject("target_user").map(this::createUser).orElse((Object)null);
      long targetMessageId = content.isNull("target_message_id") ? 0L : content.getLong("target_message_id");
      return new Message.InteractionMetadata(id, type, user, integrationOwners, originalResponseMessageId, interactedMessageId, triggeringInteraction, targetUser, targetMessageId);
   }

   public MessageSnapshot createMessageSnapshot(MessageReference messageReference, DataObject jsonObject) {
      MessageType type = MessageType.fromId(jsonObject.getInt("type"));
      String content = jsonObject.getString("content", "");
      OffsetDateTime editTime = jsonObject.isNull("edited_timestamp") ? null : OffsetDateTime.parse(jsonObject.getString("edited_timestamp"));
      int flags = jsonObject.getInt("flags", 0);
      boolean mentionsEveryone = jsonObject.getBoolean("mention_everyone");
      List<Message.Attachment> attachments = this.<Message.Attachment>map(jsonObject, "attachments", this::createMessageAttachment);
      List<MessageEmbed> embeds = this.<MessageEmbed>map(jsonObject, "embeds", this::createMessageEmbed);
      List<StickerItem> stickers = this.<StickerItem>map(jsonObject, "sticker_items", this::createStickerItem);
      List<MessageTopLevelComponentUnion> components = this.<MessageTopLevelComponentUnion>map(jsonObject, "components", (obj) -> (MessageTopLevelComponentUnion)DEFAULT_COMPONENT_DESERIALIZER.deserializeAs(MessageTopLevelComponentUnion.class, obj));
      Guild guild = messageReference.getGuild();
      Mentions mentions = new MessageMentionsImpl(this.api, guild instanceof GuildImpl ? (GuildImpl)guild : null, content, mentionsEveryone, jsonObject.getArray("mentions"), (DataArray)jsonObject.optArray("mention_roles").orElseGet(DataArray::empty));
      return new MessageSnapshot(type, mentions, editTime, content, attachments, embeds, components, stickers, (long)flags);
   }

   @Nullable
   public PermissionOverride createPermissionOverride(DataObject override, IPermissionContainerMixin<?> chan) {
      int type = override.getInt("type");
      long id = override.getLong("id");
      boolean role = type == 0;
      if (role && chan.getGuild().getRoleById(id) == null) {
         throw new NoSuchElementException("Attempted to create a PermissionOverride for a non-existent role! JSON: " + override);
      } else if (!role && type != 1) {
         throw new IllegalArgumentException("Provided with an unknown PermissionOverride type! JSON: " + override);
      } else if (!role && id != this.api.getSelfUser().getIdLong() && !this.api.isCacheFlagSet(CacheFlag.MEMBER_OVERRIDES)) {
         return null;
      } else {
         long allow = override.getLong("allow");
         long deny = override.getLong("deny");
         if (id == chan.getGuild().getIdLong() && (allow | deny) == 0L) {
            return null;
         } else {
            PermissionOverrideImpl permOverride = (PermissionOverrideImpl)chan.getPermissionOverrideMap().get(id);
            if (permOverride == null) {
               permOverride = new PermissionOverrideImpl(chan, id, role);
               chan.getPermissionOverrideMap().put(id, permOverride);
            }

            return permOverride.setAllow(allow).setDeny(deny);
         }
      }
   }

   public WebhookImpl createWebhook(DataObject object) {
      return this.createWebhook(object, false);
   }

   public WebhookImpl createWebhook(DataObject object, boolean allowMissingChannel) {
      long id = object.getLong("id");
      long guildId = object.getUnsignedLong("guild_id");
      long channelId = object.getUnsignedLong("channel_id");
      String token = object.getString("token", (String)null);
      WebhookType type = WebhookType.fromKey(object.getInt("type", -1));
      IWebhookContainer channel = (IWebhookContainer)this.getJDA().getChannelById(IWebhookContainer.class, channelId);
      if (channel == null && !allowMissingChannel) {
         throw new NullPointerException(String.format("Tried to create Webhook for an un-cached IWebhookContainer channel! WebhookId: %s ChannelId: %s GuildId: %s", id, channelId, guildId));
      } else {
         Object name = !object.isNull("name") ? object.get("name") : null;
         Object avatar = !object.isNull("avatar") ? object.get("avatar") : null;
         DataObject fakeUser = DataObject.empty().put("username", name).put("discriminator", "0000").put("id", id).put("avatar", avatar);
         User defaultUser = this.createUser(fakeUser);
         Optional<DataObject> ownerJson = object.optObject("user");
         User owner = null;
         if (ownerJson.isPresent()) {
            DataObject json = (DataObject)ownerJson.get();
            long userId = json.getLong("id");
            owner = this.getJDA().getUserById(userId);
            if (owner == null) {
               json.put("id", userId);
               owner = this.createUser(json);
            }
         }

         Member ownerMember = owner != null && channel != null ? channel.getGuild().getMember(owner) : null;
         WebhookImpl webhook = (new WebhookImpl(channel, this.getJDA(), id, type)).setToken(token).setOwner(ownerMember, owner).setUser(defaultUser);
         if (!object.isNull("source_channel")) {
            DataObject source = object.getObject("source_channel");
            webhook.setSourceChannel(new Webhook.ChannelReference(source.getUnsignedLong("id"), source.getString("name")));
         }

         if (!object.isNull("source_guild")) {
            DataObject source = object.getObject("source_guild");
            webhook.setSourceGuild(new Webhook.GuildReference(source.getUnsignedLong("id"), source.getString("name")));
         }

         return webhook;
      }
   }

   public Invite createInvite(DataObject object) {
      String code = object.getString("code");
      User inviter = object.hasKey("inviter") ? this.createUser(object.getObject("inviter")) : null;
      DataObject channelObject = object.getObject("channel");
      ChannelType channelType = ChannelType.fromId(channelObject.getInt("type"));
      Invite.TargetType targetType = Invite.TargetType.fromId(object.getInt("target_type", 0));
      boolean guest = (object.getInt("flags", 0) & 1) == 1;
      Invite.InviteType type;
      Invite.Guild guild;
      Invite.Channel channel;
      Invite.Group group;
      if (channelType == ChannelType.GROUP) {
         type = Invite.InviteType.GROUP;
         guild = null;
         channel = null;
         String groupName = channelObject.getString("name", "");
         long groupId = channelObject.getLong("id");
         String groupIconId = channelObject.getString("icon", (String)null);
         List<String> usernames;
         if (channelObject.isNull("recipients")) {
            usernames = null;
         } else {
            usernames = this.<String>map(channelObject, "recipients", (json) -> json.getString("username"));
         }

         group = new InviteImpl.GroupImpl(groupIconId, groupName, groupId, usernames);
      } else if (channelType.isGuild()) {
         type = Invite.InviteType.GUILD;
         DataObject guildObject = object.getObject("guild");
         String guildVanityCode = guildObject.getString("vanity_url_code", (String)null);
         String guildBannerId = guildObject.getString("banner", (String)null);
         String guildIconId = guildObject.getString("icon", (String)null);
         long guildId = guildObject.getLong("id");
         String guildName = guildObject.getString("name");
         String guildSplashId = guildObject.getString("splash", (String)null);
         String description = guildObject.getString("description", (String)null);
         Guild.VerificationLevel guildVerificationLevel = Guild.VerificationLevel.fromKey(guildObject.getInt("verification_level", -1));
         Guild.NSFWLevel guildNsfwLevel = Guild.NSFWLevel.fromKey(guildObject.getInt("nsfw_level", -1));
         int presenceCount = object.getInt("approximate_presence_count", -1);
         int memberCount = object.getInt("approximate_member_count", -1);
         Set<String> guildFeatures;
         if (guildObject.isNull("features")) {
            guildFeatures = Collections.emptySet();
         } else {
            guildFeatures = Collections.unmodifiableSet((Set)StreamSupport.stream(guildObject.getArray("features").spliterator(), false).map(String::valueOf).collect(Collectors.toSet()));
         }

         GuildWelcomeScreen welcomeScreen = guildObject.isNull("welcome_screen") ? null : this.createWelcomeScreen((Guild)null, guildObject.getObject("welcome_screen"));
         guild = new InviteImpl.GuildImpl(guildId, guildVanityCode, guildBannerId, guildIconId, guildName, guildSplashId, description, guildVerificationLevel, guildNsfwLevel, presenceCount, memberCount, guildFeatures, welcomeScreen);
         String channelName = channelObject.getString("name");
         long channelId = channelObject.getLong("id");
         channel = new InviteImpl.ChannelImpl(channelId, channelName, channelType);
         group = null;
      } else {
         type = Invite.InviteType.UNKNOWN;
         guild = null;
         channel = null;
         group = null;
      }

      Invite.InviteTarget target;
      switch (targetType) {
         case EMBEDDED_APPLICATION:
            DataObject applicationObject = object.getObject("target_application");
            Invite.EmbeddedApplication application = new InviteImpl.EmbeddedApplicationImpl(applicationObject.getString("icon", (String)null), applicationObject.getString("name"), applicationObject.getString("description"), applicationObject.getString("summary"), applicationObject.getLong("id"), applicationObject.getInt("max_participants", -1));
            target = new InviteImpl.InviteTargetImpl(targetType, application, (User)null);
            break;
         case STREAM:
            DataObject targetUserObject = object.getObject("target_user");
            target = new InviteImpl.InviteTargetImpl(targetType, (Invite.EmbeddedApplication)null, this.createUser(targetUserObject));
            break;
         case NONE:
            target = null;
            break;
         default:
            target = new InviteImpl.InviteTargetImpl(targetType, (Invite.EmbeddedApplication)null, (User)null);
      }

      boolean expanded;
      int maxAge;
      int maxUses;
      boolean temporary;
      OffsetDateTime timeCreated;
      int uses;
      if (object.hasKey("max_uses")) {
         expanded = true;
         maxAge = object.getInt("max_age");
         maxUses = object.getInt("max_uses");
         uses = object.getInt("uses");
         temporary = object.getBoolean("temporary");
         timeCreated = OffsetDateTime.parse(object.getString("created_at"));
      } else {
         expanded = false;
         maxAge = -1;
         maxUses = -1;
         uses = -1;
         temporary = false;
         timeCreated = null;
      }

      return new InviteImpl(this.getJDA(), code, expanded, inviter, maxAge, maxUses, temporary, guest, timeCreated, uses, channel, guild, group, target, type);
   }

   public GuildWelcomeScreen createWelcomeScreen(Guild guild, DataObject object) {
      DataArray welcomeChannelsArray = object.getArray("welcome_channels");
      List<GuildWelcomeScreen.Channel> welcomeChannels = new ArrayList(welcomeChannelsArray.length());

      for(int i = 0; i < welcomeChannelsArray.length(); ++i) {
         DataObject welcomeChannelObj = welcomeChannelsArray.getObject(i);
         EmojiUnion emoji = null;
         if (!welcomeChannelObj.isNull("emoji_id") || !welcomeChannelObj.isNull("emoji_name")) {
            emoji = createEmoji(welcomeChannelObj, "emoji_name", "emoji_id");
         }

         welcomeChannels.add(new GuildWelcomeScreenImpl.ChannelImpl(guild, welcomeChannelObj.getLong("channel_id"), welcomeChannelObj.getString("description"), emoji));
      }

      return new GuildWelcomeScreenImpl(guild, object.getString("description", (String)null), Collections.unmodifiableList(welcomeChannels));
   }

   public Template createTemplate(DataObject object) {
      String code = object.getString("code");
      String name = object.getString("name");
      String description = object.getString("description", (String)null);
      int uses = object.getInt("usage_count");
      User creator = this.createUser(object.getObject("creator"));
      OffsetDateTime createdAt = OffsetDateTime.parse(object.getString("created_at"));
      OffsetDateTime updatedAt = OffsetDateTime.parse(object.getString("updated_at"));
      long guildId = object.getLong("source_guild_id");
      DataObject guildObject = object.getObject("serialized_source_guild");
      String guildName = guildObject.getString("name");
      String guildDescription = guildObject.getString("description", (String)null);
      String guildIconId = guildObject.getString("icon_hash", (String)null);
      Guild.VerificationLevel guildVerificationLevel = Guild.VerificationLevel.fromKey(guildObject.getInt("verification_level", -1));
      Guild.NotificationLevel notificationLevel = Guild.NotificationLevel.fromKey(guildObject.getInt("default_message_notifications", 0));
      Guild.ExplicitContentLevel explicitContentLevel = Guild.ExplicitContentLevel.fromKey(guildObject.getInt("explicit_content_filter", 0));
      DiscordLocale locale = DiscordLocale.from(guildObject.getString("preferred_locale", "en-US"));
      Guild.Timeout afkTimeout = Guild.Timeout.fromKey(guildObject.getInt("afk_timeout", 0));
      DataArray roleArray = guildObject.getArray("roles");
      DataArray channelsArray = guildObject.getArray("channels");
      long afkChannelId = guildObject.getLong("afk_channel_id", -1L);
      long systemChannelId = guildObject.getLong("system_channel_id", -1L);
      List<TemplateRole> roles = new ArrayList();

      for(int i = 0; i < roleArray.length(); ++i) {
         DataObject obj = roleArray.getObject(i);
         long roleId = obj.getLong("id");
         String roleName = obj.getString("name");
         int roleColor = obj.getInt("color");
         boolean hoisted = obj.getBoolean("hoist");
         boolean mentionable = obj.getBoolean("mentionable");
         long rawPermissions = obj.getLong("permissions");
         roles.add(new TemplateRole(roleId, roleName, roleColor == 0 ? 536870911 : roleColor, hoisted, mentionable, rawPermissions));
      }

      List<TemplateChannel> channels = new ArrayList();

      for(int i = 0; i < channelsArray.length(); ++i) {
         DataObject obj = channelsArray.getObject(i);
         long channelId = obj.getLong("id");
         int type = obj.getInt("type");
         ChannelType channelType = ChannelType.fromId(type);
         String channelName = obj.getString("name");
         String topic = obj.getString("topic", (String)null);
         int rawPosition = obj.getInt("position");
         long parentId = obj.getLong("parent_id", -1L);
         boolean nsfw = obj.getBoolean("nsfw");
         int slowmode = obj.getInt("rate_limit_per_user");
         int bitrate = obj.getInt("bitrate");
         int userLimit = obj.getInt("user_limit");
         List<TemplateChannel.PermissionOverride> permissionOverrides = new ArrayList();
         DataArray overrides = obj.getArray("permission_overwrites");

         for(int j = 0; j < overrides.length(); ++j) {
            DataObject overrideObj = overrides.getObject(j);
            long overrideId = overrideObj.getLong("id");
            long allow = overrideObj.getLong("allow");
            long deny = overrideObj.getLong("deny");
            permissionOverrides.add(new TemplateChannel.PermissionOverride(overrideId, allow, deny));
         }

         channels.add(new TemplateChannel(channelId, channelType, channelName, topic, rawPosition, parentId, type == 5, permissionOverrides, nsfw, slowmode, bitrate, userLimit));
      }

      TemplateChannel afkChannel = (TemplateChannel)channels.stream().filter((templateChannel) -> templateChannel.getIdLong() == afkChannelId).findFirst().orElse((Object)null);
      TemplateChannel systemChannel = (TemplateChannel)channels.stream().filter((templateChannel) -> templateChannel.getIdLong() == systemChannelId).findFirst().orElse((Object)null);
      TemplateGuild guild = new TemplateGuild(guildId, guildName, guildDescription, guildIconId, guildVerificationLevel, notificationLevel, explicitContentLevel, locale, afkTimeout, afkChannel, systemChannel, roles, channels);
      boolean synced = !object.getBoolean("is_dirty", false);
      return new Template(this.getJDA(), code, name, description, uses, creator, createdAt, updatedAt, guild, synced);
   }

   public ApplicationInfo createApplicationInfo(DataObject object) {
      String description = object.getString("description");
      String termsOfServiceUrl = object.getString("terms_of_service_url", (String)null);
      String privacyPolicyUrl = object.getString("privacy_policy_url", (String)null);
      boolean doesBotRequireCodeGrant = object.getBoolean("bot_require_code_grant");
      String iconId = object.getString("icon", (String)null);
      long id = object.getUnsignedLong("id");
      long flags = object.getUnsignedLong("flags", 0L);
      String name = object.getString("name");
      boolean isBotPublic = object.getBoolean("bot_public");
      User owner = this.createUser(object.getObject("owner"));
      ApplicationTeam team = !object.isNull("team") ? this.createApplicationTeam(object.getObject("team")) : null;
      String customAuthUrl = object.getString("custom_install_url", (String)null);
      List<String> tags = (List)((DataArray)object.optArray("tags").orElseGet(DataArray::empty)).stream(DataArray::getString).collect(Collectors.toList());
      List<String> redirectUris = (List)((DataArray)object.optArray("redirect_uris").orElseGet(DataArray::empty)).stream(DataArray::getString).collect(Collectors.toList());
      String interactionsEndpointUrl = object.getString("interactions_endpoint_url", (String)null);
      String roleConnectionsVerificationUrl = object.getString("role_connections_verification_url", (String)null);
      Optional<DataObject> installParams = object.optObject("install_params");
      long defaultAuthUrlPerms = (Long)installParams.map((o) -> o.getLong("permissions")).orElse(0L);
      List<String> defaultAuthUrlScopes = (List)installParams.map((obj) -> (List)obj.getArray("scopes").stream(DataArray::getString).collect(Collectors.toList())).orElse(Collections.emptyList());
      Optional<DataObject> integrationTypesConfigDict = object.optObject("integration_types_config");
      Map<IntegrationType, ApplicationInfo.IntegrationTypeConfiguration> integrationTypesConfig = (Map)integrationTypesConfigDict.map((d) -> {
         Map<IntegrationType, ApplicationInfo.IntegrationTypeConfiguration> map = new EnumMap(IntegrationType.class);

         for(String key : d.keys()) {
            DataObject value = d.getObject(key);
            ApplicationInfo.InstallParameters installParameters = (ApplicationInfo.InstallParameters)value.optObject("oauth2_install_params").map((oauth2InstallParams) -> new ApplicationInfoImpl.InstallParametersImpl((List)oauth2InstallParams.getArray("scopes").stream(DataArray::getString).collect(Collectors.toList()), Permission.getPermissions(oauth2InstallParams.getLong("permissions")))).orElse((Object)null);
            map.put(IntegrationType.fromKey(key), new ApplicationInfoImpl.IntegrationTypeConfigurationImpl(installParameters));
         }

         return map;
      }).orElse(Collections.emptyMap());
      long approxUserInstallCount = object.getLong("approximate_user_install_count", -1L);
      return new ApplicationInfoImpl(this.getJDA(), description, doesBotRequireCodeGrant, iconId, id, flags, isBotPublic, name, termsOfServiceUrl, privacyPolicyUrl, owner, team, tags, redirectUris, interactionsEndpointUrl, roleConnectionsVerificationUrl, customAuthUrl, defaultAuthUrlPerms, defaultAuthUrlScopes, approxUserInstallCount, integrationTypesConfig);
   }

   public ApplicationTeam createApplicationTeam(DataObject object) {
      String iconId = object.getString("icon", (String)null);
      long id = object.getUnsignedLong("id");
      long ownerId = object.getUnsignedLong("owner_user_id", 0L);
      List<TeamMember> members = this.<TeamMember>map(object, "members", (o) -> {
         DataObject userJson = o.getObject("user");
         TeamMember.MembershipState state = TeamMember.MembershipState.fromKey(o.getInt("membership_state"));
         User user = this.createUser(userJson);
         TeamMember.RoleType roleType = user.getIdLong() == ownerId ? TeamMember.RoleType.OWNER : TeamMember.RoleType.fromKey(o.getString("role", ""));
         return new TeamMemberImpl(user, state, roleType, id);
      });
      return new ApplicationTeamImpl(iconId, members, id, ownerId);
   }

   public AuditLogEntry createAuditLogEntry(GuildImpl guild, DataObject entryJson, DataObject userJson, DataObject webhookJson) {
      long targetId = entryJson.getLong("target_id", 0L);
      long userId = entryJson.getLong("user_id", 0L);
      long id = entryJson.getLong("id");
      int typeKey = entryJson.getInt("action_type");
      DataArray changes = entryJson.isNull("changes") ? null : entryJson.getArray("changes");
      DataObject options = entryJson.isNull("options") ? null : entryJson.getObject("options");
      String reason = entryJson.getString("reason", (String)null);
      UserImpl user = userJson == null ? null : this.createUser(userJson);
      WebhookImpl webhook = webhookJson == null ? null : this.createWebhook(webhookJson);
      ActionType type = ActionType.from(typeKey);
      Set<AuditLogChange> changesList;
      if (changes != null) {
         changesList = new HashSet(changes.length());

         for(int i = 0; i < changes.length(); ++i) {
            DataObject object = changes.getObject(i);
            AuditLogChange change = this.createAuditLogChange(object);
            changesList.add(change);
         }
      } else {
         changesList = Collections.emptySet();
      }

      CaseInsensitiveMap<String, AuditLogChange> changeMap = new CaseInsensitiveMap<String, AuditLogChange>(this.changeToMap(changesList));
      CaseInsensitiveMap<String, Object> optionMap = options != null ? new CaseInsensitiveMap(options.toMap()) : null;
      return new AuditLogEntry(type, typeKey, id, userId, targetId, guild, user, webhook, reason, changeMap, optionMap);
   }

   public AuditLogChange createAuditLogChange(DataObject change) {
      String key = change.getString("key");
      Object oldValue = change.isNull("old_value") ? null : change.get("old_value");
      Object newValue = change.isNull("new_value") ? null : change.get("new_value");
      return new AuditLogChange(oldValue, newValue, key);
   }

   public Entitlement createEntitlement(DataObject object) {
      return new EntitlementImpl(this.getJDA(), object.getUnsignedLong("id"), object.getUnsignedLong("sku_id"), object.getUnsignedLong("application_id"), object.getUnsignedLong("user_id", 0L), object.getUnsignedLong("guild_id", 0L), Entitlement.EntitlementType.fromKey(object.getInt("type")), object.getBoolean("deleted"), object.getOffsetDateTime("starts_at", (OffsetDateTime)null), object.getOffsetDateTime("ends_at", (OffsetDateTime)null), object.getBoolean("consumed", false));
   }

   private Map<String, AuditLogChange> changeToMap(Set<AuditLogChange> changesList) {
      return (Map)changesList.stream().collect(Collectors.toMap(AuditLogChange::getKey, UnaryOperator.identity()));
   }

   private <T> List<T> map(DataObject jsonObject, String key, Function<DataObject, T> convert) {
      if (jsonObject.isNull(key)) {
         return Collections.emptyList();
      } else {
         DataArray arr = jsonObject.getArray(key);
         List<T> mappedObjects = new ArrayList(arr.length());

         for(int i = 0; i < arr.length(); ++i) {
            DataObject obj = arr.getObject(i);

            try {
               T result = (T)convert.apply(obj);
               if (result != null) {
                  mappedObjects.add(result);
               }
            } catch (Exception e) {
               LOG.error("Failed to parse element in {} with content {}", key, obj, e);
            }
         }

         return mappedObjects;
      }
   }

   static {
      Set<String> tmp = new HashSet();
      tmp.add("application_id");
      tmp.add("assets");
      tmp.add("details");
      tmp.add("flags");
      tmp.add("party");
      tmp.add("session_id");
      tmp.add("sync_id");
      richGameFields = Collections.unmodifiableSet(tmp);
   }
}
