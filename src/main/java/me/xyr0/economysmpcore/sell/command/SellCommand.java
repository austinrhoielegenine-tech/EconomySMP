package me.xyr0.economysmpcore.sell.command;

import me.xyr0.economysmpcore.sell.AstralSell;
import me.xyr0.economysmpcore.sell.gui.SellMenus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SellCommand implements CommandExecutor {
   private final AstralSell plugin;

   public SellCommand(AstralSell plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player player) {
         if (!player.hasPermission("economysmpcore.system.sell.use")) {
            this.plugin.getMessages().send(player, "no-permission");
            return true;
         } else {
            SellMenus.openSell(player);
            return true;
         }
      } else {
         this.plugin.getMessages().send(sender, "player-only");
         return true;
      }
   }
}
