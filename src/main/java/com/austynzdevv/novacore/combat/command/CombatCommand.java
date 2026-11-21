package com.austynzdevv.novacore.combat.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.austynzdevv.novacore.combat.EconomyCombat;
import com.austynzdevv.novacore.combat.CombatManager;
import com.austynzdevv.novacore.combat.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class CombatCommand implements CommandExecutor, TabCompleter {
   private final EconomyCombat plugin;

   public CombatCommand(EconomyCombat plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length == 0) {
         if (sender instanceof Player) {
            Player player = (Player)sender;
            CombatManager mgr = this.plugin.getCombatManager();
            if (mgr.isInCombat(player)) {
               String msg = this.plugin.getConfig().getString("messages.status-in", "&cIn combat for &f%time%s");
               MessageUtil.sendRaw(player, msg.replace("%time%", String.valueOf(mgr.getTimeLeft(player))));
            } else {
               MessageUtil.send(player, "status-out");
            }

            return true;
         } else {
            MessageUtil.send(sender, "player-only");
            return true;
         }
      } else {
         String sub = args[0].toLowerCase();
         if (sub.equals("reload")) {
            if (!sender.hasPermission("novacorecore.system.combat.reload") && !sender.hasPermission("novacorecore.system.combat.admin")) {
               MessageUtil.send(sender, "no-permission");
               return true;
            } else {
               this.plugin.reload();
               MessageUtil.send(sender, "reload-success");
               return true;
            }
         } else if (sub.equals("status")) {
            if (sender instanceof Player) {
               Player player = (Player)sender;
               CombatManager mgr = this.plugin.getCombatManager();
               if (mgr.isInCombat(player)) {
                  String msg = this.plugin.getConfig().getString("messages.status-in", "&cIn combat for &f%time%s");
                  MessageUtil.sendRaw(player, msg.replace("%time%", String.valueOf(mgr.getTimeLeft(player))));
               } else {
                  MessageUtil.send(player, "status-out");
               }

               return true;
            } else {
               MessageUtil.send(sender, "player-only");
               return true;
            }
         } else {
            sender.sendMessage(MessageUtil.color("&eUsage: /" + label + " [reload|status]"));
            return true;
         }
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      if (args.length == 1) {
         List<String> options = Arrays.asList("reload", "status");
         return (List)options.stream().filter((s) -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
      } else {
         return new ArrayList();
      }
   }
}
