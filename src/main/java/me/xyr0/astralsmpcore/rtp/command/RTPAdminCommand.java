package me.xyr0.astralsmpcore.rtp.command;

import me.xyr0.astralsmpcore.rtp.AstralRTP;
import me.xyr0.astralsmpcore.rtp.util.MessageUtil;
import me.xyr0.astralsmpcore.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RTPAdminCommand implements CommandExecutor {
   private final AstralRTP plugin;

   public RTPAdminCommand(AstralRTP plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("astralsmpcore.system.rtp.admin")) {
         MessageUtil.send(sender, "no-permission");
         return true;
      } else if (args.length != 0 && args[0].equalsIgnoreCase("reload")) {
         this.plugin.reloadEverything();
         MessageUtil.send(sender, "reload.success");
         return true;
      } else {
         sender.sendMessage(ColorUtil.color("&7Usage: &#37BFF9/astralrtp reload"));
         return true;
      }
   }
}
