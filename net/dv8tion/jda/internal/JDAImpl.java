package net.dv8tion.jda.internal;

import com.neovisionaries.ws.client.WebSocketFactory;
import gnu.trove.set.TLongSet;
import java.time.OffsetDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.GatewayEncoding;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audio.AudioModuleConfig;
import net.dv8tion.jda.api.audio.factory.IAudioSendFactory;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.RoleConnectionMetadata;
import net.dv8tion.jda.api.entities.SKU;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.SoundboardSound;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.MediaChannel;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.emoji.ApplicationEmoji;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.sticker.StickerPack;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.api.entities.sticker.StickerUnion;
import net.dv8tion.jda.api.events.GatewayPingEvent;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.InterfacedEventManager;
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.managers.ApplicationManager;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.RestConfig;
import net.dv8tion.jda.api.requests.RestRateLimiter;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import net.dv8tion.jda.api.requests.restaction.CommandEditAction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.requests.restaction.TestEntitlementCreateAction;
import net.dv8tion.jda.api.requests.restaction.pagination.EntitlementPaginationAction;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.Once;
import net.dv8tion.jda.api.utils.SessionController;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.cache.CacheView;
import net.dv8tion.jda.api.utils.cache.ChannelCacheView;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.UserImpl;
import net.dv8tion.jda.internal.handle.EventCache;
import net.dv8tion.jda.internal.handle.GuildSetupController;
import net.dv8tion.jda.internal.hooks.EventManagerProxy;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.dv8tion.jda.internal.interactions.command.CommandImpl;
import net.dv8tion.jda.internal.managers.ApplicationManagerImpl;
import net.dv8tion.jda.internal.managers.AudioManagerImpl;
import net.dv8tion.jda.internal.managers.DirectAudioControllerImpl;
import net.dv8tion.jda.internal.managers.PresenceImpl;
import net.dv8tion.jda.internal.requests.CompletedRestAction;
import net.dv8tion.jda.internal.requests.DeferredRestAction;
import net.dv8tion.jda.internal.requests.Requester;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.WebSocketClient;
import net.dv8tion.jda.internal.requests.restaction.CommandCreateActionImpl;
import net.dv8tion.jda.internal.requests.restaction.CommandEditActionImpl;
import net.dv8tion.jda.internal.requests.restaction.CommandListUpdateActionImpl;
import net.dv8tion.jda.internal.requests.restaction.TestEntitlementCreateActionImpl;
import net.dv8tion.jda.internal.requests.restaction.pagination.EntitlementPaginationActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import net.dv8tion.jda.internal.utils.JDALogger;
import net.dv8tion.jda.internal.utils.ShutdownReason;
import net.dv8tion.jda.internal.utils.UnlockHook;
import net.dv8tion.jda.internal.utils.cache.AbstractCacheView;
import net.dv8tion.jda.internal.utils.cache.ChannelCacheViewImpl;
import net.dv8tion.jda.internal.utils.cache.SnowflakeCacheViewImpl;
import net.dv8tion.jda.internal.utils.config.AuthorizationConfig;
import net.dv8tion.jda.internal.utils.config.MetaConfig;
import net.dv8tion.jda.internal.utils.config.SessionConfig;
import net.dv8tion.jda.internal.utils.config.ThreadingConfig;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.MDC;

public class JDAImpl implements JDA {
   public static final Logger LOG = JDALogger.getLog(JDA.class);
   protected final SnowflakeCacheViewImpl<User> userCache;
   protected final SnowflakeCacheViewImpl<Guild> guildCache;
   protected final ChannelCacheViewImpl<Channel> channelCache;
   protected final ArrayDeque<Long> privateChannelLRU;
   protected final AbstractCacheView<AudioManager> audioManagers;
   protected final PresenceImpl presence;
   protected final Thread shutdownHook;
   protected final EntityBuilder entityBuilder;
   protected final EventCache eventCache;
   protected final EventManagerProxy eventManager;
   protected final GuildSetupController guildSetupController;
   protected final DirectAudioControllerImpl audioController;
   protected final AuthorizationConfig authConfig;
   protected final ThreadingConfig threadConfig;
   protected final SessionConfig sessionConfig;
   protected final MetaConfig metaConfig;
   protected final RestConfig restConfig;
   protected final AudioModuleConfig audioModuleConfig;
   public ShutdownReason shutdownReason;
   protected WebSocketClient client;
   protected Requester requester;
   protected SelfUser selfUser;
   protected JDA.ShardInfo shardInfo;
   protected long responseTotal;
   protected long gatewayPing;
   protected String gatewayUrl;
   protected ChunkingFilter chunkingFilter;
   protected String clientId;
   protected String requiredScopes;
   protected ShardManager shardManager;
   protected MemberCachePolicy memberCachePolicy;
   protected final AtomicReference<JDA.Status> status;
   protected final ReentrantLock statusLock;
   protected final Condition statusCondition;
   protected final AtomicBoolean requesterShutdown;
   protected final AtomicReference<ShutdownEvent> shutdownEvent;

   public JDAImpl(AuthorizationConfig authConfig) {
      this(authConfig, (SessionConfig)null, (ThreadingConfig)null, (MetaConfig)null, (RestConfig)null, (AudioModuleConfig)null);
   }

   public JDAImpl(AuthorizationConfig authConfig, SessionConfig sessionConfig, ThreadingConfig threadConfig, MetaConfig metaConfig, RestConfig restConfig, AudioModuleConfig audioModuleConfig) {
      this.userCache = new SnowflakeCacheViewImpl<User>(User.class, User::getName);
      this.guildCache = new SnowflakeCacheViewImpl<Guild>(Guild.class, Guild::getName);
      this.channelCache = new ChannelCacheViewImpl<Channel>(Channel.class);
      this.privateChannelLRU = new ArrayDeque();
      this.audioManagers = new CacheView.SimpleCacheView<AudioManager>(AudioManager.class, (m) -> m.getGuild().getName());
      this.entityBuilder = new EntityBuilder(this);
      this.shutdownReason = ShutdownReason.USER_SHUTDOWN;
      this.gatewayPing = -1L;
      this.clientId = null;
      this.requiredScopes = "bot";
      this.shardManager = null;
      this.memberCachePolicy = MemberCachePolicy.ALL;
      this.status = new AtomicReference(JDA.Status.INITIALIZING);
      this.statusLock = new ReentrantLock();
      this.statusCondition = this.statusLock.newCondition();
      this.requesterShutdown = new AtomicBoolean(false);
      this.shutdownEvent = new AtomicReference((Object)null);
      this.authConfig = authConfig;
      this.threadConfig = threadConfig == null ? ThreadingConfig.getDefault() : threadConfig;
      this.sessionConfig = sessionConfig == null ? SessionConfig.getDefault() : sessionConfig;
      this.metaConfig = metaConfig == null ? MetaConfig.getDefault() : metaConfig;
      this.restConfig = restConfig == null ? new RestConfig() : restConfig;
      this.audioModuleConfig = audioModuleConfig == null ? new AudioModuleConfig() : audioModuleConfig;
      this.shutdownHook = this.metaConfig.isUseShutdownHook() ? new Thread(this::shutdownNow, "JDA Shutdown Hook") : null;
      this.presence = new PresenceImpl(this);
      this.guildSetupController = new GuildSetupController(this);
      this.audioController = new DirectAudioControllerImpl(this);
      this.eventCache = new EventCache();
      this.eventManager = new EventManagerProxy(new InterfacedEventManager(), this.threadConfig.getEventPool());
   }

   public void handleEvent(@Nonnull GenericEvent event) {
      this.eventManager.handle(event);
   }

   public boolean isRawEvents() {
      return this.sessionConfig.isRawEvents();
   }

   public boolean isEventPassthrough() {
      return this.sessionConfig.isEventPassthrough();
   }

   public boolean isCacheFlagSet(CacheFlag flag) {
      return this.metaConfig.getCacheFlags().contains(flag);
   }

   public boolean isIntent(GatewayIntent intent) {
      int raw = intent.getRawValue();
      return (this.client.getGatewayIntents() & raw) == raw;
   }

   public int getLargeThreshold() {
      return this.sessionConfig.getLargeThreshold();
   }

   public int getMaxBufferSize() {
      return this.metaConfig.getMaxBufferSize();
   }

   public boolean chunkGuild(long id) {
      try {
         return this.isIntent(GatewayIntent.GUILD_MEMBERS) && this.chunkingFilter.filter(id);
      } catch (Exception e) {
         LOG.error((String)"Uncaught exception from chunking filter", (Throwable)e);
         return true;
      }
   }

   public void setChunkingFilter(ChunkingFilter filter) {
      this.chunkingFilter = filter;
   }

   public boolean cacheMember(Member member) {
      try {
         return member.getUser().equals(this.getSelfUser()) || this.memberCachePolicy.cacheMember(member);
      } catch (Exception e) {
         LOG.error((String)"Uncaught exception from member cache policy", (Throwable)e);
         return true;
      }
   }

   public void setMemberCachePolicy(MemberCachePolicy policy) {
      this.memberCachePolicy = policy;
   }

   public SessionController getSessionController() {
      return this.sessionConfig.getSessionController();
   }

   public GuildSetupController getGuildSetupController() {
      return this.guildSetupController;
   }

   public VoiceDispatchInterceptor getVoiceInterceptor() {
      return this.sessionConfig.getVoiceDispatchInterceptor();
   }

   public void usedPrivateChannel(long id) {
      synchronized(this.privateChannelLRU) {
         this.privateChannelLRU.remove(id);
         this.privateChannelLRU.addFirst(id);
         if (this.privateChannelLRU.size() > 10) {
            long removed = (Long)this.privateChannelLRU.removeLast();
            this.channelCache.remove(ChannelType.PRIVATE, removed);
         }

      }
   }

   public void initRequester() {
      if (this.requester == null) {
         RestRateLimiter rateLimiter = (RestRateLimiter)this.restConfig.getRateLimiterFactory().apply(new RestRateLimiter.RateLimitConfig(this.threadConfig.getRateLimitScheduler(), this.threadConfig.getRateLimitElastic(), this.getSessionController().getRateLimitHandle(), this.sessionConfig.isRelativeRateLimit() && this.restConfig.isRelativeRateLimit()));
         this.requester = new Requester(this, this.authConfig, this.restConfig, rateLimiter);
         this.requester.setRetryOnTimeout(this.sessionConfig.isRetryOnTimeout());
      }
   }

   public int login() {
      return this.login((String)null, (JDA.ShardInfo)null, Compression.ZLIB, true, GatewayIntent.ALL_INTENTS, GatewayEncoding.JSON);
   }

   public int login(JDA.ShardInfo shardInfo, Compression compression, boolean validateToken, int intents, GatewayEncoding encoding) {
      return this.login((String)null, shardInfo, compression, validateToken, intents, encoding);
   }

   public int login(String gatewayUrl, JDA.ShardInfo shardInfo, Compression compression, boolean validateToken, int intents, GatewayEncoding encoding) {
      this.shardInfo = shardInfo;
      this.threadConfig.init(this::getIdentifierString);
      this.initRequester();
      this.gatewayUrl = gatewayUrl == null ? this.getGateway() : gatewayUrl;
      Checks.notNull(this.gatewayUrl, "Gateway URL");
      this.setStatus(JDA.Status.LOGGING_IN);
      Map<String, String> previousContext = null;
      ConcurrentMap<String, String> contextMap = this.metaConfig.getMdcContextMap();
      if (contextMap != null) {
         if (shardInfo != null) {
            contextMap.put("jda.shard", shardInfo.getShardString());
            contextMap.put("jda.shard.id", String.valueOf(shardInfo.getShardId()));
            contextMap.put("jda.shard.total", String.valueOf(shardInfo.getShardTotal()));
         }

         previousContext = MDC.getCopyOfContextMap();
         contextMap.forEach(MDC::put);
         this.requester.setContextReady(true);
      }

      if (validateToken) {
         this.verifyToken();
         LOG.info("Login Successful!");
      }

      this.client = new WebSocketClient(this, compression, intents, encoding);
      if (previousContext != null) {
         previousContext.forEach(MDC::put);
      }

      if (this.shutdownHook != null) {
         Runtime.getRuntime().addShutdownHook(this.shutdownHook);
      }

      return shardInfo == null ? -1 : shardInfo.getShardTotal();
   }

   public String getGateway() {
      return this.getSessionController().getGateway();
   }

   public SessionController.ShardedGateway getShardedGateway() {
      return this.getSessionController().getShardedGateway(this);
   }

   public ConcurrentMap<String, String> getContextMap() {
      return this.metaConfig.getMdcContextMap() == null ? null : new ConcurrentHashMap(this.metaConfig.getMdcContextMap());
   }

   public void setContext() {
      if (this.metaConfig.getMdcContextMap() != null) {
         this.metaConfig.getMdcContextMap().forEach(MDC::put);
      }

   }

   public void setToken(String token) {
      this.authConfig.setToken(token);
   }

   public void setStatus(JDA.Status status) {
      StatusChangeEvent event = (StatusChangeEvent)MiscUtil.locked(this.statusLock, (Supplier)(() -> {
         JDA.Status oldStatus = (JDA.Status)this.status.getAndSet(status);
         this.statusCondition.signalAll();
         return new StatusChangeEvent(this, status, oldStatus);
      }));
      if (event.getOldStatus() != event.getNewStatus()) {
         this.handleEvent(event);
      }

   }

   public void verifyToken() {
      RestActionImpl<DataObject> login = (new RestActionImpl<DataObject>(this, Route.Self.GET_SELF.compile()) {
         public void handleResponse(Response response, Request<DataObject> request) {
            if (response.isOk()) {
               request.onSuccess(response.getObject());
            } else if (response.isRateLimit()) {
               request.onFailure((Throwable)(new RateLimitedException(request.getRoute(), response.retryAfter)));
            } else if (response.code == 401) {
               request.onSuccess((Object)null);
            } else {
               request.onFailure(response);
            }

         }
      }).priority();

      try {
         DataObject userResponse = (DataObject)login.complete();
         if (userResponse != null) {
            this.getEntityBuilder().createSelfUser(userResponse);
         } else {
            throw new InvalidTokenException();
         }
      } catch (Throwable error) {
         this.shutdownNow();
         throw error;
      }
   }

   @Nonnull
   public AuthorizationConfig getAuthorizationConfig() {
      return this.authConfig;
   }

   @Nonnull
   public AudioModuleConfig getAudioModuleConfig() {
      return this.audioModuleConfig;
   }

   @Nonnull
   public String getToken() {
      return this.authConfig.getToken();
   }

   public boolean isBulkDeleteSplittingEnabled() {
      return this.sessionConfig.isBulkDeleteSplittingEnabled();
   }

   public void setAutoReconnect(boolean autoReconnect) {
      this.sessionConfig.setAutoReconnect(autoReconnect);
      WebSocketClient client = this.getClient();
      if (client != null) {
         client.setAutoReconnect(autoReconnect);
      }

   }

   public void setRequestTimeoutRetry(boolean retryOnTimeout) {
      this.requester.setRetryOnTimeout(retryOnTimeout);
   }

   public boolean isAutoReconnect() {
      return this.sessionConfig.isAutoReconnect();
   }

   @Nonnull
   public JDA.Status getStatus() {
      return (JDA.Status)this.status.get();
   }

   @Nonnull
   public EnumSet<GatewayIntent> getGatewayIntents() {
      return GatewayIntent.getIntents(this.client.getGatewayIntents());
   }

   @Nonnull
   public EnumSet<CacheFlag> getCacheFlags() {
      return Helpers.<CacheFlag>copyEnumSet(CacheFlag.class, this.metaConfig.getCacheFlags());
   }

   public boolean unloadUser(long userId) {
      if (userId == this.selfUser.getIdLong()) {
         return false;
      } else {
         User user = this.getUserById(userId);
         if (user == null) {
            return false;
         } else {
            return this.getGuildCache().stream().filter((guild) -> guild.unloadMember(userId)).count() > 0L;
         }
      }
   }

   public long getGatewayPing() {
      return this.gatewayPing;
   }

   @Nonnull
   public JDA awaitStatus(@Nonnull JDA.Status status, @Nonnull JDA.Status... failOn) throws InterruptedException {
      Checks.notNull(status, "Status");
      if (this.getStatus() == JDA.Status.CONNECTED) {
         return this;
      } else {
         MiscUtil.tryLock(this.statusLock);

         try {
            EnumSet<JDA.Status> endCondition = EnumSet.of(status, failOn);

            for(JDA.Status current = this.getStatus(); !current.isInit() || current.ordinal() < status.ordinal(); current = this.getStatus()) {
               if (current == JDA.Status.SHUTDOWN) {
                  throw new IllegalStateException("Was shutdown trying to await status.\nReason: " + this.shutdownReason);
               }

               if (endCondition.contains(current)) {
                  JDAImpl var5 = this;
                  return var5;
               }

               this.statusCondition.await();
            }

            return this;
         } finally {
            this.statusLock.unlock();
         }
      }
   }

   public boolean awaitShutdown(long timeout, @Nonnull TimeUnit unit) throws InterruptedException {
      timeout = unit.toMillis(timeout);
      long deadline = timeout == 0L ? Long.MAX_VALUE : System.currentTimeMillis() + timeout;
      MiscUtil.tryLock(this.statusLock);

      try {
         for(JDA.Status current = this.getStatus(); current != JDA.Status.SHUTDOWN; current = this.getStatus()) {
            if (!this.statusCondition.await(deadline - System.currentTimeMillis(), TimeUnit.MILLISECONDS)) {
               boolean var7 = false;
               return var7;
            }
         }

         boolean var12 = true;
         return var12;
      } finally {
         this.statusLock.unlock();
      }
   }

   public int cancelRequests() {
      return this.requester.getRateLimiter().cancelRequests();
   }

   @Nonnull
   public ScheduledExecutorService getRateLimitPool() {
      return this.threadConfig.getRateLimitScheduler();
   }

   @Nonnull
   public ScheduledExecutorService getGatewayPool() {
      return this.threadConfig.getGatewayPool();
   }

   @Nonnull
   public ExecutorService getCallbackPool() {
      return this.threadConfig.getCallbackPool();
   }

   @Nonnull
   public OkHttpClient getHttpClient() {
      return this.sessionConfig.getHttpClient();
   }

   @Nonnull
   public DirectAudioControllerImpl getDirectAudioController() {
      if (!this.isIntent(GatewayIntent.GUILD_VOICE_STATES)) {
         throw new IllegalStateException("Cannot use audio features with disabled GUILD_VOICE_STATES intent!");
      } else {
         return this.audioController;
      }
   }

   @Nonnull
   public List<Guild> getMutualGuilds(@Nonnull UserSnowflake... users) {
      Checks.notNull(users, "users");
      return this.getMutualGuilds((Collection)Arrays.asList(users));
   }

   @Nonnull
   public List<Guild> getMutualGuilds(@Nonnull Collection<? extends UserSnowflake> users) {
      Checks.notNull(users, "users");

      for(UserSnowflake u : users) {
         Checks.notNull(u, "All users");
      }

      return (List)this.getGuilds().stream().filter((guild) -> {
         Stream var10000 = users.stream();
         Objects.requireNonNull(guild);
         return var10000.allMatch(guild::isMember);
      }).collect(Helpers.toUnmodifiableList());
   }

   @Nonnull
   public CacheRestAction<User> retrieveUserById(long id) {
      return new DeferredRestAction(this, User.class, () -> !this.isIntent(GatewayIntent.GUILD_MEMBERS) && !this.isIntent(GatewayIntent.GUILD_PRESENCES) ? null : this.getUserById(id), () -> {
         if (id == this.getSelfUser().getIdLong()) {
            return new CompletedRestAction(this, this.getSelfUser());
         } else {
            Route.CompiledRoute route = Route.Users.GET_USER.compile(Long.toUnsignedString(id));
            return new RestActionImpl(this, route, (response, request) -> this.getEntityBuilder().createUser(response.getObject()));
         }
      });
   }

   @Nonnull
   public CacheView<AudioManager> getAudioManagerCache() {
      return this.audioManagers;
   }

   @Nonnull
   public SnowflakeCacheView<Guild> getGuildCache() {
      return this.guildCache;
   }

   @Nonnull
   public Set<String> getUnavailableGuilds() {
      TLongSet unavailableGuilds = this.guildSetupController.getUnavailableGuilds();
      Set<String> copy = new HashSet();
      unavailableGuilds.forEach((id) -> copy.add(Long.toUnsignedString(id)));
      return copy;
   }

   public boolean isUnavailable(long guildId) {
      return this.guildSetupController.isUnavailable(guildId);
   }

   @Nonnull
   public SnowflakeCacheView<Role> getRoleCache() {
      return CacheView.allSnowflakes((Supplier)(() -> this.guildCache.stream().map(Guild::getRoleCache)));
   }

   @Nonnull
   public SnowflakeCacheView<RichCustomEmoji> getEmojiCache() {
      return CacheView.allSnowflakes((Supplier)(() -> this.guildCache.stream().map(Guild::getEmojiCache)));
   }

   @Nonnull
   public RestAction<ApplicationEmoji> createApplicationEmoji(@Nonnull String name, @Nonnull Icon icon) {
      Checks.inRange(name, 2, 32, "Emoji name");
      Checks.matches(name, Checks.ALPHANUMERIC_WITH_DASH, "Emoji name");
      Checks.notNull(icon, "Emoji icon");
      DataObject body = DataObject.empty();
      body.put("name", name);
      body.put("image", icon.getEncoding());
      Route.CompiledRoute route = Route.Applications.CREATE_APPLICATION_EMOJI.compile(this.getSelfUser().getApplicationId());
      return new RestActionImpl<ApplicationEmoji>(this, route, body, (response, request) -> {
         DataObject obj = response.getObject();
         User selfUser = this.getSelfUser();
         return this.entityBuilder.createApplicationEmoji(this, obj, selfUser);
      });
   }

   @Nonnull
   public RestAction<List<ApplicationEmoji>> retrieveApplicationEmojis() {
      Route.CompiledRoute route = Route.Applications.GET_APPLICATION_EMOJIS.compile(this.getSelfUser().getApplicationId());
      return new RestActionImpl<List<ApplicationEmoji>>(this, route, (response, request) -> {
         DataArray emojis = response.getObject().getArray("items");
         List<ApplicationEmoji> list = new ArrayList(emojis.length());

         for(int i = 0; i < emojis.length(); ++i) {
            try {
               DataObject emoji = emojis.getObject(i);
               Optional var10000 = emoji.optObject("user");
               EntityBuilder var10001 = this.entityBuilder;
               Objects.requireNonNull(var10001);
               User owner = (User)var10000.map(var10001::createUser).orElse((Object)null);
               list.add(this.entityBuilder.createApplicationEmoji(this, emoji, owner));
            } catch (ParsingException e) {
               LOG.error((String)"Failed to parse application emoji with JSON: {}", (Object)emojis.getObject(i), (Object)e);
            }
         }

         return Collections.unmodifiableList(list);
      });
   }

   @Nonnull
   public RestAction<ApplicationEmoji> retrieveApplicationEmojiById(@Nonnull String emojiId) {
      Checks.isSnowflake(emojiId);
      Route.CompiledRoute route = Route.Applications.GET_APPLICATION_EMOJI.compile(this.getSelfUser().getApplicationId(), emojiId);
      return new RestActionImpl<ApplicationEmoji>(this, route, (response, request) -> {
         DataObject emoji = response.getObject();
         Optional var10000 = emoji.optObject("user");
         EntityBuilder var10001 = this.entityBuilder;
         Objects.requireNonNull(var10001);
         User owner = (User)var10000.map(var10001::createUser).orElse((Object)null);
         return this.entityBuilder.createApplicationEmoji(this, emoji, owner);
      });
   }

   @Nonnull
   public RestAction<StickerUnion> retrieveSticker(@Nonnull StickerSnowflake sticker) {
      Checks.notNull(sticker, "Sticker");
      Route.CompiledRoute route = Route.Stickers.GET_STICKER.compile(sticker.getId());
      return new RestActionImpl<StickerUnion>(this, route, (response, request) -> this.entityBuilder.createRichSticker(response.getObject()));
   }

   @Nonnull
   public RestAction<List<StickerPack>> retrieveNitroStickerPacks() {
      Route.CompiledRoute route = Route.Stickers.LIST_PACKS.compile();
      return new RestActionImpl<List<StickerPack>>(this, route, (response, request) -> {
         DataArray array = response.getObject().getArray("sticker_packs");
         List<StickerPack> packs = new ArrayList(array.length());

         for(int i = 0; i < array.length(); ++i) {
            DataObject object = null;

            try {
               object = array.getObject(i);
               StickerPack pack = this.entityBuilder.createStickerPack(object);
               packs.add(pack);
            } catch (ParsingException var8) {
               EntityBuilder.LOG.error((String)"Failed to parse sticker pack. JSON: {}", (Object)object);
            }
         }

         return Collections.unmodifiableList(packs);
      });
   }

   @Nonnull
   public RestAction<@Unmodifiable List<SoundboardSound>> retrieveDefaultSoundboardSounds() {
      Route.CompiledRoute route = Route.SoundboardSounds.LIST_DEFAULT_SOUNDBOARD_SOUNDS.compile();
      return new RestActionImpl<List<SoundboardSound>>(this, route, (response, request) -> {
         Stream var10000 = response.getArray().stream(DataArray::getObject);
         EntityBuilder var10001 = this.entityBuilder;
         Objects.requireNonNull(var10001);
         return (List)Helpers.mapGracefully(var10000, var10001::createSoundboardSound, "Failed to parse soundboard sound").collect(Helpers.toUnmodifiableList());
      });
   }

   @Nonnull
   public SnowflakeCacheView<ScheduledEvent> getScheduledEventCache() {
      return CacheView.allSnowflakes((Supplier)(() -> this.guildCache.stream().map(Guild::getScheduledEventCache)));
   }

   @Nonnull
   public ChannelCacheView<Channel> getChannelCache() {
      return this.channelCache;
   }

   @Nonnull
   public SnowflakeCacheView<Category> getCategoryCache() {
      return this.channelCache.ofType(Category.class);
   }

   @Nonnull
   public SnowflakeCacheView<TextChannel> getTextChannelCache() {
      return this.channelCache.ofType(TextChannel.class);
   }

   @Nonnull
   public SnowflakeCacheView<NewsChannel> getNewsChannelCache() {
      return this.channelCache.ofType(NewsChannel.class);
   }

   @Nonnull
   public SnowflakeCacheView<VoiceChannel> getVoiceChannelCache() {
      return this.channelCache.ofType(VoiceChannel.class);
   }

   @Nonnull
   public SnowflakeCacheView<StageChannel> getStageChannelCache() {
      return this.channelCache.ofType(StageChannel.class);
   }

   @Nonnull
   public SnowflakeCacheView<ThreadChannel> getThreadChannelCache() {
      return this.channelCache.ofType(ThreadChannel.class);
   }

   @Nonnull
   public SnowflakeCacheView<ForumChannel> getForumChannelCache() {
      return this.channelCache.ofType(ForumChannel.class);
   }

   @Nonnull
   public SnowflakeCacheView<MediaChannel> getMediaChannelCache() {
      return this.channelCache.ofType(MediaChannel.class);
   }

   @Nonnull
   public SnowflakeCacheView<PrivateChannel> getPrivateChannelCache() {
      return this.channelCache.ofType(PrivateChannel.class);
   }

   public PrivateChannel getPrivateChannelById(@Nonnull String id) {
      return this.getPrivateChannelById(MiscUtil.parseSnowflake(id));
   }

   public PrivateChannel getPrivateChannelById(long id) {
      PrivateChannel channel = JDA.super.getPrivateChannelById(id);
      if (channel != null) {
         this.usedPrivateChannel(id);
      }

      return channel;
   }

   public <T extends Channel> T getChannelById(@Nonnull Class<T> type, long id) {
      return (T)this.channelCache.ofType(type).getElementById(id);
   }

   public GuildChannel getGuildChannelById(long id) {
      return (GuildChannel)this.channelCache.ofType(GuildChannel.class).getElementById(id);
   }

   public GuildChannel getGuildChannelById(@Nonnull ChannelType type, long id) {
      Channel channel = this.channelCache.getElementById(type, id);
      return channel instanceof GuildChannel ? (GuildChannel)channel : null;
   }

   @Nonnull
   public CacheRestAction<PrivateChannel> openPrivateChannelById(long userId) {
      if (this.selfUser != null && userId == this.selfUser.getIdLong()) {
         throw new UnsupportedOperationException("Cannot open private channel with yourself!");
      } else {
         return new DeferredRestAction(this, PrivateChannel.class, () -> {
            User user = this.getUserById(userId);
            return user instanceof UserImpl ? ((UserImpl)user).getPrivateChannel() : null;
         }, () -> {
            Route.CompiledRoute route = Route.Self.CREATE_PRIVATE_CHANNEL.compile();
            DataObject body = DataObject.empty().put("recipient_id", userId);
            return new RestActionImpl(this, route, body, (response, request) -> this.getEntityBuilder().createPrivateChannel(response.getObject()));
         });
      }
   }

   @Nonnull
   public SnowflakeCacheView<User> getUserCache() {
      return this.userCache;
   }

   public boolean hasSelfUser() {
      return this.selfUser != null;
   }

   @Nonnull
   public SelfUser getSelfUser() {
      if (this.selfUser == null) {
         throw new IllegalStateException("Session is not yet ready!");
      } else {
         return this.selfUser;
      }
   }

   public synchronized void shutdownNow() {
      this.requester.stop(true, this::shutdownRequester);
      this.shutdown();
      this.threadConfig.shutdownNow();
   }

   public synchronized void shutdown() {
      JDA.Status status = this.getStatus();
      if (status != JDA.Status.SHUTDOWN && status != JDA.Status.SHUTTING_DOWN) {
         this.setStatus(JDA.Status.SHUTTING_DOWN);
         WebSocketClient client = this.getClient();
         if (client != null) {
            client.getChunkManager().shutdown();
            client.shutdown();
         } else {
            this.shutdownInternals(new ShutdownEvent(this, OffsetDateTime.now(), 1000));
         }

      }
   }

   public void shutdownInternals(ShutdownEvent event) {
      if (this.getStatus() != JDA.Status.SHUTDOWN) {
         this.closeAudioConnections();
         this.guildSetupController.close();
         this.requester.stop(false, this::shutdownRequester);
         this.threadConfig.shutdown();
         if (this.shutdownHook != null) {
            try {
               Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
            } catch (Exception var3) {
            }
         }

         boolean signal = (Boolean)MiscUtil.locked(this.statusLock, (Supplier)(() -> this.shutdownEvent.getAndSet(event) == null && this.requesterShutdown.get()));
         if (signal) {
            this.signalShutdown();
         }

      }
   }

   public void shutdownRequester() {
      this.threadConfig.shutdownRequester();
      boolean signal = (Boolean)MiscUtil.locked(this.statusLock, (Supplier)(() -> !this.requesterShutdown.getAndSet(true) && this.shutdownEvent.get() != null));
      if (signal) {
         this.signalShutdown();
      }

   }

   private void signalShutdown() {
      this.setStatus(JDA.Status.SHUTDOWN);
      this.handleEvent((GenericEvent)this.shutdownEvent.get());
   }

   private void closeAudioConnections() {
      Stream var10000 = this.getAudioManagerCache().stream();
      Objects.requireNonNull(AudioManagerImpl.class);
      var10000.map(AudioManagerImpl.class::cast).forEach((m) -> m.closeAudioConnection(ConnectionStatus.SHUTTING_DOWN));
   }

   public long getResponseTotal() {
      return this.responseTotal;
   }

   public int getMaxReconnectDelay() {
      return this.sessionConfig.getMaxReconnectDelay();
   }

   @Nonnull
   public JDA.ShardInfo getShardInfo() {
      return this.shardInfo == null ? JDA.ShardInfo.SINGLE : this.shardInfo;
   }

   @Nonnull
   public Presence getPresence() {
      return this.presence;
   }

   @Nonnull
   public IEventManager getEventManager() {
      return this.eventManager.getSubject();
   }

   public void setEventManager(IEventManager eventManager) {
      this.eventManager.setSubject(eventManager);
   }

   public void addEventListener(@Nonnull Object... listeners) {
      Checks.noneNull(listeners, "listeners");

      for(Object listener : listeners) {
         this.eventManager.register(listener);
      }

   }

   public void removeEventListener(@Nonnull Object... listeners) {
      Checks.noneNull(listeners, "listeners");

      for(Object listener : listeners) {
         this.eventManager.unregister(listener);
      }

   }

   @Nonnull
   public List<Object> getRegisteredListeners() {
      return this.eventManager.getRegisteredListeners();
   }

   @Nonnull
   public <E extends GenericEvent> Once.Builder<E> listenOnce(@Nonnull Class<E> eventType) {
      return new Once.Builder<E>(this, eventType);
   }

   @Nonnull
   public RestAction<List<Command>> retrieveCommands(boolean withLocalizations) {
      Route.CompiledRoute route = Route.Interactions.GET_COMMANDS.compile(this.getSelfUser().getApplicationId()).withQueryParams("with_localizations", String.valueOf(withLocalizations));
      return new RestActionImpl<List<Command>>(this, route, (response, request) -> (List)response.getArray().stream(DataArray::getObject).map((json) -> new CommandImpl(this, (Guild)null, json)).collect(Collectors.toList()));
   }

   @Nonnull
   public RestAction<Command> retrieveCommandById(@Nonnull String id) {
      Checks.isSnowflake(id);
      Route.CompiledRoute route = Route.Interactions.GET_COMMAND.compile(this.getSelfUser().getApplicationId(), id);
      return new RestActionImpl<Command>(this, route, (response, request) -> new CommandImpl(this, (Guild)null, response.getObject()));
   }

   @Nonnull
   public CommandCreateAction upsertCommand(@Nonnull CommandData command) {
      Checks.notNull(command, "CommandData");
      return new CommandCreateActionImpl(this, (CommandDataImpl)command);
   }

   @Nonnull
   public CommandListUpdateAction updateCommands() {
      Route.CompiledRoute route = Route.Interactions.UPDATE_COMMANDS.compile(this.getSelfUser().getApplicationId());
      return new CommandListUpdateActionImpl(this, (GuildImpl)null, route);
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
      Route.CompiledRoute route = Route.Interactions.DELETE_COMMAND.compile(this.getSelfUser().getApplicationId(), commandId);
      return new RestActionImpl<Void>(this, route);
   }

   @Nonnull
   public RestAction<List<RoleConnectionMetadata>> retrieveRoleConnectionMetadata() {
      Route.CompiledRoute route = Route.Applications.GET_ROLE_CONNECTION_METADATA.compile(this.getSelfUser().getApplicationId());
      return new RestActionImpl<List<RoleConnectionMetadata>>(this, route, (response, request) -> (List)response.getArray().stream(DataArray::getObject).map(RoleConnectionMetadata::fromData).collect(Helpers.toUnmodifiableList()));
   }

   @Nonnull
   public RestAction<List<RoleConnectionMetadata>> updateRoleConnectionMetadata(@Nonnull Collection<? extends RoleConnectionMetadata> records) {
      Checks.noneNull(records, "Records");
      Checks.check(records.size() <= 5, "An application can have a maximum of %d metadata records", (int)5);
      Route.CompiledRoute route = Route.Applications.UPDATE_ROLE_CONNECTION_METADATA.compile(this.getSelfUser().getApplicationId());
      DataArray array = DataArray.fromCollection(records);
      RequestBody body = RequestBody.create(array.toJson(), Requester.MEDIA_TYPE_JSON);
      return new RestActionImpl<List<RoleConnectionMetadata>>(this, route, body, (response, request) -> (List)response.getArray().stream(DataArray::getObject).map(RoleConnectionMetadata::fromData).collect(Helpers.toUnmodifiableList()));
   }

   @Nonnull
   public RestAction<Webhook> retrieveWebhookById(@Nonnull String webhookId) {
      Checks.isSnowflake(webhookId, "Webhook ID");
      Route.CompiledRoute route = Route.Webhooks.GET_WEBHOOK.compile(webhookId);
      return new RestActionImpl<Webhook>(this, route, (response, request) -> {
         DataObject object = response.getObject();
         EntityBuilder builder = this.getEntityBuilder();
         return builder.createWebhook(object, true);
      });
   }

   @Nonnull
   public ApplicationManager getApplicationManager() {
      return new ApplicationManagerImpl(this);
   }

   @Nonnull
   public RestAction<ApplicationInfo> retrieveApplicationInfo() {
      Route.CompiledRoute route = Route.Applications.GET_BOT_APPLICATION.compile();
      return new RestActionImpl<ApplicationInfo>(this, route, (response, request) -> {
         ApplicationInfo info = this.getEntityBuilder().createApplicationInfo(response.getObject());
         this.clientId = info.getId();
         return info;
      });
   }

   @Nonnull
   public RestAction<List<SKU>> retrieveSKUList() {
      Route.CompiledRoute route = Route.Applications.GET_SKUS.compile(this.getSelfUser().getApplicationId());
      return new RestActionImpl<List<SKU>>(this, route, (response, request) -> (List)Helpers.mapGracefully(response.getArray().stream(DataArray::getObject), EntityBuilder::createSKU, "Failed to parse SKU").collect(Helpers.toUnmodifiableList()));
   }

   @Nonnull
   public EntitlementPaginationAction retrieveEntitlements() {
      return new EntitlementPaginationActionImpl(this);
   }

   @Nonnull
   public RestAction<Entitlement> retrieveEntitlementById(long entitlementId) {
      return new RestActionImpl<Entitlement>(this, Route.Applications.GET_ENTITLEMENT.compile(this.getSelfUser().getApplicationId(), Long.toUnsignedString(entitlementId)));
   }

   @Nonnull
   public TestEntitlementCreateAction createTestEntitlement(long skuId, long ownerId, @Nonnull TestEntitlementCreateAction.OwnerType ownerType) {
      Checks.notNull(ownerType, "ownerType");
      return new TestEntitlementCreateActionImpl(this, skuId, ownerId, ownerType);
   }

   @Nonnull
   public RestAction<Void> deleteTestEntitlement(long entitlementId) {
      Route.CompiledRoute route = Route.Applications.DELETE_TEST_ENTITLEMENT.compile(this.getSelfUser().getApplicationId(), Long.toUnsignedString(entitlementId));
      return new RestActionImpl<Void>(this, route);
   }

   @Nonnull
   public JDA setRequiredScopes(@Nonnull Collection<String> scopes) {
      Checks.noneNull(scopes, "Scopes");
      this.requiredScopes = String.join("+", scopes);
      if (!this.requiredScopes.contains("bot")) {
         if (this.requiredScopes.isEmpty()) {
            this.requiredScopes = "bot";
         } else {
            this.requiredScopes = this.requiredScopes + "+bot";
         }
      }

      return this;
   }

   @Nonnull
   public String getInviteUrl(Permission... permissions) {
      StringBuilder builder = this.buildBaseInviteUrl();
      if (permissions != null && permissions.length > 0) {
         builder.append("&permissions=").append(Permission.getRaw(permissions));
      }

      return builder.toString();
   }

   @Nonnull
   public String getInviteUrl(Collection<Permission> permissions) {
      StringBuilder builder = this.buildBaseInviteUrl();
      if (permissions != null && !permissions.isEmpty()) {
         builder.append("&permissions=").append(Permission.getRaw(permissions));
      }

      return builder.toString();
   }

   private StringBuilder buildBaseInviteUrl() {
      if (this.clientId == null) {
         if (this.selfUser != null) {
            this.clientId = this.selfUser.getApplicationId();
         } else {
            this.retrieveApplicationInfo().complete();
         }
      }

      StringBuilder builder = new StringBuilder("https://discord.com/oauth2/authorize?client_id=");
      builder.append(this.clientId);
      builder.append("&scope=").append(this.requiredScopes);
      return builder;
   }

   public void setShardManager(ShardManager shardManager) {
      this.shardManager = shardManager;
   }

   public ShardManager getShardManager() {
      return this.shardManager;
   }

   public EntityBuilder getEntityBuilder() {
      return this.entityBuilder;
   }

   public IAudioSendFactory getAudioSendFactory() {
      return this.audioModuleConfig.getAudioSendFactory();
   }

   public void setGatewayPing(long ping) {
      long oldPing = this.gatewayPing;
      this.gatewayPing = ping;
      this.handleEvent(new GatewayPingEvent(this, oldPing));
   }

   public Requester getRequester() {
      return this.requester;
   }

   public WebSocketFactory getWebSocketFactory() {
      return this.sessionConfig.getWebSocketFactory();
   }

   public WebSocketClient getClient() {
      return this.client;
   }

   public SnowflakeCacheViewImpl<User> getUsersView() {
      return this.userCache;
   }

   public SnowflakeCacheViewImpl<Guild> getGuildsView() {
      return this.guildCache;
   }

   public ChannelCacheViewImpl<Channel> getChannelsView() {
      return this.channelCache;
   }

   public AbstractCacheView<AudioManager> getAudioManagersView() {
      return this.audioManagers;
   }

   public void setSelfUser(SelfUser selfUser) {
      UnlockHook hook = this.userCache.writeLock();

      try {
         this.userCache.getMap().put(selfUser.getIdLong(), selfUser);
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

      this.selfUser = selfUser;
   }

   public void setResponseTotal(int responseTotal) {
      this.responseTotal = (long)responseTotal;
   }

   public String getIdentifierString() {
      return this.shardInfo != null ? "JDA " + this.shardInfo.getShardString() : "JDA";
   }

   public EventCache getEventCache() {
      return this.eventCache;
   }

   public String getGatewayUrl() {
      return this.gatewayUrl == null ? (this.gatewayUrl = this.getGateway()) : this.gatewayUrl;
   }

   public void resetGatewayUrl() {
      this.gatewayUrl = null;
   }

   public ScheduledExecutorService getAudioLifeCyclePool() {
      return this.threadConfig.getAudioPool(this::getIdentifierString);
   }
}
