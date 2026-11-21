package com.austynzdevv.economysmp.sell.command;

import java.util.UUID;
import com.austynzdevv.economysmp.sell.EconomySell;
import com.austynzdevv.economysmp.sell.gui.SellMenus;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HistoryCommand implements CommandExecutor {
   private final EconomySell plugin;

   public HistoryCommand(EconomySell plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player player) {
         if (!player.hasPermission("economysmpcore.system.sell.use")) {
            this.plugin.getMessages().send(player, "no-permission");
            return true;
         } else {
            UUID target = player.getUniqueId();
            if (args.length >= 1) {
               if (!player.hasPermission("economysmpcore.system.sell.admin")) {
                  this.plugin.getMessages().send(player, "no-permission");
                  return true;
               }

               OfflinePlayer off = Bukkit.getOfflinePlayer(args[0]);
               if (off.getName() == null && !off.hasPlayedBefore()) {
                  this.plugin.getMessages().send(player, "player-not-found");
                  return true;
               }

               target = off.getUniqueId();
            }

            if (this.plugin.getHistory().get(target).isEmpty()) {
               this.plugin.getMessages().send(player, "history-empty");
               return true;
            } else {
               SellMenus.openHistory(player, target, 0);
               return true;
            }
         }
      } else {
         this.plugin.getMessages().send(sender, "player-only");
         return true;
      }
   }
}
