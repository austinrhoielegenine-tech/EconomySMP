package net.dv8tion.jda.api.entities;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.annotations.Incubating;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.automod.AutoModRule;
import net.dv8tion.jda.api.entities.automod.build.AutoModRuleData;
import net.dv8tion.jda.api.entities.channel.attribute.ICopyableChannel;
import net.dv8tion.jda.api.entities.channel.attribute.IGuildChannelContainer;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.MediaChannel;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.entities.detached.IDetachableEntity;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.guild.SecurityIncidentActions;
import net.dv8tion.jda.api.entities.guild.SecurityIncidentDetections;
import net.dv8tion.jda.api.entities.guild.SystemChannelFlag;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.api.entities.templates.Template;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.PrivilegeConfig;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.IntegrationPrivilege;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.managers.AutoModRuleManager;
import net.dv8tion.jda.api.managers.GuildManager;
import net.dv8tion.jda.api.managers.GuildStickerManager;
import net.dv8tion.jda.api.managers.GuildWelcomeScreenManager;
import net.dv8tion.jda.api.managers.SoundboardSoundManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import net.dv8tion.jda.api.requests.restaction.CommandEditAction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.requests.restaction.MemberAction;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import net.dv8tion.jda.api.requests.restaction.ScheduledEventAction;
import net.dv8tion.jda.api.requests.restaction.SoundboardSoundCreateAction;
import net.dv8tion.jda.api.requests.restaction.order.CategoryOrderAction;
import net.dv8tion.jda.api.requests.restaction.order.ChannelOrderAction;
import net.dv8tion.jda.api.requests.restaction.order.RoleOrderAction;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.BanPaginationAction;
import net.dv8tion.jda.api.utils.DiscordAssets;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.ImageFormat;
import net.dv8tion.jda.api.utils.ImageProxy;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.cache.MemberCacheView;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import net.dv8tion.jda.api.utils.cache.SortedChannelCacheView;
import net.dv8tion.jda.api.utils.cache.SortedSnowflakeCacheView;
import net.dv8tion.jda.api.utils.concurrent.Task;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.dv8tion.jda.internal.requests.DeferredRestAction;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;
import net.dv8tion.jda.internal.utils.concurrent.task.GatewayTask;
import org.jetbrains.annotations.Unmodifiable;

public interface Guild extends IGuildChannelContainer<GuildChannel>, ISnowflake, IDetachableEntity {
   /** @deprecated */
   @Deprecated
   String ICON_URL = "https://cdn.discordapp.com/icons/%s/%s.%s";
   /** @deprecated */
   @Deprecated
   String SPLASH_URL = "https://cdn.discordapp.com/splashes/%s/%s.png";
   /** @deprecated */
   @Deprecated
   String BANNER_URL = "https://cdn.discordapp.com/banners/%s/%s.%s";

   @Nonnull
   @CheckReturnValue
   default RestAction<List<Command>> retrieveCommands() {
      return this.retrieveCommands(false);
   }

   @Nonnull
   @CheckReturnValue
   RestAction<List<Command>> retrieveCommands(boolean var1);

   @Nonnull
   @CheckReturnValue
   RestAction<Command> retrieveCommandById(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   default RestAction<Command> retrieveCommandById(long id) {
      return this.retrieveCommandById(Long.toUnsignedString(id));
   }

   @Nonnull
   @CheckReturnValue
   RestAction<Command> upsertCommand(@Nonnull CommandData var1);

   @Nonnull
   @CheckReturnValue
   default CommandCreateAction upsertCommand(@Nonnull String name, @Nonnull String description) {
      return (CommandCreateAction)this.upsertCommand(new CommandDataImpl(name, description));
   }

   @Nonnull
   @CheckReturnValue
   CommandListUpdateAction updateCommands();

   @Nonnull
   @CheckReturnValue
   CommandEditAction editCommandById(@Nonnull Command.Type var1, @Nonnull String var2);

   @Nonnull
   @CheckReturnValue
   default CommandEditAction editCommandById(@Nonnull Command.Type type, long id) {
      return this.editCommandById(type, Long.toUnsignedString(id));
   }

   @Nonnull
   @CheckReturnValue
   RestAction<Void> deleteCommandById(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> deleteCommandById(long commandId) {
      return this.deleteCommandById(Long.toUnsignedString(commandId));
   }

   @Nonnull
   @CheckReturnValue
   RestAction<List<IntegrationPrivilege>> retrieveIntegrationPrivilegesById(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   default RestAction<List<IntegrationPrivilege>> retrieveIntegrationPrivilegesById(long targetId) {
      return this.retrieveIntegrationPrivilegesById(Long.toUnsignedString(targetId));
   }

   @Nonnull
   @CheckReturnValue
   RestAction<PrivilegeConfig> retrieveCommandPrivileges();

   @Nonnull
   @CheckReturnValue
   default RestAction<EnumSet<Region>> retrieveRegions() {
      return this.retrieveRegions(true);
   }

   @Nonnull
   @CheckReturnValue
   RestAction<EnumSet<Region>> retrieveRegions(boolean var1);

   @Nonnull
   @CheckReturnValue
   RestAction<@Unmodifiable List<AutoModRule>> retrieveAutoModRules();

   @Nonnull
   @CheckReturnValue
   RestAction<AutoModRule> retrieveAutoModRuleById(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   default RestAction<AutoModRule> retrieveAutoModRuleById(long id) {
      return this.retrieveAutoModRuleById(Long.toUnsignedString(id));
   }

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<AutoModRule> createAutoModRule(@Nonnull AutoModRuleData var1);

   @Nonnull
   @CheckReturnValue
   AutoModRuleManager modifyAutoModRuleById(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   default AutoModRuleManager modifyAutoModRuleById(long id) {
      return this.modifyAutoModRuleById(Long.toUnsignedString(id));
   }

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> deleteAutoModRuleById(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> deleteAutoModRuleById(long id) {
      return this.deleteAutoModRuleById(Long.toUnsignedString(id));
   }

   @Nonnull
   @CheckReturnValue
   MemberAction addMember(@Nonnull String var1, @Nonnull UserSnowflake var2);

   boolean isLoaded();

   void pruneMemberCache();

   boolean unloadMember(long var1);

   int getMemberCount();

   @Nonnull
   String getName();

   @Nullable
   String getIconId();

   @Nullable
   default String getIconUrl() {
      String iconId = this.getIconId();
      return iconId == null ? null : this.getIconUrl(iconId.startsWith("a_") ? ImageFormat.ANIMATED_WEBP : ImageFormat.PNG);
   }

   @Nullable
   default String getIconUrl(@Nonnull ImageFormat format) {
      ImageProxy icon = this.getIcon(format);
      return icon == null ? null : icon.getUrl();
   }

   @Nullable
   default ImageProxy getIcon() {
      String iconUrl = this.getIconUrl();
      return iconUrl == null ? null : new ImageProxy(iconUrl);
   }

   @Nullable
   default ImageProxy getIcon(@Nonnull ImageFormat format) {
      String iconId = this.getIconId();
      return iconId == null ? null : DiscordAssets.guildIcon(format, this.getId(), iconId);
   }

   @Nonnull
   @Unmodifiable Set<String> getFeatures();

   default boolean isInvitesDisabled() {
      return this.getFeatures().contains("INVITES_DISABLED");
   }

   @Nullable
   String getSplashId();

   @Nullable
   default String getSplashUrl() {
      String splashId = this.getSplashId();
      return splashId == null ? null : this.getSplashUrl(ImageFormat.PNG);
   }

   @Nullable
   default String getSplashUrl(@Nonnull ImageFormat format) {
      ImageProxy splash = this.getSplash(format);
      return splash == null ? null : splash.getUrl();
   }

   @Nullable
   default ImageProxy getSplash() {
      String splashUrl = this.getSplashUrl();
      return splashUrl == null ? null : new ImageProxy(splashUrl);
   }

   @Nullable
   default ImageProxy getSplash(@Nonnull ImageFormat format) {
      return DiscordAssets.guildSplash(format, this.getId(), this.getSplashId());
   }

   @Nullable
   String getVanityCode();

   @Nullable
   default String getVanityUrl() {
      return this.getVanityCode() == null ? null : "https://discord.gg/" + this.getVanityCode();
   }

   @Nonnull
   @CheckReturnValue
   RestAction<VanityInvite> retrieveVanityInvite();

   @Nullable
   String getDescription();

   @Nonnull
   DiscordLocale getLocale();

   @Nullable
   String getBannerId();

   @Nullable
   default String getBannerUrl() {
      String bannerId = this.getBannerId();
      return bannerId == null ? null : this.getBannerUrl(bannerId.startsWith("a_") ? ImageFormat.ANIMATED_WEBP : ImageFormat.PNG);
   }

   @Nullable
   default String getBannerUrl(@Nonnull ImageFormat format) {
      ImageProxy banner = this.getBanner(format);
      return banner == null ? null : banner.getUrl();
   }

   @Nullable
   default ImageProxy getBanner() {
      String bannerUrl = this.getBannerUrl();
      return bannerUrl == null ? null : new ImageProxy(bannerUrl);
   }

   @Nullable
   default ImageProxy getBanner(@Nonnull ImageFormat format) {
      return DiscordAssets.guildBanner(format, this.getId(), this.getBannerId());
   }

   @Nonnull
   BoostTier getBoostTier();

   int getBoostCount();

   @Nonnull
   @Unmodifiable List<Member> getBoosters();

   default int getMaxBitrate() {
      int maxBitrate = this.getFeatures().contains("VIP_REGIONS") ? 384000 : 96000;
      return Math.max(maxBitrate, this.getBoostTier().getMaxBitrate());
   }

   default long getMaxFileSize() {
      return this.getBoostTier().getMaxFileSize();
   }

   default int getMaxEmojis() {
      int max = this.getFeatures().contains("MORE_EMOJI") ? 200 : 50;
      return Math.max(max, this.getBoostTier().getMaxEmojis());
   }

   int getMaxMembers();

   int getMaxPresences();

   @Nonnull
   @CheckReturnValue
   RestAction<MetaData> retrieveMetaData();

   @Nullable
   VoiceChannel getAfkChannel();

   @Nullable
   TextChannel getSystemChannel();

   @Nullable
   TextChannel getRulesChannel();

   @Nullable
   TextChannel getCommunityUpdatesChannel();

   @Nullable
   TextChannel getSafetyAlertsChannel();

   @Nullable
   Member getOwner();

   long getOwnerIdLong();

   @Nonnull
   default String getOwnerId() {
      return Long.toUnsignedString(this.getOwnerIdLong());
   }

   @Nonnull
   Timeout getAfkTimeout();

   @Nonnull
   SecurityIncidentActions getSecurityIncidentActions();

   @Nonnull
   SecurityIncidentDetections getSecurityIncidentDetections();

   boolean isMember(@Nonnull UserSnowflake var1);

   @Nonnull
   SelfMember getSelfMember();

   @Nonnull
   NSFWLevel getNSFWLevel();

   @Nonnull
   Set<SystemChannelFlag> getSystemChannelFlags();

   int getSystemChannelFlagsRaw();

   @Nullable
   Member getMember(@Nonnull UserSnowflake var1);

   @Nullable
   default Member getMemberById(@Nonnull String userId) {
      return this.getMemberCache().getElementById(userId);
   }

   @Nullable
   default Member getMemberById(long userId) {
      return this.getMemberCache().getElementById(userId);
   }

   @Nullable
   default Member getMemberByTag(@Nonnull String tag) {
      User user = this.getJDA().getUserByTag(tag);
      return user == null ? null : this.getMember(user);
   }

   @Nullable
   default Member getMemberByTag(@Nonnull String username, @Nonnull String discriminator) {
      User user = this.getJDA().getUserByTag(username, discriminator);
      return user == null ? null : this.getMember(user);
   }

   @Nonnull
   default @Unmodifiable List<Member> getMembers() {
      return this.getMemberCache().asList();
   }

   @Nonnull
   @Incubating
   default @Unmodifiable List<Member> getMembersByName(@Nonnull String name, boolean ignoreCase) {
      return this.getMemberCache().getElementsByUsername(name, ignoreCase);
   }

   @Nonnull
   default @Unmodifiable List<Member> getMembersByNickname(@Nullable String nickname, boolean ignoreCase) {
      return this.getMemberCache().getElementsByNickname(nickname, ignoreCase);
   }

   @Nonnull
   default @Unmodifiable List<Member> getMembersByEffectiveName(@Nonnull String name, boolean ignoreCase) {
      return this.getMemberCache().getElementsByName(name, ignoreCase);
   }

   @Nonnull
   default @Unmodifiable List<Member> getMembersWithRoles(@Nonnull Role... roles) {
      Checks.notNull(roles, "Roles");
      return this.getMembersWithRoles((Collection)Arrays.asList(roles));
   }

   @Nonnull
   default @Unmodifiable List<Member> getMembersWithRoles(@Nonnull Collection<Role> roles) {
      Checks.noneNull(roles, "Roles");

      for(Role role : roles) {
         Checks.check(this.equals(role.getGuild()), "All roles must be from the same guild!");
      }

      return this.getMemberCache().getElementsWithRoles(roles);
   }

   @Nonnull
   MemberCacheView getMemberCache();

   @Nonnull
   SortedSnowflakeCacheView<ScheduledEvent> getScheduledEventCache();

   @Nonnull
   @CheckReturnValue
   default RestAction<List<ScheduledEvent>> retrieveScheduledEvents() {
      return this.retrieveScheduledEvents(false);
   }

   @Nonnull
   @CheckReturnValue
   RestAction<List<ScheduledEvent>> retrieveScheduledEvents(boolean var1);

   @Nonnull
   default @Unmodifiable List<ScheduledEvent> getScheduledEventsByName(@Nonnull String name, boolean ignoreCase) {
      return this.getScheduledEventCache().getElementsByName(name, ignoreCase);
   }

   @Nullable
   default ScheduledEvent getScheduledEventById(@Nonnull String id) {
      return (ScheduledEvent)this.getScheduledEventCache().getElementById(id);
   }

   @Nullable
   default ScheduledEvent getScheduledEventById(long id) {
      return (ScheduledEvent)this.getScheduledEventCache().getElementById(id);
   }

   @Nonnull
   default @Unmodifiable List<ScheduledEvent> getScheduledEvents() {
      return this.getScheduledEventCache().asList();
   }

   @Nonnull
   SortedSnowflakeCacheView<StageChannel> getStageChannelCache();

   @Nonnull
   SortedSnowflakeCacheView<ThreadChannel> getThreadChannelCache();

   @Nonnull
   SortedSnowflakeCacheView<Category> getCategoryCache();

   @Nonnull
   SortedSnowflakeCacheView<TextChannel> getTextChannelCache();

   @Nonnull
   SortedSnowflakeCacheView<NewsChannel> getNewsChannelCache();

   @Nonnull
   SortedSnowflakeCacheView<VoiceChannel> getVoiceChannelCache();

   @Nonnull
   SortedSnowflakeCacheView<ForumChannel> getForumChannelCache();

   @Nonnull
   SortedChannelCacheView<GuildChannel> getChannelCache();

   @Nonnull
   default @Unmodifiable List<GuildChannel> getChannels() {
      return this.getChannels(true);
   }

   @Nonnull
   @Unmodifiable List<GuildChannel> getChannels(boolean var1);

   @Nullable
   default Role getRoleById(@Nonnull String id) {
      return (Role)this.getRoleCache().getElementById(id);
   }

   @Nullable
   default Role getRoleById(long id) {
      return (Role)this.getRoleCache().getElementById(id);
   }

   @Nonnull
   default @Unmodifiable List<Role> getRoles() {
      return this.getRoleCache().asList();
   }

   @Nonnull
   default @Unmodifiable List<Role> getRolesByName(@Nonnull String name, boolean ignoreCase) {
      return this.getRoleCache().getElementsByName(name, ignoreCase);
   }

   @Nullable
   default Role getRoleByBot(long userId) {
      return (Role)this.getRoleCache().applyStream((stream) -> (Role)stream.filter((role) -> role.getTags().getBotIdLong() == userId).findFirst().orElse((Object)null));
   }

   @Nullable
   default Role getRoleByBot(@Nonnull String userId) {
      return this.getRoleByBot(MiscUtil.parseSnowflake(userId));
   }

   @Nullable
   default Role getRoleByBot(@Nonnull User user) {
      Checks.notNull(user, "User");
      return this.getRoleByBot(user.getIdLong());
   }

   @Nullable
   default Role getBotRole() {
      return this.getRoleByBot((User)this.getJDA().getSelfUser());
   }

   @Nullable
   default Role getBoostRole() {
      return (Role)this.getRoleCache().applyStream((stream) -> (Role)stream.filter((role) -> role.getTags().isBoost()).findFirst().orElse((Object)null));
   }

   @Nonnull
   SortedSnowflakeCacheView<Role> getRoleCache();

   @Nullable
   default RichCustomEmoji getEmojiById(@Nonnull String id) {
      return (RichCustomEmoji)this.getEmojiCache().getElementById(id);
   }

   @Nullable
   default RichCustomEmoji getEmojiById(long id) {
      return (RichCustomEmoji)this.getEmojiCache().getElementById(id);
   }

   @Nonnull
   default @Unmodifiable List<RichCustomEmoji> getEmojis() {
      return this.getEmojiCache().asList();
   }

   @Nonnull
   default @Unmodifiable List<RichCustomEmoji> getEmojisByName(@Nonnull String name, boolean ignoreCase) {
      return this.getEmojiCache().getElementsByName(name, ignoreCase);
   }

   @Nonnull
   SnowflakeCacheView<RichCustomEmoji> getEmojiCache();

   @Nullable
   default GuildSticker getStickerById(@Nonnull String id) {
      return (GuildSticker)this.getStickerCache().getElementById(id);
   }

   @Nullable
   default GuildSticker getStickerById(long id) {
      return (GuildSticker)this.getStickerCache().getElementById(id);
   }

   @Nonnull
   default @Unmodifiable List<GuildSticker> getStickers() {
      return this.getStickerCache().asList();
   }

   @Nonnull
   default @Unmodifiable List<GuildSticker> getStickersByName(@Nonnull String name, boolean ignoreCase) {
      return this.getStickerCache().getElementsByName(name, ignoreCase);
   }

   @Nonnull
   SnowflakeCacheView<GuildSticker> getStickerCache();

   @Nullable
   default SoundboardSound getSoundboardSoundById(@Nonnull String id) {
      return (SoundboardSound)this.getSoundboardSoundCache().getElementById(id);
   }

   @Nullable
   default SoundboardSound getSoundboardSoundById(long id) {
      return (SoundboardSound)this.getSoundboardSoundCache().getElementById(id);
   }

   @Nonnull
   default @Unmodifiable List<SoundboardSound> getSoundboardSounds() {
      return this.getSoundboardSoundCache().asList();
   }

   @Nonnull
   default @Unmodifiable List<SoundboardSound> getSoundboardSoundsByName(@Nonnull String name, boolean ignoreCase) {
      return this.getSoundboardSoundCache().getElementsByName(name, ignoreCase);
   }

   @Nonnull
   SnowflakeCacheView<SoundboardSound> getSoundboardSoundCache();

   @Nonnull
   @CheckReturnValue
   RestAction<@Unmodifiable List<RichCustomEmoji>> retrieveEmojis();

   @Nonnull
   @CheckReturnValue
   RestAction<RichCustomEmoji> retrieveEmojiById(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   default RestAction<RichCustomEmoji> retrieveEmojiById(long id) {
      return this.retrieveEmojiById(Long.toUnsignedString(id));
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<RichCustomEmoji> retrieveEmoji(@Nonnull CustomEmoji emoji) {
      Checks.notNull(emoji, "Emoji");
      if (emoji instanceof RichCustomEmoji && ((RichCustomEmoji)emoji).getGuild() != null) {
         Checks.check(((RichCustomEmoji)emoji).getGuild().equals(this), "Emoji must be from the same Guild!");
      }

      JDA jda = this.getJDA();
      return new DeferredRestAction(jda, RichCustomEmoji.class, () -> {
         if (emoji instanceof RichCustomEmoji) {
            RichCustomEmoji richEmoji = (RichCustomEmoji)emoji;
            if (richEmoji.getOwner() != null || !this.getSelfMember().hasPermission(new Permission[]{Permission.MANAGE_GUILD_EXPRESSIONS})) {
               return richEmoji;
            }
         }

         return null;
      }, () -> this.retrieveEmojiById(emoji.getId()));
   }

   @Nonnull
   @CheckReturnValue
   RestAction<@Unmodifiable List<GuildSticker>> retrieveStickers();

   @Nonnull
   @CheckReturnValue
   RestAction<GuildSticker> retrieveSticker(@Nonnull StickerSnowflake var1);

   @Nonnull
   @CheckReturnValue
   GuildStickerManager editSticker(@Nonnull StickerSnowflake var1);

   @Nonnull
   @CheckReturnValue
   CacheRestAction<List<SoundboardSound>> retrieveSoundboardSounds();

   @Nonnull
   @CheckReturnValue
   CacheRestAction<SoundboardSound> retrieveSoundboardSound(@Nonnull SoundboardSoundSnowflake var1);

   @Nonnull
   @CheckReturnValue
   SoundboardSoundManager editSoundboardSound(@Nonnull SoundboardSoundSnowflake var1);

   @Nonnull
   @CheckReturnValue
   BanPaginationAction retrieveBanList();

   @Nonnull
   @CheckReturnValue
   RestAction<Ban> retrieveBan(@Nonnull UserSnowflake var1);

   @Nonnull
   @CheckReturnValue
   RestAction<Integer> retrievePrunableMemberCount(int var1);

   @Nonnull
   Role getPublicRole();

   @Nullable
   DefaultGuildChannelUnion getDefaultChannel();

   @Nonnull
   @CheckReturnValue
   GuildManager getManager();

   boolean isBoostProgressBarEnabled();

   @Nonnull
   @CheckReturnValue
   AuditLogPaginationAction retrieveAuditLogs();

   @Nonnull
   @CheckReturnValue
   RestAction<Void> leave();

   @Nonnull
   AudioManager getAudioManager();

   @Nonnull
   Task<Void> requestToSpeak();

   @Nonnull
   Task<Void> cancelRequestToSpeak();

   @Nonnull
   JDA getJDA();

   @Nonnull
   @CheckReturnValue
   RestAction<@Unmodifiable List<Invite>> retrieveInvites();

   @Nonnull
   @CheckReturnValue
   RestAction<@Unmodifiable List<Template>> retrieveTemplates();

   @Nonnull
   @CheckReturnValue
   RestAction<Template> createTemplate(@Nonnull String var1, @Nullable String var2);

   @Nonnull
   @CheckReturnValue
   RestAction<@Unmodifiable List<Webhook>> retrieveWebhooks();

   @Nonnull
   @CheckReturnValue
   RestAction<GuildWelcomeScreen> retrieveWelcomeScreen();

   @Nonnull
   @Unmodifiable List<GuildVoiceState> getVoiceStates();

   @Nonnull
   @CheckReturnValue
   CacheRestAction<GuildVoiceState> retrieveMemberVoiceStateById(long var1);

   @Nonnull
   @CheckReturnValue
   default RestAction<GuildVoiceState> retrieveMemberVoiceStateById(@Nonnull String id) {
      return this.retrieveMemberVoiceStateById(MiscUtil.parseSnowflake(id));
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<GuildVoiceState> retrieveMemberVoiceState(@Nonnull UserSnowflake user) {
      Checks.notNull(user, "User");
      return this.retrieveMemberVoiceStateById(user.getIdLong());
   }

   @Nonnull
   VerificationLevel getVerificationLevel();

   @Nonnull
   NotificationLevel getDefaultNotificationLevel();

   @Nonnull
   MFALevel getRequiredMFALevel();

   @Nonnull
   ExplicitContentLevel getExplicitContentLevel();

   @Nonnull
   @CheckReturnValue
   default Task<List<Member>> loadMembers() {
      return this.findMembers((m) -> true);
   }

   @Nonnull
   @CheckReturnValue
   default Task<List<Member>> findMembers(@Nonnull Predicate<? super Member> filter) {
      Checks.notNull(filter, "Filter");
      List<Member> list = new ArrayList();
      CompletableFuture<List<Member>> future = new CompletableFuture();
      Task<Void> reference = this.loadMembers((member) -> {
         if (filter.test(member)) {
            list.add(member);
         }

      });
      Objects.requireNonNull(reference);
      GatewayTask<List<Member>> task = (new GatewayTask<List<Member>>(future, reference::cancel)).onSetTimeout((timeout) -> reference.setTimeout(Duration.ofMillis(timeout)));
      Task var10000 = reference.onSuccess((it) -> future.complete(list));
      Objects.requireNonNull(future);
      var10000.onError(future::completeExceptionally);
      return task;
   }

   @Nonnull
   @CheckReturnValue
   default Task<List<Member>> findMembersWithRoles(@Nonnull Collection<Role> roles) {
      Checks.noneNull(roles, "Roles");

      for(Role role : roles) {
         Checks.check(this.equals(role.getGuild()), "All roles must be from the same guild!");
      }

      if (this.isLoaded()) {
         CompletableFuture<List<Member>> future = CompletableFuture.completedFuture(this.getMembersWithRoles(roles));
         return new GatewayTask<List<Member>>(future, () -> {
         });
      } else {
         List<Role> rolesWithoutPublicRole = (List)roles.stream().filter((rolex) -> !rolex.isPublicRole()).collect(Collectors.toList());
         return rolesWithoutPublicRole.isEmpty() ? this.loadMembers() : this.findMembers((member) -> member.getUnsortedRoles().containsAll(rolesWithoutPublicRole));
      }
   }

   @Nonnull
   @CheckReturnValue
   default Task<List<Member>> findMembersWithRoles(@Nonnull Role... roles) {
      Checks.noneNull(roles, "Roles");
      return this.findMembersWithRoles(Arrays.asList(roles));
   }

   @Nonnull
   Task<Void> loadMembers(@Nonnull Consumer<Member> var1);

   @Nonnull
   @CheckReturnValue
   default CacheRestAction<Member> retrieveMember(@Nonnull UserSnowflake user) {
      Checks.notNull(user, "User");
      return this.retrieveMemberById(user.getId());
   }

   @Nonnull
   @CheckReturnValue
   default CacheRestAction<Member> retrieveOwner() {
      return this.retrieveMemberById(this.getOwnerIdLong());
   }

   @Nonnull
   @CheckReturnValue
   default CacheRestAction<Member> retrieveMemberById(@Nonnull String id) {
      return this.retrieveMemberById(MiscUtil.parseSnowflake(id));
   }

   @Nonnull
   @CheckReturnValue
   CacheRestAction<Member> retrieveMemberById(long var1);

   @Nonnull
   @CheckReturnValue
   default Task<List<Member>> retrieveMembers(@Nonnull Collection<? extends UserSnowflake> users) {
      Checks.noneNull(users, "Users");
      if (users.isEmpty()) {
         return new GatewayTask<List<Member>>(CompletableFuture.completedFuture(Collections.emptyList()), () -> {
         });
      } else {
         long[] ids = users.stream().mapToLong(ISnowflake::getIdLong).toArray();
         return this.retrieveMembersByIds(ids);
      }
   }

   @Nonnull
   @CheckReturnValue
   default Task<List<Member>> retrieveMembersByIds(@Nonnull Collection<Long> ids) {
      Checks.noneNull(ids, "IDs");
      if (ids.isEmpty()) {
         return new GatewayTask<List<Member>>(CompletableFuture.completedFuture(Collections.emptyList()), () -> {
         });
      } else {
         long[] arr = ids.stream().mapToLong(Long::longValue).toArray();
         return this.retrieveMembersByIds(arr);
      }
   }

   @Nonnull
   @CheckReturnValue
   default Task<List<Member>> retrieveMembersByIds(@Nonnull String... ids) {
      Checks.notNull(ids, "Array");
      if (ids.length == 0) {
         return new GatewayTask<List<Member>>(CompletableFuture.completedFuture(Collections.emptyList()), () -> {
         });
      } else {
         long[] arr = new long[ids.length];

         for(int i = 0; i < ids.length; ++i) {
            arr[i] = MiscUtil.parseSnowflake(ids[i]);
         }

         return this.retrieveMembersByIds(arr);
      }
   }

   @Nonnull
   @CheckReturnValue
   default Task<List<Member>> retrieveMembersByIds(@Nonnull long... ids) {
      boolean presence = this.getJDA().getGatewayIntents().contains(GatewayIntent.GUILD_PRESENCES);
      return this.retrieveMembersByIds(presence, ids);
   }

   @Nonnull
   @CheckReturnValue
   default Task<List<Member>> retrieveMembers(boolean includePresence, @Nonnull Collection<? extends UserSnowflake> users) {
      Checks.noneNull(users, "Users");
      if (users.isEmpty()) {
         return new GatewayTask<List<Member>>(CompletableFuture.completedFuture(Collections.emptyList()), () -> {
         });
      } else {
         long[] ids = users.stream().mapToLong(ISnowflake::getIdLong).toArray();
         return this.retrieveMembersByIds(includePresence, ids);
      }
   }

   @Nonnull
   @CheckReturnValue
   default Task<List<Member>> retrieveMembersByIds(boolean includePresence, @Nonnull Collection<Long> ids) {
      Checks.noneNull(ids, "IDs");
      if (ids.isEmpty()) {
         return new GatewayTask<List<Member>>(CompletableFuture.completedFuture(Collections.emptyList()), () -> {
         });
      } else {
         long[] arr = ids.stream().mapToLong(Long::longValue).toArray();
         return this.retrieveMembersByIds(includePresence, arr);
      }
   }

   @Nonnull
   @CheckReturnValue
   default Task<List<Member>> retrieveMembersByIds(boolean includePresence, @Nonnull String... ids) {
      Checks.notNull(ids, "Array");
      if (ids.length == 0) {
         return new GatewayTask<List<Member>>(CompletableFuture.completedFuture(Collections.emptyList()), () -> {
         });
      } else {
         long[] arr = new long[ids.length];

         for(int i = 0; i < ids.length; ++i) {
            arr[i] = MiscUtil.parseSnowflake(ids[i]);
         }

         return this.retrieveMembersByIds(includePresence, arr);
      }
   }

   @Nonnull
   @CheckReturnValue
   Task<List<Member>> retrieveMembersByIds(boolean var1, @Nonnull long... var2);

   @Nonnull
   @CheckReturnValue
   Task<List<Member>> retrieveMembersByPrefix(@Nonnull String var1, int var2);

   @Nonnull
   @CheckReturnValue
   RestAction<@Unmodifiable List<ThreadChannel>> retrieveActiveThreads();

   @Nonnull
   @CheckReturnValue
   default CacheRestAction<ScheduledEvent> retrieveScheduledEventById(long id) {
      return this.retrieveScheduledEventById(Long.toUnsignedString(id));
   }

   @Nonnull
   @CheckReturnValue
   CacheRestAction<ScheduledEvent> retrieveScheduledEventById(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   RestAction<Void> moveVoiceMember(@Nonnull UserSnowflake var1, @Nullable AudioChannel var2);

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> kickVoiceMember(@Nonnull UserSnowflake user) {
      return this.moveVoiceMember(user, (AudioChannel)null);
   }

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> modifyNickname(@Nonnull Member var1, @Nullable String var2);

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Integer> prune(int days, @Nonnull Role... roles) {
      return this.prune(days, true, roles);
   }

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Integer> prune(int var1, boolean var2, @Nonnull Role... var3);

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> modifySecurityIncidents(@Nonnull SecurityIncidentActions var1);

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> kick(@Nonnull UserSnowflake var1);

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> ban(@Nonnull UserSnowflake var1, int var2, @Nonnull TimeUnit var3);

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<BulkBanResponse> ban(@Nonnull Collection<? extends UserSnowflake> var1, @Nullable Duration var2);

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<BulkBanResponse> ban(@Nonnull Collection<? extends UserSnowflake> users, int deletionTimeframe, @Nonnull TimeUnit unit) {
      Checks.notNull(unit, "TimeUnit");
      return this.ban(users, Duration.ofSeconds(unit.toSeconds((long)deletionTimeframe)));
   }

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> unban(@Nonnull UserSnowflake var1);

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> timeoutFor(@Nonnull UserSnowflake user, long amount, @Nonnull TimeUnit unit) {
      Checks.check(amount >= 1L, "The amount must be more than 0");
      Checks.notNull(unit, "TimeUnit");
      return this.timeoutUntil(user, Helpers.toOffset(System.currentTimeMillis() + unit.toMillis(amount)));
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> timeoutFor(@Nonnull UserSnowflake user, @Nonnull Duration duration) {
      Checks.notNull(duration, "Duration");
      return this.timeoutUntil(user, Helpers.toOffset(System.currentTimeMillis() + duration.toMillis()));
   }

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> timeoutUntil(@Nonnull UserSnowflake var1, @Nonnull TemporalAccessor var2);

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> removeTimeout(@Nonnull UserSnowflake var1);

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> deafen(@Nonnull UserSnowflake var1, boolean var2);

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> mute(@Nonnull UserSnowflake var1, boolean var2);

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> addRoleToMember(@Nonnull UserSnowflake var1, @Nonnull Role var2);

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> removeRoleFromMember(@Nonnull UserSnowflake var1, @Nonnull Role var2);

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> modifyMemberRoles(@Nonnull Member var1, @Nullable Collection<Role> var2, @Nullable Collection<Role> var3);

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Void> modifyMemberRoles(@Nonnull Member member, @Nonnull Role... roles) {
      return this.modifyMemberRoles(member, (Collection)Arrays.asList(roles));
   }

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> modifyMemberRoles(@Nonnull Member var1, @Nonnull Collection<Role> var2);

   @Nonnull
   @CheckReturnValue
   RestAction<RoleMemberCounts> retrieveRoleMemberCounts();

   @Nonnull
   @CheckReturnValue
   default ChannelAction<TextChannel> createTextChannel(@Nonnull String name) {
      return this.createTextChannel(name, (Category)null);
   }

   @Nonnull
   @CheckReturnValue
   ChannelAction<TextChannel> createTextChannel(@Nonnull String var1, @Nullable Category var2);

   @Nonnull
   @CheckReturnValue
   default ChannelAction<NewsChannel> createNewsChannel(@Nonnull String name) {
      return this.createNewsChannel(name, (Category)null);
   }

   @Nonnull
   @CheckReturnValue
   ChannelAction<NewsChannel> createNewsChannel(@Nonnull String var1, @Nullable Category var2);

   @Nonnull
   @CheckReturnValue
   default ChannelAction<VoiceChannel> createVoiceChannel(@Nonnull String name) {
      return this.createVoiceChannel(name, (Category)null);
   }

   @Nonnull
   @CheckReturnValue
   ChannelAction<VoiceChannel> createVoiceChannel(@Nonnull String var1, @Nullable Category var2);

   @Nonnull
   @CheckReturnValue
   default ChannelAction<StageChannel> createStageChannel(@Nonnull String name) {
      return this.createStageChannel(name, (Category)null);
   }

   @Nonnull
   @CheckReturnValue
   ChannelAction<StageChannel> createStageChannel(@Nonnull String var1, @Nullable Category var2);

   @Nonnull
   @CheckReturnValue
   default ChannelAction<ForumChannel> createForumChannel(@Nonnull String name) {
      return this.createForumChannel(name, (Category)null);
   }

   @Nonnull
   @CheckReturnValue
   ChannelAction<ForumChannel> createForumChannel(@Nonnull String var1, @Nullable Category var2);

   @Nonnull
   @CheckReturnValue
   default ChannelAction<MediaChannel> createMediaChannel(@Nonnull String name) {
      return this.createMediaChannel(name, (Category)null);
   }

   @Nonnull
   @CheckReturnValue
   ChannelAction<MediaChannel> createMediaChannel(@Nonnull String var1, @Nullable Category var2);

   @Nonnull
   @CheckReturnValue
   ChannelAction<Category> createCategory(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   default <T extends ICopyableChannel> ChannelAction<T> createCopyOfChannel(@Nonnull T channel) {
      Checks.notNull(channel, "Channel");
      return channel.createCopy(this);
   }

   @Nonnull
   @CheckReturnValue
   RoleAction createRole();

   @Nonnull
   @CheckReturnValue
   default RoleAction createCopyOfRole(@Nonnull Role role) {
      Checks.notNull(role, "Role");
      return role.createCopy(this);
   }

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<RichCustomEmoji> createEmoji(@Nonnull String var1, @Nonnull Icon var2, @Nonnull Role... var3);

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<GuildSticker> createSticker(@Nonnull String var1, @Nonnull String var2, @Nonnull FileUpload var3, @Nonnull Collection<String> var4);

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<GuildSticker> createSticker(@Nonnull String name, @Nonnull String description, @Nonnull FileUpload file, @Nonnull String tag, @Nonnull String... tags) {
      List<String> list = new ArrayList(tags.length + 1);
      list.add(tag);
      Collections.addAll(list, tags);
      return this.createSticker(name, description, file, list);
   }

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> deleteSticker(@Nonnull StickerSnowflake var1);

   @Nonnull
   @CheckReturnValue
   SoundboardSoundCreateAction createSoundboardSound(@Nonnull String var1, @Nonnull FileUpload var2);

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> deleteSoundboardSound(@Nonnull SoundboardSoundSnowflake var1);

   @Nonnull
   @CheckReturnValue
   ScheduledEventAction createScheduledEvent(@Nonnull String var1, @Nonnull String var2, @Nonnull OffsetDateTime var3, @Nonnull OffsetDateTime var4);

   @Nonnull
   @CheckReturnValue
   ScheduledEventAction createScheduledEvent(@Nonnull String var1, @Nonnull GuildChannel var2, @Nonnull OffsetDateTime var3);

   @Nonnull
   @CheckReturnValue
   ChannelOrderAction modifyCategoryPositions();

   @Nonnull
   @CheckReturnValue
   ChannelOrderAction modifyTextChannelPositions();

   @Nonnull
   @CheckReturnValue
   ChannelOrderAction modifyVoiceChannelPositions();

   @Nonnull
   @CheckReturnValue
   CategoryOrderAction modifyTextChannelPositions(@Nonnull Category var1);

   @Nonnull
   @CheckReturnValue
   CategoryOrderAction modifyVoiceChannelPositions(@Nonnull Category var1);

   @Nonnull
   @CheckReturnValue
   default RoleOrderAction modifyRolePositions() {
      return this.modifyRolePositions(false);
   }

   @Nonnull
   @CheckReturnValue
   RoleOrderAction modifyRolePositions(boolean var1);

   @Nonnull
   @CheckReturnValue
   GuildWelcomeScreenManager modifyWelcomeScreen();

   public static enum Timeout {
      SECONDS_60(60),
      SECONDS_300(300),
      SECONDS_900(900),
      SECONDS_1800(1800),
      SECONDS_3600(3600);

      private final int seconds;

      private Timeout(int seconds) {
         this.seconds = seconds;
      }

      public int getSeconds() {
         return this.seconds;
      }

      @Nonnull
      public static Timeout fromKey(int seconds) {
         for(Timeout t : values()) {
            if (t.getSeconds() == seconds) {
               return t;
            }
         }

         throw new IllegalArgumentException("Provided key was not recognized. Seconds: " + seconds);
      }

      // $FF: synthetic method
      private static Timeout[] $values() {
         return new Timeout[]{SECONDS_60, SECONDS_300, SECONDS_900, SECONDS_1800, SECONDS_3600};
      }
   }

   public static enum VerificationLevel {
      NONE(0),
      LOW(1),
      MEDIUM(2),
      HIGH(3),
      VERY_HIGH(4),
      UNKNOWN(-1);

      private final int key;

      private VerificationLevel(int key) {
         this.key = key;
      }

      public int getKey() {
         return this.key;
      }

      @Nonnull
      public static VerificationLevel fromKey(int key) {
         for(VerificationLevel level : values()) {
            if (level.getKey() == key) {
               return level;
            }
         }

         return UNKNOWN;
      }

      // $FF: synthetic method
      private static VerificationLevel[] $values() {
         return new VerificationLevel[]{NONE, LOW, MEDIUM, HIGH, VERY_HIGH, UNKNOWN};
      }
   }

   public static enum NotificationLevel {
      ALL_MESSAGES(0),
      MENTIONS_ONLY(1),
      UNKNOWN(-1);

      private final int key;

      private NotificationLevel(int key) {
         this.key = key;
      }

      public int getKey() {
         return this.key;
      }

      @Nonnull
      public static NotificationLevel fromKey(int key) {
         for(NotificationLevel level : values()) {
            if (level.getKey() == key) {
               return level;
            }
         }

         return UNKNOWN;
      }

      // $FF: synthetic method
      private static NotificationLevel[] $values() {
         return new NotificationLevel[]{ALL_MESSAGES, MENTIONS_ONLY, UNKNOWN};
      }
   }

   public static enum MFALevel {
      NONE(0),
      TWO_FACTOR_AUTH(1),
      UNKNOWN(-1);

      private final int key;

      private MFALevel(int key) {
         this.key = key;
      }

      public int getKey() {
         return this.key;
      }

      @Nonnull
      public static MFALevel fromKey(int key) {
         for(MFALevel level : values()) {
            if (level.getKey() == key) {
               return level;
            }
         }

         return UNKNOWN;
      }

      // $FF: synthetic method
      private static MFALevel[] $values() {
         return new MFALevel[]{NONE, TWO_FACTOR_AUTH, UNKNOWN};
      }
   }

   public static enum ExplicitContentLevel {
      OFF(0, "Don't scan any messages."),
      NO_ROLE(1, "Scan messages from members without a role."),
      ALL(2, "Scan messages sent by all members."),
      UNKNOWN(-1, "Unknown filter level!");

      private final int key;
      private final String description;

      private ExplicitContentLevel(int key, String description) {
         this.key = key;
         this.description = description;
      }

      public int getKey() {
         return this.key;
      }

      @Nonnull
      public String getDescription() {
         return this.description;
      }

      @Nonnull
      public static ExplicitContentLevel fromKey(int key) {
         for(ExplicitContentLevel level : values()) {
            if (level.key == key) {
               return level;
            }
         }

         return UNKNOWN;
      }

      // $FF: synthetic method
      private static ExplicitContentLevel[] $values() {
         return new ExplicitContentLevel[]{OFF, NO_ROLE, ALL, UNKNOWN};
      }
   }

   public static enum NSFWLevel {
      DEFAULT(0),
      EXPLICIT(1),
      SAFE(2),
      AGE_RESTRICTED(3),
      UNKNOWN(-1);

      private final int key;

      private NSFWLevel(int key) {
         this.key = key;
      }

      public int getKey() {
         return this.key;
      }

      @Nonnull
      public static NSFWLevel fromKey(int key) {
         for(NSFWLevel level : values()) {
            if (level.getKey() == key) {
               return level;
            }
         }

         return UNKNOWN;
      }

      // $FF: synthetic method
      private static NSFWLevel[] $values() {
         return new NSFWLevel[]{DEFAULT, EXPLICIT, SAFE, AGE_RESTRICTED, UNKNOWN};
      }
   }

   public static enum BoostTier {
      NONE(0, 96000, 50),
      TIER_1(1, 128000, 100),
      TIER_2(2, 256000, 150),
      TIER_3(3, 384000, 250),
      UNKNOWN(-1, Integer.MAX_VALUE, Integer.MAX_VALUE);

      private final int key;
      private final int maxBitrate;
      private final int maxEmojis;

      private BoostTier(int key, int maxBitrate, int maxEmojis) {
         this.key = key;
         this.maxBitrate = maxBitrate;
         this.maxEmojis = maxEmojis;
      }

      public int getKey() {
         return this.key;
      }

      public int getMaxBitrate() {
         return this.maxBitrate;
      }

      public int getMaxEmojis() {
         return this.maxEmojis;
      }

      public long getMaxFileSize() {
         if (this.key == 2) {
            return 52428800L;
         } else {
            return this.key == 3 ? 104857600L : 10485760L;
         }
      }

      @Nonnull
      public static BoostTier fromKey(int key) {
         for(BoostTier tier : values()) {
            if (tier.key == key) {
               return tier;
            }
         }

         return UNKNOWN;
      }

      // $FF: synthetic method
      private static BoostTier[] $values() {
         return new BoostTier[]{NONE, TIER_1, TIER_2, TIER_3, UNKNOWN};
      }
   }

   public static class Ban {
      protected final User user;
      protected final String reason;

      public Ban(User user, String reason) {
         this.user = user;
         this.reason = reason;
      }

      @Nonnull
      public User getUser() {
         return this.user;
      }

      @Nullable
      public String getReason() {
         return this.reason;
      }

      public String toString() {
         return (new EntityString(this)).addMetadata("user", this.user).addMetadata("reason", this.reason).toString();
      }
   }

   public static class MetaData {
      private final int memberLimit;
      private final int presenceLimit;
      private final int approximatePresences;
      private final int approximateMembers;

      public MetaData(int memberLimit, int presenceLimit, int approximatePresences, int approximateMembers) {
         this.memberLimit = memberLimit;
         this.presenceLimit = presenceLimit;
         this.approximatePresences = approximatePresences;
         this.approximateMembers = approximateMembers;
      }

      public int getMemberLimit() {
         return this.memberLimit;
      }

      public int getPresenceLimit() {
         return this.presenceLimit;
      }

      public int getApproximatePresences() {
         return this.approximatePresences;
      }

      public int getApproximateMembers() {
         return this.approximateMembers;
      }
   }
}
