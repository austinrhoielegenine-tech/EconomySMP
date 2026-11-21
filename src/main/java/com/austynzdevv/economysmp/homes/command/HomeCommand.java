package com.austynzdevv.economysmp.homes.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.austynzdevv.economysmp.homes.EconomyHomes;
import com.austynzdevv.economysmp.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor, TabCompleter {
   private final EconomyHomes plugin;

   public HomeCommand(EconomyHomes plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      String name = command.getName().toLowerCase();
      if (name.equals("sethome")) {
         if (sender instanceof Player) {
            Player player = (Player)sender;
            if (args.length < 1) {
               player.sendMessage(ColorUtil.color("&cUsage: /sethome <number>"));
               return true;
            } else {
               this.plugin.homes().set(player, args[0]);
               return true;
            }
         } else {
            sender.sendMessage(ColorUtil.color(this.plugin.configs().msg("players-only")));
            return true;
         }
      } else if (name.equals("delhome")) {
         if (sender instanceof Player) {
            Player player = (Player)sender;
            if (args.length < 1) {
               player.sendMessage(ColorUtil.color("&cUsage: /delhome <number>"));
               return true;
            } else {
               this.plugin.homes().delete(player, args[0]);
               return true;
            }
         } else {
            sender.sendMessage(ColorUtil.color(this.plugin.configs().msg("players-only")));
            return true;
         }
      } else if (args.length == 0) {
         if (sender instanceof Player) {
            Player player = (Player)sender;
            this.plugin.mainGui().open(player);
            return true;
         } else {
            sender.sendMessage(ColorUtil.color(this.plugin.configs().msg("players-only")));
            return true;
         }
      } else {
         String sub = args[0].toLowerCase();
         if (sub.equals("reload")) {
            if (!sender.hasPermission("economysmpcore.system.homes.admin")) {
               sender.sendMessage(ColorUtil.color(this.plugin.configs().msg("denied")));
               return true;
            } else {
               this.plugin.reloadPlugin();
               sender.sendMessage(ColorUtil.color(this.plugin.configs().msg("reloaded")));
               if (sender instanceof Player) {
                  Player p = (Player)sender;
                  this.plugin.homes().sound(p, "reload");
               }

               return true;
            }
         } else if (sub.equals("admin") && args.length >= 2) {
            if (!sender.hasPermission("economysmpcore.system.homes.others") && !sender.hasPermission("economysmpcore.system.homes.admin")) {
               sender.sendMessage(ColorUtil.color(this.plugin.configs().msg("denied")));
               return true;
            } else if (sender instanceof Player) {
               Player player = (Player)sender;
               Player target = Bukkit.getPlayer(args[1]);
               if (target == null) {
                  sender.sendMessage(ColorUtil.color("&cPlayer not found."));
                  return true;
               } else {
                  this.plugin.mainGui().open(player, target);
                  return true;
               }
            } else {
               sender.sendMessage(ColorUtil.color(this.plugin.configs().msg("players-only")));
               return true;
            }
         } else {
            if (sender instanceof Player) {
               Player player = (Player)sender;
               this.plugin.mainGui().open(player);
            } else {
               sender.sendMessage(ColorUtil.color(this.plugin.configs().getLang().getString("help-staff", "")));
            }

            return true;
         }
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      List<String> out = new ArrayList();
      if (!command.getName().equalsIgnoreCase("home") && !command.getName().equalsIgnoreCase("homes")) {
         if (args.length == 1 && (command.getName().equalsIgnoreCase("sethome") || command.getName().equalsIgnoreCase("delhome"))) {
            for(int i = 1; i <= 10; ++i) {
               String s = String.valueOf(i);
               if (s.startsWith(args[0])) {
                  out.add(s);
               }
            }
         }
      } else if (args.length == 1) {
         for(String s : Arrays.asList("reload", "admin")) {
            if (s.startsWith(args[0].toLowerCase())) {
               out.add(s);
            }
         }
      } else if (args.length == 2 && args[0].equalsIgnoreCase("admin")) {
         return null;
      }

      return out;
   }
}
