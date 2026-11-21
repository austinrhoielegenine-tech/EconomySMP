package com.austynzdevv.novacore.shards;

import java.io.File;
import java.util.UUID;
import com.austynzdevv.novacore.data.PlayerDataStore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public final class ShardManager {
   private final EconomyShards plugin;
   private File file;
   private FileConfiguration data;

   public ShardManager(EconomyShards plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      this.file = PlayerDataStore.get().file();
      this.data = PlayerDataStore.get().config();
   }

   public void save() {
      if (this.data != null && this.file != null) {
         PlayerDataStore.get().save();
      }
   }

   public long getBalance(UUID id) {
      return this.data.getLong("players." + id.toString() + ".shards", 0L);
   }

   public long getBalance(Player player) {
      return this.getBalance(player.getUniqueId());
   }

   public void setBalance(UUID id, long amount) {
      if (amount < 0L) {
         amount = 0L;
      }

      this.data.set("players." + id.toString() + ".shards", amount);
      this.save();
   }

   public void setBalance(Player player, long amount) {
      this.setBalance(player.getUniqueId(), amount);
   }

   public void add(UUID id, long amount) {
      if (amount > 0L) {
         this.setBalance(id, this.getBalance(id) + amount);
      }
   }

   public void add(Player player, long amount) {
      this.add(player.getUniqueId(), amount);
   }

   public boolean take(UUID id, long amount) {
      if (amount <= 0L) {
         return true;
      } else {
         long bal = this.getBalance(id);
         if (bal < amount) {
            return false;
         } else {
            this.setBalance(id, bal - amount);
            return true;
         }
      }
   }

   public boolean take(Player player, long amount) {
      return this.take(player.getUniqueId(), amount);
   }

   public boolean has(UUID id, long amount) {
      return this.getBalance(id) >= amount;
   }

   public boolean has(Player player, long amount) {
      return this.has(player.getUniqueId(), amount);
   }
}
