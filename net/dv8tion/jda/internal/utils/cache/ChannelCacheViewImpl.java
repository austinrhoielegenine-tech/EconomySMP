package net.dv8tion.jda.internal.utils.cache;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.utils.ClosableIterator;
import net.dv8tion.jda.api.utils.LockIterator;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.cache.ChannelCacheView;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import net.dv8tion.jda.internal.utils.UnlockHook;

public class ChannelCacheViewImpl<T extends Channel> extends ReadWriteLockCache<T> implements ChannelCacheView<T> {
   protected final EnumMap<ChannelType, TLongObjectMap<T>> caches = new EnumMap(ChannelType.class);

   public ChannelCacheViewImpl(Class<T> type) {
      for(ChannelType channelType : ChannelType.values()) {
         channelType = this.normalizeKey(channelType);
         Class<? extends Channel> clazz = channelType.getInterface();
         if (channelType != ChannelType.UNKNOWN && type.isAssignableFrom(clazz)) {
            this.caches.put(channelType, new TLongObjectHashMap());
         }
      }

   }

   protected ChannelType normalizeKey(ChannelType type) {
      return type.isThread() ? ChannelType.GUILD_PUBLIC_THREAD : type;
   }

   @Nullable
   protected <C extends T> TLongObjectMap<C> getMap(@Nonnull ChannelType type) {
      return (TLongObjectMap)this.caches.get(this.normalizeKey(type));
   }

   @Nullable
   public <C extends T> C put(C element) {
      UnlockHook hook = this.writeLock();

      Channel var3;
      try {
         var3 = (Channel)this.getMap(element.getType()).put(element.getIdLong(), element);
      } catch (Throwable var6) {
         if (hook != null) {
            try {
               hook.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (hook != null) {
         hook.close();
      }

      return (C)var3;
   }

   @Nullable
   public <C extends T> C remove(ChannelType type, long id) {
      UnlockHook hook = this.writeLock();

      Channel var6;
      try {
         T removed = (T)(this.getMap(type).remove(id));
         var6 = removed;
      } catch (Throwable var8) {
         if (hook != null) {
            try {
               hook.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (hook != null) {
         hook.close();
      }

      return (C)var6;
   }

   public <C extends T> C remove(C channel) {
      return (C)this.remove(channel.getType(), channel.getIdLong());
   }

   public <C extends T> void removeIf(Class<C> typeFilter, Predicate<? super C> predicate) {
      UnlockHook hook = this.writeLock();

      try {
         this.ofType(typeFilter).removeIf(predicate);
      } catch (Throwable var7) {
         if (hook != null) {
            try {
               hook.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (hook != null) {
         hook.close();
      }

   }

   public void clear() {
      UnlockHook hook = this.writeLock();

      try {
         this.caches.values().forEach(TLongObjectMap::clear);
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

   }

   @Nonnull
   public <C extends T> ChannelCacheViewImpl<T>.FilteredCacheView<C> ofType(@Nonnull Class<C> type) {
      return new FilteredCacheView<C>(type);
   }

   public void forEach(Consumer<? super T> action) {
      UnlockHook hook = this.readLock();

      try {
         for(TLongObjectMap<T> cache : this.caches.values()) {
            cache.valueCollection().forEach(action);
         }
      } catch (Throwable var6) {
         if (hook != null) {
            try {
               hook.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (hook != null) {
         hook.close();
      }

   }

   @Nonnull
   public List<T> asList() {
      List<T> list = this.getCachedList();
      if (list == null) {
         List<T> newList = (List)this.applyStream((stream) -> (List)stream.collect(Collectors.toList()));
         list = this.cache(newList);
      }

      return list;
   }

   @Nonnull
   public Set<T> asSet() {
      Set<T> set = this.getCachedSet();
      if (set == null) {
         Set<T> newSet = (Set)this.applyStream((stream) -> (Set)stream.collect(Collectors.toSet()));
         set = this.cache(newSet);
      }

      return set;
   }

   @Nonnull
   public ClosableIterator<T> lockedIterator() {
      ReentrantReadWriteLock.ReadLock readLock = this.lock.readLock();
      MiscUtil.tryLock(readLock);

      try {
         Iterator<? extends T> directIterator = this.caches.values().stream().flatMap((map) -> map.valueCollection().stream()).iterator();
         return new LockIterator<T>(directIterator, readLock);
      } catch (Throwable t) {
         readLock.unlock();
         throw t;
      }
   }

   public long size() {
      UnlockHook hook = this.readLock();

      long var2;
      try {
         var2 = this.caches.values().stream().mapToLong(TLongObjectMap::size).sum();
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

   public boolean isEmpty() {
      UnlockHook hook = this.readLock();

      boolean var2;
      try {
         var2 = this.caches.values().stream().allMatch(TLongObjectMap::isEmpty);
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
   public List<T> getElementsByName(@Nonnull String name, boolean ignoreCase) {
      Checks.notEmpty(name, "Name");
      return (List)this.applyStream((stream) -> (List)stream.filter((channel) -> Helpers.equals(channel.getName(), name, ignoreCase)).collect(Helpers.toUnmodifiableList()));
   }

   @Nonnull
   public Stream<T> stream() {
      return this.asList().stream();
   }

   @Nonnull
   public Stream<T> parallelStream() {
      return this.asList().parallelStream();
   }

   @Nullable
   public T getElementById(long id) {
      UnlockHook hook = this.readLock();

      Channel var7;
      label49: {
         Iterator var10;
         try {
            for(TLongObjectMap<? extends T> cache : this.caches.values()) {
               T element = cache.get(id);
               if (element != null) {
                  var7 = element;
                  break label49;
               }
            }

            var10 = null;
         } catch (Throwable var9) {
            if (hook != null) {
               try {
                  hook.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (hook != null) {
            hook.close();
         }

         return (T)var10;
      }

      if (hook != null) {
         hook.close();
      }

      return (T)var7;
   }

   public T getElementById(@Nonnull ChannelType type, long id) {
      Checks.notNull(type, "ChannelType");
      UnlockHook hook = this.readLock();

      Channel var6;
      try {
         TLongObjectMap<T> map = this.<T>getMap(type);
         var6 = map == null ? null : (Channel)map.get(id);
      } catch (Throwable var8) {
         if (hook != null) {
            try {
               hook.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (hook != null) {
         hook.close();
      }

      return (T)var6;
   }

   @Nonnull
   public Iterator<T> iterator() {
      return this.stream().iterator();
   }

   public class FilteredCacheView<C extends T> implements ChannelCacheView<C> {
      protected final Class<C> type;
      protected final List<TLongObjectMap<C>> filteredMaps;

      protected FilteredCacheView(Class<C> type) {
         Checks.notNull(type, "Type");
         this.checkChannelInterface(type);
         this.type = type;
         this.filteredMaps = (List)ChannelCacheViewImpl.this.caches.entrySet().stream().filter((entry) -> entry.getKey() != null && type.isAssignableFrom(((ChannelType)entry.getKey()).getInterface())).map((entry) -> (TLongObjectMap)entry.getValue()).collect(Collectors.toList());
      }

      private void checkChannelInterface(Class<C> type) {
         boolean isValidInterfaceType = false;

         for(ChannelType channelType : ChannelType.values()) {
            isValidInterfaceType |= type.isAssignableFrom(channelType.getInterface());
         }

         Checks.check(isValidInterfaceType, "Type %s is not a valid channel interface", (Object)type.getSimpleName());
      }

      protected void removeIf(Predicate<? super C> filter) {
         this.filteredMaps.forEach((map) -> map.valueCollection().removeIf(filter));
      }

      @Nonnull
      public List<C> asList() {
         return (List)this.applyStream((stream) -> (List)stream.collect(Helpers.toUnmodifiableList()));
      }

      @Nonnull
      public Set<C> asSet() {
         return (Set)this.applyStream((stream) -> (Set)stream.collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet)));
      }

      @Nonnull
      public ClosableIterator<C> lockedIterator() {
         ReentrantReadWriteLock.ReadLock readLock = ChannelCacheViewImpl.this.lock.readLock();
         MiscUtil.tryLock(readLock);

         try {
            Iterator<? extends C> directIterator = this.filteredMaps.stream().flatMap((map) -> map.valueCollection().stream()).iterator();
            return new LockIterator<C>(directIterator, readLock);
         } catch (Throwable t) {
            readLock.unlock();
            throw t;
         }
      }

      public long size() {
         UnlockHook hook = ChannelCacheViewImpl.this.readLock();

         long var2;
         try {
            var2 = this.filteredMaps.stream().mapToLong(TLongObjectMap::size).sum();
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

      public boolean isEmpty() {
         UnlockHook hook = ChannelCacheViewImpl.this.readLock();

         boolean var2;
         try {
            var2 = this.filteredMaps.stream().allMatch(TLongObjectMap::isEmpty);
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
      public List<C> getElementsByName(@Nonnull String name, boolean ignoreCase) {
         Checks.notEmpty(name, "Name");
         return (List)this.applyStream((stream) -> (List)stream.filter((channel) -> Helpers.equals(channel.getName(), name, ignoreCase)).collect(Helpers.toUnmodifiableList()));
      }

      @Nonnull
      public Stream<C> stream() {
         return this.asList().stream();
      }

      @Nonnull
      public Stream<C> parallelStream() {
         return this.asList().parallelStream();
      }

      @Nonnull
      public <C1 extends C> ChannelCacheView<C1> ofType(@Nonnull Class<C1> type) {
         return ChannelCacheViewImpl.this.ofType(type);
      }

      @Nullable
      public C getElementById(@Nonnull ChannelType type, long id) {
         T channel = (T)ChannelCacheViewImpl.this.getElementById(type, id);
         return (C)(this.type.isInstance(channel) ? (Channel)this.type.cast(channel) : null);
      }

      @Nullable
      public C getElementById(long id) {
         UnlockHook hook = ChannelCacheViewImpl.this.readLock();

         Channel var4;
         try {
            var4 = (Channel)this.filteredMaps.stream().map((it) -> (Channel)it.get(id)).filter(Objects::nonNull).findFirst().orElse((Object)null);
         } catch (Throwable var7) {
            if (hook != null) {
               try {
                  hook.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (hook != null) {
            hook.close();
         }

         return (C)var4;
      }

      @Nonnull
      public Iterator<C> iterator() {
         return this.asList().iterator();
      }
   }
}
