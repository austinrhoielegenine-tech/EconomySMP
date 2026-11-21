package net.dv8tion.jda.api.sharding;

import com.neovisionaries.ws.client.WebSocketFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.annotations.ReplaceWith;
import net.dv8tion.jda.api.GatewayEncoding;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.audio.AudioModuleConfig;
import net.dv8tion.jda.api.audio.factory.DefaultSendFactory;
import net.dv8tion.jda.api.audio.factory.IAudioSendFactory;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestConfig;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.SessionController;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.concurrent.CountingThreadFactory;
import net.dv8tion.jda.internal.utils.config.flags.ConfigFlag;
import net.dv8tion.jda.internal.utils.config.flags.ShardingConfigFlag;
import net.dv8tion.jda.internal.utils.config.sharding.EventConfig;
import net.dv8tion.jda.internal.utils.config.sharding.PresenceProviderConfig;
import net.dv8tion.jda.internal.utils.config.sharding.ShardingConfig;
import net.dv8tion.jda.internal.utils.config.sharding.ShardingMetaConfig;
import net.dv8tion.jda.internal.utils.config.sharding.ShardingSessionConfig;
import net.dv8tion.jda.internal.utils.config.sharding.ThreadingProviderConfig;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;

public class DefaultShardManagerBuilder {
   protected final List<Object> listeners = new ArrayList();
   protected final List<IntFunction<Object>> listenerProviders = new ArrayList();
   protected final EnumSet<CacheFlag> automaticallyDisabled = EnumSet.noneOf(CacheFlag.class);
   protected SessionController sessionController = null;
   protected VoiceDispatchInterceptor voiceDispatchInterceptor = null;
   protected EnumSet<CacheFlag> cacheFlags = EnumSet.allOf(CacheFlag.class);
   protected EnumSet<ConfigFlag> flags = ConfigFlag.getDefault();
   protected EnumSet<ShardingConfigFlag> shardingFlags = ShardingConfigFlag.getDefault();
   protected Compression compression;
   protected GatewayEncoding encoding;
   protected int shardsTotal;
   protected int maxReconnectDelay;
   protected int largeThreshold;
   protected int maxBufferSize;
   protected int intents;
   protected String token;
   protected IntFunction<Boolean> idleProvider;
   protected IntFunction<OnlineStatus> statusProvider;
   protected IntFunction<? extends Activity> activityProvider;
   protected IntFunction<? extends ConcurrentMap<String, String>> contextProvider;
   protected IntFunction<? extends IEventManager> eventManagerProvider;
   protected ThreadPoolProvider<? extends ScheduledExecutorService> rateLimitSchedulerProvider;
   protected ThreadPoolProvider<? extends ExecutorService> rateLimitElasticProvider;
   protected ThreadPoolProvider<? extends ScheduledExecutorService> gatewayPoolProvider;
   protected ThreadPoolProvider<? extends ExecutorService> callbackPoolProvider;
   protected ThreadPoolProvider<? extends ExecutorService> eventPoolProvider;
   protected ThreadPoolProvider<? extends ScheduledExecutorService> audioPoolProvider;
   protected IntFunction<? extends RestConfig> restConfigProvider;
   protected Collection<Integer> shards;
   protected OkHttpClient.Builder httpClientBuilder;
   protected OkHttpClient httpClient;
   protected WebSocketFactory wsFactory;
   protected AudioModuleConfig audioModuleConfig;
   protected ThreadFactory threadFactory;
   protected ChunkingFilter chunkingFilter;
   protected MemberCachePolicy memberCachePolicy;

   protected DefaultShardManagerBuilder(@Nullable String token, int intents) {
      this.compression = Compression.ZLIB;
      this.encoding = GatewayEncoding.JSON;
      this.shardsTotal = -1;
      this.maxReconnectDelay = 900;
      this.largeThreshold = 250;
      this.maxBufferSize = 2048;
      this.intents = -1;
      this.token = null;
      this.idleProvider = null;
      this.statusProvider = null;
      this.activityProvider = null;
      this.contextProvider = null;
      this.eventManagerProvider = null;
      this.rateLimitSchedulerProvider = ThreadPoolProvider.<ScheduledExecutorService>lazy((total) -> Executors.newScheduledThreadPool(Math.max(2, 2 * (int)Math.log((double)total)), new CountingThreadFactory(() -> "JDA", "RateLimit-Scheduler", true)));
      this.rateLimitElasticProvider = ThreadPoolProvider.<ExecutorService>lazy((total) -> {
         ExecutorService pool = Executors.newCachedThreadPool(new CountingThreadFactory(() -> "JDA", "RateLimit-Elastic", true));
         if (pool instanceof ThreadPoolExecutor) {
            ((ThreadPoolExecutor)pool).setCorePoolSize(Math.max(1, (int)Math.log((double)total)));
            ((ThreadPoolExecutor)pool).setKeepAliveTime(2L, TimeUnit.MINUTES);
         }

         return pool;
      });
      this.gatewayPoolProvider = ThreadPoolProvider.<ScheduledExecutorService>lazy((total) -> Executors.newScheduledThreadPool(Math.max(1, (int)Math.log((double)total)), new CountingThreadFactory(() -> "JDA", "Gateway")));
      this.callbackPoolProvider = null;
      this.eventPoolProvider = null;
      this.audioPoolProvider = null;
      this.restConfigProvider = null;
      this.shards = null;
      this.httpClientBuilder = null;
      this.httpClient = null;
      this.wsFactory = null;
      this.audioModuleConfig = null;
      this.threadFactory = null;
      this.chunkingFilter = ChunkingFilter.ALL;
      this.memberCachePolicy = MemberCachePolicy.ALL;
      this.token = token;
      this.intents = 1 | intents;
   }

   @Nonnull
   @CheckReturnValue
   public static DefaultShardManagerBuilder createDefault(@Nullable String token) {
      return (new DefaultShardManagerBuilder(token, GatewayIntent.DEFAULT)).applyDefault();
   }

   @Nonnull
   @CheckReturnValue
   public static DefaultShardManagerBuilder createDefault(@Nullable String token, @Nonnull GatewayIntent intent, @Nonnull GatewayIntent... intents) {
      Checks.notNull(intent, "GatewayIntent");
      Checks.noneNull(intents, "GatewayIntent");
      return createDefault(token, EnumSet.of(intent, intents));
   }

   @Nonnull
   @CheckReturnValue
   public static DefaultShardManagerBuilder createDefault(@Nullable String token, @Nonnull Collection<GatewayIntent> intents) {
      return create(token, intents).applyDefault();
   }

   private DefaultShardManagerBuilder applyDefault() {
      return this.setMemberCachePolicy(MemberCachePolicy.DEFAULT).setChunkingFilter(ChunkingFilter.NONE).disableCache(CacheFlag.getPrivileged()).setLargeThreshold(250);
   }

   @Nonnull
   @CheckReturnValue
   public static DefaultShardManagerBuilder createLight(@Nullable String token) {
      return (new DefaultShardManagerBuilder(token, GatewayIntent.DEFAULT)).applyLight();
   }

   @Nonnull
   @CheckReturnValue
   public static DefaultShardManagerBuilder createLight(@Nullable String token, @Nonnull GatewayIntent intent, @Nonnull GatewayIntent... intents) {
      Checks.notNull(intent, "GatewayIntent");
      Checks.noneNull(intents, "GatewayIntent");
      return createLight(token, EnumSet.of(intent, intents));
   }

   @Nonnull
   @CheckReturnValue
   public static DefaultShardManagerBuilder createLight(@Nullable String token, @Nonnull Collection<GatewayIntent> intents) {
      return create(token, intents).applyLight();
   }

   private DefaultShardManagerBuilder applyLight() {
      return this.setMemberCachePolicy(MemberCachePolicy.NONE).setChunkingFilter(ChunkingFilter.NONE).disableCache(EnumSet.allOf(CacheFlag.class)).setLargeThreshold(50);
   }

   @Nonnull
   @CheckReturnValue
   public static DefaultShardManagerBuilder create(@Nonnull GatewayIntent intent, @Nonnull GatewayIntent... intents) {
      return create((String)null, intent, intents);
   }

   @Nonnull
   @CheckReturnValue
   public static DefaultShardManagerBuilder create(@Nonnull Collection<GatewayIntent> intents) {
      return create((String)null, (Collection)intents);
   }

   @Nonnull
   @CheckReturnValue
   public static DefaultShardManagerBuilder create(@Nullable String token, @Nonnull GatewayIntent intent, @Nonnull GatewayIntent... intents) {
      return (new DefaultShardManagerBuilder(token, GatewayIntent.getRaw(intent, intents))).applyIntents();
   }

   @Nonnull
   @CheckReturnValue
   public static DefaultShardManagerBuilder create(@Nullable String token, @Nonnull Collection<GatewayIntent> intents) {
      return (new DefaultShardManagerBuilder(token, GatewayIntent.getRaw(intents))).applyIntents();
   }

   private DefaultShardManagerBuilder applyIntents() {
      EnumSet<CacheFlag> disabledCache = EnumSet.allOf(CacheFlag.class);

      for(CacheFlag flag : CacheFlag.values()) {
         GatewayIntent requiredIntent = flag.getRequiredIntent();
         if (requiredIntent == null || (requiredIntent.getRawValue() & this.intents) != 0) {
            disabledCache.remove(flag);
         }
      }

      boolean enableMembers = (this.intents & GatewayIntent.GUILD_MEMBERS.getRawValue()) != 0;
      return this.setChunkingFilter(enableMembers ? ChunkingFilter.ALL : ChunkingFilter.NONE).setMemberCachePolicy(enableMembers ? MemberCachePolicy.ALL : MemberCachePolicy.DEFAULT).setDisabledCache(disabledCache);
   }

   private DefaultShardManagerBuilder setDisabledCache(EnumSet<CacheFlag> flags) {
      this.disableCache(flags);
      this.automaticallyDisabled.addAll(flags);
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setGatewayEncoding(@Nonnull GatewayEncoding encoding) {
      Checks.notNull(encoding, "GatewayEncoding");
      this.encoding = encoding;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setRawEventsEnabled(boolean enable) {
      return this.setFlag(ConfigFlag.RAW_EVENTS, enable);
   }

   @Nonnull
   public DefaultShardManagerBuilder setEventPassthrough(boolean enable) {
      return this.setFlag(ConfigFlag.EVENT_PASSTHROUGH, enable);
   }

   @Nonnull
   public DefaultShardManagerBuilder setRestConfigProvider(@Nonnull IntFunction<? extends RestConfig> provider) {
      Checks.notNull(provider, "RestConfig Provider");
      this.restConfigProvider = provider;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setRestConfig(@Nonnull RestConfig config) {
      Checks.notNull(config, "RestConfig");
      return this.setRestConfigProvider((ignored) -> config);
   }

   @Nonnull
   public DefaultShardManagerBuilder enableCache(@Nonnull Collection<CacheFlag> flags) {
      Checks.noneNull(flags, "CacheFlags");
      this.cacheFlags.addAll(flags);
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder enableCache(@Nonnull CacheFlag flag, @Nonnull CacheFlag... flags) {
      Checks.notNull(flag, "CacheFlag");
      Checks.noneNull(flags, "CacheFlag");
      this.cacheFlags.addAll(EnumSet.of(flag, flags));
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder disableCache(@Nonnull Collection<CacheFlag> flags) {
      Checks.noneNull(flags, "CacheFlags");
      this.automaticallyDisabled.removeAll(flags);
      this.cacheFlags.removeAll(flags);
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder disableCache(@Nonnull CacheFlag flag, @Nonnull CacheFlag... flags) {
      Checks.notNull(flag, "CacheFlag");
      Checks.noneNull(flags, "CacheFlag");
      return this.disableCache(EnumSet.of(flag, flags));
   }

   @Nonnull
   public DefaultShardManagerBuilder setMemberCachePolicy(@Nullable MemberCachePolicy policy) {
      if (policy == null) {
         this.memberCachePolicy = MemberCachePolicy.ALL;
      } else {
         this.memberCachePolicy = policy;
      }

      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setSessionController(@Nullable SessionController controller) {
      this.sessionController = controller;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setVoiceDispatchInterceptor(@Nullable VoiceDispatchInterceptor interceptor) {
      this.voiceDispatchInterceptor = interceptor;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setContextMap(@Nullable IntFunction<? extends ConcurrentMap<String, String>> provider) {
      this.contextProvider = provider;
      if (provider != null) {
         this.setContextEnabled(true);
      }

      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setContextEnabled(boolean enable) {
      return this.setFlag(ConfigFlag.MDC_CONTEXT, enable);
   }

   @Nonnull
   public DefaultShardManagerBuilder setCompression(@Nonnull Compression compression) {
      Checks.notNull(compression, "Compression");
      this.compression = compression;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder addEventListeners(@Nonnull Object... listeners) {
      return this.addEventListeners((Collection)Arrays.asList(listeners));
   }

   @Nonnull
   public DefaultShardManagerBuilder addEventListeners(@Nonnull Collection<Object> listeners) {
      Checks.noneNull(listeners, "listeners");
      this.listeners.addAll(listeners);
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder removeEventListeners(@Nonnull Object... listeners) {
      return this.removeEventListeners((Collection)Arrays.asList(listeners));
   }

   @Nonnull
   public DefaultShardManagerBuilder removeEventListeners(@Nonnull Collection<Object> listeners) {
      Checks.noneNull(listeners, "listeners");
      this.listeners.removeAll(listeners);
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder addEventListenerProvider(@Nonnull IntFunction<Object> listenerProvider) {
      return this.addEventListenerProviders(Collections.singleton(listenerProvider));
   }

   @Nonnull
   public DefaultShardManagerBuilder addEventListenerProviders(@Nonnull Collection<IntFunction<Object>> listenerProviders) {
      Checks.noneNull(listenerProviders, "listener providers");
      this.listenerProviders.addAll(listenerProviders);
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder removeEventListenerProvider(@Nonnull IntFunction<Object> listenerProvider) {
      return this.removeEventListenerProviders(Collections.singleton(listenerProvider));
   }

   @Nonnull
   public DefaultShardManagerBuilder removeEventListenerProviders(@Nonnull Collection<IntFunction<Object>> listenerProviders) {
      Checks.noneNull(listenerProviders, "listener providers");
      this.listenerProviders.removeAll(listenerProviders);
      return this;
   }

   /** @deprecated */
   @Nonnull
   @Deprecated
   @ReplaceWith("setAudioModuleConfig(new AudioModuleConfig().withAudioSendFactory(factory))")
   public DefaultShardManagerBuilder setAudioSendFactory(@Nullable IAudioSendFactory factory) {
      if (this.audioModuleConfig == null) {
         this.audioModuleConfig = new AudioModuleConfig();
      }

      this.audioModuleConfig = this.audioModuleConfig.withAudioSendFactory((IAudioSendFactory)(factory == null ? new DefaultSendFactory() : factory));
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setAudioModuleConfig(@Nullable AudioModuleConfig config) {
      this.audioModuleConfig = config;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setAutoReconnect(boolean autoReconnect) {
      return this.setFlag(ConfigFlag.AUTO_RECONNECT, autoReconnect);
   }

   @Nonnull
   public DefaultShardManagerBuilder setBulkDeleteSplittingEnabled(boolean enabled) {
      return this.setFlag(ConfigFlag.BULK_DELETE_SPLIT, enabled);
   }

   @Nonnull
   public DefaultShardManagerBuilder setEnableShutdownHook(boolean enable) {
      return this.setFlag(ConfigFlag.SHUTDOWN_HOOK, enable);
   }

   @Nonnull
   public DefaultShardManagerBuilder setEventManagerProvider(@Nonnull IntFunction<? extends IEventManager> eventManagerProvider) {
      Checks.notNull(eventManagerProvider, "eventManagerProvider");
      this.eventManagerProvider = eventManagerProvider;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setActivity(@Nullable Activity activity) {
      return this.setActivityProvider((id) -> activity);
   }

   @Nonnull
   public DefaultShardManagerBuilder setActivityProvider(@Nullable IntFunction<? extends Activity> activityProvider) {
      this.activityProvider = activityProvider;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setIdle(boolean idle) {
      return this.setIdleProvider((id) -> idle);
   }

   @Nonnull
   public DefaultShardManagerBuilder setIdleProvider(@Nullable IntFunction<Boolean> idleProvider) {
      this.idleProvider = idleProvider;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setStatus(@Nullable OnlineStatus status) {
      Checks.notNull(status, "status");
      Checks.check(status != OnlineStatus.UNKNOWN, "OnlineStatus cannot be unknown!");
      return this.setStatusProvider((id) -> status);
   }

   @Nonnull
   public DefaultShardManagerBuilder setStatusProvider(@Nullable IntFunction<OnlineStatus> statusProvider) {
      this.statusProvider = statusProvider;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setThreadFactory(@Nullable ThreadFactory threadFactory) {
      this.threadFactory = threadFactory;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setHttpClientBuilder(@Nullable OkHttpClient.Builder builder) {
      this.httpClientBuilder = builder;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setHttpClient(@Nullable OkHttpClient client) {
      this.httpClient = client;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setRateLimitScheduler(@Nullable ScheduledExecutorService pool) {
      return this.setRateLimitScheduler(pool, pool == null);
   }

   @Nonnull
   public DefaultShardManagerBuilder setRateLimitScheduler(@Nullable ScheduledExecutorService pool, boolean automaticShutdown) {
      return this.setRateLimitSchedulerProvider(pool == null ? null : new ThreadPoolProviderImpl(pool, automaticShutdown));
   }

   @Nonnull
   public DefaultShardManagerBuilder setRateLimitSchedulerProvider(@Nullable ThreadPoolProvider<? extends ScheduledExecutorService> provider) {
      this.rateLimitSchedulerProvider = provider;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setRateLimitElastic(@Nullable ExecutorService pool) {
      return this.setRateLimitElastic(pool, pool == null);
   }

   @Nonnull
   public DefaultShardManagerBuilder setRateLimitElastic(@Nullable ExecutorService pool, boolean automaticShutdown) {
      return this.setRateLimitElasticProvider(pool == null ? null : new ThreadPoolProviderImpl(pool, automaticShutdown));
   }

   @Nonnull
   public DefaultShardManagerBuilder setRateLimitElasticProvider(@Nullable ThreadPoolProvider<? extends ExecutorService> provider) {
      this.rateLimitElasticProvider = provider;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setGatewayPool(@Nullable ScheduledExecutorService pool) {
      return this.setGatewayPool(pool, pool == null);
   }

   @Nonnull
   public DefaultShardManagerBuilder setGatewayPool(@Nullable ScheduledExecutorService pool, boolean automaticShutdown) {
      return this.setGatewayPoolProvider(pool == null ? null : new ThreadPoolProviderImpl(pool, automaticShutdown));
   }

   @Nonnull
   public DefaultShardManagerBuilder setGatewayPoolProvider(@Nullable ThreadPoolProvider<? extends ScheduledExecutorService> provider) {
      this.gatewayPoolProvider = provider;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setCallbackPool(@Nullable ExecutorService executor) {
      return this.setCallbackPool(executor, executor == null);
   }

   @Nonnull
   public DefaultShardManagerBuilder setCallbackPool(@Nullable ExecutorService executor, boolean automaticShutdown) {
      return this.setCallbackPoolProvider(executor == null ? null : new ThreadPoolProviderImpl(executor, automaticShutdown));
   }

   @Nonnull
   public DefaultShardManagerBuilder setCallbackPoolProvider(@Nullable ThreadPoolProvider<? extends ExecutorService> provider) {
      this.callbackPoolProvider = provider;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setEventPool(@Nullable ExecutorService executor) {
      return this.setEventPool(executor, executor == null);
   }

   @Nonnull
   public DefaultShardManagerBuilder setEventPool(@Nullable ExecutorService executor, boolean automaticShutdown) {
      return this.setEventPoolProvider(executor == null ? null : new ThreadPoolProviderImpl(executor, automaticShutdown));
   }

   @Nonnull
   public DefaultShardManagerBuilder setEventPoolProvider(@Nullable ThreadPoolProvider<? extends ExecutorService> provider) {
      this.eventPoolProvider = provider;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setAudioPool(@Nullable ScheduledExecutorService pool) {
      return this.setAudioPool(pool, pool == null);
   }

   @Nonnull
   public DefaultShardManagerBuilder setAudioPool(@Nullable ScheduledExecutorService pool, boolean automaticShutdown) {
      return this.setAudioPoolProvider(pool == null ? null : new ThreadPoolProviderImpl(pool, automaticShutdown));
   }

   @Nonnull
   public DefaultShardManagerBuilder setAudioPoolProvider(@Nullable ThreadPoolProvider<? extends ScheduledExecutorService> provider) {
      this.audioPoolProvider = provider;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setMaxReconnectDelay(int maxReconnectDelay) {
      Checks.check(maxReconnectDelay >= 32, "Max reconnect delay must be 32 seconds or greater. You provided %d.", (Object)maxReconnectDelay);
      this.maxReconnectDelay = maxReconnectDelay;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setRequestTimeoutRetry(boolean retryOnTimeout) {
      return this.setFlag(ConfigFlag.RETRY_TIMEOUT, retryOnTimeout);
   }

   @Nonnull
   public DefaultShardManagerBuilder setShards(@Nonnull int... shardIds) {
      Checks.notNull(shardIds, "shardIds");

      for(int id : shardIds) {
         Checks.notNegative(id, "minShardId");
         Checks.check(id < this.shardsTotal, "maxShardId must be lower than shardsTotal");
      }

      this.shards = (Collection)Arrays.stream(shardIds).boxed().collect(Collectors.toSet());
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setShards(int minShardId, int maxShardId) {
      Checks.notNegative(minShardId, "minShardId");
      Checks.check(maxShardId < this.shardsTotal, "maxShardId must be lower than shardsTotal");
      Checks.check(minShardId <= maxShardId, "minShardId must be lower than or equal to maxShardId");
      List<Integer> shards = new ArrayList(maxShardId - minShardId + 1);

      for(int i = minShardId; i <= maxShardId; ++i) {
         shards.add(i);
      }

      this.shards = shards;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setShards(@Nonnull Collection<Integer> shardIds) {
      Checks.notNull(shardIds, "shardIds");

      for(Integer id : shardIds) {
         Checks.notNegative(id, "minShardId");
         Checks.check(id < this.shardsTotal, "maxShardId must be lower than shardsTotal");
      }

      this.shards = new ArrayList(shardIds);
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setShardsTotal(int shardsTotal) {
      Checks.check(shardsTotal == -1 || shardsTotal > 0, "shardsTotal must either be -1 or greater than 0");
      this.shardsTotal = shardsTotal;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setToken(@Nonnull String token) {
      Checks.notBlank(token, "token");
      this.token = token;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setUseShutdownNow(boolean useShutdownNow) {
      return this.setFlag(ShardingConfigFlag.SHUTDOWN_NOW, useShutdownNow);
   }

   @Nonnull
   public DefaultShardManagerBuilder setWebsocketFactory(@Nullable WebSocketFactory factory) {
      this.wsFactory = factory;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setChunkingFilter(@Nullable ChunkingFilter filter) {
      this.chunkingFilter = filter;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setDisabledIntents(@Nonnull GatewayIntent intent, @Nonnull GatewayIntent... intents) {
      Checks.notNull(intent, "Intent");
      Checks.noneNull(intents, "Intent");
      EnumSet<GatewayIntent> set = EnumSet.of(intent, intents);
      return this.setDisabledIntents(set);
   }

   @Nonnull
   public DefaultShardManagerBuilder setDisabledIntents(@Nullable Collection<GatewayIntent> intents) {
      this.intents = GatewayIntent.ALL_INTENTS;
      if (intents != null) {
         this.intents = 1 | GatewayIntent.ALL_INTENTS & ~GatewayIntent.getRaw(intents);
      }

      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder disableIntents(@Nonnull Collection<GatewayIntent> intents) {
      Checks.noneNull(intents, "GatewayIntent");
      int raw = GatewayIntent.getRaw(intents);
      this.intents &= ~raw;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder disableIntents(@Nonnull GatewayIntent intent, @Nonnull GatewayIntent... intents) {
      Checks.notNull(intent, "GatewayIntent");
      Checks.noneNull(intents, "GatewayIntent");
      int raw = GatewayIntent.getRaw(intent, intents);
      this.intents &= ~raw;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setEnabledIntents(@Nonnull GatewayIntent intent, @Nonnull GatewayIntent... intents) {
      Checks.notNull(intent, "Intent");
      Checks.noneNull(intents, "Intent");
      EnumSet<GatewayIntent> set = EnumSet.of(intent, intents);
      return this.setEnabledIntents(set);
   }

   @Nonnull
   public DefaultShardManagerBuilder setEnabledIntents(@Nullable Collection<GatewayIntent> intents) {
      if (intents != null && !intents.isEmpty()) {
         this.intents = 1 | GatewayIntent.getRaw(intents);
      } else {
         this.intents = 1;
      }

      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder enableIntents(@Nonnull Collection<GatewayIntent> intents) {
      Checks.noneNull(intents, "GatewayIntent");
      int raw = GatewayIntent.getRaw(intents);
      this.intents |= raw;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder enableIntents(@Nonnull GatewayIntent intent, @Nonnull GatewayIntent... intents) {
      Checks.notNull(intent, "GatewayIntent");
      Checks.noneNull(intents, "GatewayIntent");
      int raw = GatewayIntent.getRaw(intent, intents);
      this.intents |= raw;
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setLargeThreshold(int threshold) {
      this.largeThreshold = Math.max(50, Math.min(250, threshold));
      return this;
   }

   @Nonnull
   public DefaultShardManagerBuilder setMaxBufferSize(int bufferSize) {
      Checks.notNegative(bufferSize, "The buffer size");
      this.maxBufferSize = bufferSize;
      return this;
   }

   @Nonnull
   public ShardManager build() throws IllegalArgumentException {
      return this.build(true);
   }

   @Nonnull
   public ShardManager build(boolean login) throws IllegalArgumentException {
      this.checkIntents();
      boolean useShutdownNow = this.shardingFlags.contains(ShardingConfigFlag.SHUTDOWN_NOW);
      ShardingConfig shardingConfig = new ShardingConfig(this.shardsTotal, useShutdownNow, this.intents, this.memberCachePolicy);
      EventConfig eventConfig = new EventConfig(this.eventManagerProvider);
      List var10000 = this.listeners;
      Objects.requireNonNull(eventConfig);
      var10000.forEach(eventConfig::addEventListener);
      var10000 = this.listenerProviders;
      Objects.requireNonNull(eventConfig);
      var10000.forEach(eventConfig::addEventListenerProvider);
      PresenceProviderConfig presenceConfig = new PresenceProviderConfig();
      presenceConfig.setActivityProvider(this.activityProvider);
      presenceConfig.setStatusProvider(this.statusProvider);
      presenceConfig.setIdleProvider(this.idleProvider);
      ThreadingProviderConfig threadingConfig = new ThreadingProviderConfig(this.rateLimitSchedulerProvider, this.rateLimitElasticProvider, this.gatewayPoolProvider, this.callbackPoolProvider, this.eventPoolProvider, this.audioPoolProvider, this.threadFactory);
      ShardingSessionConfig sessionConfig = new ShardingSessionConfig(this.sessionController, this.voiceDispatchInterceptor, this.httpClient, this.httpClientBuilder, this.wsFactory, this.flags, this.shardingFlags, this.maxReconnectDelay, this.largeThreshold);
      ShardingMetaConfig metaConfig = new ShardingMetaConfig(this.maxBufferSize, this.contextProvider, this.cacheFlags, this.flags, this.compression, this.encoding);
      DefaultShardManager manager = new DefaultShardManager(this.token, this.shards, shardingConfig, eventConfig, presenceConfig, threadingConfig, sessionConfig, metaConfig, this.restConfigProvider, this.audioModuleConfig, this.chunkingFilter);
      if (login) {
         manager.login();
      }

      return manager;
   }

   private DefaultShardManagerBuilder setFlag(ConfigFlag flag, boolean enable) {
      if (enable) {
         this.flags.add(flag);
      } else {
         this.flags.remove(flag);
      }

      return this;
   }

   private DefaultShardManagerBuilder setFlag(ShardingConfigFlag flag, boolean enable) {
      if (enable) {
         this.shardingFlags.add(flag);
      } else {
         this.shardingFlags.remove(flag);
      }

      return this;
   }

   private void checkIntents() {
      boolean membersIntent = (this.intents & GatewayIntent.GUILD_MEMBERS.getRawValue()) != 0;
      if (!membersIntent && this.memberCachePolicy == MemberCachePolicy.ALL) {
         throw new IllegalStateException("Cannot use MemberCachePolicy.ALL without GatewayIntent.GUILD_MEMBERS enabled!");
      } else {
         if (!membersIntent && this.chunkingFilter != ChunkingFilter.NONE) {
            DefaultShardManager.LOG.warn("Member chunking is disabled due to missing GUILD_MEMBERS intent.");
         }

         if (!this.automaticallyDisabled.isEmpty()) {
            JDAImpl.LOG.warn("Automatically disabled CacheFlags due to missing intents");
            Stream var10000 = this.automaticallyDisabled.stream().map((it) -> "Disabled CacheFlag." + it + " (missing GatewayIntent." + it.getRequiredIntent() + ")");
            Logger var10001 = JDAImpl.LOG;
            Objects.requireNonNull(var10001);
            var10000.forEach(var10001::warn);
            JDAImpl.LOG.warn("You can manually disable these flags to remove this warning by using disableCache({}) on your DefaultShardManagerBuilder", this.automaticallyDisabled.stream().map((it) -> "CacheFlag." + it).collect(Collectors.joining(", ")));
            this.automaticallyDisabled.clear();
         }

         if (!this.cacheFlags.isEmpty()) {
            EnumSet<GatewayIntent> providedIntents = GatewayIntent.getIntents(this.intents);

            for(CacheFlag flag : this.cacheFlags) {
               GatewayIntent intent = flag.getRequiredIntent();
               if (intent != null && !providedIntents.contains(intent)) {
                  throw new IllegalArgumentException("Cannot use CacheFlag." + flag + " without GatewayIntent." + intent + "!");
               }
            }

         }
      }
   }

   private static class ThreadPoolProviderImpl<T extends ExecutorService> implements ThreadPoolProvider<T> {
      private final boolean autoShutdown;
      private final T pool;

      private ThreadPoolProviderImpl(T pool, boolean autoShutdown) {
         this.autoShutdown = autoShutdown;
         this.pool = pool;
      }

      public T provide(int shardId) {
         return this.pool;
      }

      public boolean shouldShutdownAutomatically(int shardId) {
         return this.autoShutdown;
      }
   }
}
