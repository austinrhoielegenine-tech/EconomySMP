package com.austynzdevv.novacore.rtpq;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class WorldManager {
   private final EconomyRTPQ plugin;
   private final Map<String, WorldConfig> worlds = new LinkedHashMap();

   public WorldManager(EconomyRTPQ plugin) {
      this.plugin = plugin;
      this.load();
   }

   public void load() {
      this.worlds.clear();
      File file = new File(this.plugin.getDataFolder(), "worlds.yml");
      if (!file.exists()) {
         this.plugin.saveResource("worlds.yml", false);
      }

      FileConfiguration config = YamlConfiguration.loadConfiguration(file);
      ConfigurationSection section = config.getConfigurationSection("worlds");
      if (section == null) {
         this.plugin.getLogger().warning("No worlds found in worlds.yml!");
      } else {
         for(String id : section.getKeys(false)) {
            ConfigurationSection worldSec = section.getConfigurationSection(id);
            if (worldSec != null) {
               String displayName = worldSec.getString("display-name", id);
               String worldName = worldSec.getString("world", id);
               int minRadius = worldSec.getInt("min-radius", 500);
               int radius = worldSec.getInt("radius", 5000);
               this.worlds.put(id, new WorldConfig(id, displayName, worldName, minRadius, radius));
            }
         }

         this.plugin.getLogger().info("Loaded " + this.worlds.size() + " RTP world(s).");
      }
   }

   public Map<String, WorldConfig> getWorlds() {
      return Collections.unmodifiableMap(this.worlds);
   }

   public WorldConfig getWorld(String id) {
      return (WorldConfig)this.worlds.get(id);
   }

   public boolean hasWorld(String id) {
      return this.worlds.containsKey(id);
   }
}
