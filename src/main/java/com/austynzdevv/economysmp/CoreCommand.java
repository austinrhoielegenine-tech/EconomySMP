package com.austynzdevv.economysmp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CoreCommand implements CommandExecutor, TabCompleter {
   private final EconomySMP plugin;

   public CoreCommand(EconomySMP plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("economysmp.reload") && !sender.hasPermission("economysmp.system.admin")) {
         this.plugin.messages().send(sender, "general.no-permission");
         return true;
      } else if (args.length == 0) {
         this.plugin.messages().send(sender, "general.invalid-usage");
         return true;
      } else {
         String sub = args[0].toLowerCase();
         if (sub.equals("reload")) {
            if (args.length == 1) {
               this.plugin.reloadAll();
               this.plugin.messages().send(sender, "general.reload-success");
            } else {
               String system = args[1].toLowerCase();
               this.plugin.messages().send(sender, "general.reload-success");
            }

            return true;
         } else if (sub.equals("enable") && args.length >= 2) {
            String system = args[1].toLowerCase();
            if (!this.plugin.getConfig().contains("systems." + system.replace("_", "-"))) {
               this.plugin.messages().send(sender, "general.system-not-found");
               return true;
            } else if (this.plugin.configs().isEnabled(system)) {
               this.plugin.messages().send(sender, "general.system-already-enabled");
               return true;
            } else {
               this.plugin.modules().enable(system);
               Map<String, String> ph = Map.of("system", system);
               this.plugin.messages().send(sender, "general.system-enabled", ph);
               return true;
            }
         } else if (sub.equals("disable") && args.length >= 2) {
            String system = args[1].toLowerCase();
            if (!this.plugin.getConfig().contains("systems." + system.replace("_", "-"))) {
               this.plugin.messages().send(sender, "general.system-not-found");
               return true;
            } else if (!this.plugin.configs().isEnabled(system)) {
               this.plugin.messages().send(sender, "general.system-already-disabled");
               return true;
            } else {
               this.plugin.modules().disable(system);
               Map<String, String> ph = Map.of("system", system);
               this.plugin.messages().send(sender, "general.system-disabled", ph);
               return true;
            }
         } else {
            this.plugin.messages().send(sender, "general.invalid-usage");
            return true;
         }
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      if (!sender.hasPermission("economysmp.reload") && !sender.hasPermission("economysmp.system.admin")) {
         return List.of();
      } else if (args.length == 1) {
         return (List)Arrays.asList("reload", "enable", "disable").stream().filter((s) -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
      } else if (args.length != 2 || !args[0].equalsIgnoreCase("enable") && !args[0].equalsIgnoreCase("disable") && !args[0].equalsIgnoreCase("reload")) {
         return List.of();
      } else {
         List<String> systems = new ArrayList(this.plugin.getConfig().getConfigurationSection("systems").getKeys(false));
         return (List)systems.stream().filter((s) -> s.startsWith(args[1].toLowerCase())).collect(Collectors.toList());
      }
   }
}
