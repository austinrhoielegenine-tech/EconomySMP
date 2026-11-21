package com.austynzdevv.novacore.punishment2.command;

import java.util.ArrayList;
import java.util.List;
import com.austynzdevv.novacore.punishment2.EconomyPunishment;
import com.austynzdevv.novacore.punishment2.Punishment;
import com.austynzdevv.novacore.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class UnoffendCommand implements TabExecutor {
   private final EconomyPunishment plugin;

   public UnoffendCommand(EconomyPunishment plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("novacorecore.system.punishment.unoffend")) {
         sender.sendMessage(ColorUtil.color(this.plugin.getMessages().getString("no-permission", "&cYou do not have permission.")));
         return true;
      } else if (args.length < 2) {
         sender.sendMessage(ColorUtil.color(this.plugin.getMessages().getString("usage.unoffend", "&cUsage: /unoffend <player> <reason>")));
         return true;
      } else {
         OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
         String name = target.getName() != null ? target.getName() : args[0];
         StringBuilder builder = new StringBuilder();

         for(int i = 1; i < args.length; ++i) {
            builder.append(args[i]);
            if (i < args.length - 1) {
               builder.append(" ");
            }
         }

         String reason = builder.toString();
         boolean removedSomething = false;
         Punishment found = null;

         for(Punishment punishment : this.plugin.getDatabaseManager().getHistory(target.getUniqueId())) {
            if (punishment.isActive() && punishment.getReason().equalsIgnoreCase(reason)) {
               this.plugin.getDatabaseManager().removePunishment(punishment.getBanId());
               found = punishment;
               removedSomething = true;
            }
         }

         boolean hadIpName = this.plugin.getIpBanManager().hasActiveForPlayer(name, reason);
         if (hadIpName) {
            this.plugin.getIpBanManager().removeByPlayerAndReason(name, reason);
            removedSomething = true;
         }

         if (!args[0].equalsIgnoreCase(name)) {
            this.plugin.getIpBanManager().removeByPlayerAndReason(args[0], reason);
         }

         Player online = target.getPlayer();
         if (online == null) {
            online = Bukkit.getPlayerExact(args[0]);
         }

         if (online != null) {
            try {
               if (online.getAddress() != null && online.getAddress().getAddress() != null) {
                  String ip = online.getAddress().getAddress().getHostAddress();
                  this.plugin.getIpBanManager().removeByIpAndReason(ip, reason);
                  removedSomething = true;
               }
            } catch (Exception var15) {
            }
         }

         if (!removedSomething && found == null && !hadIpName) {
            sender.sendMessage(ColorUtil.color("&cNo active punishment found."));
            return true;
         } else {
            if (found == null) {
               found = new Punishment(target.getUniqueId(), name, "UNOFFEND", reason, "N/A", 0L, sender.getName(), 0, "NONE", System.currentTimeMillis(), false);
            }

            this.plugin.getWebhookManager().sendUnoffend(found, sender.getName());
            Bukkit.broadcastMessage(ColorUtil.color(this.plugin.getMessages().getString("punishment.unoffend-broadcast", "&#00ff66Punishment removed from &a%player% &7Reason: &f%reason%").replace("%player%", name).replace("%reason%", reason)));
            sender.sendMessage(ColorUtil.color("&aPunishment removed successfully."));
            return true;
         }
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      List<String> list = new ArrayList();
      if (args.length == 1) {
         for(Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
               list.add(p.getName());
            }
         }

         return list;
      } else if (args.length == 2) {
         OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
         String partial = args[1].toLowerCase();

         for(Punishment punishment : this.plugin.getDatabaseManager().getHistory(target.getUniqueId())) {
            if (punishment.isActive()) {
               String r = punishment.getReason();
               if (r != null && r.toLowerCase().startsWith(partial) && !list.contains(r)) {
                  list.add(r);
               }
            }
         }

         String name = target.getName() != null ? target.getName() : args[0];

         for(String r : this.plugin.getIpBanManager().getActiveReasonsForPlayer(name)) {
            if (r.toLowerCase().startsWith(partial) && !list.contains(r)) {
               list.add(r);
            }
         }

         return list;
      } else {
         return list;
      }
   }
}
