package com.austynzdevv.nyrethacore.rtp.command;

import com.austynzdevv.nyrethacore.rtp.EconomyRTP;
import com.austynzdevv.nyrethacore.rtp.util.MessageUtil;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RTPAdminCommand implements CommandExecutor {
   private final EconomyRTP plugin;

   public RTPAdminCommand(EconomyRTP plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("nyrethacorecore.system.rtp.admin")) {
         MessageUtil.send(sender, "no-permission");
         return true;
      } else if (args.length != 0 && args[0].equalsIgnoreCase("reload")) {
         this.plugin.reloadEverything();
         MessageUtil.send(sender, "reload.success");
         return true;
      } else {
         sender.sendMessage(ColorUtil.color("&7Usage: &#37BFF9/economyrtp reload"));
         return true;
      }
   }
}
