package com.austynzdevv.economysmp.order.command;

import java.util.Collections;
import java.util.List;
import com.austynzdevv.economysmp.order.EconomyOrder;
import com.austynzdevv.economysmp.order.gui.OrderMenus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class OrdersCommand implements CommandExecutor, TabCompleter {
   private final EconomyOrder plugin;

   public OrdersCommand(EconomyOrder plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player player) {
         if (!player.hasPermission("economysmpcore.system.order.use")) {
            this.plugin.getMessages().send(player, "no-permission");
            return true;
         } else if (!this.plugin.getEconomy().isReady()) {
            this.plugin.getMessages().send(player, "no-money");
            return true;
         } else {
            OrderMenus.openBrowse(player, 0);
            return true;
         }
      } else {
         this.plugin.getMessages().send(sender, "only-players");
         return true;
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      return Collections.emptyList();
   }
}
