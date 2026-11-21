package me.xyr0.economysmpcore.sell.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import me.xyr0.economysmpcore.sell.AstralSell;
import me.xyr0.economysmpcore.util.ColorUtil;
import me.xyr0.economysmpcore.util.EconomyUtil;
import me.xyr0.economysmpcore.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class AdminCommand implements CommandExecutor, TabCompleter {
   private final AstralSell plugin;

   public AdminCommand(AstralSell plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("economysmpcore.system.sell.admin")) {
         this.plugin.getMessages().send(sender, "no-permission");
         return true;
      } else if (args.length == 0) {
         sender.sendMessage(ColorUtil.parse("&#00fc88/astralsell reload"));
         sender.sendMessage(ColorUtil.parse("&#00fc88/astralsell setprice <material> <price>"));
         sender.sendMessage(ColorUtil.parse("&#00fc88/astralsell blacklist <add|remove> <material>"));
         return true;
      } else {
         switch (args[0].toLowerCase(Locale.ROOT)) {
            case "reload":
               this.plugin.reloadAll();
               this.plugin.getMessages().send(sender, "reloaded");
               break;
            case "setprice":
               if (args.length < 3) {
                  return true;
               }

               try {
                  Material mat = Material.valueOf(args[1].toUpperCase(Locale.ROOT));
                  double price = Double.parseDouble(args[2]);
                  this.plugin.getPrices().setPrice(mat, price);
                  this.plugin.getMessages().send(sender, "price-set", Map.of("item", ItemUtil.pretty(mat), "price", EconomyUtil.formatSell(price)));
               } catch (Exception var12) {
                  sender.sendMessage(ColorUtil.parse("&cInvalid material or price."));
               }
               break;
            case "blacklist":
               if (args.length < 3) {
                  return true;
               }

               try {
                  Material mat = Material.valueOf(args[2].toUpperCase(Locale.ROOT));
                  if (args[1].equalsIgnoreCase("add")) {
                     this.plugin.getBlacklist().add(mat);
                     this.plugin.getMessages().send(sender, "blacklist-added", Map.of("item", ItemUtil.pretty(mat)));
                  } else {
                     this.plugin.getBlacklist().remove(mat);
                     this.plugin.getMessages().send(sender, "blacklist-removed", Map.of("item", ItemUtil.pretty(mat)));
                  }
               } catch (Exception var11) {
                  sender.sendMessage(ColorUtil.parse("&cInvalid material."));
               }
         }

         return true;
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      return args.length == 1 ? (List)List.of("reload", "setprice", "blacklist").stream().filter((s) -> s.startsWith(args[0].toLowerCase(Locale.ROOT))).collect(Collectors.toCollection(ArrayList::new)) : List.of();
   }
}
