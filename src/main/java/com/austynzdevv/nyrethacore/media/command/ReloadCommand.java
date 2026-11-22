package com.austynzdevv.nyrethacore.media.command;

import com.austynzdevv.nyrethacore.media.EconomyMedia;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("nyrethacorecore.system.admin")) {
         sender.sendMessage(ColorUtil.color(EconomyMedia.getInstance().getConfig().getString("messages.no-permission")));
         return true;
      } else if (args.length != 0 && args[0].equalsIgnoreCase("reload")) {
         EconomyMedia.getInstance().reloadConfig();
         sender.sendMessage(ColorUtil.color(EconomyMedia.getInstance().getConfig().getString("messages.reload")));
         return true;
      } else {
         sender.sendMessage(ColorUtil.color("&cUsage: /economymedia reload"));
         return true;
      }
   }
}
