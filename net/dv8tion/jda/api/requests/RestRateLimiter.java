package net.dv8tion.jda.api.requests;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.Blocking;

public interface RestRateLimiter {
   String RESET_AFTER_HEADER = "X-RateLimit-Reset-After";
   String RESET_HEADER = "X-RateLimit-Reset";
   String LIMIT_HEADER = "X-RateLimit-Limit";
   String REMAINING_HEADER = "X-RateLimit-Remaining";
   String GLOBAL_HEADER = "X-RateLimit-Global";
   String HASH_HEADER = "X-RateLimit-Bucket";
   String RETRY_AFTER_HEADER = "Retry-After";
   String SCOPE_HEADER = "X-RateLimit-Scope";

   void enqueue(@Nonnull Work var1);

   void stop(boolean var1, @Nonnull Runnable var2);

   boolean isStopped();

   int cancelRequests();

   public interface GlobalRateLimit {
      long getClassic();

      void setClassic(long var1);

      long getCloudflare();

      void setCloudflare(long var1);

      @Nonnull
      static GlobalRateLimit create() {
         return new GlobalRateLimit() {
            private final AtomicLong classic = new AtomicLong(-1L);
            private final AtomicLong cloudflare = new AtomicLong(-1L);

            public long getClassic() {
               return this.classic.get();
            }

            public void setClassic(long timestamp) {
               this.classic.set(timestamp);
            }

            public long getCloudflare() {
               return this.cloudflare.get();
            }

            public void setCloudflare(long timestamp) {
               this.cloudflare.set(timestamp);
            }
         };
      }
   }

   public static class RateLimitConfig {
      private final ScheduledExecutorService scheduler;
      private final ExecutorService elastic;
      private final GlobalRateLimit globalRateLimit;
      private final boolean isRelative;

      public RateLimitConfig(@Nonnull ScheduledExecutorService scheduler, @Nonnull GlobalRateLimit globalRateLimit, boolean isRelative) {
         this(scheduler, scheduler, globalRateLimit, isRelative);
      }

      public RateLimitConfig(@Nonnull ScheduledExecutorService scheduler, @Nonnull ExecutorService elastic, @Nonnull GlobalRateLimit globalRateLimit, boolean isRelative) {
         this.scheduler = scheduler;
         this.elastic = elastic;
         this.globalRateLimit = globalRateLimit;
         this.isRelative = isRelative;
      }

      @Nonnull
      public ScheduledExecutorService getScheduler() {
         return this.scheduler;
      }

      @Nonnull
      public ExecutorService getElastic() {
         return this.elastic;
      }

      @Nonnull
      public GlobalRateLimit getGlobalRateLimit() {
         return this.globalRateLimit;
      }

      public boolean isRelative() {
         return this.isRelative;
      }
   }

   public interface Work {
      @Nonnull
      Route.CompiledRoute getRoute();

      @Nonnull
      JDA getJDA();

      @Nullable
      @Blocking
      okhttp3.Response execute();

      boolean isSkipped();

      boolean isDone();

      boolean isPriority();

      boolean isCancelled();

      void cancel();
   }
}
