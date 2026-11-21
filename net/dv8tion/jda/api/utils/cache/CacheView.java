package net.dv8tion.jda.api.utils.cache;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.annotations.UnknownNullability;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.utils.ClosableIterator;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.cache.AbstractCacheView;
import net.dv8tion.jda.internal.utils.cache.ShardCacheViewImpl;
import net.dv8tion.jda.internal.utils.cache.UnifiedCacheViewImpl;
import org.jetbrains.annotations.Unmodifiable;

public interface CacheView<T> extends Iterable<T> {
   @Nonnull
   @Unmodifiable List<T> asList();

   @Nonnull
   @Unmodifiable Set<T> asSet();

   @Nonnull
   ClosableIterator<T> lockedIterator();

   default void forEachUnordered(@Nonnull Consumer<? super T> action) {
      this.forEach(action);
   }

   @UnknownNullability
   default <R> R applyStream(@Nonnull Function<? super Stream<T>, ? extends R> action) {
      Checks.notNull(action, "Action");
      ClosableIterator<T> it = this.lockedIterator();

      Object var5;
      try {
         Spliterator<T> spliterator = Spliterators.spliterator(it, this.size(), 1280);
         Stream<T> stream = StreamSupport.stream(spliterator, false);
         var5 = action.apply(stream);
      } catch (Throwable var7) {
         if (it != null) {
            try {
               it.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (it != null) {
         it.close();
      }

      return (R)var5;
   }

   default void acceptStream(@Nonnull Consumer<? super Stream<T>> action) {
      Checks.notNull(action, "Action");
      ClosableIterator<T> it = this.lockedIterator();

      try {
         Spliterator<T> spliterator = Spliterators.spliterator(it, this.size(), 1280);
         Stream<T> stream = StreamSupport.stream(spliterator, false);
         action.accept(stream);
      } catch (Throwable var6) {
         if (it != null) {
            try {
               it.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (it != null) {
         it.close();
      }

   }

   long size();

   boolean isEmpty();

   @Nonnull
   @Unmodifiable List<T> getElementsByName(@Nonnull String var1, boolean var2);

   @Nonnull
   default @Unmodifiable List<T> getElementsByName(@Nonnull String name) {
      return this.getElementsByName(name, false);
   }

   @Nonnull
   Stream<T> stream();

   @Nonnull
   Stream<T> parallelStream();

   @Nonnull
   default <R, A> R collect(@Nonnull Collector<? super T, A, R> collector) {
      return (R)this.stream().collect(collector);
   }

   @Nonnull
   static <E> CacheView<E> all(@Nonnull Collection<? extends CacheView<E>> cacheViews) {
      Checks.noneNull(cacheViews, "Collection");
      Objects.requireNonNull(cacheViews);
      return new UnifiedCacheViewImpl(cacheViews::stream);
   }

   @Nonnull
   static <E> CacheView<E> all(@Nonnull Supplier<? extends Stream<? extends CacheView<E>>> generator) {
      Checks.notNull(generator, "Generator");
      return new UnifiedCacheViewImpl(generator);
   }

   @Nonnull
   static ShardCacheView allShards(@Nonnull Collection<ShardCacheView> cacheViews) {
      Checks.noneNull(cacheViews, "Collection");
      Objects.requireNonNull(cacheViews);
      return new ShardCacheViewImpl.UnifiedShardCacheViewImpl(cacheViews::stream);
   }

   @Nonnull
   static ShardCacheView allShards(@Nonnull Supplier<? extends Stream<? extends ShardCacheView>> generator) {
      Checks.notNull(generator, "Generator");
      return new ShardCacheViewImpl.UnifiedShardCacheViewImpl(generator);
   }

   @Nonnull
   static <E extends ISnowflake> SnowflakeCacheView<E> allSnowflakes(@Nonnull Collection<? extends SnowflakeCacheView<E>> cacheViews) {
      Checks.noneNull(cacheViews, "Collection");
      Objects.requireNonNull(cacheViews);
      return new UnifiedCacheViewImpl.UnifiedSnowflakeCacheView<E>(cacheViews::stream);
   }

   @Nonnull
   static <E extends ISnowflake> SnowflakeCacheView<E> allSnowflakes(@Nonnull Supplier<? extends Stream<? extends SnowflakeCacheView<E>>> generator) {
      Checks.notNull(generator, "Generator");
      return new UnifiedCacheViewImpl.UnifiedSnowflakeCacheView<E>(generator);
   }

   @Nonnull
   static UnifiedMemberCacheView allMembers(@Nonnull Collection<? extends MemberCacheView> cacheViews) {
      Checks.noneNull(cacheViews, "Collection");
      Objects.requireNonNull(cacheViews);
      return new UnifiedCacheViewImpl.UnifiedMemberCacheViewImpl(cacheViews::stream);
   }

   @Nonnull
   static UnifiedMemberCacheView allMembers(@Nonnull Supplier<? extends Stream<? extends MemberCacheView>> generator) {
      Checks.notNull(generator, "Generator");
      return new UnifiedCacheViewImpl.UnifiedMemberCacheViewImpl(generator);
   }

   public static class SimpleCacheView<T> extends AbstractCacheView<T> {
      public SimpleCacheView(@Nonnull Class<T> type, @Nullable Function<T, String> nameMapper) {
         super(type, nameMapper);
      }
   }
}
