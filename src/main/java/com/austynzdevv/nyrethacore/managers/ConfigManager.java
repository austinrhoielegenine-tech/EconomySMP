package com.austynzdevv.nyrethacore.managers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.austynzdevv.nyrethacore.NyrethaCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {
   private final NyrethaCore plugin;
   private final Map<String, FileConfiguration> systemConfigs = new HashMap();

   public ConfigManager(NyrethaCore plugin) {
      this.plugin = plugin;
      this.load();
   }

   public void load() {
      File main = new File(this.plugin.getDataFolder(), "main.config.yml");
      if (!main.exists()) {
         try {
            this.plugin.saveResource("main.config.yml", false);
         } catch (Exception var5) {
         }
      }

      if (main.exists()) {
         FileConfiguration cfg = YamlConfiguration.loadConfiguration(main);

         for(String key : cfg.getKeys(true)) {
            this.plugin.getConfig().set(key, cfg.get(key));
         }
      }

      this.loadFlatConfigs();
      this.reload();
   }

   private void loadFlatConfigs() {
      this.systemConfigs.clear();
      String[] flat = new String[]{"chainmail", "combat", "doublejump", "enderchest", "help", "hide", "keyall", "mobs", "media", "rules", "moderation", "reports", "safety", "scoreboard", "settings", "welcomer", "wl"};

      for(String name : flat) {
         File f = new File(this.plugin.getDataFolder(), name + ".yml");
         if (f.exists()) {
            this.systemConfigs.put(name, YamlConfiguration.loadConfiguration(f));
            if (name.equals("enderchest")) {
               this.systemConfigs.put("ec", (FileConfiguration)this.systemConfigs.get(name));
            }

            if (name.equals("wl")) {
               this.systemConfigs.put("whitelist", (FileConfiguration)this.systemConfigs.get(name));
            }
         }
      }

   }

   public void reload() {
      File main = new File(this.plugin.getDataFolder(), "main.config.yml");
      if (main.exists()) {
         FileConfiguration cfg = YamlConfiguration.loadConfiguration(main);

         for(String key : cfg.getKeys(true)) {
            this.plugin.getConfig().set(key, cfg.get(key));
         }
      }

      this.loadFlatConfigs();
      File data = this.plugin.getDataFolder();
      File[] dirs = data.listFiles(File::isDirectory);
      if (dirs != null) {
         for(File dir : dirs) {
            File cfg = new File(dir, "config.yml");
            if (cfg.exists()) {
               this.systemConfigs.put(dir.getName().toLowerCase(), YamlConfiguration.loadConfiguration(cfg));
            }
         }
      }

   }

   public boolean isEnabled(String system) {
      String key = system.toLowerCase().replace("_", "-");
      if (key.equals("rtpq") || key.equals("rtp-queue") || key.equals("rtpzone") || key.equals("rtp-zone")) {
         key = "rtp";
      }

      if (key.equals("echest") || key.equals("ec")) {
         key = "enderchest";
      }

      if (key.equals("wl")) {
         key = "whitelist";
      }

      if ((key.equals("links") || key.equals("voicechat") || key.equals("nightvision") || key.equals("tab")) && !this.plugin.getConfig().contains("systems." + key)) {
         return true;
      } else {
         return !key.equals("nightvision") && !key.equals("tab") ? this.plugin.getConfig().getBoolean("systems." + key, false) : this.plugin.getConfig().getBoolean("systems." + key, true);
      }
   }

   public FileConfiguration getSystemConfig(String system) {
      return (FileConfiguration)this.systemConfigs.get(system.toLowerCase());
   }

   public FileConfiguration getMain() {
      return this.plugin.getConfig();
   }

   public void setEnabled(String system, boolean enabled) {
      String key = system.toLowerCase().replace("_", "-");
      this.plugin.getConfig().set("systems." + key, enabled);

      try {
         this.plugin.getConfig().save(new File(this.plugin.getDataFolder(), "main.config.yml"));
      } catch (IOException e) {
         this.plugin.getLogger().warning("Failed to save main.config.yml: " + e.getMessage());
      }

   }
}
