package me.xyr0.astralsmpcore.reports.command;

import java.util.List;
import me.xyr0.astralsmpcore.reports.AstralReports;
import me.xyr0.astralsmpcore.util.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportCommand implements CommandExecutor {
   private final AstralReports plugin;

   public ReportCommand(AstralReports plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!(sender instanceof Player player)) {
         return true;
      } else if (args.length < 2) {
         player.sendMessage("§cUsage: /report <player> <reason>");
         return true;
      } else {
         Player target = Bukkit.getPlayer(args[0]);
         if (target == null) {
            player.sendMessage(this.plugin.getConfig().getString("messages.player-not-found").replace("&", "§"));
            return true;
         } else if (target.equals(player)) {
            player.sendMessage(this.plugin.getConfig().getString("messages.cannot-report-self").replace("&", "§"));
            return true;
         } else if (this.plugin.getCooldownManager().hasCooldown(player.getUniqueId(), target.getUniqueId())) {
            long seconds = this.plugin.getCooldownManager().getRemaining(player.getUniqueId(), target.getUniqueId()) / 1000L;
            player.sendMessage(this.plugin.getConfig().getString("messages.cooldown").replace("%time%", seconds + " seconds").replace("&", "§"));
            return true;
         } else {
            String reason = args[1];
            List<String> reasons = this.plugin.getConfig().getStringList("reasons");
            String validReason = null;

            for(String r : reasons) {
               if (r.equalsIgnoreCase(reason)) {
                  validReason = r;
                  break;
               }
            }

            if (validReason == null) {
               player.sendMessage(this.plugin.getConfig().getString("messages.invalid-reason").replace("&", "§"));
               return true;
            } else {
               this.plugin.getDatabaseManager().createReport(player.getUniqueId().toString(), player.getName(), target.getUniqueId().toString(), target.getName(), validReason);
               this.plugin.getCooldownManager().setCooldown(player.getUniqueId(), target.getUniqueId());
               player.sendMessage(this.plugin.getConfig().getString("messages.report-success").replace("%player%", target.getName()).replace("%reason%", validReason).replace("&", "§"));
               SoundUtil.play(player, player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
               String notify = this.plugin.getConfig().getString("messages.staff-notify").replace("%reporter%", player.getName()).replace("%reported%", target.getName()).replace("%reason%", validReason).replace("&", "§");

               for(Player staff : Bukkit.getOnlinePlayers()) {
                  if (staff.hasPermission("astralsmpcore.system.reports.staff")) {
                     staff.sendMessage(notify);
                     SoundUtil.play(staff, staff.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
                  }
               }

               return true;
            }
         }
      }
   }
}
