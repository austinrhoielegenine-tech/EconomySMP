package net.dv8tion.jda.internal.entities.detached;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.BulkBanResponse;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.GuildWelcomeScreen;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.RoleMemberCounts;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.SelfMember;
import net.dv8tion.jda.api.entities.SoundboardSound;
import net.dv8tion.jda.api.entities.SoundboardSoundSnowflake;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.VanityInvite;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.automod.AutoModRule;
import net.dv8tion.jda.api.entities.automod.build.AutoModRuleData;
import net.dv8tion.jda.api.entities.channel.ChannelType;
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
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.cache.MemberCacheView;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import net.dv8tion.jda.api.utils.cache.SortedSnowflakeCacheView;
import net.dv8tion.jda.api.utils.concurrent.Task;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.detached.mixin.IDetachableEntityMixin;
import net.dv8tion.jda.internal.requests.restaction.pagination.BanPaginationActionImpl;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.cache.SortedChannelCacheViewImpl;

public class DetachedGuildImpl implements Guild, IDetachableEntityMixin {
   private final long id;
   private final JDAImpl api;
   private Set<String> features;
   private DiscordLocale preferredLocale;

   public DetachedGuildImpl(JDAImpl api, long id) {
      this.preferredLocale = DiscordLocale.ENGLISH_US;
      this.id = id;
      this.api = api;
   }

   public boolean isDetached() {
      return true;
   }

   @Nonnull
   public RestAction<List<Command>> retrieveCommands(boolean withLocalizations) {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<Command> retrieveCommandById(@Nonnull String id) {
      throw this.detachedException();
   }

   @Nonnull
   public CommandCreateAction upsertCommand(@Nonnull CommandData command) {
      throw this.detachedException();
   }

   @Nonnull
   public CommandListUpdateAction updateCommands() {
      throw this.detachedException();
   }

   @Nonnull
   public CommandEditAction editCommandById(@Nonnull Command.Type type, @Nonnull String id) {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<Void> deleteCommandById(@Nonnull String commandId) {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<List<IntegrationPrivilege>> retrieveIntegrationPrivilegesById(@Nonnull String targetId) {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<PrivilegeConfig> retrieveCommandPrivileges() {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<EnumSet<Region>> retrieveRegions(boolean includeDeprecated) {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<List<AutoModRule>> retrieveAutoModRules() {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<AutoModRule> retrieveAutoModRuleById(@Nonnull String id) {
      throw this.detachedException();
   }

   @Nonnull
   public AuditableRestAction<AutoModRule> createAutoModRule(@Nonnull AutoModRuleData rule) {
      throw this.detachedException();
   }

   @Nonnull
   public AutoModRuleManager modifyAutoModRuleById(@Nonnull String id) {
      throw this.detachedException();
   }

   @Nonnull
   public AuditableRestAction<Void> deleteAutoModRuleById(@Nonnull String id) {
      throw this.detachedException();
   }

   @Nonnull
   public MemberAction addMember(@Nonnull String accessToken, @Nonnull UserSnowflake user) {
      throw this.detachedException();
   }

   public boolean isLoaded() {
      throw this.detachedException();
   }

   public void pruneMemberCache() {
      throw this.detachedException();
   }

   public boolean unloadMember(long userId) {
      throw this.detachedException();
   }

   public int getMemberCount() {
      throw this.detachedException();
   }

   @Nonnull
   public String getName() {
      throw this.detachedException();
   }

   public String getIconId() {
      throw this.detachedException();
   }

   @Nonnull
   public Set<String> getFeatures() {
      return this.features;
   }

   public String getSplashId() {
      throw this.detachedException();
   }

   @Nullable
   public String getVanityCode() {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<VanityInvite> retrieveVanityInvite() {
      throw this.detachedException();
   }

   @Nullable
   public String getDescription() {
      throw this.detachedException();
   }

   @Nonnull
   public DiscordLocale getLocale() {
      return this.preferredLocale;
   }

   @Nullable
   public String getBannerId() {
      throw this.detachedException();
   }

   @Nonnull
   public Guild.BoostTier getBoostTier() {
      throw this.detachedException();
   }

   public int getBoostCount() {
      throw this.detachedException();
   }

   @Nonnull
   public List<Member> getBoosters() {
      throw this.detachedException();
   }

   public int getMaxMembers() {
      throw this.detachedException();
   }

   public int getMaxPresences() {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<Guild.MetaData> retrieveMetaData() {
      throw this.detachedException();
   }

   public VoiceChannel getAfkChannel() {
      throw this.detachedException();
   }

   public TextChannel getSystemChannel() {
      throw this.detachedException();
   }

   public TextChannel getRulesChannel() {
      throw this.detachedException();
   }

   @Nonnull
   public CacheRestAction<ScheduledEvent> retrieveScheduledEventById(@Nonnull String id) {
      throw this.detachedException();
   }

   @Nonnull
   public CacheRestAction<ScheduledEvent> retrieveScheduledEventById(long id) {
      throw this.detachedException();
   }

   @Nonnull
   public ScheduledEventAction createScheduledEvent(@Nonnull String name, @Nonnull String location, @Nonnull OffsetDateTime startTime, @Nonnull OffsetDateTime endTime) {
      throw this.detachedException();
   }

   @Nonnull
   public ScheduledEventAction createScheduledEvent(@Nonnull String name, @Nonnull GuildChannel channel, @Nonnull OffsetDateTime startTime) {
      throw this.detachedException();
   }

   public TextChannel getCommunityUpdatesChannel() {
      throw this.detachedException();
   }

   @Nullable
   public TextChannel getSafetyAlertsChannel() {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<List<Webhook>> retrieveWebhooks() {
      throw this.detachedException();
   }

   public Member getOwner() {
      throw this.detachedException();
   }

   public long getOwnerIdLong() {
      throw this.detachedException();
   }

   @Nonnull
   public Guild.Timeout getAfkTimeout() {
      throw this.detachedException();
   }

   @Nonnull
   public SecurityIncidentActions getSecurityIncidentActions() {
      throw this.detachedException();
   }

   @Nonnull
   public SecurityIncidentDetections getSecurityIncidentDetections() {
      throw this.detachedException();
   }

   public boolean isMember(@Nonnull UserSnowflake user) {
      throw this.detachedException();
   }

   @Nonnull
   public SelfMember getSelfMember() {
      throw this.detachedException();
   }

   public Member getMember(@Nonnull UserSnowflake user) {
      throw this.detachedException();
   }

   @Nonnull
   public Task<List<Member>> findMembers(@Nonnull Predicate<? super Member> filter) {
      throw this.detachedException();
   }

   @Nonnull
   public MemberCacheView getMemberCache() {
      throw this.detachedException();
   }

   @Nonnull
   public SortedSnowflakeCacheView<ScheduledEvent> getScheduledEventCache() {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<List<ScheduledEvent>> retrieveScheduledEvents(boolean includeUserCount) {
      throw this.detachedException();
   }

   @Nonnull
   public SortedSnowflakeCacheView<Category> getCategoryCache() {
      throw this.detachedException();
   }

   @Nonnull
   public SortedSnowflakeCacheView<TextChannel> getTextChannelCache() {
      throw this.detachedException();
   }

   @Nonnull
   public SortedSnowflakeCacheView<NewsChannel> getNewsChannelCache() {
      throw this.detachedException();
   }

   @Nonnull
   public SortedSnowflakeCacheView<VoiceChannel> getVoiceChannelCache() {
      throw this.detachedException();
   }

   @Nonnull
   public SortedSnowflakeCacheView<ForumChannel> getForumChannelCache() {
      throw this.detachedException();
   }

   @Nonnull
   public SnowflakeCacheView<MediaChannel> getMediaChannelCache() {
      throw this.detachedException();
   }

   @Nonnull
   public SortedSnowflakeCacheView<StageChannel> getStageChannelCache() {
      throw this.detachedException();
   }

   @Nonnull
   public SortedSnowflakeCacheView<ThreadChannel> getThreadChannelCache() {
      throw this.detachedException();
   }

   @Nonnull
   public SortedChannelCacheViewImpl<GuildChannel> getChannelCache() {
      throw this.detachedException();
   }

   @Nullable
   public GuildChannel getGuildChannelById(long id) {
      throw this.detachedException();
   }

   public GuildChannel getGuildChannelById(@Nonnull ChannelType type, long id) {
      throw this.detachedException();
   }

   @Nonnull
   public SortedSnowflakeCacheView<Role> getRoleCache() {
      throw this.detachedException();
   }

   @Nonnull
   public SnowflakeCacheView<RichCustomEmoji> getEmojiCache() {
      throw this.detachedException();
   }

   @Nonnull
   public SnowflakeCacheView<GuildSticker> getStickerCache() {
      throw this.detachedException();
   }

   @Nonnull
   public SnowflakeCacheView<SoundboardSound> getSoundboardSoundCache() {
      throw this.detachedException();
   }

   @Nonnull
   public List<GuildChannel> getChannels(boolean includeHidden) {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<List<RichCustomEmoji>> retrieveEmojis() {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<RichCustomEmoji> retrieveEmojiById(@Nonnull String id) {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<RichCustomEmoji> retrieveEmoji(@Nonnull CustomEmoji emoji) {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<List<GuildSticker>> retrieveStickers() {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<GuildSticker> retrieveSticker(@Nonnull StickerSnowflake sticker) {
      throw this.detachedException();
   }

   @Nonnull
   public GuildStickerManager editSticker(@Nonnull StickerSnowflake sticker) {
      throw this.detachedException();
   }

   @Nonnull
   public CacheRestAction<List<SoundboardSound>> retrieveSoundboardSounds() {
      throw this.detachedException();
   }

   @Nonnull
   public CacheRestAction<SoundboardSound> retrieveSoundboardSound(@Nonnull SoundboardSoundSnowflake sound) {
      throw this.detachedException();
   }

   @Nonnull
   public SoundboardSoundManager editSoundboardSound(@Nonnull SoundboardSoundSnowflake sound) {
      throw this.detachedException();
   }

   @Nonnull
   public BanPaginationActionImpl retrieveBanList() {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<Guild.Ban> retrieveBan(@Nonnull UserSnowflake user) {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<Integer> retrievePrunableMemberCount(int days) {
      throw this.detachedException();
   }

   @Nonnull
   public Role getPublicRole() {
      throw this.detachedException();
   }

   @Nullable
   public DefaultGuildChannelUnion getDefaultChannel() {
      throw this.detachedException();
   }

   @Nonnull
   public GuildManager getManager() {
      throw this.detachedException();
   }

   public boolean isBoostProgressBarEnabled() {
      throw this.detachedException();
   }

   @Nonnull
   public AuditLogPaginationAction retrieveAuditLogs() {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<Void> leave() {
      throw this.detachedException();
   }

   @Nonnull
   public AudioManager getAudioManager() {
      throw this.detachedException();
   }

   @Nonnull
   public synchronized Task<Void> requestToSpeak() {
      throw this.detachedException();
   }

   @Nonnull
   public synchronized Task<Void> cancelRequestToSpeak() {
      throw this.detachedException();
   }

   @Nonnull
   public JDAImpl getJDA() {
      return this.api;
   }

   @Nonnull
   public List<GuildVoiceState> getVoiceStates() {
      throw this.detachedException();
   }

   @Nonnull
   public CacheRestAction<GuildVoiceState> retrieveMemberVoiceStateById(long id) {
      throw this.detachedException();
   }

   @Nonnull
   public Guild.VerificationLevel getVerificationLevel() {
      throw this.detachedException();
   }

   @Nonnull
   public Guild.NSFWLevel getNSFWLevel() {
      throw this.detachedException();
   }

   @Nonnull
   public Set<SystemChannelFlag> getSystemChannelFlags() {
      throw this.detachedException();
   }

   public int getSystemChannelFlagsRaw() {
      throw this.detachedException();
   }

   @Nonnull
   public Guild.NotificationLevel getDefaultNotificationLevel() {
      throw this.detachedException();
   }

   @Nonnull
   public Guild.MFALevel getRequiredMFALevel() {
      throw this.detachedException();
   }

   @Nonnull
   public Guild.ExplicitContentLevel getExplicitContentLevel() {
      throw this.detachedException();
   }

   @Nonnull
   public Task<Void> loadMembers(@Nonnull Consumer<Member> callback) {
      throw this.detachedException();
   }

   @Nonnull
   public CacheRestAction<Member> retrieveMemberById(long id) {
      throw this.detachedException();
   }

   @Nonnull
   public Task<List<Member>> retrieveMembersByIds(boolean includePresence, @Nonnull long... ids) {
      throw this.detachedException();
   }

   @Nonnull
   @CheckReturnValue
   public Task<List<Member>> retrieveMembersByPrefix(@Nonnull String prefix, int limit) {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<List<ThreadChannel>> retrieveActiveThreads() {
      throw this.detachedException();
   }

   public long getIdLong() {
      return this.id;
   }

   @Nonnull
   public RestAction<List<Invite>> retrieveInvites() {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<List<Template>> retrieveTemplates() {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<Template> createTemplate(@Nonnull String name, @Nullable String description) {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<GuildWelcomeScreen> retrieveWelcomeScreen() {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<Void> moveVoiceMember(@Nonnull UserSnowflake user, @Nullable AudioChannel audioChannel) {
      throw this.detachedException();
   }

   @Nonnull
   public AuditableRestAction<Void> modifyNickname(@Nonnull Member member, String nickname) {
      throw this.detachedException();
   }

   @Nonnull
   public AuditableRestAction<Integer> prune(int days, boolean wait, @Nonnull Role... roles) {
      throw this.detachedException();
   }

   @Nonnull
   public AuditableRestAction<Void> modifySecurityIncidents(@Nonnull SecurityIncidentActions incidents) {
      throw this.detachedException();
   }

   @Nonnull
   public AuditableRestAction<Void> kick(@Nonnull UserSnowflake user) {
      throw this.detachedException();
   }

   @Nonnull
   public AuditableRestAction<Void> ban(@Nonnull UserSnowflake user, int duration, @Nonnull TimeUnit unit) {
      throw this.detachedException();
   }

   @Nonnull
   public AuditableRestAction<BulkBanResponse> ban(@Nonnull Collection<? extends UserSnowflake> users, @Nullable Duration deletionTime) {
      throw this.detachedException();
   }

   @Nonnull
   public AuditableRestAction<Void> unban(@Nonnull UserSnowflake user) {
      throw this.detachedException();
   }

   @Nonnull
   public AuditableRestAction<Void> timeoutUntil(@Nonnull UserSnowflake user, @Nonnull TemporalAccessor temporal) {
      throw this.detachedException();
   }

   @Nonnull
   public AuditableRestAction<Void> removeTimeout(@Nonnull UserSnowflake user) {
      throw this.detachedException();
   }

   @Nonnull
   public AuditableRestAction<Void> deafen(@Nonnull UserSnowflake user, boolean deafen) {
      throw this.detachedException();
   }

   @Nonnull
   public AuditableRestAction<Void> mute(@Nonnull UserSnowflake user, boolean mute) {
      throw this.detachedException();
   }

   @Nonnull
   public AuditableRestAction<Void> addRoleToMember(@Nonnull UserSnowflake user, @Nonnull Role role) {
      throw this.detachedException();
   }

   @Nonnull
   public AuditableRestAction<Void> removeRoleFromMember(@Nonnull UserSnowflake user, @Nonnull Role role) {
      throw this.detachedException();
   }

   @Nonnull
   public AuditableRestAction<Void> modifyMemberRoles(@Nonnull Member member, Collection<Role> rolesToAdd, Collection<Role> rolesToRemove) {
      throw this.detachedException();
   }

   @Nonnull
   public AuditableRestAction<Void> modifyMemberRoles(@Nonnull Member member, @Nonnull Collection<Role> roles) {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<RoleMemberCounts> retrieveRoleMemberCounts() {
      throw this.detachedException();
   }

   @Nonnull
   public ChannelAction<TextChannel> createTextChannel(@Nonnull String name, Category parent) {
      throw this.detachedException();
   }

   @Nonnull
   public ChannelAction<NewsChannel> createNewsChannel(@Nonnull String name, Category parent) {
      throw this.detachedException();
   }

   @Nonnull
   public ChannelAction<VoiceChannel> createVoiceChannel(@Nonnull String name, Category parent) {
      throw this.detachedException();
   }

   @Nonnull
   public ChannelAction<StageChannel> createStageChannel(@Nonnull String name, Category parent) {
      throw this.detachedException();
   }

   @Nonnull
   public ChannelAction<ForumChannel> createForumChannel(@Nonnull String name, Category parent) {
      throw this.detachedException();
   }

   @Nonnull
   public ChannelAction<MediaChannel> createMediaChannel(@Nonnull String name, @Nullable Category parent) {
      throw this.detachedException();
   }

   @Nonnull
   public ChannelAction<Category> createCategory(@Nonnull String name) {
      throw this.detachedException();
   }

   @Nonnull
   public RoleAction createRole() {
      throw this.detachedException();
   }

   @Nonnull
   public AuditableRestAction<RichCustomEmoji> createEmoji(@Nonnull String name, @Nonnull Icon icon, @Nonnull Role... roles) {
      throw this.detachedException();
   }

   @Nonnull
   public AuditableRestAction<GuildSticker> createSticker(@Nonnull String name, @Nonnull String description, @Nonnull FileUpload file, @Nonnull Collection<String> tags) {
      throw this.detachedException();
   }

   @Nonnull
   public AuditableRestAction<Void> deleteSticker(@Nonnull StickerSnowflake id) {
      throw this.detachedException();
   }

   @Nonnull
   public SoundboardSoundCreateAction createSoundboardSound(@Nonnull String name, @Nonnull FileUpload file) {
      throw this.detachedException();
   }

   @Nonnull
   public AuditableRestAction<Void> deleteSoundboardSound(@Nonnull SoundboardSoundSnowflake sound) {
      throw this.detachedException();
   }

   @Nonnull
   public ChannelOrderAction modifyCategoryPositions() {
      throw this.detachedException();
   }

   @Nonnull
   public ChannelOrderAction modifyTextChannelPositions() {
      throw this.detachedException();
   }

   @Nonnull
   public ChannelOrderAction modifyVoiceChannelPositions() {
      throw this.detachedException();
   }

   @Nonnull
   public CategoryOrderAction modifyTextChannelPositions(@Nonnull Category category) {
      throw this.detachedException();
   }

   @Nonnull
   public CategoryOrderAction modifyVoiceChannelPositions(@Nonnull Category category) {
      throw this.detachedException();
   }

   @Nonnull
   public RoleOrderAction modifyRolePositions(boolean useAscendingOrder) {
      throw this.detachedException();
   }

   @Nonnull
   public GuildWelcomeScreenManager modifyWelcomeScreen() {
      throw this.detachedException();
   }

   public DetachedGuildImpl setFeatures(Set<String> features) {
      this.features = Collections.unmodifiableSet(features);
      return this;
   }

   public DetachedGuildImpl setLocale(DiscordLocale locale) {
      this.preferredLocale = locale;
      return this;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof DetachedGuildImpl)) {
         return false;
      } else {
         DetachedGuildImpl oGuild = (DetachedGuildImpl)o;
         return this.id == oGuild.id;
      }
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }

   public String toString() {
      return (new EntityString(this)).toString();
   }
}
