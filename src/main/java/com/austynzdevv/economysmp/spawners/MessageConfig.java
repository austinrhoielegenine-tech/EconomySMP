package com.austynzdevv.economysmp.spawners;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MessageConfig {
   private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder().hexColors().character('&').build();
   private final EconomySpawners plugin;
   private final Map<String, String> messages = new HashMap();
   private String prefix = "";

   public MessageConfig(EconomySpawners plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      this.messages.clear();
      File file = new File(this.plugin.getDataFolder(), "messages.yml");
      if (!file.exists()) {
         this.plugin.saveResource("messages.yml", false);
      }

      FileConfiguration config = YamlConfiguration.loadConfiguration(file);

      for(String key : config.getKeys(false)) {
         this.messages.put(key, config.getString(key, ""));
      }

      this.prefix = (String)this.messages.getOrDefault("prefix", "");
   }

   public String raw(String key) {
      return ((String)this.messages.getOrDefault(key, key)).replace("%prefix%", this.prefix);
   }

   public Component component(String key) {
      return LEGACY.deserialize(this.raw(key));
   }

   public Component component(String key, Map<String, String> placeholders) {
      String text = this.raw(key);

      for(Map.Entry<String, String> e : placeholders.entrySet()) {
         text = text.replace("%" + (String)e.getKey() + "%", (CharSequence)e.getValue());
      }

      return LEGACY.deserialize(text);
   }

   public void send(CommandSender sender, String key) {
      sender.sendMessage(this.component(key));
   }

   public void send(CommandSender sender, String key, Map<String, String> placeholders) {
      sender.sendMessage(this.component(key, placeholders));
   }

   public String color(String input) {
      return input.replace('&', '§');
   }
}
