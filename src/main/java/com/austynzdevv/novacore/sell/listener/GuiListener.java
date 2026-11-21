package com.austynzdevv.novacore.sell.listener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.novacore.lb.EconomyLeaderboards;
import com.austynzdevv.novacore.order.EconomyOrder;
import com.austynzdevv.novacore.order.Order;
import com.austynzdevv.novacore.sell.EconomySell;
import com.austynzdevv.novacore.sell.SellRecord;
import com.austynzdevv.novacore.sell.gui.MenuSession;
import com.austynzdevv.novacore.sell.gui.SellMenus;
import com.austynzdevv.novacore.sell.util.InputUtil;
import com.austynzdevv.novacore.util.EconomyUtil;
import com.austynzdevv.novacore.util.SoundUtil;
import com.austynzdevv.novacore.util.Tasks;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class GuiListener implements Listener {
   private final EconomySell plugin;
   private static final Set<UUID> PREVIEW_PENDING = ConcurrentHashMap.newKeySet();

   public GuiListener(EconomySell plugin) {
      this.plugin = plugin;
   }

   private void schedulePreview(Player player, Inventory inventory, int confirmSlot) {
      UUID id = player.getUniqueId();
      if (PREVIEW_PENDING.add(id)) {
         Tasks.run(this.plugin, () -> {
            PREVIEW_PENDING.remove(id);
            if (player.isOnline() && MenuSession.VIEW.get(id) == MenuSession.View.SELL) {
               this.returnBottomRowItems(player, inventory, confirmSlot);
               SellMenus.updateConfirm(player, inventory);
            }
         });
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onClick(InventoryClickEvent event) {
      HumanEntity var3 = event.getWhoClicked();
      if (var3 instanceof Player player) {
         MenuSession.View view = (MenuSession.View)MenuSession.VIEW.get(player.getUniqueId());
         if (view != null) {
            if (view == MenuSession.View.SELL) {
               this.handleSell(player, event);
            } else {
               event.setCancelled(true);
               if (event.getClickedInventory() != null && event.getClickedInventory() == event.getView().getTopInventory()) {
                  if (event.getClick() != ClickType.NUMBER_KEY && event.getClick() != ClickType.SWAP_OFFHAND) {
                     SoundUtil.playConfigured(player, EconomySell.get().getCore(), "sounds.gui-click");
                     int slot = event.getRawSlot();
                     int page = (Integer)MenuSession.PAGE.getOrDefault(player.getUniqueId(), 0);
                     if (view == MenuSession.View.WORTH) {
                        this.worth(player, slot, page);
                     } else if (view == MenuSession.View.HISTORY) {
                        this.history(player, slot, page);
                     } else if (view == MenuSession.View.SELLMULTI) {
                        this.sellMulti(player, slot);
                     } else if (view == MenuSession.View.PROGRESS) {
                        if (slot == 45) {
                           SellMenus.openSellMulti(player);
                           return;
                        }

                        if (slot == 1) {
                           String cat = (String)MenuSession.PROGRESS_CATEGORY.get(player.getUniqueId());
                           if (cat != null) {
                              SellMenus.openCategoryItems(player, cat, 0);
                           }
                        }
                     } else if (view == MenuSession.View.CATEGORY_ITEMS) {
                        this.categoryItems(player, slot);
                     }

                  }
               }
            }
         }
      }
   }

   private void categoryItems(Player player, int slot) {
      String cat = (String)MenuSession.PROGRESS_CATEGORY.get(player.getUniqueId());
      int page = (Integer)MenuSession.PAGE.getOrDefault(player.getUniqueId(), 0);
      if (slot == 45) {
         if (cat != null) {
            SellMenus.openCategoryItems(player, cat, page - 1);
         }

      } else if (slot == 53) {
         if (cat != null) {
            SellMenus.openCategoryItems(player, cat, page + 1);
         }

      } else {
         if (slot == 49) {
            if (cat != null) {
               SellMenus.openProgress(player, cat);
            } else {
               SellMenus.openSellMulti(player);
            }
         }

      }
   }

   private void sellMulti(Player player, int slot) {
      String var10000;
      switch (slot) {
         case 9 -> var10000 = "crops";
         case 10 -> var10000 = "ores";
         case 11 -> var10000 = "mobs";
         case 12 -> var10000 = "natural";
         case 13 -> var10000 = "tools";
         case 14 -> var10000 = "fish";
         case 15 -> var10000 = "books";
         case 16 -> var10000 = "potions";
         case 17 -> var10000 = "blocks";
         default -> var10000 = null;
      }

      String category = var10000;
      if (category != null) {
         SellMenus.openProgress(player, category);
      }
   }

   private void handleSell(Player player, InventoryClickEvent event) {
      int confirmSlot = this.plugin.getConfig().getInt("sell.confirm-slot", 44);
      int raw = event.getRawSlot();
      int topSize = event.getView().getTopInventory().getSize();
      boolean top = event.getClickedInventory() != null && event.getClickedInventory().equals(event.getView().getTopInventory());
      if (raw >= 36 && raw < topSize) {
         event.setCancelled(true);
         if (raw == confirmSlot) {
            SoundUtil.playConfigured(player, EconomySell.get().getCore(), "sounds.gui-click");
            this.sellAll(player, event.getView().getTopInventory(), confirmSlot);
         } else {
            this.schedulePreview(player, event.getView().getTopInventory(), confirmSlot);
         }
      } else if (event.isShiftClick() && !top) {
         event.setCancelled(true);
         ItemStack clicked = event.getCurrentItem();
         if (clicked != null && !clicked.getType().isAir()) {
            this.moveIntoSellArea(player, event.getView().getTopInventory(), clicked, event.getSlot());
            this.schedulePreview(player, event.getView().getTopInventory(), confirmSlot);
         }

      } else {
         if (top) {
            this.schedulePreview(player, event.getView().getTopInventory(), confirmSlot);
         }

      }
   }

   private void moveIntoSellArea(Player player, Inventory top, ItemStack source, int playerSlot) {
      ItemStack moving = source.clone();
      int left = moving.getAmount();

      for(int i = 0; i < 36 && left > 0; ++i) {
         ItemStack cur = top.getItem(i);
         if (cur != null && !cur.getType().isAir()) {
            if (cur.isSimilar(moving) && cur.getAmount() < cur.getMaxStackSize()) {
               int space = cur.getMaxStackSize() - cur.getAmount();
               int put = Math.min(space, left);
               cur.setAmount(cur.getAmount() + put);
               top.setItem(i, cur);
               left -= put;
            }
         } else {
            int put = Math.min(left, moving.getMaxStackSize());
            ItemStack place = moving.clone();
            place.setAmount(put);
            top.setItem(i, place);
            left -= put;
         }
      }

      if (left <= 0) {
         player.getInventory().setItem(playerSlot, (ItemStack)null);
      } else {
         source.setAmount(left);
         player.getInventory().setItem(playerSlot, source);
      }

   }

   private void returnBottomRowItems(Player player, Inventory inv, int confirmSlot) {
      if (inv != null) {
         int size = inv.getSize();

         for(int i = 36; i < size && i <= 44; ++i) {
            ItemStack stack = inv.getItem(i);
            if (stack != null && !stack.getType().isAir() && (i != confirmSlot || !stack.getType().name().contains("GLASS_PANE"))) {
               if (stack.getType().name().contains("GLASS_PANE") && i != confirmSlot) {
                  inv.setItem(i, (ItemStack)null);
               } else {
                  inv.setItem(i, (ItemStack)null);
                  Map<Integer, ItemStack> left = player.getInventory().addItem(new ItemStack[]{stack});
                  left.values().forEach((s) -> player.getWorld().dropItemNaturally(player.getLocation(), s));
               }
            }
         }

      }
   }

   private void sellAll(Player player, Inventory inv, int confirmSlot) {
      this.returnBottomRowItems(player, inv, confirmSlot);
      List<ItemStack> toReturn = new ArrayList();
      Map<Material, int[]> sold = new HashMap();
      double total = (double)0.0F;
      int itemCount = 0;
      double multiplier = this.plugin.getPrices().getPlayerMultiplier(player);
      Map<String, Double> progressByCategory = new HashMap();
      boolean integrateOrders = this.plugin.getConfig().getBoolean("Integrate-order", false);
      Map<Material, List<Order>> ordersByMaterial = null;
      EconomyOrder ordersApi = null;
      if (integrateOrders) {
         try {
            ordersApi = EconomyOrder.get();
            if (ordersApi != null && ordersApi.getOrderManager() != null) {
               ordersByMaterial = new HashMap();

               for(Order o : ordersApi.getOrderManager().getOpen()) {
                  if (o.getMaterial() != null && o.getRemaining() > 0) {
                     ((List)ordersByMaterial.computeIfAbsent(o.getMaterial(), (k) -> new ArrayList())).add(o);
                  }
               }

               for(List<Order> list : ordersByMaterial.values()) {
                  list.sort(Comparator.comparingDouble(Order::getPriceEach).reversed());
               }
            }
         } catch (Exception var32) {
            ordersByMaterial = null;
            ordersApi = null;
         }
      }

      boolean ordersTouched = false;

      for(int i = 0; i < Math.min(36, inv.getSize()); ++i) {
         ItemStack stack = inv.getItem(i);
         if (stack != null && !stack.getType().isAir() && !stack.getType().name().contains("GLASS_PANE")) {
            if (this.plugin.getBlacklist().isBlocked(stack)) {
               toReturn.add(stack.clone());
               inv.setItem(i, (ItemStack)null);
            } else {
               if (this.plugin.getConfig().getBoolean("sell.shulker-content-selling", true) && stack.getType().name().endsWith("SHULKER_BOX")) {
                  ItemMeta var20 = stack.getItemMeta();
                  if (var20 instanceof BlockStateMeta) {
                     BlockStateMeta meta = (BlockStateMeta)var20;
                     BlockState var50 = meta.getBlockState();
                     if (var50 instanceof ShulkerBox) {
                        ShulkerBox shulker = (ShulkerBox)var50;
                        boolean changed = false;
                        Inventory contents = shulker.getInventory();

                        for(int contentSlot = 0; contentSlot < contents.getSize(); ++contentSlot) {
                           ItemStack content = contents.getItem(contentSlot);
                           if (content != null && !content.getType().isAir() && !this.plugin.getBlacklist().isBlocked(content)) {
                              double contentPay = this.plugin.getPrices().getSellPrice(player, content, multiplier);
                              if (!(contentPay <= (double)0.0F)) {
                                 total += contentPay;
                                 itemCount += content.getAmount();
                                 sold.computeIfAbsent(content.getType(), (k) -> new int[]{0, 0});
                                 int[] var10000 = (int[])sold.get(content.getType());
                                 var10000[0] += content.getAmount();
                                 var10000 = (int[])sold.get(content.getType());
                                 var10000[1] += (int)Math.round(contentPay * (double)100.0F);
                                 String contentCategory = this.plugin.getProgress().categoryOf(content.getType());
                                 progressByCategory.merge(contentCategory, contentPay, Double::sum);
                                 contents.setItem(contentSlot, (ItemStack)null);
                                 changed = true;
                              }
                           }
                        }

                        if (changed) {
                           meta.setBlockState(shulker);
                           stack.setItemMeta(meta);
                           inv.setItem(i, stack);
                        }

                        if (changed || !contents.isEmpty()) {
                           continue;
                        }
                     }
                  }
               }

               ItemStack remaining = stack.clone();
               double pay = (double)0.0F;
               int soldAmount = 0;
               if (ordersByMaterial != null) {
                  List<Order> matches = (List)ordersByMaterial.get(stack.getType());
                  if (matches != null) {
                     for(Order order : matches) {
                        if (order.getRemaining() > 0 && !(order.getPriceEach() <= (double)0.0F) && !(order.getEscrowLeft() < order.getPriceEach())) {
                           int affordable = (int)Math.min((double)Integer.MAX_VALUE, Math.floor(order.getEscrowLeft() / order.getPriceEach()));
                           int qty = Math.min(Math.min(remaining.getAmount(), order.getRemaining()), affordable);
                           if (qty > 0) {
                              double orderPay = order.payFor(qty);
                              int paidQty = Math.min(qty, (int)Math.floor(orderPay / order.getPriceEach() + 1.0E-8));
                              if (paidQty > 0) {
                                 order.deliver(paidQty);
                                 order.addHistory(player.getUniqueId(), player.getName(), paidQty);
                                 pay += orderPay;
                                 soldAmount += paidQty;
                                 remaining.setAmount(remaining.getAmount() - paidQty);
                                 ordersTouched = true;
                                 if (remaining.getAmount() <= 0) {
                                    break;
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               if (remaining.getAmount() > 0) {
                  pay += this.plugin.getPrices().getSellPrice(player, remaining, multiplier);
                  soldAmount += remaining.getAmount();
               }

               total += pay;
               itemCount += soldAmount;
               sold.computeIfAbsent(stack.getType(), (k) -> new int[]{0, 0});
               int[] var60 = (int[])sold.get(stack.getType());
               var60[0] += soldAmount;
               var60 = (int[])sold.get(stack.getType());
               var60[1] += (int)Math.round(pay * (double)100.0F);
               String cat = this.plugin.getProgress().categoryOf(stack.getType());
               progressByCategory.merge(cat, pay, Double::sum);
               inv.setItem(i, (ItemStack)null);
            }
         }
      }

      if (ordersTouched && ordersApi != null && ordersApi.getOrderManager() != null) {
         try {
            ordersApi.getOrderManager().save();
         } catch (Exception var31) {
         }
      }

      for(Map.Entry<String, Double> entry : progressByCategory.entrySet()) {
         this.plugin.getProgress().addSold(player.getUniqueId(), (String)entry.getKey(), (Double)entry.getValue());
      }

      if (itemCount <= 0) {
         for(ItemStack stack : toReturn) {
            Map<Integer, ItemStack> left = player.getInventory().addItem(new ItemStack[]{stack});
            left.values().forEach((s) -> player.getWorld().dropItemNaturally(player.getLocation(), s));
         }

         this.plugin.getMessages().send(player, "nothing-to-sell");
         SoundUtil.playConfigured(player, EconomySell.get().getCore(), "sounds.error");
         SellMenus.updateConfirm(player, inv);
      } else {
         this.plugin.getEconomy().deposit(player, total);
         this.plugin.getProgress().addMoneyMade(player.getUniqueId(), total);

         try {
            EconomyLeaderboards _lb = EconomyLeaderboards.get();
            if (_lb != null && _lb.getStats() != null) {
               _lb.getStats().addSellEarned(player.getUniqueId(), total);
            }
         } catch (Exception var30) {
         }

         long now = System.currentTimeMillis();

         for(Map.Entry<Material, int[]> e : sold.entrySet()) {
            this.plugin.getHistory().add(new SellRecord(UUID.randomUUID(), player.getUniqueId(), (Material)e.getKey(), ((int[])e.getValue())[0], (double)((int[])e.getValue())[1] / (double)100.0F, now));
         }

         for(ItemStack stack : toReturn) {
            Map<Integer, ItemStack> left = player.getInventory().addItem(new ItemStack[]{stack});
            left.values().forEach((s) -> player.getWorld().dropItemNaturally(player.getLocation(), s));
         }

         if (!toReturn.isEmpty()) {
            this.plugin.getMessages().send(player, "blacklisted-returned", Map.of("count", String.valueOf(toReturn.size())));
         }

         this.plugin.getMessages().send(player, "sold", Map.of("amount", EconomyUtil.formatSell(total)));
         this.plugin.getMessages().send(player, "sold-detail", Map.of("items", String.valueOf(itemCount), "amount", EconomyUtil.formatSell(total)));
         SoundUtil.playConfigured(player, EconomySell.get().getCore(), "sounds.sell");
         SellMenus.updateConfirm(player, inv);
         if (this.plugin.getWorthLore() != null) {
            Tasks.run(this.plugin, () -> this.plugin.getWorthLore().refresh(player));
         }

      }
   }

   private void worth(Player player, int slot, int page) {
      if (slot == 45) {
         SellMenus.openWorth(player, page - 1);
      } else if (slot == 53) {
         SellMenus.openWorth(player, page + 1);
      } else if (slot == 49) {
         SellMenus.openWorth(player, page);
      } else if (slot == 50) {
         InputUtil.requestSearch(player);
      } else if (slot == 47) {
         int sort = (Integer)MenuSession.SORT.getOrDefault(player.getUniqueId(), 0);
         MenuSession.SORT.put(player.getUniqueId(), (sort + 1) % 3);
         SellMenus.openWorth(player, 0);
      } else {
         if (slot == 48) {
            int filter = (Integer)MenuSession.FILTER.getOrDefault(player.getUniqueId(), 0);
            int size = Math.max(1, this.plugin.getFilters().names().size());
            MenuSession.FILTER.put(player.getUniqueId(), (filter + 1) % size);
            SellMenus.openWorth(player, 0);
         }

      }
   }

   private void history(Player player, int slot, int page) {
      if (slot == 45) {
         SellMenus.openHistory(player, page - 1);
      } else if (slot == 53) {
         SellMenus.openHistory(player, page + 1);
      } else {
         if (slot == 49) {
            player.closeInventory();
         }

      }
   }

   @EventHandler
   public void onDrag(InventoryDragEvent event) {
      HumanEntity var3 = event.getWhoClicked();
      if (var3 instanceof Player player) {
         MenuSession.View view = (MenuSession.View)MenuSession.VIEW.get(player.getUniqueId());
         if (view != null) {
            if (view != MenuSession.View.SELL) {
               event.setCancelled(true);
            } else {
               for(int s : event.getRawSlots()) {
                  if (s >= 36 && s <= 44) {
                     event.setCancelled(true);
                     break;
                  }
               }

               int confirmSlot = this.plugin.getConfig().getInt("sell.confirm-slot", 44);
               this.schedulePreview(player, event.getView().getTopInventory(), confirmSlot);
            }
         }
      }
   }

   @EventHandler
   public void onClose(InventoryCloseEvent event) {
      HumanEntity var3 = event.getPlayer();
      if (var3 instanceof Player player) {
         if (!MenuSession.SWITCHING.contains(player.getUniqueId())) {
            if (!Boolean.TRUE.equals(MenuSession.CHAT_SEARCH.get(player.getUniqueId()))) {
               if (!Boolean.TRUE.equals(MenuSession.SIGN_SEARCH.get(player.getUniqueId()))) {
                  MenuSession.View view = (MenuSession.View)MenuSession.VIEW.get(player.getUniqueId());
                  if (view == MenuSession.View.SELL) {
                     int confirmSlot = this.plugin.getConfig().getInt("sell.confirm-slot", 44);
                     Inventory inv = event.getInventory();

                     for(int i = 0; i < inv.getSize(); ++i) {
                        ItemStack stack = inv.getItem(i);
                        if (stack != null && !stack.getType().isAir() && !stack.getType().name().contains("GLASS_PANE")) {
                           inv.setItem(i, (ItemStack)null);
                           Map<Integer, ItemStack> left = player.getInventory().addItem(new ItemStack[]{stack});
                           left.values().forEach((s) -> player.getWorld().dropItemNaturally(player.getLocation(), s));
                        }
                     }
                  }

                  MenuSession.clear(player.getUniqueId());
               }
            }
         }
      }
   }
}
