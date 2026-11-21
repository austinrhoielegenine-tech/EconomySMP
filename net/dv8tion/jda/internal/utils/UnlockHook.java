package net.dv8tion.jda.internal.utils;

import java.util.concurrent.locks.Lock;

public class UnlockHook implements AutoCloseable {
   private final Lock lock;

   public UnlockHook(Lock lock) {
      this.lock = lock;
   }

   public void close() {
      this.lock.unlock();
   }
}
