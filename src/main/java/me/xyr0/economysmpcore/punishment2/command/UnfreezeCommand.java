package me.xyr0.economysmpcore.punishment2.command;

import me.xyr0.economysmpcore.punishment2.AstralPunishment;
import me.xyr0.economysmpcore.punishment2.Punishment;
import me.xyr0.economysmpcore.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnfreezeCommand implements CommandExecutor {
   private final AstralPunishment plugin;

   public UnfreezeCommand(AstralPunishment plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player staff) {
         if (!staff.hasPermission("economysmpcore.system.punishment.freeze")) {
            staff.sendMessage(ColorUtil.color("&cNo permission."));
            return true;
         } else if (args.length != 1) {
            staff.sendMessage(ColorUtil.color("&cUsage: /unfreeze <player>"));
            return true;
         } else {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
               staff.sendMessage(ColorUtil.color("&cPlayer not found."));
               return true;
            } else if (!this.plugin.getFreezeManager().isFrozen(target.getUniqueId())) {
               staff.sendMessage(ColorUtil.color("&cThat player isn't frozen."));
               return true;
            } else {
               this.plugin.getFreezeManager().unfreeze(target.getUniqueId());
               Punishment log = new Punishment(target.getUniqueId(), target.getName(), "UNFREEZE", "Unfrozen", "Instant", 0L, staff.getName(), 0, "NONE", System.currentTimeMillis(), false);
               this.plugin.getDatabaseManager().savePunishment(log);
               String var10000 = target.getName();
               Bukkit.broadcastMessage(ColorUtil.color("&#33ff99✔ &f" + var10000 + " &#33ff99has been unfrozen by &f" + staff.getName()));
               this.plugin.getWebhookManager().sendPunishment("UNFREEZE", log);
               return true;
            }
         }
      } else {
         sender.sendMessage("Players only.");
         return true;
      }
   }
}
