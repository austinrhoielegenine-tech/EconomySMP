package net.dv8tion.jda.internal.utils.config.sharding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.sharding.ThreadPoolProvider;

public class ThreadingProviderConfig {
   private final ThreadPoolProvider<? extends ScheduledExecutorService> rateLimitSchedulerProvider;
   private final ThreadPoolProvider<? extends ExecutorService> rateLimitElasticProvider;
   private final ThreadPoolProvider<? extends ScheduledExecutorService> gatewayPoolProvider;
   private final ThreadPoolProvider<? extends ExecutorService> callbackPoolProvider;
   private final ThreadPoolProvider<? extends ExecutorService> eventPoolProvider;
   private final ThreadPoolProvider<? extends ScheduledExecutorService> audioPoolProvider;
   private final ThreadFactory threadFactory;

   public ThreadingProviderConfig(@Nullable ThreadPoolProvider<? extends ScheduledExecutorService> rateLimitSchedulerProvider, @Nullable ThreadPoolProvider<? extends ExecutorService> rateLimitElasticProvider, @Nullable ThreadPoolProvider<? extends ScheduledExecutorService> gatewayPoolProvider, @Nullable ThreadPoolProvider<? extends ExecutorService> callbackPoolProvider, @Nullable ThreadPoolProvider<? extends ExecutorService> eventPoolProvider, @Nullable ThreadPoolProvider<? extends ScheduledExecutorService> audioPoolProvider, @Nullable ThreadFactory threadFactory) {
      this.rateLimitSchedulerProvider = rateLimitSchedulerProvider;
      this.rateLimitElasticProvider = rateLimitElasticProvider;
      this.gatewayPoolProvider = gatewayPoolProvider;
      this.callbackPoolProvider = callbackPoolProvider;
      this.eventPoolProvider = eventPoolProvider;
      this.audioPoolProvider = audioPoolProvider;
      this.threadFactory = threadFactory;
   }

   @Nullable
   public ThreadFactory getThreadFactory() {
      return this.threadFactory;
   }

   private void init(ThreadPoolProvider<?> provider, int shardTotal) {
      if (provider instanceof ThreadPoolProvider.LazySharedProvider) {
         ((ThreadPoolProvider.LazySharedProvider)provider).init(shardTotal);
      }

   }

   private void shutdown(ThreadPoolProvider<?> provider) {
      if (provider instanceof ThreadPoolProvider.LazySharedProvider) {
         ((ThreadPoolProvider.LazySharedProvider)provider).shutdown();
      }

   }

   public void init(int shardTotal) {
      this.init(this.rateLimitSchedulerProvider, shardTotal);
      this.init(this.rateLimitElasticProvider, shardTotal);
      this.init(this.gatewayPoolProvider, shardTotal);
      this.init(this.callbackPoolProvider, shardTotal);
      this.init(this.eventPoolProvider, shardTotal);
      this.init(this.audioPoolProvider, shardTotal);
   }

   public void shutdown() {
      this.shutdown(this.rateLimitSchedulerProvider);
      this.shutdown(this.rateLimitElasticProvider);
      this.shutdown(this.gatewayPoolProvider);
      this.shutdown(this.callbackPoolProvider);
      this.shutdown(this.eventPoolProvider);
      this.shutdown(this.audioPoolProvider);
   }

   @Nullable
   public ThreadPoolProvider<? extends ScheduledExecutorService> getRateLimitSchedulerProvider() {
      return this.rateLimitSchedulerProvider;
   }

   @Nullable
   public ThreadPoolProvider<? extends ExecutorService> getRateLimitElasticProvider() {
      return this.rateLimitElasticProvider;
   }

   @Nullable
   public ThreadPoolProvider<? extends ScheduledExecutorService> getGatewayPoolProvider() {
      return this.gatewayPoolProvider;
   }

   @Nullable
   public ThreadPoolProvider<? extends ExecutorService> getCallbackPoolProvider() {
      return this.callbackPoolProvider;
   }

   @Nullable
   public ThreadPoolProvider<? extends ExecutorService> getEventPoolProvider() {
      return this.eventPoolProvider;
   }

   @Nullable
   public ThreadPoolProvider<? extends ScheduledExecutorService> getAudioPoolProvider() {
      return this.audioPoolProvider;
   }

   @Nonnull
   public static ThreadingProviderConfig getDefault() {
      return new ThreadingProviderConfig((ThreadPoolProvider)null, (ThreadPoolProvider)null, (ThreadPoolProvider)null, (ThreadPoolProvider)null, (ThreadPoolProvider)null, (ThreadPoolProvider)null, (ThreadFactory)null);
   }
}
