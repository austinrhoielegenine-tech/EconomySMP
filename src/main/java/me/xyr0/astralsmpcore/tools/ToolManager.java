package me.xyr0.astralsmpcore.tools;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.configuration.file.YamlConfiguration;

public class ToolManager {
   private final AstralTools plugin;
   private final Map<String, ToolDefinition> tools = new LinkedHashMap();

   public ToolManager(AstralTools plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      this.tools.clear();
      File folder = new File(this.plugin.getDataFolder(), "tools");
      if (!folder.exists()) {
         folder.mkdirs();

         for(String id : new String[]{"drill", "treechopper", "shovel", "hoe", "enderpearl", "rocket", "waterbucket", "lavabucket", "bucket", "jump"}) {
            this.plugin.saveResource("tools/" + id + ".yml", false);
         }
      }

      File[] files = folder.listFiles((d, n) -> n.endsWith(".yml"));
      if (files != null) {
         for(File file : files) {
            String id = file.getName().replace(".yml", "").toLowerCase();
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            this.tools.put(id, new ToolDefinition(id, cfg));
         }

      }
   }

   public ToolDefinition get(String id) {
      return id == null ? null : (ToolDefinition)this.tools.get(id.toLowerCase());
   }

   public Collection<ToolDefinition> all() {
      return this.tools.values();
   }

   public String listIds() {
      return String.join(", ", this.tools.keySet());
   }
}
