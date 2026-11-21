package net.dv8tion.jda.api.sharding;

import java.util.concurrent.ExecutorService;
import java.util.function.IntFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.internal.utils.Checks;

public interface ThreadPoolProvider<T extends ExecutorService> {
   @Nullable
   T provide(int var1);

   default boolean shouldShutdownAutomatically(int shardId) {
      return false;
   }

   @Nonnull
   static <T extends ExecutorService> LazySharedProvider<T> lazy(@Nonnull IntFunction<T> init) {
      Checks.notNull(init, "Initializer");
      return new LazySharedProvider<T>(init);
   }

   public static final class LazySharedProvider<T extends ExecutorService> implements ThreadPoolProvider<T> {
      private final IntFunction<T> initializer;
      private T pool;

      LazySharedProvider(@Nonnull IntFunction<T> initializer) {
         this.initializer = initializer;
      }

      public synchronized void init(int shardTotal) {
         if (this.pool == null) {
            this.pool = (T)(this.initializer.apply(shardTotal));
         }

      }

      public synchronized void shutdown() {
         if (this.pool != null) {
            this.pool.shutdown();
            this.pool = null;
         }

      }

      @Nullable
      public synchronized T provide(int shardId) {
         return this.pool;
      }
   }
}
