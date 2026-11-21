package com.austynzdevv.economysmp.order;

import java.io.File;
import java.util.Map;
import com.austynzdevv.economysmp.util.ColorUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MessageManager {
   private final EconomyOrder plugin;
   private FileConfiguration messages;
   private boolean prefixEnabled;
   private String prefix;

   public MessageManager(EconomyOrder plugin) {
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

   public void send(CommandSender sender, String key, Map<String, String> placeholders) {
      String raw = this.resolve(key);
      if (raw == null) {
         raw = key;
      }

      String var10000 = this.prefixEnabled ? this.prefix : "";
      String out = var10000 + raw;

      for(Map.Entry<String, String> e : placeholders.entrySet()) {
         out = out.replace("%" + (String)e.getKey() + "%", (CharSequence)(e.getValue() == null ? "" : (CharSequence)e.getValue()));
      }

      sender.sendMessage(ColorUtil.parse(out));
   }

   public String get(String key) {
      String raw = this.resolve(key);
      return raw == null ? key : raw;
   }

   private String resolve(String key) {
      return this.messages.contains("messages." + key) ? this.messages.getString("messages." + key) : this.messages.getString(key);
   }
}
