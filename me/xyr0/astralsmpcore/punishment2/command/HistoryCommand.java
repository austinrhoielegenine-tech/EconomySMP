package me.xyr0.astralsmpcore.punishment2.command;

import me.xyr0.astralsmpcore.punishment2.AstralPunishment;
import me.xyr0.astralsmpcore.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HistoryCommand implements CommandExecutor {
   private final AstralPunishment plugin;

   public HistoryCommand(AstralPunishment plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player player) {
         if (!player.hasPermission("astralsmpcore.system.punishment.history")) {
            player.sendMessage(ColorUtil.color("&cYou do not have permission."));
            return true;
         } else if (args.length != 1) {
            player.sendMessage(ColorUtil.color("&cUsage: /history <player>"));
            return true;
         } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target.getUniqueId() == null) {
               player.sendMessage(ColorUtil.color("&cPlayer not found."));
               return true;
            } else {
               this.plugin.getHistoryManager().openHistory(player, target, 0);
               return true;
            }
         }
      } else {
         sender.sendMessage("Players only.");
         return true;
      }
   }
}
