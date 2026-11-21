package com.austynzdevv.economysmp.warps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class WarpManager {
   private final EconomyWarps plugin;
   private final Map<String, NamedLocation> warps = new LinkedHashMap();
   private final Map<String, NamedLocation> spawns = new LinkedHashMap();
   private final Map<String, NamedLocation> afks = new LinkedHashMap();
   private File warpsFile;
   private File spawnsFile;
   private File afkFile;
   private FileConfiguration warpsCfg;
   private FileConfiguration spawnsCfg;
   private FileConfiguration afkCfg;

   public WarpManager(EconomyWarps plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      this.warps.clear();
      this.spawns.clear();
      this.afks.clear();
      this.warpsFile = new File(this.plugin.getDataFolder(), "setting.yml");
      this.spawnsFile = new File(this.plugin.getDataFolder(), "spawns.yml");
      this.afkFile = new File(this.plugin.getDataFolder(), "afk.yml");
      if (!this.warpsFile.exists()) {
         this.plugin.saveResource("setting.yml", false);
      }

      if (!this.spawnsFile.exists()) {
         this.plugin.saveResource("spawns.yml", false);
      }

      if (!this.afkFile.exists()) {
         this.plugin.saveResource("afk.yml", false);
      }

      this.warpsCfg = YamlConfiguration.loadConfiguration(this.warpsFile);
      this.spawnsCfg = YamlConfiguration.loadConfiguration(this.spawnsFile);
      this.afkCfg = YamlConfiguration.loadConfiguration(this.afkFile);
      this.loadMap(this.warpsCfg.getConfigurationSection("warps"), this.warps, false);
      this.loadMap(this.spawnsCfg.getConfigurationSection("spawns"), this.spawns, true);
      this.loadMap(this.afkCfg.getConfigurationSection("afk-areas"), this.afks, true);
   }

   private void loadMap(ConfigurationSection sec, Map<String, NamedLocation> map, boolean withMax) {
      if (sec != null) {
         for(String key : sec.getKeys(false)) {
            ConfigurationSection s = sec.getConfigurationSection(key);
            if (s != null) {
               int max = withMax ? s.getInt("maxPlayers", -1) : -1;
               map.put(key.toLowerCase(Locale.ROOT), new NamedLocation(key, s.getString("world", "world"), s.getDouble("x"), s.getDouble("y"), s.getDouble("z"), (float)s.getDouble("yaw"), (float)s.getDouble("pitch"), max));
            }
         }

      }
   }

   public void saveAll() {
      this.saveSection(this.warpsCfg, this.warpsFile, "warps", this.warps, false);
      this.saveSection(this.spawnsCfg, this.spawnsFile, "spawns", this.spawns, true);
      this.saveSection(this.afkCfg, this.afkFile, "afk-areas", this.afks, true);
   }

   private void saveSection(FileConfiguration cfg, File file, String root, Map<String, NamedLocation> map, boolean withMax) {
      cfg.set(root, (Object)null);

      for(NamedLocation loc : map.values()) {
         String path = root + "." + loc.getName();
         Location l = loc.toLocation();
         if (l != null && l.getWorld() != null) {
            cfg.set(path + ".world", l.getWorld().getName());
            cfg.set(path + ".x", l.getX());
            cfg.set(path + ".y", l.getY());
            cfg.set(path + ".z", l.getZ());
            cfg.set(path + ".yaw", l.getYaw());
            cfg.set(path + ".pitch", l.getPitch());
            if (withMax) {
               cfg.set(path + ".maxPlayers", loc.getMaxPlayers());
            }
         }
      }

      try {
         cfg.save(file);
      } catch (IOException var10) {
      }

   }

   public NamedLocation getWarp(String name) {
      return (NamedLocation)this.warps.get(name.toLowerCase(Locale.ROOT));
   }

   public NamedLocation getSpawn(String name) {
      return (NamedLocation)this.spawns.get(name.toLowerCase(Locale.ROOT));
   }

   public NamedLocation getAfk(String name) {
      return (NamedLocation)this.afks.get(name.toLowerCase(Locale.ROOT));
   }

   public Map<String, NamedLocation> getWarps() {
      return Collections.unmodifiableMap(this.warps);
   }

   public Map<String, NamedLocation> getSpawns() {
      return Collections.unmodifiableMap(this.spawns);
   }

   public Map<String, NamedLocation> getAfks() {
      return Collections.unmodifiableMap(this.afks);
   }

   public boolean setWarp(String name, Location loc) {
      String key = name.toLowerCase(Locale.ROOT);
      if (this.warps.containsKey(key)) {
         return false;
      } else {
         this.warps.put(key, new NamedLocation(name, loc, -1));
         this.saveAll();
         return true;
      }
   }

   public boolean deleteWarp(String name) {
      NamedLocation removed = (NamedLocation)this.warps.remove(name.toLowerCase(Locale.ROOT));
      if (removed != null) {
         this.saveAll();
         return true;
      } else {
         return false;
      }
   }

   public void setSpawn(String name, Location loc, int maxPlayers) {
      this.spawns.put(name.toLowerCase(Locale.ROOT), new NamedLocation(name, loc, maxPlayers));
      this.saveAll();
   }

   public boolean deleteSpawn(String name) {
      NamedLocation removed = (NamedLocation)this.spawns.remove(name.toLowerCase(Locale.ROOT));
      if (removed != null) {
         this.saveAll();
         return true;
      } else {
         return false;
      }
   }

   public void setAfk(String name, Location loc, int maxPlayers) {
      this.afks.put(name.toLowerCase(Locale.ROOT), new NamedLocation(name, loc, maxPlayers));
      this.saveAll();
   }

   public boolean deleteAfk(String name) {
      NamedLocation removed = (NamedLocation)this.afks.remove(name.toLowerCase(Locale.ROOT));
      if (removed != null) {
         this.saveAll();
         return true;
      } else {
         return false;
      }
   }

   public NamedLocation randomSpawn() {
      List<NamedLocation> available = new ArrayList();

      for(NamedLocation loc : this.spawns.values()) {
         if (!loc.isFull() && loc.toLocation() != null) {
            available.add(loc);
         }
      }

      if (available.isEmpty()) {
         return null;
      } else {
         return (NamedLocation)available.get(ThreadLocalRandom.current().nextInt(available.size()));
      }
   }

   public NamedLocation randomAfk() {
      List<NamedLocation> available = new ArrayList();

      for(NamedLocation loc : this.afks.values()) {
         if (!loc.isFull() && loc.toLocation() != null) {
            available.add(loc);
         }
      }

      if (available.isEmpty()) {
         return null;
      } else {
         return (NamedLocation)available.get(ThreadLocalRandom.current().nextInt(available.size()));
      }
   }

   public NamedLocation defaultSpawn() {
      String name = this.plugin.getConfig().getString("default-spawn", "");
      if (name != null && !name.isBlank()) {
         NamedLocation loc = this.getSpawn(name);
         if (loc != null) {
            return loc;
         }
      }

      return !this.spawns.isEmpty() ? (NamedLocation)this.spawns.values().iterator().next() : null;
   }
}
