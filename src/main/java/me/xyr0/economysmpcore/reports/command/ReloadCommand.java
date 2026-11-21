package me.xyr0.economysmpcore.reports.command;

import me.xyr0.economysmpcore.reports.AstralReports;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
   private final AstralReports plugin;

   public ReloadCommand(AstralReports plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("economysmpcore.system.reports.staff")) {
         sender.sendMessage("§cYou do not have permission.");
         return true;
      } else {
         this.plugin.reloadConfig();
         sender.sendMessage("§aAstralReports configuration reloaded.");
         return true;
      }
   }
}
