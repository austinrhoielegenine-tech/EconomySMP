package me.xyr0.astralsmpcore.maintenance.util;

import me.xyr0.astralsmpcore.maintenance.astralMaintenance;
import me.xyr0.astralsmpcore.util.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class MessageUtil {
   private final astralMaintenance plugin;

   public MessageUtil(astralMaintenance plugin) {
      this.plugin = plugin;
   }

   public String color(String message) {
      return TextUtil.color(message);
   }

   public String get(String path) {
      FileConfiguration msg = this.plugin.getMessages();
      String prefix = msg.getString("prefix", "");
      String message = msg.getString(path, "");
      return this.color(prefix + message);
   }

   public String get(String path, String player) {
      return this.get(path).replace("%player%", player);
   }

   public void send(CommandSender sender, String path) {
      String msg = this.get(path);
      if (msg == null || msg.isBlank()) {
         String var10000;
         switch (path) {
            case "enabled" -> var10000 = this.color("&aMaintenance mode enabled.");
            case "disabled" -> var10000 = this.color("&cMaintenance mode disabled.");
            case "already-enabled" -> var10000 = this.color("&cMaintenance is already enabled.");
            case "already-disabled" -> var10000 = this.color("&cMaintenance is already disabled.");
            case "no-permission" -> var10000 = this.color("&cNo permission.");
            case "reload" -> var10000 = this.color("&aMaintenance reloaded.");
            default -> var10000 = this.color("&7" + path);
         }

         msg = var10000;
      }

      sender.sendMessage(msg);
   }

   public void send(CommandSender sender, String path, String player) {
      String msg = this.get(path, player);
      if (msg == null || msg.isBlank()) {
         msg = this.color("&7" + path + " &f" + player);
      }

      sender.sendMessage(msg);
   }
}
