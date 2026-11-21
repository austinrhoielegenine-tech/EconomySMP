package net.dv8tion.jda.api.sharding;

import gnu.trove.set.TIntSet;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.audio.AudioModuleConfig;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestConfig;
import net.dv8tion.jda.api.requests.RestRateLimiter;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.SequentialRestRateLimiter;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.SessionController;
import net.dv8tion.jda.api.utils.cache.ShardCacheView;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.SelfUserImpl;
import net.dv8tion.jda.internal.managers.PresenceImpl;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.IOUtil;
import net.dv8tion.jda.internal.utils.JDALogger;
import net.dv8tion.jda.internal.utils.UnlockHook;
import net.dv8tion.jda.internal.utils.cache.ShardCacheViewImpl;
import net.dv8tion.jda.internal.utils.config.AuthorizationConfig;
import net.dv8tion.jda.internal.utils.config.MetaConfig;
import net.dv8tion.jda.internal.utils.config.SessionConfig;
import net.dv8tion.jda.internal.utils.config.ThreadingConfig;
import net.dv8tion.jda.internal.utils.config.sharding.EventConfig;
import net.dv8tion.jda.internal.utils.config.sharding.PresenceProviderConfig;
import net.dv8tion.jda.internal.utils.config.sharding.ShardingConfig;
import net.dv8tion.jda.internal.utils.config.sharding.ShardingMetaConfig;
import net.dv8tion.jda.internal.utils.config.sharding.ShardingSessionConfig;
import net.dv8tion.jda.internal.utils.config.sharding.ThreadingProviderConfig;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;

public class DefaultShardManager implements ShardManager {
   public static final Logger LOG = JDALogger.getLog(ShardManager.class);
   public static final ThreadFactory DEFAULT_THREAD_FACTORY = (r) -> new Thread(r, "DefaultShardManager");
   protected final ScheduledExecutorService executor;
   protected final Queue<Integer> queue;
   protected ShardCacheViewImpl shards;
   protected final AtomicBoolean shutdown;
   protected final Thread shutdownHook;
   protected final String token;
   protected Future<?> worker;
   protected String gatewayURL;
   protected final PresenceProviderConfig presenceConfig;
   protected final EventConfig eventConfig;
   protected final ShardingConfig shardingConfig;
   protected final ThreadingProviderConfig threadingConfig;
   protected final ShardingSessionConfig sessionConfig;
   protected final ShardingMetaConfig metaConfig;
   protected final ChunkingFilter chunkingFilter;
   protected final IntFunction<? extends RestConfig> restConfigProvider;
   @Nullable
   protected final AudioModuleConfig audioModuleConfig;

   public DefaultShardManager(@Nonnull String token) {
      this(token, (Collection)null);
   }

   public DefaultShardManager(@Nonnull String token, @Nullable Collection<Integer> shardIds) {
      this(token, shardIds, (ShardingConfig)null, (EventConfig)null, (PresenceProviderConfig)null, (ThreadingProviderConfig)null, (ShardingSessionConfig)null, (ShardingMetaConfig)null, (IntFunction)null, (AudioModuleConfig)null, (ChunkingFilter)null);
   }

   public DefaultShardManager(@Nonnull String token, @Nullable Collection<Integer> shardIds, @Nullable ShardingConfig shardingConfig, @Nullable EventConfig eventConfig, @Nullable PresenceProviderConfig presenceConfig, @Nullable ThreadingProviderConfig threadingConfig, @Nullable ShardingSessionConfig sessionConfig, @Nullable ShardingMetaConfig metaConfig, @Nullable IntFunction<? extends RestConfig> restConfigProvider, @Nullable AudioModuleConfig audioModuleConfig, @Nullable ChunkingFilter chunkingFilter) {
      this.queue = new ConcurrentLinkedQueue();
      this.shutdown = new AtomicBoolean(false);
      this.token = token;
      this.eventConfig = eventConfig == null ? EventConfig.getDefault() : eventConfig;
      this.shardingConfig = shardingConfig == null ? ShardingConfig.getDefault() : shardingConfig;
      this.threadingConfig = threadingConfig == null ? ThreadingProviderConfig.getDefault() : threadingConfig;
      this.sessionConfig = sessionConfig == null ? ShardingSessionConfig.getDefault() : sessionConfig;
      this.presenceConfig = presenceConfig == null ? PresenceProviderConfig.getDefault() : presenceConfig;
      this.metaConfig = metaConfig == null ? ShardingMetaConfig.getDefault() : metaConfig;
      this.chunkingFilter = chunkingFilter == null ? ChunkingFilter.ALL : chunkingFilter;
      this.restConfigProvider = restConfigProvider == null ? (ix) -> new RestConfig() : restConfigProvider;
      this.audioModuleConfig = audioModuleConfig;
      this.executor = this.createExecutor(this.threadingConfig.getThreadFactory());
      this.shutdownHook = this.metaConfig.isUseShutdownHook() ? new Thread(this::shutdown, "JDA Shutdown Hook") : null;
      synchronized(this.queue) {
         if (this.getShardsTotal() != -1) {
            if (shardIds == null) {
               this.shards = new ShardCacheViewImpl(this.getShardsTotal());

               for(int i = 0; i < this.getShardsTotal(); ++i) {
                  this.queue.add(i);
               }
            } else {
               this.shards = new ShardCacheViewImpl(shardIds.size());
               Stream var10000 = shardIds.stream().distinct().sorted();
               Queue var10001 = this.queue;
               Objects.requireNonNull(var10001);
               var10000.forEach(var10001::add);
            }
         }

      }
   }

   @Nonnull
   public EnumSet<GatewayIntent> getGatewayIntents() {
      return GatewayIntent.getIntents(this.shardingConfig.getIntents());
   }

   public void addEventListener(@Nonnull Object... listeners) {
      ShardManager.super.addEventListener(listeners);

      for(Object o : listeners) {
         this.eventConfig.addEventListener(o);
      }

   }

   public void removeEventListener(@Nonnull Object... listeners) {
      ShardManager.super.removeEventListener(listeners);

      for(Object o : listeners) {
         this.eventConfig.removeEventListener(o);
      }

   }

   public void addEventListeners(@Nonnull IntFunction<Object> eventListenerProvider) {
      ShardManager.super.addEventListeners(eventListenerProvider);
      this.eventConfig.addEventListenerProvider(eventListenerProvider);
   }

   public void removeEventListenerProvider(@Nonnull IntFunction<Object> eventListenerProvider) {
      this.eventConfig.removeEventListenerProvider(eventListenerProvider);
   }

   public int getShardsQueued() {
      return this.queue.size();
   }

   public int getShardsTotal() {
      return this.shardingConfig.getShardsTotal();
   }

   @Nullable
   public Guild getGuildById(long id) {
      int shardId = MiscUtil.getShardForGuild(id, this.getShardsTotal());
      JDA shard = this.getShardById(shardId);
      return shard == null ? null : shard.getGuildById(id);
   }

   @Nonnull
   public ShardCacheView getShardCache() {
      return this.shards;
   }

   public void login() {
      JDAImpl jda = null;

      try {
         int shardId = this.queue.isEmpty() ? 0 : (Integer)this.queue.peek();
         jda = this.buildInstance(shardId);
         UnlockHook hook = this.shards.writeLock();

         try {
            this.shards.getMap().put(shardId, jda);
         } catch (Throwable var9) {
            if (hook != null) {
               try {
                  hook.close();
               } catch (Throwable var7) {
                  var9.addSuppressed(var7);
               }
            }

            throw var9;
         }

         if (hook != null) {
            hook.close();
         }

         synchronized(this.queue) {
            this.queue.remove(shardId);
         }
      } catch (Exception e) {
         if (jda != null) {
            if (this.shardingConfig.isUseShutdownNow()) {
               jda.shutdownNow();
            } else {
               jda.shutdown();
            }
         }

         throw e;
      }

      this.runQueueWorker();
      if (this.shutdownHook != null) {
         Runtime.getRuntime().addShutdownHook(this.shutdownHook);
      }

   }

   public void restart(int shardId) {
      Checks.notNegative(shardId, "shardId");
      Checks.check(shardId < this.getShardsTotal(), "shardId must be lower than shardsTotal");
      JDA jda = this.shards.remove(shardId);
      if (jda != null) {
         if (this.shardingConfig.isUseShutdownNow()) {
            jda.shutdownNow();
         } else {
            jda.shutdown();
         }
      }

      this.enqueueShard(shardId);
   }

   public void restart() {
      TIntSet map = this.shards.keySet();
      Arrays.stream(map.toArray()).sorted().forEach(this::restart);
   }

   public void shutdown() {
      if (!this.shutdown.getAndSet(true)) {
         if (this.worker != null && !this.worker.isDone()) {
            this.worker.cancel(true);
         }

         if (this.shutdownHook != null) {
            try {
               Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
            } catch (Exception var2) {
            }
         }

         if (this.shards != null) {
            this.executor.execute(() -> {
               synchronized(this.queue) {
                  this.shards.forEach((jda) -> {
                     if (this.shardingConfig.isUseShutdownNow()) {
                        jda.shutdownNow();
                     } else {
                        jda.shutdown();
                     }

                  });
                  this.queue.clear();
               }

               this.executor.shutdown();
            });
         } else {
            this.executor.shutdown();
         }

         this.threadingConfig.shutdown();
      }
   }

   public void shutdown(int shardId) {
      JDA jda = this.shards.remove(shardId);
      if (jda != null) {
         if (this.shardingConfig.isUseShutdownNow()) {
            jda.shutdownNow();
         } else {
            jda.shutdown();
         }
      }

   }

   public void start(int shardId) {
      Checks.notNegative(shardId, "shardId");
      Checks.check(shardId < this.getShardsTotal(), "shardId must be lower than shardsTotal");
      this.enqueueShard(shardId);
   }

   protected void enqueueShard(int shardId) {
      synchronized(this.queue) {
         this.queue.add(shardId);
         this.runQueueWorker();
      }
   }

   protected void runQueueWorker() {
      if (this.shutdown.get()) {
         throw new RejectedExecutionException("ShardManager is already shutdown!");
      } else if (this.worker == null) {
         this.worker = this.executor.submit(() -> {
            while(!this.queue.isEmpty() && !Thread.currentThread().isInterrupted()) {
               this.processQueue();
            }

            this.gatewayURL = null;
            synchronized(this.queue) {
               this.worker = null;
               if (!this.shutdown.get() && !this.queue.isEmpty()) {
                  this.runQueueWorker();
               }

            }
         });
      }
   }

   protected void processQueue() {
      int shardId;
      if (this.shards == null) {
         shardId = 0;
      } else {
         Integer tmp = (Integer)this.queue.peek();
         shardId = tmp == null ? -1 : tmp;
      }

      if (shardId != -1) {
         JDAImpl api;
         try {
            api = this.shards == null ? null : (JDAImpl)this.shards.getElementById(shardId);
            if (api == null) {
               api = this.buildInstance(shardId);
            }
         } catch (CompletionException e) {
            if (e.getCause() instanceof InterruptedException) {
               LOG.debug("The worker thread was interrupted");
            } else {
               LOG.error((String)"Caught an exception in queue processing thread", (Throwable)e);
            }

            return;
         } catch (InvalidTokenException e) {
            LOG.warn((String)"The token has been invalidated and the ShardManager will shutdown!", (Throwable)e);
            this.shutdown();
            return;
         } catch (Exception e) {
            LOG.error((String)"Caught an exception in the queue processing thread", (Throwable)e);
            return;
         }

         UnlockHook hook = this.shards.writeLock();

         try {
            this.shards.getMap().put(shardId, api);
         } catch (Throwable var9) {
            if (hook != null) {
               try {
                  hook.close();
               } catch (Throwable var7) {
                  var9.addSuppressed(var7);
               }
            }

            throw var9;
         }

         if (hook != null) {
            hook.close();
         }

         synchronized(this.queue) {
            this.queue.remove(shardId);
         }
      }
   }

   protected JDAImpl buildInstance(int shardId) {
      OkHttpClient httpClient = this.sessionConfig.getHttpClient();
      if (httpClient == null) {
         httpClient = this.sessionConfig.getHttpBuilder().build();
      }

      this.retrieveShardTotal(httpClient);
      this.threadingConfig.init(this.queue.isEmpty() ? this.getShardsTotal() : this.queue.size());
      ExecutorPair<ScheduledExecutorService> rateLimitSchedulerPair = resolveExecutor(this.threadingConfig.getRateLimitSchedulerProvider(), shardId);
      ScheduledExecutorService rateLimitScheduler = (ScheduledExecutorService)rateLimitSchedulerPair.executor;
      boolean shutdownRateLimitScheduler = rateLimitSchedulerPair.automaticShutdown;
      ExecutorPair<ExecutorService> rateLimitElasticPair = resolveExecutor(this.threadingConfig.getRateLimitElasticProvider(), shardId);
      ExecutorService rateLimitElastic = rateLimitElasticPair.executor;
      boolean shutdownRateLimitElastic = rateLimitElasticPair.automaticShutdown;
      ExecutorPair<ScheduledExecutorService> gatewayPair = resolveExecutor(this.threadingConfig.getGatewayPoolProvider(), shardId);
      ScheduledExecutorService gatewayPool = (ScheduledExecutorService)gatewayPair.executor;
      boolean shutdownGatewayPool = gatewayPair.automaticShutdown;
      ExecutorPair<ExecutorService> callbackPair = resolveExecutor(this.threadingConfig.getCallbackPoolProvider(), shardId);
      ExecutorService callbackPool = callbackPair.executor;
      boolean shutdownCallbackPool = callbackPair.automaticShutdown;
      ExecutorPair<ExecutorService> eventPair = resolveExecutor(this.threadingConfig.getEventPoolProvider(), shardId);
      ExecutorService eventPool = eventPair.executor;
      boolean shutdownEventPool = eventPair.automaticShutdown;
      ExecutorPair<ScheduledExecutorService> audioPair = resolveExecutor(this.threadingConfig.getAudioPoolProvider(), shardId);
      ScheduledExecutorService audioPool = (ScheduledExecutorService)audioPair.executor;
      boolean shutdownAudioPool = audioPair.automaticShutdown;
      AuthorizationConfig authConfig = new AuthorizationConfig(this.token);
      SessionConfig sessionConfig = this.sessionConfig.toSessionConfig(httpClient);
      ThreadingConfig threadingConfig = new ThreadingConfig();
      threadingConfig.setRateLimitScheduler(rateLimitScheduler, shutdownRateLimitScheduler);
      threadingConfig.setRateLimitElastic(rateLimitElastic, shutdownRateLimitElastic);
      threadingConfig.setGatewayPool(gatewayPool, shutdownGatewayPool);
      threadingConfig.setCallbackPool(callbackPool, shutdownCallbackPool);
      threadingConfig.setEventPool(eventPool, shutdownEventPool);
      threadingConfig.setAudioPool(audioPool, shutdownAudioPool);
      MetaConfig metaConfig = new MetaConfig(this.metaConfig.getMaxBufferSize(), this.metaConfig.getContextMap(shardId), this.metaConfig.getCacheFlags(), this.sessionConfig.getFlags());
      RestConfig restConfig = (RestConfig)this.restConfigProvider.apply(shardId);
      if (restConfig == null) {
         restConfig = new RestConfig();
      }

      JDAImpl jda = new JDAImpl(authConfig, sessionConfig, threadingConfig, metaConfig, restConfig, this.audioModuleConfig);
      jda.setMemberCachePolicy(this.shardingConfig.getMemberCachePolicy());
      Objects.requireNonNull(jda);
      threadingConfig.init(jda::getIdentifierString);
      jda.initRequester();
      if ((this.shardingConfig.getIntents() & GatewayIntent.GUILD_MEMBERS.getRawValue()) == 0) {
         jda.setChunkingFilter(ChunkingFilter.NONE);
      } else {
         jda.setChunkingFilter(this.chunkingFilter);
      }

      jda.setShardManager(this);
      if (this.eventConfig.getEventManagerProvider() != null) {
         jda.setEventManager((IEventManager)this.eventConfig.getEventManagerProvider().apply(shardId));
      }

      jda.addEventListener(this.eventConfig.getListeners().toArray());
      this.eventConfig.getListenerProviders().forEach((provider) -> jda.addEventListener(provider.apply(shardId)));
      PresenceImpl presence = (PresenceImpl)jda.getPresence();
      if (this.presenceConfig.getActivityProvider() != null) {
         presence.setCacheActivity((Activity)this.presenceConfig.getActivityProvider().apply(shardId));
      }

      if (this.presenceConfig.getIdleProvider() != null) {
         presence.setCacheIdle((Boolean)this.presenceConfig.getIdleProvider().apply(shardId));
      }

      if (this.presenceConfig.getStatusProvider() != null) {
         presence.setCacheStatus((OnlineStatus)this.presenceConfig.getStatusProvider().apply(shardId));
      }

      if (this.gatewayURL == null) {
         SessionController.ShardedGateway gateway = jda.getShardedGateway();
         this.sessionConfig.getSessionController().setConcurrency(gateway.getConcurrency());
         this.gatewayURL = gateway.getUrl();
         if (this.gatewayURL == null) {
            throw new IllegalStateException("Acquired null gateway url from SessionController");
         }

         LOG.info("Login Successful!");
      }

      JDA.ShardInfo shardInfo = new JDA.ShardInfo(shardId, this.getShardsTotal());
      SelfUser selfUser = (SelfUser)this.getShardCache().applyStream((s) -> (SelfUser)s.map(JDA::getSelfUser).findFirst().orElse((Object)null));
      Object var31;
      if (selfUser == null) {
         var31 = this.retrieveSelfUser(jda);
      } else {
         var31 = SelfUserImpl.copyOf((SelfUserImpl)selfUser, jda);
      }

      jda.setSelfUser((SelfUser)var31);
      jda.setStatus(JDA.Status.INITIALIZED);
      jda.login(this.gatewayURL, shardInfo, this.metaConfig.getCompression(), false, this.shardingConfig.getIntents(), this.metaConfig.getEncoding());
      return jda;
   }

   private SelfUser retrieveSelfUser(JDAImpl jda) {
      Route.CompiledRoute route = Route.Self.GET_SELF.compile();
      return (SelfUser)(new RestActionImpl(jda, route, (response, request) -> jda.getEntityBuilder().createSelfUser(response.getObject()))).complete();
   }

   public void setActivityProvider(@Nullable IntFunction<? extends Activity> activityProvider) {
      ShardManager.super.setActivityProvider(activityProvider);
      this.presenceConfig.setActivityProvider(activityProvider);
   }

   public void setIdleProvider(@Nonnull IntFunction<Boolean> idleProvider) {
      ShardManager.super.setIdleProvider(idleProvider);
      this.presenceConfig.setIdleProvider(idleProvider);
   }

   public void setPresenceProvider(@Nullable IntFunction<OnlineStatus> statusProvider, @Nullable IntFunction<? extends Activity> activityProvider) {
      ShardManager.super.setPresenceProvider(statusProvider, activityProvider);
      this.presenceConfig.setStatusProvider(statusProvider);
      this.presenceConfig.setActivityProvider(activityProvider);
   }

   public void setStatusProvider(@Nullable IntFunction<OnlineStatus> statusProvider) {
      ShardManager.super.setStatusProvider(statusProvider);
      this.presenceConfig.setStatusProvider(statusProvider);
   }

   private synchronized void retrieveShardTotal(OkHttpClient httpClient) {
      if (this.getShardsTotal() == -1) {
         LOG.debug("Fetching shard total using temporary rate-limiter");
         CompletableFuture<Integer> future = new CompletableFuture();
         ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor((task) -> {
            Thread thread = new Thread(task, "DefaultShardManager retrieveShardTotal");
            thread.setDaemon(true);
            return thread;
         });

         try {
            RestRateLimiter.RateLimitConfig rateLimitConfig = new RestRateLimiter.RateLimitConfig(pool, RestRateLimiter.GlobalRateLimit.create(), true);
            SequentialRestRateLimiter rateLimiter = new SequentialRestRateLimiter(rateLimitConfig);
            rateLimiter.enqueue(new ShardTotalTask(future, httpClient));
            int shardTotal = (Integer)future.join();
            this.shardingConfig.setShardsTotal(shardTotal);
            this.shards = new ShardCacheViewImpl(shardTotal);
            synchronized(this.queue) {
               for(int i = 0; i < shardTotal; ++i) {
                  this.queue.add(i);
               }
            }
         } catch (CompletionException ex) {
            if (ex.getCause() instanceof RuntimeException) {
               throw (RuntimeException)ex.getCause();
            }

            if (ex.getCause() instanceof Error) {
               throw (Error)ex.getCause();
            }

            throw ex;
         } finally {
            future.cancel(false);
            pool.shutdownNow();
         }

      }
   }

   protected ScheduledExecutorService createExecutor(ThreadFactory threadFactory) {
      ThreadFactory factory = threadFactory == null ? DEFAULT_THREAD_FACTORY : threadFactory;
      return Executors.newSingleThreadScheduledExecutor(factory);
   }

   protected static <E extends ExecutorService> ExecutorPair<E> resolveExecutor(ThreadPoolProvider<? extends E> provider, int shardId) {
      E executor = null;
      boolean automaticShutdown = true;
      if (provider != null) {
         executor = provider.provide(shardId);
         automaticShutdown = provider.shouldShutdownAutomatically(shardId);
      }

      return new ExecutorPair<E>(executor, automaticShutdown);
   }

   protected static class ExecutorPair<E extends ExecutorService> {
      protected final E executor;
      protected final boolean automaticShutdown;

      protected ExecutorPair(E executor, boolean automaticShutdown) {
         this.executor = executor;
         this.automaticShutdown = automaticShutdown;
      }
   }

   protected class ShardTotalTask implements RestRateLimiter.Work {
      private final CompletableFuture<Integer> future;
      private final OkHttpClient httpClient;
      private int failedAttempts = 0;

      protected ShardTotalTask(CompletableFuture<Integer> future, OkHttpClient httpClient) {
         this.future = future;
         this.httpClient = httpClient;
      }

      @Nonnull
      public Route.CompiledRoute getRoute() {
         return Route.Misc.GATEWAY_BOT.compile();
      }

      @Nonnull
      public JDA getJDA() {
         throw new UnsupportedOperationException();
      }

      @Nullable
      public Response execute() {
         try {
            RestConfig config = (RestConfig)DefaultShardManager.this.restConfigProvider.apply(0);
            HttpUrl baseUrl = HttpUrl.get(config.getBaseUrl());
            HttpUrl url = this.getRoute().toHttpUrl(baseUrl);
            DefaultShardManager.LOG.debug((String)"Requesting shard total with url {}", (Object)url);
            Request.Builder builder = (new Request.Builder()).get().url(url).header("authorization", "Bot " + DefaultShardManager.this.token).header("accept-encoding", "gzip").header("user-agent", config.getUserAgent());
            Consumer<? super Request.Builder> customBuilder = config.getCustomBuilder();
            if (customBuilder != null) {
               customBuilder.accept(builder);
            }

            Call call = this.httpClient.newCall(builder.build());
            Response response = call.execute();

            Response var19;
            try {
               DefaultShardManager.LOG.debug((String)"Received response with code {}", (Object)response.code());
               InputStream body = IOUtil.getBody(response);
               if (response.isSuccessful()) {
                  DataObject json = DataObject.fromJson(body);
                  int shardTotal = json.getInt("shards");
                  this.future.complete(shardTotal);
               } else if (response.code() == 401) {
                  this.future.completeExceptionally(new InvalidTokenException());
               } else if ((response.code() == 429 || response.code() >= 500) && ++this.failedAttempts <= 4) {
                  if (response.code() >= 500) {
                     int backoff = 1 << this.failedAttempts;
                     DefaultShardManager.LOG.warn((String)"Failed to retrieve recommended shard total. Code: {} ... retrying in {}s", (Object)response.code(), (Object)backoff);
                     response = response.newBuilder().headers(response.headers().newBuilder().set("X-RateLimit-Reset-After", String.valueOf(backoff)).set("X-RateLimit-Remaining", String.valueOf(0)).set("X-RateLimit-Limit", String.valueOf(1)).set("X-RateLimit-Scope", "custom").build()).build();
                  }
               } else {
                  this.future.completeExceptionally(new IllegalStateException("Failed to fetch recommended shard total! Code: " + response.code() + "\n" + new String(IOUtil.readFully(body), StandardCharsets.UTF_8)));
               }

               var19 = response;
            } finally {
               response.close();
            }

            return var19;
         } catch (IOException e) {
            this.future.completeExceptionally(e);
            throw new UncheckedIOException(e);
         } catch (Throwable e) {
            this.future.completeExceptionally(e);
            throw e;
         }
      }

      public boolean isSkipped() {
         return this.isCancelled();
      }

      public boolean isDone() {
         return this.future.isDone();
      }

      public boolean isPriority() {
         return true;
      }

      public boolean isCancelled() {
         return this.future.isCancelled();
      }

      public void cancel() {
         this.future.cancel(false);
      }
   }
}
