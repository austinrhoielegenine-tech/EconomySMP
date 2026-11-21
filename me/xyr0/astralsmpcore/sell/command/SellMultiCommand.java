package me.xyr0.astralsmpcore.sell.command;

import me.xyr0.astralsmpcore.sell.AstralSell;
import me.xyr0.astralsmpcore.sell.gui.SellMenus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SellMultiCommand implements CommandExecutor {
   private final AstralSell plugin;

   public SellMultiCommand(AstralSell plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player player) {
         if (!player.hasPermission("astralsmpcore.system.sell.use")) {
            this.plugin.getMessages().send(player, "no-permission");
            return true;
         } else {
            SellMenus.openSellMulti(player);
            return true;
         }
      } else {
         this.plugin.getMessages().send(sender, "player-only");
         return true;
      }
   }
}
