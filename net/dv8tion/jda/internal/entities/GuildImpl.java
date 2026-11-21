package net.dv8tion.jda.internal.entities;

import gnu.trove.map.TLongIntMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongIntHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.entities.BulkBanResponse;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.GuildWelcomeScreen;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.RoleMemberCounts;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.SelfMember;
import net.dv8tion.jda.api.entities.SoundboardSound;
import net.dv8tion.jda.api.entities.SoundboardSoundSnowflake;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.VanityInvite;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.automod.AutoModRule;
import net.dv8tion.jda.api.entities.automod.build.AutoModRuleData;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
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
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.guild.SecurityIncidentActions;
import net.dv8tion.jda.api.entities.guild.SecurityIncidentDetections;
import net.dv8tion.jda.api.entities.guild.SystemChannelFlag;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.entities.sticker.StandardSticker;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.api.entities.templates.Template;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.ParsingException;
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
import net.dv8tion.jda.api.requests.Route;
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
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.cache.CacheView;
import net.dv8tion.jda.api.utils.cache.MemberCacheView;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import net.dv8tion.jda.api.utils.cache.SortedSnowflakeCacheView;
import net.dv8tion.jda.api.utils.concurrent.Task;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.automod.AutoModRuleImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.GuildChannelMixin;
import net.dv8tion.jda.internal.handle.EventCache;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.dv8tion.jda.internal.interactions.command.CommandImpl;
import net.dv8tion.jda.internal.managers.AudioManagerImpl;
import net.dv8tion.jda.internal.managers.AutoModRuleManagerImpl;
import net.dv8tion.jda.internal.managers.GuildManagerImpl;
import net.dv8tion.jda.internal.managers.GuildStickerManagerImpl;
import net.dv8tion.jda.internal.managers.GuildWelcomeScreenManagerImpl;
import net.dv8tion.jda.internal.managers.SoundboardSoundManagerImpl;
import net.dv8tion.jda.internal.requests.CompletedRestAction;
import net.dv8tion.jda.internal.requests.DeferredRestAction;
import net.dv8tion.jda.internal.requests.MemberChunkManager;
import net.dv8tion.jda.internal.requests.Requester;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.WebSocketClient;
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl;
import net.dv8tion.jda.internal.requests.restaction.ChannelActionImpl;
import net.dv8tion.jda.internal.requests.restaction.CommandCreateActionImpl;
import net.dv8tion.jda.internal.requests.restaction.CommandEditActionImpl;
import net.dv8tion.jda.internal.requests.restaction.CommandListUpdateActionImpl;
import net.dv8tion.jda.internal.requests.restaction.MemberActionImpl;
import net.dv8tion.jda.internal.requests.restaction.RoleActionImpl;
import net.dv8tion.jda.internal.requests.restaction.ScheduledEventActionImpl;
import net.dv8tion.jda.internal.requests.restaction.SoundboardSoundCreateActionImpl;
import net.dv8tion.jda.internal.requests.restaction.order.CategoryOrderActionImpl;
import net.dv8tion.jda.internal.requests.restaction.order.ChannelOrderActionImpl;
import net.dv8tion.jda.internal.requests.restaction.order.RoleOrderActionImpl;
import net.dv8tion.jda.internal.requests.restaction.pagination.AuditLogPaginationActionImpl;
import net.dv8tion.jda.internal.requests.restaction.pagination.BanPaginationActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;
import net.dv8tion.jda.internal.utils.UnlockHook;
import net.dv8tion.jda.internal.utils.cache.AbstractCacheView;
import net.dv8tion.jda.internal.utils.cache.ChannelCacheViewImpl;
import net.dv8tion.jda.internal.utils.cache.MemberCacheViewImpl;
import net.dv8tion.jda.internal.utils.cache.SnowflakeCacheViewImpl;
import net.dv8tion.jda.internal.utils.cache.SortedChannelCacheViewImpl;
import net.dv8tion.jda.internal.utils.cache.SortedSnowflakeCacheViewImpl;
import net.dv8tion.jda.internal.utils.concurrent.task.GatewayTask;
import okhttp3.MediaType;
import okhttp3.MultipartBody;

public class GuildImpl implements Guild {
   private final long id;
   private final JDAImpl api;
   private final SortedSnowflakeCacheViewImpl<ScheduledEvent> scheduledEventCache = new SortedSnowflakeCacheViewImpl<ScheduledEvent>(ScheduledEvent.class, ScheduledEvent::getName, Comparator.naturalOrder());
   private final SortedChannelCacheViewImpl<GuildChannel> channelCache = new SortedChannelCacheViewImpl<GuildChannel>(GuildChannel.class);
   private final SortedSnowflakeCacheViewImpl<Role> roleCache = new SortedSnowflakeCacheViewImpl<Role>(Role.class, Role::getName, Comparator.reverseOrder());
   private final SnowflakeCacheViewImpl<RichCustomEmoji> emojicache = new SnowflakeCacheViewImpl<RichCustomEmoji>(RichCustomEmoji.class, Emoji::getName);
   private final SnowflakeCacheViewImpl<GuildSticker> stickerCache = new SnowflakeCacheViewImpl<GuildSticker>(GuildSticker.class, Sticker::getName);
   private final SnowflakeCacheViewImpl<SoundboardSound> soundboardCache = new SnowflakeCacheViewImpl<SoundboardSound>(SoundboardSound.class, SoundboardSound::getName);
   private final MemberCacheViewImpl memberCache = new MemberCacheViewImpl();
   private final CacheView.SimpleCacheView<MemberPresenceImpl> memberPresences;
   private final SnowflakeCacheViewImpl<GuildVoiceStateImpl> voiceStateCache = new SnowflakeCacheViewImpl<GuildVoiceStateImpl>(GuildVoiceStateImpl.class, (state) -> state.getMember().getEffectiveName());
   private CompletableFuture<Void> pendingRequestToSpeak;
   private Member owner;
   private String name;
   private String iconId;
   private String splashId;
   private String vanityCode;
   private String description;
   private String banner;
   private int maxPresences;
   private int maxMembers;
   private int boostCount;
   private long ownerId;
   private Set<String> features;
   private VoiceChannel afkChannel;
   private TextChannel systemChannel;
   private TextChannel rulesChannel;
   private TextChannel communityUpdatesChannel;
   private TextChannel safetyAlertsChannel;
   private Role publicRole;
   private SecurityIncidentActions securityIncidentActions = SecurityIncidentActions.disabled();
   private SecurityIncidentDetections securityIncidentDetections;
   private Guild.VerificationLevel verificationLevel;
   private Guild.NotificationLevel defaultNotificationLevel;
   private Guild.MFALevel mfaLevel;
   private Guild.ExplicitContentLevel explicitContentLevel;
   private Guild.NSFWLevel nsfwLevel;
   private Guild.Timeout afkTimeout;
   private Guild.BoostTier boostTier;
   private DiscordLocale preferredLocale;
   private int memberCount;
   private int systemChannelFlags;
   private boolean boostProgressBarEnabled;

   public GuildImpl(JDAImpl api, long id) {
      this.securityIncidentDetections = SecurityIncidentDetections.EMPTY;
      this.verificationLevel = Guild.VerificationLevel.UNKNOWN;
      this.defaultNotificationLevel = Guild.NotificationLevel.UNKNOWN;
      this.mfaLevel = Guild.MFALevel.UNKNOWN;
      this.explicitContentLevel = Guild.ExplicitContentLevel.UNKNOWN;
      this.nsfwLevel = Guild.NSFWLevel.UNKNOWN;
      this.boostTier = Guild.BoostTier.NONE;
      this.preferredLocale = DiscordLocale.ENGLISH_US;
      this.id = id;
      this.api = api;
      if (api.getCacheFlags().stream().anyMatch(CacheFlag::isPresence)) {
         this.memberPresences = new CacheView.SimpleCacheView<MemberPresenceImpl>(MemberPresenceImpl.class, (Function)null);
      } else {
         this.memberPresences = null;
      }

   }

   public boolean isDetached() {
      return false;
   }

   public void invalidate() {
      this.getJDA().getGuildsView().remove(this.id);
      ChannelCacheViewImpl<Channel> channelsView = this.getJDA().getChannelsView();
      UnlockHook hook = channelsView.writeLock();

      try {
         this.getChannels().forEach((channel) -> channelsView.remove(channel.getType(), channel.getIdLong()));
      } catch (Throwable var12) {
         if (hook != null) {
            try {
               hook.close();
            } catch (Throwable var10) {
               var12.addSuppressed(var10);
            }
         }

         throw var12;
      }

      if (hook != null) {
         hook.close();
      }

      this.getJDA().getClient().removeAudioConnection(this.id);
      AbstractCacheView<AudioManager> audioManagerView = this.getJDA().getAudioManagersView();
      AudioManagerImpl manager = (AudioManagerImpl)audioManagerView.get(this.id);
      if (manager != null) {
         manager.closeAudioConnection(ConnectionStatus.DISCONNECTED_REMOVED_FROM_GUILD);
      }

      audioManagerView.remove(this.id);
      TLongSet memberIds = this.getMembersView().keySet();
      Stream var10000 = this.getJDA().getGuildCache().stream();
      Objects.requireNonNull(GuildImpl.class);
      var10000.map(GuildImpl.class::cast).forEach((g) -> memberIds.removeAll(g.getMembersView().keySet()));
      SnowflakeCacheViewImpl<User> userView = this.getJDA().getUsersView();
      UnlockHook hook = userView.writeLock();

      try {
         long selfId = this.getJDA().getSelfUser().getIdLong();
         memberIds.forEach((memberId) -> {
            if (memberId == selfId) {
               return true;
            } else {
               userView.remove(memberId);
               this.getJDA().getEventCache().clear(EventCache.Type.USER, memberId);
               return true;
            }
         });
      } catch (Throwable var11) {
         if (hook != null) {
            try {
               hook.close();
            } catch (Throwable var9) {
               var11.addSuppressed(var9);
            }
         }

         throw var11;
      }

      if (hook != null) {
         hook.close();
      }

   }

   public void uncacheChannel(GuildChannel channel, boolean keepThreads) {
      long id = channel.getIdLong();
      if (this.channelCache.remove(channel.getType(), id) != null) {
         this.api.getChannelsView().remove(channel.getType(), id);
         if (!keepThreads && channel instanceof IThreadContainer) {
            SortedChannelCacheViewImpl<GuildChannel> localView = this.getChannelView();
            ChannelCacheViewImpl<Channel> globalView = this.api.getChannelsView();
            Predicate<ThreadChannel> predicate = (thread) -> channel.equals(thread.getParentChannel());
            UnlockHook hook1 = localView.writeLock();

            try {
               UnlockHook hook2 = globalView.writeLock();

               try {
                  localView.removeIf(ThreadChannel.class, predicate);
                  globalView.removeIf(ThreadChannel.class, predicate);
               } catch (Throwable var14) {
                  if (hook2 != null) {
                     try {
                        hook2.close();
                     } catch (Throwable var13) {
                        var14.addSuppressed(var13);
                     }
                  }

                  throw var14;
               }

               if (hook2 != null) {
                  hook2.close();
               }
            } catch (Throwable var15) {
               if (hook1 != null) {
                  try {
                     hook1.close();
                  } catch (Throwable var12) {
                     var15.addSuppressed(var12);
                  }
               }

               throw var15;
            }

            if (hook1 != null) {
               hook1.close();
            }
         }

      }
   }

   @Nonnull
   public RestAction<List<Command>> retrieveCommands(boolean withLocalizations) {
      Route.CompiledRoute route = Route.Interactions.GET_GUILD_COMMANDS.compile(this.getJDA().getSelfUser().getApplicationId(), this.getId()).withQueryParams("with_localizations", String.valueOf(withLocalizations));
      return new RestActionImpl<List<Command>>(this.getJDA(), route, (response, request) -> (List)response.getArray().stream(DataArray::getObject).map((json) -> new CommandImpl(this.getJDA(), this, json)).collect(Collectors.toList()));
   }

   @Nonnull
   public RestAction<Command> retrieveCommandById(@Nonnull String id) {
      Checks.isSnowflake(id);
      Route.CompiledRoute route = Route.Interactions.GET_GUILD_COMMAND.compile(this.getJDA().getSelfUser().getApplicationId(), this.getId(), id);
      return new RestActionImpl<Command>(this.getJDA(), route, (response, request) -> new CommandImpl(this.getJDA(), this, response.getObject()));
   }

   @Nonnull
   public CommandCreateAction upsertCommand(@Nonnull CommandData command) {
      Checks.notNull(command, "CommandData");
      return new CommandCreateActionImpl(this, (CommandDataImpl)command);
   }

   @Nonnull
   public CommandListUpdateAction updateCommands() {
      Route.CompiledRoute route = Route.Interactions.UPDATE_GUILD_COMMANDS.compile(this.getJDA().getSelfUser().getApplicationId(), this.getId());
      return new CommandListUpdateActionImpl(this.getJDA(), this, route);
   }

   @Nonnull
   public CommandEditAction editCommandById(@Nonnull Command.Type type, @Nonnull String id) {
      Checks.isSnowflake(id);
      Checks.notNull(type, "CommandType");
      Checks.check(type != Command.Type.UNKNOWN, "Type must not be UNKNOWN");
      return new CommandEditActionImpl(this, type, id);
   }

   @Nonnull
   public RestAction<Void> deleteCommandById(@Nonnull String commandId) {
      Checks.isSnowflake(commandId);
      Route.CompiledRoute route = Route.Interactions.DELETE_GUILD_COMMAND.compile(this.getJDA().getSelfUser().getApplicationId(), this.getId(), commandId);
      return new RestActionImpl<Void>(this.getJDA(), route);
   }

   @Nonnull
   public RestAction<List<IntegrationPrivilege>> retrieveIntegrationPrivilegesById(@Nonnull String targetId) {
      Checks.isSnowflake(targetId, "ID");
      Route.CompiledRoute route = Route.Interactions.GET_COMMAND_PERMISSIONS.compile(this.getJDA().getSelfUser().getApplicationId(), this.getId(), targetId);
      return new RestActionImpl<List<IntegrationPrivilege>>(this.getJDA(), route, (response, request) -> this.parsePrivilegesList(response.getObject()));
   }

   @Nonnull
   public RestAction<PrivilegeConfig> retrieveCommandPrivileges() {
      Route.CompiledRoute route = Route.Interactions.GET_ALL_COMMAND_PERMISSIONS.compile(this.getJDA().getSelfUser().getApplicationId(), this.getId());
      return new RestActionImpl<PrivilegeConfig>(this.getJDA(), route, (response, request) -> {
         Map<String, List<IntegrationPrivilege>> privileges = new HashMap();
         response.getArray().stream(DataArray::getObject).forEach((obj) -> {
            String id = obj.getString("id");
            List<IntegrationPrivilege> list = Collections.unmodifiableList(this.parsePrivilegesList(obj));
            privileges.put(id, list);
         });
         return new PrivilegeConfig(this, privileges);
      });
   }

   private List<IntegrationPrivilege> parsePrivilegesList(DataObject obj) {
      return (List)obj.getArray("permissions").stream(DataArray::getObject).map(this::parsePrivilege).collect(Collectors.toList());
   }

   private IntegrationPrivilege parsePrivilege(DataObject data) {
      IntegrationPrivilege.Type type = IntegrationPrivilege.Type.fromKey(data.getInt("type", 1));
      boolean enabled = data.getBoolean("permission");
      return new IntegrationPrivilege(this, type, enabled, data.getUnsignedLong("id"));
   }

   @Nonnull
   public RestAction<EnumSet<Region>> retrieveRegions(boolean includeDeprecated) {
      Route.CompiledRoute route = Route.Guilds.GET_VOICE_REGIONS.compile(this.getId());
      return new RestActionImpl<EnumSet<Region>>(this.getJDA(), route, (response, request) -> {
         EnumSet<Region> set = EnumSet.noneOf(Region.class);
         DataArray arr = response.getArray();

         for(int i = 0; i < arr.length(); ++i) {
            DataObject obj = arr.getObject(i);
            if (includeDeprecated || !obj.getBoolean("deprecated")) {
               String id = obj.getString("id", "");
               Region region = Region.fromKey(id);
               if (region != Region.UNKNOWN) {
                  set.add(region);
               }
            }
         }

         return set;
      });
   }

   @Nonnull
   public RestAction<List<AutoModRule>> retrieveAutoModRules() {
      this.checkPermission(Permission.MANAGE_SERVER);
      Route.CompiledRoute route = Route.AutoModeration.LIST_RULES.compile(this.getId());
      return new RestActionImpl<List<AutoModRule>>(this.api, route, (response, request) -> {
         DataArray array = response.getArray();
         List<AutoModRule> rules = new ArrayList(array.length());

         for(int i = 0; i < array.length(); ++i) {
            try {
               DataObject obj = array.getObject(i);
               rules.add(AutoModRuleImpl.fromData(this, obj));
            } catch (ParsingException exception) {
               EntityBuilder.LOG.error((String)"Failed to parse AutoModRule", (Throwable)exception);
            }
         }

         return Collections.unmodifiableList(rules);
      });
   }

   @Nonnull
   public RestAction<AutoModRule> retrieveAutoModRuleById(@Nonnull String id) {
      Checks.isSnowflake(id);
      this.checkPermission(Permission.MANAGE_SERVER);
      Route.CompiledRoute route = Route.AutoModeration.GET_RULE.compile(this.getId(), id);
      return new RestActionImpl<AutoModRule>(this.api, route, (response, request) -> AutoModRuleImpl.fromData(this, response.getObject()));
   }

   @Nonnull
   public AuditableRestAction<AutoModRule> createAutoModRule(@Nonnull AutoModRuleData rule) {
      Checks.notNull(rule, "AutoMod Rule");
      rule.getRequiredPermissions().forEach(this::checkPermission);
      Route.CompiledRoute route = Route.AutoModeration.CREATE_RULE.compile(this.getId());
      return new AuditableRestActionImpl<AutoModRule>(this.api, route, rule.toData(), (response, request) -> AutoModRuleImpl.fromData(this, response.getObject()));
   }

   @Nonnull
   public AutoModRuleManager modifyAutoModRuleById(@Nonnull String id) {
      Checks.isSnowflake(id);
      this.checkPermission(Permission.MANAGE_SERVER);
      return new AutoModRuleManagerImpl(this, id);
   }

   @Nonnull
   public AuditableRestAction<Void> deleteAutoModRuleById(@Nonnull String id) {
      Checks.isSnowflake(id);
      this.checkPermission(Permission.MANAGE_SERVER);
      Route.CompiledRoute route = Route.AutoModeration.DELETE_RULE.compile(this.getId(), id);
      return new AuditableRestActionImpl<Void>(this.api, route);
   }

   @Nonnull
   public MemberAction addMember(@Nonnull String accessToken, @Nonnull UserSnowflake user) {
      Checks.notBlank(accessToken, "Access-Token");
      Checks.notNull(user, "User");
      Checks.check(!this.isMember(user), "User is already in this guild");
      if (!this.getSelfMember().hasPermission(new Permission[]{Permission.CREATE_INSTANT_INVITE})) {
         throw new InsufficientPermissionException(this, Permission.CREATE_INSTANT_INVITE);
      } else {
         return new MemberActionImpl(this.getJDA(), this, user.getId(), accessToken);
      }
   }

   public boolean isLoaded() {
      return this.getJDA().isIntent(GatewayIntent.GUILD_MEMBERS) && (long)this.getMemberCount() <= this.getMemberCache().size();
   }

   public void pruneMemberCache() {
      UnlockHook h = this.memberCache.writeLock();

      try {
         EntityBuilder builder = this.getJDA().getEntityBuilder();
         Set<Member> members = this.memberCache.asSet();
         members.forEach((m) -> builder.updateMemberCache((MemberImpl)m));
      } catch (Throwable var5) {
         if (h != null) {
            try {
               h.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (h != null) {
         h.close();
      }

   }

   public boolean unloadMember(long userId) {
      if (userId == this.api.getSelfUser().getIdLong()) {
         return false;
      } else {
         MemberImpl member = (MemberImpl)this.getMemberById(userId);
         if (member == null) {
            return false;
         } else {
            this.api.getEntityBuilder().updateMemberCache(member, true);
            return true;
         }
      }
   }

   public int getMemberCount() {
      return this.memberCount;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   public String getIconId() {
      return this.iconId;
   }

   @Nonnull
   public Set<String> getFeatures() {
      return this.features;
   }

   public String getSplashId() {
      return this.splashId;
   }

   @Nullable
   public String getVanityCode() {
      return this.vanityCode;
   }

   @Nonnull
   public RestAction<VanityInvite> retrieveVanityInvite() {
      this.checkPermission(Permission.MANAGE_SERVER);
      JDAImpl api = this.getJDA();
      Route.CompiledRoute route = Route.Guilds.GET_VANITY_URL.compile(this.getId());
      return new RestActionImpl<VanityInvite>(api, route, (response, request) -> new VanityInvite(this.vanityCode, response.getObject().getInt("uses")));
   }

   @Nullable
   public String getDescription() {
      return this.description;
   }

   @Nonnull
   public DiscordLocale getLocale() {
      return this.preferredLocale;
   }

   @Nullable
   public String getBannerId() {
      return this.banner;
   }

   @Nonnull
   public Guild.BoostTier getBoostTier() {
      return this.boostTier;
   }

   public int getBoostCount() {
      return this.boostCount;
   }

   @Nonnull
   public List<Member> getBoosters() {
      return (List)this.memberCache.applyStream((members) -> (List)members.filter((m) -> m.getTimeBoosted() != null).sorted(Comparator.comparing(Member::getTimeBoosted)).collect(Helpers.toUnmodifiableList()));
   }

   public int getMaxMembers() {
      return this.maxMembers;
   }

   public int getMaxPresences() {
      return this.maxPresences;
   }

   @Nonnull
   public RestAction<Guild.MetaData> retrieveMetaData() {
      Route.CompiledRoute route = Route.Guilds.GET_GUILD.compile(this.getId());
      route = route.withQueryParams("with_counts", "true");
      return new RestActionImpl<Guild.MetaData>(this.getJDA(), route, (response, request) -> {
         DataObject json = response.getObject();
         int memberLimit = json.getInt("max_members", 0);
         int presenceLimit = json.getInt("max_presences", 5000);
         this.maxMembers = memberLimit;
         this.maxPresences = presenceLimit;
         int approxMembers = json.getInt("approximate_member_count", this.memberCount);
         int approxPresence = json.getInt("approximate_presence_count", 0);
         return new Guild.MetaData(memberLimit, presenceLimit, approxPresence, approxMembers);
      });
   }

   public VoiceChannel getAfkChannel() {
      return this.afkChannel;
   }

   public TextChannel getSystemChannel() {
      return this.systemChannel;
   }

   public TextChannel getRulesChannel() {
      return this.rulesChannel;
   }

   @Nonnull
   public RestAction<List<ScheduledEvent>> retrieveScheduledEvents(boolean includeUserCount) {
      Route.CompiledRoute route = Route.Guilds.GET_SCHEDULED_EVENTS.compile(this.getId()).withQueryParams("with_user_count", String.valueOf(includeUserCount));
      EntityBuilder entityBuilder = this.getJDA().getEntityBuilder();
      return new RestActionImpl<List<ScheduledEvent>>(this.getJDA(), route, (response, request) -> (List)Helpers.mapGracefully(response.getArray().stream(DataArray::getObject), (data) -> entityBuilder.createScheduledEvent(this, data), "Failed to parse scheduled event").collect(Helpers.toUnmodifiableList()));
   }

   @Nonnull
   public CacheRestAction<ScheduledEvent> retrieveScheduledEventById(@Nonnull String id) {
      Checks.isSnowflake(id);
      return new DeferredRestAction(this.getJDA(), ScheduledEvent.class, () -> this.getScheduledEventById(id), () -> {
         Route.CompiledRoute route = Route.Guilds.GET_SCHEDULED_EVENT.compile(this.getId(), id);
         return new RestActionImpl(this.getJDA(), route, (response, request) -> this.api.getEntityBuilder().createScheduledEvent(this, response.getObject()));
      });
   }

   @Nonnull
   public CacheRestAction<ScheduledEvent> retrieveScheduledEventById(long id) {
      return this.retrieveScheduledEventById(Long.toUnsignedString(id));
   }

   @Nonnull
   public ScheduledEventAction createScheduledEvent(@Nonnull String name, @Nonnull String location, @Nonnull OffsetDateTime startTime, @Nonnull OffsetDateTime endTime) {
      this.checkPermission(Permission.CREATE_SCHEDULED_EVENTS);
      return new ScheduledEventActionImpl(name, location, startTime, endTime, this);
   }

   @Nonnull
   public ScheduledEventAction createScheduledEvent(@Nonnull String name, @Nonnull GuildChannel channel, @Nonnull OffsetDateTime startTime) {
      this.checkPermission(Permission.CREATE_SCHEDULED_EVENTS);
      return new ScheduledEventActionImpl(name, channel, startTime, this);
   }

   public TextChannel getCommunityUpdatesChannel() {
      return this.communityUpdatesChannel;
   }

   @Nullable
   public TextChannel getSafetyAlertsChannel() {
      return this.safetyAlertsChannel;
   }

   @Nonnull
   public RestAction<List<Webhook>> retrieveWebhooks() {
      if (!this.getSelfMember().hasPermission(new Permission[]{Permission.MANAGE_WEBHOOKS})) {
         throw new InsufficientPermissionException(this, Permission.MANAGE_WEBHOOKS);
      } else {
         Route.CompiledRoute route = Route.Guilds.GET_WEBHOOKS.compile(this.getId());
         return new RestActionImpl<List<Webhook>>(this.getJDA(), route, (response, request) -> {
            DataArray array = response.getArray();
            List<Webhook> webhooks = new ArrayList(array.length());
            EntityBuilder builder = this.api.getEntityBuilder();

            for(int i = 0; i < array.length(); ++i) {
               try {
                  webhooks.add(builder.createWebhook(array.getObject(i)));
               } catch (Exception e) {
                  JDAImpl.LOG.error((String)"Error creating webhook from json", (Throwable)e);
               }
            }

            return Collections.unmodifiableList(webhooks);
         });
      }
   }

   public Member getOwner() {
      return this.owner;
   }

   public long getOwnerIdLong() {
      return this.ownerId;
   }

   @Nonnull
   public Guild.Timeout getAfkTimeout() {
      return this.afkTimeout;
   }

   @Nonnull
   public SecurityIncidentActions getSecurityIncidentActions() {
      return this.securityIncidentActions;
   }

   @Nonnull
   public SecurityIncidentDetections getSecurityIncidentDetections() {
      return this.securityIncidentDetections;
   }

   public boolean isMember(@Nonnull UserSnowflake user) {
      return this.memberCache.get(user.getIdLong()) != null;
   }

   @Nonnull
   public SelfMember getSelfMember() {
      Member member = this.getMember(this.getJDA().getSelfUser());
      if (member == null) {
         throw new IllegalStateException("Guild does not have a self member");
      } else {
         return (SelfMember)member;
      }
   }

   public Member getMember(@Nonnull UserSnowflake user) {
      Checks.notNull(user, "User");
      return this.getMemberById(user.getIdLong());
   }

   @Nonnull
   public MemberCacheView getMemberCache() {
      return this.memberCache;
   }

   @Nonnull
   public SortedSnowflakeCacheView<ScheduledEvent> getScheduledEventCache() {
      return this.scheduledEventCache;
   }

   @Nonnull
   public SortedSnowflakeCacheView<Category> getCategoryCache() {
      return this.channelCache.ofType(Category.class);
   }

   @Nonnull
   public SortedSnowflakeCacheView<TextChannel> getTextChannelCache() {
      return this.channelCache.ofType(TextChannel.class);
   }

   @Nonnull
   public SortedSnowflakeCacheView<NewsChannel> getNewsChannelCache() {
      return this.channelCache.ofType(NewsChannel.class);
   }

   @Nonnull
   public SortedSnowflakeCacheView<VoiceChannel> getVoiceChannelCache() {
      return this.channelCache.ofType(VoiceChannel.class);
   }

   @Nonnull
   public SortedSnowflakeCacheView<ForumChannel> getForumChannelCache() {
      return this.channelCache.ofType(ForumChannel.class);
   }

   @Nonnull
   public SnowflakeCacheView<MediaChannel> getMediaChannelCache() {
      return this.channelCache.ofType(MediaChannel.class);
   }

   @Nonnull
   public SortedSnowflakeCacheView<StageChannel> getStageChannelCache() {
      return this.channelCache.ofType(StageChannel.class);
   }

   @Nonnull
   public SortedSnowflakeCacheView<ThreadChannel> getThreadChannelCache() {
      return this.channelCache.ofType(ThreadChannel.class);
   }

   @Nonnull
   public SortedChannelCacheViewImpl<GuildChannel> getChannelCache() {
      return this.channelCache;
   }

   @Nullable
   public GuildChannel getGuildChannelById(long id) {
      return (GuildChannel)this.channelCache.getElementById(id);
   }

   public GuildChannel getGuildChannelById(@Nonnull ChannelType type, long id) {
      return (GuildChannel)this.channelCache.getElementById(type, id);
   }

   @Nonnull
   public SortedSnowflakeCacheView<Role> getRoleCache() {
      return this.roleCache;
   }

   @Nonnull
   public SnowflakeCacheView<RichCustomEmoji> getEmojiCache() {
      return this.emojicache;
   }

   @Nonnull
   public SnowflakeCacheView<GuildSticker> getStickerCache() {
      return this.stickerCache;
   }

   @Nonnull
   public SnowflakeCacheViewImpl<SoundboardSound> getSoundboardSoundCache() {
      return this.soundboardCache;
   }

   @Nonnull
   public List<GuildChannel> getChannels(boolean includeHidden) {
      if (includeHidden) {
         return (List)this.channelCache.applyStream((stream) -> (List)stream.filter((it) -> !it.getType().isThread()).sorted().collect(Helpers.toUnmodifiableList()));
      } else {
         Member self = this.getSelfMember();
         SortedSet<GuildChannel> channels = new TreeSet();
         this.channelCache.ofType(ICategorizableChannel.class).forEachUnordered((channel) -> {
            if (!channel.getType().isThread() && self.hasPermission(channel, new Permission[]{Permission.VIEW_CHANNEL})) {
               Category category = channel.getParentCategory();
               channels.add(channel);
               if (category != null) {
                  channels.add(category);
               }

            }
         });
         return Collections.unmodifiableList(new ArrayList(channels));
      }
   }

   @Nonnull
   public RestAction<List<RichCustomEmoji>> retrieveEmojis() {
      Route.CompiledRoute route = Route.Emojis.GET_EMOJIS.compile(this.getId());
      return new RestActionImpl<List<RichCustomEmoji>>(this.getJDA(), route, (response, request) -> {
         EntityBuilder builder = this.getJDA().getEntityBuilder();
         DataArray emojis = response.getArray();
         List<RichCustomEmoji> list = new ArrayList(emojis.length());

         for(int i = 0; i < emojis.length(); ++i) {
            DataObject emoji = emojis.getObject(i);
            list.add(builder.createEmoji(this, emoji));
         }

         return Collections.unmodifiableList(list);
      });
   }

   @Nonnull
   public RestAction<RichCustomEmoji> retrieveEmojiById(@Nonnull String id) {
      Checks.isSnowflake(id, "Emoji ID");
      JDAImpl jda = this.getJDA();
      return new DeferredRestAction(jda, RichCustomEmoji.class, () -> {
         RichCustomEmoji emoji = this.getEmojiById(id);
         return emoji == null || emoji.getOwner() == null && this.getSelfMember().hasPermission(new Permission[]{Permission.MANAGE_GUILD_EXPRESSIONS}) ? null : emoji;
      }, () -> {
         Route.CompiledRoute route = Route.Emojis.GET_EMOJI.compile(this.getId(), id);
         return new AuditableRestActionImpl(jda, route, (response, request) -> {
            EntityBuilder builder = this.getJDA().getEntityBuilder();
            return builder.createEmoji(this, response.getObject());
         });
      });
   }

   @Nonnull
   public RestAction<List<GuildSticker>> retrieveStickers() {
      Route.CompiledRoute route = Route.Stickers.GET_GUILD_STICKERS.compile(this.getId());
      return new RestActionImpl<List<GuildSticker>>(this.getJDA(), route, (response, request) -> {
         DataArray array = response.getArray();
         List<GuildSticker> stickers = new ArrayList(array.length());
         EntityBuilder builder = this.api.getEntityBuilder();

         for(int i = 0; i < array.length(); ++i) {
            DataObject object = null;

            try {
               object = array.getObject(i);
               GuildSticker sticker = (GuildSticker)builder.createRichSticker(object);
               stickers.add(sticker);
            } catch (ClassCastException | ParsingException ex) {
               EntityBuilder.LOG.error((String)"Failed to parse sticker for JSON: {}", (Object)object, (Object)ex);
            }
         }

         return Collections.unmodifiableList(stickers);
      });
   }

   @Nonnull
   public RestAction<GuildSticker> retrieveSticker(@Nonnull StickerSnowflake sticker) {
      Checks.notNull(sticker, "Sticker");
      Route.CompiledRoute route = Route.Stickers.GET_GUILD_STICKER.compile(this.getId(), sticker.getId());
      return new RestActionImpl<GuildSticker>(this.getJDA(), route, (response, request) -> {
         DataObject object = response.getObject();
         EntityBuilder builder = this.api.getEntityBuilder();
         return (GuildSticker)builder.createRichSticker(object);
      });
   }

   @Nonnull
   public GuildStickerManager editSticker(@Nonnull StickerSnowflake sticker) {
      Checks.notNull(sticker, "Sticker");
      if (sticker instanceof GuildSticker) {
         Checks.check(((GuildSticker)sticker).getGuildIdLong() == this.id, "Cannot edit a sticker from another guild!");
      }

      Checks.check(!(sticker instanceof StandardSticker), "Cannot edit a standard sticker.");
      return new GuildStickerManagerImpl(this, this.id, sticker);
   }

   @Nonnull
   public CacheRestAction<List<SoundboardSound>> retrieveSoundboardSounds() {
      return new DeferredRestAction(this.api, List.class, () -> this.api.isCacheFlagSet(CacheFlag.SOUNDBOARD_SOUNDS) ? this.getSoundboardSounds() : null, this::retrieveSoundboardSounds0);
   }

   private RestAction<List> retrieveSoundboardSounds0() {
      return new RestActionImpl<List>(this.api, Route.SoundboardSounds.LIST_GUILD_SOUNDBOARD_SOUNDS.compile(this.getId()), (response, request) -> (List)Helpers.mapGracefully(response.getArray().stream(DataArray::getObject), (o) -> this.api.getEntityBuilder().createSoundboardSound(o), "Failed to parse soundboard sound").collect(Helpers.toUnmodifiableList()));
   }

   @Nonnull
   public CacheRestAction<SoundboardSound> retrieveSoundboardSound(@Nonnull SoundboardSoundSnowflake sound) {
      Checks.notNull(sound, "Sound");
      return new DeferredRestAction(this.api, SoundboardSound.class, () -> this.getSoundboardSoundById(sound.getIdLong()), () -> new RestActionImpl(this.api, Route.SoundboardSounds.GET_GUILD_SOUNDBOARD_SOUND.compile(this.getId(), sound.getId()), (response, request) -> this.api.getEntityBuilder().createSoundboardSound(response.getObject())));
   }

   @Nonnull
   public SoundboardSoundManager editSoundboardSound(@Nonnull SoundboardSoundSnowflake sound) {
      Checks.notNull(sound, "Sound");
      this.checkPermission(Permission.MANAGE_GUILD_EXPRESSIONS);
      return new SoundboardSoundManagerImpl(this, sound);
   }

   @Nonnull
   public BanPaginationActionImpl retrieveBanList() {
      if (!this.getSelfMember().hasPermission(new Permission[]{Permission.BAN_MEMBERS})) {
         throw new InsufficientPermissionException(this, Permission.BAN_MEMBERS);
      } else {
         return new BanPaginationActionImpl(this);
      }
   }

   @Nonnull
   public RestAction<Guild.Ban> retrieveBan(@Nonnull UserSnowflake user) {
      if (!this.getSelfMember().hasPermission(new Permission[]{Permission.BAN_MEMBERS})) {
         throw new InsufficientPermissionException(this, Permission.BAN_MEMBERS);
      } else {
         Checks.notNull(user, "User");
         Route.CompiledRoute route = Route.Guilds.GET_BAN.compile(this.getId(), user.getId());
         return new RestActionImpl<Guild.Ban>(this.getJDA(), route, (response, request) -> {
            EntityBuilder builder = this.api.getEntityBuilder();
            DataObject bannedObj = response.getObject();
            DataObject userJson = bannedObj.getObject("user");
            return new Guild.Ban(builder.createUser(userJson), bannedObj.getString("reason", (String)null));
         });
      }
   }

   @Nonnull
   public RestAction<Integer> retrievePrunableMemberCount(int days) {
      if (!this.getSelfMember().hasPermission(new Permission[]{Permission.KICK_MEMBERS})) {
         throw new InsufficientPermissionException(this, Permission.KICK_MEMBERS);
      } else {
         Checks.check(days >= 1 && days <= 30, "Provided %d days must be between 1 and 30.", (Object)days);
         Route.CompiledRoute route = Route.Guilds.PRUNABLE_COUNT.compile(this.getId()).withQueryParams("days", Integer.toString(days));
         return new RestActionImpl<Integer>(this.getJDA(), route, (response, request) -> response.getObject().getInt("pruned"));
      }
   }

   @Nonnull
   public Role getPublicRole() {
      return this.publicRole;
   }

   @Nullable
   public DefaultGuildChannelUnion getDefaultChannel() {
      Role role = this.getPublicRole();
      return (DefaultGuildChannelUnion)Stream.concat(this.getTextChannelCache().stream(), this.getNewsChannelCache().stream()).filter((c) -> role.hasPermission(c, new Permission[]{Permission.VIEW_CHANNEL})).min(Comparator.naturalOrder()).orElse((Object)null);
   }

   @Nonnull
   public GuildManager getManager() {
      return new GuildManagerImpl(this);
   }

   public boolean isBoostProgressBarEnabled() {
      return this.boostProgressBarEnabled;
   }

   @Nonnull
   public AuditLogPaginationAction retrieveAuditLogs() {
      return new AuditLogPaginationActionImpl(this);
   }

   @Nonnull
   public RestAction<Void> leave() {
      Route.CompiledRoute route = Route.Self.LEAVE_GUILD.compile(this.getId());
      return new RestActionImpl<Void>(this.getJDA(), route);
   }

   @Nonnull
   public AudioManager getAudioManager() {
      if (!this.getJDA().isIntent(GatewayIntent.GUILD_VOICE_STATES)) {
         throw new IllegalStateException("Cannot use audio features with disabled GUILD_VOICE_STATES intent!");
      } else {
         AbstractCacheView<AudioManager> managerMap = this.getJDA().getAudioManagersView();
         AudioManager mng = managerMap.get(this.id);
         if (mng == null) {
            UnlockHook hook = managerMap.writeLock();

            try {
               GuildImpl cachedGuild = (GuildImpl)this.getJDA().getGuildById(this.id);
               if (cachedGuild == null) {
                  throw new IllegalStateException("Cannot get an AudioManager instance on an uncached Guild");
               }

               mng = managerMap.get(this.id);
               if (mng == null) {
                  mng = new AudioManagerImpl(cachedGuild);
                  managerMap.getMap().put(this.id, mng);
               }
            } catch (Throwable var7) {
               if (hook != null) {
                  try {
                     hook.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (hook != null) {
               hook.close();
            }
         }

         return mng;
      }
   }

   @Nonnull
   public synchronized Task<Void> requestToSpeak() {
      if (!this.isRequestToSpeakPending()) {
         this.pendingRequestToSpeak = new CompletableFuture();
      }

      Task<Void> task = new GatewayTask<Void>(this.pendingRequestToSpeak, this::cancelRequestToSpeak);
      this.updateRequestToSpeak();
      return task;
   }

   @Nonnull
   public synchronized Task<Void> cancelRequestToSpeak() {
      if (this.isRequestToSpeakPending()) {
         this.pendingRequestToSpeak.cancel(false);
         this.pendingRequestToSpeak = null;
      }

      AudioChannel channel = this.getSelfMember().getVoiceState().getChannel();
      if (channel instanceof StageChannel) {
         CompletableFuture<Void> future = ((StageChannel)channel).cancelRequestToSpeak().submit();
         return new GatewayTask<Void>(future, () -> future.cancel(false));
      } else {
         return new GatewayTask<Void>(CompletableFuture.completedFuture((Object)null), () -> {
         });
      }
   }

   @Nonnull
   public JDAImpl getJDA() {
      return this.api;
   }

   @Nonnull
   public List<GuildVoiceState> getVoiceStates() {
      return (List)this.voiceStateCache.applyStream((stream) -> (List)stream.collect(Helpers.toUnmodifiableList()));
   }

   @Nonnull
   @CheckReturnValue
   public CacheRestAction<GuildVoiceState> retrieveMemberVoiceStateById(long id) {
      JDAImpl jda = this.getJDA();
      Route.CompiledRoute route = Route.Guilds.GET_VOICE_STATE.compile(this.getId(), Long.toUnsignedString(id));
      return new DeferredRestAction(jda, GuildVoiceState.class, () -> (GuildVoiceState)this.voiceStateCache.get(id), () -> new RestActionImpl(jda, route, (response, request) -> {
            EntityBuilder entityBuilder = jda.getEntityBuilder();
            DataObject voiceStateData = response.getObject();
            MemberImpl member = entityBuilder.createMember(this, voiceStateData.getObject("member"), (DataObject)null, (DataObject)null);
            entityBuilder.updateMemberCache(member);
            return entityBuilder.createGuildVoiceState(member, voiceStateData);
         }));
   }

   @Nonnull
   public Guild.VerificationLevel getVerificationLevel() {
      return this.verificationLevel;
   }

   @Nonnull
   public Guild.NotificationLevel getDefaultNotificationLevel() {
      return this.defaultNotificationLevel;
   }

   @Nonnull
   public Guild.MFALevel getRequiredMFALevel() {
      return this.mfaLevel;
   }

   @Nonnull
   public Guild.ExplicitContentLevel getExplicitContentLevel() {
      return this.explicitContentLevel;
   }

   @Nonnull
   public Task<Void> loadMembers(@Nonnull Consumer<Member> callback) {
      Checks.notNull(callback, "Callback");
      if (!this.getJDA().isIntent(GatewayIntent.GUILD_MEMBERS)) {
         throw new IllegalStateException("Cannot use loadMembers without GatewayIntent.GUILD_MEMBERS!");
      } else if (this.isLoaded()) {
         this.memberCache.forEachUnordered(callback);
         return new GatewayTask<Void>(CompletableFuture.completedFuture((Object)null), () -> {
         });
      } else {
         MemberChunkManager chunkManager = this.getJDA().getClient().getChunkManager();
         boolean includePresences = this.getJDA().isIntent(GatewayIntent.GUILD_PRESENCES);
         MemberChunkManager.ChunkRequest handler = chunkManager.chunkGuild(this, includePresences, (last, list) -> list.forEach(callback));
         handler.exceptionally((ex) -> {
            WebSocketClient.LOG.error("Encountered exception trying to handle member chunk response", ex);
            return null;
         });
         GatewayTask var10000 = new GatewayTask(handler, () -> handler.cancel(false));
         Objects.requireNonNull(handler);
         return var10000.onSetTimeout(handler::setTimeout);
      }
   }

   @Nonnull
   public CacheRestAction<Member> retrieveMemberById(long id) {
      JDAImpl jda = this.getJDA();
      return (new DeferredRestAction(jda, Member.class, () -> this.getMemberById(id), () -> {
         if (id == jda.getSelfUser().getIdLong()) {
            return new CompletedRestAction(jda, this.getSelfMember());
         } else {
            Route.CompiledRoute route = Route.Guilds.GET_MEMBER.compile(this.getId(), Long.toUnsignedString(id));
            return new RestActionImpl(jda, route, (resp, req) -> {
               MemberImpl member = jda.getEntityBuilder().createMember(this, resp.getObject());
               jda.getEntityBuilder().updateMemberCache(member);
               return member;
            });
         }
      })).useCache(jda.isIntent(GatewayIntent.GUILD_MEMBERS));
   }

   @Nonnull
   public Task<List<Member>> retrieveMembersByIds(boolean includePresence, @Nonnull long... ids) {
      Checks.notNull(ids, "ID Array");
      Checks.check(!includePresence || this.api.isIntent(GatewayIntent.GUILD_PRESENCES), "Cannot retrieve presences of members without GUILD_PRESENCES intent!");
      if (ids.length == 0) {
         return new GatewayTask<List<Member>>(CompletableFuture.completedFuture(Collections.emptyList()), () -> {
         });
      } else {
         Checks.check(ids.length <= 100, "You can only request 100 members at once");
         MemberChunkManager chunkManager = this.api.getClient().getChunkManager();
         List<Member> collect = new ArrayList(ids.length);
         CompletableFuture<List<Member>> result = new CompletableFuture();
         MemberChunkManager.ChunkRequest handle = chunkManager.chunkGuild(this, includePresence, ids, (last, list) -> {
            collect.addAll(list);
            if (last) {
               result.complete(collect);
            }

         });
         handle.exceptionally((ex) -> {
            WebSocketClient.LOG.error("Encountered exception trying to handle member chunk response", ex);
            result.completeExceptionally(ex);
            return null;
         });
         GatewayTask var10000 = new GatewayTask(result, () -> handle.cancel(false));
         Objects.requireNonNull(handle);
         return var10000.onSetTimeout(handle::setTimeout);
      }
   }

   @Nonnull
   @CheckReturnValue
   public Task<List<Member>> retrieveMembersByPrefix(@Nonnull String prefix, int limit) {
      Checks.notEmpty(prefix, "Prefix");
      Checks.positive(limit, "Limit");
      Checks.check(limit <= 100, "Limit must not be greater than 100");
      MemberChunkManager chunkManager = this.api.getClient().getChunkManager();
      List<Member> collect = new ArrayList(limit);
      CompletableFuture<List<Member>> result = new CompletableFuture();
      MemberChunkManager.ChunkRequest handle = chunkManager.chunkGuild(this, prefix, limit, (last, list) -> {
         collect.addAll(list);
         if (last) {
            result.complete(collect);
         }

      });
      handle.exceptionally((ex) -> {
         WebSocketClient.LOG.error("Encountered exception trying to handle member chunk response", ex);
         result.completeExceptionally(ex);
         return null;
      });
      GatewayTask var10000 = new GatewayTask(result, () -> handle.cancel(false));
      Objects.requireNonNull(handle);
      return var10000.onSetTimeout(handle::setTimeout);
   }

   @Nonnull
   public RestAction<List<ThreadChannel>> retrieveActiveThreads() {
      Route.CompiledRoute route = Route.Guilds.LIST_ACTIVE_THREADS.compile(this.getId());
      return new RestActionImpl<List<ThreadChannel>>(this.api, route, (response, request) -> {
         DataObject obj = response.getObject();
         DataArray selfThreadMembers = obj.getArray("members");
         DataArray threads = obj.getArray("threads");
         List<ThreadChannel> list = new ArrayList(threads.length());
         EntityBuilder builder = this.api.getEntityBuilder();
         TLongObjectMap<DataObject> selfThreadMemberMap = new TLongObjectHashMap<DataObject>();

         for(int i = 0; i < selfThreadMembers.length(); ++i) {
            DataObject selfThreadMember = selfThreadMembers.getObject(i);
            selfThreadMemberMap.put(selfThreadMember.getLong("id"), selfThreadMember);
         }

         for(int i = 0; i < threads.length(); ++i) {
            DataObject threadObj = threads.getObject(i);
            DataObject selfThreadMemberObj = selfThreadMemberMap.get(threadObj.getLong("id", 0L));
            if (selfThreadMemberObj != null) {
               threadObj.put("member", selfThreadMemberObj);
            }

            try {
               ThreadChannel thread = builder.createThreadChannel(threadObj, this.getIdLong());
               list.add(thread);
            } catch (Exception e) {
               if ("MISSING_CHANNEL".equals(e.getMessage())) {
                  EntityBuilder.LOG.debug((String)"Discarding thread without cached parent channel. JSON: {}", (Object)threadObj);
               } else {
                  EntityBuilder.LOG.warn((String)"Failed to create thread channel. JSON: {}", (Object)threadObj, (Object)e);
               }
            }
         }

         return Collections.unmodifiableList(list);
      });
   }

   public long getIdLong() {
      return this.id;
   }

   @Nonnull
   public RestAction<List<Invite>> retrieveInvites() {
      if (!this.getSelfMember().hasPermission(new Permission[]{Permission.MANAGE_SERVER})) {
         throw new InsufficientPermissionException(this, Permission.MANAGE_SERVER);
      } else {
         Route.CompiledRoute route = Route.Invites.GET_GUILD_INVITES.compile(this.getId());
         return new RestActionImpl<List<Invite>>(this.getJDA(), route, (response, request) -> {
            EntityBuilder entityBuilder = this.api.getEntityBuilder();
            DataArray array = response.getArray();
            List<Invite> invites = new ArrayList(array.length());

            for(int i = 0; i < array.length(); ++i) {
               invites.add(entityBuilder.createInvite(array.getObject(i)));
            }

            return Collections.unmodifiableList(invites);
         });
      }
   }

   @Nonnull
   public RestAction<List<Template>> retrieveTemplates() {
      if (!this.getSelfMember().hasPermission(new Permission[]{Permission.MANAGE_SERVER})) {
         throw new InsufficientPermissionException(this, Permission.MANAGE_SERVER);
      } else {
         Route.CompiledRoute route = Route.Templates.GET_GUILD_TEMPLATES.compile(this.getId());
         return new RestActionImpl<List<Template>>(this.getJDA(), route, (response, request) -> {
            EntityBuilder entityBuilder = this.api.getEntityBuilder();
            DataArray array = response.getArray();
            List<Template> templates = new ArrayList(array.length());

            for(int i = 0; i < array.length(); ++i) {
               try {
                  templates.add(entityBuilder.createTemplate(array.getObject(i)));
               } catch (Exception e) {
                  JDAImpl.LOG.error((String)"Error creating template from json", (Throwable)e);
               }
            }

            return Collections.unmodifiableList(templates);
         });
      }
   }

   @Nonnull
   public RestAction<Template> createTemplate(@Nonnull String name, @Nullable String description) {
      this.checkPermission(Permission.MANAGE_SERVER);
      Checks.notBlank(name, "Name");
      name = name.trim();
      Checks.notLonger(name, 100, "Name");
      if (description != null) {
         Checks.notLonger(description, 120, "Description");
      }

      Route.CompiledRoute route = Route.Templates.CREATE_TEMPLATE.compile(this.getId());
      DataObject object = DataObject.empty();
      object.put("name", name);
      object.put("description", description);
      return new RestActionImpl<Template>(this.getJDA(), route, object, (response, request) -> {
         EntityBuilder entityBuilder = this.api.getEntityBuilder();
         return entityBuilder.createTemplate(response.getObject());
      });
   }

   @Nonnull
   public RestAction<GuildWelcomeScreen> retrieveWelcomeScreen() {
      Route.CompiledRoute route = Route.Guilds.GET_WELCOME_SCREEN.compile(this.getId());
      return new RestActionImpl<GuildWelcomeScreen>(this.getJDA(), route, (response, request) -> {
         EntityBuilder entityBuilder = this.api.getEntityBuilder();
         return entityBuilder.createWelcomeScreen(this, response.getObject());
      });
   }

   @Nonnull
   public RestAction<Void> moveVoiceMember(@Nonnull UserSnowflake user, @Nullable AudioChannel audioChannel) {
      Checks.notNull(user, "User");
      Member member = user instanceof Member ? (Member)user : this.getMember(user);
      if (member != null) {
         this.checkGuild(member.getGuild(), "Member");
      }

      if (audioChannel != null) {
         this.checkGuild(audioChannel.getGuild(), "AudioChannel");
      }

      if (!this.getJDA().isCacheFlagSet(CacheFlag.VOICE_STATE)) {
         throw new IllegalStateException("Cannot move a Member with disabled CacheFlag.VOICE_STATE");
      } else {
         GuildVoiceState vState = this.voiceStateCache.getElementById(user.getIdLong());
         if (vState == null) {
            throw new IllegalStateException("You cannot move a Member who isn't in an AudioChannel!");
         } else {
            AudioChannel channel = vState.getChannel();
            if (channel == null) {
               throw new IllegalStateException("You cannot move a Member who isn't in an AudioChannel!");
            } else {
               Member selfMember = this.getSelfMember();
               if (!selfMember.hasPermission(channel, new Permission[]{Permission.VOICE_MOVE_OTHERS})) {
                  throw new InsufficientPermissionException(channel, Permission.VOICE_MOVE_OTHERS, "This account does not have Permission to MOVE_OTHERS out of the channel that the Member is currently in.");
               } else if (member != null && audioChannel != null && !selfMember.hasPermission(audioChannel, new Permission[]{Permission.VOICE_CONNECT}) && !member.hasPermission(audioChannel, new Permission[]{Permission.VOICE_CONNECT})) {
                  throw new InsufficientPermissionException(audioChannel, Permission.VOICE_CONNECT, "Neither this account nor the Member that is attempting to be moved have the VOICE_CONNECT permission for the destination AudioChannel, so the move cannot be done.");
               } else {
                  DataObject body = DataObject.empty().put("channel_id", audioChannel == null ? null : audioChannel.getId());
                  Route.CompiledRoute route = Route.Guilds.MODIFY_MEMBER.compile(this.getId(), user.getId());
                  return new RestActionImpl<Void>(this.getJDA(), route, body);
               }
            }
         }
      }
   }

   @Nonnull
   public AuditableRestAction<Void> modifyNickname(@Nonnull Member member, String nickname) {
      Checks.notNull(member, "Member");
      this.checkGuild(member.getGuild(), "Member");
      if (member.equals(this.getSelfMember())) {
         if (!member.hasPermission(new Permission[]{Permission.NICKNAME_CHANGE}) && !member.hasPermission(new Permission[]{Permission.NICKNAME_MANAGE})) {
            throw new InsufficientPermissionException(this, Permission.NICKNAME_CHANGE, "You neither have NICKNAME_CHANGE nor NICKNAME_MANAGE permission!");
         }
      } else {
         this.checkPermission(Permission.NICKNAME_MANAGE);
         this.checkPosition((UserSnowflake)member);
      }

      JDAImpl jda = this.getJDA();
      return (new DeferredRestAction(jda, () -> {
         DataObject body = DataObject.empty().put("nick", nickname == null ? "" : nickname);
         Route.CompiledRoute route;
         if (member.equals(this.getSelfMember())) {
            route = Route.Guilds.MODIFY_SELF.compile(this.getId());
         } else {
            route = Route.Guilds.MODIFY_MEMBER.compile(this.getId(), member.getUser().getId());
         }

         return new AuditableRestActionImpl(jda, route, body);
      })).setCacheCheck(() -> !Objects.equals(nickname, member.getNickname()));
   }

   @Nonnull
   public AuditableRestAction<Integer> prune(int days, boolean wait, @Nonnull Role... roles) {
      this.checkPermission(Permission.KICK_MEMBERS);
      Checks.check(days >= 1 && days <= 30, "Provided %d days must be between 1 and 30.", (Object)days);
      Checks.notNull(roles, "Roles");
      Route.CompiledRoute route = Route.Guilds.PRUNE_MEMBERS.compile(this.getId());
      DataObject body = DataObject.empty();
      body.put("days", days);
      if (!wait) {
         body.put("compute_prune_count", false);
      }

      if (roles.length != 0) {
         for(Role role : roles) {
            Checks.notNull(role, "Role");
            Checks.check(role.getGuild().equals(this), "Role is not from the same guild!");
         }

         body.put("include_roles", Arrays.stream(roles).map(ISnowflake::getId).collect(Collectors.toList()));
      }

      return new AuditableRestActionImpl<Integer>(this.getJDA(), route, body, (response, request) -> response.getObject().getInt("pruned", 0));
   }

   @Nonnull
   public AuditableRestAction<Void> modifySecurityIncidents(@Nonnull SecurityIncidentActions incidents) {
      Checks.notNull(incidents, "SecurityIncidentActions");
      this.checkPermission(Permission.MANAGE_SERVER);
      Route.CompiledRoute route = Route.Guilds.MODIFY_GUILD_INCIDENTS.compile(this.getId());
      DataObject body = DataObject.empty().put("invites_disabled_until", Objects.toString(incidents.getInvitesDisabledUntil(), (String)null)).put("dms_disabled_until", Objects.toString(incidents.getDirectMessagesDisabledUntil(), (String)null));
      return new AuditableRestActionImpl<Void>(this.api, route, body);
   }

   @Nonnull
   public AuditableRestAction<Void> kick(@Nonnull UserSnowflake user) {
      Checks.notNull(user, "User");
      this.checkPermission(Permission.KICK_MEMBERS);
      this.checkOwner(user.getIdLong(), "kick");
      this.checkPosition(user);
      Route.CompiledRoute route = Route.Guilds.KICK_MEMBER.compile(this.getId(), user.getId());
      return new AuditableRestActionImpl<Void>(this.getJDA(), route);
   }

   @Nonnull
   public AuditableRestAction<Void> ban(@Nonnull UserSnowflake user, int duration, @Nonnull TimeUnit unit) {
      Checks.notNull(user, "User");
      Checks.notNull(unit, "TimeUnit");
      Checks.notNegative(duration, "Deletion Timeframe");
      Checks.check(unit.toDays((long)duration) <= 7L, "Deletion timeframe must not be larger than 7 days");
      this.checkPermission(Permission.BAN_MEMBERS);
      this.checkOwner(user.getIdLong(), "ban");
      this.checkPosition(user);
      Route.CompiledRoute route = Route.Guilds.BAN.compile(this.getId(), user.getId());
      DataObject params = DataObject.empty();
      if (duration > 0) {
         params.put("delete_message_seconds", unit.toSeconds((long)duration));
      }

      return new AuditableRestActionImpl<Void>(this.getJDA(), route, params);
   }

   @Nonnull
   public AuditableRestAction<BulkBanResponse> ban(@Nonnull Collection<? extends UserSnowflake> users, @Nullable Duration deletionTime) {
      deletionTime = deletionTime == null ? Duration.ZERO : deletionTime;
      Checks.noneNull(users, "Users");
      Checks.notNegative(deletionTime.getSeconds(), "Deletion timeframe");
      Checks.check(deletionTime.getSeconds() <= TimeUnit.DAYS.toSeconds(7L), "Deletion timeframe must not be larger than 7 days. Provided: %d seconds", (Object)deletionTime.getSeconds());
      Checks.check(users.size() <= 200, "Cannot ban more than 200 users at once");
      this.checkPermission(Permission.BAN_MEMBERS);
      this.checkPermission(Permission.MANAGE_SERVER);

      for(UserSnowflake user : users) {
         this.checkOwner(user.getIdLong(), "ban");
         this.checkPosition(user);
      }

      Set<Long> userIds = (Set)users.stream().map(ISnowflake::getIdLong).collect(Collectors.toSet());
      DataObject body = DataObject.empty().put("user_ids", DataArray.fromCollection(userIds)).put("delete_message_seconds", deletionTime.getSeconds());
      Route.CompiledRoute route = Route.Guilds.BULK_BAN.compile(this.getId());
      return new AuditableRestActionImpl<BulkBanResponse>(this.getJDA(), route, body, (res, req) -> {
         DataObject responseBody = res.getObject();
         List<UserSnowflake> bannedUsers = (List)responseBody.getArray("banned_users").stream(DataArray::getLong).map(UserSnowflake::fromId).collect(Collectors.toList());
         List<UserSnowflake> failedUsers = (List)responseBody.getArray("failed_users").stream(DataArray::getLong).map(UserSnowflake::fromId).collect(Collectors.toList());
         return new BulkBanResponse(bannedUsers, failedUsers);
      });
   }

   @Nonnull
   public AuditableRestAction<Void> unban(@Nonnull UserSnowflake user) {
      Checks.notNull(user, "User");
      this.checkPermission(Permission.BAN_MEMBERS);
      Route.CompiledRoute route = Route.Guilds.UNBAN.compile(this.getId(), user.getId());
      return new AuditableRestActionImpl<Void>(this.getJDA(), route);
   }

   @Nonnull
   public AuditableRestAction<Void> timeoutUntil(@Nonnull UserSnowflake user, @Nonnull TemporalAccessor temporal) {
      Checks.notNull(user, "User");
      Checks.notNull(temporal, "Temporal");
      OffsetDateTime date = Helpers.toOffsetDateTime(temporal);
      Checks.check(date.isAfter(OffsetDateTime.now()), "Cannot put a member in time out with date in the past. Provided: %s", (Object)date);
      Checks.check(date.isBefore(OffsetDateTime.now().plusDays(28L)), "Cannot put a member in time out for more than 28 days. Provided: %s", (Object)date);
      this.checkPermission(Permission.MODERATE_MEMBERS);
      this.checkOwner(user.getIdLong(), "time out");
      this.checkPosition(user);
      return this.timeoutUntilById0(user.getId(), date);
   }

   @Nonnull
   public AuditableRestAction<Void> removeTimeout(@Nonnull UserSnowflake user) {
      Checks.notNull(user, "User");
      return this.timeoutUntilById0(user.getId(), (OffsetDateTime)null);
   }

   @Nonnull
   private AuditableRestAction<Void> timeoutUntilById0(@Nonnull String userId, @Nullable OffsetDateTime date) {
      DataObject body = DataObject.empty().put("communication_disabled_until", date == null ? null : date.toString());
      Route.CompiledRoute route = Route.Guilds.MODIFY_MEMBER.compile(this.getId(), userId);
      return new AuditableRestActionImpl<Void>(this.getJDA(), route, body);
   }

   @Nonnull
   public AuditableRestAction<Void> deafen(@Nonnull UserSnowflake user, boolean deafen) {
      Checks.notNull(user, "User");
      if (this.shouldCacheVoiceState(user.getIdLong())) {
         GuildVoiceStateImpl voiceState = (GuildVoiceStateImpl)this.voiceStateCache.get(user.getIdLong());
         AudioChannelUnion channel = voiceState != null ? voiceState.getChannel() : null;
         if (channel == null) {
            throw new IllegalStateException("Can only deafen members who are currently in a voice channel");
         }

         if (voiceState.isGuildDeafened() == deafen) {
            return new CompletedRestAction<Void>(this.getJDA(), (Throwable)null);
         }

         ((GuildChannelMixin)channel).checkPermission(Permission.VOICE_DEAF_OTHERS);
      }

      DataObject body = DataObject.empty().put("deaf", deafen);
      Route.CompiledRoute route = Route.Guilds.MODIFY_MEMBER.compile(this.getId(), user.getId());
      return new AuditableRestActionImpl<Void>(this.getJDA(), route, body);
   }

   @Nonnull
   public AuditableRestAction<Void> mute(@Nonnull UserSnowflake user, boolean mute) {
      Checks.notNull(user, "User");
      if (this.shouldCacheVoiceState(user.getIdLong())) {
         GuildVoiceStateImpl voiceState = (GuildVoiceStateImpl)this.voiceStateCache.get(user.getIdLong());
         AudioChannelUnion channel = voiceState != null ? voiceState.getChannel() : null;
         if (channel == null) {
            throw new IllegalStateException("Can only mute members who are currently in a voice channel");
         }

         if (voiceState.isGuildMuted() == mute && (mute || !voiceState.isSuppressed())) {
            return new CompletedRestAction<Void>(this.getJDA(), (Throwable)null);
         }

         ((GuildChannelMixin)channel).checkPermission(Permission.VOICE_MUTE_OTHERS);
      }

      DataObject body = DataObject.empty().put("mute", mute);
      Route.CompiledRoute route = Route.Guilds.MODIFY_MEMBER.compile(this.getId(), user.getId());
      return new AuditableRestActionImpl<Void>(this.getJDA(), route, body);
   }

   @Nonnull
   public AuditableRestAction<Void> addRoleToMember(@Nonnull UserSnowflake user, @Nonnull Role role) {
      Checks.notNull(user, "User");
      Checks.notNull(role, "Role");
      this.checkGuild(role.getGuild(), "Role");
      this.checkPermission(Permission.MANAGE_ROLES);
      this.checkPosition(role);
      Route.CompiledRoute route = Route.Guilds.ADD_MEMBER_ROLE.compile(this.getId(), user.getId(), role.getId());
      return new AuditableRestActionImpl<Void>(this.getJDA(), route);
   }

   @Nonnull
   public AuditableRestAction<Void> removeRoleFromMember(@Nonnull UserSnowflake user, @Nonnull Role role) {
      Checks.notNull(user, "User");
      Checks.notNull(role, "Role");
      this.checkGuild(role.getGuild(), "Role");
      this.checkPermission(Permission.MANAGE_ROLES);
      this.checkPosition(role);
      Route.CompiledRoute route = Route.Guilds.REMOVE_MEMBER_ROLE.compile(this.getId(), user.getId(), role.getId());
      return new AuditableRestActionImpl<Void>(this.getJDA(), route);
   }

   @Nonnull
   public AuditableRestAction<Void> modifyMemberRoles(@Nonnull Member member, Collection<Role> rolesToAdd, Collection<Role> rolesToRemove) {
      Checks.notNull(member, "Member");
      this.checkGuild(member.getGuild(), "Member");
      this.checkPermission(Permission.MANAGE_ROLES);
      Set<Role> currentRoles = new HashSet(((MemberImpl)member).getRoleSet());
      if (rolesToAdd != null) {
         this.checkRoles(rolesToAdd, "add", "to");
         currentRoles.addAll(rolesToAdd);
      }

      if (rolesToRemove != null) {
         this.checkRoles(rolesToRemove, "remove", "from");
         currentRoles.removeAll(rolesToRemove);
      }

      return this.modifyMemberRoles(member, currentRoles);
   }

   @Nonnull
   public AuditableRestAction<Void> modifyMemberRoles(@Nonnull Member member, @Nonnull Collection<Role> roles) {
      Checks.notNull(member, "Member");
      Checks.notNull(roles, "Roles");
      this.checkGuild(member.getGuild(), "Member");
      roles.forEach((role) -> {
         Checks.notNull(role, "Role in collection");
         this.checkGuild(role.getGuild(), "Role: " + role.toString());
      });
      Checks.check(!roles.contains(this.getPublicRole()), "Cannot add the PublicRole of a Guild to a Member. All members have this role by default!");
      List<Role> memberRoles = member.getRoles();
      if (Helpers.deepEqualsUnordered(roles, memberRoles)) {
         return new CompletedRestAction<Void>(this.getJDA(), (Throwable)null);
      } else {
         for(Role r : memberRoles) {
            if (!roles.contains(r)) {
               this.checkPosition(r);
               Checks.check(!r.isManaged(), "Cannot remove managed role from member. Role: %s", (Object)r);
            }
         }

         for(Role r : roles) {
            if (!memberRoles.contains(r)) {
               this.checkPosition(r);
               Checks.check(!r.isManaged(), "Cannot add managed role to member. Role: %s", (Object)r);
            }
         }

         DataObject body = DataObject.empty().put("roles", roles.stream().map(ISnowflake::getId).collect(Collectors.toSet()));
         Route.CompiledRoute route = Route.Guilds.MODIFY_MEMBER.compile(this.getId(), member.getUser().getId());
         return new AuditableRestActionImpl<Void>(this.getJDA(), route, body);
      }
   }

   @Nonnull
   public RestAction<RoleMemberCounts> retrieveRoleMemberCounts() {
      return new RestActionImpl<RoleMemberCounts>(this.api, Route.Guilds.GET_ROLE_MEMBER_COUNTS.compile(this.getId()), (response, request) -> {
         TLongIntMap map = new TLongIntHashMap();
         response.getObject().toMap().forEach((roleId, count) -> map.put(Long.parseUnsignedLong(roleId), (Integer)count));
         return new RoleMemberCountsImpl(this, map);
      });
   }

   @Nonnull
   public ChannelAction<TextChannel> createTextChannel(@Nonnull String name, Category parent) {
      return this.<TextChannel>createChannel(ChannelType.TEXT, TextChannel.class, name, parent);
   }

   @Nonnull
   public ChannelAction<NewsChannel> createNewsChannel(@Nonnull String name, Category parent) {
      return this.<NewsChannel>createChannel(ChannelType.NEWS, NewsChannel.class, name, parent);
   }

   @Nonnull
   public ChannelAction<VoiceChannel> createVoiceChannel(@Nonnull String name, Category parent) {
      return this.<VoiceChannel>createChannel(ChannelType.VOICE, VoiceChannel.class, name, parent);
   }

   @Nonnull
   public ChannelAction<StageChannel> createStageChannel(@Nonnull String name, Category parent) {
      return this.<StageChannel>createChannel(ChannelType.STAGE, StageChannel.class, name, parent);
   }

   @Nonnull
   public ChannelAction<ForumChannel> createForumChannel(@Nonnull String name, Category parent) {
      return this.<ForumChannel>createChannel(ChannelType.FORUM, ForumChannel.class, name, parent);
   }

   @Nonnull
   public ChannelAction<MediaChannel> createMediaChannel(@Nonnull String name, @Nullable Category parent) {
      return this.<MediaChannel>createChannel(ChannelType.MEDIA, MediaChannel.class, name, parent);
   }

   @Nonnull
   public ChannelAction<Category> createCategory(@Nonnull String name) {
      return this.<Category>createChannel(ChannelType.CATEGORY, Category.class, name, (Category)null);
   }

   private <T extends GuildChannel> ChannelAction<T> createChannel(ChannelType type, Class<T> clazz, String name, Category parent) {
      this.checkCanCreateChannel(parent);
      Checks.notBlank(name, "Name");
      name = name.trim();
      Checks.notEmpty(name, "Name");
      Checks.notLonger(name, 100, "Name");
      return (new ChannelActionImpl(clazz, name, this, type)).setParent(parent);
   }

   @Nonnull
   public RoleAction createRole() {
      this.checkPermission(Permission.MANAGE_ROLES);
      return new RoleActionImpl(this);
   }

   @Nonnull
   public AuditableRestAction<RichCustomEmoji> createEmoji(@Nonnull String name, @Nonnull Icon icon, @Nonnull Role... roles) {
      this.checkPermission(Permission.CREATE_GUILD_EXPRESSIONS);
      Checks.inRange(name, 2, 32, "Emoji name");
      Checks.notNull(icon, "Emoji icon");
      Checks.notNull(roles, "Roles");
      DataObject body = DataObject.empty();
      body.put("name", name);
      body.put("image", icon.getEncoding());
      if (roles.length > 0) {
         body.put("roles", Stream.of(roles).filter(Objects::nonNull).map(ISnowflake::getId).collect(Collectors.toSet()));
      }

      JDAImpl jda = this.getJDA();
      Route.CompiledRoute route = Route.Emojis.CREATE_EMOJI.compile(this.getId());
      return new AuditableRestActionImpl<RichCustomEmoji>(jda, route, body, (response, request) -> {
         DataObject obj = response.getObject();
         return jda.getEntityBuilder().createEmoji(this, obj);
      });
   }

   @Nonnull
   public AuditableRestAction<GuildSticker> createSticker(@Nonnull String name, @Nonnull String description, @Nonnull FileUpload file, @Nonnull Collection<String> tags) {
      this.checkPermission(Permission.CREATE_GUILD_EXPRESSIONS);
      Checks.inRange(name, 2, 30, "Name");
      Checks.notNull(file, "File");
      Checks.notNull(description, "Description");
      Checks.notEmpty(tags, "Tags");
      if (!description.isEmpty()) {
         Checks.inRange(description, 2, 100, "Description");
      }

      for(String t : tags) {
         Checks.notEmpty(t, "Tags");
      }

      String csv = String.join(",", tags);
      Checks.notLonger(csv, 200, "Tags");
      int index = file.getName().lastIndexOf(46);
      Checks.check(index > -1, "Filename for sticker is missing file extension. Provided: '" + file.getName() + "'. Must be PNG, GIF, or JSON.");
      MediaType mediaType;
      switch (file.getName().substring(index + 1).toLowerCase(Locale.ROOT)) {
         case "apng":
         case "png":
            mediaType = Requester.MEDIA_TYPE_PNG;
            break;
         case "gif":
            mediaType = Requester.MEDIA_TYPE_GIF;
            break;
         case "json":
            mediaType = Requester.MEDIA_TYPE_JSON;
            break;
         default:
            throw new IllegalArgumentException("Unsupported file extension: '." + extension + "', must be PNG, GIF, or JSON.");
      }

      MultipartBody.Builder builder = (new MultipartBody.Builder()).setType(MultipartBody.FORM);
      builder.addFormDataPart("name", name);
      builder.addFormDataPart("description", description);
      builder.addFormDataPart("tags", csv);
      builder.addFormDataPart("file", file.getName(), file.getRequestBody(mediaType));
      MultipartBody body = builder.build();
      Route.CompiledRoute route = Route.Stickers.CREATE_GUILD_STICKER.compile(this.getId());
      return new AuditableRestActionImpl<GuildSticker>(this.api, route, body, (response, request) -> (GuildSticker)this.api.getEntityBuilder().createRichSticker(response.getObject()));
   }

   @Nonnull
   public AuditableRestAction<Void> deleteSticker(@Nonnull StickerSnowflake id) {
      Checks.notNull(id, "Sticker");
      Route.CompiledRoute route = Route.Stickers.DELETE_GUILD_STICKER.compile(this.getId(), id.getId());
      return new AuditableRestActionImpl<Void>(this.api, route);
   }

   @Nonnull
   public SoundboardSoundCreateAction createSoundboardSound(@Nonnull String name, @Nonnull FileUpload file) {
      this.checkPermission(Permission.CREATE_GUILD_EXPRESSIONS);
      Checks.notNull(name, "Name");
      Checks.check(name.length() >= 2 && name.length() <= 32, "Name must be between 2 and 32 characters");
      Checks.notNull(file, "File");
      Route.CompiledRoute route = Route.SoundboardSounds.CREATE_GUILD_SOUNDBOARD_SOUND.compile(this.getId());
      return new SoundboardSoundCreateActionImpl(this.getJDA(), route, name, file);
   }

   @Nonnull
   public AuditableRestAction<Void> deleteSoundboardSound(@Nonnull SoundboardSoundSnowflake sound) {
      Checks.notNull(sound, "Sound");
      this.checkPermission(Permission.MANAGE_GUILD_EXPRESSIONS);
      return new AuditableRestActionImpl<Void>(this.api, Route.SoundboardSounds.DELETE_GUILD_SOUNDBOARD_SOUND.compile(this.getId(), sound.getId()));
   }

   @Nonnull
   public ChannelOrderAction modifyCategoryPositions() {
      return new ChannelOrderActionImpl(this, ChannelType.CATEGORY.getSortBucket());
   }

   @Nonnull
   public ChannelOrderAction modifyTextChannelPositions() {
      return new ChannelOrderActionImpl(this, ChannelType.TEXT.getSortBucket());
   }

   @Nonnull
   public ChannelOrderAction modifyVoiceChannelPositions() {
      return new ChannelOrderActionImpl(this, ChannelType.VOICE.getSortBucket());
   }

   @Nonnull
   public CategoryOrderAction modifyTextChannelPositions(@Nonnull Category category) {
      Checks.notNull(category, "Category");
      this.checkGuild(category.getGuild(), "Category");
      return new CategoryOrderActionImpl(category, ChannelType.TEXT.getSortBucket());
   }

   @Nonnull
   public CategoryOrderAction modifyVoiceChannelPositions(@Nonnull Category category) {
      Checks.notNull(category, "Category");
      this.checkGuild(category.getGuild(), "Category");
      return new CategoryOrderActionImpl(category, ChannelType.VOICE.getSortBucket());
   }

   @Nonnull
   public RoleOrderAction modifyRolePositions(boolean useAscendingOrder) {
      return new RoleOrderActionImpl(this, useAscendingOrder);
   }

   @Nonnull
   public GuildWelcomeScreenManager modifyWelcomeScreen() {
      return new GuildWelcomeScreenManagerImpl(this);
   }

   protected void checkGuild(Guild providedGuild, String comment) {
      if (!this.equals(providedGuild)) {
         throw new IllegalArgumentException("Provided " + comment + " is not part of this Guild!");
      }
   }

   protected void checkPermission(Permission perm) {
      if (!this.getSelfMember().hasPermission(new Permission[]{perm})) {
         throw new InsufficientPermissionException(this, perm);
      }
   }

   protected void checkPosition(UserSnowflake user) {
      Member member = this.resolveMember(user);
      if (member != null && !this.getSelfMember().canInteract(member)) {
         throw new HierarchyException("Can't modify a member with higher or equal highest role than yourself!");
      }
   }

   protected void checkPosition(Role role) {
      if (!this.getSelfMember().canInteract(role)) {
         throw new HierarchyException("Can't modify a role with higher or equal highest role than yourself! Role: " + role.toString());
      }
   }

   private void checkRoles(Collection<Role> roles, String type, String preposition) {
      roles.forEach((role) -> {
         Checks.notNull(role, "Role in roles to " + type);
         this.checkGuild(role.getGuild(), "Role: " + role);
         this.checkPosition(role);
         Checks.check(!role.isManaged(), "Cannot %s a managed role %s a Member. Role: %s", type, preposition, role.toString());
      });
   }

   private void checkCanCreateChannel(Category parent) {
      if (parent != null) {
         Checks.check(parent.getGuild().equals(this), "Category is not from the same guild!");
         if (!this.getSelfMember().hasPermission(parent, new Permission[]{Permission.MANAGE_CHANNEL})) {
            throw new InsufficientPermissionException(parent, Permission.MANAGE_CHANNEL);
         }
      } else {
         this.checkPermission(Permission.MANAGE_CHANNEL);
      }

   }

   private void checkOwner(long userId, String what) {
      if (userId == this.ownerId) {
         throw new HierarchyException("Cannot " + what + " the owner of a guild.");
      }
   }

   private Member resolveMember(UserSnowflake user) {
      Member member = this.getMemberById(user.getIdLong());
      if (member == null && user instanceof Member) {
         member = (Member)user;
         if (!this.equals(member.getGuild())) {
            member = null;
         }
      }

      return member;
   }

   private synchronized boolean isRequestToSpeakPending() {
      return this.pendingRequestToSpeak != null && !this.pendingRequestToSpeak.isDone();
   }

   public synchronized void updateRequestToSpeak() {
      if (this.isRequestToSpeakPending()) {
         AudioChannel connectedChannel = this.getSelfMember().getVoiceState().getChannel();
         if (connectedChannel instanceof StageChannel) {
            StageChannel stage = (StageChannel)connectedChannel;
            CompletableFuture<Void> future = this.pendingRequestToSpeak;
            this.pendingRequestToSpeak = null;

            try {
               RestAction var10000 = stage.requestToSpeak();
               Consumer var10001 = (v) -> future.complete((Object)null);
               Objects.requireNonNull(future);
               var10000.queue(var10001, future::completeExceptionally);
            } catch (Throwable ex) {
               future.completeExceptionally(ex);
               if (ex instanceof Error) {
                  throw ex;
               }
            }

         }
      }
   }

   public GuildImpl setOwner(Member owner) {
      if (owner != null && this.getMemberById(owner.getIdLong()) != null) {
         this.owner = owner;
      }

      return this;
   }

   public GuildImpl setName(String name) {
      this.name = name;
      return this;
   }

   public GuildImpl setIconId(String iconId) {
      this.iconId = iconId;
      return this;
   }

   public GuildImpl setFeatures(Set<String> features) {
      this.features = Collections.unmodifiableSet(features);
      return this;
   }

   public GuildImpl setSplashId(String splashId) {
      this.splashId = splashId;
      return this;
   }

   public GuildImpl setVanityCode(String code) {
      this.vanityCode = code;
      return this;
   }

   public GuildImpl setDescription(String description) {
      this.description = description;
      return this;
   }

   public GuildImpl setBannerId(String bannerId) {
      this.banner = bannerId;
      return this;
   }

   public GuildImpl setMaxPresences(int maxPresences) {
      this.maxPresences = maxPresences;
      return this;
   }

   public GuildImpl setMaxMembers(int maxMembers) {
      this.maxMembers = maxMembers;
      return this;
   }

   public GuildImpl setAfkChannel(VoiceChannel afkChannel) {
      this.afkChannel = afkChannel;
      return this;
   }

   public GuildImpl setSystemChannel(TextChannel systemChannel) {
      this.systemChannel = systemChannel;
      return this;
   }

   public GuildImpl setRulesChannel(TextChannel rulesChannel) {
      this.rulesChannel = rulesChannel;
      return this;
   }

   public GuildImpl setCommunityUpdatesChannel(TextChannel communityUpdatesChannel) {
      this.communityUpdatesChannel = communityUpdatesChannel;
      return this;
   }

   public GuildImpl setSafetyAlertsChannel(TextChannel safetyAlertsChannel) {
      this.safetyAlertsChannel = safetyAlertsChannel;
      return this;
   }

   public GuildImpl setPublicRole(Role publicRole) {
      this.publicRole = publicRole;
      return this;
   }

   public GuildImpl setSecurityIncidentActions(SecurityIncidentActions actions) {
      this.securityIncidentActions = actions == null ? SecurityIncidentActions.disabled() : actions;
      return this;
   }

   public GuildImpl setSecurityIncidentDetections(SecurityIncidentDetections detections) {
      this.securityIncidentDetections = detections == null ? SecurityIncidentDetections.EMPTY : detections;
      return this;
   }

   public GuildImpl setVerificationLevel(Guild.VerificationLevel level) {
      this.verificationLevel = level;
      return this;
   }

   public GuildImpl setDefaultNotificationLevel(Guild.NotificationLevel level) {
      this.defaultNotificationLevel = level;
      return this;
   }

   public GuildImpl setRequiredMFALevel(Guild.MFALevel level) {
      this.mfaLevel = level;
      return this;
   }

   public GuildImpl setExplicitContentLevel(Guild.ExplicitContentLevel level) {
      this.explicitContentLevel = level;
      return this;
   }

   public GuildImpl setAfkTimeout(Guild.Timeout afkTimeout) {
      this.afkTimeout = afkTimeout;
      return this;
   }

   public GuildImpl setLocale(DiscordLocale locale) {
      this.preferredLocale = locale;
      return this;
   }

   public GuildImpl setBoostTier(int tier) {
      this.boostTier = Guild.BoostTier.fromKey(tier);
      return this;
   }

   public GuildImpl setBoostCount(int count) {
      this.boostCount = count;
      return this;
   }

   public GuildImpl setOwnerId(long ownerId) {
      this.ownerId = ownerId;
      return this;
   }

   public GuildImpl setMemberCount(int count) {
      this.memberCount = count;
      return this;
   }

   public GuildImpl setNSFWLevel(Guild.NSFWLevel nsfwLevel) {
      this.nsfwLevel = nsfwLevel;
      return this;
   }

   public GuildImpl setBoostProgressBarEnabled(boolean enabled) {
      this.boostProgressBarEnabled = enabled;
      return this;
   }

   public GuildImpl setSystemChannelFlags(int systemChannelFlags) {
      this.systemChannelFlags = systemChannelFlags;
      return this;
   }

   public SortedSnowflakeCacheViewImpl<ScheduledEvent> getScheduledEventsView() {
      return this.scheduledEventCache;
   }

   public SortedChannelCacheViewImpl<GuildChannel> getChannelView() {
      return this.channelCache;
   }

   public SortedSnowflakeCacheViewImpl<Role> getRolesView() {
      return this.roleCache;
   }

   public SnowflakeCacheViewImpl<RichCustomEmoji> getEmojisView() {
      return this.emojicache;
   }

   public SnowflakeCacheViewImpl<GuildSticker> getStickersView() {
      return this.stickerCache;
   }

   public SnowflakeCacheViewImpl<SoundboardSound> getSoundboardSoundsView() {
      return this.soundboardCache;
   }

   public MemberCacheViewImpl getMembersView() {
      return this.memberCache;
   }

   @Nonnull
   public Guild.NSFWLevel getNSFWLevel() {
      return this.nsfwLevel;
   }

   @Nonnull
   public Set<SystemChannelFlag> getSystemChannelFlags() {
      return Collections.unmodifiableSet(SystemChannelFlag.getFlags(this.systemChannelFlags));
   }

   public int getSystemChannelFlagsRaw() {
      return this.systemChannelFlags;
   }

   @Nullable
   public CacheView.SimpleCacheView<MemberPresenceImpl> getPresenceView() {
      return this.memberPresences;
   }

   @Nonnull
   public SnowflakeCacheViewImpl<GuildVoiceStateImpl> getVoiceStateView() {
      return this.voiceStateCache;
   }

   public void onMemberAdd() {
      ++this.memberCount;
   }

   public void onMemberRemove(long memberId) {
      --this.memberCount;
      this.voiceStateCache.remove(memberId);
      if (this.memberPresences != null) {
         this.memberPresences.remove(memberId);
      }

   }

   public boolean shouldCacheVoiceState(long userId) {
      return userId == this.api.getSelfUser().getIdLong() || this.api.getCacheFlags().contains(CacheFlag.VOICE_STATE);
   }

   public GuildVoiceStateImpl getVoiceState(Member member) {
      GuildVoiceStateImpl voiceState = this.voiceStateCache.getElementById(member.getIdLong());
      if (voiceState != null) {
         return voiceState;
      } else {
         return this.shouldCacheVoiceState(member.getIdLong()) ? new GuildVoiceStateImpl(member) : null;
      }
   }

   public void updateCacheVoiceStateMember(MemberImpl member) {
      if (this.shouldCacheVoiceState(member.getIdLong())) {
         UnlockHook hook = this.voiceStateCache.writeLock();

         try {
            GuildVoiceStateImpl voiceState = (GuildVoiceStateImpl)this.voiceStateCache.get(member.getIdLong());
            if (voiceState != null) {
               voiceState.setMember(member);
            }
         } catch (Throwable var6) {
            if (hook != null) {
               try {
                  hook.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (hook != null) {
            hook.close();
         }

      }
   }

   public void handleVoiceStateUpdate(GuildVoiceStateImpl voiceState) {
      if (this.shouldCacheVoiceState(voiceState.getIdLong())) {
         UnlockHook hook = this.voiceStateCache.writeLock();

         try {
            if (voiceState.getChannel() != null) {
               this.voiceStateCache.getMap().put(voiceState.getIdLong(), voiceState);
            } else {
               this.voiceStateCache.getMap().remove(voiceState.getIdLong());
            }
         } catch (Throwable var6) {
            if (hook != null) {
               try {
                  hook.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (hook != null) {
            hook.close();
         }

      }
   }

   public List<Member> getConnectedMembers(GuildChannel channel) {
      return (List)this.voiceStateCache.applyStream((stream) -> (List)stream.filter((state) -> channel.equals(state.getChannel())).map(GuildVoiceStateImpl::getMember).filter(Objects::nonNull).collect(Helpers.toUnmodifiableList()));
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof GuildImpl)) {
         return false;
      } else {
         GuildImpl oGuild = (GuildImpl)o;
         return this.id == oGuild.id;
      }
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }

   public String toString() {
      return (new EntityString(this)).setName(this.getName()).toString();
   }
}
