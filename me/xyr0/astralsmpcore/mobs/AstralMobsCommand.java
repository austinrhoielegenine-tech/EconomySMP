package me.xyr0.astralsmpcore.mobs;

import me.xyr0.astralsmpcore.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AstralMobsCommand implements CommandExecutor {
   private final AstralMobs plugin;

   public AstralMobsCommand(AstralMobs plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("AstralMobs.admin")) {
         sender.sendMessage(ColorUtil.color(this.plugin.getConfig().getString("messages.no-permission")));
         return true;
      } else if (args.length == 0) {
         sender.sendMessage(ColorUtil.color("&7Usage: &f/astralmobs reload"));
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
