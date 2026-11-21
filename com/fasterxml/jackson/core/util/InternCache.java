package com.fasterxml.jackson.core.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public final class InternCache extends ConcurrentHashMap<String, String> {
   private static final long serialVersionUID = 1L;
   private static final int DEFAULT_MAX_ENTRIES = 280;
   public static final InternCache instance = new InternCache();
   private final ReentrantLock lock;

   public InternCache() {
      this(280, 0.8F, 4);
   }

   public InternCache(int maxSize, float loadFactor, int concurrency) {
      super(maxSize, loadFactor, concurrency);
      this.lock = new ReentrantLock();
   }

   public String intern(String input) {
      String result = (String)this.get(input);
      if (result != null) {
         return result;
      } else {
         if (this.size() >= 280 && this.lock.tryLock()) {
            try {
               if (this.size() >= 280) {
                  this.clear();
               }
            } finally {
               this.lock.unlock();
            }
         }

         result = input.intern();
         this.put(result, result);
         return result;
      }
   }
}
