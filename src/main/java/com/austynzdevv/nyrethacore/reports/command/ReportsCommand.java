package com.austynzdevv.nyrethacore.reports.command;

import com.austynzdevv.nyrethacore.reports.EconomyReports;
import com.austynzdevv.nyrethacore.reports.gui.ReportsGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportsCommand implements CommandExecutor {
   private final EconomyReports plugin;

   public ReportsCommand(EconomyReports plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player player) {
         if (!player.hasPermission("nyrethacorecore.system.reports.staff")) {
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
