package me.xyr0.astralsmpcore.order.listener;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.xyr0.astralsmpcore.SettingsModule;
import me.xyr0.astralsmpcore.order.AstralOrder;
import me.xyr0.astralsmpcore.order.Order;
import me.xyr0.astralsmpcore.order.gui.AdminMenus;
import me.xyr0.astralsmpcore.order.gui.EnchantMenus;
import me.xyr0.astralsmpcore.order.gui.MenuSession;
import me.xyr0.astralsmpcore.order.gui.OrderMenus;
import me.xyr0.astralsmpcore.order.util.ShulkerUtil;
import me.xyr0.astralsmpcore.util.ColorUtil;
import me.xyr0.astralsmpcore.util.EconomyUtil;
import me.xyr0.astralsmpcore.util.GuiUtil;
import me.xyr0.astralsmpcore.util.ItemUtil;
import me.xyr0.astralsmpcore.util.SoundUtil;
import me.xyr0.astralsmpcore.util.Tasks;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class GuiListener implements Listener {
   private static final Set<UUID> BUSY = ConcurrentHashMap.newKeySet();
   private final AstralOrder plugin;

   public GuiListener(AstralOrder plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onClick(InventoryClickEvent event) {
      HumanEntity var3 = event.getWhoClicked();
      if (var3 instanceof Player player) {
         UUID var9 = player.getUniqueId();
         MenuSession.View view = (MenuSession.View)MenuSession.VIEW.get(var9);
         if (view != null) {
            if (view == MenuSession.View.DELIVER_CHEST) {
               if (event.getRawSlot() == 22 && event.getClickedInventory() == event.getView().getTopInventory()) {
                  event.setCancelled(true);

                  try {
                     SoundUtil.playConfigured(player, AstralOrder.get().getCore(), "sounds.gui-click");
                  } catch (Exception var7) {
                  }
               }

            } else {
               event.setCancelled(true);

               try {
                  if (event.getCurrentItem() != null && !event.getCurrentItem().getType().isAir()) {
                     SoundUtil.playConfigured(player, AstralOrder.get().getCore(), "sounds.gui-click");
                  }
               } catch (Exception var8) {
               }

               if (event.getClickedInventory() != null && event.getClickedInventory() == event.getView().getTopInventory()) {
                  if (event.getClick() != ClickType.NUMBER_KEY && event.getClick() != ClickType.SWAP_OFFHAND) {
                     int slot = event.getRawSlot();
                     int page = (Integer)MenuSession.PAGE.getOrDefault(var9, 0);
                     switch (view) {
                        case BROWSE:
                           this.browse(player, slot, page, event);
                           break;
                        case YOUR_ORDERS:
                           this.yourOrders(player, slot);
                           break;
                        case NEW_ORDER:
                           this.newOrder(player, slot);
                           break;
                        case MATERIALS:
                           this.materials(player, slot, page);
                           break;
                        case CONFIRM_DELIVER:
                           this.confirmDeliver(player, slot);
                           break;
                        case CONFIRM_CANCEL:
                           this.confirmCancel(player, slot);
                           break;
                        case EDIT_ORDER:
                           this.edit(player, slot);
                           break;
                        case COLLECT:
                           this.collect(player, slot);
                        case DELIVER_CHEST:
                        default:
                           break;
                        case CONFIRM_SELL:
                           this.confirmSell(player, slot);
                           break;
                        case ADMIN:
                           this.admin(player, slot);
                           break;
                        case ADMIN_PROFILE:
                           this.adminProfile(player, slot);
                           break;
                        case ADMIN_HISTORY:
                           this.adminHistory(player, slot);
                           break;
                        case ENCHANTS:
                           this.enchants(player, slot);
                     }

                  }
               }
            }
         }
      }
   }

   private void browse(Player player, int slot, int page, InventoryClickEvent event) {
      if (slot == 45) {
         OrderMenus.openBrowse(player, page - 1);
      } else if (slot == 53) {
         OrderMenus.openBrowse(player, page + 1);
      } else if (slot == 49) {
         OrderMenus.openBrowse(player, page);
      } else if (slot == 51) {
         OrderMenus.openYourOrders(player);
      } else if (slot == 47) {
         int sort = (Integer)MenuSession.SORT.getOrDefault(player.getUniqueId(), 0);
         MenuSession.SORT.put(player.getUniqueId(), (sort + 1) % 4);
         OrderMenus.openBrowse(player, 0);
      } else if (slot == 48) {
         int filter = (Integer)MenuSession.FILTER.getOrDefault(player.getUniqueId(), 0);
         MenuSession.FILTER.put(player.getUniqueId(), (filter + 1) % 9);
         OrderMenus.openBrowse(player, 0);
      } else if (slot == 50) {
         this.plugin.getInput().request(player, MenuSession.ChatMode.SEARCH);
      } else if (slot >= 0 && slot < 45) {
         Order order = this.resolveBrowseOrder(player, page, slot);
         if (order != null) {
            if (player.hasPermission("astralsmpcore.system.order.use") && event.isShiftClick()) {
               AdminMenus.openAdmin(player, order);
            } else if (order.getOwner().equals(player.getUniqueId())) {
               this.plugin.getMessages().send(player, "cannot-deliver-own");
            } else {
               OrderMenus.openDeliverChest(player, order);
            }
         }
      }
   }

   private Order resolveBrowseOrder(Player player, int page, int slot) {
      List<Order> list = OrderMenus.filteredSorted(player);
      int index = page * 45 + slot;
      return index >= 0 && index < list.size() ? (Order)list.get(index) : null;
   }

   private void yourOrders(Player player, int slot) {
      if (slot == 0) {
         if (this.plugin.getBlocks().isBlocked(player.getUniqueId())) {
            this.plugin.getMessages().send(player, "player-is-blocked");
         } else {
            int maxOrders = this.plugin.getConfig().getInt("max-orders-per-player", 27);
            if (!player.hasPermission("astralsmpcore.system.order.use") && this.plugin.getOrderManager().countOpen(player.getUniqueId()) >= maxOrders) {
               this.plugin.getMessages().send(player, "max-orders-reached", Map.of("max", String.valueOf(maxOrders)));
            } else {
               OrderMenus.openNewOrder(player);
            }
         }
      } else {
         List<Order> mine = this.plugin.getOrderManager().getByOwner(player.getUniqueId());
         int index = slot - 1;
         if (index >= 0 && index < mine.size()) {
            OrderMenus.openEdit(player, (Order)mine.get(index));
         }
      }
   }

   private void newOrder(Player player, int slot) {
      UUID id = player.getUniqueId();
      if (slot == 10) {
         MenuSession.DRAFT_ITEM.remove(id);
         MenuSession.DRAFT_AMOUNT.remove(id);
         MenuSession.DRAFT_PRICE.remove(id);
         OrderMenus.openYourOrders(player);
      } else if (slot == 12) {
         OrderMenus.openMaterials(player, 0);
      } else if (slot == 13) {
         this.plugin.getInput().request(player, MenuSession.ChatMode.AMOUNT);
      } else if (slot == 14) {
         this.plugin.getInput().request(player, MenuSession.ChatMode.PRICE);
      } else if (slot == 16) {
         if (BUSY.add(id)) {
            try {
               if (!this.plugin.getBlocks().isBlocked(id)) {
                  ItemStack draft = MenuSession.draftItem(id);
                  int amount = (Integer)MenuSession.DRAFT_AMOUNT.getOrDefault(id, 1);
                  double price = (Double)MenuSession.DRAFT_PRICE.getOrDefault(id, (double)1.0F);
                  int minAmount = Math.max(1, this.plugin.getConfig().getInt("minimum-item-per-order", 1));
                  int maxAmount = this.plugin.getConfig().getInt("maximum-item-per-order", -1);
                  if (maxAmount < 0) {
                     maxAmount = Integer.MAX_VALUE;
                  }

                  double minPrice = this.plugin.getConfig().getDouble("minimum-price-per-item", (double)1.0F);
                  if (minPrice < (double)0.0F) {
                     minPrice = 0.01;
                  }

                  if (amount < minAmount) {
                     this.plugin.getMessages().send(player, "amount-too-low", Map.of("min", String.valueOf(minAmount)));
                     return;
                  }

                  if (amount > maxAmount) {
                     this.plugin.getMessages().send(player, "amount-too-high", Map.of("max", String.valueOf(maxAmount)));
                     return;
                  }

                  if (price < minPrice) {
                     this.plugin.getMessages().send(player, "price-too-low", Map.of("min", String.format("%.2f", minPrice)));
                     return;
                  }

                  double total = (double)amount * price;
                  if (!this.plugin.getEconomy().has(player, total)) {
                     this.plugin.getMessages().send(player, "no-money");
                     return;
                  }

                  if (!this.plugin.getEconomy().withdraw(player, total)) {
                     this.plugin.getMessages().send(player, "no-money");
                     return;
                  }

                  Order order = this.plugin.getOrderManager().create(player, draft, amount, price);
                  if (this.plugin.getWebhooks() != null) {
                     this.plugin.getWebhooks().orderCreated(order);
                  }

                  MenuSession.DRAFT_ITEM.remove(id);
                  MenuSession.DRAFT_AMOUNT.remove(id);
                  MenuSession.DRAFT_PRICE.remove(id);
                  String symbol = this.plugin.getConfig().getString("currency-symbol", "$");
                  this.plugin.getMessages().send(player, "order-created", Map.of("amount", String.valueOf(amount), "item", ItemUtil.pretty(order.getMaterial()), "price", String.format("%.2f", price), "total_price", String.format("%.2f", total)));
                  if (this.plugin.getConfig().getBoolean("announce-orders", true)) {
                     String raw = this.plugin.getConfig().getString("announce-message", "").replace("%owner%", player.getName()).replace("%requested-amount%", String.valueOf(amount)).replace("%requested-material%", ItemUtil.pretty(order.getMaterial())).replace("%price%", symbol + String.format("%.2f", price));
                     if (raw != null && !raw.isBlank()) {
                        Component comp = ColorUtil.parse(raw);

                        for(Player op : Bukkit.getOnlinePlayers()) {
                           if (!SettingsModule.isOrderNotifDisabled(op.getUniqueId())) {
                              op.sendMessage(comp);
                           }
                        }
                     }
                  }

                  OrderMenus.openBrowse(player, 0);
                  return;
               }

               this.plugin.getMessages().send(player, "player-is-blocked");
            } finally {
               BUSY.remove(id);
            }

         }
      }
   }

   private void materials(Player player, int slot, int page) {
      if (slot == 45) {
         OrderMenus.openMaterials(player, page - 1);
      } else if (slot == 53) {
         OrderMenus.openMaterials(player, page + 1);
      } else if (slot == 50) {
         this.plugin.getInput().request(player, MenuSession.ChatMode.SEARCH);
      } else if (slot == 48) {
         int sort = (Integer)MenuSession.SORT.getOrDefault(player.getUniqueId(), 0);
         MenuSession.SORT.put(player.getUniqueId(), (sort + 1) % 2);
         OrderMenus.openMaterials(player, 0);
      } else if (slot == 49) {
         int filter = (Integer)MenuSession.FILTER.getOrDefault(player.getUniqueId(), 0);
         MenuSession.FILTER.put(player.getUniqueId(), (filter + 1) % 9);
         OrderMenus.openMaterials(player, 0);
      } else if (slot >= 0 && slot < 45) {
         ItemStack clicked = player.getOpenInventory().getTopInventory().getItem(slot);
         if (clicked != null && clicked.getType() != Material.AIR) {
            ItemStack chosen = new ItemStack(clicked.getType());
            if (EnchantMenus.isEnchantable(chosen.getType())) {
               MenuSession.ENCHANT_PICK.put(player.getUniqueId(), new HashSet());
               EnchantMenus.open(player, chosen, 0);
            } else {
               MenuSession.DRAFT_ITEM.put(player.getUniqueId(), chosen);
               this.plugin.getMessages().send(player, "item-set", Map.of("item", ItemUtil.pretty(chosen.getType())));
               OrderMenus.openNewOrder(player);
            }
         }
      }
   }

   private void confirmDeliver(Player player, int slot) {
      Order order = (Order)MenuSession.SELECTED.get(player.getUniqueId());
      if (order == null) {
         OrderMenus.openBrowse(player, 0);
      } else if (slot == 11) {
         this.plugin.getMessages().send(player, "delivery-cancelled");
         this.returnItems(player);
         OrderMenus.openBrowse(player, 0);
      } else if (slot == 15) {
         UUID pid = player.getUniqueId();
         if (BUSY.add(pid)) {
            try {
               int want = (Integer)MenuSession.DRAFT_DELIVER.getOrDefault(player.getUniqueId(), 1);
               order = this.plugin.getOrderManager().get(order.getId());
               if (order != null && order.getRemaining() > 0) {
                  ItemStack template = order.getDisplay();
                  boolean materialOnly = this.plugin.getConfig().getBoolean("delivering-check-ignore-lore", true) && (template.getEnchantments() == null || template.getEnchantments().isEmpty());
                  Material target = order.getMaterial();
                  ItemStack[] snapshot = (ItemStack[])MenuSession.CHEST_SNAPSHOT.get(player.getUniqueId());
                  int removed = 0;
                  if (snapshot != null) {
                     int need = Math.min(want, order.getRemaining());

                     for(int i = 0; i < snapshot.length && removed < need; ++i) {
                        ItemStack stack = snapshot[i];
                        if (stack != null && stack.getType() != Material.AIR && stack.getType() != Material.PAPER) {
                           int took = ShulkerUtil.takeMatching(stack, target, materialOnly, template, need - removed);
                           removed += took;
                           if (stack.getAmount() <= 0 && !ShulkerUtil.isShulker(stack)) {
                              snapshot[i] = null;
                           } else {
                              snapshot[i] = stack;
                           }
                        }
                     }

                     for(ItemStack stack : snapshot) {
                        if (stack != null && stack.getType() != Material.AIR && stack.getType() != Material.PAPER && (stack.getAmount() > 0 || ShulkerUtil.isShulker(stack))) {
                           Map<Integer, ItemStack> left = player.getInventory().addItem(new ItemStack[]{stack});
                           left.values().forEach((it) -> player.getWorld().dropItemNaturally(player.getLocation(), it));
                        }
                     }

                     MenuSession.CHEST_SNAPSHOT.remove(player.getUniqueId());
                  }

                  if (removed <= 0) {
                     this.plugin.getMessages().send(player, "no-items");
                     OrderMenus.openBrowse(player, 0);
                     return;
                  }

                  int delivered = order.deliver(removed);
                  order.addHistory(player.getUniqueId(), player.getName(), delivered);
                  if (order.isComplete() && this.plugin.getWebhooks() != null) {
                     this.plugin.getWebhooks().orderFilled(order, player.getName());
                  }

                  double pay = order.payFor(delivered);
                  this.plugin.getEconomy().deposit(player, pay);
                  String symbol = this.plugin.getConfig().getString("currency-symbol", "$");
                  this.plugin.getMessages().send(player, "reward-messages", Map.of("reward", EconomyUtil.formatOrder(pay)));
                  Player owner = Bukkit.getPlayer(order.getOwner());
                  if (owner != null) {
                     this.plugin.getMessages().send(owner, "delivered-user", Map.of("player", player.getName(), "amount", String.valueOf(delivered), "item", ItemUtil.pretty(order.getMaterial())));
                  }

                  if (order.isComplete()) {
                     this.plugin.getMessages().send(player, "order-finished");
                  }

                  OrderMenus.openBrowse(player, 0);
                  return;
               }

               this.plugin.getMessages().send(player, "order-not-available");
               this.returnItems(player);
               OrderMenus.openBrowse(player, 0);
            } finally {
               BUSY.remove(pid);
            }

         }
      }
   }

   private void confirmCancel(Player player, int slot) {
      Order order = (Order)MenuSession.SELECTED.get(player.getUniqueId());
      if (order == null) {
         OrderMenus.openYourOrders(player);
      } else if (slot == 11) {
         this.plugin.getMessages().send(player, "cancelled-closed");
         OrderMenus.openEdit(player, order);
      } else if (slot == 15) {
         order = this.plugin.getOrderManager().get(order.getId());
         if (order == null) {
            OrderMenus.openYourOrders(player);
         } else {
            double refund = order.takeEscrowRefund();
            if (refund > (double)0.0F) {
               this.plugin.getEconomy().deposit(player, refund);
            }

            int give;
            for(int items = order.getUncollected(); items > 0; items -= give) {
               give = Math.min(items, order.getDisplay().getMaxStackSize());
               ItemStack stack = order.getDisplay();
               stack.setAmount(give);
               Map<Integer, ItemStack> left = player.getInventory().addItem(new ItemStack[]{stack});
               left.values().forEach((i) -> player.getWorld().dropItemNaturally(player.getLocation(), i));
            }

            this.plugin.getOrderManager().remove(order.getId());
            String symbol = this.plugin.getConfig().getString("currency-symbol", "$");
            this.plugin.getMessages().send(player, "refund-success", Map.of("amount", String.format("%.2f", refund)));
            if (this.plugin.getWebhooks() != null && order != null) {
               this.plugin.getWebhooks().orderCancelled(order);
            }

            this.plugin.getMessages().send(player, "order-cancelled-success");
            OrderMenus.openYourOrders(player);
         }
      }
   }

   private void edit(Player player, int slot) {
      Order order = (Order)MenuSession.SELECTED.get(player.getUniqueId());
      if (order == null) {
         OrderMenus.openYourOrders(player);
      } else if (slot == 18) {
         OrderMenus.openYourOrders(player);
      } else if (slot == 13) {
         OrderMenus.openConfirmCancel(player, order);
      } else {
         if (slot == 15) {
            if (order.getUncollected() <= 0) {
               this.plugin.getMessages().send(player, "no-items-to-collect");
               return;
            }

            OrderMenus.openCollect(player, order, 0);
         }

      }
   }

   private void collect(Player player, int slot) {
      Order order = (Order)MenuSession.SELECTED.get(player.getUniqueId());
      if (order == null) {
         OrderMenus.openYourOrders(player);
      } else if (slot == 48) {
         int uncollected = order.getUncollected();
         if (uncollected <= 0) {
            this.plugin.getMessages().send(player, "no-items-to-collect");
         } else {
            double total = (double)uncollected * this.plugin.getPrices().get(order.getMaterial());
            if (total <= (double)0.0F) {
               this.plugin.getMessages().send(player, "error");
            } else {
               MenuSession.DRAFT_PRICE.put(player.getUniqueId(), total);
               OrderMenus.openConfirmSell(player, order, total);
            }
         }
      } else if (slot == 49) {
         int uncollected = order.getUncollected();
         if (uncollected <= 0) {
            this.plugin.getMessages().send(player, "no-items-to-collect");
         } else {
            int dropped;
            int give;
            for(dropped = 0; uncollected > 0; uncollected -= give) {
               give = Math.min(uncollected, order.getDisplay().getMaxStackSize());
               ItemStack stack = order.getDisplay();
               stack.setAmount(give);
               this.dropTowardLook(player, stack);
               order.collect(give);
               dropped += give;
            }

            this.plugin.getMessages().send(player, "items-collected", Map.of("amount", String.valueOf(dropped), "item", ItemUtil.pretty(order.getMaterial())));
            if (order.isComplete() && order.getUncollected() <= 0) {
               this.plugin.getOrderManager().remove(order.getId());
            }

            OrderMenus.openYourOrders(player);
         }
      } else {
         if (slot >= 0 && slot < 45) {
            ItemStack clicked = player.getOpenInventory().getTopInventory().getItem(slot);
            if (clicked == null || clicked.getType() == Material.AIR) {
               return;
            }

            int uncollected = order.getUncollected();
            int take = Math.min(uncollected, clicked.getAmount());
            if (take <= 0) {
               this.plugin.getMessages().send(player, "no-items-to-collect");
               return;
            }

            ItemStack give = order.getDisplay();
            give.setAmount(take);
            Map<Integer, ItemStack> left = player.getInventory().addItem(new ItemStack[]{give});
            int given = take - left.values().stream().mapToInt(ItemStack::getAmount).sum();
            if (given <= 0) {
               this.plugin.getMessages().send(player, "inventory-full");
               return;
            }

            order.collect(given);
            this.plugin.getMessages().send(player, "items-collected", Map.of("amount", String.valueOf(given), "item", ItemUtil.pretty(order.getMaterial())));
            if (order.isComplete() && order.getUncollected() <= 0) {
               this.plugin.getOrderManager().remove(order.getId());
               OrderMenus.openYourOrders(player);
            } else {
               OrderMenus.openCollect(player, order, 0);
            }
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
            if (MenuSession.CHAT.getOrDefault(player.getUniqueId(), MenuSession.ChatMode.NONE) == MenuSession.ChatMode.NONE) {
               UUID id = player.getUniqueId();
               MenuSession.View view = (MenuSession.View)MenuSession.VIEW.get(id);
               if (view == MenuSession.View.DELIVER_CHEST) {
                  this.handleDeliverClose(player, event.getInventory());
               } else if (view == MenuSession.View.CONFIRM_DELIVER) {
                  this.returnItems(player);
                  MenuSession.clear(id);
               } else {
                  if (MenuSession.CHEST_SNAPSHOT.containsKey(id)) {
                     this.returnItems(player);
                  }

                  MenuSession.clear(id);
               }
            }
         }
      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      Player player = event.getPlayer();
      UUID id = player.getUniqueId();
      if (MenuSession.CHEST_SNAPSHOT.containsKey(id)) {
         this.returnItems(player);
      }

      MenuSession.clear(id);
      BUSY.remove(id);
   }

   private void handleDeliverClose(Player player, Inventory inv) {
      Order order = (Order)MenuSession.SELECTED.get(player.getUniqueId());
      if (order == null) {
         MenuSession.clear(player.getUniqueId());
      } else {
         ItemStack template = order.getDisplay();
         boolean materialOnly = this.plugin.getConfig().getBoolean("delivering-check-ignore-lore", true) && (template.getEnchantments() == null || template.getEnchantments().isEmpty());
         Material target = order.getMaterial();
         int total = 0;
         ItemStack[] contents = inv.getContents();

         for(ItemStack stack : contents) {
            if (stack != null && stack.getType() != Material.AIR && stack.getType() != Material.PAPER) {
               total += ShulkerUtil.countMatching(stack, target, materialOnly, template);
            }
         }

         if (total <= 0) {
            for(ItemStack stack : contents) {
               if (stack != null && stack.getType() != Material.AIR && stack.getType() != Material.PAPER) {
                  Map<Integer, ItemStack> left = player.getInventory().addItem(new ItemStack[]{stack.clone()});
                  left.values().forEach((i) -> player.getWorld().dropItemNaturally(player.getLocation(), i));
               }
            }

            inv.clear();
            this.plugin.getMessages().send(player, "no-items");
            MenuSession.clear(player.getUniqueId());
            Tasks.run(this.plugin.getCore(), () -> OrderMenus.openBrowse(player, 0));
         } else {
            int deliverAmount = Math.min(total, order.getRemaining());
            MenuSession.CHEST_SNAPSHOT.put(player.getUniqueId(), this.cloneContents(contents));
            MenuSession.DRAFT_DELIVER.put(player.getUniqueId(), deliverAmount);
            Tasks.run(this.plugin.getCore(), () -> OrderMenus.openConfirmDeliver(player, order, deliverAmount));
         }
      }
   }

   private ItemStack[] cloneContents(ItemStack[] contents) {
      ItemStack[] copy = new ItemStack[contents.length];

      for(int i = 0; i < contents.length; ++i) {
         copy[i] = contents[i] == null ? null : contents[i].clone();
      }

      return copy;
   }

   private void confirmSell(Player player, int slot) {
      Order order = (Order)MenuSession.SELECTED.get(player.getUniqueId());
      if (order == null) {
         OrderMenus.openYourOrders(player);
      } else if (slot == 11) {
         OrderMenus.openCollect(player, order, 0);
      } else if (slot == 15) {
         int uncollected = order.getUncollected();
         double priceEach = this.plugin.getPrices().get(order.getMaterial());
         double total = (double)uncollected * priceEach;
         if (!(total <= (double)0.0F) && uncollected > 0) {
            order.collect(uncollected);
            this.plugin.getEconomy().deposit(player, total);
            this.plugin.getMessages().send(player, "reward-messages", Map.of("reward", EconomyUtil.formatOrder(total)));
            if (order.isComplete() && order.getUncollected() <= 0) {
               this.plugin.getOrderManager().remove(order.getId());
            }

            OrderMenus.openYourOrders(player);
         } else {
            this.plugin.getMessages().send(player, "error");
            OrderMenus.openCollect(player, order, 0);
         }
      }
   }

   private void enchants(Player player, int slot) {
      UUID id = player.getUniqueId();
      int page = (Integer)MenuSession.PAGE.getOrDefault(id, 0);
      ItemStack base = (ItemStack)MenuSession.ENCHANT_BASE.get(id);
      if (base == null) {
         OrderMenus.openMaterials(player, 0);
      } else if (slot == 46) {
         MenuSession.ENCHANT_PICK.remove(id);
         MenuSession.DRAFT_ITEM.put(id, base.clone());
         this.plugin.getMessages().send(player, "item-set", Map.of("item", ItemUtil.pretty(base.getType())));
         OrderMenus.openNewOrder(player);
      } else if (slot == 52) {
         ItemStack result = EnchantMenus.applySelected(id);
         MenuSession.DRAFT_ITEM.put(id, result);
         this.plugin.getMessages().send(player, "enchantments-applied", Map.of("item", ItemUtil.pretty(result.getType())));
         MenuSession.ENCHANT_PICK.remove(id);
         OrderMenus.openNewOrder(player);
      } else if (slot == 45) {
         EnchantMenus.open(player, base, page - 1);
      } else if (slot == 53) {
         EnchantMenus.open(player, base, page + 1);
      } else if (slot >= 2 && slot < 45) {
         ItemStack clicked = player.getOpenInventory().getTopInventory().getItem(slot);
         if (clicked != null && clicked.getType() == Material.ENCHANTED_BOOK) {
            ItemMeta var8 = clicked.getItemMeta();
            if (var8 instanceof EnchantmentStorageMeta) {
               EnchantmentStorageMeta meta = (EnchantmentStorageMeta)var8;
               if (!meta.getStoredEnchants().isEmpty()) {
                  Enchantment ench = (Enchantment)meta.getStoredEnchants().keySet().iterator().next();
                  Set<Enchantment> pick = MenuSession.enchantPick(id);
                  if (pick.contains(ench)) {
                     pick.remove(ench);
                  } else {
                     pick.add(ench);
                  }

                  EnchantMenus.open(player, base, page);
               }
            }
         }
      }
   }

   private void admin(Player player, int slot) {
      Order order = (Order)MenuSession.SELECTED.get(player.getUniqueId());
      if (order == null) {
         OrderMenus.openBrowse(player, 0);
      } else if (slot == 22) {
         OrderMenus.openBrowse(player, 0);
      } else if (slot == 11) {
         AdminMenus.openHistory(player, order, 0);
      } else if (slot == 13) {
         double refund = order.takeEscrowRefund();
         if (refund > (double)0.0F) {
            this.plugin.getEconomy().deposit(order.getOwner(), refund);
         }

         int items = order.getUncollected();
         OfflinePlayer owner = Bukkit.getOfflinePlayer(order.getOwner());
         int give;
         if (owner.isOnline() && owner.getPlayer() != null) {
            for(Player op = owner.getPlayer(); items > 0; items -= give) {
               give = Math.min(items, order.getDisplay().getMaxStackSize());
               ItemStack stack = order.getDisplay();
               stack.setAmount(give);
               Map<Integer, ItemStack> left = op.getInventory().addItem(new ItemStack[]{stack});
               left.values().forEach((i) -> op.getWorld().dropItemNaturally(op.getLocation(), i));
            }
         }

         this.plugin.getOrderManager().remove(order.getId());
         this.plugin.getMessages().send(player, "order-deleted-admin");
         OrderMenus.openBrowse(player, 0);
      } else if (slot == 15) {
         AdminMenus.openProfile(player, order.getOwner());
      } else {
         if (slot == 4) {
            int uncollected = order.getUncollected();
            if (uncollected <= 0) {
               this.plugin.getMessages().send(player, "no-items-to-collect");
               return;
            }

            int given;
            int added;
            for(given = 0; uncollected > 0; uncollected -= added) {
               int give = Math.min(uncollected, order.getDisplay().getMaxStackSize());
               ItemStack stack = order.getDisplay();
               stack.setAmount(give);
               Map<Integer, ItemStack> left = player.getInventory().addItem(new ItemStack[]{stack});
               added = give - left.values().stream().mapToInt(ItemStack::getAmount).sum();
               if (added <= 0) {
                  break;
               }

               order.collect(added);
               given += added;
            }

            if (given > 0) {
               this.plugin.getMessages().send(player, "claimed-admin", Map.of("amount", String.valueOf(given), "item", ItemUtil.pretty(order.getMaterial())));
            }

            if (order.isComplete() && order.getUncollected() <= 0) {
               this.plugin.getOrderManager().remove(order.getId());
            }

            AdminMenus.openAdmin(player, order);
         }

      }
   }

   private void adminProfile(Player player, int slot) {
      UUID target = (UUID)MenuSession.PROFILE_TARGET.get(player.getUniqueId());
      Order order = (Order)MenuSession.SELECTED.get(player.getUniqueId());
      if (slot == 22) {
         if (order != null) {
            AdminMenus.openAdmin(player, order);
         } else {
            OrderMenus.openBrowse(player, 0);
         }

      } else if (target != null) {
         if (slot == 11) {
            OfflinePlayer off = Bukkit.getOfflinePlayer(target);
            String name = off.getName() == null ? target.toString() : off.getName();
            if (this.plugin.getBlocks().isBlocked(target)) {
               this.plugin.getBlocks().unblock(target);
               this.plugin.getMessages().send(player, "player-unblocked", Map.of("player", name));
            } else {
               this.plugin.getBlocks().block(target);
               this.plugin.getMessages().send(player, "player-blocked", Map.of("player", name));
            }

            AdminMenus.openProfile(player, target);
         }

      }
   }

   private void adminHistory(Player player, int slot) {
      Order order = (Order)MenuSession.SELECTED.get(player.getUniqueId());
      if (order == null) {
         OrderMenus.openBrowse(player, 0);
      } else {
         int page = (Integer)MenuSession.PAGE.getOrDefault(player.getUniqueId(), 0);
         if (slot == 49) {
            AdminMenus.openAdmin(player, order);
         } else if (slot == 45) {
            AdminMenus.openHistory(player, order, page - 1);
         } else {
            if (slot == 53) {
               AdminMenus.openHistory(player, order, page + 1);
            }

         }
      }
   }

   private void returnItems(Player player) {
      ItemStack[] snapshot = (ItemStack[])MenuSession.CHEST_SNAPSHOT.remove(player.getUniqueId());
      if (snapshot != null) {
         for(ItemStack stack : snapshot) {
            if (stack != null && stack.getType() != Material.AIR && stack.getType() != Material.PAPER) {
               Map<Integer, ItemStack> left = player.getInventory().addItem(new ItemStack[]{stack});
               left.values().forEach((i) -> player.getWorld().dropItemNaturally(player.getLocation(), i));
            }
         }

      }
   }

   private void dropTowardLook(Player player, ItemStack stack) {
      if (stack != null && stack.getType() != Material.AIR && stack.getAmount() > 0) {
         ItemStack toDrop = stack.clone();
         player.getWorld().dropItemNaturally(player.getLocation(), toDrop);
      }
   }
}
