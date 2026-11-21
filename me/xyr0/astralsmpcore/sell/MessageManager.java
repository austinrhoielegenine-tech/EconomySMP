package me.xyr0.astralsmpcore.sell;

import java.io.File;
import java.util.Map;
import me.xyr0.astralsmpcore.util.ColorUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MessageManager {
   private final AstralSell plugin;
   private FileConfiguration messages;
   private boolean prefixEnabled;
   private String prefix;

   public MessageManager(AstralSell plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      File file = new File(this.plugin.getDataFolder(), "messages.yml");
      if (!file.exists()) {
         this.plugin.saveResource("messages.yml", false);
      }

      this.messages = YamlConfiguration.loadConfiguration(file);
      this.prefixEnabled = this.messages.getBoolean("prefix-enable", false);
      this.prefix = this.messages.getString("prefix", "");
      if (this.prefix == null) {
         this.prefix = "";
      }

   }

   public void send(CommandSender sender, String key) {
      this.send(sender, key, Map.of());
   }

   public void send(CommandSender sender, String key, Map<String, String> ph) {
      String raw = this.messages.getString("messages." + key, key);
      String var10000 = this.prefixEnabled ? this.prefix : "";
      String out = var10000 + raw;

      for(Map.Entry<String, String> e : ph.entrySet()) {
         out = out.replace("%" + (String)e.getKey() + "%", (CharSequence)(e.getValue() == null ? "" : (CharSequence)e.getValue()));
      }

      sender.sendMessage(ColorUtil.parse(out));
   }

   public String raw(String key, String def) {
      String v = this.messages.getString("messages." + key);
      return v != null && !v.isEmpty() ? v : def;
   }
}
