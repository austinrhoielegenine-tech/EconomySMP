package com.austynzdevv.economysmp.spawners;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class SettingsConfig {
   private final EconomySpawners plugin;
   private FileConfiguration config;

   public SettingsConfig(EconomySpawners plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      File file = new File(this.plugin.getDataFolder(), "config.yml");
      if (!file.exists()) {
         this.plugin.saveResource("config.yml", false);
      }

      this.config = YamlConfiguration.loadConfiguration(file);
   }

   public boolean isDebug() {
      return this.config.getBoolean("debug", false);
   }

   public boolean isHolograms() {
      return this.config.getBoolean("holograms", false);
   }

   public int getHologramUpdateInterval() {
      return this.config.getInt("hologram-update-ticks", 40);
   }

   public long getGenerationInterval() {
      return this.config.getLong("generation.interval-ticks", 200L);
   }

   public boolean requirePlayerNearby() {
      return this.config.getBoolean("generation.require-player-nearby", true);
   }

   public int getActivationRange() {
      return this.config.getInt("generation.activation-range", 24);
   }

   public boolean offlineGeneration() {
      return this.config.getBoolean("generation.offline-generation", false);
   }

   public int getLootLimit() {
      return this.config.getInt("generation.loot-limit", -1);
   }

   public int getMaxStack() {
      return this.config.getInt("stacking.max-stack", 64);
   }

   public boolean mergeOnPlace() {
      return this.config.getBoolean("stacking.merge-on-place", true);
   }

   public boolean sameTypeOnly() {
      return this.config.getBoolean("stacking.same-type-only", true);
   }

   public boolean isBreakingEnabled() {
      return this.config.getBoolean("breaking.enabled", true);
   }

   public boolean silkTouchRequired() {
      return this.config.getBoolean("breaking.silk-touch-required", true);
   }

   public int getDropChance() {
      return this.config.getInt("breaking.drop-chance", 100);
   }

   public boolean directToInventory() {
      return this.config.getBoolean("breaking.direct-to-inventory", true);
   }

   public boolean explosionProtection() {
      return this.config.getBoolean("breaking.explosion-protection", true);
   }

   public int getMaxSlots() {
      return this.config.getInt("storage.max-slots", 45);
   }

   public int getMaxExp() {
      return this.config.getInt("storage.max-exp", 50000);
   }

   public boolean autoSell() {
      return this.config.getBoolean("storage.auto-sell", false);
   }

   public boolean economyEnabled() {
      return this.config.getBoolean("economy.enabled", true);
   }

   public String getCurrencySymbol() {
      return this.config.getString("economy.currency-symbol", "$");
   }

   public boolean asyncSave() {
      return this.config.getBoolean("performance.async-save", true);
   }

   public int getSaveInterval() {
      return this.config.getInt("performance.save-interval-seconds", 300);
   }

   public int getMaxSpawnersPerChunk() {
      return this.config.getInt("performance.max-spawners-per-chunk", 32);
   }

   public boolean disableHologramsForBedrock() {
      return this.config.getBoolean("bedrock.disable-holograms-for-bedrock", true);
   }

   public boolean convertNaturalSpawners() {
      return this.config.getBoolean("convert-natural-spawners", true);
   }

   public void setConvertNaturalSpawners(boolean value) {
      this.config.set("convert-natural-spawners", value);

      try {
         this.config.save(new File(this.plugin.getDataFolder(), "config.yml"));
      } catch (Exception e) {
         this.plugin.getLogger().warning("Could not save config.yml: " + e.getMessage());
      }

   }
}
