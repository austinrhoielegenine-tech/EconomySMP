package net.dv8tion.jda.internal.utils.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.utils.ClosableIterator;
import net.dv8tion.jda.api.utils.cache.CacheView;
import net.dv8tion.jda.api.utils.cache.MemberCacheView;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import net.dv8tion.jda.api.utils.cache.UnifiedMemberCacheView;
import net.dv8tion.jda.internal.utils.ChainedClosableIterator;
import net.dv8tion.jda.internal.utils.Helpers;

public class UnifiedCacheViewImpl<T, E extends CacheView<T>> implements CacheView<T> {
   protected final Supplier<? extends Stream<? extends E>> generator;

   public UnifiedCacheViewImpl(Supplier<? extends Stream<? extends E>> generator) {
      this.generator = generator;
   }

   public long size() {
      return this.distinctStream().mapToLong(CacheView::size).sum();
   }

   public boolean isEmpty() {
      return this.distinctStream().allMatch(CacheView::isEmpty);
   }

   public void forEach(Consumer<? super T> action) {
      Objects.requireNonNull(action);
      ClosableIterator<T> it = this.lockedIterator();

      try {
         while(it.hasNext()) {
            action.accept(it.next());
         }
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

   @Nonnull
   public List<T> asList() {
      List<T> list = new ArrayList();
      Objects.requireNonNull(list);
      this.forEach(list::add);
      return Collections.unmodifiableList(list);
   }

   @Nonnull
   public Set<T> asSet() {
      ChainedClosableIterator<T> it = this.lockedIterator();

      Set var2;
      try {
         while(it.hasNext()) {
            it.next();
         }

         var2 = Collections.unmodifiableSet(it.getItems());
      } catch (Throwable var5) {
         if (it != null) {
            try {
               it.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (it != null) {
         it.close();
      }

      return var2;
   }

   @Nonnull
   public ChainedClosableIterator<T> lockedIterator() {
      Iterator<? extends E> gen = ((Stream)this.generator.get()).iterator();
      return new ChainedClosableIterator<T>(gen);
   }

   @Nonnull
   public List<T> getElementsByName(@Nonnull String name, boolean ignoreCase) {
      return (List)this.distinctStream().flatMap((view) -> view.getElementsByName(name, ignoreCase).stream()).distinct().collect(Helpers.toUnmodifiableList());
   }

   @Nonnull
   public Stream<T> stream() {
      return this.distinctStream().flatMap(CacheView::stream).distinct();
   }

   @Nonnull
   public Stream<T> parallelStream() {
      return this.distinctStream().flatMap(CacheView::parallelStream).distinct();
   }

   @Nonnull
   public Iterator<T> iterator() {
      return this.stream().iterator();
   }

   protected Stream<? extends E> distinctStream() {
      return ((Stream)this.generator.get()).distinct();
   }

   public static class UnifiedSnowflakeCacheView<T extends ISnowflake> extends UnifiedCacheViewImpl<T, SnowflakeCacheView<T>> implements SnowflakeCacheView<T> {
      public UnifiedSnowflakeCacheView(Supplier<? extends Stream<? extends SnowflakeCacheView<T>>> generator) {
         super(generator);
      }

      public T getElementById(long id) {
         return (T)(((Stream)this.generator.get()).map((view) -> view.getElementById(id)).filter(Objects::nonNull).findFirst().orElse((Object)null));
      }
   }

   public static class UnifiedMemberCacheViewImpl extends UnifiedCacheViewImpl<Member, MemberCacheView> implements UnifiedMemberCacheView {
      public UnifiedMemberCacheViewImpl(Supplier<? extends Stream<? extends MemberCacheView>> generator) {
         super(generator);
      }

      @Nonnull
      public List<Member> getElementsById(long id) {
         return (List)this.distinctStream().map((view) -> view.getElementById(id)).filter(Objects::nonNull).collect(Helpers.toUnmodifiableList());
      }

      @Nonnull
      public List<Member> getElementsByUsername(@Nonnull String name, boolean ignoreCase) {
         return (List)this.distinctStream().flatMap((view) -> view.getElementsByUsername(name, ignoreCase).stream()).collect(Helpers.toUnmodifiableList());
      }

      @Nonnull
      public List<Member> getElementsByNickname(@Nullable String name, boolean ignoreCase) {
         return (List)this.distinctStream().flatMap((view) -> view.getElementsByNickname(name, ignoreCase).stream()).collect(Helpers.toUnmodifiableList());
      }

      @Nonnull
      public List<Member> getElementsWithRoles(@Nonnull Role... roles) {
         return (List)this.distinctStream().flatMap((view) -> view.getElementsWithRoles(roles).stream()).collect(Helpers.toUnmodifiableList());
      }

      @Nonnull
      public List<Member> getElementsWithRoles(@Nonnull Collection<Role> roles) {
         return (List)this.distinctStream().flatMap((view) -> view.getElementsWithRoles(roles).stream()).collect(Helpers.toUnmodifiableList());
      }
   }
}
