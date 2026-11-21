package com.austynzdevv.economysmp.punishment2.command;

import com.austynzdevv.economysmp.punishment2.EconomyPunishment;
import com.austynzdevv.economysmp.punishment2.Punishment;
import com.austynzdevv.economysmp.punishment2.util.TimeUtil;
import com.austynzdevv.economysmp.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckBanCommand implements CommandExecutor {
   private final EconomyPunishment plugin;

   public CheckBanCommand(EconomyPunishment plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("economysmpcore.system.punishment.checkban")) {
         sender.sendMessage(ColorUtil.color("&cNo permission."));
         return true;
      } else if (args.length != 1) {
         sender.sendMessage(ColorUtil.color("&cUsage: /checkban <player>"));
         return true;
      } else {
         Player target = Bukkit.getPlayer(args[0]);
         if (target == null) {
            sender.sendMessage(ColorUtil.color("&cPlayer not found."));
            return true;
         } else {
            Punishment punishment = this.plugin.getDatabaseManager().getActivePunishment(target.getUniqueId(), "BAN");
            if (punishment == null) {
               sender.sendMessage(ColorUtil.color("&aThis player has no active bans."));
               return true;
            } else {
               sender.sendMessage(ColorUtil.color("&c&m------------------------"));
               sender.sendMessage(ColorUtil.color("&cPlayer: &f" + target.getName()));
               sender.sendMessage(ColorUtil.color("&cReason: &f" + punishment.getReason()));
               sender.sendMessage(ColorUtil.color("&cStaff: &f" + punishment.getStaff()));
               sender.sendMessage(ColorUtil.color("&cDuration: &f" + punishment.getDuration()));
               long var10001 = punishment.getExpireTime();
               sender.sendMessage(ColorUtil.color("&cTime Left: &f" + TimeUtil.formatTime(var10001 - System.currentTimeMillis())));
               sender.sendMessage(ColorUtil.color("&cBan ID: &f" + punishment.getBanId()));
               sender.sendMessage(ColorUtil.color("&cOffense: &f" + punishment.getOffense()));
               sender.sendMessage(ColorUtil.color("&c&m------------------------"));
               return true;
            }
         }
      }
   }
}
