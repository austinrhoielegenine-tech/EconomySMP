package com.austynzdevv.economysmp.media;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
   private final EconomyMedia plugin;

   public ConfigManager(EconomyMedia plugin) {
      this.plugin = plugin;
   }

   public FileConfiguration getConfig() {
      return this.plugin.getConfig();
   }

   public void reload() {
      this.plugin.reloadConfig();
   }
}
