package com.austynzdevv.economysmp.sell.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import com.austynzdevv.economysmp.sell.EconomySell;
import com.austynzdevv.economysmp.sell.SellAxeManager;
import com.austynzdevv.economysmp.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellAxeCommand implements CommandExecutor, TabCompleter {
   private final EconomySell plugin;
   private final SellAxeManager manager;

   public SellAxeCommand(EconomySell plugin, SellAxeManager manager) {
      this.plugin = plugin;
      this.manager = manager;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("EconomySMP.system.sellaxe.use")) {
         this.plugin.getMessages().send(sender, "no-permission");
         return true;
      } else if (args.length >= 3 && args[0].equalsIgnoreCase("give")) {
         Player target = Bukkit.getPlayerExact(args[1]);
         if (target == null) {
            this.plugin.getMessages().send(sender, "player-not-found");
            return true;
         } else {
            long duration = SellAxeManager.parseDuration(args[2]);
            ItemStack axe = this.manager.create(duration);
            target.getInventory().addItem(new ItemStack[]{axe});
            this.plugin.getMessages().send(target, "sellaxe-received", Map.of("time", SellAxeManager.formatLeft(duration)));
            String var10001 = target.getName();
            sender.sendMessage(ColorUtil.parse("&#00fc88Gave sell axe to &f" + var10001 + " &#00fc88(" + args[2] + ")"));
            return true;
         }
      } else {
         sender.sendMessage(ColorUtil.parse("&cUsage: /sellaxe give <player> <duration>"));
         sender.sendMessage(ColorUtil.parse("&7Example: /sellaxe give Steve 7d"));
         return true;
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      if (args.length == 1) {
         return (List)List.of("give").stream().filter((s) -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
      } else if (args.length == 2) {
         return (List)Bukkit.getOnlinePlayers().stream().map(Player::getName).filter((n) -> n.toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT))).collect(Collectors.toCollection(ArrayList::new));
      } else {
         return args.length == 3 ? List.of("1d", "3d", "7d", "30d", "12h", "1h") : List.of();
      }
   }
}
