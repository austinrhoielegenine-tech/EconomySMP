package net.dv8tion.jda.internal.utils;

import java.util.concurrent.Callable;
import net.dv8tion.jda.api.audit.ThreadLocalReason;

public class ContextRunnable<E> implements Runnable, Callable<E> {
   private final String localReason = ThreadLocalReason.getCurrent();
   private final Runnable runnable;
   private final Callable<E> callable;

   public ContextRunnable(Runnable runnable) {
      this.runnable = runnable;
      this.callable = null;
   }

   public ContextRunnable(Callable<E> callable) {
      this.runnable = null;
      this.callable = callable;
   }

   public void run() {
      ThreadLocalReason.Closable __ = ThreadLocalReason.closable(this.localReason);

      try {
         this.runnable.run();
      } catch (Throwable var5) {
         if (__ != null) {
            try {
               __.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (__ != null) {
         __.close();
      }

   }

   public E call() throws Exception {
      ThreadLocalReason.Closable __ = ThreadLocalReason.closable(this.localReason);

      Object var2;
      try {
         var2 = this.callable.call();
      } catch (Throwable var5) {
         if (__ != null) {
            try {
               __.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (__ != null) {
         __.close();
      }

      return (E)var2;
   }
}
