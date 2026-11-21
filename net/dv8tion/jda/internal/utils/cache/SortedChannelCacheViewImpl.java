package net.dv8tion.jda.internal.utils.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Spliterator;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.utils.cache.SortedChannelCacheView;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import net.dv8tion.jda.internal.utils.UnlockHook;

public class SortedChannelCacheViewImpl<T extends Channel & Comparable<? super T>> extends ChannelCacheViewImpl<T> implements SortedChannelCacheView<T> {
   public SortedChannelCacheViewImpl(Class<T> type) {
      super(type);
   }

   @Nonnull
   public <C extends T> SortedChannelCacheViewImpl<T>.SortedFilteredCacheView<C> ofType(@Nonnull Class<C> type) {
      return new SortedFilteredCacheView<C>(type);
   }

   @Nonnull
   public List<T> asList() {
      List<T> list = this.getCachedList();
      if (list == null) {
         list = this.cache(new ArrayList(this.asSet()));
      }

      return list;
   }

   @Nonnull
   public NavigableSet<T> asSet() {
      NavigableSet<T> set = (NavigableSet)this.getCachedSet();
      if (set == null) {
         set = this.cache((NavigableSet)this.applyStream((stream) -> (TreeSet)stream.collect(Collectors.toCollection(TreeSet::new))));
      }

      return set;
   }

   public void forEachUnordered(@Nonnull Consumer<? super T> action) {
      super.forEach(action);
   }

   public void forEach(@Nonnull Consumer<? super T> action) {
      this.asSet().forEach(action);
   }

   @Nonnull
   public List<T> getElementsByName(@Nonnull String name) {
      List<T> elements = super.getElementsByName(name);
      elements.sort(Comparator.naturalOrder());
      return elements;
   }

   @Nonnull
   public Stream<T> streamUnordered() {
      UnlockHook hook = this.readLock();

      Stream var2;
      try {
         var2 = ((List)this.caches.values().stream().flatMap((cache) -> cache.valueCollection().stream()).collect(Collectors.toList())).stream();
      } catch (Throwable var5) {
         if (hook != null) {
            try {
               hook.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (hook != null) {
         hook.close();
      }

      return var2;
   }

   @Nonnull
   public Stream<T> parallelStreamUnordered() {
      return (Stream)this.streamUnordered().parallel();
   }

   public Spliterator<T> spliterator() {
      return this.asSet().spliterator();
   }

   @Nonnull
   public Iterator<T> iterator() {
      return this.asSet().iterator();
   }

   public class SortedFilteredCacheView<C extends T> extends ChannelCacheViewImpl<T>.FilteredCacheView<C> implements SortedChannelCacheView<C> {
      protected SortedFilteredCacheView(Class<C> type) {
         super(type);
      }

      @Nonnull
      public List<C> asList() {
         return (List)this.applyStream((stream) -> (List)stream.sorted().collect(Helpers.toUnmodifiableList()));
      }

      @Nonnull
      public NavigableSet<C> asSet() {
         return (NavigableSet)this.applyStream((stream) -> (NavigableSet)stream.collect(Collectors.collectingAndThen(Collectors.toCollection(TreeSet::new), Collections::unmodifiableNavigableSet)));
      }

      @Nonnull
      public List<C> getElementsByName(@Nonnull String name, boolean ignoreCase) {
         Checks.notEmpty(name, "Name");
         return (List)this.applyStream((stream) -> (List)stream.filter((it) -> Helpers.equals(name, it.getName(), ignoreCase)).sorted().collect(Helpers.toUnmodifiableList()));
      }

      @Nonnull
      public Stream<C> streamUnordered() {
         List<C> elements = (List)this.applyStream((stream) -> {
            Class var10001 = this.type;
            Objects.requireNonNull(var10001);
            Class<C> rec$ = var10001;
            return (List)stream.filter((x$0) -> rec$.isInstance(x$0)).collect(Collectors.toList());
         });
         return elements.stream();
      }

      @Nonnull
      public Stream<C> parallelStreamUnordered() {
         return (Stream)this.stream().parallel();
      }

      @Nonnull
      public <C1 extends C> SortedChannelCacheView<C1> ofType(@Nonnull Class<C1> type) {
         return SortedChannelCacheViewImpl.this.ofType(type);
      }

      public void forEachUnordered(@Nonnull Consumer<? super C> action) {
         super.forEach(action);
      }

      public void forEach(Consumer<? super C> action) {
         this.stream().forEach(action);
      }
   }
}
