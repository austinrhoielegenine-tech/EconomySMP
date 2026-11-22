package com.austynzdevv.nyrethacore.hide.command;

import com.austynzdevv.nyrethacore.hide.EconomyHide;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
   private final EconomyHide plugin;

   public ReloadCommand(EconomyHide plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("EconomyHide.reload")) {
         sender.sendMessage(ColorUtil.color("&cYou don't have permission."));
         return true;
      } else {
         this.plugin.reloadConfig();
         sender.sendMessage(ColorUtil.color("&7EconomyHide configuration reloaded."));
         return true;
      }
   }
}
