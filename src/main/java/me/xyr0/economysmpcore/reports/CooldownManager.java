package me.xyr0.economysmpcore.reports;

import java.util.HashMap;
import java.util.UUID;

public class CooldownManager {
   private final AstralReports plugin;
   private final HashMap<String, Long> cooldowns = new HashMap();

   public CooldownManager(AstralReports plugin) {
      this.plugin = plugin;
   }

   public boolean hasCooldown(UUID reporter, UUID reported) {
      if (!this.plugin.getConfig().getBoolean("report-cooldown.enabled", true)) {
         return false;
      } else {
         String var10000 = String.valueOf(reporter);
         String key = var10000 + ":" + String.valueOf(reported);
         if (!this.cooldowns.containsKey(key)) {
            return false;
         } else {
            return (Long)this.cooldowns.get(key) > System.currentTimeMillis();
         }
      }
   }

   public long getRemaining(UUID reporter, UUID reported) {
      String var10000 = String.valueOf(reporter);
      String key = var10000 + ":" + String.valueOf(reported);
      long time = (Long)this.cooldowns.getOrDefault(key, 0L);
      return Math.max(0L, time - System.currentTimeMillis());
   }

   public void setCooldown(UUID reporter, UUID reported) {
      String var10000 = String.valueOf(reporter);
      String key = var10000 + ":" + String.valueOf(reported);
      long duration = this.parseDuration(this.plugin.getConfig().getString("report-cooldown.duration", "30m"));
      this.cooldowns.put(key, System.currentTimeMillis() + duration);
   }

   private long parseDuration(String input) {
      try {
         long number = Long.parseLong(input.replaceAll("[^0-9]", ""));
         if (input.toLowerCase().endsWith("s")) {
            return number * 1000L;
         } else {
            return input.toLowerCase().endsWith("h") ? number * 3600000L : number * 60000L;
         }
      } catch (Exception var4) {
         return 1800000L;
      }
   }
}
