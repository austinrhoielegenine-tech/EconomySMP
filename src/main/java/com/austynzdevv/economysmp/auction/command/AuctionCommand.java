package com.austynzdevv.economysmp.auction.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import com.austynzdevv.economysmp.SettingsModule;
import com.austynzdevv.economysmp.auction.EconomyAuction;
import com.austynzdevv.economysmp.auction.gui.AdminMenus;
import com.austynzdevv.economysmp.auction.gui.AuctionMenus;
import com.austynzdevv.economysmp.auction.gui.MenuSession;
import com.austynzdevv.economysmp.util.ColorUtil;
import com.austynzdevv.economysmp.util.EconomyUtil;
import com.austynzdevv.economysmp.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AuctionCommand implements CommandExecutor, TabCompleter {
   private final EconomyAuction plugin;

   public AuctionCommand(EconomyAuction plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player player) {
         if (!player.hasPermission("economysmpcore.system.auction.use")) {
            this.plugin.getMessages().send(player, "no-permission");
            return true;
         } else if (this.plugin.getBlocks().isBlocked(player.getUniqueId()) && !player.hasPermission("economysmpcore.system.auction.use")) {
            this.plugin.getMessages().send(player, "player-is-blocked");
            return true;
         } else if (!this.plugin.getEconomy().isReady()) {
            this.plugin.getMessages().send(player, "not-enough-money");
            return true;
         } else if (args.length == 0) {
            MenuSession.SEARCH.remove(player.getUniqueId());
            AuctionMenus.openMain(player, 0);
            return true;
         } else {
            switch (args[0].toLowerCase(Locale.ROOT)) {
               case "sell":
                  this.sell(player, args);
                  break;
               case "search":
                  this.search(player, args);
                  break;
               case "view":
                  this.view(player, args);
                  break;
               case "fastbuy":
                  this.toggleFast(player, true);
                  break;
               case "fastsell":
                  this.toggleFast(player, false);
                  break;
               case "history":
                  AuctionMenus.openTransactions(player, 0);
                  break;
               case "admin":
                  this.admin(player, args);
                  break;
               case "reload":
                  if (!player.hasPermission("economysmpcore.system.auction.use")) {
                     this.plugin.getMessages().send(player, "no-permission");
                     return true;
                  }

                  this.plugin.reloadAll();
                  this.plugin.getMessages().send(player, "config-reloaded");
                  break;
               case "help":
                  player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("help")));
                  break;
               default:
                  AuctionMenus.openMain(player, 0);
            }

            return true;
         }
      } else {
         this.plugin.getMessages().send(sender, "player-only");
         return true;
      }
   }

   private void sell(Player player, String[] args) {
      if (args.length < 2) {
         this.plugin.getMessages().send(player, "usage-sell");
      } else {
         ItemStack hand = player.getInventory().getItemInMainHand();
         if (hand != null && hand.getType() != Material.AIR) {
            if (this.plugin.getAuctions().isBlacklisted(hand)) {
               this.plugin.getMessages().send(player, "blacklisted-item");
            } else {
               int limit = this.plugin.getAuctions().getLimit(player);
               int current = this.plugin.getAuctions().countActive(player.getUniqueId());
               if (current >= limit) {
                  this.plugin.getMessages().send(player, "limit-reached", Map.of("current", String.valueOf(current), "limit", String.valueOf(limit)));
               } else {
                  double price;
                  try {
                     price = EconomyUtil.parseCompact(args[1]);
                  } catch (Exception var17) {
                     this.plugin.getMessages().send(player, "invalid-price");
                     return;
                  }

                  double min = this.plugin.getConfig().getDouble("auction.minimum-price", (double)1.0F);
                  double max = this.plugin.getConfig().getDouble("auction.maximum-price", (double)1.0E9F);
                  if (price < min) {
                     this.plugin.getMessages().send(player, "price-too-low", Map.of("amount", EconomyUtil.formatAuction(min)));
                  } else if (price > max) {
                     this.plugin.getMessages().send(player, "price-too-high", Map.of("amount", EconomyUtil.formatAuction(max)));
                  } else {
                     ItemStack toSell = hand.clone();
                     hand.setAmount(0);
                     if (Boolean.TRUE.equals(MenuSession.FAST_SELL.get(player.getUniqueId())) && player.hasPermission("economysmpcore.system.auction.use") && !this.plugin.getConfig().getBoolean("Use-shards", false)) {
                        this.plugin.getAuctions().create(player, toSell, price);
                        this.plugin.getMessages().send(player, "auction-created", Map.of("price", EconomyUtil.formatAuction(price)));
                        if (this.plugin.getMessages().raw().getBoolean("chat-broadcast.enable", true)) {
                           String msg = this.plugin.getMessages().raw().getString("chat-broadcast.message", "").replace("%player%", player.getName()).replace("%amount%", String.valueOf(toSell.getAmount())).replace("%item%", ItemUtil.pretty(toSell)).replace("%price%", EconomyUtil.formatAuction(price));

                           for(Player op : Bukkit.getOnlinePlayers()) {
                              if (!SettingsModule.isAuctionAlertDisabled(op.getUniqueId())) {
                                 op.sendMessage(ColorUtil.parse(msg));
                              }
                           }
                        }

                     } else {
                        MenuSession.PENDING_SHARDS.put(player.getUniqueId(), false);
                        AuctionMenus.openConfirmList(player, toSell, price);
                     }
                  }
               }
            }
         } else {
            this.plugin.getMessages().send(player, "no-item-in-hand");
         }
      }
   }

   private void search(Player player, String[] args) {
      if (args.length < 2) {
         this.plugin.getMessages().send(player, "search");
      } else {
         String q = String.join(" ", (CharSequence[])Arrays.copyOfRange(args, 1, args.length));
         MenuSession.SEARCH.put(player.getUniqueId(), q);
         AuctionMenus.openMain(player, 0);
      }
   }

   private void view(Player player, String[] args) {
      if (args.length < 2) {
         AuctionMenus.openMyItems(player);
      } else {
         Player target = Bukkit.getPlayerExact(args[1]);
         if (target == null) {
            this.plugin.getMessages().send(player, "item-not-found");
         } else {
            MenuSession.SEARCH.put(player.getUniqueId(), target.getName());
            AuctionMenus.openMain(player, 0);
         }
      }
   }

   private void admin(Player player, String[] args) {
      if (!player.hasPermission("economysmpcore.system.auction.use")) {
         this.plugin.getMessages().send(player, "no-permission");
      } else if (args.length < 2) {
         this.plugin.getMessages().send(player, "admin-usage");
      } else {
         OfflinePlayer off = Bukkit.getOfflinePlayer(args[1]);
         if (off.getUniqueId() == null) {
            this.plugin.getMessages().send(player, "item-not-found");
         } else {
            AdminMenus.openAdmin(player, off.getUniqueId());
         }
      }
   }

   private void toggleFast(Player player, boolean buy) {
      if (buy) {
         if (!player.hasPermission("economysmpcore.system.auction.use")) {
            this.plugin.getMessages().send(player, "no-permission");
            return;
         }

         boolean next = !Boolean.TRUE.equals(MenuSession.FAST_BUY.get(player.getUniqueId()));
         MenuSession.FAST_BUY.put(player.getUniqueId(), next);
         this.plugin.getMessages().send(player, next ? "fast-buy-on" : "fast-buy-off");
      } else {
         if (!player.hasPermission("economysmpcore.system.auction.use")) {
            this.plugin.getMessages().send(player, "no-permission");
            return;
         }

         boolean next = !Boolean.TRUE.equals(MenuSession.FAST_SELL.get(player.getUniqueId()));
         MenuSession.FAST_SELL.put(player.getUniqueId(), next);
         this.plugin.getMessages().send(player, next ? "fast-sell-on" : "fast-sell-off");
      }

   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      if (args.length == 1) {
         return (List)List.of("sell", "search", "view", "fastbuy", "fastsell", "history", "admin", "reload", "help").stream().filter((s) -> s.startsWith(args[0].toLowerCase(Locale.ROOT))).collect(Collectors.toCollection(ArrayList::new));
      } else {
         return args.length == 2 && args[0].equalsIgnoreCase("view") ? null : List.of();
      }
   }
}
