package me.xyr0.astralsmpcore.media;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
   private final AstralMedia plugin;

   public ConfigManager(AstralMedia plugin) {
      this.plugin = plugin;
   }

   public FileConfiguration getConfig() {
      return this.plugin.getConfig();
   }

   public void reload() {
      this.plugin.reloadConfig();
   }
}
