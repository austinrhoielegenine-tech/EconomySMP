package com.austynzdevv.nyrethacore.mobs;

import com.austynzdevv.nyrethacore.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EconomyMobsCommand implements CommandExecutor {
   private final EconomyMobs plugin;

   public EconomyMobsCommand(EconomyMobs plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("EconomyMobs.admin")) {
         sender.sendMessage(ColorUtil.color(this.plugin.getConfig().getString("messages.no-permission")));
         return true;
      } else if (args.length == 0) {
         sender.sendMessage(ColorUtil.color("&7Usage: &f/economymobs reload"));
         return true;
      } else if (args[0].equalsIgnoreCase("reload")) {
         this.plugin.reloadPlugin();
         sender.sendMessage(ColorUtil.color(this.plugin.getConfig().getString("messages.reload")));
         return true;
      } else {
         sender.sendMessage(ColorUtil.color("&cUnknown command."));
         return true;
      }
   }
}
