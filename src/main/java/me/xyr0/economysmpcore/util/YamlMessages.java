package me.xyr0.economysmpcore.util;

import java.io.File;
import me.xyr0.economysmpcore.SystemHost;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class YamlMessages {
   private final SystemHost plugin;
   private FileConfiguration messages;

   public YamlMessages(SystemHost plugin) {
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

   public String get(String key) {
      return this.messages.getString(key, key);
   }

   public String get(String key, String fallback) {
      return this.messages.getString(key, fallback);
   }
}
