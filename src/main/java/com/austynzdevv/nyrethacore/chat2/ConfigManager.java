package com.austynzdevv.nyrethacore.chat2;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {
   private final EconomyChat plugin;
   private File messagesFile;
   private File modulesFile;
   private FileConfiguration messages;
   private FileConfiguration modules;

   public ConfigManager(EconomyChat plugin) {
      this.plugin = plugin;
   }

   public void loadConfigs() {
      this.plugin.saveDefaultConfig();
      this.messagesFile = new File(this.plugin.getDataFolder(), "messages.yml");
      this.modulesFile = new File(this.plugin.getDataFolder(), "modules.yml");
      if (!this.messagesFile.exists()) {
         this.plugin.saveResource("messages.yml", false);
      }

      if (!this.modulesFile.exists()) {
         this.plugin.saveResource("modules.yml", false);
      }

      this.messages = YamlConfiguration.loadConfiguration(this.messagesFile);
      this.modules = YamlConfiguration.loadConfiguration(this.modulesFile);
   }

   public FileConfiguration getMessages() {
      return this.messages;
   }

   public FileConfiguration getModules() {
      return this.modules;
   }

   public boolean isModuleEnabled(String module) {
      return this.modules.getBoolean("modules." + module, true);
   }

   public String getMessage(String path) {
      return this.messages.getString(path, "&cMessage missing: " + path);
   }
}
