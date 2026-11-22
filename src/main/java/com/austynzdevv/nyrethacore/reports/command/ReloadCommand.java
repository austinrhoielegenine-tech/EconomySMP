package com.austynzdevv.nyrethacore.reports.command;

import com.austynzdevv.nyrethacore.reports.EconomyReports;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
   private final EconomyReports plugin;

   public ReloadCommand(EconomyReports plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("nyrethacorecore.system.reports.staff")) {
         sender.sendMessage("§cYou do not have permission.");
         return true;
      } else {
         this.plugin.reloadConfig();
         sender.sendMessage("§aEconomyReports configuration reloaded.");
         return true;
      }
   }
}
