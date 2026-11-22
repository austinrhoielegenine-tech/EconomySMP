package com.austynzdevv.nyrethacore.punishment2.command;

import java.util.ArrayList;
import java.util.List;
import com.austynzdevv.nyrethacore.punishment2.EconomyPunishment;
import com.austynzdevv.nyrethacore.punishment2.Punishment;
import com.austynzdevv.nyrethacore.punishment2.util.BanIdUtil;
import com.austynzdevv.nyrethacore.punishment2.util.TimeUtil;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class OffendIPCommand implements TabExecutor {
   private final EconomyPunishment plugin;

   public OffendIPCommand(EconomyPunishment plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("nyrethacorecore.system.punishment.offend")) {
         sender.sendMessage(ColorUtil.color("&cYou do not have permission."));
         return true;
      } else if (args.length < 2) {
         sender.sendMessage(ColorUtil.color(this.plugin.getMessages().getString("usage.offend", "&cUsage: /offendip <player> <reason>")));
         return true;
      } else {
         Player target = Bukkit.getPlayerExact(args[0]);
         if (target == null) {
            sender.sendMessage(ColorUtil.color(this.plugin.getMessages().getString("player-not-found", "&cThat player could not be found.")));
            return true;
         } else {
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

               long expire = TimeUtil.getExpireTime(duration);
               String banId = BanIdUtil.generate();
               Punishment punishment = new Punishment(target.getUniqueId(), target.getName(), type, reason, duration, expire, sender.getName(), offense, banId, System.currentTimeMillis(), true);
               this.plugin.getDatabaseManager().savePunishment(punishment);
               String ip = target.getAddress().getAddress().getHostAddress();
               this.plugin.getIpBanManager().banIP(ip, target.getName(), type, reason, duration, sender.getName());
               FileConfiguration messages = this.plugin.getMessages();
               if (type.equalsIgnoreCase("BAN")) {
                  StringBuilder screen = new StringBuilder();
                  screen.append(ColorUtil.color(messages.getString("ban.screen.title", "&7Connection Lost")));

                  for(String line : messages.getStringList("ban.screen.lines")) {
                     screen.append("\n").append(ColorUtil.color(line.replace("{type}", "IP-Banned").replace("{reason}", reason).replace("{time}", duration).replace("{banid}", banId).replace("{discord}", this.plugin.getConfig().getString("discord", "discord.gg/yourserver"))));
                  }

                  target.kickPlayer(screen.toString());
               } else {
                  target.sendMessage(ColorUtil.color(String.join("\n", messages.getStringList("ip-mute-message")).replace("%reason%", reason).replace("%duration%", duration).replace("%offense%", String.valueOf(offense))));
               }

               String broadcast = type.equalsIgnoreCase("BAN") ? "ip-ban-broadcast" : "ip-mute-broadcast";
               Bukkit.broadcastMessage(ColorUtil.color(String.join("\n", messages.getStringList(broadcast)).replace("%player%", target.getName()).replace("%reason%", reason).replace("%duration%", duration)));
               this.plugin.getWebhookManager().sendIPPunishment(type, punishment, ip);
               return true;
            }
         }
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      List<String> list = new ArrayList();
      if (args.length == 1) {
         for(Player player : Bukkit.getOnlinePlayers()) {
            list.add(player.getName());
         }
      }

      if (args.length == 2) {
         list.addAll(this.plugin.getReasonManager().getReasons());
      }

      return list;
   }
}
