package me.xyr0.economysmpcore.hide.command;

import me.xyr0.economysmpcore.hide.AstralHide;
import me.xyr0.economysmpcore.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
   private final AstralHide plugin;

   public ReloadCommand(AstralHide plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("AstralHide.reload")) {
         sender.sendMessage(ColorUtil.color("&cYou don't have permission."));
         return true;
      } else {
         this.plugin.reloadConfig();
         sender.sendMessage(ColorUtil.color("&7AstralHide configuration reloaded."));
         return true;
      }
   }
}
