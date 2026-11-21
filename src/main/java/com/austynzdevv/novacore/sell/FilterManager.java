package com.austynzdevv.novacore.sell;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FilterManager {
   private final EconomySell plugin;
   private final Map<String, Set<Material>> filters = new LinkedHashMap();

   public FilterManager(EconomySell plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      this.filters.clear();
      File file = new File(this.plugin.getDataFolder(), "Worth/Worth-Filter.yml");
      if (!file.exists()) {
         file.getParentFile().mkdirs();
         this.plugin.saveResource("Worth/Worth-Filter.yml", false);
      }

      FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

      for(String key : cfg.getKeys(false)) {
         List<String> mats = cfg.getStringList(key);
         HashSet<Material> set = new HashSet();

         for(String m : mats) {
            try {
               set.add(Material.valueOf(m.toUpperCase()));
            } catch (Exception var10) {
            }
         }

         this.filters.put(key, set);
      }

      if (this.filters.isEmpty()) {
         this.filters.put("All", Set.of());
      }

   }

   public List<String> names() {
      return new ArrayList(this.filters.keySet());
   }

   public boolean matches(String filterName, Material material) {
      if (filterName != null && !filterName.equalsIgnoreCase("All")) {
         Set<Material> set = (Set)this.filters.get(filterName);
         return set != null && !set.isEmpty() ? set.contains(material) : true;
      } else {
         return true;
      }
   }
}
