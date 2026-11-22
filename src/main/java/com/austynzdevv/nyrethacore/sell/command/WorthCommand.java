package com.austynzdevv.nyrethacore.sell.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import com.austynzdevv.nyrethacore.sell.EconomySell;
import com.austynzdevv.nyrethacore.sell.gui.SellMenus;
import com.austynzdevv.nyrethacore.util.EconomyUtil;
import com.austynzdevv.nyrethacore.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WorthCommand implements CommandExecutor, TabCompleter {
   private final EconomySell plugin;

   public WorthCommand(EconomySell plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!(sender instanceof Player player)) {
         this.plugin.getMessages().send(sender, "player-only");
         return true;
      } else if (!player.hasPermission("nyrethacorecore.system.sell.use")) {
         this.plugin.getMessages().send(player, "no-permission");
         return true;
      } else if (args.length == 0) {
         SellMenus.openWorth(player, 0);
         return true;
      } else {
         String sub = args[0].toLowerCase(Locale.ROOT);
         if (!sub.equals("multi") && !sub.equals("multiplier")) {
            if (sub.equals("hand")) {
               ItemStack hand = player.getInventory().getItemInMainHand();
               if (hand != null && !hand.getType().isAir()) {
                  double total = this.plugin.getPrices().getSellPrice(player, hand);
                  double unit = hand.getAmount() > 0 ? total / (double)hand.getAmount() : total;
                  this.plugin.getMessages().send(player, "worth-stack", Map.of("item", ItemUtil.pretty(hand.getType()), "amount", String.valueOf(hand.getAmount()), "total", EconomyUtil.formatSell(total), "single", EconomyUtil.formatSell(unit)));
                  return true;
               } else {
                  this.plugin.getMessages().send(player, "no-item-in-hand");
                  return true;
               }
            } else if (sub.equals("set")) {
               if (!player.hasPermission("nyrethacorecore.system.sell.admin") && !player.hasPermission("nyrethacorecore.system.sell.admin")) {
                  this.plugin.getMessages().send(player, "no-permission");
                  return true;
               } else if (args.length < 2) {
                  this.plugin.getMessages().send(player, "worth-set-usage");
                  return true;
               } else {
                  double price;
                  try {
                     price = Double.parseDouble(args[1]);
                  } catch (NumberFormatException var13) {
                     this.plugin.getMessages().send(player, "invalid-price");
                     return true;
                  }

                  if (price < (double)0.0F) {
                     this.plugin.getMessages().send(player, "invalid-price");
                     return true;
                  } else {
                     Material material;
                     if (args.length >= 3) {
                        try {
                           material = Material.valueOf(args[2].toUpperCase(Locale.ROOT));
                        } catch (IllegalArgumentException var12) {
                           this.plugin.getMessages().send(player, "invalid-material");
                           return true;
                        }
                     } else {
                        ItemStack hand = player.getInventory().getItemInMainHand();
                        if (hand == null || hand.getType().isAir()) {
                           this.plugin.getMessages().send(player, "no-item-in-hand");
                           return true;
                        }

                        material = hand.getType();
                     }

                     this.plugin.getPrices().setPrice(material, price);
                     this.plugin.getMessages().send(player, "price-set", Map.of("item", ItemUtil.pretty(material), "price", EconomyUtil.formatSell(price)));
                     if (this.plugin.getWorthLore() != null) {
                        this.plugin.getWorthLore().refresh(player);
                     }

                     return true;
                  }
               }
            } else {
               try {
                  Material mat = Material.valueOf(args[0].toUpperCase(Locale.ROOT));
                  double unit = this.plugin.getPrices().getUnitPrice(player, mat);
                  this.plugin.getMessages().send(player, "worth-hand", Map.of("item", ItemUtil.pretty(mat), "price", EconomyUtil.formatSell(unit)));
                  return true;
               } catch (Exception var14) {
                  SellMenus.openWorth(player, 0);
                  return true;
               }
            }
         } else {
            double multi = this.plugin.getPrices().getPlayerMultiplier(player);
            this.plugin.getMessages().send(player, "multiplier", Map.of("x", String.format("%.2f", multi)));
            return true;
         }
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      return args.length == 1 ? (List)List.of("multi", "set", "hand").stream().filter((s) -> s.startsWith(args[0].toLowerCase(Locale.ROOT))).collect(Collectors.toCollection(ArrayList::new)) : List.of();
   }
}
