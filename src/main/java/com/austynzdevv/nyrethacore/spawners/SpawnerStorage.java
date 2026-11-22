package com.austynzdevv.nyrethacore.spawners;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

public class SpawnerStorage {
   private final EconomySpawners plugin;
   private final File file;
   private final Map<String, SpawnerData> byLocation = new ConcurrentHashMap();
   private final Map<UUID, SpawnerData> byId = new ConcurrentHashMap();

   public SpawnerStorage(EconomySpawners plugin) {
      this.plugin = plugin;
      this.file = new File(plugin.getDataFolder(), "data.yml");
   }

   public void load() {
      this.byLocation.clear();
      this.byId.clear();
      if (this.file.exists()) {
         FileConfiguration config = YamlConfiguration.loadConfiguration(this.file);
         ConfigurationSection root = config.getConfigurationSection("spawners");
         if (root != null) {
            for(String key : root.getKeys(false)) {
               ConfigurationSection sec = root.getConfigurationSection(key);
               if (sec != null) {
                  try {
                     UUID id = UUID.fromString(sec.getString("id", key));
                     String world = sec.getString("world");
                     int x = sec.getInt("x");
                     int y = sec.getInt("y");
                     int z = sec.getInt("z");
                     EntityType type = EntityType.valueOf(sec.getString("type"));
                     int stack = sec.getInt("stack", 1);
                     int exp = sec.getInt("exp", 0);
                     long last = sec.getLong("last", System.currentTimeMillis());
                     int ticks = sec.getInt("ticks", 0);
                     Map<Material, Integer> items = new HashMap();
                     ConfigurationSection itemSec = sec.getConfigurationSection("items");
                     if (itemSec != null) {
                        for(String mKey : itemSec.getKeys(false)) {
                           try {
                              items.put(Material.valueOf(mKey), itemSec.getInt(mKey));
                           } catch (IllegalArgumentException var22) {
                           }
                        }
                     }

                     SpawnerData data = new SpawnerData(id, world, x, y, z, type, stack, exp, items, last, ticks);
                     this.byLocation.put(data.locationKey(), data);
                     this.byId.put(id, data);
                  } catch (Exception ex) {
                     this.plugin.getLogger().warning("Failed to load spawner " + key + ": " + ex.getMessage());
                  }
               }
            }

         }
      }
   }

   public void save() {
      FileConfiguration config = new YamlConfiguration();

      for(SpawnerData data : this.byId.values()) {
         String path = "spawners." + String.valueOf(data.getId());
         config.set(path + ".id", data.getId().toString());
         config.set(path + ".world", data.getWorldName());
         config.set(path + ".x", data.getX());
         config.set(path + ".y", data.getY());
         config.set(path + ".z", data.getZ());
         config.set(path + ".type", data.getEntityType().name());
         config.set(path + ".stack", data.getStackSize());
         config.set(path + ".exp", data.getStoredExp());
         config.set(path + ".last", data.getLastGenerate());
         config.set(path + ".ticks", data.getTicksUntilGenerate());

         for(Map.Entry<Material, Integer> e : data.getStorage().entrySet()) {
            config.set(path + ".items." + ((Material)e.getKey()).name(), e.getValue());
         }

         data.setDirty(false);
      }

      try {
         config.save(this.file);
      } catch (IOException e) {
         this.plugin.getLogger().severe("Could not save data.yml: " + e.getMessage());
      }

   }

   public void put(SpawnerData data) {
      this.byLocation.put(data.locationKey(), data);
      this.byId.put(data.getId(), data);
   }

   public void remove(SpawnerData data) {
      this.byLocation.remove(data.locationKey());
      this.byId.remove(data.getId());
   }

   public SpawnerData getByLocation(String key) {
      return (SpawnerData)this.byLocation.get(key);
   }

   public SpawnerData getById(UUID id) {
      return (SpawnerData)this.byId.get(id);
   }

   public Map<UUID, SpawnerData> all() {
      return this.byId;
   }

   public int size() {
      return this.byId.size();
   }
}
