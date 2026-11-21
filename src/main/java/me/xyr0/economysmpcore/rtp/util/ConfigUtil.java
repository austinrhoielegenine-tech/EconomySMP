package me.xyr0.economysmpcore.rtp.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import me.xyr0.economysmpcore.rtp.AstralRTP;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class ConfigUtil {
   private static final Map<String, FileConfiguration> cache = new HashMap();

   private ConfigUtil() {
   }

   public static FileConfiguration getFile(AstralRTP plugin, String name) {
      FileConfiguration cached = (FileConfiguration)cache.get(name);
      if (cached != null) {
         return cached;
      } else {
         File file = new File(plugin.getDataFolder(), name);
         if (!file.exists()) {
            plugin.saveResource(name, false);
         }

         FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
         cache.put(name, cfg);
         return cfg;
      }
   }

   public static void reload(AstralRTP plugin, String name) {
      cache.remove(name);
      getFile(plugin, name);
   }

   public static void reloadAll(AstralRTP plugin) {
      cache.clear();
      getFile(plugin, "messages.yml");
      getFile(plugin, "guis.yml");
      LocationUtil.reload(plugin);
      MessageUtil.reload(plugin);
   }
}
