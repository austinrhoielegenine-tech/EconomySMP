package com.austynzdevv.economysmp.teams;

import java.io.File;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class MessageManager {
   private final EconomyTeams plugin;
   private FileConfiguration messages;

   public MessageManager(EconomyTeams plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      File file = new File(this.plugin.getDataFolder(), "messages.yml");
      if (!file.exists()) {
         this.plugin.saveResource("messages.yml", false);
      }

      this.messages = YamlConfiguration.loadConfiguration(file);
   }

   public String get(String key, String def) {
      return this.messages.getString(key, def);
   }

   public String get(String key) {
      return this.messages.getString(key, key);
   }

   public List<String> getList(String key) {
      return this.messages.getStringList(key);
   }
}
