package me.xyr0.astralsmpcore.baltop.command;

import java.util.ArrayList;
import java.util.List;
import me.xyr0.astralsmpcore.baltop.AstralBaltop;
import me.xyr0.astralsmpcore.baltop.gui.BaltopMenus;
import me.xyr0.astralsmpcore.baltop.gui.MenuSession;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class BaltopCommand implements CommandExecutor, TabCompleter {
   private final AstralBaltop plugin;

   public BaltopCommand(AstralBaltop plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
         if (!sender.hasPermission("astralsmpcore.system.baltop.reload") && !sender.hasPermission("astralsmpcore.system.baltop.admin")) {
            this.plugin.getMessages().send(sender, "no-permissions");
            return true;
         } else {
            this.plugin.reloadAll();
            this.plugin.getMessages().send(sender, "reloaded");
            return true;
         }
      } else if (sender instanceof Player) {
         Player player = (Player)sender;
         if (!player.hasPermission("astralsmpcore.system.baltop.use")) {
            this.plugin.getMessages().send(player, "no-permissions");
            return true;
         } else {
            MenuSession.SEARCH.remove(player.getUniqueId());
            this.plugin.getCache().updatePlayer(player);
            BaltopMenus.open(player, 0);
            return true;
         }
      } else {
         sender.sendMessage("Players only.");
         return true;
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      if (args.length == 1 && sender.hasPermission("astralsmpcore.system.baltop.reload")) {
         List<String> out = new ArrayList();
         if ("reload".startsWith(args[0].toLowerCase())) {
            out.add("reload");
         }

         return out;
      } else {
         return List.of();
      }
   }
}
