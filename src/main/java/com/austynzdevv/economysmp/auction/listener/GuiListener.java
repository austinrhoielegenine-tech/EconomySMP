package com.austynzdevv.economysmp.auction.listener;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.economysmp.SettingsModule;
import com.austynzdevv.economysmp.auction.EconomyAuction;
import com.austynzdevv.economysmp.auction.AuctionListing;
import com.austynzdevv.economysmp.auction.gui.AdminMenus;
import com.austynzdevv.economysmp.auction.gui.AuctionMenus;
import com.austynzdevv.economysmp.auction.gui.MenuSession;
import com.austynzdevv.economysmp.auction.util.InputUtil;
import com.austynzdevv.economysmp.shards.EconomyShards;
import com.austynzdevv.economysmp.util.ColorUtil;
import com.austynzdevv.economysmp.util.EconomyUtil;
import com.austynzdevv.economysmp.util.GuiUtil;
import com.austynzdevv.economysmp.util.ItemUtil;
import com.austynzdevv.economysmp.util.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class GuiListener implements Listener {
   private final EconomyAuction plugin;
   private static final Set<UUID> BUSY = ConcurrentHashMap.newKeySet();

   public GuiListener(EconomyAuction plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onClick(InventoryClickEvent event) {
      HumanEntity var3 = event.getWhoClicked();
      if (var3 instanceof Player player) {
         UUID var7 = player.getUniqueId();
         MenuSession.View view = (MenuSession.View)MenuSession.VIEW.get(var7);
         if (view != null) {
            event.setCancelled(true);
            if (event.getClickedInventory() != null && event.getClickedInventory() == event.getView().getTopInventory()) {
               if (event.getClick() != ClickType.NUMBER_KEY && event.getClick() != ClickType.SWAP_OFFHAND) {
                  int slot = event.getRawSlot();
                  int page = (Integer)MenuSession.PAGE.getOrDefault(var7, 0);
                  SoundUtil.playConfigured(player, EconomyAuction.get().getCore(), "sounds.gui-click");
                  switch (view) {
                     case MAIN:
                        this.main(player, slot, page, event);
                        break;
                     case MY_ITEMS:
                        this.myItems(player, slot);
                        break;
                     case CONFIRM_BUY:
                        this.confirmBuy(player, slot);
                        break;
                     case CONFIRM_LIST:
                        this.confirmList(player, slot);
                        break;
                     case TRANSACTIONS:
                        this.transactions(player, slot, page);
                        break;
                     case SHULKER:
                        if (slot == 27) {
                           AuctionMenus.openMain(player, 0);
                        }
                        break;
                     case ADMIN:
                        this.admin(player, slot);
                        break;
                     case ADMIN_LISTINGS:
                        this.adminListings(player, slot, page, event);
                  }

               }
            }
         }
      }
   }

   private void main(Player player, int slot, int page, InventoryClickEvent event) {
      if (slot == 45) {
         AuctionMenus.openMain(player, page - 1);
      } else if (slot == 53) {
         AuctionMenus.openMain(player, page + 1);
      } else if (slot == 49) {
         AuctionMenus.openMain(player, page);
      } else if (slot == 51) {
         AuctionMenus.openMyItems(player);
      } else if (slot == 52 && this.plugin.getConfig().getBoolean("Use-shards", false)) {
         MenuSession.SHARD_VIEW.put(player.getUniqueId(), !Boolean.TRUE.equals(MenuSession.SHARD_VIEW.get(player.getUniqueId())));
         AuctionMenus.openMain(player, 0);
      } else if (slot == 50) {
         InputUtil.requestSearch(player);
      } else if (slot == 47) {
         int sort = (Integer)MenuSession.SORT.getOrDefault(player.getUniqueId(), 0);
         MenuSession.SORT.put(player.getUniqueId(), (sort + 1) % 4);
         AuctionMenus.openMain(player, 0);
      } else if (slot == 48) {
         int filter = (Integer)MenuSession.FILTER.getOrDefault(player.getUniqueId(), 0);
         MenuSession.FILTER.put(player.getUniqueId(), (filter + 1) % 9);
         AuctionMenus.openMain(player, 0);
      } else if (slot >= 0 && slot < 45) {
         String search = (String)MenuSession.SEARCH.getOrDefault(player.getUniqueId(), "");
         int sort = (Integer)MenuSession.SORT.getOrDefault(player.getUniqueId(), 0);
         int filter = (Integer)MenuSession.FILTER.getOrDefault(player.getUniqueId(), 0);
         List<AuctionListing> list = this.plugin.getAuctions().filtered(search, sort, filter);
         int index = page * 45 + slot;
         if (index < list.size()) {
            AuctionListing listing = (AuctionListing)list.get(index);
            if (player.hasPermission("economysmpcore.system.auction.use") && event.isShiftClick() && event.isRightClick()) {
               this.plugin.getAuctions().adminRemove(listing);
               this.plugin.getMessages().send(player, "listing-removed-admin");
               SoundUtil.playConfigured(player, EconomyAuction.get().getCore(), "sounds.error");
               AuctionMenus.openMain(player, page);
            } else if (player.hasPermission("economysmpcore.system.auction.use") && event.isShiftClick() && event.isLeftClick()) {
               AdminMenus.openAdmin(player, listing.getSellerId());
            } else if (listing.getItem().getType().name().endsWith("SHULKER_BOX") && player.isSneaking()) {
               AuctionMenus.openShulker(player, listing.getItem());
            } else {
               if (!Boolean.TRUE.equals(MenuSession.FAST_BUY.get(player.getUniqueId())) && (!player.hasPermission("economysmpcore.system.auction.fastbuy") || !event.isRightClick() || !Boolean.TRUE.equals(MenuSession.FAST_BUY.getOrDefault(player.getUniqueId(), false)))) {
                  boolean var14 = false;
               } else {
                  boolean var10000 = true;
               }

               if (!Boolean.TRUE.equals(MenuSession.FAST_BUY.get(player.getUniqueId())) || !player.hasPermission("economysmpcore.system.auction.fastbuy") && !player.hasPermission("economysmpcore.system.auction.use") && !player.isOp()) {
                  if (event.isRightClick() && Boolean.TRUE.equals(MenuSession.FAST_BUY.get(player.getUniqueId()))) {
                     this.tryBuy(player, listing);
                     AuctionMenus.openMain(player, page);
                  } else {
                     AuctionMenus.openConfirmBuy(player, listing);
                  }
               } else {
                  this.tryBuy(player, listing);
                  AuctionMenus.openMain(player, page);
               }
            }
         }
      }
   }

   private void admin(Player player, int slot) {
      UUID target = (UUID)MenuSession.ADMIN_TARGET.get(player.getUniqueId());
      if (target == null) {
         AuctionMenus.openMain(player, 0);
      } else if (slot == 22) {
         AuctionMenus.openMain(player, 0);
      } else if (slot == 11) {
         AdminMenus.openListings(player, target, 0);
      } else if (slot == 13) {
         MenuSession.ADMIN_TARGET.put(player.getUniqueId(), target);
         AuctionMenus.openTransactions(player, 0);
      } else {
         if (slot == 15) {
            OfflinePlayer off = Bukkit.getOfflinePlayer(target);
            String name = off.getName() == null ? target.toString() : off.getName();
            if (this.plugin.getBlocks().isBlocked(target)) {
               this.plugin.getBlocks().unblock(target);
               this.plugin.getMessages().send(player, "player-unblocked", Map.of("player", name));
            } else {
               this.plugin.getBlocks().block(target);
               this.plugin.getMessages().send(player, "player-blocked", Map.of("player", name));
            }

            AdminMenus.openAdmin(player, target);
         }

      }
   }

   private void adminListings(Player player, int slot, int page, InventoryClickEvent event) {
      UUID target = (UUID)MenuSession.ADMIN_TARGET.get(player.getUniqueId());
      if (target == null) {
         AuctionMenus.openMain(player, 0);
      } else if (slot == 45) {
         AdminMenus.openListings(player, target, page - 1);
      } else if (slot == 53) {
         AdminMenus.openListings(player, target, page + 1);
      } else if (slot == 49) {
         AdminMenus.openAdmin(player, target);
      } else if (slot >= 0 && slot < 45) {
         List<AuctionListing> list = this.plugin.getAuctions().getBySeller(target);
         int index = page * 45 + slot;
         if (index < list.size()) {
            AuctionListing listing = (AuctionListing)list.get(index);
            if (event.isShiftClick()) {
               this.plugin.getAuctions().adminRemove(listing);
               this.plugin.getMessages().send(player, "listing-removed-admin");
               AdminMenus.openListings(player, target, page);
            }

         }
      }
   }

   private void myItems(Player player, int slot) {
      if (slot == 26) {
         AuctionMenus.openTransactions(player, 0);
      } else if (slot != 0) {
         List<AuctionListing> mine = this.plugin.getAuctions().getBySeller(player.getUniqueId());
         int index = slot - 1;
         if (index >= 0 && index < mine.size()) {
            this.plugin.getAuctions().cancel(player, (AuctionListing)mine.get(index));
            AuctionMenus.openMyItems(player);
         }
      }
   }

   private void confirmBuy(Player player, int slot) {
      if (slot == 11) {
         AuctionMenus.openMain(player, 0);
      } else if (slot == 15) {
         AuctionListing listing = (AuctionListing)MenuSession.SELECTED.get(player.getUniqueId());
         this.tryBuy(player, listing);
         AuctionMenus.openMain(player, 0);
      }
   }

   private void tryBuy(Player player, AuctionListing listing) {
      if (listing != null) {
         if (BUSY.add(player.getUniqueId())) {
            try {
               listing = this.plugin.getAuctions().get(listing.getId());
               if (listing != null && listing.isActive()) {
                  if (!listing.getSellerId().equals(player.getUniqueId())) {
                     if (listing.isShards()) {
                        EconomyShards shards = EconomyShards.get();
                        if (shards == null || !shards.getShards().has(player, (long)Math.ceil(listing.getPrice()))) {
                           player.sendMessage(ColorUtil.parse("&cYou don't have enough shards."));
                           return;
                        }
                     } else if (!this.plugin.getEconomy().has(player, listing.getPrice())) {
                        this.plugin.getMessages().send(player, "not-enough-money");
                        return;
                     }

                     if (this.plugin.getAuctions().buy(player, listing)) {
                        SoundUtil.playConfigured(player, EconomyAuction.get().getCore(), "sounds.bought");
                     } else {
                        SoundUtil.playConfigured(player, EconomyAuction.get().getCore(), "sounds.error");
                     }

                     return;
                  }

                  this.plugin.getMessages().send(player, "own-auction");
                  return;
               }

               this.plugin.getMessages().send(player, "auction-already-sold");
            } finally {
               BUSY.remove(player.getUniqueId());
            }

         }
      }
   }

   private void confirmList(Player player, int slot) {
      if (slot == 4 && this.plugin.getConfig().getBoolean("Use-shards", false)) {
         MenuSession.PENDING_SHARDS.put(player.getUniqueId(), !Boolean.TRUE.equals(MenuSession.PENDING_SHARDS.get(player.getUniqueId())));
         ItemStack pending = (ItemStack)MenuSession.PENDING_ITEM.get(player.getUniqueId());
         Double price = (Double)MenuSession.PENDING_PRICE.get(player.getUniqueId());
         if (pending != null && price != null) {
            AuctionMenus.openConfirmList(player, pending, price);
         }

      } else if (slot == 11) {
         ItemStack pending = (ItemStack)MenuSession.PENDING_ITEM.remove(player.getUniqueId());
         MenuSession.PENDING_PRICE.remove(player.getUniqueId());
         MenuSession.PENDING_SHARDS.remove(player.getUniqueId());
         if (pending != null) {
            Map<Integer, ItemStack> left = player.getInventory().addItem(new ItemStack[]{pending});
            left.values().forEach((i) -> player.getWorld().dropItemNaturally(player.getLocation(), i));
         }

         AuctionMenus.openMain(player, 0);
      } else if (slot == 15) {
         if (BUSY.add(player.getUniqueId())) {
            try {
               ItemStack pending = (ItemStack)MenuSession.PENDING_ITEM.get(player.getUniqueId());
               Double price = (Double)MenuSession.PENDING_PRICE.get(player.getUniqueId());
               if (pending != null && price != null) {
                  boolean shardPrice = Boolean.TRUE.equals(MenuSession.PENDING_SHARDS.get(player.getUniqueId()));
                  if (shardPrice) {
                     price = Math.ceil(price);
                  }

                  MenuSession.PENDING_ITEM.remove(player.getUniqueId());
                  MenuSession.PENDING_PRICE.remove(player.getUniqueId());
                  MenuSession.PENDING_SHARDS.remove(player.getUniqueId());
                  this.plugin.getAuctions().create(player, pending, price, shardPrice);
                  SoundUtil.playConfigured(player, EconomyAuction.get().getCore(), "sounds.sold");
                  this.plugin.getMessages().send(player, "auction-created", Map.of("price", EconomyUtil.formatAuction(price)));
                  if (this.plugin.getMessages().raw().getBoolean("chat-broadcast.enable", true)) {
                     String msg = this.plugin.getMessages().raw().getString("chat-broadcast.message", "").replace("%player%", player.getName()).replace("%amount%", String.valueOf(pending.getAmount())).replace("%item%", ItemUtil.pretty(pending)).replace("%price%", EconomyUtil.formatAuction(price));

                     for(Player op : Bukkit.getOnlinePlayers()) {
                        if (!SettingsModule.isAuctionAlertDisabled(op.getUniqueId())) {
                           op.sendMessage(ColorUtil.parse(msg));
                        }
                     }
                  }

                  AuctionMenus.openMain(player, 0);
                  return;
               }

               AuctionMenus.openMain(player, 0);
            } finally {
               BUSY.remove(player.getUniqueId());
            }

         }
      }
   }

   private void transactions(Player player, int slot, int page) {
      if (slot == 45) {
         AuctionMenus.openTransactions(player, page - 1);
      } else if (slot == 53) {
         AuctionMenus.openTransactions(player, page + 1);
      } else if (slot == 49) {
         AuctionMenus.openTransactions(player, page);
      } else {
         if (slot == 50) {
            InputUtil.requestSearch(player);
         }

      }
   }

   @EventHandler
   public void onDrag(InventoryDragEvent event) {
      GuiUtil.cancelDragForSession(event, MenuSession.VIEW);
   }

   @EventHandler
   public void onClose(InventoryCloseEvent event) {
      HumanEntity var3 = event.getPlayer();
      if (var3 instanceof Player player) {
         if (!MenuSession.SWITCHING.contains(player.getUniqueId())) {
            if (!Boolean.TRUE.equals(MenuSession.CHAT_SEARCH.get(player.getUniqueId()))) {
               if (!Boolean.TRUE.equals(MenuSession.SIGN_SEARCH.get(player.getUniqueId()))) {
                  UUID id = player.getUniqueId();
                  if (MenuSession.VIEW.get(id) == MenuSession.View.CONFIRM_LIST) {
                     ItemStack pending = (ItemStack)MenuSession.PENDING_ITEM.remove(id);
                     MenuSession.PENDING_PRICE.remove(id);
                     MenuSession.PENDING_SHARDS.remove(id);
                     if (pending != null) {
                        Map<Integer, ItemStack> left = player.getInventory().addItem(new ItemStack[]{pending});
                        left.values().forEach((i) -> player.getWorld().dropItemNaturally(player.getLocation(), i));
                     }
                  }

                  MenuSession.clear(id);
               }
            }
         }
      }
   }
}
