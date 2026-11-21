package net.dv8tion.jda.internal.utils.cache;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import java.lang.reflect.Array;
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
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.utils.LockIterator;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.cache.CacheView;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.UnlockHook;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;

public abstract class AbstractCacheView<T> extends ReadWriteLockCache<T> implements CacheView<T> {
   protected final TLongObjectMap<T> elements = new TLongObjectHashMap<T>();
   protected final T[] emptyArray;
   protected final Function<T, String> nameMapper;
   protected final Class<T> type;

   protected AbstractCacheView(Class<T> type, Function<T, String> nameMapper) {
      this.nameMapper = nameMapper;
      this.type = type;
      this.emptyArray = (T[])((Object[])Array.newInstance(type, 0));
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

   public TLongObjectMap<T> getMap() {
      if (!this.lock.writeLock().isHeldByCurrentThread()) {
         throw new IllegalStateException("Cannot access map directly without holding write lock!");
      } else {
         return this.elements;
      }
   }

   public T get(long id) {
      UnlockHook hook = this.readLock();

      Object var4;
      try {
         var4 = this.elements.get(id);
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

      return (T)var4;
   }

   public T remove(long id) {
      UnlockHook hook = this.writeLock();

      Object var4;
      try {
         var4 = this.elements.remove(id);
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

      return (T)var4;
   }

   public TLongSet keySet() {
      UnlockHook hook = this.readLock();

      TLongHashSet var2;
      try {
         var2 = new TLongHashSet(this.elements.keySet());
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

   public void forEach(Consumer<? super T> action) {
      Objects.requireNonNull(action);
      UnlockHook hook = this.readLock();

      try {
         for(T elem : this.elements.valueCollection()) {
            action.accept(elem);
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
   public LockIterator<T> lockedIterator() {
      ReentrantReadWriteLock.ReadLock readLock = this.lock.readLock();
      MiscUtil.tryLock(readLock);

      try {
         Iterator<T> directIterator = this.elements.valueCollection().iterator();
         return new LockIterator<T>(directIterator, readLock);
      } catch (Throwable t) {
         readLock.unlock();
         throw t;
      }
   }

   @Nonnull
   public List<T> asList() {
      if (this.isEmpty()) {
         return Collections.emptyList();
      } else {
         UnlockHook hook = this.readLock();

         List var7;
         label47: {
            try {
               List<T> list = this.getCachedList();
               if (list != null) {
                  var7 = list;
                  break label47;
               }

               List<T> var6 = new ArrayList(this.elements.size());
               TLongObjectMap var10000 = this.elements;
               Objects.requireNonNull(var6);
               var10000.forEachValue(var6::add);
               var7 = this.cache(var6);
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

            return var7;
         }

         if (hook != null) {
            hook.close();
         }

         return var7;
      }
   }

   @Nonnull
   public Set<T> asSet() {
      if (this.isEmpty()) {
         return Collections.emptySet();
      } else {
         UnlockHook hook = this.readLock();

         Set var7;
         label47: {
            try {
               Set<T> set = this.getCachedSet();
               if (set != null) {
                  var7 = set;
                  break label47;
               }

               Set<T> var6 = new HashSet(this.elements.size());
               TLongObjectMap var10000 = this.elements;
               Objects.requireNonNull(var6);
               var10000.forEachValue(var6::add);
               var7 = this.cache(var6);
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

            return var7;
         }

         if (hook != null) {
            hook.close();
         }

         return var7;
      }
   }

   public long size() {
      return (long)this.elements.size();
   }

   public boolean isEmpty() {
      return this.elements.isEmpty();
   }

   @Nonnull
   public List<T> getElementsByName(@Nonnull String name, boolean ignoreCase) {
      Checks.notEmpty(name, "Name");
      if (this.elements.isEmpty()) {
         return Collections.emptyList();
      } else if (this.nameMapper == null) {
         throw new UnsupportedOperationException("The contained elements are not assigned with names.");
      } else if (this.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<T> list = new ArrayList();
         this.forEach((elem) -> {
            String elementName = (String)this.nameMapper.apply(elem);
            if (elementName != null && this.equals(ignoreCase, elementName, name)) {
               list.add(elem);
            }

         });
         return list;
      }
   }

   public Spliterator<T> spliterator() {
      UnlockHook hook = this.readLock();

      Spliterator var2;
      try {
         var2 = Spliterators.spliterator(this.elements.values(), 1024);
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
   public Stream<T> stream() {
      return StreamSupport.stream(this.spliterator(), false);
   }

   @Nonnull
   public Stream<T> parallelStream() {
      return StreamSupport.stream(this.spliterator(), true);
   }

   @Nonnull
   public Iterator<T> iterator() {
      UnlockHook hook = this.readLock();

      ObjectArrayIterator var2;
      try {
         var2 = new ObjectArrayIterator(this.elements.values(this.emptyArray));
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

   public String toString() {
      return this.asList().toString();
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
      } else if (!(obj instanceof AbstractCacheView)) {
         return false;
      } else {
         AbstractCacheView<?> view = (AbstractCacheView)obj;
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

   protected boolean equals(boolean ignoreCase, String first, String second) {
      return ignoreCase ? first.equalsIgnoreCase(second) : first.equals(second);
   }
}
