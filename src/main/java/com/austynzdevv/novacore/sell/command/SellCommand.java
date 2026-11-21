package com.austynzdevv.novacore.sell.command;

import com.austynzdevv.novacore.sell.EconomySell;
import com.austynzdevv.novacore.sell.gui.SellMenus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SellCommand implements CommandExecutor {
   private final EconomySell plugin;

   public SellCommand(EconomySell plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player player) {
         if (!player.hasPermission("novacorecore.system.sell.use")) {
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
