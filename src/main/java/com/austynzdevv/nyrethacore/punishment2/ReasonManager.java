package com.austynzdevv.nyrethacore.punishment2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ReasonManager {
   private final EconomyPunishment plugin;
   private FileConfiguration reasons;

   public ReasonManager(EconomyPunishment plugin) {
      this.plugin = plugin;
      this.load();
   }

   public void load() {
      File file = new File(this.plugin.getDataFolder(), "reasons.yml");
      if (!file.exists()) {
         this.plugin.saveResource("reasons.yml", false);
      }

      this.reasons = YamlConfiguration.loadConfiguration(file);
   }

   public String getType(String reason) {
      for(String key : this.reasons.getKeys(false)) {
         String stored = this.reasons.getString(key + ".reason");
         if (stored != null && stored.equalsIgnoreCase(reason)) {
            return this.reasons.getString(key + ".type", "BAN");
         }
      }

      return null;
   }

   public String getDuration(String reason, int offense) {
      for(String key : this.reasons.getKeys(false)) {
         String stored = this.reasons.getString(key + ".reason");
         if (stored != null && stored.equalsIgnoreCase(reason)) {
            return this.reasons.getString(key + ".offenses." + offense, "Permanent");
         }
      }

      return null;
   }

   public List<String> getReasons() {
      List<String> list = new ArrayList();

      for(String key : this.reasons.getKeys(false)) {
         String reason = this.reasons.getString(key + ".reason");
         if (reason != null) {
            list.add(reason);
         }
      }

      return list;
   }
}
