package me.xyr0.economysmpcore.crates;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import me.xyr0.economysmpcore.data.PlayerDataStore;
import me.xyr0.economysmpcore.data.SystemDataStore;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class DataManager {
   private final AstralCrates plugin;
   private final Map<String, String> bound = new HashMap();
   private final Map<UUID, Map<String, Integer>> virtual = new HashMap();
   private File file;
   private FileConfiguration cfg;
   private FileConfiguration playerCfg;

   public DataManager(AstralCrates plugin) {
      this.plugin = plugin;
   }

   public void load() {
      this.bound.clear();
      this.virtual.clear();
      this.file = SystemDataStore.get().file("crates.yml");
      this.cfg = SystemDataStore.get().load("crates.yml");
      this.playerCfg = PlayerDataStore.get().config();
      if (this.cfg.getConfigurationSection("bound") != null) {
         for(String key : this.cfg.getConfigurationSection("bound").getKeys(false)) {
            this.bound.put(key, this.cfg.getString("bound." + key));
         }
      }

      if (this.playerCfg.getConfigurationSection("players") != null) {
         for(String uuid : this.playerCfg.getConfigurationSection("players").getKeys(false)) {
            try {
               UUID id = UUID.fromString(uuid);
               Map<String, Integer> map = new HashMap();
               if (this.playerCfg.getConfigurationSection("players." + uuid + ".crates.virtual") != null) {
                  for(String crate : this.playerCfg.getConfigurationSection("players." + uuid + ".crates.virtual").getKeys(false)) {
                     map.put(crate.toLowerCase(), this.playerCfg.getInt("players." + uuid + ".crates.virtual." + crate));
                  }
               }

               this.virtual.put(id, map);
            } catch (Exception var7) {
            }
         }
      }

   }

   public void save() {
      this.cfg.set("bound", (Object)null);

      for(Map.Entry<String, String> e : this.bound.entrySet()) {
         this.cfg.set("bound." + (String)e.getKey(), e.getValue());
      }

      for(Map.Entry<UUID, Map<String, Integer>> e : this.virtual.entrySet()) {
         for(Map.Entry<String, Integer> c : e.getValue().entrySet()) {
            this.playerCfg.set("players." + String.valueOf(e.getKey()) + ".crates.virtual." + (String)c.getKey(), c.getValue());
         }
      }

      SystemDataStore.get().save("crates.yml", (YamlConfiguration)this.cfg);
      PlayerDataStore.get().save();
   }

   public static String locKey(Location loc) {
      if (loc != null && loc.getWorld() != null) {
         String var10000 = loc.getWorld().getName();
         return var10000 + ";" + loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
      } else {
         return null;
      }
   }

   public void bind(Location loc, String crateId) {
      String key = locKey(loc);
      if (key != null) {
         this.bound.put(key, crateId.toLowerCase());
         this.save();
      }
   }

   public boolean unbind(Location loc) {
      String key = locKey(loc);
      if (key == null) {
         return false;
      } else {
         boolean removed = this.bound.remove(key) != null;
         if (removed) {
            this.save();
         }

         return removed;
      }
   }

   public String getBound(Location loc) {
      String key = locKey(loc);
      return key == null ? null : (String)this.bound.get(key);
   }

   public String getBoundByKey(String key) {
      return (String)this.bound.get(key);
   }

   public Set<Map.Entry<String, String>> boundEntries() {
      return this.bound.entrySet();
   }

   public int getVirtual(UUID uuid, String crateId) {
      Map<String, Integer> map = (Map)this.virtual.get(uuid);
      return map == null ? 0 : (Integer)map.getOrDefault(crateId.toLowerCase(), 0);
   }

   public void setVirtual(UUID uuid, String crateId, int amount) {
      ((Map)this.virtual.computeIfAbsent(uuid, (k) -> new HashMap())).put(crateId.toLowerCase(), Math.max(0, amount));
      this.save();
   }

   public void addVirtual(UUID uuid, String crateId, int amount) {
      this.setVirtual(uuid, crateId, this.getVirtual(uuid, crateId) + amount);
   }
}
