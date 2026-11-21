package net.dv8tion.jda.api.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import javax.annotation.Nonnull;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

public class LockIterator<T> implements ClosableIterator<T> {
   private static final Logger log = JDALogger.getLog(ClosableIterator.class);
   private final Iterator<? extends T> it;
   private Lock lock;

   public LockIterator(@Nonnull Iterator<? extends T> it, Lock lock) {
      this.it = it;
      this.lock = lock;
   }

   public void close() {
      if (this.lock != null) {
         this.lock.unlock();
      }

      this.lock = null;
   }

   public boolean hasNext() {
      if (this.lock == null) {
         return false;
      } else {
         boolean hasNext = this.it.hasNext();
         if (!hasNext) {
            this.close();
         }

         return hasNext;
      }
   }

   @Nonnull
   public T next() {
      if (this.lock == null) {
         throw new NoSuchElementException();
      } else {
         return (T)this.it.next();
      }
   }

   /** @deprecated */
   @Deprecated
   protected void finalize() {
      if (this.lock != null) {
         log.error("Finalizing without closing, performing force close on lock");
         this.close();
      }

   }
}
