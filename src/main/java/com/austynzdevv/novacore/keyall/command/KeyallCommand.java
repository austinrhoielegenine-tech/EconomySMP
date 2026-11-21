package com.austynzdevv.novacore.keyall.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.austynzdevv.novacore.keyall.EconomyKeyall;
import com.austynzdevv.novacore.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class KeyallCommand implements CommandExecutor, TabCompleter {
   private final EconomyKeyall plugin;

   public KeyallCommand(EconomyKeyall plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("novacorecore.system.keyall.admin") && !sender.hasPermission("novacorecore.system.keyall.admin")) {
         sender.sendMessage(ColorUtil.parse(this.msg("no-permission")));
         return true;
      } else if (args.length == 0) {
         this.help(sender);
         return true;
      } else {
         switch (args[0].toLowerCase()) {
            case "random":
               if (this.plugin.getKeys().isEmpty()) {
                  sender.sendMessage(ColorUtil.parse(this.msg("no-keys")));
                  return true;
               }

               this.plugin.getKeys().runKeyall();
               break;
            case "reload":
               if (!sender.hasPermission("novacorecore.system.keyall.admin") && !sender.hasPermission("novacorecore.system.keyall.admin")) {
                  sender.sendMessage(ColorUtil.parse(this.msg("no-permission")));
                  return true;
               }

               this.plugin.reloadAll();
               sender.sendMessage(ColorUtil.parse(this.msg("reload")));
               break;
            case "timer":
               if (args.length > 1 && args[1].equalsIgnoreCase("reset")) {
                  if (!sender.hasPermission("novacorecore.system.keyall.admin")) {
                     sender.sendMessage(ColorUtil.parse(this.msg("no-permission")));
                     return true;
                  }

                  this.plugin.getTimer().reset();
                  sender.sendMessage(ColorUtil.parse(this.msg("timer-reset")));
                  return true;
               }

               Player var10000;
               if (sender instanceof Player) {
                  Player p = (Player)sender;
                  var10000 = p;
               } else {
                  var10000 = null;
               }

               Player target = var10000;
               String time = this.plugin.getTimer().format(target);
               sender.sendMessage(ColorUtil.parse(this.msg("timer-info").replace("%time%", time)));
               break;
            default:
               this.help(sender);
         }

         return true;
      }
   }

   private void help(CommandSender sender) {
      sender.sendMessage(ColorUtil.parse("&6&lEconomyKeyall"));
      sender.sendMessage(ColorUtil.parse("&e/keyall random &7- Run a random keyall"));
      sender.sendMessage(ColorUtil.parse("&e/keyall timer &7- Show remaining time"));
      sender.sendMessage(ColorUtil.parse("&e/keyall timer reset &7- Reset timer"));
      sender.sendMessage(ColorUtil.parse("&e/keyall reload &7- Reload config"));
   }

   private String msg(String key) {
      String prefix = this.plugin.getConfig().getString("messages.prefix", "");
      String body = this.plugin.getConfig().getString("messages." + key, key);
      return prefix + body;
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      if (args.length == 1) {
         return this.filter(Arrays.asList("random", "reload", "timer"), args[0]);
      } else {
         return args.length == 2 && args[0].equalsIgnoreCase("timer") ? this.filter(List.of("reset"), args[1]) : List.of();
      }
   }

   private List<String> filter(List<String> options, String input) {
      List<String> out = new ArrayList();
      String lower = input.toLowerCase();

      for(String o : options) {
         if (o.toLowerCase().startsWith(lower)) {
            out.add(o);
         }
      }

      return out;
   }
}
