package me.xyr0.astralsmpcore.rtpq.command;

import me.xyr0.astralsmpcore.rtpq.AstralRTPQ;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RTPQueueAdminCommand implements CommandExecutor {
   private final AstralRTPQ plugin;

   public RTPQueueAdminCommand(AstralRTPQ plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission(this.plugin.getConfig().getString("permissions.admin", "AstralRTPQ.admin"))) {
         sender.sendMessage("§cNo permission.");
         return true;
      } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
         this.plugin.reloadConfig();
         sender.sendMessage("§aAstralRTPQ configuration reloaded.");
         return true;
      } else {
         sender.sendMessage("§7Use /astralrtpq reload");
         return true;
      }
   }
}
