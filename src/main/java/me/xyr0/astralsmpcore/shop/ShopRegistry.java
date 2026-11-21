package me.xyr0.astralsmpcore.shop;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class ShopRegistry {
   private final AstralShop plugin;
   private final Map<String, FileConfiguration> shops = new LinkedHashMap();
   private final Map<String, File> shopFiles = new LinkedHashMap();

   public ShopRegistry(AstralShop plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      this.shops.clear();
      this.shopFiles.clear();
      this.saveDefaults();
      File guiMain = new File(this.plugin.getDataFolder(), "gui/main.yml");
      if (guiMain.exists()) {
         this.shops.put("main", YamlConfiguration.loadConfiguration(guiMain));
         this.shopFiles.put("main", guiMain);
      }

      File catDir = new File(this.plugin.getDataFolder(), "categories");
      if (catDir.isDirectory()) {
         File[] files = catDir.listFiles((dir, name) -> name.endsWith(".yml"));
         if (files != null) {
            for(File file : files) {
               String id = file.getName().substring(0, file.getName().length() - 4).toLowerCase();
               this.shops.put(id, YamlConfiguration.loadConfiguration(file));
               this.shopFiles.put(id, file);
            }
         }
      }

   }

   private void saveDefaults() {
      File guiDir = new File(this.plugin.getDataFolder(), "gui");
      if (!guiDir.exists()) {
         guiDir.mkdirs();
      }

      File main = new File(guiDir, "main.yml");
      if (!main.exists()) {
         this.plugin.saveResource("gui/main.yml", false);
      }

      File catDir = new File(this.plugin.getDataFolder(), "categories");
      if (!catDir.exists()) {
         catDir.mkdirs();
      }

      String[] defaults = new String[]{"end.yml", "nether.yml", "gear.yml", "food.yml", "redstone.yml", "shards.yml"};

      for(String name : defaults) {
         File out = new File(catDir, name);
         if (!out.exists()) {
            this.plugin.saveResource("categories/" + name, false);
         }
      }

      File messages = new File(this.plugin.getDataFolder(), "messages.yml");
      if (!messages.exists()) {
         this.plugin.saveResource("messages.yml", false);
      }

   }

   public FileConfiguration getShop(String id) {
      return id == null ? null : (FileConfiguration)this.shops.get(id.toLowerCase());
   }

   public boolean hasShop(String id) {
      return id != null && this.shops.containsKey(id.toLowerCase());
   }

   public Set<String> getShopIds() {
      return Collections.unmodifiableSet(this.shops.keySet());
   }

   public FileConfiguration createCategory(String id, int size) {
      id = id.toLowerCase();
      File catDir = new File(this.plugin.getDataFolder(), "categories");
      if (!catDir.exists()) {
         catDir.mkdirs();
      }

      File file = new File(catDir, id + ".yml");
      FileConfiguration cfg = new YamlConfiguration();
      cfg.set("title", "&8" + id);
      cfg.set("size", size);
      cfg.set("type", "shop");
      cfg.set("economy", "vault");
      cfg.set("back-slot", Math.max(0, size - 9));
      cfg.set("back-open", "main");
      cfg.set("pagination.next-slot", Math.max(0, size - 1));
      cfg.set("pagination.previous-slot", Math.max(0, size - 2));

      try {
         cfg.save(file);
      } catch (IOException var7) {
         this.plugin.getLogger().warning("Could not create category " + id);
      }

      this.shops.put(id, cfg);
      this.shopFiles.put(id, file);
      return cfg;
   }

   public void saveShop(String id) {
      id = id.toLowerCase();
      FileConfiguration cfg = (FileConfiguration)this.shops.get(id);
      File file = (File)this.shopFiles.get(id);
      if (cfg != null && file != null) {
         try {
            cfg.save(file);
         } catch (IOException var5) {
            this.plugin.getLogger().warning("Could not save shop " + id);
         }

      }
   }
}
