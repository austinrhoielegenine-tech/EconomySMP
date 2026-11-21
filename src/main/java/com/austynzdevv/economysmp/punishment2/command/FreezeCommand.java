package com.austynzdevv.economysmp.punishment2.command;

import com.austynzdevv.economysmp.punishment2.EconomyPunishment;
import com.austynzdevv.economysmp.punishment2.Punishment;
import com.austynzdevv.economysmp.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FreezeCommand implements CommandExecutor {
   private final EconomyPunishment plugin;

   public FreezeCommand(EconomyPunishment plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player staff) {
         if (!staff.hasPermission("economysmpcore.system.punishment.freeze")) {
            staff.sendMessage(ColorUtil.color("&cYou do not have permission."));
            return true;
         } else if (args.length != 1) {
            staff.sendMessage(ColorUtil.color("&cUsage: /freeze <player>"));
            return true;
         } else {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
               staff.sendMessage(ColorUtil.color("&cPlayer not found."));
               return true;
            } else if (target.hasPermission("economysmpcore.system.punishment.bypass")) {
               staff.sendMessage(ColorUtil.color("&cThat player cannot be frozen."));
               return true;
            } else if (this.plugin.getFreezeManager().isFrozen(target.getUniqueId())) {
               staff.sendMessage(ColorUtil.color("&cThat player is already frozen."));
               return true;
            } else {
               this.plugin.getFreezeManager().freeze(target.getUniqueId());
               Punishment punishment = new Punishment(target.getUniqueId(), target.getName(), "FREEZE", "Frozen", "Until Unfrozen", -1L, staff.getName(), 1, "NONE", System.currentTimeMillis(), true);
               this.plugin.getDatabaseManager().savePunishment(punishment);
               String var10000 = target.getName();
               Bukkit.broadcastMessage(ColorUtil.color("&#33ccff❄ &f" + var10000 + " &#33ccffhas been frozen by &f" + staff.getName()));
               target.sendMessage(ColorUtil.color("&#33ccffYou have been frozen by &f" + staff.getName() + "\n&#33ccffDo not leave the server."));
               this.plugin.getWebhookManager().sendPunishment("FREEZE", punishment);
               return true;
            }
         }
      } else {
         sender.sendMessage("Players only.");
         return true;
      }
   }
}
