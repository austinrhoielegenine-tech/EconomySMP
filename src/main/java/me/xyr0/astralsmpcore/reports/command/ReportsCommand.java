package me.xyr0.astralsmpcore.reports.command;

import me.xyr0.astralsmpcore.reports.AstralReports;
import me.xyr0.astralsmpcore.reports.gui.ReportsGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportsCommand implements CommandExecutor {
   private final AstralReports plugin;

   public ReportsCommand(AstralReports plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player player) {
         if (!player.hasPermission("astralsmpcore.system.reports.staff")) {
            player.sendMessage(this.plugin.getConfig().getString("messages.no-permission").replace("&", "§"));
            return true;
         } else {
            ReportsGUI.open(player, this.plugin);
            return true;
         }
      } else {
         return true;
      }
   }
}
