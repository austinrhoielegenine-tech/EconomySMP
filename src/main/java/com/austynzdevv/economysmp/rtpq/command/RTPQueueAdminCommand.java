package com.austynzdevv.economysmp.rtpq.command;

import com.austynzdevv.economysmp.rtpq.EconomyRTPQ;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RTPQueueAdminCommand implements CommandExecutor {
   private final EconomyRTPQ plugin;

   public RTPQueueAdminCommand(EconomyRTPQ plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission(this.plugin.getConfig().getString("permissions.admin", "EconomyRTPQ.admin"))) {
         sender.sendMessage("§cNo permission.");
         return true;
      } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
         this.plugin.reloadConfig();
         sender.sendMessage("§aEconomyRTPQ configuration reloaded.");
         return true;
      } else {
         sender.sendMessage("§7Use /economyrtpq reload");
         return true;
      }
   }
}
