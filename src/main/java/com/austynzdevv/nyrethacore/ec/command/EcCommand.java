package com.austynzdevv.nyrethacore.ec.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.austynzdevv.nyrethacore.ec.EconomyEC;
import com.austynzdevv.nyrethacore.ec.gui.EcGui;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class EcCommand implements CommandExecutor, TabCompleter {
   private final EconomyEC plugin;

   public EcCommand(EconomyEC plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      String name = command.getName().toLowerCase(Locale.ROOT);
      if (name.equals("ecsee")) {
         return this.handleSee(sender, args);
      } else if (args.length < 1 || !args[0].equalsIgnoreCase("see") && !args[0].equalsIgnoreCase("esee")) {
         if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("nyrethacorecore.system.ec.admin")) {
               this.msg(sender, "no-permission");
               return true;
            } else {
               this.plugin.reloadAll();
               this.msg(sender, "reloaded");
               return true;
            }
         } else if (sender instanceof Player) {
            Player player = (Player)sender;
            if (!player.hasPermission("nyrethacorecore.system.ec.everywhere")) {
               this.msg(sender, "no-permission");
               return true;
            } else {
               EcGui.openOwn(player, this.plugin);
               return true;
            }
         } else {
            this.msg(sender, "player-only");
            return true;
         }
      } else if (!sender.hasPermission("nyrethacorecore.system.ec.see")) {
         this.msg(sender, "no-permission");
         return true;
      } else {
         String[] rest = new String[args.length - 1];
         System.arraycopy(args, 1, rest, 0, rest.length);
         return this.handleSee(sender, rest);
      }
   }

   private boolean handleSee(CommandSender sender, String[] args) {
      if (sender instanceof Player player) {
         if (!player.hasPermission("nyrethacorecore.system.ec.see")) {
            this.msg(sender, "no-permission");
            return true;
         } else if (args.length < 1) {
            player.sendMessage(ColorUtil.parse("&cUsage: /ecsee <player>"));
            return true;
         } else {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
               target = Bukkit.getPlayer(args[0]);
            }

            if (target == null) {
               this.msg(sender, "player-not-found");
               return true;
            } else if (target.getUniqueId().equals(player.getUniqueId()) && !player.hasPermission("nyrethacorecore.system.ec.admin")) {
               this.msg(sender, "cannot-see-self");
               return true;
            } else {
               this.msg(sender, "opening-target", Map.of("target", target.getName()));
               EcGui.openOther(player, target, this.plugin);
               return true;
            }
         }
      } else {
         this.msg(sender, "player-only");
         return true;
      }
   }

   private void msg(CommandSender sender, String key) {
      this.msg(sender, key, Map.of());
   }

   private void msg(CommandSender sender, String key, Map<String, String> ph) {
      String raw = this.plugin.getMessages().getString(key, key);
      String prefix = this.plugin.getMessages().getString("prefix", "");
      raw = raw.replace("%prefix%", prefix);

      for(Map.Entry<String, String> e : ph.entrySet()) {
         raw = raw.replace("%" + (String)e.getKey() + "%", (CharSequence)e.getValue());
         raw = raw.replace("{" + (String)e.getKey() + "}", (CharSequence)e.getValue());
      }

      sender.sendMessage(ColorUtil.parse(raw));
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      List<String> out = new ArrayList();
      if (command.getName().equalsIgnoreCase("ecsee") && args.length == 1) {
         String p = args[0].toLowerCase(Locale.ROOT);

         for(Player pl : Bukkit.getOnlinePlayers()) {
            if (pl.getName().toLowerCase(Locale.ROOT).startsWith(p)) {
               out.add(pl.getName());
            }
         }
      } else if (args.length == 1) {
         String p = args[0].toLowerCase(Locale.ROOT);
         if ("see".startsWith(p) && sender.hasPermission("nyrethacorecore.system.ec.see")) {
            out.add("see");
         }

         if ("reload".startsWith(p) && sender.hasPermission("nyrethacorecore.system.ec.admin")) {
            out.add("reload");
         }
      } else if (args.length == 2 && args[0].equalsIgnoreCase("see")) {
         String p = args[1].toLowerCase(Locale.ROOT);

         for(Player pl : Bukkit.getOnlinePlayers()) {
            if (pl.getName().toLowerCase(Locale.ROOT).startsWith(p)) {
               out.add(pl.getName());
            }
         }
      }

      return out;
   }
}
