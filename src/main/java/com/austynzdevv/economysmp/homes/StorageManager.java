package com.austynzdevv.economysmp.homes;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.economysmp.data.PlayerDataStore;
import com.austynzdevv.economysmp.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class StorageManager {
   private final EconomyHomes plugin;
   private final Map<UUID, PlayerData> cache = new ConcurrentHashMap();
   private final SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

   public StorageManager(EconomyHomes plugin) {
      this.plugin = plugin;
      this.migrateLegacyHomes();
   }

   public void reload() {
      this.cache.clear();
   }

   public void saveAll() {
      for(PlayerData data : this.cache.values()) {
         this.save(data);
      }

   }

   public PlayerData get(Player player) {
      return this.get(player.getUniqueId(), player.getName());
   }

   public PlayerData get(UUID uuid, String name) {
      return (PlayerData)this.cache.computeIfAbsent(uuid, (id) -> this.load(uuid, name));
   }

   private PlayerData load(UUID uuid, String name) {
      PlayerData data = new PlayerData(uuid, name);
      ConfigurationSection root = PlayerDataStore.get().config().getConfigurationSection("players." + String.valueOf(uuid));
      if (root == null) {
         data.lastSeen = this.fmt.format(new Date());
         return data;
      } else {
         data.name = root.getString("name", name);
         data.lastSeen = root.getString("lastSeen", this.fmt.format(new Date()));
         ConfigurationSection section = root.getConfigurationSection("homes");
         if (section != null) {
            for(String key : section.getKeys(false)) {
               ConfigurationSection h = section.getConfigurationSection(key);
               if (h != null) {
                  World world = Bukkit.getWorld(h.getString("world", ""));
                  if (world != null) {
                     Location loc = new Location(world, h.getDouble("x"), h.getDouble("y"), h.getDouble("z"), (float)h.getDouble("yaw"), (float)h.getDouble("pitch"));
                     data.homes.put(key, new Home(loc, h.getString("timestamp", this.fmt.format(new Date()))));
                  }
               }
            }
         }

         return data;
      }
   }

   public void save(PlayerData data) {
      String base = "players." + String.valueOf(data.uuid);
      YamlConfiguration cfg = PlayerDataStore.get().config();
      cfg.set(base + ".name", data.name);
      cfg.set(base + ".lastSeen", data.lastSeen != null ? data.lastSeen : this.fmt.format(new Date()));
      cfg.set(base + ".homes", (Object)null);

      for(Map.Entry<String, Home> e : data.homes.entrySet()) {
         String path = base + ".homes." + (String)e.getKey();
         Home home = (Home)e.getValue();
         Location loc = home.location;
         cfg.set(path + ".timestamp", home.timestamp);
         cfg.set(path + ".world", loc.getWorld().getName());
         cfg.set(path + ".x", loc.getX());
         cfg.set(path + ".y", loc.getY());
         cfg.set(path + ".z", loc.getZ());
         cfg.set(path + ".yaw", loc.getYaw());
         cfg.set(path + ".pitch", loc.getPitch());
      }

      PlayerDataStore.get().save();
   }

   private void migrateLegacyHomes() {
      File folder = new File(this.plugin.getCore().getDataFolder(), "homes/Playerdata");
      if (folder.exists()) {
         File[] files = folder.listFiles((d, n) -> n.endsWith(".yml"));
         if (files != null) {
            YamlConfiguration cfg = PlayerDataStore.get().config();

            for(File file : files) {
               YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
               String raw = yml.getString("uuid");
               if (raw != null) {
                  String base = "players." + raw;
                  if (!cfg.contains(base + ".homes")) {
                     cfg.set(base + ".name", yml.getString("name", file.getName().replace(".yml", "")));
                     cfg.set(base + ".lastSeen", yml.getString("lastSeen"));
                     ConfigurationSection homes = yml.getConfigurationSection("homes");
                     if (homes != null) {
                        for(String key : homes.getKeys(false)) {
                           ConfigurationSection h = homes.getConfigurationSection(key);
                           if (h != null) {
                              for(String k : h.getKeys(false)) {
                                 cfg.set(base + ".homes." + key + "." + k, h.get(k));
                              }
                           }
                        }
                     }
                  }

                  file.delete();
               }
            }

            PlayerDataStore.get().save();
            folder.delete();
            File parent = folder.getParentFile();
            if (parent != null) {
               parent.delete();
            }

         }
      }
   }

   public void saveAsync(PlayerData data) {
      Tasks.runAsync(this.plugin.getCore(), () -> this.save(data));
   }

   public void touch(Player player) {
      PlayerData data = this.get(player);
      data.name = player.getName();
      data.lastSeen = this.fmt.format(new Date());
      this.saveAsync(data);
   }

   public void unload(UUID uuid) {
      PlayerData data = (PlayerData)this.cache.remove(uuid);
      if (data != null) {
         this.save(data);
      }

   }

   public static final class PlayerData {
      public final UUID uuid;
      public String name;
      public String lastSeen;
      public final Map<String, Home> homes = new LinkedHashMap();

      public PlayerData(UUID uuid, String name) {
         this.uuid = uuid;
         this.name = name;
      }

      public boolean has(String id) {
         return this.homes.containsKey(id);
      }

      public Home get(String id) {
         return (Home)this.homes.get(id);
      }

      public void set(String id, Home home) {
         this.homes.put(id, home);
      }

      public void remove(String id) {
         this.homes.remove(id);
      }

      public int count() {
         return this.homes.size();
      }
   }

   public static final class Home {
      public final Location location;
      public final String timestamp;

      public Home(Location location, String timestamp) {
         this.location = location;
         this.timestamp = timestamp;
      }
   }
}
