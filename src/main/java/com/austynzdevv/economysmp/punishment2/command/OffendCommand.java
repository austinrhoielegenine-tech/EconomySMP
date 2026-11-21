package com.austynzdevv.economysmp.punishment2.command;

import java.util.ArrayList;
import java.util.List;
import com.austynzdevv.economysmp.punishment2.EconomyPunishment;
import com.austynzdevv.economysmp.punishment2.Punishment;
import com.austynzdevv.economysmp.punishment2.util.BanIdUtil;
import com.austynzdevv.economysmp.punishment2.util.TimeUtil;
import com.austynzdevv.economysmp.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class OffendCommand implements TabExecutor {
   private final EconomyPunishment plugin;

   public OffendCommand(EconomyPunishment plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("economysmpcore.system.punishment.offend")) {
         sender.sendMessage(ColorUtil.color("&cYou do not have permission."));
         return true;
      } else if (args.length < 2) {
         sender.sendMessage(ColorUtil.color(this.plugin.getMessages().getString("usage.offend", "&cUsage: /offend <player> <reason>")));
         return true;
      } else {
         OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
         StringBuilder builder = new StringBuilder();

         for(int i = 1; i < args.length; ++i) {
            builder.append(args[i]);
            if (i < args.length - 1) {
               builder.append(" ");
            }
         }

         String reason = builder.toString();
         String type = this.plugin.getReasonManager().getType(reason);
         if (type == null) {
            sender.sendMessage(ColorUtil.color("&cInvalid reason."));
            return true;
         } else {
            int offense = this.plugin.getDatabaseManager().getOffenseCount(target.getUniqueId(), reason) + 1;
            String duration = this.plugin.getReasonManager().getDuration(reason, offense);
            if (duration == null) {
               duration = "Permanent";
            }

            long expireTime = TimeUtil.getExpireTime(duration);
            String banId = BanIdUtil.generate();
            Punishment punishment = new Punishment(target.getUniqueId(), target.getName(), type, reason, duration, expireTime, sender.getName(), offense, banId, System.currentTimeMillis(), true);
            this.plugin.getDatabaseManager().savePunishment(punishment);
            Player online = target.getPlayer();
            if (online != null && type.equalsIgnoreCase("BAN")) {
               StringBuilder screen = new StringBuilder();
               screen.append(ColorUtil.color(this.plugin.getMessages().getString("ban.screen.title", "&7Connection Lost")));

               for(String line : this.plugin.getMessages().getStringList("ban.screen.lines")) {
                  screen.append("\n").append(ColorUtil.color(line.replace("{type}", "banned").replace("{reason}", reason).replace("{time}", duration).replace("{banid}", banId).replace("{discord}", this.plugin.getConfig().getString("discord", "discord.gg/yourserver"))));
               }

               online.kickPlayer(screen.toString());
            }

            Bukkit.broadcastMessage(ColorUtil.color(this.plugin.getMessages().getString("punishment.ban-broadcast", "&#ff3333Temporary Banned &a%player% &7Reason: %reason%").replace("%player%", target.getName()).replace("%reason%", reason).replace("%duration%", duration)));
            this.plugin.getWebhookManager().sendPunishment(type, punishment);
            return true;
         }
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      List<String> suggestions = new ArrayList();
      if (args.length == 1) {
         for(Player player : Bukkit.getOnlinePlayers()) {
            suggestions.add(player.getName());
         }
      }

      if (args.length == 2) {
         suggestions.addAll(this.plugin.getReasonManager().getReasons());
      }

      return suggestions;
   }
}
