package com.austynzdevv.novacore.shards;

import java.util.HashSet;
import java.util.UUID;
import com.austynzdevv.novacore.data.PlayerDataStore;
import org.bukkit.entity.Player;

public final class BoosterManager {
   private final EconomyShards plugin;

   public BoosterManager(EconomyShards plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      this.cleanupExpired();
   }

   public void save() {
      PlayerDataStore.get().save();
   }

   private void cleanupExpired() {
      if (PlayerDataStore.get().config().getConfigurationSection("shard-boosters.active") != null) {
         long now = System.currentTimeMillis();

         for(String key : new HashSet<>(PlayerDataStore.get().config().getConfigurationSection("shard-boosters.active").getKeys(false))) {
            long end = PlayerDataStore.get().config().getLong("shard-boosters.active." + key, 0L);
            if (end <= now) {
               PlayerDataStore.get().config().set("shard-boosters.active." + key, (Object)null);
            }
         }

         this.save();
      }
   }

   public boolean isActive(UUID uuid) {
      long end = this.getEndTime(uuid);
      if (end <= 0L) {
         return false;
      } else if (System.currentTimeMillis() >= end) {
         PlayerDataStore.get().config().set("shard-boosters.active." + String.valueOf(uuid), (Object)null);
         this.save();
         return false;
      } else {
         return true;
      }
   }

   public long getEndTime(UUID uuid) {
      return PlayerDataStore.get().config().getLong("shard-boosters.active." + String.valueOf(uuid), 0L);
   }

   public long getRemainingMillis(UUID uuid) {
      long end = this.getEndTime(uuid);
      return end <= 0L ? 0L : Math.max(0L, end - System.currentTimeMillis());
   }

   public boolean activate(Player player) {
      UUID id = player.getUniqueId();
      if (this.isActive(id)) {
         return false;
      } else {
         long hours = this.plugin.getConfig().getLong("shard-booster.duration-hours", 24L);
         if (hours < 1L) {
            hours = 24L;
         }

         long end = System.currentTimeMillis() + hours * 60L * 60L * 1000L;
         PlayerDataStore.get().config().set("shard-boosters.active." + String.valueOf(id), end);
         this.save();
         return true;
      }
   }

   public void clear(UUID uuid) {
      PlayerDataStore.get().config().set("shard-boosters.active." + String.valueOf(uuid), (Object)null);
      this.save();
   }

   public double getSpeedMultiplier(UUID uuid) {
      return !this.isActive(uuid) ? (double)1.0F : Math.max((double)1.0F, this.plugin.getConfig().getDouble("shard-booster.speed-multiplier", (double)4.0F));
   }
}
