package me.xyr0.economysmpcore.rules;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
   private final AstralRules plugin;
   private FileConfiguration config;

   public ConfigManager(AstralRules plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      this.plugin.reloadConfig();
      this.config = this.plugin.getConfig();
   }

   public FileConfiguration getConfig() {
      return this.config;
   }

   public String getGuiTitle() {
      return this.config.getString("gui.title", "&8EconomySMP Rules");
   }

   public int getGuiSize() {
      return this.config.getInt("gui.size", 27);
   }
}
