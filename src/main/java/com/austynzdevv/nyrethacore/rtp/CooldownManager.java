package com.austynzdevv.nyrethacore.rtp;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {
   private final Map<UUID, Long> map = new ConcurrentHashMap();

   public void set(UUID id) {
      this.map.put(id, System.currentTimeMillis());
   }

   public boolean hasCooldown(UUID id, long seconds) {
      Long started = (Long)this.map.get(id);
      if (started == null) {
         return false;
      } else {
         return System.currentTimeMillis() - started < seconds * 1000L;
      }
   }

   public long getRemaining(UUID id, long seconds) {
      Long started = (Long)this.map.get(id);
      if (started == null) {
         return 0L;
      } else {
         long left = started + seconds * 1000L - System.currentTimeMillis();
         return left <= 0L ? 0L : left / 1000L;
      }
   }

   public void setCooldown(UUID id) {
      this.set(id);
   }
}
