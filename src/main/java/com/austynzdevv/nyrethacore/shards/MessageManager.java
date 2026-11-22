package com.austynzdevv.nyrethacore.shards;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class MessageManager {
   private final EconomyShards plugin;
   private FileConfiguration messages;

   public MessageManager(EconomyShards plugin) {
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

   public String prefix() {
      return !this.messages.getBoolean("prefix-enable", false) ? "" : this.messages.getString("prefix", "");
   }

   public String withPrefix(String key, String def) {
      String var10000 = this.prefix();
      return var10000 + this.get(key, def);
   }
}
