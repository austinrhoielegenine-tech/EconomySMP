package net.dv8tion.jda.internal.utils.cache;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.utils.ClosableIterator;
import net.dv8tion.jda.api.utils.LockIterator;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.cache.CacheView;
import net.dv8tion.jda.api.utils.cache.ShardCacheView;
import net.dv8tion.jda.internal.utils.ChainedClosableIterator;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import net.dv8tion.jda.internal.utils.UnlockHook;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;

public class ShardCacheViewImpl extends ReadWriteLockCache<JDA> implements ShardCacheView {
   protected static final JDA[] EMPTY_ARRAY = new JDA[0];
   protected final TIntObjectMap<JDA> elements;

   public ShardCacheViewImpl() {
      this.elements = new TIntObjectHashMap<JDA>();
   }

   public ShardCacheViewImpl(int initialCapacity) {
      this.elements = new TIntObjectHashMap<JDA>(initialCapacity);
   }

   public void clear() {
      UnlockHook hook = this.writeLock();

      try {
         this.elements.clear();
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

   public JDA remove(int shardId) {
      UnlockHook hook = this.writeLock();

      JDA var3;
      try {
         var3 = this.elements.remove(shardId);
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

      return var3;
   }

   public TIntObjectMap<JDA> getMap() {
      if (!this.lock.writeLock().isHeldByCurrentThread()) {
         throw new IllegalStateException("Cannot access map without holding write lock!");
      } else {
         return this.elements;
      }
   }

   public TIntSet keySet() {
      UnlockHook hook = this.readLock();

      TIntHashSet var2;
      try {
         var2 = new TIntHashSet(this.elements.keySet());
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

   public void forEach(Consumer<? super JDA> action) {
      Objects.requireNonNull(action);
      UnlockHook hook = this.readLock();

      try {
         for(JDA shard : this.elements.valueCollection()) {
            action.accept(shard);
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
   public List<JDA> asList() {
      if (this.isEmpty()) {
         return Collections.emptyList();
      } else {
         UnlockHook hook = this.readLock();

         List var6;
         label47: {
            try {
               List<JDA> list = this.getCachedList();
               if (list != null) {
                  var6 = list;
                  break label47;
               }

               var6 = this.cache(new ArrayList(this.elements.valueCollection()));
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

            return var6;
         }

         if (hook != null) {
            hook.close();
         }

         return var6;
      }
   }

   @Nonnull
   public Set<JDA> asSet() {
      if (this.isEmpty()) {
         return Collections.emptySet();
      } else {
         UnlockHook hook = this.readLock();

         Set var6;
         label47: {
            try {
               Set<JDA> set = this.getCachedSet();
               if (set != null) {
                  var6 = set;
                  break label47;
               }

               var6 = this.cache(new HashSet(this.elements.valueCollection()));
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

            return var6;
         }

         if (hook != null) {
            hook.close();
         }

         return var6;
      }
   }

   @Nonnull
   public LockIterator<JDA> lockedIterator() {
      ReentrantReadWriteLock.ReadLock readLock = this.lock.readLock();
      MiscUtil.tryLock(readLock);

      try {
         Iterator<JDA> directIterator = this.elements.valueCollection().iterator();
         return new LockIterator<JDA>(directIterator, readLock);
      } catch (Throwable t) {
         readLock.unlock();
         throw t;
      }
   }

   public long size() {
      return (long)this.elements.size();
   }

   public boolean isEmpty() {
      return this.elements.isEmpty();
   }

   @Nonnull
   public List<JDA> getElementsByName(@Nonnull String name, boolean ignoreCase) {
      Checks.notEmpty(name, "Name");
      if (this.elements.isEmpty()) {
         return Collections.emptyList();
      } else {
         UnlockHook hook = this.readLock();

         Object var10;
         try {
            List<JDA> list = new ArrayList();

            for(JDA elem : this.elements.valueCollection()) {
               String elementName = elem.getShardInfo().getShardString();
               if (elementName != null) {
                  if (ignoreCase) {
                     if (elementName.equalsIgnoreCase(name)) {
                        list.add(elem);
                     }
                  } else if (elementName.equals(name)) {
                     list.add(elem);
                  }
               }
            }

            var10 = list;
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

         return (List<JDA>)var10;
      }
   }

   public Spliterator<JDA> spliterator() {
      UnlockHook hook = this.readLock();

      Spliterator var2;
      try {
         var2 = Spliterators.spliterator(this.iterator(), this.size(), 1280);
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
   public Stream<JDA> stream() {
      return StreamSupport.stream(this.spliterator(), false);
   }

   @Nonnull
   public Stream<JDA> parallelStream() {
      return StreamSupport.stream(this.spliterator(), true);
   }

   @Nonnull
   public Iterator<JDA> iterator() {
      UnlockHook hook = this.readLock();

      ObjectArrayIterator var3;
      try {
         JDA[] arr = (JDA[])this.elements.values(EMPTY_ARRAY);
         var3 = new ObjectArrayIterator(arr);
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

      return var3;
   }

   public JDA getElementById(int id) {
      UnlockHook hook = this.readLock();

      JDA var3;
      try {
         var3 = this.elements.get(id);
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

      return var3;
   }

   public int hashCode() {
      UnlockHook hook = this.readLock();

      int var2;
      try {
         var2 = this.elements.hashCode();
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

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof ShardCacheViewImpl)) {
         return false;
      } else {
         ShardCacheViewImpl view = (ShardCacheViewImpl)obj;
         UnlockHook hook = this.readLock();

         boolean var5;
         try {
            UnlockHook otherHook = view.readLock();

            try {
               var5 = this.elements.equals(view.elements);
            } catch (Throwable var9) {
               if (otherHook != null) {
                  try {
                     otherHook.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }
               }

               throw var9;
            }

            if (otherHook != null) {
               otherHook.close();
            }
         } catch (Throwable var10) {
            if (hook != null) {
               try {
                  hook.close();
               } catch (Throwable var7) {
                  var10.addSuppressed(var7);
               }
            }

            throw var10;
         }

         if (hook != null) {
            hook.close();
         }

         return var5;
      }
   }

   public String toString() {
      return this.asList().toString();
   }

   public static class UnifiedShardCacheViewImpl implements ShardCacheView {
      protected final Supplier<? extends Stream<? extends ShardCacheView>> generator;

      public UnifiedShardCacheViewImpl(Supplier<? extends Stream<? extends ShardCacheView>> generator) {
         this.generator = generator;
      }

      public long size() {
         return this.distinctStream().mapToLong(CacheView::size).sum();
      }

      public boolean isEmpty() {
         return ((Stream)this.generator.get()).allMatch(CacheView::isEmpty);
      }

      @Nonnull
      public List<JDA> asList() {
         List<JDA> list = new ArrayList();
         Stream var10000 = this.stream();
         Objects.requireNonNull(list);
         var10000.forEach(list::add);
         return Collections.unmodifiableList(list);
      }

      @Nonnull
      public Set<JDA> asSet() {
         Set<JDA> set = new HashSet();
         Stream var10000 = ((Stream)this.generator.get()).flatMap(CacheView::stream);
         Objects.requireNonNull(set);
         var10000.forEach(set::add);
         return Collections.unmodifiableSet(set);
      }

      @Nonnull
      public ClosableIterator<JDA> lockedIterator() {
         Iterator<? extends ShardCacheView> gen = ((Stream)this.generator.get()).iterator();
         return new ChainedClosableIterator<JDA>(gen);
      }

      @Nonnull
      public List<JDA> getElementsByName(@Nonnull String name, boolean ignoreCase) {
         return (List)this.distinctStream().flatMap((view) -> view.getElementsByName(name, ignoreCase).stream()).collect(Helpers.toUnmodifiableList());
      }

      public JDA getElementById(int id) {
         return (JDA)((Stream)this.generator.get()).map((view) -> view.getElementById(id)).filter(Objects::nonNull).findFirst().orElse((Object)null);
      }

      @Nonnull
      public Stream<JDA> stream() {
         return ((Stream)this.generator.get()).flatMap(CacheView::stream).distinct();
      }

      @Nonnull
      public Stream<JDA> parallelStream() {
         return ((Stream)this.generator.get()).flatMap(CacheView::parallelStream).distinct();
      }

      @Nonnull
      public Iterator<JDA> iterator() {
         return this.stream().iterator();
      }

      protected Stream<? extends ShardCacheView> distinctStream() {
         return ((Stream)this.generator.get()).distinct();
      }
   }
}
