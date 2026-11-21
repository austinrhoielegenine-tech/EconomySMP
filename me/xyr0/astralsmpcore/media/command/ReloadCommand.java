package me.xyr0.astralsmpcore.media.command;

import me.xyr0.astralsmpcore.media.AstralMedia;
import me.xyr0.astralsmpcore.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("astralsmpcore.system.admin")) {
         sender.sendMessage(ColorUtil.color(AstralMedia.getInstance().getConfig().getString("messages.no-permission")));
         return true;
      } else if (args.length != 0 && args[0].equalsIgnoreCase("reload")) {
         AstralMedia.getInstance().reloadConfig();
         sender.sendMessage(ColorUtil.color(AstralMedia.getInstance().getConfig().getString("messages.reload")));
         return true;
      } else {
         sender.sendMessage(ColorUtil.color("&cUsage: /astralmedia reload"));
         return true;
      }
   }
}
