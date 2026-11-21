package com.austynzdevv.economysmp.punishment2.command;

import com.austynzdevv.economysmp.punishment2.EconomyPunishment;
import com.austynzdevv.economysmp.punishment2.Punishment;
import com.austynzdevv.economysmp.punishment2.util.TimeUtil;
import com.austynzdevv.economysmp.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CheckMuteCommand implements CommandExecutor {
   private final EconomyPunishment plugin;

   public CheckMuteCommand(EconomyPunishment plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("economysmpcore.system.punishment.checkmute")) {
         sender.sendMessage(ColorUtil.color("&cYou do not have permission."));
         return true;
      } else if (args.length != 1) {
         sender.sendMessage(ColorUtil.color("&cUsage: /checkmute <player>"));
         return true;
      } else {
         OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
         if (target.getUniqueId() == null) {
            sender.sendMessage(ColorUtil.color("&cPlayer not found."));
            return true;
         } else {
            Punishment punishment = this.plugin.getDatabaseManager().getActivePunishment(target.getUniqueId(), "MUTE");
            if (punishment == null) {
               sender.sendMessage(ColorUtil.color("&aThis player has no active mute."));
               return true;
            } else {
               sender.sendMessage(ColorUtil.color("&8&m-----------------------------"));
               sender.sendMessage(ColorUtil.color("&#ff9933&lActive Mute"));
               sender.sendMessage(ColorUtil.color("&7Player: &f" + punishment.getPlayer()));
               sender.sendMessage(ColorUtil.color("&7Reason: &f" + punishment.getReason()));
               sender.sendMessage(ColorUtil.color("&7Staff: &f" + punishment.getStaff()));
               sender.sendMessage(ColorUtil.color("&7Duration: &f" + punishment.getDuration()));
               long var10001 = punishment.getExpireTime();
               sender.sendMessage(ColorUtil.color("&7Time Left: &f" + TimeUtil.formatTime(var10001 - System.currentTimeMillis())));
               sender.sendMessage(ColorUtil.color("&7Offense: &f" + punishment.getOffense()));
               sender.sendMessage(ColorUtil.color("&8&m-----------------------------"));
               return true;
            }
         }
      }
   }
}
