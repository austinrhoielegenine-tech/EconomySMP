package net.dv8tion.jda.internal.utils.cache;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.utils.ClosableIterator;
import net.dv8tion.jda.api.utils.cache.CacheView;
import net.dv8tion.jda.api.utils.cache.ChannelCacheView;
import net.dv8tion.jda.internal.utils.ChainedClosableIterator;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;

public class UnifiedChannelCacheView<C extends Channel> implements ChannelCacheView<C> {
   private final Supplier<Stream<ChannelCacheView<C>>> supplier;

   public UnifiedChannelCacheView(Supplier<Stream<ChannelCacheView<C>>> supplier) {
      this.supplier = supplier;
   }

   public void forEach(Consumer<? super C> action) {
      Objects.requireNonNull(action, "Consumer");
      ClosableIterator<C> iterator = this.lockedIterator();

      try {
         while(iterator.hasNext()) {
            action.accept(iterator.next());
         }
      } catch (Throwable var6) {
         if (iterator != null) {
            try {
               iterator.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (iterator != null) {
         iterator.close();
      }

   }

   @Nonnull
   public List<C> asList() {
      return (List)this.stream().collect(Helpers.toUnmodifiableList());
   }

   @Nonnull
   public Set<C> asSet() {
      return (Set)this.stream().collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
   }

   @Nonnull
   public ClosableIterator<C> lockedIterator() {
      return new ChainedClosableIterator<C>(((Stream)this.supplier.get()).iterator());
   }

   public long size() {
      return ((Stream)this.supplier.get()).mapToLong(CacheView::size).sum();
   }

   public boolean isEmpty() {
      return ((Stream)this.supplier.get()).allMatch(CacheView::isEmpty);
   }

   @Nonnull
   public List<C> getElementsByName(@Nonnull String name, boolean ignoreCase) {
      return (List)((Stream)this.supplier.get()).flatMap((view) -> view.getElementsByName(name, ignoreCase).stream()).collect(Helpers.toUnmodifiableList());
   }

   @Nonnull
   public Stream<C> stream() {
      return ((Stream)this.supplier.get()).flatMap(CacheView::stream);
   }

   @Nonnull
   public Stream<C> parallelStream() {
      return ((Stream)((Stream)this.supplier.get()).parallel()).flatMap(CacheView::parallelStream);
   }

   @Nonnull
   public <T extends C> ChannelCacheView<T> ofType(@Nonnull Class<T> type) {
      Checks.notNull(type, "Type");
      return new UnifiedChannelCacheView<T>(() -> ((Stream)this.supplier.get()).map((view) -> view.ofType(type)));
   }

   @Nullable
   public C getElementById(@Nonnull ChannelType type, long id) {
      return (C)(((Stream)this.supplier.get()).map((view) -> view.getElementById(type, id)).filter(Objects::nonNull).findFirst().orElse((Object)null));
   }

   @Nullable
   public C getElementById(long id) {
      return (C)(((Stream)this.supplier.get()).map((view) -> (Channel)view.getElementById(id)).filter(Objects::nonNull).findFirst().orElse((Object)null));
   }

   @Nonnull
   public Iterator<C> iterator() {
      return this.stream().iterator();
   }
}
