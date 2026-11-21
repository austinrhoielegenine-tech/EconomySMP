package net.dv8tion.jda.api.utils;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.RestRateLimiter;

public interface SessionController {
   int IDENTIFY_DELAY = 5;

   default void setConcurrency(int level) {
   }

   void appendSession(@Nonnull SessionConnectNode var1);

   void removeSession(@Nonnull SessionConnectNode var1);

   @Nonnull
   default RestRateLimiter.GlobalRateLimit getRateLimitHandle() {
      return RestRateLimiter.GlobalRateLimit.create();
   }

   @Nonnull
   default String getGateway() {
      return "wss://gateway.discord.gg/";
   }

   @Nonnull
   ShardedGateway getShardedGateway(@Nonnull JDA var1);

   public static class ShardedGateway {
      private final String url;
      private final int shardTotal;
      private final int concurrency;

      public ShardedGateway(String url, int shardTotal) {
         this(url, shardTotal, 1);
      }

      public ShardedGateway(String url, int shardTotal, int concurrency) {
         this.url = url;
         this.shardTotal = shardTotal;
         this.concurrency = concurrency;
      }

      @Nonnull
      public String getUrl() {
         return this.url;
      }

      public int getShardTotal() {
         return this.shardTotal;
      }

      public int getConcurrency() {
         return this.concurrency;
      }
   }

   public interface SessionConnectNode {
      boolean isReconnect();

      @Nonnull
      JDA getJDA();

      @Nonnull
      JDA.ShardInfo getShardInfo();

      void run(boolean var1) throws InterruptedException;
   }
}
