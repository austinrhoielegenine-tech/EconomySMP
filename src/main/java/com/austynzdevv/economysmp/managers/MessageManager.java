package com.austynzdevv.economysmp.managers;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import com.austynzdevv.economysmp.EconomySMP;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MessageManager {
   private final EconomySMP plugin;
   private FileConfiguration messages;

   public MessageManager(EconomySMP plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      File file = new File(this.plugin.getDataFolder(), "messages.yml");
      if (!file.exists()) {
         file.getParentFile().mkdirs();

         try {
            if (this.plugin.getResource("messages.yml") != null) {
               this.plugin.saveResource("messages.yml", false);
            } else {
               this.writeDefaults(file);
            }
         } catch (Exception var3) {
            this.writeDefaults(file);
         }
      }

      this.messages = YamlConfiguration.loadConfiguration(file);
   }

   private void writeDefaults(File file) {
      try {
         file.getParentFile().mkdirs();
         String defaults = "prefix: \"&8[&bEconomy&fSMP&8] &r\"\ngeneral:\n  no-permission: \"%prefix%&cYou do not have permission.\"\n  invalid-usage: \"%prefix%&cInvalid usage.\"\n  reload-success: \"%prefix%&aReloaded successfully.\"\n  system-not-found: \"%prefix%&cSystem not found.\"\n  system-enabled: \"%prefix%&aEnabled system: &f%system%\"\n  system-disabled: \"%prefix%&cDisabled system: &f%system%\"\n  system-already-enabled: \"%prefix%&eThat system is already enabled.\"\n  system-already-disabled: \"%prefix%&eThat system is already disabled.\"\n  players-only: \"%prefix%&cPlayers only.\"\n";
         Files.writeString(file.toPath(), defaults);
      } catch (Exception var3) {
         this.messages = new YamlConfiguration();
      }

   }

   public String get(String path) {
      if (this.messages == null) {
         return path;
      } else {
         String msg = this.messages.getString(path, path);
         String prefix = this.messages.getString("prefix", "");
         return this.color(msg.replace("%prefix%", prefix));
      }
   }

   public String get(String path, Map<String, String> placeholders) {
      String msg = this.get(path);
      if (placeholders != null) {
         for(Map.Entry<String, String> e : placeholders.entrySet()) {
            msg = msg.replace("%" + (String)e.getKey() + "%", (CharSequence)(e.getValue() == null ? "" : (CharSequence)e.getValue()));
         }
      }

      return msg;
   }

   public void send(CommandSender sender, String path) {
      sender.sendMessage(this.get(path));
   }

   public void send(CommandSender sender, String path, Map<String, String> placeholders) {
      sender.sendMessage(this.get(path, placeholders));
   }

   private String color(String input) {
      return input == null ? "" : ChatColor.translateAlternateColorCodes('&', input);
   }
}
